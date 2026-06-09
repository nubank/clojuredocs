> **Document metadata**
> - **Created:** 2026-06-09
> - **Last updated:** 2026-06-09
> - **Tags:** errata, entity-model, ai-mistakes, issue-43
> - **AI-assisted:** Yes — Claude Opus 4.6 via GitHub Copilot
> - **Session:** `f16cfa81`
> - **Tools:** GitHub MCP, workspace file access, Calva REPL
> - **Agents/skills:** [backseat-driver](/.vscode/extensions/betterthantomorrow.calva-backseat-driver-0.0.34/assets/skills/backseat-driver/SKILL.md)
> - **Review maturity:** L3 — human-verified via REPL evaluation
>
> _Errata document. Records errors made by AI during entity model work (issue #43, PR #57), how they were discovered, and how to reduce similar errors. See also: [research guidance on errata](https://github.com/nubank/stu-ai-projects/tree/master/research-guidance.md#write-errata-not-silent-fixes)._

# Entity Model Errata

<!-- Errata are errors discovered and subsequently corrected. Each entry states what was
     wrong, how the error was discovered and proved, the correction, why the AI likely
     made the mistake, and what would reduce similar errors.

     This is NOT a limitations section. Limitations qualify scope or confidence and belong
     inline where claims are made. -->

## Format

Each erratum follows this structure:

- **Claimed**: What the AI asserted
- **Discovered**: How the human found and proved the error
- **Corrected**: What changed
- **Why**: Speculation on why the AI made the mistake
- **Prevent**: What would catch this class of error earlier

---

1. **CSV listed `:source-url` and `:usage-urls` as Var attributes**

   - **Claimed**: [entity-attribute-model.csv](entity-attribute-model.csv) listed `:source-url` (string) and `:usage-urls` (list) as existing Var attributes with source "JVM heap (derived from ns-publics)."
   - **Discovered**: REPL evaluation in [sidecar_repl.clj](../dev/sidecar_repl.clj). `(:source-url (search/lookup "clojure.core/map"))` returned `nil`. `(:usage-urls (search/lookup "clojure.core/map"))` returned `nil`. Neither key appears in `search/var-keys` or anywhere in the gather pipeline.
   - **Corrected**: Not yet corrected in CSV — will be removed or marked as gap when EDN schema is built.
   - **Why**: The AI likely inferred these attributes from what a documentation site *should* have rather than what the code actually produces. `:source-url` is a plausible computed field (and `source-base-url` exists on Library), which may have made this fabrication seem reasonable. `:usage-urls` appears nowhere in the codebase — pure fabrication.
   - **Prevent**: Eval every attribute against the running system before marking it `:exists`. The sidecar REPL pattern makes this a 5-second check per attribute. Alternatively, a transcriptor test that asserts `(every? #(contains? var-from-repl %) claimed-keys)` would catch this mechanically.

2. **CSV listed 4 Namespace attributes; only 2 exist**

   - **Claimed**: Namespace entity has 4 attributes: `:name`, `:doc`, `:added`, `:library-url`. CSV listed `:added` (source: "JVM heap (derived at startup)") and `:library-url` (source: "JVM heap (derived at startup)") as existing.
   - **Discovered**: REPL evaluation. `(keys (first (:namespaces search/clojure-lib)))` returned `(:doc :name)` — only 2 keys. `gather-namespace` in `search.clj` selects `:doc :no-doc :added` from ns metadata then adds `:name`, but `:no-doc` and `:added` are nil on most namespaces and absent from the resulting map (`select-keys` with a missing key omits that key from the result — there is no error or nil placeholder). `:library-url` is not added by `gather-namespace` — it's only on Var.
   - **Corrected**: The EDN omits `:library-url` from Namespace (its attrs are `:name`, `:doc`, `:added`). The stale CSV still shows it, but the CSV is now [superseded by the EDN](decisions.md) (banner header) and slated for retirement, so it will not be individually corrected.
   - **Why**: The AI read `gather-namespace` source and saw `(select-keys (meta (find-ns ns-sym)) [:doc :no-doc :added])` — correctly noting those keys are *selected for*, but incorrectly concluding they *exist* on the output. For `:library-url`, the AI likely copied it from Var's attribute list (where `gather-vars` adds it) to Namespace by analogy. The CSV marks it "exists (implicit)" which is a hedge that masks fabrication.
   - **Prevent**: Distinguish "code selects for this key" from "this key appears on output." REPL eval of `(keys ...)` on actual data is the only reliable check. The hedge "exists (implicit)" should be treated as a smell — if you need to qualify existence, verify it.

3. **CSV mixed current and gap attributes without status markers on individual attributes**

   - **Claimed**: User entity listed 3 attributes (`:login`, `:account-source`, `:avatar-url`) all marked `exists`. But the original CSV also included a `reputation` attribute on User in an earlier draft, and Example had `verified` / `verification-result` — attributes that don't exist in the codebase.
   - **Discovered**: `grep -rn` across `src/` for `:reputation`, `:verified`, `:verification-result` returned zero matches. These are aspirational attributes from the 2026 vision document, not current code.
   - **Corrected**: Partially — the current CSV separates gap entities below a `---` divider, but gap *attributes* on existing entities had no marker.
   - **Why**: The AI was prompted to model both current state and future vision in the same artifact. Without a per-attribute `:status` field, the AI defaulted to listing everything it thought the entity *should* have. The CSV's flat format (one status column per row) didn't prevent this, but didn't encourage distinguishing either.
   - **Prevent**: The EDN schema's per-attribute `:status` field (`:exists` / `:gap` / `:planned`) makes this structurally impossible — every attribute must declare its status. Format enforces correctness that prose conventions cannot.

4. **CSV contained structural noise rows**

   - **Claimed**: Rows with entity names `---`, `Group`, and `QualitySignal` appeared in the CSV, some with no attributes.
   - **Discovered**: Visual inspection of CSV in editor. `Group` and `QualitySignal` have no attributes, no source, and no clear definition. The `---` row is a visual separator that a CSV parser would treat as data.
   - **Corrected**: Not yet — will not carry over to EDN.
   - **Why**: The AI treated the CSV as a display format (like a markdown table) rather than a data format. Visual separators and placeholder entities were inserted for human readability without considering that CSV is machine-parseable and should contain only valid records.
   - **Prevent**: Validate CSV with a parser after generation. Or better: use EDN from the start, where the structure rejects non-conforming entries by shape.

5. **Duplicate decision log entry**

   - **Claimed**: N/A — this is a process error, not a factual claim.
   - **Discovered**: `grep -n "## 2026-06-05 — Replace Mermaid" docs/decisions.md` returned two matches (lines 19 and 54). The entries were nearly identical, differing only in the PDF versioning mitigation paragraph.
   - **Corrected**: Removed the first copy (which lacked the PDF versioning detail), kept the second.
   - **Why**: The entry was likely generated, then the session was interrupted or context was lost, and the AI re-generated it with slight improvements without checking whether the first version still existed. Context window limitations make "did I already write this?" a hard problem for AI.
   - **Prevent**: Before appending to a log file, grep for the entry title. This is a mechanical check that should be part of the AI's workflow — and is now documented in the sidecar REPL decision as a pattern.

6. **PR had two Mermaid ER diagrams instead of one**

   - **Claimed**: The entity-attribute-model.md in PR #57 contained two separate Mermaid `erDiagram` blocks.
   - **Discovered**: Sandra (PR reviewer) flagged this during code review — "there are 2 diagrams, should be 1."
   - **Corrected**: Both diagrams were subsequently removed entirely (replaced by Miro + EDN strategy).
   - **Why**: The first diagram covered "existing" entities and the second covered "gap" entities. The AI split them because Mermaid's `erDiagram` has no built-in way to visually distinguish entity status (solid vs. dashed lines). Rather than finding a single-diagram solution, the AI created two diagrams — doubling the maintenance surface and confusing the reader.
   - **Prevent**: When a tool can't express a distinction you need, that's a signal to question the tool choice — not to work around it by duplicating artifacts. This ultimately led to the decision to drop Mermaid entirely.

7. **CSV omitted `:library-url` from LegacyVarRedirect while fabricating it on Namespace**

   - **Claimed**: CSV listed LegacyVarRedirect with 3 attributes: `function-id`, `ns`, `name`. No `:library-url` attribute.
   - **Discovered**: REPL evaluation of `(keys (first (mon/fetch :legacy-var-redirects)))` returned `(:_id :function-id :library-url :ns :name)` — 5 keys, not 3. Full-collection key frequency scan confirmed `:library-url` appears on all 1,654 documents.
   - **Corrected**: The EDN's `:legacy-var-redirect` includes both `:_id` and `:library-url`. The CSV still omits them, but the CSV is now [superseded by the EDN](decisions.md) and slated for retirement, so it will not be individually corrected.
   - **Why**: Mirror image of erratum #2. The AI fabricated `:library-url` on Namespace (where it doesn't exist) by analogy from Var, while omitting it from LegacyVarRedirect (where it does exist). The AI listed only 3 of 5 keys — whether this was because LegacyVarRedirect was treated as a simple redirect table is speculation. The result: attributes that a documentation system would plausibly have were added where absent and omitted where present.
   - **Prevent**: The key-universe scan (`(mapcat keys) frequencies`) catches both fabrications and omissions in one pass. Run it on every MongoDB collection, not just the ones you expect to be interesting.

8. **EDN listed `"special-form"` as a Var `:type` value, but no var produces it**

   - **Claimed**: [entity-attribute-model.edn](entity-attribute-model.edn) `:var :type` read `One of: "function", "macro", "var", "special-form"`.
   - **Discovered**: REPL — `(->> search/clojure-lib :vars (map :type) frequencies)` returned `{"function" 1225, "macro" 190, "var" 157}`; no `"special-form"`. Root cause in `search.clj`: `transform-var-meta`'s `(select-keys var-keys)` drops `:type` (it is not in `var-keys`), so the 15 `static/special-forms` are re-typed by `type-of` to `"var"`; the 4 vars carrying the `:special-form` metadata key (`fn`/`let`/`letfn`/`loop`) are macros, so `type-of` returns `"macro"` before reaching its `special-form` branch.
   - **Corrected**: EDN description updated to the observed set with the explanation; `var-type-values` changed from `subset?` to exact-set equality so a phantom type cannot pass again. Filed [#67](https://github.com/nubank/clojuredocs/issues/67) for the dead `type-of` branch / stripped static `:type`.
   - **Why**: Same class as #2 — the AI listed the values `type-of` *can* return (reading the `cond`), plus values the downstream UI/import code *accept*, rather than observing the actual type set on data. A `subset?` test masked the over-claim.
   - **Prevent**: Assert the *exact* value set against the running system, never a superset. `subset?`-style checks pass silently when the schema over-claims.

9. **EDN `:var` omitted `:no-doc`, which `var-keys` retains**

   - **Claimed**: The `:var` entity enumerated the full key universe (and `var-key-universe-matches-schema` asserts `actual ⊆ schema`), but had no `:no-doc` attribute.
   - **Discovered**: `search.clj` lists `:no-doc` in `var-keys`, so the gather pipeline retains it when present — yet the EDN had no entry. REPL confirmed 0/1572 current vars carry it, so the test passed *despite* the omission; the gap was latent (a future Clojure var with `:no-doc` would break the test and prove the schema incomplete).
   - **Corrected**: Added `:no-doc` to `:var` with a new `:absent` status state ("retained by code but absent from current data"), coverage `0/1572`; added `:absent` to the test's `valid-states`.
   - **Why**: The model was built from the *observed* key universe of current data; a key the code retains but no current var carries is invisible to a data-only scan. `var-keys` (the code's declared intent) was not reconciled against observed data.
   - **Prevent**: Reconcile the schema against `search/var-keys` (the code's declared key set), not only against observed data. A test asserting `schema-var-keys ⊇ (var-keys ∪ pipeline-keys)` would catch it mechanically.

10. **EDN gave DialectCompat cardinality as "~1400 var-dialect pairs" — wrong on both units**

    - **Claimed**: `:dialect-compat :cardinality "~1400 var-dialect pairs"`.
    - **Discovered**: REPL — `(count (:vars compat/dialect-compat))` returned 715; `(reduce + (map count (vals (:vars compat/dialect-compat))))` returned 1880. The figure matched neither vars (715) nor pairs (1880). No test guarded the count, so it went unnoticed.
    - **Corrected**: Cardinality updated to `"715 vars / 1880 var-dialect pairs"` (REPL-verified). Filed [#69](https://github.com/nubank/clojuredocs/issues/69) to add a count test.
    - **Why**: Plausible fabrication of a magnitude (#1) — a round "~1400" reads as reasonable for "a bit over a thousand entries" and was never checked. It was the only cardinality in the model with a `~` and no test.
    - **Prevent**: Pin every cardinality to a REPL-verified integer and guard it with a test; treat `~`-prefixed approximate counts as unverified smells.

11. **Claimed a Miro PDF export was checked into the repo; none exists**

    - **Claimed**: [entity-attribute-model.md](entity-attribute-model.md) stated "The canonical visual diagram lives in Miro … with a PDF export checked into this repo," and the [2026-06-05 decision](decisions.md) described versioned PDF exports kept in the repo.
    - **Discovered**: `git ls-files | grep -i '\.pdf$'` returns no entity-model PDF — none was ever committed. Reviewing with Alex also clarified the visual is not the current deliverable: he can review the EDN data directly.
    - **Corrected**: Reframed the model doc and PR #57 to say the EDN/data is the first deliverable and review artifact, and the Miro visual is an eventual goal (not yet produced). Recorded the direction in a [decision entry](decisions.md).
    - **Why**: The doc described the intended end-state (Miro canonical + PDF in repo) as if already done — aspirational state written as current fact, the same class as errata #1 and #2.
    - **Prevent**: Before claiming an artifact is "checked into the repo," verify it exists (`git ls-files`). Keep "the plan" and "the current state" distinct in prose.

---

## Patterns

Recurring failure modes observed across these errata:

1. **Plausible fabrication** (#1, #3, #7, #10): The AI generates attributes that are plausible for the domain but not present in the system, rather than attributes it *does* have based on code. The more reasonable the fabrication, the harder it is to catch without REPL verification. Erratum #7 shows this works in both directions — fabricating presence *and* fabricating absence. Erratum #10 shows it applies to magnitudes too: a round "~1400" reads as reasonable and was never checked.

2. **Code-reading ≠ data-reading** (#2, #8, #9): Reading source code that *selects for* a key is not the same as observing that key on actual runtime data. `select-keys` with a missing key omits it from the result silently — making this invisible to static analysis. Erratum #8 is the inverse: a value `type-of` *can* return, and the UI/import code *accept*, is never produced for the live data; #9 is a key `var-keys` retains but no current var carries.

3. **Format limitations masked as content problems** (#4, #6): When the chosen format (CSV, Mermaid) can't express a needed distinction, the AI works around it by adding noise rather than questioning the format choice. The fix is always to choose a format that makes the distinction structural.

4. **Context loss across sessions** (#5): AI cannot reliably track what it has already produced across context window boundaries. Mechanical checks (grep before append) are more reliable than memory.

## Version History

| Date | Changes |
|------|---------|
| 2026-06-09 | Initial errata document with 6 entries from entity model work. Verified against REPL output and source code. |
| 2026-06-09 | Added erratum #7 (LegacyVarRedirect `:library-url` omission). Updated pattern #1 to include bidirectional fabrication. |
| 2026-06-09 | Added errata #8–#10 (Var `:type` phantom `"special-form"`, `:no-doc` omission, DialectCompat cardinality) from REPL verification of the EDN schema while reviewing the tests. Filed [#66](https://github.com/nubank/clojuredocs/issues/66)–[#70](https://github.com/nubank/clojuredocs/issues/70). Updated patterns #1 and #2. |
| 2026-06-09 | Superseded the entity-model CSV with the EDN (banner header; retirement deferred to the #43 vision pass). Updated the "Corrected" status on errata #2 and #7 accordingly — they are resolved by the EDN plus CSV retirement, not by editing the CSV. See the [decision entry](decisions.md). |
| 2026-06-09 | Added erratum #11 (claimed a Miro PDF was checked into the repo; none exists). Corrected the model doc and PR #57 to put data first, with the Miro visual as an eventual goal — per review with Alex. |
