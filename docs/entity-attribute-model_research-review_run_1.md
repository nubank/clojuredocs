> **Document metadata**
> - **Created:** 2026-06-16
> - **Last updated:** 2026-06-16
> - **Tags:** review, research-review, entity-model, issue-43
> - **AI-assisted:** Yes — Claude Opus 4.8 (1M context) via Claude Code
> - **Tools:** workspace files; claims-auditor + link-auditor subagents; git
> - **Review maturity:** L1 — human-directed review log, findings not yet actioned by a human
>
> _Review log produced by `/research-review` on [entity-attribute-model.md](entity-attribute-model.md), plus a cross-check of its claims against [entity-model-rfc.md](rfcs/entity-model-rfc.md). Straightforward fixes were applied in the same pass and are marked **FIXED**; the rest are deferred for human action. AI-generated — verify before actioning._

# Research-review run 1 — entity-attribute-model.md

Reviewed document: [entity-attribute-model.md](entity-attribute-model.md) (at commit `1483209`).
Cross-checked against: [entity-model-rfc.md](rfcs/entity-model-rfc.md), [errata.md](errata.md), [entity-attribute-model.edn](entity-attribute-model.edn).

## RFC cross-check (the headline finding)

**Dialect data — model doc contradicted the RFC. FIXED.**

- The Key Observations table described dialect data as *"Static EDN file, attribute of Var"* and said richer dialect metadata *"would likely require promoting this to its own entity"* — a hypothetical future step.
- RFC [§5 Open Question 2](rfcs/entity-model-rfc.md) marks this **Resolved**: the data lives in its own file (`dialect-compat.edn`), keyed by var, **not** on Var documents; *"No 'future promotion' step needed."*
- The [EDN](entity-attribute-model.edn) models `:dialect-compat` as a top-level entity (`:source "edn-file"`), and `entity_model_test.clj` guards it as one. The model doc's own metadata (lines 9, 11, 13, 73) already called `DialectCompat` a verified entity — so the table cell contradicted the rest of its own document.
- **Action taken:** rewrote the row; logged [errata #12](errata.md); added a 2026-06-16 version-history entry. No silent fix.

All other model-doc claims checked against the RFC are **consistent**: embedded-var shape `{:ns :name :library-url}` (RFC §1), Library/Namespace as JVM-heap entities, Example soft-delete vs. Note/SeeAlso hard-delete (RFC §1 Deletion Semantics), and the `clojuredocs-export.json` → Calva/CIDER export contract (RFC §5 OQ3). Authorization (model doc line 42) is not covered by the RFC — no conflict, just out of the RFC's scope.

## Priority 1 — Factual concerns (claims-auditor) — DEFERRED

1. **`add-indexes-to-coll!` runtime effect stated as fact, then flagged as unconfirmed.** Line 48 asserts *"no collection other than `:examples` actually gets its intended indexes"* as fact, while line 57 says *"Confirm against production MongoDB before dropping."* The function bug is provable from code; whether those collections lack the indexes in a running DB is unverified. Rewrite to separate the proven code bug from the unverified runtime state.
2. **Unscoped absence claims.** Line 50 *"have no active queries in the codebase"* and line 30 *"There is no Miro diagram or PDF export in the repo"* are absolute. Scope to the search performed (e.g. *"Claude did not find active queries … in `src/`"* / *"as of commit `1483209`"*).
3. **Rhetorical phrasing.** Line 41 *"notes and see-alsos vanish without trace"* — replace with the testable fact (hard-delete via `mon/destroy!`, no history record).
4. **Causation as fact.** Line 39 *"Multi-library support is blocked by the hardcoded singleton"* — soften to *"one obstacle to"* (already partly hedged in the Implication column; align the two cells).
5. Lower-priority: line 38 *"every Example, Note, SeeAlso"* (provable from RFC coverage figures — OK), line 44 *"richer"* undefined comparative (now reworded by the dialect fix).

## Priority 2 — Navigation concerns (link-auditor) — DEFERRED

The largest cluster: the document has **zero commit-pinned code permalinks**. All code references are plain text.

1. **Source References table (lines 61–69)** — ~17 `file + line-range` references as plain text. Convert each to a GitHub permalink pinned to a commit (current HEAD: `14832091f583704b51e45e4e47ac5f6fd379f43f`), with line numbers in the URL, not the display text. **Not auto-fixed:** each line range must be re-verified against the pinned commit before linking — guessing ranges would propagate errors. The glossary reuses `/blob/master/` links, which violate the commit-pin convention; do not copy them verbatim.
2. **First mentions of `Var`, `Library`, `Namespace`** (lines 38–40) — link to [glossary.md](glossary.md) anchors. Deferred only to confirm the exact anchor IDs.
3. **`Calva` / `CIDER`** (line 43) — add repo links (`BetterThanTomorrow/calva`, `clojure-emacs/cider`).
4. **Repo-action references** — `PR #57` (line 79) and `errata #8–#11` (lines 81, 83) are described but not linked. Link PR #57 to its GitHub URL and each `errata #N` to its anchor in [errata.md](errata.md).
5. **`backseat-driver` metadata link** (line 8) — points at a version-pinned local `.vscode` path, not a stable/shareable URL. Flag as non-verifiable by a reader, or point at a repo permalink.

## Priority 3 — Source concerns — DEFERRED

The document has a **Source References** table (where each entity is defined/queried/mutated) but no **annotated bibliography**: no per-source relevance/quality assessment, no grouping by type (source code vs. RFC vs. EDN vs. glossary), and no explicit "not accessed / known gaps" table. For a terse internal data-model doc this is lighter-weight than a research report needs, but a short **Sources** section grouping the EDN (source of truth), the test, the RFC, and the glossary — each with a one-line relevance/quality note — would close the gap. Recommended, not auto-added.

## Priority 4 — Process concerns

1. **Version history** — before this pass the latest entry was 2026-06-09; no entry for today. **FIXED** (added 2026-06-16 row).
2. **Errata** — the doc has no inline Errata section but links to the repo-convention [errata.md](errata.md). Satisfied via that file. **OK.**
3. **`Last updated`** — was 2026-06-09. **FIXED** (bumped to 2026-06-16).
