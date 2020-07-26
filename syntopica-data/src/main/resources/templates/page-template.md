## ${topic.subject}

${topic.formatImageLink()}

In the context of this [model](../README.md), ${topic.formatPageLink()}

<#list topic.facts as fact>
* ${topic.formatFact(fact)}
</#list>

### Discussion

${discussion}

<h4 align="center"><b>&sect; &sect; &sect;</b></h4>

<#list site.linkedTopics as topic>
${topic.formatRefLinks()}
</#list>

<#list site.linkedTopics as topic>
<#if topic.hasLinkedTopics()>
${topic.formatLinkedReferences()}
</#if>
</#list>
