# Release & Announcement Protocol

How we ship and communicate changes for the Nubank fork of ClojureDocs.

This document describes the **current state** of our release and deploy
process. Much of the infrastructure is inherited from the upstream
[zk/clojuredocs](https://github.com/zk/clojuredocs) project and has not
yet been verified or modernized for the Nubank fork. Sections marked
**[goal]** describe where we intend to converge — toward Clojure ecosystem
conventions and tooling.

> **AI disclosure.** The first draft of this document and `CHANGELOG.md`
> were AI-generated and contained errors: an invented `1.0.0-nu` version
> tag that was never created in git, a `-nu` semver suffix convention that
> doesn't exist in the Clojure ecosystem, and a deploy section that
> presented the upstream AWS procedure as current without verifying it.
> These have been corrected. The `CHANGELOG.md` AI cleanup section tracks
> other AI-introduced issues that were caught and fixed.

---

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
4. **Deploy** (see §4 below).
5. **Post the announcement** (see §3 below).

## 3. Announcements

### Template

Post to the agreed channel (TBD — Slack, GitHub Discussions, or both):

```markdown
## ClojureDocs <version> released

**Date:** YYYY-MM-DD

### Highlights
- (1-3 bullet summary of the most user-visible changes)

### Full changelog
→ https://github.com/nubank/clojuredocs/blob/master/CHANGELOG.md#<anchor>

### Deploy status
- [ ] Deployed to production
- [ ] Smoke-tested (search, var pages, examples, login)

### Known issues
- (any known regressions or deferred work)
```

**Who posts:** whoever runs the deploy.
**When:** immediately after deployment is confirmed healthy.

### [goal] Automate via GitHub Releases

Once we have CI, tag pushes should automatically generate a GitHub
Release with the changelog section, removing the need for manual
announcements.

## 4. Deploy

### Status quo

The deploy infrastructure is inherited from upstream and has **two
conflicting descriptions** that need to be reconciled:

- **README** describes an AWS t2.micro with nginx + Upstart managing
  two JVMs for zero-downtime rolling restarts.
- **`bin/ship`** pushes to Heroku (`git push git@heroku.com:clojuredocs-$1.git`).
- **`system.properties`** sets `java.runtime.version=1.7`, which is a
  Heroku convention.

**Before the first Nubank fork deploy, we need to determine which
environment is current and document it here.**

#### If AWS (per README)

```bash
# On the production box:
cd $REPO
sudo service clojuredocs-web-1 stop
git pull origin master
bin/build
sudo service clojuredocs-web-1 start
sleep 15
sudo service clojuredocs-web-2 restart
```

#### If Heroku (per bin/ship)

```bash
bin/ship <environment>
# pushes master to heroku remote
```

### Smoke Test Checklist

After deploy, verify manually:

- [ ] Homepage loads (`/`)
- [ ] Search works (`/search?q=map`)
- [ ] Var page renders (`/clojure.core/map`)
- [ ] Examples section loads on a var page
- [ ] Mobile layout — search bar visible and sticky
- [ ] tools.build namespace page loads (`/clojure.tools.build.api`)

## 5. Rollback

Tag-based rollback (once tags are in use):

```bash
git checkout <previous-tag>
bin/build
# restart application processes per deploy method above
```

## 6. Build Tooling

### Status quo

- **Leiningen** (`project.clj`) — all builds, REPL, CLJS compilation
- **cljsbuild** — ClojureScript prod builds (`:simple` optimization)
- **foreman** — local dev process management (`Procfile.dev`)

### [goal] Alignment with Clojure ecosystem

- [ ] Migrate from Leiningen to **deps.edn + tools.build** (consistent
  with Clojure core and contrib libraries)
- [ ] Replace cljsbuild with **figwheel-main** for prod builds (already
  a dependency, used for dev but not prod)
- [ ] Update `system.properties` from Java 1.7 to current LTS
- [ ] Add GitHub Actions CI to run `bin/build` on every PR
- [ ] Add a `/health` endpoint for automated monitoring
