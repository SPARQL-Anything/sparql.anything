# Developer Guide

This guide documents internal design decisions and extension points for developers working on SPARQL Anything itself (as opposed to users writing SPARQL Anything queries). Most sections are still TODO — contributions welcome.

## Table of contents

- [Reading input resources: the service-provider pattern](#reading-input-resources-the-service-provider-pattern)
- Triplifier architecture — TODO
- Facade-X graph builders — TODO
- The query execution pipeline (`FacadeX.ExecutorFactory`) — TODO
- Functions and magic properties — TODO
- The plugin loading mechanism — TODO
- Module layout and build — TODO
- Testing conventions — TODO

## Reading input resources: the service-provider pattern

`Triplifier.getInputStream(Properties)` is the single entry point every triplifier uses to obtain an `InputStream` for the resource it needs to read, regardless of where that resource actually comes from (a local file, an HTTP(S) URL, inline content, the output of a shell command, an entry inside an archive, or an S3 object). Internally, this is implemented as a service-provider (SPI) pattern rather than one large method handling every input source inline.

### `ResourceService`

The core abstraction is the `ResourceService` interface (`io.github.sparqlanything.model.resources.ResourceService`, module `sparql-anything-model`):

```java
public interface ResourceService {
	InputStream getInputStream(Properties properties) throws IOException, TriplifierHTTPException;
}
```

Each input source is implemented as one `ResourceService`. It receives the full `Properties` of the current query/triplifier invocation and is responsible for returning a readable `InputStream` for that source, including any resource cleanup that must happen once the caller closes the stream.

### `@TargetOption`

A `ResourceService` implementation is annotated with `@TargetOption`, giving the `IRIArgument` key (as a raw string) that identifies when this service should be used:

```java
@TargetOption(IRIArgument.LOCATION_NAME)
public class LocationInputService implements ResourceService { ... }
```

Every `IRIArgument` constant has a companion `..._NAME` string constant (e.g. `LOCATION_NAME`, `COMMAND_NAME`, `CONTENT_NAME`, `S3_ENDPOINT_NAME`) precisely so that `@TargetOption` values, and any other code that needs the raw property key, don't need to depend on the full `IRIArgument` object.

### Built-in implementations

`sparql-anything-model` ships three built-in services:

| Service | `@TargetOption` | Handles |
|---|---|---|
| `LocationInputService` | `IRIArgument.LOCATION_NAME` | `file://`, `http(s)://`, other URL protocols, and archive-extraction (`from-archive`) |
| `CommandInputService` | `IRIArgument.COMMAND_NAME` | Running a shell command and returning its stdout |
| `ContentInputService` | `IRIArgument.CONTENT_NAME` | Wrapping an inline `content` property value as a stream |

Additional services can live in their own modules — see [S3 support](S3.md) for `sparql-anything-s3-support`'s `S3InputService`, which reads objects from an S3-compatible bucket and is discovered exactly the same way, without the engine having any hardcoded reference to S3 or the AWS SDK.

### Discovery via `java.util.ServiceLoader`

Implementations are registered using the standard Java `ServiceLoader` mechanism: each module contributes a file at

```
META-INF/services/io.github.sparqlanything.model.resources.ResourceService
```

listing the fully-qualified class name(s) of its `ResourceService` implementation(s), one per line. For example, `sparql-anything-model`'s file lists:

```
io.github.sparqlanything.model.resources.LocationInputService
io.github.sparqlanything.model.resources.CommandInputService
io.github.sparqlanything.model.resources.ContentInputService
```

`Triplifier.loadResourceServicesAll()` loads every registered implementation via `ServiceLoader.load(ResourceService.class)`, and builds a `Map<String, ResourceService>` keyed by each implementation's `@TargetOption` value. A duplicate key (two implementations registered for the same option) is treated as a configuration error and throws `IllegalStateException`.

> **Note for implementers:** the registration file must live under `META-INF/services/`, not simply `services/`, or `ServiceLoader` will silently fail to find it — there is no error, the implementation is just never discovered.

### Dispatch

`Triplifier.getInputStream(Properties)` is a thin dispatcher: it checks a fixed priority list of options —

```java
IRIArgument[] options = {IRIArgument.S3_ENDPOINT, IRIArgument.COMMAND, IRIArgument.CONTENT, IRIArgument.LOCATION};
```

— and delegates to whichever registered `ResourceService` matches the first option present in the given `Properties`. Adding support for a new input source is therefore a matter of implementing `ResourceService`, annotating it with the right `@TargetOption`, registering it via `ServiceLoader`, and adding its `IRIArgument` to this priority list if it introduces a new option.

### Root minting for non-`location` sources

`Triplifier.getRootArgument(Properties)` mints the root node of the generated Facade-X graph. For `location`-based resources, the root is derived from the (normalised) resource URL. Sources that don't have a natural URL need their own root-minting logic in this same method — for example:

- `content` and `command`: the root is `XYZ_NS` plus an MD5 hash of the content/command string, since there's no natural identifier to use.
- S3 (`s3.endpoint`): the root is the concatenation of `s3.endpoint`, `s3.bucket-name`, and `s3.key` (e.g. `http://localhost:9000/my-bucket/people.json`), so that two different objects served from the same endpoint are always minted with distinct roots.

Any new non-`location` `ResourceService` should extend `getRootArgument` similarly, ensuring the chosen root is unique for the combination of properties that identifies the resource.
