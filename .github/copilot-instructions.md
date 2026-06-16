# Copilot Instructions

Project conventions are defined in [CLAUDE.md](../CLAUDE.md) at the repo root. The key points below summarize the required conventions; when CLAUDE.md is available in context, treat it as the source of truth for the full set.

Key points:

- **AI metadata**: Every prose doc under `docs/` needs a YAML frontmatter block ([OKF](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md)-conformant: required `type`) with field semantics aligned to Dublin Core Terms + PROV-O (see CLAUDE.md for the format and [docs/rfcs/okf-metadata-rfc.md](../docs/rfcs/okf-metadata-rfc.md) for rationale)
- **Review maturity**: Use the `review_maturity` frontmatter key (L0–L4) to indicate how much human review has occurred
- **Unverified claims**: Mark with `[unverified]` inline
- **PRs**: Use conventional-commit prefixes (`feat:`, `fix:`, `docs:`, etc.)
- **Code reviews**: Follow Clojure-specific review guidance in CLAUDE.md — flag behavior changes without tests, lazy seqs escaping resource scopes, silent nil swallowing; skip style preferences
- **Code references**: Use GitHub permalinks with commit hashes, not branch names
- **Git**: Push to `upstream` (nubank/clojuredocs), not `zk`

The site has known issues and data inconsistencies (see `.github/ISSUE_TEMPLATE/`). The architecture predates the redesign described in `docs/2026vison.md`. When modifying existing code, preserve current patterns for consistency unless the change is explicitly part of the redesign. Do not proactively refactor legacy code toward the new architecture unless asked.
