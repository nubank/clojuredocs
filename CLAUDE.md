# Project Guidelines

## Git

- The default remote is `upstream` (nubank/clojuredocs). Always use `upstream` for push, pull, and fetch operations unless explicitly told otherwise.
- The `zk` remote is the upstream fork origin and should not be pushed to.

## Project structure

ClojureDocs is a community-powered documentation site for Clojure. It's currently a Ring/Compojure web app backed by MongoDB, with server-side rendering via Hiccup and a ClojureScript (Reagent) client.

> **Note:** The site has known issues and data inconsistencies (see [.github/ISSUE_TEMPLATE/](.github/ISSUE_TEMPLATE/)). The architecture predates the [2026 vision statement](docs/2026vison.md) and will be replaced as part of that redesign.

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

Every prose Markdown document under `docs/` must carry a **YAML frontmatter block** at the very top of the file — before any heading — delimited by `---`. This format is conformant with the [Open Knowledge Format (OKF) v0.1](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md): the one hard requirement is a non-empty `type` field. Field *names and semantics* align with established RDF vocabularies — [Dublin Core Terms](https://www.dublincore.org/specifications/dublin-core/dcmi-terms/) (`dcterms:`) and [W3C PROV-O](https://www.w3.org/TR/prov-o/) (`prov:`) — so the same frontmatter is liftable to RDF triples without changing how it reads.

```yaml
---
type: RFC                              # REQUIRED (OKF) — dcterms:type. From the taxonomy below.
title: Entity-Attribute Model EDN Schema        # dcterms:title
description: One-line summary used by indexes, search, and previews.   # dcterms:description
tags: [entity-model, edn-schema, issue-43]       # dcterms:subject
created: 2026-06-09                    # dcterms:created (ISO 8601 date)
modified: 2026-06-09                   # dcterms:modified (was "Last updated")
source: https://github.com/nubank/clojuredocs/issues/43   # dcterms:source / OKF resource, when one exists
# --- provenance (PROV-O) ---
ai_assisted: "Claude Opus 4.8 via Claude Code"   # the prov:SoftwareAgent; the doc prov:wasGeneratedBy its run. Omit when not AI-assisted.
session: c6580eec                      # identifies the prov:Activity
tools: [Calva REPL, MongoDB seed data, workspace files]   # prov:used
agents_skills: []                      # permalinks to agent/skill definitions applied
# --- review trust (extension; no OKF/RDF native equivalent) ---
review_maturity: L3                    # machine-readable L0–L4 (see below)
review_note: human-verified via REPL evaluation
---
```

**Required:** `type`. **Recommended:** `title`, `description`, `tags`, `created`, `modified`. **Provenance (when AI-assisted):** `ai_assisted`, `session`, `tools`, `agents_skills`. **Review trust:** `review_maturity`, `review_note`. Consumers must tolerate unknown fields and unknown `type` values (OKF §9). Scope/caveat disclaimers go in the **body** (an italic line under the H1, or a `> **Caveat:**` callout) — not in frontmatter.

### Field → RDF vocabulary mapping

| Frontmatter key | RDF term | Meaning |
|---|---|---|
| `type` | `dcterms:type` | Nature/genre of the doc (also OKF's one required field) |
| `title` | `dcterms:title` | Name of the doc |
| `description` | `dcterms:description` | One-line account |
| `tags` | `dcterms:subject` | Topics |
| `created` | `dcterms:created` | Date of creation |
| `modified` | `dcterms:modified` | Date of last meaningful change |
| `source` | `dcterms:source` / `prov:wasDerivedFrom` | Resource the doc derives from |
| `ai_assisted` | `prov:wasAttributedTo` a `prov:SoftwareAgent` | The model/interface that generated the draft |
| `tools` | `prov:used` | What the generating activity used |
| `review_maturity` / `review_note` | extension; at L4 ≈ `dcterms:creator` / `prov:wasAttributedTo` a `prov:Person` | Human review trust |

The optional [`docs/context.jsonld`](docs/context.jsonld) is the canonical JSON-LD `@context` mapping these keys to their `dcterms:`/`prov:` IRIs; it is out-of-band, so the doc files stay plain YAML you can `cat`.

### `type` taxonomy

`Reference`, `Guide`, `RFC`, `Decision Log`, `Errata`, `Data Model`, `Diagram`, `Research`, `Review`, `Vision`. Descriptive and extensible — add new values when needed; don't repurpose existing ones. (`type` ≡ `dcterms:type`; this list is our controlled vocabulary.)

### Review maturity levels

Review maturity levels use a progressive trust model, similar in spirit to [C2PA](https://c2pa.org/)'s content credentials and the Linux kernel's review trailers. Each level subsumes the ones below it.

| Level | Label | Meaning |
|---|---|---|
| **L0** | AI-generated | No human review. Raw AI output. |
| **L1** | Human-directed | Human specified what to produce. Output not yet verified. |
| **L2** | Human-reviewed | Human read the output, corrected obvious errors. Claims not individually checked. |
| **L3** | Human-verified | Human verified specific claims against primary sources (running system, database, upstream docs). |
| **L4** | Human-endorsed | Human takes ownership. Content is treated as human-authored with AI assistance. |

Use the level in the `review_maturity` frontmatter key (machine-readable), with a human-readable `review_note`: `review_maturity: L2` / `review_note: human-reviewed via PR`.

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

### OKF bundle root

`docs/` is an OKF **bundle**. Its root [`docs/index.md`](docs/index.md) carries the only frontmatter an index file may have — `okf_version: "0.1"` — and lists the docs for progressive disclosure. `index.md` and `log.md` are OKF-reserved filenames; don't use them for content documents.

### Commit trailers

```
Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
Reviewed-by: Jordan Miller <jordan.miller@nubank.com.br>
```

`Co-Authored-By` identifies the AI, following the GitHub convention for multi-author commits. `Reviewed-by` identifies the human who reviewed the diff before commit, following the Linux kernel convention for review attribution. The `ai_assisted` frontmatter key (`prov:SoftwareAgent`) and the `Co-Authored-By` trailer carry the same fact in two places — keep them consistent.

### Rules

- Applies to **prose Markdown under `docs/`**. Repo-root config (`README.md`, `CLAUDE.md`) and `.github/` templates are out of scope; `.github/ISSUE_TEMPLATE/*` carry their own GitHub-mandated frontmatter — don't touch it.
- Data files (`*.edn`, `*.csv`) are not OKF concepts and need no frontmatter.
- When updating a document, bump `modified` and adjust `review_maturity` if the review status changed.
- Provenance is mandatory for AI-assisted docs. Citations to sources should be reproducible — commit-pinned GitHub permalinks for code, not branch names.
- Cross-links between docs use repo-relative paths (e.g. `../glossary.md`) for GitHub navigability — a deliberate, OKF-permitted (§5.2) relative-link choice over OKF's `/`-rooted recommendation.
- This replaces lengthy per-session attribution logs. The commit message carries what was done; the frontmatter carries provenance and review status. No separate `docs/ai/` attribution files needed.

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

## Code review guidance

Based on Clojure code review conventions used at Nubank.

### Review posture

Be helpful, specific, and low-noise. Comment only when a change is likely to cause incorrect behavior, broken tests, confusing API behavior, security/data integrity problems, or resource/lifecycle bugs. Avoid comments about personal style or speculative refactors.

### Clojure-specific

- Side effects are explicit and not mixed into pure logic
- Data shape expectations are clear at boundaries
- Nil handling matches intended behavior
- Lazy sequences don't escape into places where resources may be closed
- Exception handling preserves useful context
- Stateful code (atoms, refs, agents) has clear ownership and safe update semantics
- Namespace changes don't leave unused requires or circular dependencies

### What to flag

- Behavior changes without test coverage
- Functions that now accept/return a different shape without call-site updates
- Hidden coupling between namespaces
- Lazy seqs from I/O consumed after the source is closed
- Error handling that turns diagnosable failures into silent nils
- Concurrency logic that can duplicate work or lose updates

### What not to flag

- Formatting and whitespace
- Alternative naming unless actively misleading
- "Could be extracted" unless duplication is causing confusion
- Replacing one valid idiom with another

## Docs conventions

- Use relative links between docs in the same repo.
- Use GitHub permalinks (with commit hash) for code references — not branch names.
- First mentions of domain terms should link to the [glossary](docs/glossary.md).
- Diagrams use Mermaid fenced code blocks (rendered natively on GitHub).
- Mermaid diagrams have a corresponding miro board 
