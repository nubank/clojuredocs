# ClojureDocs Glossary & Library Addition Criteria

## Glossary

- **Library:** A Clojure project providing reusable code, typically distributed as a dependency (e.g., `core.async`, `tools.build`).
- **Namespace:** A logical grouping of vars (functions, macros, etc.) within a library (e.g., `clojure.tools.build.api`).
- **Var:** A named reference to a function, macro, or value within a namespace.
- **Dependency:** An external library required by the project, listed in `project.clj`.
- **Markdown Overview:** A documentation file in `src/md/namespaces/` describing a library and linking to resources.
- **clojure-namespaces:** The vector in `src/clj/clojuredocs/search/static.clj` listing all supported namespaces/libraries.

## Criteria for Adding a Library

1. **Relevance:** Library is widely used or officially supported by the Clojure/core team.
2. **Stability:** Library is actively maintained and has stable releases.
3. **Documentation:** Sufficient official or community documentation exists.
4. **License:** Library is open source and compatible with ClojureDocs' license.
5. **Technical Feasibility:** Library can be loaded and introspected at runtime without conflicts.
6. **Community Value:** Adding the library will benefit the broader Clojure community.

---

*Drafted: 2026-02-19 by Jordan Miller*
