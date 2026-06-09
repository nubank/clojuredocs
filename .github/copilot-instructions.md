# Copilot Instructions

Project conventions are defined in [CLAUDE.md](../CLAUDE.md) at the repo root. The key points below are the complete set of conventions you must follow. Refer to CLAUDE.md for full details when its content is available in context.

Key points:

- **AI metadata**: Every prose doc under `docs/` needs an inline metadata block (see CLAUDE.md for format)
- **Review maturity**: Use L0–L4 levels to indicate how much human review has occurred
- **Unverified claims**: Mark with `[unverified]` inline
- **PRs**: Use conventional-commit prefixes (`feat:`, `fix:`, `docs:`, etc.)
- **Code reviews**: Follow Clojure-specific review guidance in CLAUDE.md — flag behavior changes without tests, lazy seqs escaping resource scopes, silent nil swallowing; skip style preferences
- **Code references**: Use GitHub permalinks with commit hashes, not branch names
- **Git**: Push to `upstream` (nubank/clojuredocs), not `zk`

The site has known issues and data inconsistencies (see `docs/issues/`). The architecture is legacy — a major redesign is underway per `docs/2026vison.md`. When modifying existing code, preserve current patterns for consistency unless the change is explicitly part of the redesign. Do not proactively refactor legacy code toward the new architecture unless asked.
