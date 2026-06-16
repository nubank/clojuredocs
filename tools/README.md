# Tools

Scripts for data generation and maintenance. Run manually from a REPL.

## `gen_dialect_compat.clj`

Generates `resources/dialect-compat.edn` — the static compatibility index mapping 700 vars to their supported dialects (Clojure/JVM, ClojureScript, babashka).

### Prerequisites

- **Leiningen** — for the project REPL (provides CLJS analyzer dependency)
- **babashka** (`bb`) — installed and on `$PATH` (the script shells out to `bb -e` to query bb's `ns-publics`)

### How to run

```sh
lein repl
```

```clojure
(load-file "tools/gen_dialect_compat.clj")
(tools.gen-dialect-compat/generate!)
```

Output is written to `resources/dialect-compat.edn`. Commit the generated file.

### When to regenerate

Regenerate when any of these change:

- Clojure version (currently 1.12.4)
- ClojureScript version (currently 1.12.134, declared in `project.clj`)
- babashka version (currently 1.12.215, from `bb --version`)

The generated file includes a `:versions` header for traceability.

## `validate_metadata.clj`

Validates the OKF + RDF YAML frontmatter on every prose doc under `docs/` against
[`docs/metadata-schema.edn`](../docs/metadata-schema.edn) (the source of truth). Enforces the convention in
[CLAUDE.md](../CLAUDE.md) and [docs/rfcs/okf-metadata-rfc.md](../docs/rfcs/okf-metadata-rfc.md).

### How to run

```sh
bb tools/validate_metadata.clj   # from the repo root
```

Prints `PASS`/`FAIL` per document and a summary. **Exits non-zero if any document has an error**, so it can
gate a commit or a CI job. Errors fail the run (missing frontmatter, missing required `type`, invalid
`review_maturity`, malformed date); unknown `type` values and unknown keys are warnings. Also checks that
every schema key has a JSON-LD mapping in [`docs/context.jsonld`](../docs/context.jsonld).

Requires **babashka** (`bb`) on `$PATH` — it uses bb's bundled `clj-yaml` and `cheshire`, so no JVM or lein
dependencies are needed.

## Other scripts

| Script | Purpose |
|--------|---------|
| `dev_export.clj` | Export development data |
| `old_import.clj` | Legacy data import |
| `sanity_check.clj` | Data sanity checks |
| `top_contribs.clj` | Top contributors report |
