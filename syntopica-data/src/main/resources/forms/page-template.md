## ${topic.titleSubject()}

In the context of this [model](../README.md#overview), ${topic.formatReference()}

<#list topic.getFacts() as fact>
<details>
  <summary>${topic.formatFact(fact)}</summary>

  <img src="../images/${topic.formImageName(fact)}.svg" />
</details>

</#list>

### Discussion

${discussion}

<div align="center"><b>&sect; &sect; &sect;</b></div>

<#list site.linkedTopics as topic>
${topic.formatRefLinks()}
</#list>

<#list site.linkedTopics as topic>
<#if topic.hasLinkedTopics()>
${topic.formatLinkedReferences()}
</#if>
</#list>
