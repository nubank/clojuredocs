---
type: Diagram
title: "ClojureDocs Entity-Attribute ER Diagram"
description: "Crow's-foot ER diagram of the ClojureDocs entity-attribute model, generated from the EDN source of truth."
tags: [entity-model, er-diagram, mermaid, issue-43]
created: 2026-06-09
modified: 2026-06-09
source: https://github.com/nubank/clojuredocs/issues/43
ai_assisted: "Claude Opus 4.8 via Claude Code"
tools: [babashka, EDN source of truth, tools/edn_to_mermaid.clj]
agents_skills: []
review_maturity: L1
review_note: "generated from the L3-verified EDN; diagram structure not separately reviewed"
---

# ClojureDocs Entity-Attribute ER Diagram

*Auto-generated from [`docs/entity-attribute-model.edn`](../entity-attribute-model.edn) by [`tools/edn_to_mermaid.clj`](../../tools/edn_to_mermaid.clj). Do not edit by hand — regenerate with `bb tools/edn_to_mermaid.clj`.*

## Sources

- **Source of truth:** [`docs/entity-attribute-model.edn`](../entity-attribute-model.edn) — the data model (verification scope noted in its header).
- **Narrative model:** [`docs/entity-attribute-model.md`](../entity-attribute-model.md) — entity descriptions and verification.
- **Design rationale:** [`docs/rfcs/entity-model-rfc.md`](../rfcs/entity-model-rfc.md).
- **Issue:** [nubank/clojuredocs#43](https://github.com/nubank/clojuredocs/issues/43).
- **Notation:** [Mermaid entity-relationship diagrams](https://mermaid.js.org/syntax/entityRelationshipDiagram.html).

## Diagram

```mermaid
erDiagram
    DIALECT_COMPAT ||--|| VAR : "compatibility of"
    EXAMPLE }o--|| USER : "authored by"
    EXAMPLE }o--o{ USER : "edited by"
    EXAMPLE }o--|| VAR : "documents"
    EXAMPLE_HISTORY }o--|| EXAMPLE : "edit of"
    EXAMPLE_HISTORY }o--|| USER : "edited by"
    LEGACY_VAR_REDIRECT }o--|| VAR : "redirects to"
    NAMESPACE }o--|| LIBRARY : "part of"
    NOTE }o--|| USER : "authored by"
    NOTE }o--|| VAR : "annotates"
    SEE_ALSO }o--|| USER : "authored by"
    SEE_ALSO }o--|| VAR : "from"
    SEE_ALSO }o--|| VAR : "to"
    VAR }o--|| LIBRARY : "from library"
    VAR }o--|| NAMESPACE : "in namespace"

    DIALECT_COMPAT {
       set dialects "Set of dialect keywords, e.g. # :bb :clj :cljs"
       string test_suite_url "[gap] URL to test suite that verified compatibility"
       string var_key "Qualified var name, e.g. clojure.core/map"
       instant verified_at "[gap] When compatibility was last verified"
       string version "[gap] Clojure version when compatibility was last verified"
    }
    EXAMPLE {
       object_id _id PK "MongoDB ObjectId"
       embedded_user author FK "User who created the example"
       string body "The example source code"
       integer created_at "Unix epoch millis when created"
       integer deleted_at "Unix epoch millis when soft-deleted. nil for active examples."
       vector editors "Users who have edited this example (may contain duplicates - see ..."
       integer updated_at "Unix epoch millis when last updated"
       embedded_var var FK "Which var this example is for"
    }
    EXAMPLE_HISTORY {
       object_id _id PK "MongoDB ObjectId"
       string body "The NEW body after edit (not the old body - see issue for bug)"
       integer created_at "Unix epoch millis when this edit occurred"
       embedded_user editor FK "User who made this edit"
       object_id example_id "ObjectId of the example this history entry belongs to"
    }
    LEGACY_VAR_REDIRECT {
       object_id _id PK "MongoDB ObjectId"
       integer function_id "Legacy numeric function ID"
       string library_url "Library GitHub URL"
       string name "Var name"
       string ns "Namespace name"
    }
    LIBRARY {
       string gh_tag_url "GitHub tag URL for the release"
       string library_url "GitHub repo URL"
       vector namespaces "Vector of namespace maps (38 entries). Implicitly a ref to :names..."
       string source_base_url "GitHub blob URL prefix for source links"
       vector vars "Vector of var maps (1,572 entries). Implicitly a ref to :var (PR ..."
       string version "Clojure version string, e.g. 1.12.4"
    }
    NAMESPACE {
       string added "Version when namespace was added. Only clojure.pprint and clojure..."
       string doc "Namespace docstring. Nil for 6 namespaces (core.logic.*, core.pro..."
       string name "Fully qualified namespace name"
    }
    NOTE {
       object_id _id PK "MongoDB ObjectId"
       embedded_user author FK "User who created the note"
       string body "Note text (markdown)"
       integer created_at "Unix epoch millis when created"
       integer updated_at "Unix epoch millis when last updated"
       embedded_var var FK "Which var this note is for"
    }
    SEE_ALSO {
       object_id _id PK "MongoDB ObjectId"
       embedded_user author FK "User who created this see-also"
       integer created_at "Unix epoch millis when created"
       embedded_var from_var FK "Source var of the see-also relationship"
       embedded_var to_var FK "Target var of the see-also relationship"
    }
    USER {
       object_id _id PK "MongoDB ObjectId"
       string account_source "Auth provider: github or clojuredocs"
       string avatar_url "Avatar image URL"
       string login "Username"
    }
    VAR {
       string added "Version when this var was added"
       list arglists "List of argument list strings"
       integer column "Source column number"
       string deprecated "Deprecation version string"
       string doc "Docstring"
       boolean dynamic "True if this var is dynamic (e.g. *out*)"
       string file "Source file path relative to project root"
       list forms "Usage form strings for special forms"
       string href "URL path for this var's page, e.g. /clojure.core/map"
       string library_url "Library GitHub URL (denormalized from Library)"
       integer line "Source line number"
       boolean macro "True if this var is a macro"
       string name "Var name (stringified)"
       boolean no_doc "Documentation-visibility flag. Listed in search/var-keys, so the ..."
       string ns "Namespace name (stringified)"
       boolean skip_wiki "True if var should be hidden from documentation"
       boolean special_form "True for let, fn, letfn, loop"
       boolean static "Legacy AOT-compilation hint. Present on 319 vars but no longer me..."
       string tag "Return type hint (stringified from class)"
       string type "Observed values on search/clojure-lib: function (1225), macro (19..."
       string url "Present only on letfn with nil value. Vestigial annotation from C..."
    }
```

## Key / Legend

Relationships use Mermaid crow's-foot notation, read left-to-right as `FROM token TO`. The cardinality sits on the side it describes: `}o` = zero-or-more, `||` = exactly-one, `o{` = zero-or-more on the right.

| Cardinality token | Meaning (`from`–`to`) |
|---|---|
| `}o--o{` | many-to-many |
| `}o--||` | many-to-one |
| `||--||` | one-to-one |

| Attribute marker | Meaning |
|---|---|
| `PK` | Primary key (`:_id` — MongoDB ObjectId). |
| `FK` | Reference to another entity. Some are stored as embedded sub-documents that inline fields from the target (`:embedded-var`, `:embedded-user`); others as ObjectId join keys (e.g. `EXAMPLE_HISTORY.example_id`). |
| `[gap]` prefix | Attribute described by the [2026 vision](../2026vison.md) but not yet present in code/data (`:status :gap`). |

Each box lists the entity's present-state attributes (`:status :exists`); the `:_id` row is shown first, the rest alphabetically. JVM-heap and EDN-file entities are test-guarded; MongoDB entities are snapshot-derived and not yet test-guarded ([#66](https://github.com/nubank/clojuredocs/issues/66)). Embedded sub-schemas (`:embedded-var`, `:embedded-user`) are stored inline on the parent document and are not drawn as separate boxes — the `FK` edges point at the canonical [`VAR`](../glossary.md#v)/[`USER`](../entity-attribute-model.md) entities whose fields they embed.

## Provenance & Review

This file is generated; its version is the EDN's `:generated` date (2026-06-09), carried into the frontmatter. Errata are tracked centrally in [`errata.md`](../errata.md). Review notes: [`entity-attribute-er_research-review_run_1.md`](entity-attribute-er_research-review_run_1.md).
