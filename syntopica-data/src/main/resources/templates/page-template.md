## ${topic.subject}

<div  style="float: right; margin: 20px"><img src="${topic.linkName}.svg"/></div>

In the context of this [Model](model.md), ${topic.article} ${topic.getCapitalizedLink(pageType)}

```
<#list topic.facts as fact>
${fact.getFormattedPredicate(topic, pageType)}
</#list>
```

### Discussion

${discussion}

<h3 align="center"><b>&sect; &sect; &sect;</b></h3>
