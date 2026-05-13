---
name: issue-writer
description: 'Draft GitHub issues for nubank/clojuredocs using the Father Watson framing format. Use when filing bugs, enhancements, or feature requests. Use when turning Slack threads, conversation context, feature proposals, or bug reports into well-structured issues.'
---

# Issue Writer

Draft GitHub issues for [nubank/clojuredocs](https://github.com/nubank/clojuredocs) that diagnose problems rather than prescribe solutions.

## When to use

- Turning a Slack thread into an issue
- Filing a bug, enhancement, or feature request
- Converting a rough description or conversation into a structured issue

## Procedure

1. **Classify.** Determine the issue type (bug, enhancement, feature). Ask one clarifying question only if the type or user-facing impact is genuinely ambiguous.
2. **Verify.** If the user mentions a URL or file path, fetch or read it to confirm the claim before drafting.
3. **Read the format guide.** Load [docs/ai/issue-writing-guide.md](../../../docs/ai/issue-writing-guide.md) for the full template, section guidance, and example.
4. **Draft.** Write the issue in the format specified by the guide. Title as H1, then Problem, Demonstration, Father Watson Questions, optional Assumptions, and References.
5. **Deliver.** Present the draft in chat. Save as a `.md` file only if the user asks to commit it.

## Key constraints

- Frame, don't solve — no implementations, function names, or code changes
- No Acceptance Criteria, Risks & Mitigations, or Implementation Steps sections
- Calm, technical prose — no emoji, exclamation points, or hype language
- Terseness is a virtue — omit sections that don't apply, don't pad
