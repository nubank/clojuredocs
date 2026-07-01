---
name: narrate
description: 'Reframe the current work session as a short narrative for a human, fitting a classic story arc (hero''s journey, man in a hole, three-act, Vonnegut shapes, story circle). Use for a recap, retro, demo, or changelog where a memorable story beats a status list. The arc shapes emphasis; the facts stay real; keep it brief.'
user_invocable: true
arguments:
  - name: arc
    description: 'Story arc (optional; auto-fit to the session if omitted). e.g. heros-journey, man-in-a-hole, three-act, quest, tragedy, rags-to-riches.'
    required: false
  - name: tone
    description: 'Optional voice — epic, fable, noir, dry-technical, children''s-book. Default: warm, literary, grounded.'
    required: false
---

# Narrate

Turn the session into a short story a human will remember. The human-facing complement to `/handoff`: handoff is the map the next session reads; narrate is the story a person reads.

## When to use

A standup, retro, demo, Slack recap, or changelog where a narrative lands better than bullets — or to close out a long session.

## Procedure

1. **Find the spine.** From the session context, pull the real beats: goal, the setback(s), the turning point, the resolution. Name actual artifacts (files, PRs, bugs) — they become the story's objects.
2. **Pick the arc** (`$ARGUMENTS.arc`, else auto-fit to the session's real shape):
   - **Man in a hole** — fine → broke → fixed. The default for a bug/incident.
   - **Hero's journey / story circle** — set out, faced trials, returned changed. For an ambitious build.
   - **Three-act** — setup → confrontation → resolution. General-purpose default.
   - **Quest** — one target, escalating obstacles. For a focused hunt.
   - **Tragedy / Icarus** — rise then fall. Use it honestly when a session ended worse than it began.
   State the arc and a one-line reason it fits.
3. **Tell it, short.** Map the beats to the arc and write it in the requested `tone` (default: warm, grounded). Let real artifacts carry the imagery — the failing test is the dragon, the source-of-truth file the talisman.
4. **Close with a true moral** — one line that is genuinely a lesson of *this* session.

## Key constraints

- **Succinct and digestible.** Default to ~120–200 words: a tight opening, two or three beats, a one-line moral. Shorter than the work it describes. Expand only if asked.
- **Faithful to events.** The arc shapes emphasis and order, never facts. No invented drama or triumph that didn't happen.
- **Honest endings.** If it ended unresolved, use an arc that fits (cliffhanger, *still in the hole*). No fake happy ending.
- **No-arc is valid.** A typo fix gets one sentence, not a hero's journey.
- **It's framing, not the record.** Point to the decision log / handoff for what literally happened.
