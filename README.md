Syntopica
=========

This project incorporates some ideas about [conceptual models][cenceptual-models], 
and offers a prospect for using the [EDUCE patterns][educe-patterns] 
for transforming conceptual models into named software elements.

The **syntopica** library offers a framework for modeling concepts as Facts, 
each of which relates multiple Topics within a bounded context (a Domain).

The syntax for expressing Facts are simple statements, like the following:

```
governor governs: business.
requestor requests: improvements in: activity.
expector expects: improvements in: activity for: business.
developer designs: component, dialog, interface.
developer builds: component, dialog, interface.
developer tests: component, dialog, interface.

```

The syntopica ModelSite can convert statements of this kind into a conceptual model web site, 
using either HTML or Markdown (GitHub) pages, and Scalable Vector Graphics (SVG) for model diagrams.


Project Vision
==============

The overall purpose of this project will be to push the envelope of software design, using concepts from 
[EDUCE][educe-overview] to drive design. Much of this can serve as an adjunct to [DDD][fowler-ddd]. 
So, the tools can help automate some of those efforts, maintain coordination
between the concepts within an ubiquitous language and the code, 
ideally connecting the two through web hosted, hyper-linked
resources (concepts and code).


Project Goals
=============

The current project goals include forging a critical mass of features in the library.
* generate web pages from model concepts and related discussions
* generate svg diagrams from model concepts, esp. facts
* address simplest diagram layout problems: 1 fact, 2 facts, 3 facts (per topic)

Eventual Goals
* provide assistance to convert discussions into formal models (NLP)
* generate code skeletons from conceptual models, e.g., with StringTemplate, or FreeMarker
* develop similarity measures for conceptual models, esp. fact comparison, predicate completion, ...
* develop semantic search functions

[educe-patterns]: https://educery.dev/educe/pattern-language/#educe-pattern-language
[educe-overview]: https://educery.dev/educe/patterns/
[conceptual-models]: https://educery.dev/papers/modeling/natural-conceptual-modeling/#modeling-language
[fowler-ddd]: https://martinfowler.com/tags/domain%20driven%20design.html
