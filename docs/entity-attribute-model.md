> **Document metadata**
> - **Created:** 2026-06-01
> - **Last updated:** 2026-06-16
> - **Tags:** entity-model, data-model, mongodb, issue-43
> - **AI-assisted:** Yes — Claude Opus 4.6 via GitHub Copilot (original); Claude Opus 4.8 via Claude Code (2026-06-09 verification + EDN promotion)
> - **Session:** `41bcf361` (original); `c6580eec` (2026-06-09 update)
> - **Tools:** GitHub MCP, workspace file access; Clojure MCP (live nREPL eval) for the 2026-06-09 verification
> - **Agents/skills:** [backseat-driver](/.vscode/extensions/betterthantomorrow.calva-backseat-driver-0.0.34/assets/skills/backseat-driver/SKILL.md), [editing-clojure-files](/.vscode/extensions/betterthantomorrow.calva-backseat-driver-0.0.34/assets/skills/editing-clojure-files/SKILL.md)
> - **Review maturity:** L4 — human-endorsed by Jordan Miller (2026-06-09); the JVM-heap entities (Library, Namespace, Var) and DialectCompat are REPL-verified and guarded by [`entity_model_test.clj`](../test/clojuredocs/entity_model_test.clj). Exception: MongoDB cardinalities/coverage remain L2 (snapshot-derived, not test-guarded — see [#66](https://github.com/nubank/clojuredocs/issues/66)).
>
> _AI-assisted document. JVM-heap entities (Library, Namespace, Var) and DialectCompat are REPL-verified and enforced by tests; MongoDB cardinalities are derived from a one-time snapshot and are not yet test-guarded ([#66](https://github.com/nubank/clojuredocs/issues/66)). AI-generated content may contain errors — see [errata.md](errata.md)._
>
> _**Scope — present-state only, so far.** Issue [#43](https://github.com/nubank/clojuredocs/issues/43) is to map the full data model: both what exists today **and** what the [2026 vision](2026vison.md) will require us to build. This model currently holds only the **verified present-state** — entities and attributes checked against the running system (`:status :exists`). The **envisioned future-state** — combing the vision to enumerate the entities/attributes that still need to be created (which carry `:status :gap` / `:planned`) — has not been done yet; the lone exception is `:dialect-compat`, which already carries a few `:gap` attributes. So "verified"/L4 reflects the present-state half; it is not a claim that the model is complete against the vision._

# ClojureDocs Entity-Attribute Model

> **Open review items:** A [research-review pass (run 1)](entity-attribute-model_research-review_run_1.md) on 2026-06-16 cross-checked this doc against the [entity-model RFC](rfcs/entity-model-rfc.md). It corrected one RFC contradiction (dialect data — see [errata #12](errata.md)) and logged deferred link/sourcing fixes.

### Markers

- **⚠** = Known bug or problem in the current codebase. Search this document for `⚠` to find all flagged issues.

| Marker | Issue | Description |
|--------|-------|-------------|
| ⚠ #53 | [nubank/clojuredocs#53](https://github.com/nubank/clojuredocs/issues/53) | `add-indexes-to-coll!` ignores collection argument |
| ⚠ #54 | [nubank/clojuredocs#54](https://github.com/nubank/clojuredocs/issues/54) | `patch-note-handler` missing authorship check |
| ⚠ #55 | [nubank/clojuredocs#55](https://github.com/nubank/clojuredocs/issues/55) | ExampleHistory stores new body, not previous |
| ⚠ #56 | [nubank/clojuredocs#56](https://github.com/nubank/clojuredocs/issues/56) | Typo `:migraion-key` in index definition |

## Entity Model Diagram

A visual diagram in Miro is the **eventual** representation, not the current focus. Reviewing with Alex — who confirmed he can review the data directly — the first step is sound data, not a diagram. There is **no** Miro diagram or PDF export in the repo yet; the data below is the deliverable under review. See [the decision log](decisions.md) for rationale.

The machine-readable source of truth for entities and attributes is [`entity-attribute-model.edn`](entity-attribute-model.edn), which superseded the earlier [`entity-attribute-model.csv`](entity-attribute-model.csv). The EDN carries a per-attribute `:status` and is enforced by [`entity_model_test.clj`](../test/clojuredocs/entity_model_test.clj), which checks the JVM-heap entities (Library, Namespace, Var) and DialectCompat against the running system on every test run. Errors found and corrected while building and verifying the schema are recorded in [errata.md](errata.md).

## Key Observations

| Concern | Current State | Implication |
|---------|--------------|-------------|
| **Var identity** | Composite {ns, name, library-url} — no canonical record, duplicated as embedded sub-docs in every Example, Note, SeeAlso | A var rename requires updating every referencing document |
| **Library** | Hardcoded singleton in `search.clj` | Multi-library support is blocked by the hardcoded singleton; one approach is promoting Library to a persisted entity |
| **Namespace** | Derived from JVM at startup via static list | Adding a namespace requires a code change and deploy |
| **Deletion** | Examples: soft-delete (`deleted-at`). Notes & SeeAlsos: hard-delete (`mon/destroy!`) | Inconsistent audit trail — examples have history, notes and see-alsos vanish without trace |
| **Authorization** | Author-only delete for all three. Example editing: any logged-in user. Note editing: author-only in UI (`can-edit?`) but no authorship check at the API level ⚠ [#54](https://github.com/nubank/clojuredocs/issues/54) | No moderation, no admin, no flagging |
| **Export contract** | `clojuredocs-export.json` consumed by Calva, CIDER | Schema changes risk breaking downstream |
| **Dialect data** | Its own entity (`:dialect-compat`), loaded from `resources/dialect-compat.edn` at startup and keyed by var — **not** stored on Var documents | Already modeled as a standalone entity per [RFC §5 OQ2](rfcs/entity-model-rfc.md); vision keys (`:version`, `:test-suite-url`, `:verified-at`) are tracked as `:gap` attributes in the [EDN](entity-attribute-model.edn). No future "promotion" step needed |

## Vestigial Collections

⚠ [#53](https://github.com/nubank/clojuredocs/issues/53) **`add-indexes-to-coll!` bug:** The function in `main.clj` L53-54 ignores its `coll` parameter — every call does `(mon/add-index! :examples [k])` regardless of which collection was passed. This means **no collection other than `:examples` actually gets its intended indexes**, including the actively-used `:see-alsos`, `:notes`, and `:users` collections.

The following collections are called by `add-all-indexes!` but have no active queries in the codebase:

- `:namespaces` — intended index: `[:name]`
- `:libraries` — intended index: `[:namespaces]`
- `:vars` — referenced only in `tools/old_import.clj`
- `:migrate-users` — intended index: `[:email :migraion-key]` (note: typo `migraion` in source ⚠ [#56](https://github.com/nubank/clojuredocs/issues/56))

> Confirm against production MongoDB before dropping.

## Source References

| Entity | Schema defined in | Queried in | Mutated in |
|--------|------------------|------------|------------|
| Example | `api/examples.clj` L43-49 (Insert), L63-65 (Update) | `data.clj` L6-12 | `api/examples.clj` L52-107 |
| ExampleHistory | `api/examples.clj` L68-73 | — | `api/examples.clj` L93 |
| Note | `api/notes.clj` L12-18 | `data.clj` L16-22 | `api/notes.clj` L20-62 |
| SeeAlso | `api/see_alsos.clj` L24-28 | `data.clj` L26-30 | `api/see_alsos.clj` L38-65 |
| User | `api/common.clj` L9-12 | `pages/user.clj` L13 | `pages/gh_auth.clj` L22 |
| Var | `api/common.clj` L14-16 | `search.clj` L148-150 | — (read-only, derived) |
| Library | `search.clj` L112-115 | `pages/vars.clj` L34 | — (hardcoded) |

---

> **AI Disclosure**: This model was first extracted from the codebase by Claude (Opus 4.6) via GitHub Copilot, then verified against the running system by Claude (Opus 4.8) via Claude Code and reviewed by Jordan Miller. JVM-heap entity shapes (Library, Namespace, Var) and DialectCompat are confirmed by REPL evaluation and guarded by [`entity_model_test.clj`](../test/clojuredocs/entity_model_test.clj). MongoDB cardinalities are snapshot-derived ([#66](https://github.com/nubank/clojuredocs/issues/66)). AI-generated content may contain errors — see [errata.md](errata.md) for ones found and corrected.

## Version History

| Date | Changes |
|------|---------|
| 2026-06-01 | Initial entity-attribute model extracted from source code (PR #57). Review maturity L2. |
| 2026-06-05 | Replaced Mermaid ER diagrams with Miro + EDN strategy; pointed source-of-truth at the forthcoming EDN. |
| 2026-06-09 | Promoted the EDN to source of truth (now REPL-verified and guarded by `entity_model_test.clj`). Corrected the stale "read from source, not the running system" framing. Bumped to L3 (JVM-heap entities; Mongo cardinalities remain L2 pending [#66](https://github.com/nubank/clojuredocs/issues/66)). Logged errata #8–#10 and filed [#66](https://github.com/nubank/clojuredocs/issues/66)–[#70](https://github.com/nubank/clojuredocs/issues/70) during verification. |
| 2026-06-09 | Endorsed to L4 by Jordan Miller for the JVM-heap entities (REPL-verified + test-guarded). MongoDB cardinalities explicitly held at L2 pending [#66](https://github.com/nubank/clojuredocs/issues/66). |
| 2026-06-09 | Corrected the claim that the canonical visual lives in Miro and that a PDF is checked into the repo (none exists). Per review with Alex, the EDN/data is the first deliverable and review artifact; the Miro visual is the eventual goal (errata #11). |
| 2026-06-16 | Cross-checked claims against the [entity-model RFC](rfcs/entity-model-rfc.md) and corrected the "Dialect data" Key Observation, which described `dialect-compat` as an attribute of Var — the RFC ([§5 OQ2](rfcs/entity-model-rfc.md)) and the [EDN](entity-attribute-model.edn) model it as its own entity (errata #12). Ran `/research-review`; deferred link and sourcing fixes logged in [run 1](entity-attribute-model_research-review_run_1.md). |
