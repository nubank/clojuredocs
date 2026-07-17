---
type: RFC
title: "RFC: Entity-Attribute Model EDN Schema"
description: "Replace the entity-model CSV with an EDN schema as the source of truth."
tags: [rfc, entity-model, edn-schema, issue-43, datomic-migration]
created: 2026-06-09
modified: 2026-06-09
ai_assisted: "Claude Opus 4.6 via GitHub Copilot"
tools: "Calva REPL, MongoDB (seed data), workspace files"
review_maturity: L3
review_note: "human-verified via REPL evaluation"
---

# RFC: Entity-Attribute Model EDN Schema

> _All data in this RFC was verified against the running system via REPL on 2026-06-09. See [sidecar_repl.clj](../dev/sidecar_repl.clj) for the executable evidence. Caveat: the MongoDB collection counts and coverage figures are a point-in-time snapshot and are **not** test-guarded — they will drift (see §1 and [#66](https://github.com/nubank/clojuredocs/issues/66)). The JVM-heap entities are re-verified on every test run by [entity_model_test.clj](../test/clojuredocs/entity_model_test.clj)._

## Context

Issue [#43](https://github.com/nubank/clojuredocs/issues/43) asks to make the data model explicit. The current entity-attribute model exists as a CSV ([entity-attribute-model.csv](entity-attribute-model.csv)) produced by AI, which contained 7 documented errors ([errata.md](errata.md)) — 5 in the CSV's entity/attribute claims and 2 in related artifacts. This RFC proposes replacing the CSV with an EDN schema as the source of truth — the next step in the [reliability ratchet](../CLAUDE.md) (LLM → REPL → **Library** → Enforcement).

The [2026 vision](2026vison.md) requires an explicit, extensible data model. Datomic is under evaluation as the migration target. This schema serves dual purposes: documenting what exists today and providing a migration target for the redesign.

## Scope

Document all existing entities and attributes verified via REPL. Include gap/planned entities from the vision with richer status metadata that traces each to its source (verification timestamp, vision doc line, or GitHub issue).

> **Status of this scope.** The present-state inventory (§1) is complete and REPL-verified. The vision-derived gap/planned (envisioned) entities are **not yet enumerated** — that pass over the [2026 vision](2026vison.md) is the pending second half of [#43](https://github.com/nubank/clojuredocs/issues/43). What exists in the EDN today is the verified present-state plus a few `:gap` attributes on `:dialect-compat`.

---

## 1. REPL-Verified Entity Inventory

### Collection Counts

| Collection | Documents | Source |
|---|---:|---|
| `examples` | 2,671 | MongoDB |
| `example-histories` | 3,358 | MongoDB |
| `notes` | 497 | MongoDB |
| `see-alsos` | 2,494 | MongoDB |
| `users` | 4,902 | MongoDB |
| `legacy-var-redirects` | 1,654 | MongoDB |
| Library (singleton) | 1 | JVM heap |
| Namespaces | 38 | JVM heap |
| Vars | 1,572 | JVM heap |

> **Caveat — the MongoDB figures are a snapshot, not a guarded invariant.** The six MongoDB collection counts above, and the per-key coverage figures throughout this section, were captured by a one-time [`dev/sidecar_repl.clj`](../dev/sidecar_repl.clj) run against the database on 2026-06-09. Unlike the JVM-heap entities (Library, Namespace, Var) and DialectCompat — which [`entity_model_test.clj`](../test/clojuredocs/entity_model_test.clj) re-verifies against the running system on every test run — these MongoDB numbers are **not** test-guarded and will drift as the database changes. Tracked in [#66](https://github.com/nubank/clojuredocs/issues/66).

### Key Universe (MongoDB — All Collections)

Every key on every document, with frequencies. No sampling — full scans.

**Examples** (2,671 docs, 6 universal + 2 sparse):

| Key | Count | Coverage |
|---|---:|---|
| `:var` | 2,671 | 100% |
| `:body` | 2,671 | 100% |
| `:created-at` | 2,671 | 100% |
| `:author` | 2,671 | 100% |
| `:_id` | 2,671 | 100% |
| `:updated-at` | 2,671 | 100% |
| `:editors` | 1,708 | 64% |
| `:deleted-at` | 54 | 2% |

**Example Histories** (3,358 docs, 5 keys, all universal):
`:editor`, `:body`, `:created-at`, `:example-id`, `:_id`

**Notes** (497 docs, 6 keys, all universal):
`:updated-at`, `:var`, `:body`, `:created-at`, `:author`, `:_id`

**See-Alsos** (2,494 docs, 5 keys, all universal):
`:created-at`, `:author`, `:to-var`, `:from-var`, `:_id`

**Users** (4,902 docs, 4 keys, all universal):
`:login`, `:account-source`, `:avatar-url`, `:_id`
- 3,650 GitHub users + 1,252 legacy ClojureDocs users
- No `:email`, `:created-at`, or `:reputation` — these keys do not exist in the database

**Legacy Var Redirects** (1,654 docs, 5 keys, all universal):
`:function-id`, `:library-url`, `:ns`, `:name`, `:_id`
- CSV omitted `:library-url` — see [errata #7](errata.md)

### Key Universe (JVM Heap — Startup Entities)

**Library** (1 singleton, 4 scalar + 2 nested):
`:library-url`, `:version`, `:source-base-url`, `:gh-tag-url`, `:namespaces` (38), `:vars` (1,572)

**Namespace** (38 total, 2 universal + 1 sparse):
`:name` (38), `:doc` (38), `:added` (2 — only `clojure.pprint` and `clojure.reflect`)

**Var** (1,572 total, 20 distinct keys):

| Key | Count | Coverage |
|---|---:|---|
| `:ns` | 1,572 | 100% |
| `:name` | 1,572 | 100% |
| `:type` | 1,572 | 100% |
| `:arglists` | 1,572 | 100% |
| `:library-url` | 1,572 | 100% |
| `:href` | 1,572 | 100% |
| `:file` | 1,482 | 94% |
| `:column` | 1,482 | 94% |
| `:line` | 1,482 | 94% |
| `:doc` | 1,293 | 82% |
| `:added` | 907 | 58% |
| `:static` | 319 | 20% |
| `:macro` | 190 | 12% |
| `:tag` | 122 | 8% |
| `:dynamic` | 56 | 4% |
| `:skip-wiki` | 31 | 2% |
| `:deprecated` | 18 | 1% |
| `:special-form` | 4 | <1% |
| `:forms` | 4 | <1% |
| `:url` | 1 | <1% |

`:url` is vestigial — present on exactly one var (`letfn`) with a `nil` value. It originates from an incomplete annotation in Clojure's `core.clj` source (line 6622): the `defmacro letfn` metadata includes `:url` but no value was assigned. The other three special-form macros (`let`, `fn`, `loop`) lack the key entirely. It passes through to ClojureDocs because `:url` is in `search/var-keys` and `select-keys` retains nil-valued keys. Not worth modeling as a meaningful attribute.

### Embedded Sub-Document Consistency

Two embedded doc types exist. Both are 100% uniform across all usages.

| Embedded Type | Shape | Total Occurrences | Collections |
|---|---|---:|---|
| EmbeddedVar | `{:ns :name :library-url}` | 8,156 | Examples, Notes, SeeAlsos (from-var, to-var) |
| EmbeddedUser | `{:account-source :avatar-url :login}` | 11,616[^1] | Examples (author, editors), Notes, SeeAlsos, ExampleHistories |

EmbeddedUser is User minus `:_id`. Zero sparsity in either type.

[^1]: 11,616 counts individual embedded user objects across document-level fields (`:author`, `:editor`: 9,020) and list entries within `:editors` (2,596).

### Deletion Semantics

| Entity | Mechanism | Evidence |
|---|---|---|
| Example | Soft-delete via `:deleted-at` | 54 of 2,671 (2%) have non-nil `:deleted-at` |
| Note | Hard-delete | No `:deleted-at` key exists on any document |
| SeeAlso | Hard-delete | No `:deleted-at` key exists on any document |
| ExampleHistory | Append-only | No deletion mechanism |
| User | None observed | No deletion mechanism |
| LegacyVarRedirect | None observed | No deletion mechanism |

### Activity Profile

| Metric | Value |
|---|---|
| Oldest example | 2010-07-03 |
| Newest example | 2025-09-05 |
| Newest example history | 2025-09-10 |
| Newest note | 2025-09-09 |
| Newest see-also | 2025-09-06 |
| Active examples | 2,617 (54 soft-deleted) |
| Vars with ≥1 example | 997 of 1,572 (63%) |
| Vars with 0 examples | 575 (37%) |

---

## 2. Shared Sub-Schema vs Inline: Tradeoffs

### Option A: Shared sub-schemas

```clojure
{:sub-schemas
 {:embedded-var  {:ns {:type :string} :name {:type :string} :library-url {:type :string}}
  :embedded-user {:login {:type :string} :account-source {:type :string} :avatar-url {:type :string}}}
 :entities
 {:example {:var {:type :ref :schema :embedded-var}
            :author {:type :ref :schema :embedded-user}}}}
```

**Pros:**
- Single place to update when shape changes (DRY)
- Makes the uniformity finding explicit — the schema *encodes* that all embedded vars are the same
- Maps directly to Datomic component entities or tuple types
- Validation script checks one definition, not N copies

**Cons:**
- Indirection — reader must look up the sub-schema definition
- Slightly more complex EDN structure

### Option B: Inline on each entity

```clojure
{:entities
 {:example {:var {:type :map :keys {:ns {:type :string} :name {:type :string} :library-url {:type :string}}}
            :author {:type :map :keys {:login {:type :string} ...}}}}}
```

**Pros:**
- Self-contained — each entity readable in isolation
- Simpler EDN structure

**Cons:**
- Same shape repeated 6+ times — change requires updating all
- Hides the uniformity finding (reader doesn't know all embedded vars are identical unless they compare)
- When shapes diverge during migration, inline copies drift silently

### Recommendation

**Shared sub-schemas.** The REPL proved uniformity, and the schema should encode that proof. If Datomic is chosen, these embedded docs will likely need to become component entities or refs — starting with shared schemas aligns with that direction.

---

## 3. Per-Attribute Metadata Fields

### Reference: Clojure's own var metadata

Clojure vars carry metadata with well-established conventions. Key fields across all 679 public vars in `clojure.core` (distinct from the 1,572 vars across all 38 namespaces in the ClojureDocs index):

| Meta Key | Count | Purpose |
|---|---:|---|
| `:ns` | 679 | Identity — which namespace |
| `:name` | 679 | Identity — symbol name |
| `:file` | 651 | Provenance — where defined |
| `:line` | 651 | Provenance — source location |
| `:doc` | 643 | Documentation |
| `:added` | 636 | Lifecycle — when introduced |
| `:arglists` | 632 | Interface — how to call |
| `:deprecated` | 4 | Lifecycle — end-of-life signal |
| `:tag` | 51 | Type hint |

**Patterns worth adopting:**
- **Provenance** (`:file`, `:line`) — we need `:source` and `:verified`
- **Lifecycle** (`:added`, `:deprecated`) — we need `:status` with richer semantics
- **Documentation** (`:doc`) — we need `:description`
- **Type** (`:tag`) — we need `:type`

### Proposed per-attribute fields

```clojure
{:key         :body           ;; attribute name (keyword)
 :type        :string         ;; data type
 :required?   true            ;; universal (true) or sparse (false, with :coverage)
 :coverage    nil             ;; e.g. 1708/2671 for sparse fields. nil when required
 :source      "mongodb"       ;; where the data lives: "mongodb", "jvm-heap", "edn-file"
 :description "The example code"
 :status      {:state :exists
               :verified "2026-06-09"
               :evidence "sidecar_repl.clj — key universe scan"}}
```

### Status field design

The `:status` field is a map, not a keyword, to carry provenance:

```clojure
;; Verified existing attribute
{:state    :exists
 :verified "2026-06-09"
 :evidence "sidecar_repl.clj — key universe scan"}

;; Gap identified in vision doc
{:state   :gap
 :vision  "2026vison.md#L42"      ;; line in vision doc
 :context "Verification pipeline"}  ;; which vision section

;; Planned work with tracking
{:state :planned
 :issue "https://github.com/nubank/clojuredocs/issues/55"
 :context "ExampleHistory body semantics"}
```

This makes every claim traceable: `:exists` attributes link to their REPL verification date, `:gap` attributes link to the vision doc line that calls for them, and `:planned` attributes link to the GitHub issue tracking the work.

---

## 4. Proposed EDN Structure

```clojure
{:schema-version "1.0.0"
 :generated      "2026-06-09"
 :issue           "https://github.com/nubank/clojuredocs/issues/43"

 :sub-schemas
 {:embedded-var
  {:ns         {:type :string  :description "Namespace name"}
   :name       {:type :string  :description "Var name"}
   :library-url {:type :string :description "Library GitHub URL"}}

  :embedded-user
  {:login          {:type :string :description "Username"}
   :account-source {:type :string :description "Auth provider (github or clojuredocs)"}
   :avatar-url     {:type :string :description "Avatar image URL"}}}

 :entities
 {:library    {;; attrs with full metadata ...}
  :namespace  {;; ...}
  :var        {;; ...}
  :example    {;; ...}
  :dialect-compat {;; var-dialect pair — exists today in dialect-compat.edn,
                   ;; gap attrs from vision (version, test-suite-url)}
  ;; etc.
  }}
```

Each entity is a map of keyword → attribute metadata. Each attribute carries the fields from section 3.

---

## 5. Open Questions

1. **Should `:_id` be modeled?** It's MongoDB infrastructure, present on every document. Options: include it with a `:mongodb-internal? true` flag, or omit it since it won't survive the Datomic migration.

2. ~~**Dialect compat**~~ **Resolved.** Model as its own entity now. The data already lives in its own file (`dialect-compat.edn`), not on Var documents — it's structurally an entity, not an attribute. Current keys (`:var-key`, `:dialects`) are `:exists`; vision keys (`:version`, `:test-suite-url`, `:verified-at`) are `:gap`. No "future promotion" step needed.

3. **Export contract**: `clojuredocs-export.json` has its own shape distinct from the MongoDB documents. Should it be modeled as a separate entity or a view?

4. **Indexes**: MongoDB indexes affect query performance but aren't part of the logical model. Include or defer?

## Version History

| Date | Changes |
|------|---------|
| 2026-06-09 | Initial RFC from REPL-verified discovery on `feat/43/entity-attribute-model` branch. |
| 2026-06-09 | Claims audit fixes: clarified error count scope, corrected Datomic/vision wording, fixed `:dialects` description, added EmbeddedUser count footnote, softened Datomic modeling assumptions, clarified `clojure.core` var population scope, dropped loose identity analogy. |
| 2026-06-09 | Added caveat that the MongoDB collection counts/coverage are a one-time snapshot and not test-guarded (see [#66](https://github.com/nubank/clojuredocs/issues/66)), to match the verification split now enforced by `entity_model_test.clj`. |