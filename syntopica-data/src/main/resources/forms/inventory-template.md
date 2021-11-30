## ${domain.title} Domain Inventory

<#list domain.items as topic>
<details>
  <summary>${topic.formatSubjectLink()}</summary>

  <#list topic.getFacts() as fact>
  * ${topic.formatFact(fact, "topics/")}
  </#list>
</details>

</#list>

