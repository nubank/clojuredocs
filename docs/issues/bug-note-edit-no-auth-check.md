# `patch-note-handler` does not verify authorship — any logged-in user can edit any note via API

## Bug

## Problem

The note editing endpoint (`PATCH /api/notes/:id`) requires login but does not check whether the logged-in user is the author of the note. The UI restricts the edit button to the author (via `can-edit?` in `pages/vars.clj`), but the API endpoint has no such guard. A logged-in user who sends a direct PATCH request can edit any note.

This is inconsistent with the delete path, which does check authorship.

## Steps to Reproduce

1. User A creates a note on any var page
2. User B logs in via GitHub
3. User B sends a direct `PATCH /api/notes/:id` request with User A's note ID and a new body
4. The note is updated — no authorship check is performed

## Father Watson Questions

**What do we know?**
- `patch-note-handler` in `api/notes.clj` L31-43 calls `c/require-login!` but never checks `is-author`
- `delete-note-handler` in `api/notes.clj` L45-62 does check `is-author` before allowing deletion
- The UI sets `can-edit?` to `author?` in `pages/vars.clj` L209-211, so the edit button is hidden for non-authors
- Example editing intentionally allows non-authors to edit (editors are tracked in an `:editors` list)

**What do we need to know?**
- Was the lack of an authorship check on note editing intentional (matching example behavior) or an oversight?
- Should note editing track editors the way example editing does, or should it be restricted to the author?

**Where are we?**
- The UI restricts note editing to the author
- The API does not enforce this restriction
- There is no `:editors` list on notes — the original `:author` is preserved regardless of who edits

**Where are we going?**
- The API enforces the same authorship restriction that the UI presents, or
- Note editing is explicitly opened to all logged-in users with an `:editors` list (matching example behavior), and the UI is updated accordingly

## References

- `src/clj/clojuredocs/api/notes.clj` L31-43 (`patch-note-handler` — no author check)
- `src/clj/clojuredocs/api/notes.clj` L45-62 (`delete-note-handler` — has author check)
- `src/clj/clojuredocs/pages/vars.clj` L208-211 (UI sets `can-edit?` to author-only)
- `src/clj/clojuredocs/api/examples.clj` L74-97 (`patch-example-handler` — intentionally allows non-author editing with editor tracking)
- Discovered during entity-attribute model work for #43
