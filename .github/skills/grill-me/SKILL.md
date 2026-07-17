---
name: grill-me
description: 'Alignment-first interview before building. Claude interviews you to surface requirements, constraints, edge cases, and acceptance criteria, then writes a short plan/PRD to disk that survives a /clear. Use at the start of any non-trivial task, before writing code.'
user_invocable: true
arguments:
  - name: topic
    description: The task or feature to align on (optional; Claude will ask if omitted)
    required: false
---

# Grill Me

Front-load alignment. Before any implementation, interview the developer until the task is concrete enough to build without guessing, then write the result to disk as a plan a fresh session can pick up.

This is the *alignment phase* from Matt Pocock's AI-coding workshop: the model interviews the developer rather than the reverse, because the expensive failures come from building the wrong thing confidently, not from typing slowly.

## When to use

- Starting a non-trivial feature, refactor, or investigation
- A request is underspecified and you can feel multiple reasonable interpretations
- Before kicking off an autonomous/background implementation, so it has a spec to work from

## Procedure

1. **Establish the goal.** If `$ARGUMENTS.topic` was given, restate it in one sentence and confirm. Otherwise ask what we're building and why.
2. **Interview in focused rounds.** Ask high-leverage questions a few at a time (use `AskUserQuestion` for genuine choices), and let each answer shape the next question. Do not dump a 40-item quiz. Cover, as relevant:
   - **Problem / why** — what's broken or missing; who feels it.
   - **Users / stakeholders** — who consumes this; whose sign-off matters.
   - **Scope boundaries** — explicitly in-scope vs out-of-scope. Force the line.
   - **Constraints** — tech, dependencies, time, compatibility, conventions to honor.
   - **Interfaces & data shapes** — inputs, outputs, schemas, the boundary contracts.
   - **Edge cases & failure modes** — what happens when inputs are bad, empty, huge, concurrent.
   - **Acceptance criteria** — how we'll know it's done and correct (tests, observable behavior).
   - **Risks & unknowns** — what we don't know yet; what could invalidate the approach.
3. **Know when to stop.** Stop when the picture is concrete enough to implement without guessing. Don't pad with questions whose answers wouldn't change the work. Surface remaining unknowns explicitly rather than inventing answers.
4. **Write the plan to disk.** Produce a short markdown plan/PRD the developer can read and a future session can resume from. Default to a `plans/` directory if one exists, else a file the user names (e.g. `./<slug>-plan.md`). If the repo has documentation conventions (e.g. required frontmatter under `docs/`), follow them. Sections:
   - **Goal** (one or two sentences) · **Context / why** · **In scope** / **Out of scope** · **Constraints** · **Interfaces & data** · **Acceptance criteria** · **Open questions** · **Slices** (optional ordered list of vertically-sliced steps, each independently shippable).
5. **Hand off.** Tell the user the file path and that it survives a `/clear` — a fresh session can start from it (pair with `/handoff`).

## Key constraints

- **Do not start implementing.** This skill ends at a written plan. Building is a separate, ideally fresh, session.
- **Never fabricate an answer.** If the user defers or doesn't know, record it under Open questions — an honest unknown is more useful than a confident guess.
- **Fewer, sharper questions beat a long quiz.** Optimize for the questions whose answers most change the work.
- **The plan is the artifact**, not the conversation. Keep it tight and decision-dense.
