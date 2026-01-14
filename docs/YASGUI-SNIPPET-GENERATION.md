# YASGUI Snippet Auto-Generation

## Overview

This document describes the automated system for generating YASGUI code snippets in SPARQL Anything. The system uses build-time generation from annotations and a central registry, eliminating manual snippet maintenance.

## Architecture

### Documentation Sources

The snippet generation system retrieves documentation from three sources:

1. **@FXFunctionDoc annotations** - For directly-registered SPARQL functions
2. **@MagicPropertyDoc annotations** - For magic properties
3. **ReflectionFunctionDocRegistry** - For reflection-based functions (String.*, DigestUtils.*, etc.)

### Annotations

#### @FXFunctionDoc
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXFunctionDoc {
    String description();
    String example();
    String group() default "FUNCTIONS";
    String label() default "";
}
```

**Location**: `sparql-anything-model/src/main/java/io/github/sparqlanything/model/annotations/`

**Usage**: Applied to function classes in `sparql-anything-engine`

**Fields**:
- `description`: Function description shown as comment
- `example`: Code example/usage pattern  
- `group`: YASGUI snippet group (default: "FUNCTIONS")
- `label`: Display label (defaults to function URI)

**Example**:
```java
@FXFunctionDoc(
    description = "fx:cardinal(?a) returns the corresponding cardinal integer from ?a (rdf:_24 -> 24)",
    example = "BIND(fx:cardinal(?slot) AS ?index)",
    group = "FUNCTIONS",
    label = "fx:cardinal"
)
public class Cardinal extends FunctionBase1 implements FXFunction {
    // ...
}
```

#### @MagicPropertyDoc
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MagicPropertyDoc {
    String description();
    String example();
    String group() default "MAGIC PROPERTIES";
    String label() default "";
}
```

**Location**: `sparql-anything-model/src/main/java/io/github/sparqlanything/model/annotations/`

**Usage**: Applied to magic property classes in `sparql-anything-engine`

**Example**:
```java
@MagicPropertyDoc(
    description = "This property matches the RDF container membership properties (e.g. rdf:_1, rdf:_2 ...).",
    example = "?s fx:anySlot ?slot .",
    group = "MAGIC PROPERTIES",
    label = "fx:anySlot"
)
public class AnySlot extends PFuncSimple {
    // ...
}
```

### ReflectionFunctionDocRegistry

**Location**: `sparql-anything-fuseki/src/main/java/io/github/sparqlanything/fuseki/`

**Purpose**: Documents functions created dynamically via `ReflectionFunctionFactory` which cannot be annotated directly.

**Structure**:
```java
public class ReflectionFunctionDocRegistry {
    private static final Map<String, FunctionDocEntry> REGISTRY;
    
    static {
        register("String.trim", "STRING FUNCTIONS", 
                "Remove leading and trailing whitespace from a string", 
                "BIND(fx:String.trim(\"  hello  \") AS ?result)");
        // ... 32 total functions
    }
    
    public static FunctionDocEntry getDocumentation(String functionName);
    public static boolean isRegistered(String functionName);
}
```

**Covered Functions (32 total)**:

**String Functions (12)**:
- `String.trim`, `String.substring`, `String.indexOf`, `String.startsWith`, `String.endsWith`
- `String.replace`, `String.strip`, `String.stripLeading`, `String.stripTrailing`
- `String.lastIndexOf`, `String.toLowerCase`, `String.toUpperCase`

**Hash Functions (6)**:
- `DigestUtils.md2Hex`, `md5Hex`, `sha1Hex`, `sha256Hex`, `sha384Hex`, `sha512Hex`

**Text Processing (5)**:
- `WordUtils.capitalize`, `capitalizeFully`, `initials`, `swapCase`, `uncapitalize`

**URL Functions (2)**:
- `URLEncoder.encode`, `URLDecoder.decode`

**Similarity Functions (7)**:
- `LevenshteinDistance`, `JaccardDistance`, `JaroWinklerDistance`
- `LongestCommonSubsequenceDistance`, `HammingDistance`, `QGramDistance`, `CosineDistance`

## Core Components

### SnippetGenerator

**Location**: `sparql-anything-fuseki/src/main/java/io/github/sparqlanything/fuseki/`

**Main Class**: Orchestrates snippet generation from all sources

**Key Methods**:

#### generateFormatSnippets()
Generates snippets for file format triplifiers:
- Iterates through registered triplifiers via `TriplifierRegister`
- Reads `@Format` and `@Option` annotations
- Extracts `@Example` annotations from options for query templates
- Falls back to generated queries if no examples exist

#### generateFunctionSnippets()
Generates snippets for SPARQL functions:
- Scans `FunctionRegistry` for fx: namespace functions
- First checks `ReflectionFunctionDocRegistry` for documentation
- Falls back to `@FXFunctionDoc` annotations
- Organizes by group (FUNCTIONS, STRING FUNCTIONS, HASH FUNCTIONS, etc.)

#### generateMagicPropertySnippets()
Generates snippets for magic properties:
- Scans `PropertyFunctionRegistry` for fx: namespace properties
- Reads `@MagicPropertyDoc` annotations
- Currently covers `fx:anySlot`

### Supporting Classes

**SnippetSection** (`sparql-anything-fuseki`)
- Model class for snippet data
- Provides JavaScript escaping: `getEscapedCode()`, `getEscapedLabel()`

**FunctionMetadata** (`sparql-anything-fuseki`)
- Extracts function metadata from `@FXFunctionDoc`
- Provides fallback label extraction from URI

**MagicPropertyMetadata** (`sparql-anything-fuseki`)
- Extracts magic property metadata from `@MagicPropertyDoc`

## Build Integration

### Maven Plugin Configuration

**File**: `sparql-anything-fuseki/pom.xml`

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>generate-snippets</id>
            <phase>process-classes</phase>
            <goals>
                <goal>java</goal>
            </goals>
            <configuration>
                <mainClass>io.github.sparqlanything.fuseki.SnippetGenerator</mainClass>
                <arguments>
                    <argument>${project.build.outputDirectory}/io/github/sparqlanything/fuseki/generated-snippets.js</argument>
                </arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Execution**:
- **Phase**: `process-classes` (after compilation, before packaging)
- **Output**: `generated-snippets.js` in classpath at `/io/github/sparqlanything/fuseki/`

**Build Command**:
```bash
mvn clean install
```

## Runtime Integration

### YASGUIServlet

**Location**: `sparql-anything-fuseki/src/main/java/io/github/sparqlanything/fuseki/`

**Snippet Loading**:
```java
private String generatedSnippetsJs = null;

private void loadGeneratedSnippets() {
    try {
        InputStream is = getClass().getResourceAsStream(
            "/io/github/sparqlanything/fuseki/generated-snippets.js");
        if (is != null) {
            generatedSnippetsJs = new String(is.readAllBytes());
        } else {
            generatedSnippetsJs = "[]";
        }
    } catch (IOException e) {
        logger.error("Failed to load generated snippets", e);
        generatedSnippetsJs = "[]";
    }
}
```

The servlet loads generated snippets from the classpath and passes them to the FreeMarker template.

### yasgui.ftlh Template

**Location**: `sparql-anything-fuseki/src/main/resources/io/github/sparqlanything/fuseki/`

**Usage**:
```javascript
yasqe: {
    snippets: ${generatedSnippets},
},
```

The template injects the generated JavaScript array directly into YASGUI's configuration.

## Snippet Output Format

### Generated File Structure

**File**: `generated-snippets.js`

**Format**:
```javascript
[
    { label: "JSON", code: "PREFIX fx: ...", group: "FILE FORMATS" },
    { label: "fx:cardinal", code: "# fx:cardinal(?a) returns ...", group: "FUNCTIONS" },
    { label: "fx:String.trim", code: "# Remove leading and trailing ...", group: "STRING FUNCTIONS" },
    ...
]
```

### Snippet Groups

The system generates snippets organized into these groups:

- **FILE FORMATS** - Format-specific query templates (JSON, XML, CSV, etc.)
- **FUNCTIONS** - Directly-registered fx: functions (cardinal, serial, etc.)
- **STRING FUNCTIONS** - String manipulation functions
- **HASH FUNCTIONS** - Cryptographic hash functions
- **TEXT PROCESSING** - Text formatting functions
- **URL FUNCTIONS** - URL encoding/decoding
- **SIMILARITY FUNCTIONS** - String similarity/distance functions
- **MAGIC PROPERTIES** - Magic properties (anySlot)

## Documented Functions

### Annotated Functions (9)

Functions with `@FXFunctionDoc` annotations in `sparql-anything-engine`:

1. **fx:cardinal** - Returns cardinal integer from container membership property
2. **fx:serial** - Generates incremental numbers using arguments as reference counters
3. **fx:isContainerMembershipProperty** - Checks if node is a container membership property
4. **fx:before** - Compares container membership properties (lower than)
5. **fx:after** - Compares container membership properties (higher than)
6. **fx:previous** - Returns preceding container membership property
7. **fx:next** - Returns succeeding container membership property
8. **fx:literal** - Builds literal from string representation with datatype or lang code

### Documented Magic Properties (1)

Property with `@MagicPropertyDoc` annotation in `sparql-anything-engine`:

1. **fx:anySlot** - Matches any RDF container membership property

### Registry-Documented Functions (32)

Functions documented in `ReflectionFunctionDocRegistry` (see sections above for complete list).

## Testing

### Test Files

**SnippetGeneratorTest** (`sparql-anything-fuseki/src/test/java/.../`)
- `testGenerateAllSnippets()` - Verifies all snippet types generated
- `testSnippetEscaping()` - Tests JavaScript string escaping
- `testGenerateFormatSnippets()` - Validates format snippet generation
- `testGenerateFunctionSnippets()` - Checks annotated functions present
- `testGenerateMagicPropertySnippets()` - Verifies magic property snippets
- `testReflectionFunctionsIncluded()` - Ensures reflection functions included

**ReflectionFunctionDocRegistryTest** (`sparql-anything-fuseki/src/test/java/.../`)
- `testStringFunctionsRegistered()` - Verifies String.* functions
- `testHashFunctionsRegistered()` - Verifies DigestUtils.* functions
- `testWordUtilsFunctionsRegistered()` - Verifies WordUtils.* functions
- `testURLFunctionsRegistered()` - Verifies URL encoding/decoding
- `testDistanceFunctionsRegistered()` - Verifies similarity functions
- `testGetDocumentation()` - Tests metadata retrieval
- `testAllFunctionsHaveDocumentation()` - Ensures complete documentation

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SnippetGeneratorTest

# Run specific test method
mvn test -Dtest=SnippetGeneratorTest#testGenerateFunctionSnippets
```

## Adding New Functions

### For Directly-Registered Functions

1. Add `@FXFunctionDoc` annotation to function class:
```java
@FXFunctionDoc(
    description = "Your function description here",
    example = "BIND(fx:yourFunction(?arg) AS ?result)",
    group = "FUNCTIONS",
    label = "fx:yourFunction"
)
public class YourFunction extends FunctionBase1 {
    // Implementation
}
```

2. Rebuild project - snippet automatically generated

### For Reflection-Based Functions

1. Add entry to `ReflectionFunctionDocRegistry`:
```java
static {
    register("YourClass.method", "YOUR GROUP",
            "Description of the function",
            "BIND(fx:YourClass.method(?arg) AS ?result)");
}
```

2. Rebuild project - snippet automatically generated

### For Magic Properties

1. Add `@MagicPropertyDoc` annotation to property class:
```java
@MagicPropertyDoc(
    description = "Your property description",
    example = "?subject fx:yourProperty ?object .",
    group = "MAGIC PROPERTIES",
    label = "fx:yourProperty"
)
public class YourProperty extends PFuncSimple {
    // Implementation
}
```

2. Rebuild project - snippet automatically generated

## Format Options with @Example

Format options can include `@Example` annotations to provide query templates. These are extracted during snippet generation:

**Purpose**: The `@Example` annotation on format options provides specific query examples for that option, which the snippet generator uses to create more useful, context-specific query templates instead of generic ones.

**Location**: `@Option` annotations in format triplifier classes

**Example**:
```java
@Format(
    name = "JSON",
    description = "Triplifier for JSON files"
)
public class JSONTriplifier implements Triplifier {
    
    @Option(
        description = "Parse arrays as containers",
        validValues = "true/false",
        defaultValue = "false"
    )
    @Example(
        "PREFIX fx: <http://sparql.xyz/facade-x/ns/>\n" +
        "SELECT * WHERE {\n" +
        "  SERVICE <x-sparql-anything:> {\n" +
        "    fx:properties fx:location 'data.json' ;\n" +
        "                  fx:json.arrays-as-containers true .\n" +
        "    ?s ?p ?o .\n" +
        "  }\n" +
        "}"
    )
    public static final IRIArgument ARRAYS_AS_CONTAINERS = ...
}
```

When the snippet generator encounters an option with `@Example`, it uses that example query in the generated snippet. If no `@Example` is present, it generates a basic query template automatically.

**Note**: Most format options currently do not have `@Example` annotations. Adding them would provide more helpful, option-specific query templates in the YASGUI interface.

## Verification

After building, verify generated snippets:

```bash
# Check file exists
ls sparql-anything-fuseki/target/classes/io/github/sparqlanything/fuseki/generated-snippets.js

# View generated snippets
cat sparql-anything-fuseki/target/classes/io/github/sparqlanything/fuseki/generated-snippets.js

# Search for specific function
grep "String.trim" sparql-anything-fuseki/target/classes/io/github/sparqlanything/fuseki/generated-snippets.js
```

## Benefits

1. **Single Source of Truth** - Documentation lives with code or in central registry
2. **Complete Coverage** - Handles both annotatable and reflection-based functions
3. **Automatic Synchronization** - Snippets update when code changes
4. **Zero Manual Maintenance** - No manual yasgui.ftlh editing required
5. **Consistent Structure** - Same format across all snippets
6. **Easy Extensibility** - Add new functions via annotations or registry
7. **Build-Time Validation** - Compilation errors if annotations malformed
8. **Organized Grouping** - Functions grouped by category for easy discovery
