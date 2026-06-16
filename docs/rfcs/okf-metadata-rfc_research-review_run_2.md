---
type: Review
title: "Research-review run 2 — OKF metadata RFC (enforcement)"
description: Second claims/link audit after enforcement was implemented; fixes applied.
tags: [review, research-review, okf, enforcement]
created: 2026-06-16
modified: 2026-06-16
source: okf-metadata-rfc.md
ai_assisted: "Claude Opus 4.8 via Claude Code"
agents_skills: [claims-auditor, link-auditor]
review_maturity: L1
review_note: AI-generated review log — verify before actioning deferred items.
---

# Research-review run 2 — OKF metadata RFC (enforcement)

Second `/research-review` of [okf-metadata-rfc.md](okf-metadata-rfc.md), run after the `## Enforcement`
section changed from "follow-up" to "implemented". Action codes: **FIXED** · **DEFERRED** · **OK**.

Both auditors **verified the enforcement claim is true**: `tools/validate_metadata.clj`,
`docs/metadata-schema.edn`, `.githooks/pre-commit`, `bin/install-hooks`, `test/clojuredocs/metadata_test.clj`,
and `.github/workflows/docs-metadata.yml` all exist on the branch and wire together as described. Remaining
items were precision/links.

## Priority 1 — Factual concerns (claims-auditor)

- **FIXED (substantive)** — "ISO 8601 dates" overstated a shape-only regex. The validator now parses the raw
  authored `created`/`modified` text with `java.time.LocalDate`, rejecting impossible (`2026-13-45`),
  unpadded (`2026-6-6`), and non-date values. Validating the *parsed* value could not catch this — clj-yaml
  leniently rolls `2026-13-45` over to a real `Date`. The RFC now says "valid `YYYY-MM-DD` dates (validated
  against the raw text)".
- **FIXED** — "every schema key has a mapping in context.jsonld" → "every known frontmatter key except
  `okf_version`" (the validator checks `:known-keys` minus `:context-exempt`, not schema config keys).
- **FIXED (tone)** — Dropped "the ratchet's final rung"; aligned to the canonical ratchet
  (LLM → REPL → Library → Enforcement) and kept the concrete `vibes+prose → … → clojure.test` framing as an
  illustration, not a competing ladder.
- **FIXED (cosmetic)** — `docs/**.md` → "every `.md` under `docs/` (recursively)".
- **OK** — Migration count "13 … as of `feb227c`" is commit-scoped (acceptable). The L4/`type`/absence claims
  from run 1 remain correctly scoped.

## Priority 2 — Navigation concerns (link-auditor)

- **FIXED** — The four implemented artifacts cited as bare code are now relative links
  (`.githooks/pre-commit`, `bin/install-hooks`, `clojuredocs.metadata-test`, the CI workflow), matching how
  the sibling artifacts are linked. All five pre-existing relative links resolve.
- **DEFERRED (soft)** — Some link display text repeats the file path (e.g. `tools/validate_metadata.clj`).
  Judgment call: in an RFC about file conventions the exact paths are load-bearing, so left as-is.
- **OK** — `lein test` unlinked (well-known tool); PROV-O term cluster covered by the section's PROV-O link.

## Priority 3 / 4 — Sources & process

- **OK** — Bibliography present and grouped; Version History has the 2026-06-16 enforcement row. No Errata
  section (RFC, not a running research doc).

## Cross-cutting

- **OK** — Confidentiality: no internal-only mechanics or private-repo names (resolved in run 1).
