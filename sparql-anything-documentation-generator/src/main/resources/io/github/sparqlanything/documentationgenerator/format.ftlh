<!-- This page has been generated with sparql-anything-documentation-generator module -->

# ${format.name}

${format.description}

<#if format.extensions?has_content>
## Extensions

SPARQL Anything selects this transformer for the following file extensions:
</#if>

<#list format.extensions as extension>
- ${extension}
</#list>

<#if format.mediaTypes?has_content>
## Media types

SPARQL Anything selects this transformer for the following media types:
</#if>

<#list format.mediaTypes as mediaType>
- ${mediaType}
</#list>

## Default implementation

<#list format.triplifiers as triplifier>
- [${triplifier.className}](../sparql-anything-${format.name?lower_case}/src/main/java/io/github/sparqlanything/${format.name?lower_case}/${triplifier.klazz.simpleName}.java)
</#list>

## Default Transformation

### Data

<#if format.resourceExamplePreview?has_content && !format.binary>
```${format.name}
${format.resourceExamplePreview?no_esc}
```
</#if>

Located at ${format.resourceExample}

### Query

```
${format.defaultTransformationQuery?no_esc}
```

### Facade-X RDF

```turtle
${format.facadeXRdf?no_esc}
```
<#if format.optionSections?has_content>
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
</#if>
<#list format.optionSections as optionSection>
| [${optionSection.name}](#${optionSection.link}) | ${optionSection.description} | ${optionSection.validValues} | ${optionSection.defaultValue} |
</#list>

<#list format.optionSections as optionSection>
---
### `${optionSection.name}`

#### Description

${optionSection.description}

#### Valid Values

${optionSection.validValues}

#### Default Value

${optionSection.defaultValue}

<#if optionSection.examples?has_content>
#### Examples
</#if>

<#list optionSection.examples as example>
##### Example ${example?index + 1 }

${example.description}

###### Input

<#if example.resourceExamplePreview?has_content && !format.binary>
```${format.name}
${example.resourceExamplePreview?no_esc}
```
</#if>

${example.resource?no_esc}

###### Query

```
${example.query?no_esc}
```

###### Result

```turtle
${example.result?no_esc}
```

</#list>
</#list>




