---
type: Review
title: "Research-review run 1 — OKF + RDF metadata RFC"
description: Prioritized claims/link audit of okf-metadata-rfc.md; fixes applied and items deferred.
tags: [review, research-review, okf, rdf]
created: 2026-06-16
modified: 2026-06-16
source: okf-metadata-rfc.md
ai_assisted: "Claude Opus 4.8 via Claude Code"
agents_skills: [claims-auditor, link-auditor]
review_maturity: L1
review_note: AI-generated review log — verify before actioning deferred items.
---

# Research-review run 1 — OKF + RDF metadata RFC

Review of [okf-metadata-rfc.md](okf-metadata-rfc.md) via the `/research-review` skill (claims-auditor +
link-auditor), 2026-06-16. Action codes: **FIXED** (applied to the RFC this run) · **DEFERRED** (left for a
human) · **N/A**.

## Priority 1 — Factual concerns (claims-auditor)

- **Auditor "root concern" that OKF may be hallucinated — RESOLVED, not a defect.** The claims-auditor's
  training cutoff predates OKF; it could not confirm the spec exists. OKF v0.1 was fetched from the live
  `GoogleCloudPlatform/knowledge-catalog` repo in June 2026 and the spec link is now commit-pinned. No action
  on the RFC beyond the pin.
- **FIXED** — Overstated absolutes softened: "single hard requirement" → "effectively its one mandatory
  field; Claude found no other"; "the W3C standard for provenance" → "a W3C Recommendation"; "the canonical
  way" / "widely-deployed" reworded.
- **FIXED** — `dcterms:type` misattribution: clarified the taxonomy is *our* controlled vocabulary supplied
  as the `dcterms:type` value, **not** DCMI's recommended Type Vocabulary.
- **FIXED** — "PROV-O models our exact case" → "provides terms that fit this case".
- **FIXED** — `source` row split from `prov:wasDerivedFrom` (different semantics); the "cat a file" line is now
  attributed as a direct spec quote.
- **DEFERRED** — Convert every inline `OKF §N` reference (§4.1, §4.2, §5, §6, §9, §9.1, §9.2, §10, §11) into an
  anchored permalink into the pinned `SPEC.md`, so each section claim is one click to verify. Numerous;
  mechanical but not done this run.

## Priority 2 — Navigation concerns (link-auditor)

- **FIXED** — OKF spec link and `knowledge-catalog` repo link commit-pinned (were `/blob/main`, `/tree/main`).
- **FIXED** — First-mention acronyms expanded/linked in the Caveat (OKF, Dublin Core Terms, PROV-O = W3C
  Provenance Ontology).
- **DEFERRED** — Optionally link each RDF term cell in the alignment table (`dcterms:*`, `prov:*`) to its
  canonical fragment. Judgment call: full linking adds noise; a representative link per vocabulary may suffice.
- **DEFERRED** — Minor display-text trims (`SPEC.md` → "spec"; `docs/context.jsonld` label). Low priority; the
  file paths are arguably load-bearing in an RFC about file conventions.
- **OK** — All relative links (`../../CLAUDE.md`, `../context.jsonld`, `../index.md`) resolve on this branch.
  `entity-model-rfc.md` is referenced only in prose (not a link), so it is not broken.

## Priority 3 — Source concerns

- **OK** — Annotated bibliography present, grouped (specs/code, public references, not-accessed), with
  relevance + quality assessments.

## Priority 4 — Process concerns

- **OK** — Version History present with a 2026-06-16 entry.
- **N/A** — No Errata section: this is an RFC, not a running research document; errata would attach to the
  superseded blockquote convention if errors surface later.

## Cross-cutting — Confidentiality (needs human decision)

- **RESOLVED** — A pre-existing line in
  [docs/research/data-model-coupling-audit.md](../research/data-model-coupling-audit.md) (Research Context)
  named a private internal repository as the origin of a "research-analyst" agent. This PR did not introduce
  it, but it was in the changed-file set; naming a private repo in a public doc is a confidentiality smell. It
  was rephrased to "adapted from an internal agent definition".
