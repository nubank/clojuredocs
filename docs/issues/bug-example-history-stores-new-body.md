# ExampleHistory records store the new body, not the previous body

## Bug

## Problem

When an example is edited, `create-example-history` stores the *incoming* body (the new content) rather than the *existing* body (the content being replaced). The parameter is named `new-body` and is passed `(:body example-update)` — the request payload.

This means the history trail records what each edit changed the body *to*, but the original body before the first edit is never captured in history. If a user wants to see what an example said before an edit, they must look at the *previous* history entry, not the one for that edit — and the state before the very first edit is lost entirely.

## Steps to Reproduce

1. User A creates an example with body "original content"
2. User B edits it to "revised content"
3. The ExampleHistory entry created for this edit contains `{:body "revised content"}` — the new body
4. The original "original content" is not stored in any history record

## Father Watson Questions

**What do we know?**
- `create-example-history` in `api/examples.clj` L17-22 accepts `new-body` and stores it as `:body`
- `patch-example-handler` passes `(:body example-update)` — the incoming request body — as the `new-body` argument
- The `example` variable in the handler holds the pre-edit document, but its body is not passed to the history function
- The history entry records the editor, the timestamp, and the example-id

**What do we need to know?**
- Was this intentional? Storing "what it became" vs. "what it was" are both valid history models, but the current approach loses the pre-first-edit state
- How is example history displayed in the UI? Does `example-handler` in `pages/vars.clj` L335-355 render history entries in a way that compensates for this?
- Are there consumers of `:example-histories` that depend on the current behavior?

**Where are we?**
- Each history entry stores the body that was submitted in that edit
- The body before the first edit is never captured in history
- The current example document always holds the latest body

**Where are we going?**
- History entries preserve the body being replaced (the pre-edit state), or
- The first edit also captures the original body as a baseline history entry, or
- The current behavior is documented as intentional and the UI accounts for it

## References

- `src/clj/clojuredocs/api/examples.clj` L17-22 (`create-example-history`)
- `src/clj/clojuredocs/api/examples.clj` L74-97 (`patch-example-handler` — where history is created)
- `src/clj/clojuredocs/pages/vars.clj` L335-355 (`example-handler` — renders history)
- Discovered during entity-attribute model work for #43
