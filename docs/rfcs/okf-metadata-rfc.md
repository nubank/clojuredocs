---
type: RFC
title: OKF + RDF-aligned document metadata convention
description: Replace the blockquote metadata block with OKF YAML frontmatter whose semantics align to Dublin Core Terms and PROV-O.
tags: [rfc, ai-metadata, okf, rdf, dublin-core, prov-o, conventions]
created: 2026-06-16
modified: 2026-06-16
source: https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/ee67a5ca27044ebe7c38385f5b6cffc2305a9c1a/okf/SPEC.md
ai_assisted: "Claude Opus 4.8 via Claude Code"
tools: [GitHub MCP, WebFetch, workspace files]
agents_skills: []
review_maturity: L2
review_note: human-directed; OKF/RDF claims sourced to primary specs; pending PR review
---

# RFC: OKF + RDF-aligned document metadata convention

> **Caveat:** AI-drafted from human direction. The framework claims — the [Open Knowledge Format (OKF)](#sources), [Dublin Core Terms](#sources), and the W3C Provenance Ontology ([PROV-O](#sources)) — are sourced to the primary specifications listed in [Sources](#sources), each fetched June 2026 (the OKF spec link is pinned to a commit). Verify against those before treating any mapping as authoritative.

## Context

Prose documents under `docs/` carry AI provenance and review metadata. The current convention
([CLAUDE.md](../../CLAUDE.md) → "AI metadata on documents") encodes that metadata as a Markdown
**blockquote** (`> **Document metadata**` + bold-label bullets). This RFC proposes replacing the blockquote
with **YAML frontmatter** that is conformant with the [Open Knowledge Format (OKF) v0.1](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/ee67a5ca27044ebe7c38385f5b6cffc2305a9c1a/okf/SPEC.md)
and whose field *semantics* align with two established RDF vocabularies —
[Dublin Core Terms](https://www.dublincore.org/specifications/dublin-core/dcmi-terms/) and
[W3C PROV-O](https://www.w3.org/TR/prov-o/).

This is the **Library** rung of the reliability ratchet (LLM → REPL → Library → Enforcement): the convention
becomes a parseable, shared format. A `bb` validator (the **Enforcement** rung) is a deliberate follow-up.

## Problem

The blockquote is human-readable but **not machine-parseable**. Comparing it to OKF v0.1 surfaces concrete
deviations:

1. **Frontmatter format — hard conflict.** OKF *mandates* a YAML frontmatter block delimited by `---`
   (OKF §4.1). A blockquote yields **no frontmatter**, so a conformant OKF consumer treats the document as
   having none — failing conformance (OKF §9.1).
2. **Required `type` — absent.** OKF requires a non-empty `type` field (OKF §9.2) — effectively its one
   mandatory field; Claude found no other hard requirement in the spec. The blockquote has no `type`.
3. **Field-name divergence.** Only `tags` overlaps OKF, and as prose (`Tags: a, b`) rather than a YAML list.
4. **Review-trust is not queryable.** The L0–L4 maturity level, section `<!-- reviewed -->` markers, and
   `[unverified]` flags live in prose and HTML comments — invisible to any structured consumer.

What the current conventions already get **right** (and this RFC keeps): metadata stored as code in version
control (OKF §10, "metadata as code"); structural Markdown over freeform prose (OKF §4.2); cross-links as a
graph (OKF §5); a permissive, progressive-trust posture (OKF §9) — our L0–L4 ladder is our own, stricter
extension of that idea, not part of OKF; and dated change history (OKF's `log.md` and our per-doc Version
History tables both serve this end).

## Proposal

### Frontmatter

```yaml
---
type: RFC                              # REQUIRED (OKF) — dcterms:type
title: OKF + RDF-aligned document metadata convention   # dcterms:title
description: One-line summary used by indexes, search, and previews.   # dcterms:description
tags: [rfc, ai-metadata, okf, rdf]     # dcterms:subject
created: 2026-06-16                    # dcterms:created
modified: 2026-06-16                   # dcterms:modified
source: https://github.com/nubank/clojuredocs/issues/43   # dcterms:source (when one exists)
ai_assisted: "Claude Opus 4.8 via Claude Code"            # prov:wasAttributedTo a prov:SoftwareAgent
session: <session-id>                  # identifies the prov:Activity
tools: [GitHub MCP, workspace files]   # prov:used
agents_skills: []                      # permalinks to agent/skill definitions applied
review_maturity: L2                    # machine-readable L0–L4
review_note: human-reviewed via PR
---
```

**Required:** `type`. **Recommended:** `title`, `description`, `tags`, `created`, `modified`.
**Provenance (when AI-assisted):** `ai_assisted`, `session`, `tools`, `agents_skills`.
**Review trust:** `review_maturity`, `review_note`. Consumers tolerate unknown keys and unknown `type`
values (OKF §9).

### `type` taxonomy

`Reference`, `Guide`, `RFC`, `Decision Log`, `Errata`, `Data Model`, `Diagram`, `Research`, `Review`,
`Vision`. Descriptive and extensible. This is *our* controlled vocabulary, supplied as the value of the
`dcterms:type` slot — it is not DCMI's recommended Type Vocabulary (`Text`, `Dataset`, `Software`, …).

### RDF alignment — additive, never required

The **required surface stays pure OKF** (`type` + YAML). Field names map to well-known RDF terms so the same
frontmatter is liftable to triples; nothing forces a consumer to care.

| Frontmatter key | RDF term | Meaning |
|---|---|---|
| `type` | `dcterms:type` | Nature/genre of the document (also OKF's required field) |
| `title` | `dcterms:title` | Name |
| `description` | `dcterms:description` | One-line account |
| `tags` | `dcterms:subject` | Topics |
| `created` | `dcterms:created` | Date of creation |
| `modified` | `dcterms:modified` | Date of last meaningful change |
| `source` | `dcterms:source` | Related resource the document is based on. (Use `prov:wasDerivedFrom` only when there is a true derivation, not a mere reference.) |
| `ai_assisted` | `prov:wasAttributedTo` a `prov:SoftwareAgent` | Model/interface that generated the draft |
| `tools` | `prov:used` | What the generating activity used |
| `review_maturity` / `review_note` | extension (no native OKF/RDF equivalent) | Human review trust; at L4 the human owner can be recorded as `dcterms:creator` / a `prov:Person` the entity is `prov:wasAttributedTo` |

PROV-O provides terms that fit this case: the document is a `prov:Entity` that `prov:wasGeneratedBy` an
activity and `prov:wasAttributedTo` a `prov:SoftwareAgent` (the model) which `prov:actedOnBehalfOf` a
`prov:Person` (the human owner). The canonical mapping ships as an out-of-band JSON-LD context,
[`docs/context.jsonld`](../context.jsonld), so the document files stay plain YAML you can `cat`.

### What stays in the body

Section-level `<!-- reviewed: name, date — scope -->` markers, inline `[unverified]` flags, and scope/caveat
disclaimers remain in the body — OKF tolerates them; they are not structured fields.

### OKF bundle root

`docs/` becomes an OKF bundle: [`docs/index.md`](../index.md) carries `okf_version: "0.1"` (the only
frontmatter an index may have, OKF §11) and lists documents for progressive disclosure (OKF §6).

## Why RDF (Dublin Core + PROV-O)

- **Dublin Core Terms** is a long-established W3C-adjacent vocabulary for descriptive and lifecycle metadata;
  `dcterms:type` covers the same ground as OKF's required field, so alignment is nearly free.
- **PROV-O** is a W3C Recommendation providing an OWL ontology for provenance; its terms directly support
  "an entity generated by a software agent on behalf of a person" — precisely AI-assisted authorship.
- Keeping it **additive** (vocabulary + optional `@context`, not mandated IRIs/Turtle) respects OKF's
  deliberate minimalism — per the spec, *"If you can `cat` a file, you can read OKF; if you can `git clone`
  a repo, you can ship it."*

## Principles carried over

Generic, publicly-sourced principles reinforced by this convention: provenance is mandatory for AI-assisted
docs; citations should be reproducible (commit-pinned GitHub permalinks for code); git is the system of
record; structure beats prose; humans take ownership at L4. These echo OKF, DCMI, PROV-O,
[C2PA](https://c2pa.org/) content credentials, the Linux kernel `Reviewed-by` trailer, and Datomic's
immutable/transactional model.

## Alternatives considered

1. **Parse the blockquote with `bb` (regex/grammar).** Cheapest, zero doc changes — but brittle, non-standard,
   and not OKF-conformant. Rejected.
2. **Prefixed `dcterms:`/`prov:` keys directly in YAML.** Unambiguous RDF, but noisy and un-OKF-idiomatic.
   Bare keys + `docs/context.jsonld` yield the same triples with cleaner files. Rejected.
3. **Full RDF serialization (Turtle / JSON-LD documents).** Runs against OKF's minimalism and is heavyweight
   for a `docs/` tree. Rejected.
4. **Keep the blockquote.** That is the problem. Rejected.

## Migration

Full sweep of prose Markdown under `docs/` to frontmatter. On `master` that is the 13 current prose `.md`
files under `docs/` (excluding `.github/` templates and data files; count as of branch base `feb227c`); the
documents living on `feat/43` (entity model, errata, dataflows, the entity-model RFC) get frontmatter when
that branch lands. Out of scope: `.github/` templates (GitHub-mandated frontmatter), repo-root config, and
data files (`*.edn`, `*.csv` — not OKF concepts).

## Enforcement

Implemented: [`tools/validate_metadata.clj`](../../tools/validate_metadata.clj) (babashka) validates every
non-reserved `.md` under `docs/` (recursively) against [`docs/metadata-schema.edn`](../metadata-schema.edn) —
parseable frontmatter; non-empty `type` (warns if outside the taxonomy); `review_maturity ∈ {L0..L4}`;
`created`/`modified` are valid `YYYY-MM-DD` dates (validated against the raw text, since clj-yaml leniently
rolls bad dates over); and that every known frontmatter key except `okf_version` has a JSON-LD mapping in
[`docs/context.jsonld`](../context.jsonld). One validator, three surfaces: a git pre-commit hook
([`.githooks/pre-commit`](../../.githooks/pre-commit), enabled via [`bin/install-hooks`](../../bin/install-hooks)),
`lein test` ([`clojuredocs.metadata-test`](../../test/clojuredocs/metadata_test.clj)), and CI
([`.github/workflows/docs-metadata.yml`](../../.github/workflows/docs-metadata.yml)). This completes the
**Enforcement** rung of the reliability ratchet (LLM → REPL → Library → Enforcement) — concretely,
`vibes+prose → sidecar.clj → schema.edn → clojure.test`.

## Open questions

- Should `review_maturity` gain an explicit human-owner field (`reviewed_by`) to fully realize the PROV-O
  `prov:Person` attribution at L4? Deferred to the enforcement PR.
- Whether to generate `docs/index.md` from frontmatter automatically (OKF §6 permits it). Deferred.

> Review logs: [run 1](okf-metadata-rfc_research-review_run_1.md) (initial claims/link audit) · [run 2](okf-metadata-rfc_research-review_run_2.md) (post-enforcement audit) — fixes applied, items deferred.

## Version history

| Date | Change |
|---|---|
| 2026-06-16 | Initial RFC: propose OKF YAML frontmatter with Dublin Core + PROV-O semantics; supersede the blockquote block. |
| 2026-06-16 | Enforcement implemented: `bb` validator + `metadata-schema.edn`, wired into a pre-commit hook, `lein test`, and CI. |

## Sources

**Specifications & code (primary)**

| Source | Relevance | Quality |
|---|---|---|
| [OKF v0.1 spec](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/ee67a5ca27044ebe7c38385f5b6cffc2305a9c1a/okf/SPEC.md) | Critical — the format this RFC conforms to | Excellent (primary spec); commit-pinned |
| [knowledge-catalog repo](https://github.com/GoogleCloudPlatform/knowledge-catalog/tree/2d0bb3f547b847fcbd1c7611bdab8a9e2ccb098f) | High — reference implementation and samples | Good; commit-pinned |

**Public references**

| Source | Relevance | Quality |
|---|---|---|
| [DCMI Metadata Terms](https://www.dublincore.org/specifications/dublin-core/dcmi-terms/) | Critical — `dcterms:` field semantics | Excellent (W3C-adjacent standard) |
| [W3C PROV-O](https://www.w3.org/TR/prov-o/) | Critical — provenance model for AI authorship | Excellent (W3C Recommendation) |
| [OKF announcement blog](https://cloud.google.com/blog/products/data-analytics/how-the-open-knowledge-format-can-improve-data-sharing/) | Medium — motivation and framing | Good (vendor blog; defer to SPEC) |
| [C2PA](https://c2pa.org/) | Medium — progressive-trust analogy for L0–L4 | Good |
| [Linux kernel submitting-patches (`Reviewed-by`)](https://docs.kernel.org/process/submitting-patches.html) | Medium — review-trailer convention | Excellent (primary) |

**Not accessed / out of scope**

| Source | Reason |
|---|---|
| Internal Nubank context-engineering conventions | Intentionally excluded — this is a public repository; internal conventions are tracked separately. |
