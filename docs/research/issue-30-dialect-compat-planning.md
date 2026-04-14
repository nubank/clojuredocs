# Planning: Cross-Dialect Compatibility Indicators

> **Issue:** [nubank/clojuredocs#30](https://github.com/nubank/clojuredocs/issues/30)
> **Branch:** `research/30/dialect-compat-planning`
> **Author:** <!-- your name -->
> **Date started:** <!-- YYYY-MM-DD -->
> **Last updated:** <!-- YYYY-MM-DD -->

---

## How to use this document

This is a planning artifact for [Issue #30: Cross-Dialect Compatibility Indicators](https://github.com/nubank/clojuredocs/issues/30). It follows the Reflective Inquiry framework from Rich Hickey's ["Design in Practice"](https://www.youtube.com/watch?v=c5QF2HjHLSE) talk and Alex Miller's ["Design in Practice in Practice"](https://www.youtube.com/watch?v=VBnGhQOyTM4) follow-up.

The structure uses Father Watson's four questions applied to two axes — Understanding and Activity — forming a 2×2 matrix that can be revisited at any point during the work:

|               | Understanding (why)          | Activity (what)          |
|---------------|------------------------------|--------------------------|
| **Status**    | What do we know?             | Where are we at?         |
| **Agenda**    | What do we need to know?     | Where are we going?      |

Fill this in iteratively. It is not a checklist. Come back and update sections as understanding grows. Sections that say "unknown" are the most valuable — they tell you where to direct effort next.

---

## Glossary

<!-- Precision in naming yields precision in thinking. Define terms on first use. -->

| Term | Definition |
|------|-----------|
| **Dialect** | A runtime implementation that executes Clojure-syntax code. The three dialects in scope for this issue are Clojure/JVM, ClojureScript, and babashka. |
| **Var** | A named reference to a value in a Clojure namespace (e.g., `clojure.core/map`). On ClojureDocs, each var has a dedicated page. |
| **Compatibility indicator** | A visual label on a var page showing whether that var is supported in a given dialect. Three states: supported, not supported, unknown. |
| **EDN** | Extensible Data Notation. Clojure's data format, used here for the static compatibility data file. |
| <!-- term --> | <!-- definition --> |

---

## Part 1: Per-Dialect Research (Father Watson × 3)

For each dialect, answer the four questions to build understanding before planning implementation.

---

### 1.1 ClojureScript

#### What do we know? (Status — Understanding)

<!-- What is the current state of ClojureScript's standard library coverage?
     What data sources exist? How complete and fresh are they? -->

- ClojureScript compiler version: <!-- e.g., 1.11.x -->
- `cljs.core` maps to `clojure.core` with known omissions (e.g., `pmap`, `locking`, concurrency primitives)
- `clojure.string` is available in ClojureScript as `clojure.string` (same namespace)
- Known data sources:
  - [ ] [ClojureScript cheatsheet](https://cljs.info/cheatsheet/) — last verified: <!-- date or "not yet" -->
  - [ ] ClojureScript compiler source (`cljs.core` namespace) — location: <!-- URL or "not yet located" -->
  - [ ] ClojureScript API docs — <!-- URL or "not yet located" -->
  - [ ] Other: <!-- any additional sources -->
- What is the authoritative, machine-readable source for "which vars exist in cljs.core"?
  <!-- e.g., the compiler source, an API dump, a namespace listing -->

#### Where are we at? (Status — Activity)

<!-- What concrete steps have been taken so far for ClojureScript data? -->

- [ ] Identified authoritative data source
- [ ] Retrieved var list from data source
- [ ] Cross-referenced against ClojureDocs `clojure.core` vars
- [ ] Cross-referenced against ClojureDocs `clojure.string` vars
- [ ] Documented gaps and surprises

#### What do we need to know? (Agenda — Understanding)

<!-- What open questions remain? What would change our approach if answered differently? -->

- Is there a programmatic way to dump all public vars from `cljs.core`? Or must we parse source/docs?
- Are there vars that exist in ClojureScript under a different name or namespace?
- Are there vars where the signature differs (e.g., different arities)?
- Does ClojureScript version matter for our data? (Which version do we target?)
- <!-- add more questions as they arise -->

#### Where are we going? (Agenda — Activity)

<!-- What are the next concrete steps? -->

1. <!-- next step -->
2. <!-- next step -->

#### Maintainer / Point of Contact

| Field | Value |
|-------|-------|
| Project | ClojureScript |
| Repository | https://github.com/clojure/clojurescript |
| Primary maintainer(s) | <!-- name(s) --> |
| Contact info | <!-- email, Slack handle, Clojurians Slack channel, etc. --> |
| Timezone / Location | <!-- e.g., EST / Durham, NC --> |
| Best way to reach them | <!-- e.g., #clojurescript on Clojurians Slack --> |
| Have we contacted them? | <!-- yes/no, date, outcome --> |
| Their stance on this feature | <!-- supportive / neutral / unknown / concerns --> |
| Notes | <!-- anything relevant from conversations --> |

---

### 1.2 babashka

#### What do we know? (Status — Understanding)

<!-- What is the current state of babashka's standard library coverage?
     What data sources exist? How complete and fresh are they? -->

- babashka version: <!-- e.g., 1.x.x -->
- babashka supports a subset of `clojure.core` and `clojure.string` (plus additional namespaces)
- Known data sources:
  - [ ] [babashka documentation](https://book.babashka.org/) — last verified: <!-- date or "not yet" -->
  - [ ] `bb -e '(keys (ns-publics (quote clojure.core)))'` — tested: <!-- date or "not yet" -->
  - [ ] `bb -e '(keys (ns-publics (quote clojure.string)))'` — tested: <!-- date or "not yet" -->
  - [ ] babashka source / var registry — <!-- URL or "not yet located" -->
  - [ ] Other: <!-- any additional sources -->
- What is the authoritative, machine-readable source for "which vars exist in bb's clojure.core"?

#### Where are we at? (Status — Activity)

- [ ] Identified authoritative data source
- [ ] Retrieved var list from data source
- [ ] Cross-referenced against ClojureDocs `clojure.core` vars
- [ ] Cross-referenced against ClojureDocs `clojure.string` vars
- [ ] Documented gaps and surprises

#### What do we need to know? (Agenda — Understanding)

- Can we rely on `bb -e '(ns-publics ...)'` as the authoritative source, or does it miss vars?
- How frequently does babashka add new var support? Is our data stale within weeks or months?
- Are there vars that exist in bb but behave differently in non-obvious ways?
- Does babashka version matter? (Which version do we target? Latest stable?)
- Is there an existing machine-readable list of supported vars we can consume instead of generating?
- <!-- add more questions -->

#### Where are we going? (Agenda — Activity)

1. <!-- next step -->
2. <!-- next step -->

#### Maintainer / Point of Contact

| Field | Value |
|-------|-------|
| Project | babashka |
| Repository | https://github.com/babashka/babashka |
| Primary maintainer(s) | <!-- name(s) --> |
| Contact info | <!-- email, Slack handle, etc. --> |
| Timezone / Location | <!-- --> |
| Best way to reach them | <!-- e.g., #babashka on Clojurians Slack --> |
| Have we contacted them? | <!-- yes/no, date, outcome --> |
| Their stance on this feature | <!-- supportive / neutral / unknown / concerns --> |
| Existing compatibility data they publish | <!-- e.g., does bb publish a var list somewhere? --> |
| Notes | <!-- --> |

---

### 1.3 Clojure/JVM

#### What do we know? (Status — Understanding)

<!-- This dialect is the baseline — all vars on ClojureDocs are JVM-supported by definition.
     But document what we know about the data source anyway. -->

- Clojure version tracked by ClojureDocs: <!-- e.g., 1.12.x — check `search.clj` config -->
- All vars in `clojuredocs.search.static/clojure-namespaces` are Clojure/JVM vars
- The `:clj` column is always "supported" for every var on ClojureDocs
- Source of truth: the var metadata loaded at startup via `clojuredocs.search/gather-vars`

#### Where are we at? (Status — Activity)

- [ ] Confirmed the Clojure version in `src/clj/clojuredocs/search.clj`
- [ ] Enumerated `clojure.core` var count from ClojureDocs
- [ ] Enumerated `clojure.string` var count from ClojureDocs

#### What do we need to know? (Agenda — Understanding)

- How many vars are in scope? (Total count for `clojure.core` + `clojure.string` on ClojureDocs)
- Are there vars on ClojureDocs that lack `:name` or `:ns` metadata and would break our lookup key?
- <!-- add more -->

#### Where are we going? (Agenda — Activity)

1. <!-- next step -->
2. <!-- next step -->

#### Maintainer / Point of Contact

| Field | Value |
|-------|-------|
| Project | Clojure |
| Repository | https://github.com/clojure/clojure |
| Primary maintainer(s) | <!-- name(s) --> |
| Contact info | <!-- --> |
| Timezone / Location | <!-- --> |
| Notes | This is the baseline dialect. No outreach needed unless questions arise about var metadata. |

---

## Part 2: Cross-Cutting Understanding

### What do we know across all three dialects?

<!-- Observations that span dialects. Fill in as research progresses. -->

- Total var count in scope (`clojure.core` + `clojure.string`): <!-- number or "unknown" -->
- Expected overlap (all three dialects support): <!-- rough % or "unknown" -->
- Expected CLJ-only vars: <!-- rough count or "unknown" -->
- Known tricky cases (e.g., vars that exist but behave differently): <!-- list or "none identified yet" -->

### Data quality assessment

| Data source | Format | Freshness | Machine-readable? | Confidence |
|-------------|--------|-----------|-------------------|------------|
| ClojureDocs var list | in-memory at startup | matches Clojure version in config | yes (code) | high |
| ClojureScript cheatsheet | HTML | <!-- ? --> | <!-- ? --> | <!-- ? --> |
| ClojureScript compiler source | Clojure source | <!-- ? --> | <!-- ? --> | <!-- ? --> |
| babashka `ns-publics` dump | EDN via `bb -e` | matches installed bb version | yes | <!-- ? --> |
| babashka docs | HTML | <!-- ? --> | <!-- ? --> | <!-- ? --> |
| <!-- other --> | | | | |

### Risks and open questions

<!-- Promote the most important unknowns here as they emerge. -->

1. <!-- risk or question -->
2. <!-- risk or question -->
3. <!-- risk or question -->

---

## Part 3: Implementation Approach

> Do not fill this section until Parts 1 and 2 have enough substance to make decisions from. The purpose of the research above is to make this section obvious, not to justify a pre-chosen approach.

### Proposed approach (high-level)

<!-- 2-4 sentences. What will we build? What pattern does it follow? -->

### Key decisions

| Decision | Options considered | Chosen | Why |
|----------|-------------------|--------|-----|
| Data file format | EDN / JSON / database | <!-- --> | <!-- --> |
| Data generation | manual / scripted / hybrid | <!-- --> | <!-- --> |
| Loading pattern | startup atom / on-demand / config | <!-- --> | <!-- --> |
| Rendering location | `$var-header` / below signature / sidebar | <!-- --> | <!-- --> |
| Unknown state handling | show "?" / show nothing / omit label | <!-- --> | <!-- --> |
| <!-- decision --> | <!-- --> | <!-- --> | <!-- --> |

### Breakdown into smaller steps

<!-- Numbered list of steps small enough to be a single PR or pairing session.
     Each step should have a clear "done" signal. -->

1. <!-- step: what, done-signal -->
2. <!-- step -->
3. <!-- step -->
4. <!-- step -->
5. <!-- step -->

### What I want to review with Alex

<!-- Specific questions or decisions to discuss with your manager before proceeding. -->

1. <!-- question or decision needing input -->
2. <!-- -->
3. <!-- -->

---

## Part 4: Timeline and Coordination

| Milestone | Target date | Status | Notes |
|-----------|------------|--------|-------|
| Research complete (Parts 1-2 filled) | <!-- --> | not started | |
| Approach reviewed with Alex | <!-- --> | not started | |
| Data file generated | <!-- --> | not started | |
| Rendering implemented | <!-- --> | not started | |
| Local verification | <!-- --> | not started | |
| PR submitted | <!-- --> | not started | |
| Deployed | <!-- --> | not started | Issue target: April 27 |

### External dependencies

<!-- People, repos, or events that could block or accelerate this work. -->

| Dependency | Who/what | Status | Impact if delayed |
|------------|----------|--------|-------------------|
| ClojureScript var list | <!-- --> | <!-- --> | <!-- --> |
| babashka var list | <!-- --> | <!-- --> | <!-- --> |
| Dutch Clojure Days (mid-May) | community event | upcoming | Shipping before this = feedback opportunity |
| babashka conf (mid-May) | community event | upcoming | Same as above |
| <!-- other --> | | | |

---

## References

- [Issue #30: Cross-Dialect Compatibility Indicators](https://github.com/nubank/clojuredocs/issues/30)
- [ClojureDocs Two-Year Vision (2026-2028)](../2026vison.md) — "Cross-dialect hub" strategic bet
- [Data Model Coupling Audit](../resources/datamodelaudit.md) — constraints on schema changes
- Rich Hickey, ["Design in Practice"](https://www.youtube.com/watch?v=c5QF2HjHLSE) (Clojure/conj 2023) — [transcript](https://github.com/matthiasn/talk-transcripts/blob/master/Hickey_Rich/DesignInPractice.md)
- Alex Miller, ["Design in Practice in Practice"](https://www.youtube.com/watch?v=VBnGhQOyTM4) (Clojure/conj 2024)
- [Clojure Team Bets for 2026](../resources/Clojure_2026_1_Pager.pdf) — ClojureDocs as reference application
- [ClojureScript cheatsheet](https://cljs.info/cheatsheet/)
- [babashka documentation](https://book.babashka.org/)
- [jank-lang/clojure-test-suite](https://github.com/jank-lang/clojure-test-suite) — future data source for jank dialect
- `src/clj/clojuredocs/pages/vars.clj` — var page renderer
- `src/clj/clojuredocs/search/static.clj` — static data loading pattern to follow

---

## Version History

| Date | Changes |
|------|---------|
| <!-- YYYY-MM-DD --> | Initial planning document created. |
