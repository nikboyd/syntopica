Syntopica
=========

This project incorporates some ideas about 
[natural conceptual models](http://educery.com/papers/educe/models/), 
and offers a prospect for using the 
[EDUCE](http://educery.com/educe/patterns/educe-overview.html) 
process for transforming such models into named software elements.

The syntopica library offers a framework for modeling concepts as Facts, 
each of which relates multiple Topics within the bounded context of a Domain.

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
using either HTML or Markdown (GitHub) pages, and Scalable Vector Graphics (SVG) diagrams.

