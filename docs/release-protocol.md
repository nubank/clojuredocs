# Release & Announcement Protocol

How we ship and communicate changes for the Nubank ClojureDocs fork.

---

## 1. Versioning

We use **semantic-ish tags** with a `-nu` suffix to distinguish from upstream:

```
v<major>.<minor>.<patch>-nu
```

- **major** — breaking changes to the site (URL scheme, data model, auth)
- **minor** — new features visible to users (new namespaces, UI changes)
- **patch** — bug fixes, dependency updates, docs-only changes

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
   git tag -a v<version> -m "Release v<version>"
   git push origin v<version>
   ```
4. **Deploy** (see §4 below).
5. **Post the announcement** (see §3 below).

## 3. Announcement Template

Post to the agreed channel (Slack `#clojuredocs`, GitHub Discussions, or both)
using this template:

```markdown
## ClojureDocs v<version> released

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

## 4. Deploy Procedure (AWS)

Production runs on an AWS t2.micro with nginx load-balancing to two JVMs
managed by Upstart. Zero-downtime deploy via rolling restart.

```bash
# On the production box:
cd $REPO

# Stop first process
sudo service clojuredocs-web-1 stop

# Pull and build
git pull origin master
bin/build

# Start first process, wait for it to serve requests
sudo service clojuredocs-web-1 start
sleep 15

# Rolling restart of second process
sudo service clojuredocs-web-2 restart
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

If something is broken after deploy:

```bash
# On the production box:
sudo service clojuredocs-web-1 stop
sudo service clojuredocs-web-2 stop
git checkout <previous-tag>
bin/build
sudo service clojuredocs-web-1 start
sudo service clojuredocs-web-2 start
```

## 6. Future Improvements

- [ ] Add GitHub Actions CI to run `bin/build` on every PR
- [ ] Automate changelog generation from conventional commits
- [ ] Add a `/health` endpoint for automated monitoring
- [ ] Consider GitHub Releases for distributing changelogs
