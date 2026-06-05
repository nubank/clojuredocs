# Copilot Instructions

All project conventions — including AI metadata, review maturity levels, PR format, and docs conventions — are defined in [CLAUDE.md](../CLAUDE.md) at the repo root. Follow those conventions for all code and documentation work.

Key points:

- **AI metadata**: Every prose doc under `docs/` needs an inline metadata block (see CLAUDE.md for format)
- **Review maturity**: Use L0–L4 levels to indicate how much human review has occurred
- **Unverified claims**: Mark with `[unverified]` inline
- **PRs**: Use conventional-commit prefixes (`feat:`, `fix:`, `docs:`, etc.)
- **Code references**: Use GitHub permalinks with commit hashes, not branch names
- **Git**: Push to `upstream` (nubank/clojuredocs), not `zk`

The site has known issues and data inconsistencies (see `docs/issues/`). The architecture is legacy — a major redesign is underway per `docs/2026vison.md`.
