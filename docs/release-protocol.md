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
from upstream and never updated. There are no git tags.

### Goal

Adopt straightforward incremental versioning consistent with Clojure
projects:

```
YYYY.MM.DD   (date-based, for deploys)
```

Date-based tags are simple, monotonic, and avoid the semantic debates
that semver introduces for a web application that has no public API
contract. Each deploy gets a tag like `2026.03.13`.

When a tag is cut, bump `project.clj` from `SNAPSHOT` to the release
version.

## 2. Release Checklist

Before every release:

1. **Update `CHANGELOG.md`** — move items from `[Unreleased]` into a new
   version section with today's date.
2. **Run the full build locally**:
   ```bash
   bin/build   # CLJS compile → tests → AOT compile
   ```
3. **Tag the release**:
   ```bash
   git tag -a <version> -m "Release <version>"
   git push origin <version>
   ```
4. **Deploy** (see §3 below).

## 3. Deploy

### Status quo

Production runs on an **AWS EC2 instance** (t2.micro). An nginx
reverse proxy balances to two JVM processes managed by Upstart,
enabling zero-downtime rolling restarts.

> **Note:** `bin/ship` and `system.properties` reference Heroku — these
> are upstream leftovers and are **not** the active deploy path.

#### SSH access

SSH config (in `~/.ssh/config`):

```
Host clojuredocs
  HostName <prod-ip>
  User ubuntu
  IdentityFile ~/.ssh/ClojureDocs.pem
  IdentitiesOnly yes
```

Then connect with:

```bash
ssh clojuredocs
```

The PEM key is shared out-of-band (ask the team lead).

#### Deploy steps

```bash
# 1. SSH into the production box
ssh clojuredocs

# 2. Stop the first JVM process
cd $REPO
sudo service clojuredocs-web-1 stop

# 3. Pull latest code
git pull origin master

# 4. Build (compiles CLJS, runs tests, AOT compiles)
bin/build

# 5. Start the first process back up
sudo service clojuredocs-web-1 start

# 6. Wait for it to begin serving, then rolling-restart the second
sleep 15
sudo service clojuredocs-web-2 restart
```

#### Regenerate Upstart scripts (if process config changes)

```bash
cd $REPO
sudo foreman export -a clojuredocs -e ./.env -u ubuntu -c "web=2" upstart /etc/init/
```

### Smoke Test Checklist

After deploy, verify manually:

- [ ] Homepage loads (`/`)
- [ ] Search works (`/search?q=map`)
- [ ] Var page renders (`/clojure.core/map`)
- [ ] Examples section loads on a var page
- [ ] Mobile layout — search bar visible and sticky
- [ ] newly added namespace page loads (example `/clojure.tools.build.api`)

## 5. Rollback

Tag-based rollback (once tags are in use):

```bash
git checkout <previous-tag>
bin/build
# restart application processes per deploy method above
```

## 4. Build Tooling

### Status quo

- **Leiningen** (`project.clj`) — all builds, REPL, CLJS compilation (dev build broken)
- **cljsbuild** — ClojureScript prod builds (`:simple` optimization)
- **foreman** — local dev process management (`Procfile.dev`)

### [goal] Alignment with Clojure ecosystem

- [ ] Migrate from Leiningen to **deps.edn + tools.build** (consistent
  with Clojure core and contrib libraries)
- [ ] Modernize cljsbuild (figwheel or shadow)
- [ ] Update `system.properties` from Java 1.7 to current LTS
- [ ] Add GitHub Actions CI to run `bin/build` on every PR

---
> **AI Disclaimer**: Initial draft and research conducted and drafted by Claude (Opus 4.6) via GitHub Copilot in VS Code. Information has been reviewed by a human, corrected where applicable and noted in Version History below. 
>


## Version History

| Date | Summary |
|------|--------|
| 2026-03-13 | Rewrite: document status quo honestly, flag AI-introduced errors, surface AWS vs Heroku ambiguity, propose date-based versioning. |
| 2026-03-13 | Initial draft created (AI-generated). Contained invented version scheme and unverified deploy procedure. |
