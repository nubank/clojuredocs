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

## `gen_model_views.clj`

Single entry point that regenerates **all** derived views of
[`docs/entity-attribute-model.edn`](../docs/entity-attribute-model.edn) — the Mermaid ER diagram and both
CSVs — by shelling out to the two generators below.

```sh
bb tools/gen_model_views.clj           # regenerate diagram + CSVs
bb tools/gen_model_views.clj --check   # also round-trip the diagram through Kroki
```

Pure babashka — no JVM, Leiningen, or deps involvement. Run it after editing the EDN. Output is
deterministic, so a clean tree after running means the views are already in sync with the EDN.

## `edn_to_mermaid.clj`

Generates [`docs/diagrams/entity-attribute-er.md`](../docs/diagrams/entity-attribute-er.md) — a Mermaid
`erDiagram` with a Key/Legend and Sources — from [`docs/entity-attribute-model.edn`](../docs/entity-attribute-model.edn)
(entity boxes from `:entities`/`:attrs`, edges from `:relationships`).

```sh
bb tools/edn_to_mermaid.clj            # write the diagram doc
bb tools/edn_to_mermaid.clj --check    # also POST to kroki.io to confirm it renders
bb tools/edn_to_mermaid.clj --stdout   # print the markdown, write nothing
```

Every identifier is sanitized **by construction** to be Mermaid-safe; a self-lint then asserts the output is
well-formed (identifier regex, valid cardinality tokens, balanced braces, no braces leaking into comments), so
the diagram is guaranteed to parse. Output is **deterministic** — dates come from the EDN's `:generated`, not
wall-clock — so re-runs are byte-identical. The generated file carries a "do not edit by hand" banner;
regenerate instead of editing. Requires **babashka** (`bb`) on `$PATH`; `--check` also needs network access.

## `edn_to_csv.clj`

Generates two CSV views of [`docs/entity-attribute-model.edn`](../docs/entity-attribute-model.edn) for import
into a spreadsheet (Google Sheets: **File > Import > Upload**, one file per tab):

- `docs/diagrams/entity-attributes.csv` — one row per (entity, attribute), with type, refs, status, coverage, evidence.
- `docs/diagrams/entity-relationships.csv` — one row per relationship edge (`From`, `To`, `Via`, `Cardinality`, `Label`).

```sh
bb tools/edn_to_csv.clj            # write both CSVs
bb tools/edn_to_csv.clj --stdout   # print the attributes CSV, write nothing
```

Quoting (commas, quotes, newlines in descriptions) is handled by `clojure.data.csv`. Output is deterministic
(rows sorted, no timestamps), so re-runs are byte-identical. Requires **babashka** (`bb`) on `$PATH`.

## Other scripts

| Script | Purpose |
|--------|---------|
| `dev_export.clj` | Export development data |
| `old_import.clj` | Legacy data import |
| `sanity_check.clj` | Data sanity checks |
| `top_contribs.clj` | Top contributors report |
