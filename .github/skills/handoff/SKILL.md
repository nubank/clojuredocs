---
name: handoff
description: 'Write a structured state-of-work note to disk so you can /clear and resume in a fresh context instead of using /compact. Use at phase boundaries or when the session is long enough that reasoning quality is degrading (~100K tokens, the "smart zone" ceiling).'
user_invocable: true
arguments:
  - name: path
    description: Where to write the handoff note (optional; defaults to a handoffs directory)
    required: false
---

# Handoff

Capture the state of the work to disk so the conversation can be thrown away. Then `/clear` and resume in a fresh, full-quality context — instead of `/compact`, which keeps you in one ever-degrading window.

The premise (from Matt Pocock's AI-coding workshop): reasoning degrades as context grows, so a long session is a *liability*, not an asset. The fix is to externalize state to files and start fresh. A summary kept in-context still carries the rot; a note on disk does not.

## When to use

- At a natural phase boundary (a slice is done, a PR is shipped).
- When the session is long and answers are getting vaguer or repetitive.
- Before stepping away, so the work can resume cleanly later or by someone else.
- Any time you're tempted to `/compact` — handoff then `/clear` instead.

## Procedure

1. **Choose the path.** Use `$ARGUMENTS.path` if given; else default to `.claude/handoffs/<YYYY-MM-DD>-<slug>.md` (create the directory) or a repo-appropriate location. Prefer a stable, discoverable path.
2. **Write only what a fresh session needs.** The reader will re-read the actual files, so point at ground truth rather than reproducing it. Capture:
   - **Task / goal** — the objective, with links to the issue, PRD, or `/grill-me` plan.
   - **Current state** — what's done, what's in progress, and crucially what's *verified* vs *unverified*.
   - **Ground-truth pointers** — the source-of-truth files, tests, and key paths to read first. Paths, not paraphrases.
   - **Next steps** — the immediate next actions, concrete enough to act on cold.
   - **Open questions / pending decisions** — anything unresolved.
   - **Git state** — current branch, last commit, uncommitted changes, any stacked-PR context.
   - **Gotchas** — context that isn't obvious from the files (a non-obvious constraint, a dead end already ruled out, a flaky step).
3. **Keep it tight.** A handoff is a map, not a transcript. If you're reproducing file contents, stop and link the file instead.
4. **Be honest about what's unverified.** Mark claims that haven't been checked against a running system / tests, so the next session re-verifies rather than trusting them.
5. **Close out.** Tell the user it's safe to `/clear` now, and that resuming means: start a fresh session and read the handoff file first.

## Key constraints

- **Do not dump the conversation.** Capture decisions and state, not narrative history.
- **Point to files; don't transcribe them.** Ground truth lives on disk; the note routes the reader to it.
- **Don't overstate completion.** "Done and verified" and "written but untested" are different — say which.
- **One handoff = one resumable unit.** If two unrelated threads are open, write two notes.
