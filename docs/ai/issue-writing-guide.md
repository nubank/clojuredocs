# Issue Writing Guide

How to write GitHub issues for [nubank/clojuredocs](https://github.com/nubank/clojuredocs).

This guide describes the format conventions used in this repo's issue tracker. It doubles as a prompt for AI assistants.

## Using this guide as an AI prompt

Copy the **Prompt** section below into any AI assistant (ChatGPT, Claude, Copilot, etc.) as a system prompt or paste it at the start of a conversation. Then give it a rough description of a bug or feature idea — a Slack thread, a conversation summary, a one-liner — and it will produce an issue formatted for this repo.

If you use VS Code with GitHub Copilot, this guide is also available as a [skill](.github/skills/issue-writer/SKILL.md) that can be invoked with `/issue-writer` or loaded automatically when the agent detects a relevant context.

<details>
<summary><strong>Prompt</strong> (click to expand)</summary>

```
You draft GitHub issues for the nubank/clojuredocs repository (https://github.com/nubank/clojuredocs), the codebase behind clojuredocs.org.

Your job: take a rough description of a bug, friction point, or improvement idea and turn it into a well-structured issue. You diagnose; you do not prescribe. The issue should clarify the problem and frame the inquiry, not prescribe a solution.

Rules:
- Frame, don't solve. No implementations, function names, file layouts, or code changes.
- Show, don't tell. Link to clojuredocs.org pages, repo file paths, or reproducible steps.
- Neutral, declarative prose. No exclamation points, no emoji, no hype language.
- Be honest about uncertainty. If you don't know whether something is a bug or intended behavior, say so.
- No "Acceptance Criteria", "Risks & Mitigations", or "Implementation Steps" sections.
- Omit sections that don't apply. Don't pad.

Format:

# <Title — short, descriptive>

## Bug / Enhancement / Feature (if helpful)

## Problem
2–6 sentences. User-visible effect, then consequence.

## Demonstration (or "Steps to Reproduce" for bugs)
Numbered steps with URLs, var names, console output as relevant.

## Father Watson Questions

**What do we know?**
- Bullet established facts, code paths, prior discussion.

**What do we need to know?**
- Bullet open questions that would unblock a decision.

**Where are we?**
- Current state of the relevant code or feature.

**Where are we going?**
- Desired outcomes in user-facing terms, not implementation details.

## Assumptions (optional, only if load-bearing)

## References
- Related issues (#N), repo file paths, external docs, clojuredocs.org pages.
```

</details>

## Philosophy

**Diagnose; do not prescribe.** An issue should make a problem legible and the unknowns explicit. Maintainers and contributors decide how to fix things — the issue's job is to frame the inquiry, not prescribe a solution.

## Principles

1. **Frame, don't solve.** Describe the problem and the open questions. Leave implementation to whoever picks up the work.
2. **Show, don't tell.** Link to a specific page on clojuredocs.org, a file path in the repo, console output, or a reproducible sequence of steps.
3. **Match the house style.** Neutral, declarative prose. No exclamation points, no marketing language, no "Hi team!" preambles. Get to the point.
4. **Be honest about uncertainty.** If you don't know whether something is a bug or intended behavior, say so rather than guessing.

## Issue template

Use this skeleton, omitting sections that don't apply:

```markdown
# <Issue title — short, descriptive, no clickbait>

<Optional one-line type tag like `## Bug` or `## Feature` or `## Enhancement` if it helps>

## Problem

<2–6 sentences. What is wrong or missing, from a user's perspective. Why does it matter?
Cite the relevant page on clojuredocs.org or file in the repo where useful.>

## Demonstration

<Concrete, reproducible evidence. For bugs, "Steps to Reproduce" works better
as the section name. Include URLs, exact var names, console output, screenshots if relevant.>

1. <step>
2. <step>
3. <observed result>

## Father Watson Questions

**What do we know?**
<Bullet the established facts. Behavior observed, code paths involved, prior discussion,
related issues. Cite specific files or links where you can.>

**What do we need to know?**
<Bullet the open questions. What information would unblock a decision? What hasn't been
investigated yet? What would a maintainer reasonably need to ask before triaging this?>

**Where are we?**
<The current state of the relevant code or feature. What exists today, what's already
in place, what the user-facing behavior is right now.>

**Where are we going?**
<The desired end state, framed as outcomes rather than implementation. What would success
look like for the user? What invariants should hold afterward?>

## Assumptions (optional)

<Only include if there are non-obvious assumptions baked into the framing of the issue.
Skip this section entirely if there's nothing to flag.>

## References

- <link to related issue, e.g. `#5`>
- <link to relevant file path in the repo>
- <link to external docs, cheatsheets, or specs>
```

## Section guidance

### Title

Short, neutral, scannable.

| Good | Bad |
|------|-----|
| `Undocumented vars in Clojure are implementation and should be hidden in clojuredocs` | `BUG!! Notes are broken please fix ASAP` |
| `Add Note button on var pages remains disabled after typing text into the note editor` | `Feature idea: make the site better` |
| `[Enhancement] Cross-Dialect Compatibility Indicators` | |

Bracketed type tags like `[Enhancement]` or `[Bug]` are used but optional. Use them when the title alone wouldn't make the type obvious.

### Problem

Lead with the user-visible effect, then the consequence. Pretend the reader has never seen the issue before. Do not editorialize about severity unless it's load-bearing for triage.

### Demonstration / Steps to Reproduce

For **bugs**: numbered steps that a maintainer can follow in a fresh browser session, ending with the observed broken behavior. Include console output verbatim in fenced code blocks.

For **features and enhancements**: concrete examples of the current gap. E.g. "Visit clojuredocs.org/clojure.core/pmap — nothing on the page indicates that pmap does not exist in ClojureScript."

### Father Watson Questions

These four questions replace the "Proposed Solution / Implementation Steps / Acceptance Criteria" pattern. They keep the issue in the **diagnostic phase** rather than jumping to design.

- **What do we know?** — Established facts. Evidence, code references, prior conversation.
- **What do we need to know?** — Open questions. Investigations not yet done. Decisions deferred to maintainers.
- **Where are we?** — Current state. The baseline. Different from "what do we know" — this is the snapshot, not the inventory of facts.
- **Where are we going?** — Desired outcomes, in user-facing terms. Not "add a function called X" but "users can tell at a glance whether a var works in their dialect."

Use bullet points under each question. Keep each bullet to a single idea. If a question doesn't apply, write a single sentence explaining why rather than padding.

### Assumptions

Only include if there's a load-bearing assumption that, if false, would change the framing of the issue. E.g. "Assumes the runtime-var-access constraint from #5 still holds." Skip otherwise — empty Assumptions sections are noise.

### References

Link to:
- Related issues by number (`#5`)
- Repo file paths (`src/clj/clojuredocs/pages/vars.clj`)
- External docs (Clojure docs, ClojureScript cheatsheet, babashka book, etc.)
- The relevant clojuredocs.org page

## What does NOT belong in an issue

- Specific implementations, function names, file layouts, or code changes
- "Acceptance Criteria" checklists — that's for whoever picks up the work
- "Risks & Mitigations" sections — those are design-phase artifacts
- Scope inflation by speculating about related improvements
- Hype language, emoji, or exclamation points

## Example

**Rough input:**

> The search bar on clojuredocs autocompletes really slowly, especially on mobile. I think it's making a network request on every keystroke.

**Issue:**

```markdown
# Search autocomplete feels slow, especially on mobile

## Bug

## Problem

The search bar on clojuredocs.org returns autocomplete suggestions with noticeable
latency. The effect is most pronounced on mobile, where each keystroke appears to incur
a round-trip delay. This makes the primary discovery surface of the site feel sluggish.

## Steps to Reproduce

1. Open clojuredocs.org on a mobile device (or throttled connection in devtools)
2. Tap the search input at the top of the page
3. Type `map` one character at a time
4. Suggestions update with a perceptible delay after each keystroke, rather than
   feeling instant

## Father Watson Questions

**What do we know?**
- Autocomplete latency is user-visible on mobile and on throttled connections
- The site exposes a search endpoint that the autocomplete UI calls
- Other Clojure documentation sites (e.g. cljdoc) handle this with client-side prefix
  matching against a preloaded index

**What do we need to know?**
- Is the autocomplete firing one request per keystroke, or is it debounced?
- Is the request hitting the server every time, or is there a cache layer in front?
- How large is the namespace/var index — would it be feasible to ship to the client
  and search locally?
- Is there server-side timing data (e.g. in Matomo, see #24) that would quantify
  the latency?

**Where are we?**
- Autocomplete is implemented via server-side requests on input change
- No client-side index is loaded at page start

**Where are we going?**
- Typing in the search box feels instant on mobile and on slow connections
- The search experience does not degrade meaningfully as network conditions worsen

## References

- clojuredocs.org search bar (top-of-page autocomplete)
- #24 Investigate User Behavior using Matomo details
```

---

When in doubt, prefer fewer words and more links.
