# Skills

Project-level [Claude Code skills](https://docs.claude.com/en/docs/claude-code/skills) for this repo. Each
skill is a directory with a `SKILL.md`. User-invocable skills are triggered with `/<name>`.

| Skill | Use |
|---|---|
| [`issue-writer`](issue-writer/SKILL.md) | Draft GitHub issues that diagnose rather than prescribe. |
| [`grill-me`](grill-me/SKILL.md) | Alignment-first interview before building; writes a plan/PRD to disk. |
| [`handoff`](handoff/SKILL.md) | Write a resumable state-of-work note so you can `/clear` instead of `/compact`. |
| [`ratchet`](ratchet/SKILL.md) | Harden an informal check toward enforcement: LLM → REPL → Library → Enforcement. |
| [`narrate`](narrate/SKILL.md) | Reframe a finished session as a short story for a human (recap, retro, demo). |

## Session discipline

`grill-me`, `handoff`, and `ratchet` operationalize the "smart zone" argument for disciplined AI coding —
reasoning degrades as context grows, so front-load alignment, externalize state to disk, and start fresh
rather than dragging a long, degrading context forward. They are adapted from
[Matt Pocock's AI-coding workshop](https://finance.biggo.com/news/e7209c094224b09c) and this repo's own
reliability-ratchet convention (see the [glossary](../../docs/glossary.md)).

A typical loop: `/grill-me` to align and write a plan → implement one vertical slice → ship → `/handoff` →
`/clear` → resume from the handoff for the next slice. Reach for `/ratchet` whenever a check starts repeating
by hand, and `/narrate` to recap a finished session as a short, human-readable story.
