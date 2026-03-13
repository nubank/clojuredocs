# Changelog

All notable changes to the Nubank fork of [ClojureDocs](https://clojuredocs.org) are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Changes are relative to the upstream [zk/clojuredocs](https://github.com/zk/clojuredocs) `master` branch.

---

## [Unreleased]

### Fixed
- Fix test-paths in `project.clj` so `lein test` discovers tests (#20)
- Replace broken placeholder test with passing one

---

## [1.0.0-nu] — 2026-03-13

First tagged release of the Nubank fork. Encompasses all changes since forking
from upstream `zk/clojuredocs` (commit `31d4bd2`).

### Added
- **tools.build library support**: new namespace `clojure.tools.build.api` with
  documentation, landing page, and link guide
  ([#3](https://github.com/nubank/clojuredocs/pull/3))
- **Sticky search bar**: search bar is now sticky and visible on mobile
  ([#15](https://github.com/nubank/clojuredocs/pull/15))
- **Developer docs**: VS Code / Calva dev setup guide, general dev setup guide
  ([#6](https://github.com/nubank/clojuredocs/pull/6))
- **Glossary**: 21-term glossary for codebase analysis (issue #4)
  ([#10](https://github.com/nubank/clojuredocs/pull/10))
- **Research**: data-model coupling audit
  ([#19](https://github.com/nubank/clojuredocs/pull/19))
- Clojure survey banner (later removed)
- New namespace docs: `clojure.java.process`, `clojure.repl.deps`,
  `clojure.tools.deps.interop`

### Changed
- Clojure version updates: 1.12.1 → 1.12.2 → 1.12.3 → 1.12.4
- Nubank branding assets
- Replaced Google Analytics with Matomo
- Updated MongoDB data export
- Upgraded nrepl 1.6.0, cider-nrepl 0.58.0 to fix middleware errors
- Updated Ring 1.13.0, commons-io 2.21.0 (CVE-free)
- Correct gravatar size in "Recently Updated" section
  ([#17](https://github.com/nubank/clojuredocs/pull/17))

### Removed
- Unused `.less` files (16k+ lines)
  ([#16](https://github.com/nubank/clojuredocs/pull/16))
- Third-party Mailgun integration
- Survey banner (after survey ended)
- Generated `app.css` from version control

### Fixed
- Landing page search uses `q` param to match backend handler
  ([#15](https://github.com/nubank/clojuredocs/pull/15))
- Navbar search hidden on landing page to avoid duplication
- Stray quote in CSS `position` value
- Mobile push-wrapper `:right 0` placement
- `.gitignore` for `.calva/` and generated assets

### Security
- Ring reverted from 1.15.3 to 1.13.0 (breaking API change), then
  commons-io upgraded to 2.21.0 to resolve CVEs independently

---

<!-- Links -->
[Unreleased]: https://github.com/nubank/clojuredocs/compare/v1.0.0-nu...HEAD
[1.0.0-nu]: https://github.com/nubank/clojuredocs/compare/31d4bd2...v1.0.0-nu
