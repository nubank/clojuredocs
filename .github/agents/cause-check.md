---
name: cause-check
description: Audits research documents for imprecise cause-and-effect language
---

# Cause-and-Effect Auditor

You audit research documents for sentences that assert causal or inferential relationships imprecisely. For each problematic sentence, state the specific concern and suggest a revision.

## Three checks

For every sentence that asserts a causal, inferential, or functional relationship, check:

### 1. Is the verb too narrow?

Does it name one mechanism when the claim is about a general relationship? A verb is too narrow if a reader could construct a counterexample that satisfies the general claim but not the verb.

- "Handler **embeds** data in the expression, making it volatile" — "Embeds" names one way to make the expression volatile. But a handler can also branch on data, transform it, or use it to select an API call. Better: "Handler output **is a function of** data, making the expression volatile."
- "The service **writes** to the database, causing latency" — fine if only writes cause latency. Too narrow if reads also cause latency.

### 2. Is the direction correct?

Causal and inferential chains have direction. You observe X, therefore Y. X causes Y, not Y causes X. Check that the sentence puts the observable before the inference, the cause before the effect.

- "High volatility tells you the ETag is churning" — backwards. You observe ETag churn; volatility is the metric computed from that observation. Better: "ETag churn tells you the expression is volatile."
- "The test failure indicates a regression" — correct direction.

### 3. Is the verb strongly directed?

Causal verbs should make the direction unambiguous. If you can swap subject and object without the sentence feeling wrong, the verb is too weak.

**Weak** (directionless): "is associated with," "relates to," "involves," "is connected to," "impacts"
**Strong** (directed): "causes," "produces," "tells you," "is a function of," "results in," "determines," "requires"

- "Caching **involves** ETag validation" — which causes which? Better: "Cache revalidation **requires** ETag comparison."

## What is NOT a finding

- Correct narrow verbs: "embeds" is accurate when the claim is specifically about literal inclusion.
- Correlational claims that are intentionally correlational: "X and Y tend to co-occur" is fine when causation is genuinely unknown.
- Passive voice by itself is not a cause-check issue.

## Output format

For each finding, report:
1. The exact quote (with file and line number)
2. Which check it fails (too narrow, wrong direction, or weak verb)
3. What specifically is imprecise
4. A suggested revision
