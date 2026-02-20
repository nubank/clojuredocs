# Adding Support for Additional Libraries in ClojureDocs

## Overview
This document describes the current process for adding support for new libraries to ClojureDocs, outlines constraints and required modifications, and proposes a plan for adding `tools.build` as the first new library beyond `clojure.core`.

---

## Current Process to Add a Library

1. **Add the library dependency** to `project.clj`.
   - Example: `[org.clojure/tools.build "0.9.6"]`
2. **Add the namespace symbol** to the `clojure-namespaces` vector in `src/clj/clojuredocs/search/static.clj`.
   - Example: `'clojure.tools.build`
3. **Create a Markdown file** in `src/md/namespaces/` for the library (e.g., `clojure.tools.build.md`).
   - Include a short description and links to documentation, articles, or videos.
4. **Restart the server** to pick up changes.

---

## Constraints & Required Modifications

- **Static Namespace List:** Currently, supported libraries are hardcoded in `clojure-namespaces`. This requires a code change and deploy for each new library.
- **Dependency Management:** All libraries must be added to `project.clj` to be available at runtime.
- **Documentation:** Each library should have a corresponding Markdown file for its overview.
- **No Dynamic Loading:** There is no UI or API for dynamically adding libraries without a code change.

---

## Proposed Approach for Adding Libraries

- **Short Term:** Continue with the current process for `tools.build`.
- **Longer Term:** Consider refactoring to allow dynamic registration of libraries (e.g., via config or admin UI/API), reducing the need for code changes.

---

## Plan for Adding `tools.build`

1. Add `[org.clojure/tools.build]` to `project.clj` dependencies.
2. Add `'clojure.tools.build` to `clojure-namespaces` in `src/clj/clojuredocs/search/static.clj`.
3. Create `src/md/namespaces/clojure.tools.build.md` with a description and resource links.
4. Restart the app and verify that `tools.build` appears on the site and its functions are browsable.

---

## Next Steps
- Draft a glossary and criteria for future library additions.
- Review with the ClojureDocs team and iterate as needed.

---

*Last updated: 2026-02-19 by Jordan Miller*
