## ${topic.subject}

![${topic.linkName}](https://raw.githubusercontent.com/nikboyd/Syntopica/master/syntopica-data/src/test/resources/pages/${topic.linkName}.svg)

In the context of this [Model](model.md), ${topic.article} ${topic.getCapitalizedLink(pageType)}

```
<#list topic.facts as fact>
${fact.getFormattedPredicate(topic, pageType)}
</#list>
```

### Discussion

${discussion}

<h3 align="center"><b>&sect; &sect; &sect;</b></h3>
