---
type: Reference
title: Release & Announcement Protocol
description: Status-quo audit of how we ship and announce changes.
tags: [release, deploy, protocol, audit]
created: 2026-03-13
modified: 2026-11-17
ai_assisted: "Claude Opus 5.5 via Claude Code"
session: 7955d90a
tools: [Bash, SSH to prod host (read-only inspection), GitHub CLI, workspace files]
agents_skills: []
review_maturity: L1
review_note: Status-quo audit. §3 Deploy and §4 Rollback checked by Claude against the prod host on 2026-11-16 (crontab, ~/deploy.sh, ~/deploy.log, journalctl); not yet human-verified.
---

# Release & Announcement Protocol

How we ship and communicate changes for the public Nubank fork of ClojureDocs.

This document is an audit describing the **status quo** of our release and deploy
process. Infrastructure is inherited from the upstream
[zk/clojuredocs](https://github.com/zk/clojuredocs) project and has not
yet been verified or modernized. Sections marked
**[goal]** describe where we intend to converge — toward Clojure ecosystem
conventions and tooling. 

## 1. Versioning

### Status quo

The project version in `project.clj` is `"0.1.0-SNAPSHOT"` — inherited
from upstream and never updated. Date-based tags exist (`2026.04.07`,
`2026.04.07a`, `2026.04.29`) but are cut by hand and have lapsed: nothing
merged after `2026.04.29`, up to and including #105, has been tagged.

### Goal

Adopt straightforward incremental versioning consistent with Clojure
projects:

```
YYYY.MM.DD   (date-based, for deploys)
```

Date-based tags are simple, monotonic, and avoid the semantic debates
that semver introduces for a web application that has no public API
contract. Each release gets a tag like `2026.03.13`. Because every merge
to `master` deploys (see §3), a tag marks a merge commit that is already
live — it records what shipped, it does not trigger anything.

When a tag is cut, bump `project.clj` from `SNAPSHOT` to the release
version.

## 2. Release Checklist

Merging to `master` **is** the release — prod picks it up within about a
minute (§3). So the checklist splits into before-merge and after-merge:

**Before merging to `master`:**

1. **Update `CHANGELOG.md`** — add the change under `[Unreleased]`, or move
   `[Unreleased]` into a new version section with today's date.
2. **Run the build locally** — the same steps prod runs, plus tests:
   ```bash
   lein test
   lein compile
   lein cljsbuild once prod
   ```
   CI runs `lein test` and `clj-kondo` on every PR, but not the CLJS prod
   build, so a CLJS break can reach prod.

**After the merge deploys:**

3. **Run the smoke tests** (§3).
4. **Tag the release** on the merge commit:
   ```bash
   git tag -a <version> <merge-sha> -m "Release <version>"
   git push origin <version>
   ```

## 3. Deploy

### Status quo

Production runs on an **AWS EC2 instance** with a single JVM process
managed by **systemd** (`clojuredocs.service`). Nginx reverse-proxies
port 80 to `127.0.0.1:4000`.

> **Important:** The app defaults to port 8080 (hardcoded in
> `clojuredocs.main`). `PORT=4000` must be set in `~/ClojureDocs/.env`
> on the server so the app listens where nginx expects it. If this
> variable is missing, the site will return 502.

> **Note:** The two-process Upstart rolling-restart procedure previously
> documented here was outdated; the server uses a single systemd service.
> `bin/ship` and `system.properties` (Heroku leftovers from upstream)
> have been removed. `~/ClojureDocs/provision.sh` on the server reflects
> the original provisioning but does not match the running config exactly
> (e.g. it assumed port 4000 without setting `PORT` in `.env`).

#### SSH access

SSH config (in `~/.ssh/config`):

```
Host clojuredocs
  HostName <prod-ip>
  User ubuntu
  IdentityFile ~/.ssh/ClojureDocs.pem
  IdentitiesOnly yes
```
The PEM key is shared out-of-band (ask the team lead).

#### Deploy steps

**Deploys are automatic. Merging to `master` deploys to prod** — there is
no manual step. The ubuntu user's crontab on the prod host runs the deploy
script every minute:

```
* * * * * /home/ubuntu/deploy.sh >> /home/ubuntu/deploy.log
```

`~/deploy.sh` on the server is a copy of [`bin/deploy.sh`](../bin/deploy.sh)
(identical as of 2026-11-16). Each run:

1. `git fetch origin master` (origin is `nubank/clojuredocs`).
2. If `HEAD` already equals `origin/master`, exits silently — this is the
   common case, so most minutes do nothing.
3. Otherwise `git pull --ff-only`, then `lein deps`, `lein compile`,
   `lein cljsbuild once prod`, and `sudo systemctl restart clojuredocs`.

Timing, measured on the PR #105 deploy (2026-11-16, UTC):

| Event | Time |
|---|---|
| PR merged | 20:37:50 |
| Cron run starts deploy | 20:38:01 |
| Build done, service restarted | 20:38:21 |
| App listening on :4000 | 20:38:48 |

> **Note:** The restart is in-place — nginx returns **502** for roughly
> 30s while the JVM starts. Any merge to `master` causes this window, so
> avoid merging during high-traffic moments, and don't mistake a 502 in
> the first minute after a merge for a failed deploy.

The deploy script only writes stdout to `~/deploy.log`; stderr (e.g. a
failed `git pull` or build) is not captured there. [unverified: where cron
sends that stderr on this host]

#### Watching a deploy

```bash
ssh clojuredocs
tail -f ~/deploy.log                              # "deploying A -> B" … "deploy complete, now at B"
sudo journalctl -u clojuredocs -f                 # wait for "Server running on port 4000"
sudo systemctl status clojuredocs
```

A "deploying" line with no matching "deploy complete" line means the
run failed partway (`set -e`).

#### Changing the deploy script

Edit [`bin/deploy.sh`](../bin/deploy.sh) in the repo, then copy it to
`~/deploy.sh` on the server by hand — the server does not run the repo
copy, and nothing syncs the two.

### Smoke Test Checklist

After deploy, verify manually:

- [ ] Homepage loads (`/`)
- [ ] Search works (`/search?q=map`)
- [ ] Var page renders (`/clojure.core/map`)
- [ ] Examples section loads on a var page
- [ ] Mobile layout — search bar visible and sticky
- [ ] newly added namespace page loads (example `/clojure.tools.build.api`)

## 4. Rollback

**Preferred: revert on `master`.** Open and merge a PR that reverts the
bad change (`git revert -m 1 <merge-sha>`). The cron deploy ships it like
any other merge, and `master` stays the source of truth.

**Emergency: pin the server to an older commit.** Use this when a revert
PR would be too slow:

```bash
ssh clojuredocs
cd ~/ClojureDocs
git checkout <previous-tag-or-commit>
sudo -u ubuntu /usr/local/bin/lein deps
sudo -u ubuntu /usr/local/bin/lein compile
sudo -u ubuntu /usr/local/bin/lein cljsbuild once prod
sudo systemctl restart clojuredocs
```

> **Warning:** This leaves the checkout on a detached `HEAD`, which
> **silently disables auto-deploy**. Every minute the cron run sees `HEAD`
> differ from `origin/master`, logs a `deploying …` line, and then
> `git pull --ff-only` fails ("You are not currently on a branch"), so
> nothing deploys. (Behaviour reproduced in a scratch repo on 2026-11-16,
> not on the prod host.) To resume auto-deploys, fix `master` (revert the
> bad change), then on the server run `git checkout master` — the next
> cron run fast-forwards and deploys.

## 5. Build Tooling

### Status quo

- **Leiningen** (`project.clj`) — all builds, REPL, CLJS compilation
- **cljsbuild** — ClojureScript prod builds (`:simple` optimization)
- **foreman** — no longer used; `Procfile`, `Procfile.dev`, and `Procfile.prod-local` are now unreferenced leftovers from the removed `bin/dev`/`bin/prod`/`bin/prod-local` scripts (see Version History below)

### [goal] Alignment with Clojure ecosystem

- [ ] Migrate from Leiningen to **deps.edn + tools.build** (consistent
  with Clojure core and contrib libraries)
- [ ] Modernize cljsbuild (figwheel or shadow)
- [x] GitHub Actions CI runs `lein test` and `clj-kondo` on every PR (`.github/workflows/test.yml`); still missing a full CLJS-compile + AOT check like the release checklist above

## Errata

1. **§2 told readers to run `bin/deploy.sh` as a local build check.**
   From 2026-10-19 to 2026-11-17, step 2 said `bin/deploy.sh   # CLJS
   compile → tests → AOT compile`. `bin/deploy.sh` is the prod deploy
   script: it `cd`s to `/home/ubuntu/ClojureDocs` and runs no tests. Run
   locally it fails at the `cd`; run on the server it deploys. Corrected
   to list the actual `lein` commands. Likely cause: #96 (`dc10980`)
   removed `bin/build` and replaced its name in this doc with the new
   script's name without checking what each script does.
2. **The 2026-10-19 Version History entry said §2 had been updated to show
   the underlying `lein` commands.** It hadn't — §2 still pointed at a
   script (error 1). Likely cause: the entry described the intended change,
   not the one actually committed.
3. **§3 described deploys as a manual `ssh` + `~/deploy.sh` step** after
   they had become automatic. The every-minute cron deploy has been running
   since at least 2026-10-19 (the first entry in `~/deploy.log`), but the
   doc wasn't updated, so readers expected a manual step that never
   happens. Corrected to describe the cron deploy. Likely cause: the cron
   entry was added by hand on the server, and only the script made it into
   the repo (#96), so nothing in the diff prompted a doc update.

---
> **AI Disclaimer**: Initial draft and research conducted and drafted by Claude (Opus 4.6) via GitHub Copilot in VS Code. Information has been reviewed by a human, corrected where applicable and noted in Version History below.
>


## Version History

| Date | Summary |
|------|--------|
| 2026-11-17 | §3 Deploy: rewrote for the every-minute cron auto-deploy (merge to `master` = deploy), with measured timing, how to watch a deploy, and how `~/deploy.sh` relates to `bin/deploy.sh`. §2 Release Checklist: split into before-/after-merge; replaced the `bin/deploy.sh` build step with actual `lein` commands. §4 Rollback: revert-on-`master` is now preferred; documented that a server-side `git checkout` disables auto-deploy. §1: noted existing tags. Fixed §4/§5 numbering. Added Errata (3 entries). Deploy facts checked by Claude against the prod host; not yet human-verified. |
| 2026-10-19 | Removed `bin/build`, `bin/dev`, `bin/prod`, `bin/prod-local` (unused/dead scripts; `bin/prod` set an `IN_PROD` env var nothing read, `bin/dev`/`bin/prod-local` duplicated steps now documented inline). §2 Release Checklist and §4 Build Tooling updated to show the underlying `lein` commands directly instead of pointing at the removed scripts. `Procfile`, `Procfile.dev`, `Procfile.prod-local` are now orphaned — candidates for removal in a follow-up. |
| 2026-03-18 | §3 Deploy: replaced outdated two-process Upstart rolling-restart with verified single-service systemd deploy (from server `deploy.sh` and `provision.sh`). §5 Rollback: updated to match actual deploy commands. Removed `system.properties` migration goal (file deleted). Removed `bin/ship` and `system.properties` (Heroku vestiges). |
| 2026-03-13 | Rewrite: document status quo honestly, flag AI-introduced errors, surface AWS vs Heroku ambiguity, propose date-based versioning. |
| 2026-03-13 | Initial draft created (AI-generated). Contained invented version scheme and unverified deploy procedure. |
