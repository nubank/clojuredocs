# Typo in index key: `:migraion-key` in `add-all-indexes!`

## Bug

## Problem

In `main.clj`, `add-all-indexes!` indexes the `:migrate-users` collection on `[:email :migraion-key]`. The key `:migraion-key` is missing a 't' — it should presumably be `:migration-key`.

If any code writes or reads using the correctly-spelled `:migration-key`, the index would not match. This is a low-severity issue since the `:migrate-users` collection appears vestigial (no active queries found in the codebase), but it should be corrected or the collection should be removed.

Note: this bug is also masked by the `add-indexes-to-coll!` bug — the index is actually being applied to `:examples`, not `:migrate-users`.

## Steps to Reproduce

1. Read `src/clj/clojuredocs/main.clj` L81:
   ```clojure
   (add-indexes-to-coll! :migrate-users [:email :migraion-key])
   ```
2. Note the spelling: `:migraion-key` (missing 't')

## Father Watson Questions

**What do we know?**
- The key is spelled `:migraion-key` in `main.clj` L81
- No active queries against `:migrate-users` were found in the codebase
- The `add-indexes-to-coll!` bug means this index goes to `:examples` anyway

**What do we need to know?**
- Is `:migrate-users` still needed, or can the entire collection and its index call be removed?
- If it is needed, what is the correct spelling of the key?

**Where are we?**
- A typo exists in a vestigial index definition
- The typo is masked by a separate bug

**Where are we going?**
- Either the typo is fixed and the collection is confirmed as needed, or the entire `:migrate-users` index call is removed as dead code

## References

- `src/clj/clojuredocs/main.clj` L81 (the typo)
- Discovered during entity-attribute model work for #43
