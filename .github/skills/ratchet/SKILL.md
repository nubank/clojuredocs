---
name: ratchet
description: 'Harden an informal finding, convention, or manual check into progressively more deterministic enforcement: LLM → REPL → Library → Enforcement. Use when you catch yourself repeating a check by hand, or relying on the model to "remember" a rule.'
user_invocable: true
arguments:
  - name: finding
    description: The informal finding, rule, or check to harden (optional; Claude will ask)
    required: false
---

# Ratchet

Move a thing you currently trust on vibes one rung up the reliability ladder, toward something a machine enforces. LLMs will happily stay at rung one forever — restating a rule in prose every time — so the human has to decide when to advance.

The ladder: **LLM → REPL → Library → Enforcement.**

- **LLM** — the rule lives in a prompt, a doc, or your head. Re-checked by re-reasoning; drifts silently.
- **REPL** — you can reproduce the check in a snippet/command. Repeatable, but run by hand.
- **Library** — the check is a function, schema, or data file. A deterministic artifact, importable and testable.
- **Enforcement** — the artifact is wired into a test, lint, CI job, or git hook that *fails* on violation. Correctness is now guarded, not hoped for.

Documentation is for humans deciding whether to adopt; enforcement is for ensuring correctness. They are not substitutes.

## When to use

- You've consulted the same convention by hand more than once.
- You're relying on the model to remember a rule across turns or sessions.
- A finding was just verified in a REPL and risks rotting back into prose.
- A review keeps catching the same class of mistake.

## Procedure

1. **State the finding** (`$ARGUMENTS.finding` or ask) and **locate its current rung** — is it prose, a one-off snippet, an artifact, or already enforced?
2. **Name the next rung and the move to reach it:**
   - LLM → REPL: write a reproducible snippet/command that decides it (pass/fail), not a paragraph describing it.
   - REPL → Library: extract the check into a function, schema, or data file — a single deterministic source of truth.
   - Library → Enforcement: wire it into a test / lint rule / CI step / pre-commit hook that fails on violation, and emit an actionable message ("bad date format — try padding to YYYY-MM-DD").
3. **Recommend the highest rung worth reaching now** — not always the top. Weigh cost vs. how often the check runs and how expensive a miss is. A one-time check may stop at REPL; a load-bearing invariant should reach Enforcement.
4. **Offer to implement the next step** (with approval). Leave a clear trail: what was hardened, from which rung to which, and where the artifact lives.
5. **Watch for the smell:** if you keep consulting a reference to apply a rule correctly, that's the signal to harden it into a deterministic check rather than document it more thoroughly.

## Key constraints

- **Advance one rung at a time** unless a higher jump is clearly cheap. Each rung must actually work before the next.
- **Enforcement needs a good error message.** A failing check that doesn't say why just moves the guessing.
- **Don't over-ratchet.** Not every observation deserves a CI gate; match the rung to the stakes.
- **Prefer one source of truth.** When you reach Library/Enforcement, the test and the doc should reference the same artifact, not restate the rule independently.
