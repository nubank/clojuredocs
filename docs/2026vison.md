# ClojureDocs: Two-Year Vision (2026–2028)

> Make ClojureDocs the highest-signal, REPL-native, community-connected documentation system in the programming ecosystem.

---

## What does success look like?

Examples on ClojureDocs are accurate, executable, and written by real practitioners. Every example runs — if it doesn't execute correctly against the documented var, it is structurally rejected, not manually moderated. Contributors are known community members, and every contribution carries visible quality signals: authorship, freshness, and engagement. AI-generated slop doesn't survive contact with the verification pipeline. People trust what they read because the system earns that trust through enforcement, not curation alone.

The data model is explicit, documented, and separated from business logic. Adding a new feature — a scoring system, a REPL widget, a new library — does not require understanding the entire codebase. Contributors ship improvements without fear of breaking unrelated functionality. The architecture supports multi-library documentation, not just `clojure.core`. The system is AI-legible: loosely coupled, well-contracted, and independently testable — qualities that make it safe for both human and agentic contributors.

Every var page has an embedded REPL. Examples are executable — input and output are visible, and users can modify and re-run them in place. This is not just a convenience. In a world where AI tools threaten to eliminate the repetition necessary to build skills, the REPL is where developers put in the reps. Learning Clojure on ClojureDocs is active, not passive learning. The gap between "reading about a function" and "using a function" is eliminated.

Docs are not a dead end. Each var and namespace is a hub connecting to blog posts, talks, GitHub usage, mob programming sessions, and pairing opportunities. Engineers discovering a function or a library also discover the humans and resources around it. ClojureDocs is an entry point into the Clojure community, not a static reference page.

We understand how people actually use the site. Analytics are instrumented, baselines are established, and decisions are driven by data. We know which pages matter, which are dead zones, and where users drop off.

ClojureDocs implementation exeplifies modern good quality code conventions and serves as a project template for modern clojure/clojurescript applications. The code is configured with built in guidelines for AI. As an open source project it is a place where engineers can demonstrate the skills that matter most in the next era of software development. Contributing to ClojureDocs exercises the capabilities that distinguish effective developers in an AI-assisted world: reviewing and verifying code written by others, writing and referencing clear specifications that define what "correct" means, designing systems that enforce quality through structure rather than process, thinking architecturally about data models and extension points, and making judgment calls about what's useful versus what's merely correct. An engineer who ships a contribution to ClojureDocs — whether it's a verified example, a hardened validation rule, a data model improvement, or a new library integration — has produced a public, verifiable artifact that demonstrates these skills in a way no interview question can simulate. The codebase is intentionally designed to reward this kind of work: loosely coupled so contributions are independently testable, explicitly modeled so changes are safe to make, and enforced by machines so human effort goes toward the decisions that require judgment. ClojureDocs is not just an open source project. It's practicing the skills the industry needs most.

---

## We will instrument analytics and establish baseline metrics for how people use ClojureDocs today.

**Why:** We can't improve what we don't measure. Right now we don't know which vars drive the most traffic, where users enter and leave, or which examples get used. A validated understanding of current usage patterns — entry points, popular vars, dead zones, session depth — is the foundation for every prioritization decision that follows. This work comes first because it de-risks everything else.

## We will make the data model explicit and extensible, decoupled from business logic.

**Why:** The current system encodes its data model implicitly across business logic. Every new feature risks breaking something unrelated. This is the keystone investment — without a clean, explicit schema, multi-library support, quality scoring, REPL integration, and every other improvement requires scattered changes across the codebase. An explicit model unlocks safe, parallel iteration and lowers the barrier for contributors.

## We will build a verification pipeline that structurally rejects incorrect examples rather than relying on moderation alone.

**Why:** AI has made content generation nearly free. The bottleneck is no longer producing examples — it's verifying them. If every example must execute correctly against the var it documents, an entire class of bad contributions is eliminated at submission time, not caught by reviewers after the fact. This is the difference between documenting rules and enforcing invariants. A moderation queue that humans review is Path A — bulldozing through the quality problem. Executable validation that rejects broken examples before they enter the system is Path B — eliminating the problem. We choose Path B wherever possible, and reserve human judgment for the questions machines can't answer: is this example *useful*, not just *correct*?

## We will introduce content quality signals to preserve human-first, high-trust documentation.

**Why:** Even with executable validation, not all correct examples are good examples. Author reputation, freshness scores, and engagement metrics help surface the examples that come from real experience and teach effectively — the kind of signal that no volume of AI-generated content can replicate. In the AI age, the value of documentation is no longer in generating text, but in curating truth, enabling execution, and connecting people. ClojureDocs is an existence proof that human curation paired with executable verification produces better outcomes than AI-generated volume alone.

## We will embed an interactive REPL on every var page with executable examples.

**Why:** Clojure is learned at the REPL, but ClojureDocs is a static text site. This is a fundamental mismatch. Executable examples — where input and output are visible and modifiable in place — collapse the distance between reading documentation and writing code. This also directly addresses a risk the industry is learning the hard way: AI tools threaten to eliminate the repetition necessary to build skills. The REPL is where developers build muscle memory, develop critical thinking about how functions behave, and earn the kind of trustworthiness that comes from knowing things deeply. 

## We will support documentation for libraries beyond `clojure.core`.

**Why:** ClojureDocs currently assumes a single-library world, hardcoded into the data model and UI. The Clojure ecosystem has grown far beyond core, and developers need the same quality documentation for libraries they use daily. Multi-library support transforms ClojureDocs from a reference for one namespace into a documentation platform for the ecosystem.

## We will connect documentation to people and community resources — blog posts, talks, pairing sessions, mob programming.

**Why:** A var page that shows only a docstring and examples is a dead end. Developers learning a function often need context that only comes from humans: when to use it, when not to, how it composes with other tools. By linking vars to blog posts, conference talks, GitHub usage, and live community sessions like Clojure Camp, each page becomes a hub in a knowledge graph rather than an isolated leaf. Docs become an entry point into the community, not a dead end.

---

## Strategic Bets

1. **Human signal over volume.** We are explicitly not competing with AI-generated docs. Our moat is trust. The industry evidence is clear: companies that replaced human judgment with AI volume saw quality degrade and [reversed course](https://customerservicemanager.com/the-truth-about-klarnas-backtrack-on-ai-and-the-rehiring-of-humans/). We bet on the opposite direction.
2. **Enforce, don't document.** When a quality rule must be followed, we build it into the system so it's enforced automatically — not written as a guideline that contributors must read and remember. The current status quo offers guidelines as style guides but relies on trust with zero verification of truth. This doesn't scale. "Good" is vagely defined but those guidelines are not current nor enforced. No true peer (or computational) verification or "good signal" exists. Every new contributor has to read and interpret the rules. Every reviewer has to check whether those rules were followed. The more rules you write, the more human attention each contribution requires. Guidelines that depend on people reading them will eventually be ignored — especially as AI makes it trivially cheap to generate plausible-looking content at volume.
3. **REPL as the interface.** Docs should behave like code, not text. The REPL is both a learning tool and a verification mechanism.
4. **Docs as a graph, not pages.** A var page that exists in isolation is a dead end. Knowledge about a function is not contained on a single page — it's distributed across the ecosystem.

   Someone who truly understands `reduce` has read blog posts about transducers, watched Rich Hickey's talks on sequences, seen how `reduce` is used in popular libraries, and possibly paired with someone who showed them a non-obvious application. Today, none of those connections exist on ClojureDocs. You arrive at a var page, you read, you leave.

   We model knowledge as a graph where each var is a node with meaningful connections:

- **Var → Var:** "See also" relationships. `reduce` connects to `transduce`, `reductions`, `into`. These already exist in ClojureDocs but are hand-curated and sparse. With an explicit data model, they can be enriched by usage analysis: functions that co-occur in real codebases are likely conceptually related.
- **Var → Namespace → Library:** A var lives in a namespace, which lives in a library. Navigation should be fluid in both directions. Arriving at `clojure.string/split` should make the rest of `clojure.string` one click away, and related string-handling libraries discoverable.
- **Var → People:** Who contributed the best example for this var? Who wrote the definitive blog post about it? Who runs the mob programming session where this concept comes up regularly? The graph makes human expertise visible and reachable. This is the opposite of AI-generated anonymity — it connects knowledge to the people who hold it.
- **Var → External Resources:** Blog posts, conference talks, Stack Overflow threads, GitHub usage examples. Each var page becomes a curated entry point into everything the community has produced about that concept.

   The graph model also changes how discovery works. Today, you search for a var by name — you have to already know what you're looking for. In a graph, you can navigate by relationship: "show me all functions related to sequence transformation," "show me what people commonly use alongside `core.async/go`," "show me community resources for learning transducers."

   This requires the explicit data model (bet #5). You cannot build a graph on top of implicit, hardcoded relationships scattered through business logic. The data model is the foundation; the graph is what it enables.

1. **Abstraction-first development.** No feature work without a stable model underneath.

---

## Risks

- **Refactor stall:** Investing in the data model without shipping visible user value for too long. The 11th-hour insight applies here — weeks of understanding should produce concrete enforcement, not just more documents.
- **Overengineering:** Building more schema than current use cases require.
- **Moderation friction:** Quality enforcement that creates false positives and discourages real contributors. The line between "structurally rejected" and "frustratingly pedantic" requires ongoing calibration.
- **Low engagement:** Improvements that don't move contributor or user behavior.


---

## Success Metrics

- ↓ Time to first successful REPL interaction on a var page
- ↑ Percentage of examples that are executable and verified
- ↑ Percentage of pages with executable examples
- ↑ Repeat usage and session depth
- ↑ Contributions from known community members
- ↓ Incorrect examples that reach users (defect escapement)
- ↓ Low-quality or AI-generated submissions that survive the verification pipeline
- ↑ Libraries documented beyond `clojure.core`
