# Project Guidelines

## Git

- The default remote is `upstream` (nubank/clojuredocs). Always use `upstream` for push, pull, and fetch operations unless explicitly told otherwise.
- The `zk` remote is the upstream fork origin and should not be pushed to.

## Project structure

ClojureDocs is a community-powered documentation site for Clojure. It's currently a Ring/Compojure web app backed by MongoDB, with server-side rendering via Hiccup and a ClojureScript (Reagent) client.

> **Note:** The site has known issues and data inconsistencies (see [docs/issues/](docs/issues/)). The architecture is legacy — a major redesign is underway to align with the [2026 vision statement](docs/2026vison.md).

| Directory | Contents |
|---|---|
| `src/clj/` | Server-side Clojure — routes, pages, data layer, search |
| `src/cljs/` | Client-side ClojureScript — Reagent components |
| `src/cljc/` | Shared code (util, schemas) |
| `resources/` | Static assets, nginx config |
| `data/mongodb/` | Seed data (BSON dumps) |
| `docs/` | Design docs, research, diagrams, glossary |
| `bin/` | Dev scripts (`dev`, `prod`, `db-reset`, etc.) |
| `tools/` | One-off scripts (import, export, sanity checks) |

## Dev environment

```bash
mongod --dbpath ./dev-db      # start MongoDB
source bin/.devenv             # load env vars
bin/dev                        # start REPL + Figwheel
```

See [docs/dev-setup.md](docs/dev-setup.md) for full setup.

## AI metadata on documents

Every prose document under `docs/` must carry an inline metadata block at the top — before the first heading content — using this format:

```markdown
> **Document metadata**
> - **Created:** YYYY-MM-DD
> - **Last updated:** YYYY-MM-DD
> - **Tags:** comma, separated, tags
> - **AI-assisted:** Yes — model + interface (e.g. "Claude Opus 4.6 via GitHub Copilot")
> - **Session:** `session-id` (from debug log or Copilot session)
> - **Tools:** MCP servers and capabilities available (e.g. "GitHub MCP, workspace files")
> - **Agents/skills:** links to agent or skill definitions applied
> - **Review maturity:** L0–L4 level + short description
>
> _AI-assisted document. [Scope-specific disclaimer about what to verify.]_
```

### Review maturity levels

Inspired by C2PA's progressive trust model (Well-Formed → Valid → Trusted) and the Linux kernel's `Reviewed-by` / `Tested-by` conventions. Each level subsumes the ones below it.

| Level | Label | Meaning |
|---|---|---|
| **L0** | AI-generated | No human review. Raw AI output. |
| **L1** | Human-directed | Human specified what to produce. Output not yet verified. |
| **L2** | Human-reviewed | Human read the output, corrected obvious errors. Claims not individually checked. |
| **L3** | Human-verified | Human verified specific claims against primary sources (running system, database, upstream docs). |
| **L4** | Human-endorsed | Human takes ownership. Content is treated as human-authored with AI assistance. |

Use the level number in the metadata block: `**Review maturity:** L2 — human-reviewed via PR`.

### Section-level review markers

Use HTML comments to mark which sections a human has reviewed and when. These are invisible in rendered Markdown but visible in source:

```markdown
<!-- reviewed: jordan.miller, 2026-06-01 — ER diagram, entity descriptions -->
## Entity: Example
...
```

Sections without a review comment are implicitly at the document's base review level. Sections with a comment may be at a higher level than the document default.

### Unverified claims

Mark claims the AI made that haven't been checked against a primary source with `[unverified]` inline. Remove the marker once someone verifies the claim against a running system, database query, or upstream owner.

```markdown
Each Example document stores a `:created-at` timestamp. [unverified]
```

This is the Markdown equivalent of Wikipedia's `[citation needed]` and C2PA's `reviewRatings` — it makes the verification gap visible to readers rather than hiding it behind uniform confidence.

### Metadata field reference

- **AI-assisted** — model name + version + interface. The `Co-Authored-By` trailer in git carries this too, but it's invisible in rendered docs.
- **Session** — Copilot/Claude session ID. Ephemeral (won't resolve after the session ends), but useful as a correlation key for the AI usage log and debug logs.
- **Tools** — MCP servers and capabilities the AI had access to. "Claude with GitHub MCP + Confluence access" is a different provenance story than "Claude with only workspace files." A reader assessing reliability needs to know whether the AI could verify claims against primary sources.
- **Agents/skills** — Permalink to the agent or skill definition files that were active. Different skills have different reliability profiles.
- **Review maturity** — L0–L4 level. The level is a machine-readable prefix; the description after the dash is human-readable context.

### Commit trailers

```
Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
Reviewed-by: Jordan Miller <jordan.miller@nubank.com.br>
```

`Co-Authored-By` identifies the AI. `Reviewed-by` identifies the human who reviewed the diff before commit. This follows the Linux kernel convention for review attribution in git history.

### Rules

- CSVs and data files don't need metadata blocks — only prose Markdown.
- When updating a document, bump **Last updated** and adjust **Review maturity** if the review status changed.
- This replaces lengthy per-session attribution logs. The commit message carries what was done; the document metadata carries the review status. No separate `docs/ai/` attribution files needed.

## PR conventions

### Titles

Use conventional-commit prefixes: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`. Keep titles under ~70 characters; details go in the body.

### Body

Four sections, in this order:

1. **Context.** What triggered the change. Link the issue if there is one.
2. **Problem.** What is broken, missing, or insufficient today.
3. **Solution.** What the change does. Highlight rationale only for non-obvious decisions.
4. **Father Watson Questions.** In a collapsible `<details>` block. What do we know, what do we need to know, where are we, where are we going. Use when the change involves open questions or diagnostic framing.

Tone: direct and assertive. Short and specific.

Don't:

- Use marketing language ("comprehensive", "robust", "production-ready").
- Restate the diff line by line — the reviewer can read it.
- Pad a simple change with paragraphs. If you need walls of text to describe one decision, the decision isn't clear yet.

See the [PR template](.github/pull_request_template.md) for the standard body skeleton.

## Docs conventions

- Use relative links between docs in the same repo.
- Use GitHub permalinks (with commit hash) for code references — not branch names.
- First mentions of domain terms should link to the [glossary](docs/glossary.md).
- Diagrams use Mermaid fenced code blocks (rendered natively on GitHub).
- Mermaid diagrams have a corresponding miro board 
