---
name: issue-writer
description: 'Draft GitHub issues for nubank/clojuredocs. Use when filing bugs, enhancements, or feature requests. Use when turning Slack threads, conversation context, feature proposals, or bug reports into well-structured issues.'
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
3. **Pick the template.** Use the matching [issue template](../../ISSUE_TEMPLATE/) (bug, enhancement, or feature). Load [docs/github-templates/issue-writing-guide.md](../../../docs/github-templates/issue-writing-guide.md) for section guidance and examples.
4. **Draft.** Write the issue following the template structure: Problem, Demonstration/Steps to Reproduce, Context, and References. Omit sections that don't apply.
5. **Deliver.** Present the draft in chat. Save as a `.md` file only if the user asks to commit it.

Note: Father Watson diagnostic questions (What do we know? What do we need to know? Where are we? Where are we going?) belong in the [PR description template](../../pull_request_template.md), not in issues.

## Key constraints

- Frame, don't solve — no implementations, function names, or code changes
- No Acceptance Criteria, Risks & Mitigations, or Implementation Steps sections
- Calm, technical prose — no emoji, exclamation points, or hype language
- Terseness is a virtue — omit sections that don't apply, don't pad
