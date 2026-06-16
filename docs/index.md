---
okf_version: "0.1"
---

# ClojureDocs Documentation Bundle

Progressive-disclosure index for the `docs/` [OKF](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md) bundle. Each entry links a document and its one-line description. See [README.md](README.md) for the human landing page and [CLAUDE.md](../CLAUDE.md) for the metadata convention.

# Project

* [2026 Vision](2026vison.md) - Two-year vision and redesign direction.
* [Glossary](glossary.md) - Domain terms scoped to this codebase.
* [Documentation map](README.md) - Human landing page for the docs.

# Conventions & process

* [RFC: OKF + RDF document metadata](rfcs/okf-metadata-rfc.md) - Proposal to adopt OKF YAML frontmatter with RDF-aligned semantics.
* [Decision Log](decisions.md) - Design and architecture decisions; lightweight ADRs.
* [Release & Announcement Protocol](release-protocol.md) - Status-quo audit of the ship/announce process.
* [GitHub Templates](github-templates/README.md) - Issue and PR templates and conventions.
* [Issue Writing Guide](github-templates/issue-writing-guide.md) - How to write issues; doubles as an AI prompt.

# Setup & architecture

* [Dev Environment Setup](dev-setup.md) - Local development environment notes.
* [VS Code + Calva](dev-setup-vscode.md) - Editor-specific setup for Calva users.
* [Multi-Library :library-url chain](lib-layers.md) - How the `:library-url` property is resolved across layers.

# Research

* [Data Model Coupling Audit](research/data-model-coupling-audit.md) - Audit of data-model coupling in the current architecture.
* [Cross-Dialect Compatibility Planning](research/issue-30-dialect-compat-planning.md) - Planning for Clojure/CLJS/babashka compatibility indicators (issue #30).
* [Issue #30 Planning Prompt](research/30-dialect-prompt.md) - Claude Code prompt that generated the issue #30 planning doc.
