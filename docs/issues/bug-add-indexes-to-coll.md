# `add-indexes-to-coll!` ignores its collection argument — all indexes go to `:examples`

## Bug

## Problem

`add-indexes-to-coll!` in `main.clj` accepts a `coll` parameter but hardcodes `:examples` in the body. Every call to this function — for `:see-alsos`, `:notes`, `:users`, `:legacy-var-redirects`, and all other collections — creates indexes on `:examples` instead of the intended collection.

This means `:see-alsos`, `:notes`, and `:users` have no indexes in production despite being queried on every var page load. The `:examples` collection accumulates indexes for fields that do not exist on its documents (e.g., `from-var.name`, `var.ns`).

## Steps to Reproduce

1. Read `src/clj/clojuredocs/main.clj` L53-54:
   ```clojure
   (defn add-indexes-to-coll! [coll ks]
     (doseq [k ks]
       (mon/add-index! :examples [k])))
   ```
2. Note that the `coll` parameter is never used — `:examples` is hardcoded
3. Observe that `add-all-indexes!` calls this function for `:namespaces`, `:see-alsos`, `:libraries`, `:notes`, `:legacy-var-redirects`, `:users`, and `:migrate-users`
4. None of those collections receive their intended indexes

## Father Watson Questions

**What do we know?**
- The function signature accepts `coll` but the body uses the literal `:examples`
- `add-all-indexes!` is called at server startup
- The intended indexes for `:see-alsos` include `[:from-var.name :from-var.ns :from-var.library-url]` — fields queried on every var page load via `data/find-see-alsos-for`
- Similarly, `:notes` queries filter on `[:var.ns :var.name :var.library-url]`

**What do we need to know?**
- What is the actual index state in production MongoDB? The bug may have been present since the function was written, or indexes may have been created manually at some point
- Is there measurable query latency on pages with many see-alsos or notes?

**Where are we?**
- `add-indexes-to-coll!` always indexes `:examples` regardless of the collection argument
- All other collections are unindexed unless indexes were created outside this code path

**Where are we going?**
- The function uses its `coll` parameter: `(mon/add-index! coll [k])`
- Each collection has the indexes declared in `add-all-indexes!`

## References

- `src/clj/clojuredocs/main.clj` L53-54 (the bug)
- `src/clj/clojuredocs/main.clj` L56-81 (`add-all-indexes!` calling the broken function)
- `src/clj/clojuredocs/data.clj` L6-30 (queries that would benefit from correct indexes)
- Discovered during entity-attribute model work for #43
