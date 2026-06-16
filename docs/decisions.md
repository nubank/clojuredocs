---
type: Decision Log
title: Decision Log
description: Design and architecture decisions; lightweight alternative to full ADRs.
tags: [decisions, architecture, living-document]
created: 2026-04-26
modified: 2026-06-16
creator: L. Jordan Miller
ai_assisted: "Claude Opus 4.6 via GitHub Copilot (early entries); Claude Opus 4.8 via Claude Code (later entries)"
review_maturity: L4
review_note: Human-endorsed — decisions are the team's; AI-drafted rationale is owned by the author.
---

# Decision Log

Document design and architecture decisions. Lightweight alternative to full ADRs.

---

## 2026-06-16 — Generate the entity-attribute ER diagram from the EDN

### Status
Decided

### Context
- With `:relationships` now first-class in [entity-attribute-model.edn](entity-attribute-model.edn) (15 edges, each carrying `:via` provenance), the data needed to draw an ER diagram lives in the source of truth.
- The [2026-06-05 decisions](#2026-06-05--replace-mermaid-er-diagrams-with-edn-schema-and-miro-visual) concluded that Mermaid `erDiagram` could not express the intended visual language (legend, status distinction) and leaned toward Miro as the canonical visual, dropping Mermaid. That conclusion predates having relationships in the EDN and a generator that can emit a legend.
- The PR #57 review reinforced data-first. Sierra noted the tradeoff directly: a generated (algorithmically laid-out) diagram lacks the spatial meaning a person encodes by hand, **but** has the converse advantage of showing only the relationships actually expressed in the model, with no hidden assumptions.

### Decision
- Generate the ER diagram from the EDN with a babashka script ([tools/edn_to_mermaid.clj](../tools/edn_to_mermaid.clj)) that emits a Mermaid `erDiagram` plus a Key/Legend and Sources ([docs/diagrams/entity-attribute-er.md](diagrams/entity-attribute-er.md)).
- The generated Mermaid diagram is the in-repo, diff-able, GitHub-rendered view. A hand-arranged Miro board remains the spatial/pedagogical view. They are complementary — not a replacement for each other.

### Rationale
- Reliability ratchet: the diagram becomes a deterministic artifact derived from the source of truth, not hand-drawn prose that drifts. Identifiers are sanitized by construction, a self-lint asserts valid Mermaid, and a Kroki round-trip confirms it parses.
- Sierra's framing splits the work cleanly: the generator gives faithfulness (only what's in the model); Miro gives spatial intuition. Keeping both captures both properties.
- This **partially supersedes** the 2026-06-05 "drop Mermaid entirely" stance: Mermaid is used, but generated (not hand-maintained) and as a structural complement to Miro, not the sole canonical visual.

### Alternatives Considered
- Hand-maintain a Mermaid diagram — the original failure mode (drift, the two-diagram split). Rejected.
- Miro only — no in-repo, diff-able view generated from the source of truth.
- Graphviz / PlantUML / D2 — richer layout, but not natively rendered on GitHub (would require a committed SVG or Kroki) and still not generated from the EDN.

### Impacts and Risks
- The generated file must not be hand-edited; a banner and a Provenance & Review footer say so, and re-runs are byte-identical.
- The legend's glyph gloss is hardcoded prose that could drift if a new cardinality is added — noted in the [review run](diagrams/entity-attribute-er_research-review_run_1.md).
- The generator bb scripts may be extracted into a separate PR; this entry records the decision regardless of where the code lands.

### Links
- [PR #57](https://github.com/nubank/clojuredocs/pull/57)
- [tools/edn_to_mermaid.clj](../tools/edn_to_mermaid.clj)
- [docs/diagrams/entity-attribute-er.md](diagrams/entity-attribute-er.md)
- [entity-attribute-model.edn](entity-attribute-model.edn)
- [Research-review run 1](diagrams/entity-attribute-er_research-review_run_1.md)

---

## 2026-06-16 — Enforce doc metadata with a bb validator (three surfaces)

### Status
Decided

### Context
The OKF + RDF frontmatter convention was adopted but unenforced — nothing prevented drift or caught a missing
`type` / bad `review_maturity`.

### Decision
Add [tools/validate_metadata.clj](../tools/validate_metadata.clj) (babashka) validating `docs/**.md` against
[docs/metadata-schema.edn](metadata-schema.edn), wired into three surfaces: a git pre-commit hook
(`.githooks/pre-commit`, enabled via `bin/install-hooks`), `lein test` (`clojuredocs.metadata-test`), and CI
(`.github/workflows/docs-metadata.yml`). The `bb` script is the single source of truth; the test and hook
shell out to it. `metadata-schema.edn` is the editable source of truth for the taxonomy and field rules.

### Consequences
Bad frontmatter fails fast locally (hook), in the suite (`lein test`), and authoritatively on PRs (CI). New
`type` values or fields are changed in one place (`metadata-schema.edn`).

---

## 2026-06-16 — Adopt OKF + RDF-aligned YAML frontmatter for doc metadata

### Status
Decided

### Context
Document AI-provenance/review metadata was encoded as a Markdown blockquote (`> **Document metadata**`),
which is human-readable but not machine-parseable and is non-conformant with the
[Open Knowledge Format (OKF) v0.1](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md)
(no frontmatter, no required `type`).

### Decision
Replace the blockquote with YAML frontmatter conformant to OKF (required `type`), with field semantics aligned
to [Dublin Core Terms](https://www.dublincore.org/specifications/dublin-core/dcmi-terms/) and
[PROV-O](https://www.w3.org/TR/prov-o/). RDF stays additive via an optional JSON-LD context
([docs/context.jsonld](context.jsonld)); the required surface remains pure OKF. The L0–L4 review-maturity
model is retained as a queryable `review_maturity` field. See
[RFC: OKF + RDF document metadata](rfcs/okf-metadata-rfc.md). A `bb` validator is a planned follow-up.

### Consequences
All prose docs under `docs/` migrate to frontmatter; `docs/` becomes an OKF bundle (`docs/index.md`). YAML
frontmatter renders as a table on GitHub rather than the prior custom blockquote — accepted in exchange for
machine-parseability.

---

## 2026-06-09 — Data first: defer the Miro visual

### Status
Decided

### Context
- The [2026-06-05 entries](#2026-06-05--replace-mermaid-er-diagrams-with-edn-schema-and-miro-visual) named Miro the "canonical visual" for the entity model, with a PDF export to be checked into the repo.
- In practice no Miro diagram or PDF was ever produced or committed (`git ls-files` shows no PDF), yet the docs described it as the current canonical representation.
- Reviewing with Alex: he can review the data (the EDN) directly — a diagram is not a prerequisite for review.

### Decision
- Prioritize sound, verified data (the EDN) as the first deliverable and the review artifact. Treat the Miro visual as the eventual/ideal representation, deferred until the data is solid — not a blocker for review.

### Rationale
- Alex can review the EDN directly, so a visual is not on the critical path.
- The EDN is REPL-verified and test-guarded — it is the trustworthy substrate a diagram would be derived from anyway (data first, views second; the same principle behind the 2026-06-05 EDN decision).
- Sierra's ["Developing the Language of the Domain"](https://www.cognitect.com/blog/2017/4/6/developing-the-language-of-the-domain) makes the same case: the domain model is data, and visualizations are *derived views*. That ordering puts the data first and treats a diagram as something generated from it, not a prerequisite — exactly the data-first stance Alex's review confirmed.
- Avoids overstating a Miro artifact that does not exist.

### Alternatives Considered
- Produce the Miro diagram first — front-loads a visual the reviewer doesn't need yet, and which would have to track the still-settling data. Also inverts Sierra's data → derived-view ordering.
- Keep claiming Miro as canonical — misrepresents the actual state (no diagram/PDF exists).

### Impacts and Risks
- Supersedes the "Miro as the canonical visual" priority from the 2026-06-05 entries. The EDN-as-source-of-truth decision stands.
- Risk: the visual is deferred indefinitely. Mitigation: it remains the stated eventual goal; the EDN can generate derived views when wanted.

### Links
- [entity-attribute-model.edn](entity-attribute-model.edn)
- [Sierra, "Developing the Language of the Domain" (Cognitect, 2017)](https://www.cognitect.com/blog/2017/4/6/developing-the-language-of-the-domain)
- [2026-06-05 — Replace Mermaid ER diagrams with EDN schema and Miro visual](#2026-06-05--replace-mermaid-er-diagrams-with-edn-schema-and-miro-visual)
- [errata #11](errata.md)

---

## 2026-06-09 — Enforce the entity model with a test namespace

### Status
Decided

### Context
- The entity model's value is the reliability ratchet (LLM → REPL → Library → **Enforcement**). Until now its claims lived in prose + EDN, REPL-verified once but unguarded against drift.
- The [2026-04-28 RCF decision](#2026-04-28--verify-dialect-compat-via-rich-comment-blocks-not-unit-tests) chose rich comment blocks over unit tests because "adding a unit test to a project with zero real test coverage sets a convention nobody is following yet."
- That tradeoff shifted: the model produces dozens of exact, REPL-verified facts (var count 1,572; per-key coverage; the special-form/type behavior; embedded-doc uniformity) worth locking against regression and Clojure-version drift.

### Decision
- Add [`test/clojuredocs/entity_model_test.clj`](../test/clojuredocs/entity_model_test.clj) (25 tests, ~12.5k assertions). It loads the EDN and checks the JVM-heap entities (Library, Namespace, Var) and DialectCompat against the running system on every run. MongoDB entities are intentionally excluded — they need a DB connection and are snapshot-derived (tracked in [#66](https://github.com/nubank/clojuredocs/issues/66)).

### Rationale
- Hardens REPL findings into enforcement — the final ratchet step. A regression or a Clojure bump now fails a test instead of silently rotting the doc.
- Deliberately establishes the test convention for the entity-model domain, superseding the "nobody follows it" rationale of the 2026-04-28 RCF decision. The dialect-compat RCF still exists; this suite also covers dialect-compat lookups.
- Draws an explicit, honest verification boundary: in-process entities are test-guarded; database-resident cardinalities stay snapshot-derived (L2) until #66 adds a DB-backed tier.

### Alternatives Considered
- Keep REPL/RCF verification only — ephemeral, drifts silently; was the prior convention but doesn't scale to the model's many exact claims.
- Test the MongoDB entities too — needs a DB connection and seed fixtures in the harness; deferred to #66 rather than blocking this work.
- Looser assertions (e.g. `subset?` on the type set) — masked a real error (the phantom `:type "special-form"`); the suite uses exact assertions instead (errata #8).

### Impacts and Risks
- Counts are pinned to Clojure 1.12.4; a bump fails many tests at once. Intended (the ratchet), but there is no single "version changed" signal yet — tracked in [#68](https://github.com/nubank/clojuredocs/issues/68).
- MongoDB claims remain unguarded ([#66](https://github.com/nubank/clojuredocs/issues/66)), noted in the model metadata and the RFC.

### Links
- [test/clojuredocs/entity_model_test.clj](../test/clojuredocs/entity_model_test.clj)
- [entity-attribute-model.edn](entity-attribute-model.edn)
- [2026-04-28 — Verify dialect compat via rich comment blocks](#2026-04-28--verify-dialect-compat-via-rich-comment-blocks-not-unit-tests)
- [#66](https://github.com/nubank/clojuredocs/issues/66), [#68](https://github.com/nubank/clojuredocs/issues/68)

---

## 2026-06-09 — Add `:absent` status state for code-supported, data-absent keys

### Status
Decided

### Context
- Verifying the EDN against the running system surfaced `:no-doc`: it is listed in `search/var-keys` (so the gather pipeline would retain it), but no var in Clojure 1.12.4 carries it (0/1,572, REPL-verified).
- The existing status vocabulary (`:exists` / `:nil` / `:vestigial` / `:planned` / `:gap`) had no state for "the code path keeps this key, but the current dataset never populates it."
- The schema claims to enumerate the full key universe, and `var-key-universe-matches-schema` enforces `actual ⊆ schema`. Omitting `:no-doc` left the schema silently incomplete relative to `var-keys`.

### Decision
- Add a sixth status state, `:absent` — "key is retained by code (e.g. listed in `search/var-keys`) but no record in the current dataset carries it." Document `:no-doc` as `:absent`, coverage `0/1572`.

### Rationale
- Reconciles the schema with the code's declared key set (`var-keys`), not just observed data — a future Clojure var carrying `:no-doc` would be caught by the existing test rather than silently breaking it.
- Keeps `:exists` honest: only data actually observed earns `:exists`.
- Additive — the test's `valid-states` gains one entry; no existing attribute changes meaning.

### Alternatives Considered
- Reuse `:nil` — inaccurate; the key is not present at all, not present-with-nil.
- Reuse `:gap` — inaccurate; `:gap` means no code exists, but the code path does retain the key.
- Reuse `:vestigial` — inaccurate; that implies the key is present but meaningless.
- Omit `:no-doc` — leaves the "full key universe" claim false and the test passing only by luck of current data.

### Impacts and Risks
- One new state to keep in the header semantics list and the test's `valid-states`.
- Risk: low — narrowly defined, currently used by exactly one attribute.

### Links
- [entity-attribute-model.edn](entity-attribute-model.edn) — `:var :no-doc`
- [errata #9](errata.md)
- [src/clj/clojuredocs/search.clj](../src/clj/clojuredocs/search.clj) — `var-keys`

---

## 2026-06-09 — Supersede the entity-model CSV with the EDN

### Status
Decided

### Context
- The [2026-06-05 EDN decision](#2026-06-05--replace-mermaid-er-diagrams-with-edn-schema-and-miro-visual) set the condition: retain [entity-attribute-model.csv](entity-attribute-model.csv) "until the EDN schema subsumes it." The EDN is now the REPL-verified, test-guarded source of truth, so that condition is met.
- The CSV was never updated after the EDN corrections — it still carries uncorrected errata #2 (Namespace.library-url), #7 (LegacyVarRedirect missing `_id`/`library-url`), #8 (Var.type "special-form"), #9 (Var.no-doc "exists"), plus a wrong `User.account-source` ("always github" — legacy `clojuredocs` users exist).
- But the CSV's GAP section is the only structured record of several envisioned entities (`Resource`, `QualitySignal`, `Group`, `Var.source-url`, `User.reputation`, …) that were never migrated into the EDN.

### Decision
- Mark the CSV superseded with a banner header pointing at the EDN, and stop maintaining it. Retire (delete) it during the [#43](https://github.com/nubank/clojuredocs/issues/43) vision pass, once its GAP/envisioned rows are migrated into the EDN as `:gap` entries.

### Rationale
- A reader opening the CSV directly must not be misled into treating stale, error-bearing rows as current — the banner makes the status unmissable.
- Deleting now would drop the only structured record of the envisioned future-state; keeping it until the vision pass preserves that scaffolding.
- Resolves the "not yet corrected in CSV" status on errata #2 and #7 by retirement rather than by maintaining a second source — avoiding the drift the 2026-06-05 decision warned about.

### Alternatives Considered
- Correct the CSV to match the EDN — re-creates the two-source drift the EDN was meant to end; wasted effort on a file being retired.
- Delete it now — loses the envisioned entities (only in git history) before they are migrated.
- Leave it unmarked — readers mistake stale rows for current state.

### Impacts and Risks
- The banner is a non-data first row; harmless because nothing loads the CSV programmatically (docs-only artifact, verified).
- Risk: the vision pass forgets to migrate the GAP rows before deleting. Mitigation: this entry and the banner both name the GAP rows as the migration payload.

### Links
- [entity-attribute-model.csv](entity-attribute-model.csv)
- [entity-attribute-model.edn](entity-attribute-model.edn)
- [errata.md](errata.md) — #2 and #7
- [2026-06-05 — Replace Mermaid ER diagrams with EDN schema and Miro visual](#2026-06-05--replace-mermaid-er-diagrams-with-edn-schema-and-miro-visual)

---

## 2026-06-09 — Sidecar REPL scratchpad for entity model verification

### Status
Decided

### Context
- Verifying the entity-attribute model requires evaluating forms against the running system — inspecting actual keys on vars, namespace shapes, and collection contents.
- Rich comment forms inside source files mix investigation code with production code, adding noise to diffs and expanding the AI context window unnecessarily.
- The project already has a `tools/` directory for one-off scripts, but those are meant to be run as standalone programs, not evaluated interactively.

### Decision
- Create [`dev/sidecar_repl.clj`](dev/sidecar_repl.clj) as a REPL scratchpad for interactive investigation. All exploratory `comment` forms go here, not in source files.

### Rationale
- Keeps production source files clean — no exploratory code in diffs or PRs.
- Reduces AI context window cost — AI tools reading source files don't ingest investigation noise.
- The `comment` block pattern makes forms individually evaluable at the REPL without side effects on load.
- Scratchpad is committed so investigation history is visible and reproducible.

### Alternatives Considered
- Rich comment forms in source files — conventional in Clojure, but mixes investigation with production code and inflates diffs.
- Ephemeral REPL history only — loses the investigation record; not reproducible by reviewers.
- Separate `.repl` files (transcriptor-style) — appropriate for verification tests but too structured for exploratory investigation.

### Impacts and Risks
- `dev/` directory needs to be on the classpath for the namespace to resolve. Mitigation: it's a `comment`-block scratchpad — eval individual forms, don't load the file.

---

## 2026-06-05 — Replace Mermaid ER diagrams with EDN schema and Miro visual

### Status
Decided

### Context
- Mermaid's `erDiagram` parser treats hyphens as operators and rejects `{}`, `|`, `()`, and unicode characters in attribute descriptions. Clojure uses kebab-case pervasively, making every attribute name a syntax error.
- Fixing the diagrams required mangling all attribute names to underscores (`library-url` → `library_url`) and stripping descriptive annotations — losing fidelity to the actual domain.
- The [2026-06-05 decision to correct the diagram in Miro](#2026-06-05--correct-entity-model-diagram-manually-in-miro) already established Miro as the canonical visual.
- Sierra's ["Developing the Language of the Domain"](https://www.cognitect.com/blog/2017/4/6/developing-the-language-of-the-domain) describes this exact pattern: the domain model is data, visualizations are derived views.
- The repo already has precedent for EDN as a data format: `dialect-compat.edn`.

### Decision
- Remove Mermaid ER diagrams from [entity-attribute-model.md](docs/entity-attribute-model.md).
- Use EDN as the machine-readable source of truth for the entity model, with a `:status` field (`:exists` / `:gap` / `:vestigial`) on each entity and attribute.
- Use Miro as the canonical visual diagram, with a PDF export in the repo for readers without Miro access.
- Retain [entity-attribute-model.csv](docs/entity-attribute-model.csv) as the per-attribute detail format until the EDN schema subsumes it.

### Rationale
- EDN preserves kebab-case attribute names, supports nested structures, sets, and relationships — things CSV and Mermaid cannot express.
- EDN is loadable at the REPL, queryable, and validatable against the actual codebase via a babashka script.
- The `:status` field replaces the visual encoding (solid vs. dashed lines) with queryable data, enabling filtered views.
- This follows the reliability ratchet: LLM produced prose model → human corrected in Miro → EDN makes it data → bb script can enforce it.

### Alternatives Considered
- Fix Mermaid with underscored names — renders but loses fidelity to the Clojure domain; attribute names no longer match the codebase.
- PlantUML or D2 — richer syntax but no native GitHub rendering; adds a rendering dependency.
- Keep Mermaid alongside EDN — redundant; Mermaid is a lossy translation of information the EDN and Miro already capture faithfully.

### Impacts and Risks
- PDF is not diffable in git. Mitigation: PDF exports are versioned by dated filename (e.g., `entity-model-2026-06-05.pdf`) and old versions are kept, creating a visual version history through accumulation. The EDN and CSV remain the diffable sources.
- EDN schema does not yet exist — this decision commits to creating it. Mitigation: the CSV covers the gap until the EDN is written.

---

## 2026-06-05 — Unify ER diagrams with legend and magnitude annotations

### Status
Decided

### Context
- Sierra reviewed PR #57 and discovered Claude produced two separate ER diagrams instead of the single unified diagram specified in the 2026-06-01 decision entry.
- The entity-model.md header says "Solid lines = exists today. Dashed lines = required by vision" but the diagrams don't deliver this — they are two disconnected `erDiagram` blocks.
- No formal legend or key exists for reading the diagram.
- No order-of-magnitude grounding: the diagram doesn't communicate that there is 1 Library (hardcoded singleton), ~30 Namespaces, ~700 Vars, and thousands of Examples.
- Separately, Sierra's ["Developing the Language of the Domain"](https://www.cognitect.com/blog/2017/4/6/developing-the-language-of-the-domain) (Cognitect, 2017) argues for building a precise, shared vocabulary for the domain — naming entities and attributes the way the team actually talks about the system. That is a naming/vocabulary point, distinct from the legend-and-scale rationale above; the blog does not speak to diagram legends or scale.

### Decision
- Correct the entity model to be a single unified diagram with a formal legend distinguishing current-state entities from vision-gap entities, and annotate entities with approximate cardinality.

### Rationale
- A single diagram with differentiated line styles shows gaps in context rather than in isolation, making the distance between current state and vision visible at a glance.
- A legend makes the diagram self-documenting — readers shouldn't need to find prose elsewhere to interpret it.
- Order-of-magnitude counts (1 / ~30 / ~700 / thousands) shape design intuition about which entities dominate and where denormalization costs compound.

### Alternatives Considered
- Leave as two separate diagrams — loses the visual comparison between what exists and what's missing; readers must mentally merge them.
- Regenerate with AI — would reproduce the same class of error (instruction deviation) without the human engaging with the domain structure.

### Impacts and Risks
- Mermaid `erDiagram` has limited native support for dashed vs. solid relationship lines. The canonical corrected diagram will live in Miro rather than in Mermaid source.
- Risk: Miro diagram drifts from the Markdown file. Mitigation: Markdown file references the Miro board; legend in Markdown documents the intended visual language even if Mermaid can't fully render it.

### Links
- [PR #57](https://github.com/nubank/clojuredocs/pull/57)
- [docs/entity-attribute-model.md](docs/entity-attribute-model.md)
- [Sierra, "Developing the Language of the Domain"](https://www.cognitect.com/blog/2017/4/6/developing-the-language-of-the-domain)

---

## 2026-06-05 — Correct entity model diagram manually in Miro

### Status
Decided

### Context
- The AI-generated ER diagram in PR #57 deviated from the stated design (two diagrams instead of one, no legend, no magnitude).
- Miro is the team's standard diagramming tool. Mermaid ER diagrams have rendering limitations (no dashed lines, no annotations) that constrain what can be expressed in-repo.
- Jordan's pedagogical framework is constructivism: learning happens through the act of constructing and correcting mental models, not through passively receiving correct output.
- Sierra observed that humans infer meaning from spatial relationships — proximity, above/below, left-to-right — which a person expresses intuitively when arranging a diagram by hand and which an algorithmically-generated layout usually lacks. (She also noted the converse advantage of generated diagrams: they show only the relationships actually expressed in the model, without hidden assumptions.) The team took this as motivation to correct the diagram by hand to internalize the domain model.

### Decision
- Import the existing diagram into Miro and manually correct it rather than having AI regenerate it, to ensure active engagement with the domain structure.

### Rationale
- Manual correction forces the author to evaluate each entity and relationship against the codebase, building the mental model that AI-generated output bypasses.
- Miro supports the visual language (dashed lines, color coding, annotations) that Mermaid's `erDiagram` syntax cannot express.
- The corrected Miro diagram becomes a presentation artifact for the Clojure Guild session (~June 19), where the correction process itself is the subject.

### Alternatives Considered
- Have Claude regenerate with corrected instructions — faster but eliminates the learning opportunity; likely to introduce new deviations from intent.
- Correct in Mermaid syntax only — limited by Mermaid's `erDiagram` capabilities; can't render the intended visual language.

### Impacts and Risks
- The canonical diagram lives in Miro, not in the repo. The Markdown file retains a Mermaid approximation for GitHub rendering.
- Risk: Miro board is not version-controlled. Mitigation: the Markdown Mermaid diagram and CSV remain the in-repo source of truth for attributes and relationships; Miro is the visual authority.

### Links
- [PR #57](https://github.com/nubank/clojuredocs/pull/57)
- [docs/entity-attribute-model.md](docs/entity-attribute-model.md)

---

## 2026-06-01 — Dual-format entity model documentation

### Status
Decided

### Context
- Issue #43 requires defining the entity-attribute model for ClojureDocs.
- The model serves two audiences: humans reading a narrative document and humans doing column-level analysis in a spreadsheet.
- The data model coupling audit (`docs/research/data-model-coupling-audit.md`) already established that the data model is implicit and scattered.

### Decision
- Document the entity-attribute model as both a Mermaid ER diagram in Markdown (`docs/entity-attribute-model.md`) and a flat CSV (`docs/entity-attribute-model.csv`).

### Rationale
- Mermaid ER diagrams render natively in GitHub and show relationships visually.
- CSV enables filtering, sorting, and gap analysis in any spreadsheet tool.
- Each format plays to different strengths — the Markdown captures structure and narrative observations, the CSV captures per-attribute detail.

### Alternatives Considered
- Markdown only — loses the per-attribute detail and sortability that CSV provides.
- Database DDL / SQL schema — the system uses MongoDB (schemaless); a relational DDL would be misleading about the actual storage model.
- Single prose document — harder to audit systematically; the data model coupling audit already showed that implicit models resist prose-only documentation.

### Impacts and Risks
- Two files to keep in sync when the model changes.
- Risk: drift between formats. Mitigation: the CSV is the source of truth for attribute-level detail; the Markdown is the source of truth for relationships and observations.

### Links
- [Issue #43](https://github.com/nubank/clojuredocs/issues/43)
- [docs/entity-attribute-model.md](docs/entity-attribute-model.md)
- [docs/entity-attribute-model.csv](docs/entity-attribute-model.csv)
- [Data model coupling audit](docs/research/data-model-coupling-audit.md)

---

## 2026-06-01 — File bugs discovered during model extraction as separate issues

### Status
Decided

### Context
- During entity-attribute model extraction for #43, code reading revealed 4 bugs: an indexes function ignoring its parameter (#53), a missing authorship check on note edits (#54), ExampleHistory storing the wrong body (#55), and a key typo in index definitions (#56).
- These bugs are independent of the entity model work and affect production behavior.

### Decision
- File each bug as a standalone GitHub issue with Father Watson framing, and cross-reference them in the entity model document via inline ⚠ markers with an issue index table.

### Rationale
- Standalone issues are independently triageable, assignable, and closeable — embedding them in the entity model document would bury them.
- Inline ⚠ markers in the entity model preserve the connection between the finding and the evidence without coupling the documents' lifecycles.
- The Father Watson format matches the repo's issue-writing convention (`docs/ai/issue-writing-guide.md`).

### Alternatives Considered
- Note bugs inline in the entity model only — they'd be invisible to anyone not reading that specific document, and not triageable via GitHub's issue workflow.
- File bugs without linking to the entity model — loses the provenance of how the bugs were discovered.

### Impacts and Risks
- Four new issues (#53–#56) added to the backlog.
- Risk: marker notation (⚠ #N) is unfamiliar. Mitigation: legend table at the top of the entity model document defines the notation.

### Links
- [Issue #53](https://github.com/nubank/clojuredocs/issues/53) — `add-indexes-to-coll!` ignores collection parameter
- [Issue #54](https://github.com/nubank/clojuredocs/issues/54) — `patch-note-handler` missing authorship check
- [Issue #55](https://github.com/nubank/clojuredocs/issues/55) — ExampleHistory stores new body instead of previous body
- [Issue #56](https://github.com/nubank/clojuredocs/issues/56) — `:migraion-key` typo

---

## 2026-06-01 — Cross-reference issues between repos via GitHub comments

### Status
Decided

### Context
- nubank/clojuredocs is a fork of zk/clojuredocs; both have open issue backlogs with overlapping concerns.
- Issue #23 explicitly asks to scan the old repo for issues worthy of migration.
- The 4 new bugs and the entity model findings relate to existing issues in both repos (e.g., nubank#4, nubank#5, zk#226).

### Decision
- Add GitHub comments on existing issues linking them to related new issues and entity model findings, rather than maintaining a separate cross-reference document.

### Rationale
- GitHub comments appear in the issue's timeline — anyone triaging an issue sees the connections without consulting a separate document.
- Comments are durable and searchable via GitHub's interface.
- A separate cross-reference document would go stale and require manual maintenance.

### Alternatives Considered
- Maintain a cross-reference table in a markdown file — requires manual updates and is disconnected from the issue workflow.
- Add GitHub issue labels for cross-referencing — labels are too coarse to express specific relationships between issues.

### Impacts and Risks
- Comments are permanent; incorrect cross-references cannot be deleted without repo admin access.
- Risk: comment noise on old issues. Mitigation: comments are concise and only added where the connection is substantive.

### Links
- [Issue #23](https://github.com/nubank/clojuredocs/issues/23) — scan old repo for issues worthy of migration
- Comments added to nubank/clojuredocs #4, #5, #43, #54 linking to entity model findings and related issues

---

## 2026-05-11 — Schedule export JSON regeneration in-process

### Status
Decided

### Context
- `clojuredocs-export.json` is consumed by editor plugins (Calva, CIDER, etc.) to show usage examples inline. It was only regenerated manually via `lein run -m clojuredocs.export`.
- The file went stale — vars like `halt-when` had `null` examples in the JSON despite having examples on the website. A community member reported the issue in Slack.
- Issue #38 was created to automate regeneration.

### Decision
- Use `java.util.concurrent.ScheduledExecutorService` in `start-app` to run `export/run-export` every 6 hours, starting immediately on server boot.

### Rationale
- In-process scheduling reuses the existing DB connection and data access layer — no cold-start JVM, no separate env config.
- Zero new dependencies; `ScheduledExecutorService` is a JVM primitive.
- Initial delay of 0 means every deploy immediately refreshes the export.
- Scales through the planned redesign: as the data model changes (multi-library, dialect metadata, quality signals), the export function evolves with it and the scheduler is indifferent.

### Alternatives Considered
- Cron job on the server (`lein run -m clojuredocs.export`) — spins up a separate JVM each time, requires MongoDB URL in cron environment, loses access to shared caches and connection pools.
- External scheduler (e.g., systemd timer) — adds infrastructure dependency and deployment complexity for a simple periodic task.

### Impacts and Risks
- Export runs on a dedicated single-thread executor, so at most one export runs at a time.
- Risk: a long-running export could overlap with the next scheduled run. Mitigation: `scheduleAtFixedRate` skips if the previous run hasn't finished; export currently takes seconds.
- Risk: export failure kills the scheduler thread. Mitigation: `run-scheduled-export` wraps the call in try/catch.

### Links
- [Issue #38](https://github.com/nubank/clojuredocs/issues/38)
- [src/clj/clojuredocs/export.clj](src/clj/clojuredocs/export.clj)
- [src/clj/clojuredocs/main.clj](src/clj/clojuredocs/main.clj)

---

## 2026-04-28 — Defer jank dialect until stable var enumeration exists

### Status
Decided

### Context
- jank is a native Clojure dialect on LLVM with C++ interop, currently in alpha.
- Nubank sponsors the jank project.
- jank has a WIP nREPL server PR (#698) from 2 months ago, suggesting growing introspection capabilities — though the feature may not be complete.
- The `jank-lang/clojure-test-suite` repo already tests 5 dialects (Clojure, ClojureScript, babashka, ClojureCLR, Basilisp), but jank can't run `clojure.test` yet.
- The current dialect badge architecture requires a data source function (~10 lines), `cond->` clauses in `build-compat-map` (~4 lines), one `dialect-info` entry, a special forms set, and a logo file.

### Decision
- Do not add jank as a dialect in v1; revisit when jank supports `ns-publics` or provides a stable var enumeration mechanism.

### Rationale
- jank is pre-1.0 and its var surface is actively evolving (the project has daily commits).
- The test suite is a proxy for coverage ("tested") not API surface ("supported"), which is a meaningful distinction for user-facing badges.
- The architecture is additive — adding jank later requires no breaking changes to data format, rendering, or CSS.
- Worth a conversation with Jeaye (jank author) about a blessed way to enumerate supported vars before building a scraper.

### Alternatives Considered
- Scrape `jank-lang/clojure-test-suite` directory listing to infer supported vars — depends on directory naming conventions and file structure, which are not a stable API; conflates "tested" with "supported."
- Add jank now with frequent regeneration — high maintenance burden for unstable data; could mislead users.

### Impacts and Risks
- jank users won't see compatibility badges on ClojureDocs.
- Risk: none. Adding a fourth dialect requires changes to ~4 locations in the codebase, assuming a working var enumeration mechanism exists.

### Links
- [jank-lang/jank](https://github.com/jank-lang/jank)
- [jank-lang/clojure-test-suite](https://github.com/jank-lang/clojure-test-suite)
- [jank nREPL PR #698](https://github.com/jank-lang/jank/pull/698)
- [2026-04-14 — Three initial dialects](docs/decisions.md)

---

## 2026-04-28 — Verify dialect compat via rich comment blocks, not unit tests

### Status
Decided

### Context
- After implementing dialect badges, needed to capture REPL verification results as a repeatable artifact.
- The project has no real test suite — only a placeholder test (`core_test.clj` with `(is (= 1 1))`).
- PR #27 explores REPL-verified code review as a demo topic, with a tentative presentation date of June 11, 2026. `cognitect-labs/transcriptor` was flagged as relevant prior art.

### Decision
- Verify dialect compatibility lookups via inline rich comment blocks in `search/compat.clj`, not unit tests.

### Rationale
- Rich comment blocks are evaluable at the REPL during development — no test runner needed, immediate feedback.
- They serve as living documentation: expected results are inline comments next to each form.
- Adding a unit test to a project with zero real test coverage sets a convention nobody is following yet. RCFs match the project's current maturity.
- The RCF in `compat.clj` doubles as a concrete artifact for the PR #27 REPL-verified code review demo.

### Alternatives Considered
- Unit tests in `core_test.clj` — establishes a convention the project doesn't follow; requires a test runner; results are less visible during development.
- No verification artifact — REPL results are ephemeral; not repeatable by other contributors.

### Impacts and Risks
- RCFs are not run in CI — regressions won't be caught automatically.
- Risk: someone changes `dialect-compat.edn` without evaluating the RCF. Mitigation: low probability; the EDN is script-generated, not hand-edited.

### Links
- [search/compat.clj RCF](src/clj/clojuredocs/search/compat.clj)
- [PR #27 — REPL-verified code review](https://github.com/nubank/clojuredocs/pull/27)
- [cognitect-labs/transcriptor](https://github.com/cognitect-labs/transcriptor)

---

## 2026-04-28 — Hardcode special forms dialect support

### Status
Decided

### Context
- ClojureDocs tracks 15 special forms in `search/static.clj`: `def`, `if`, `do`, `quote`, `var`, `recur`, `throw`, `try`, `catch`, `finally`, `.`, `set!`, `monitor-enter`, `monitor-exit`, `new`.
- Special forms are compiler built-ins — they don't appear in `ns-publics` output, so the generation script's programmatic extraction (CLJS analyzer, bb `ns-publics`) won't capture them.
- Need to decide how to include special forms in the compatibility data.

### Decision
- Hardcode which special forms each dialect supports in the generation script.
- All 15 special forms are supported in Clojure/JVM by definition.
- CLJS supports: `def`, `if`, `do`, `quote`, `var`, `recur`, `throw`, `try`, `catch`, `finally`, `.`, `set!`, `new` (13 of 15). `monitor-enter` and `monitor-exit` are JVM threading primitives with no JS equivalent.
- bb supports: `def`, `if`, `do`, `quote`, `var`, `recur`, `throw`, `try`, `catch`, `finally`, `.`, `set!`, `monitor-enter`, `monitor-exit`, `new` (all 15). bb runs on the JVM via SCI and supports the full set.

### Rationale
- Hardcoding is appropriate because the set of special forms changes extremely rarely (last change was `clojure.core/import*` in Clojure 1.0) and there are only 15 entries.
- Programmatic detection is not feasible — special forms are not vars and don't appear in `ns-publics` or analyzer `:defs`.
- The alternative of excluding special forms would leave gaps on some of the most-visited var pages (`if`, `def`, `do`, etc.).

### Alternatives Considered
- Exclude special forms from compat data entirely — leaves high-traffic pages without badges.
- Detect programmatically — not feasible; special forms aren't in `ns-publics`.

### Impacts and Risks
- If a future Clojure version adds or removes a special form, the hardcoded list needs manual update.
- Risk: very low. Special forms are the most stable part of the language.

### Links
- [search/static.clj special-forms](src/clj/clojuredocs/search/static.clj#L43)
- [Issue #30](https://github.com/nubank/clojuredocs/issues/30)

---

## 2026-04-28 — Standalone script for dialect data generation

### Status
Decided

### Context
- The generation script (`tools/gen_dialect_compat.clj`) needs JVM for the CLJS analyzer and shells out to `bb`.
- Need to decide how to invoke it: Leiningen task, lein-exec plugin, or standalone manual execution.
- The current site will be redesigned per the 2026 Vision — infrastructure choices should be portable.

### Decision
- Keep the script as a standalone Clojure file loaded and invoked manually from a REPL (`lein repl`, then `(load-file "tools/gen_dialect_compat.clj")`).
- Add a `tools/README.md` with instructions for running the script.

### Rationale
- Simplest and most composable approach — no build tool plugins, no source path changes.
- Portable: when the site is redesigned, the script can be moved to the new project without Leiningen coupling.
- Matches the existing `tools/` convention — `dev_export.clj`, `sanity_check.clj`, and `top_contribs.clj` are all standalone files.
- The script runs rarely (only when dialect versions change), so ergonomic automation is not yet justified.

### Alternatives Considered
- `lein run -m tools.gen-dialect-compat` — requires adding `tools/` to Leiningen source paths; couples script to build config.
- `lein exec tools/gen_dialect_compat.clj` — requires `lein-exec` plugin (not in `project.clj`); adds a dependency for rare use.

### Impacts and Risks
- Slightly more manual than a one-liner, but documented in `tools/README.md`.
- Risk: someone forgets how to run it. Mitigation: README with exact commands.

### Links
- [Issue #30](https://github.com/nubank/clojuredocs/issues/30)
- [tools/ directory convention](tools/)

---

## 2026-04-28 — Use EDN for dialect compatibility data file

### Status
Decided

### Context
- Need a format for the static compatibility index mapping 700 qualified var names to their supported dialects.
- The file is checked into the repo, read once at startup, never written by the app.
- Codebase uses EDN for figwheel/ClojureScript build configuration (`dev.cljs.edn`, `prod.cljs.edn`).

### Decision
- Use EDN as the data file format for `dialect-compat.edn`.
- Key format: qualified string (`"clojure.core/map"`) mapping to a set of dialect keywords (`#{:clj :cljs :bb}`).
- Example: `{"clojure.core/map" #{:clj :cljs :bb}, "clojure.core/gen-class" #{:clj}}`.

### Rationale
- Idiomatic to Clojure — readable by all three target dialects (JVM, CLJS, bb).
- No additional dependencies needed; `clojure.edn/read-string` is in core.
- JSON — already available via Cheshire (`project.clj`), but not idiomatic for Clojure config and lacks reader literals for symbols and keywords. Database would require schema changes (ruled out by the data model coupling audit).

### Alternatives Considered
- JSON — already available via Cheshire (`project.clj`), but not idiomatic for Clojure config, and lacks reader literals for symbols and keywords.
- Database (MongoDB) — ruled out by the data model coupling audit constraint of no schema changes; also overkill for static read-only data.

### Impacts and Risks
- EDN file must be regenerated when dialect versions change.
- Risk: stale data if regeneration is forgotten. Mitigation: version-pin dialect versions in the file header and document regeneration steps.

### Links
- [Issue #30](https://github.com/nubank/clojuredocs/issues/30)
- [Data Model Coupling Audit](docs/research/data-model-coupling-audit.md)
- [Planning doc, Part 3](docs/research/issue-30-dialect-compat-planning.md)

---

## 2026-04-28 — Script-generate dialect compatibility data

### Status
Decided

### Context
- The compatibility index covers 700 vars across 3 dialects — too many to maintain by hand.
- Data sources are programmatic: JVM `ns-publics`, CLJS `cljs.analyzer.api`, bb `ns-publics` via shell.

### Decision
- Write a Clojure script at `tools/gen_dialect_compat.clj` that generates `resources/dialect-compat.edn`.

### Rationale
- All three data sources are already proven programmatically accessible (verified 2026-04-21).
- Checked-in output means the app never needs CLJS or bb at startup.
- `tools/` directory matches existing repo convention (`tools/dev_export.clj`, `tools/sanity_check.clj`).

### Alternatives Considered
- Manual curation — error-prone at 700 entries, would fall out of date silently.
- Hybrid (script for initial generation, manual overrides) — unnecessary complexity; no known cases where manual override is needed.

### Impacts and Risks
- Requires `org.clojure/clojurescript` and `bb` to be available when regenerating.
- Risk: script output could diverge from live runtime if dialect versions drift. Mitigation: record exact versions in the EDN file header.

### Links
- [Issue #30](https://github.com/nubank/clojuredocs/issues/30)
- [Planning doc, Part 1 methodology note](docs/research/issue-30-dialect-compat-planning.md)

---

## 2026-04-28 — Load compatibility data at startup via def

### Status
Decided

### Context
- Need a way for `pages/vars.clj` to look up dialect support for a var at render time.
- Existing pattern: `search/clojure-lib` is a `def` computed at startup. Both are top-level defs evaluated at load time, but `clojure-lib` introspects live JVM namespaces while the compatibility index reads a static file.

### Decision
- Add a `def` (in `search/compat.clj` or similar) that reads the EDN file at startup and exposes a `dialect-support` lookup function.

### Rationale
- Follows the established `search.clj` pattern — a top-level def evaluated at load time.
- O(1) lookup per var via a hash map, no per-request I/O.

### Alternatives Considered
- On-demand `slurp` per request — wasteful I/O on every var page load.
- Config library (e.g., `aero`, `cprop`) — unnecessary new dependency for a single static file.

### Impacts and Risks
- Data is immutable for the lifetime of the JVM process — requires restart to pick up new data.
- Risk: none beyond existing pattern. `clojure-lib` already works this way.

### Links
- [search.clj L102-L108](src/clj/clojuredocs/search.clj#L102-L108) — existing pattern

---

## 2026-04-28 — Render dialect badges in $var-header

### Status
Decided

### Context
- Need to show which dialects support a var on its page.
- The `$var-header` function in `pages/vars.clj` already renders var name, namespace link, "Available since", and source link in a `.var-meta` div.

### Decision
- Render dialect icons as small inline elements inside `.var-meta` in `$var-header`.
- Use dialect logos (Clojure, ClojureScript, babashka) as small icons, ordered `clj` `cljs` `bb`.
- For in-scope vars: always show all three icons. Supported dialects are full opacity; unsupported dialects are dimmed. This communicates "we checked, it's absent" rather than leaving the user guessing.
- For out-of-scope vars (no compat data): show nothing (see "Omit badges for unknown" decision).

### Rationale
- Dialect support is var identity metadata — it belongs next to "Available since" and the namespace link, not in a separate section.
- Showing all three icons (with dimming) is more informative than omitting unsupported ones — users can see at a glance which dialects were checked and which support the var.
- Small logos are visually compact and recognizable to the Clojure community.

### Alternatives Considered
- Below arglists — too far from the var name; users scanning quickly would miss them.
- Sidebar — separated from var identity; sidebar is already used for namespace navigation.
- Omit unsupported icons — less informative; users can't distinguish "not supported" from "not checked."
- Text labels (`clj`, `cljs`, `bb`) instead of icons — functional but less visually appealing.

### Impacts and Risks
- Adds CSS for `.dialect-badge` styles (new Garden rules in `css.clj`).
- Need to source or create small dialect logo assets.
- Risk: visual clutter on var pages. Mitigation: icons are small, and dimmed icons have low visual weight.

### Links
- [pages/vars.clj $var-header](src/clj/clojuredocs/pages/vars.clj)
- [Feature proposal #4](docs/feature-proposals-q2-2026.md)

---

## 2026-04-28 — Omit badges for unknown dialect support

### Status
Decided

### Context
- Vars outside `clojure.core` and `clojure.string` (e.g., `core.async`, `core.logic`) have no dialect compatibility data yet.
- Need to decide what to show when the lookup returns `nil`.

### Decision
- Show nothing — no badges rendered when dialect support is unknown.

### Rationale
- Absence of badges honestly communicates "we haven't checked" without cluttering the UI.
- No risk of users misinterpreting a "?" badge as partial support.
- Clean: 700 vars get badges, ~100 vars (from other namespaces) don't.

### Alternatives Considered
- Show "?" badge — adds visual noise, could be confusing ("is it supported or not?").
- Show "unknown" label — takes significant horizontal space, no actionable information for the user.

### Impacts and Risks
- Users may not realize dialect info exists if they only visit out-of-scope var pages.
- Risk: low. The feature is most valuable for `clojure.core` and `clojure.string` vars, which are the most visited.

### Links
- [Issue #30](https://github.com/nubank/clojuredocs/issues/30)

---

## 2026-04-21 — Use CLJS compiler analyzer over API docs

### Status
Decided

### Context
- Needed an authoritative source for which `clojure.core` vars exist in ClojureScript.
- Initial approach consulted the CLJS API docs at cljs.github.io, which reported 980 vars.
- Discovery: the API docs page lists only `:defs` — it misses 191 macros imported from `cljs/core.clj`.

### Decision
- Use `cljs.analyzer.api/analyze-file` on the CLJS compiler source to extract both `:defs` and `:macros`.

### Rationale
- The compiler's analyzer state is the canonical source of what `cljs.core` provides — 1090 vars vs 980 from the API docs.
- Programmatic extraction is reproducible and version-pinned (tested with `org.clojure/clojurescript 1.12.134`).
- The 10% undercounting from API docs alone would produce incorrect compatibility badges.

### Alternatives Considered
- CLJS API docs (cljs.github.io) — misses macros; 980 vs 1090 vars (see errata #10 in planning doc).
- CLJS cheatsheet (cljs.info) — curated subset, not comprehensive, not machine-readable.
- Manual review of `cljs/core.cljs` source — would miss macros defined in `cljs/core.clj`.

### Impacts and Risks
- Requires `org.clojure/clojurescript` as a dependency in the generation script.
- Risk: analyzer API could change between CLJS versions. Mitigation: pin to specific CLJS version in the script.

### Links
- [Planning doc §1.1 methodology note](docs/research/issue-30-dialect-compat-planning.md)

---

## 2026-04-21 — Use bb ns-publics as babashka data source

### Status
Decided

### Context
- Needed an authoritative source for which `clojure.core` vars babashka supports.
- babashka documentation (book.babashka.org) does not maintain a var-by-var compatibility list.
- No machine-readable var list found in the babashka repository or documentation.

### Decision
- Use `bb -e '(keys (ns-publics (quote clojure.core)))'` on the installed bb binary as the source of truth.

### Rationale
- `ns-publics` reflects exactly what the running bb binary provides — 641 core + 21 string vars.
- Programmatic, reproducible, version-pinned (tested with bb 1.12.215).

### Alternatives Considered
- babashka documentation — no var-by-var listing exists.
- babashka source / var registry on GitHub — not consulted directly; `ns-publics` is simpler and authoritative.

### Impacts and Risks
- Requires bb to be installed when regenerating compatibility data.
- Risk: bb version drift means data goes stale. Mitigation: record bb version in generated file; regenerate with each bb release (~every 3 weeks based on observed cadence of 12 releases between Aug 2025 and Apr 2026).

### Links
- [Planning doc §1.2](docs/research/issue-30-dialect-compat-planning.md)
- [bb releases](https://github.com/babashka/babashka/releases)

---

## 2026-04-21 — Binary present/absent model for v1

### Status
Decided

### Context
- Some vars exist across dialects but may behave differently (e.g., `locking` in CLJS is a macro but JS has no threads; `pmap` in bb may not parallelize under SCI).
- Need to decide granularity: binary (yes/no), ternary (yes/partial/no), or richer.

### Decision
- Use binary present/absent for v1 — a var is either supported or not in a dialect.

### Rationale
- Binary is simplest to generate (var name lookup in `ns-publics` output), render (badge or no badge), and understand.
- Behavioral differences are known to affect at least 2 of 700 vars (`locking`, `pmap`) and have not been systematically cataloged (out of scope for v1).
- The data model (a set of dialect keywords per var) leaves room to add a future "partial" state without breaking anything.

### Alternatives Considered
- Ternary (supported/partial/unsupported) — requires defining "partial" per-var, which is subjective and labor-intensive.
- Rich behavioral metadata — out of scope; would require per-var documentation effort disproportionate to the feature's value.

### Impacts and Risks
- Some vars will show as "supported" when they behave differently than on JVM (e.g., `locking` in CLJS).
- Risk: users assume behavioral equivalence. Mitigation: documented as a known limitation in the planning doc; can add "partial" state later.

### Links
- [Planning doc, Risks #2](docs/research/issue-30-dialect-compat-planning.md)

---

## 2026-04-14 — Scope to clojure.core and clojure.string only

### Status
Decided

### Context
- ClojureDocs tracks 38 namespaces from `clojure-namespaces` in `search/static.clj`, spanning the core Clojure library and separate libraries (core.async, core.logic, data.csv, tools.build).
- Need to ship by end of April 2026 to gather learnings before a full site redesign informed by the 2026 Vision.
- Cross-dialect comparison is most meaningful for the standard library namespaces shared across dialects.

### Decision
- Scope the v1 compatibility index to `clojure.core` (679 vars) and `clojure.string` (21 vars) only — 700 vars total.

### Rationale
- These two namespaces contain the foundational standard library functions and are expected to be the most visited on ClojureDocs.
- End-of-April deadline: implementing for 700 vars is achievable; expanding to 38 namespaces requires researching each library's cross-dialect status individually.
- Learnings from this implementation will inform the broader site redesign described in the 2026 Vision.

### Alternatives Considered
- All 38 namespaces — would require researching cross-dialect support for core.async, core.logic, etc., which have different availability stories. Not achievable by end of April.
- Only `clojure.core` — misses `clojure.string`, which is trivial to include (identical in bb, 20/21 in CLJS).

### Impacts and Risks
- Vars from `core.async`, `core.logic`, etc. will have no dialect badges.
- Risk: users expect all namespaces to have badges. Mitigation: "omit badges for unknown" decision means graceful degradation — no badges is better than wrong badges.

### Links
- [2026 Vision](docs/2026vison.md) — site redesign context
- [Planning doc, Part 2 risk #4](docs/research/issue-30-dialect-compat-planning.md)
- [30-dialect-prompt.md](docs/research/30-dialect-prompt.md) — "April 27 deploy target"

---

## 2026-04-14 — Three initial dialects: Clojure/JVM, ClojureScript, babashka

### Status
Decided

### Context
- The 2026 Vision names four dialects: Clojure, ClojureScript, babashka, jank. This experiment does not include jank.
- jank has a [clojure-test-suite](https://github.com/jank-lang/clojure-test-suite) repo but is not yet stable enough for production compatibility data.
- Feature proposal #4 in `feature-proposals-q2-2026.md` specifies badges for dialect compatibility.

### Decision
- Start with three dialects: Clojure/JVM (baseline), ClojureScript, and babashka.

### Rationale
- These three have stable releases and programmatic data sources (verified 2026-04-21).
- jank's test suite is a future data source but the language is pre-1.0.
- The data model (set of keyword tags per var) naturally extends to additional dialects.

### Alternatives Considered
- Include jank — premature; no stable release, test suite is incomplete.
- Only Clojure/JVM + ClojureScript — misses babashka, which has 94.6% coverage and a large user base.

### Impacts and Risks
- jank users won't see compatibility info.
- Risk: none. Adding a dialect later is additive — new keyword in the set, regenerate the data file.

### Links
- [2026 Vision, cross-dialect section](docs/2026vison.md)
- [jank-lang/clojure-test-suite](https://github.com/jank-lang/clojure-test-suite)
- [Feature proposal #4](docs/feature-proposals-q2-2026.md)
