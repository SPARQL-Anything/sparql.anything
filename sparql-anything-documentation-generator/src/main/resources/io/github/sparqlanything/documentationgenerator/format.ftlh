# ${format.name}

${format.description}

## Extensions

SPARQL Anything selects this transformer for the following file extensions:

<#list format.extensions as extension>
- ${extension}
</#list>

## Media types

SPARQL Anything selects this transformer for the following media types:

<#list format.mediaTypes as mediaType>
- ${mediaType}
</#list>

## Default implementation

<#list format.triplifiers as triplifier>
- [${triplifier.name}](../sparql-anything-${format.name?lower_case}/src/main/java/com/github/sparqlanything/${format.name?lower_case}/${triplifier.simpleName}.java)
</#list>

## Default Transformation

### Data

Located at ${format.resourceExample}

### Query

```
${defaultTransformationQuery}
```

### Facade-X RDF

```turtle
${facadeXRdf?no_esc}
```
## Options

### Summary

| Option name | Description | Valid Values | Default Value |
|-------------|-------------|--------------|---------------|
<#list optionSections as optionSection>
| ${optionSection.option.name} | ${optionSection.option.description} | ${optionSection.option.validValues} | ${optionSection.option.defaultValue} |

### `${optionSection.option.name}`

#### Description

${optionSection.option.description}

#### Valid Values

${optionSection.option.validValues}

#### Default Value

${optionSection.option.defaultValue}

#### Examples

<#list optionSection.examples as example>
#### Input

Located at ${example.resource?no_esc}

###### Query

${example.query?no_esc}

###### Result

```turtle
${example.result?no_esc}
```

</#list>
</#list>




