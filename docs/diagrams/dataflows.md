---
type: Diagram
title: "ClojureDocs Data Flows"
description: "Data flows through the ClojureDocs app, traced from source code."
tags: [diagrams, dataflow, architecture]
created: 2026-06-01
modified: 2026-06-05
ai_assisted: "Claude Opus 4.6 via GitHub Copilot"
session: "41bcf361"
tools: "GitHub MCP, workspace file access"
agents_skills: [backseat-driver]
review_maturity: L1
review_note: "human-directed, not yet verified against running system"
---

# ClojureDocs Data Flows

> _AI-assisted document. Flows were traced by reading source code — verify against a running instance for runtime accuracy._

## 1. Startup Flow

```mermaid
flowchart TD
    A([JVM boot]) --> B["Load Clojure namespaces<br/>from static list<br/>(search/static.clj)"]
    B --> C["ns-publics<br/>gather var metadata"]
    C --> D["Transform metadata"]
    D --> E[("In-memory: clojure-lib")]
    E --> F["Build Lucene<br/>in-memory search index"]
    F --> G([Ready to serve])
```

## 2. Request Flow

```mermaid
flowchart TD
    A([User visits var page]) --> B["Look up var in<br/>in-memory index"]
    B --> C{"Filter keys:<br/>ns, name, library-url"}
    C --> D["Query examples<br/>from MongoDB"]
    C --> E["Query notes<br/>from MongoDB"]
    C --> F["Query see-alsos<br/>from MongoDB"]
    D --> G["Assemble page"]
    E --> G
    F --> G
    B --> G
    G --> H([Render var page])

    DB[(MongoDB)] -.-> D
    DB -.-> E
    DB -.-> F
```

## 3. Write Flow

```mermaid
flowchart TD
    A([Authenticated user]) --> B["POST / PATCH / DELETE<br/>via API"]
    B --> C{Resource type}
    C -->|Example| D["Write to MongoDB"]
    C -->|Note| D
    C -->|See-also| D
    D --> E{"Was it an<br/>example edit?"}
    E -->|Yes| F["Create<br/>ExampleHistory record"]
    E -->|No| G([Done])
    F --> G

    DB[(MongoDB)] -.-> D
    DB -.-> F
```

## 4. Export Flow

```mermaid
flowchart TD
    A([Timer: every 6 hours]) --> B["run-export"]
    B --> C["Iterate all<br/>in-memory vars"]
    C --> D["For each var:<br/>query examples,<br/>notes, see-alsos"]
    D --> E["Write denormalized<br/>JSON file"]
    E --> F([Consumed by editor plugins<br/>Calva, CIDER, etc.])

    DB[(MongoDB)] -.-> D
    MEM[("In-memory vars<br/>clojure-lib")] -.-> C
```