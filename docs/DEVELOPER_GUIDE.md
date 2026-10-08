# Developer Guide

This guide is for people working on SPARQL Anything itself: building it, changing the code, writing tests and documentation, and making releases. For using SPARQL Anything, see the rest of the documentation.

## Table of contents

- [Getting started](#getting-started)
- [Module layout](#module-layout)
- [Coding conventions](#coding-conventions)
- [Testing](#testing)
- Architecture
    - [Reading input resources: the service-provider pattern](#reading-input-resources-the-service-provider-pattern)
    - Triplifier architecture — TODO
    - Facade-X graph builders — TODO
    - The query execution pipeline (`FacadeX.ExecutorFactory`) — TODO
    - Functions and magic properties — TODO
    - The plugin loading mechanism — TODO
- [Documentation](#documentation)
- [Branches and continuous integration](#branches-and-continuous-integration)
- [Releasing](#releasing)
- [Contributing](#contributing)

## Getting started

Requirements: Java 21 and Maven 3.9 or later.

```bash
git clone https://github.com/SPARQL-Anything/sparql.anything.git
cd sparql.anything
mvn clean install
```

`mvn clean install` compiles all modules and runs the tests. Add `-DskipTests` for a faster build.

The runnable jars are built only on request, through Maven profiles activated by properties:

| Property | Output |
|---|---|
| `-Dgenerate-cli-jar=true` | `sparql-anything-cli/target/sparql-anything-<version>.jar` |
| `-Dgenerate-server-jar=true` | `sparql-anything-fuseki/target/sparql-anything-server-<version>.jar` |
| `-Dgenerate-cli-jar-geosparql=true` | CLI jar with GeoSPARQL support (`…-geosparql.jar`) |
| `-Dgenerate-server-jar-geosparql=true` | Server jar with GeoSPARQL support (`…-geosparql.jar`) |

`./regen.sh` is a shortcut for `mvn install -DskipTests -Dgenerate-cli-jar=true`.

### Versioning

The project uses Maven [CI-friendly versions](https://maven.apache.org/maven-ci-friendly.html): every module declares `${revision}`, set once in the root `pom.xml` (for example `1.3.0-SNAPSHOT`). The `flatten-maven-plugin` resolves it in the published POMs. To build a different version without editing the POM, pass `-Drevision=<version>`. This is what the release workflows do.

## Module layout

All modules are listed in the root `pom.xml`.

| Module | Content |
|---|---|
| `sparql-anything-model` | Core abstractions: `Triplifier`, Facade-X graph builders, `IRIArgument`, annotations (`@Format`, `@Option`, `@Example`, …), input `ResourceService`s |
| `sparql-anything-engine` | Query execution: `FacadeX.ExecutorFactory`, execution strategies, caching, functions and magic properties |
| `sparql-anything-parser` | Parser for the `x-sparql-anything:` service IRI |
| `sparql-anything-csv`, `-json`, `-xml`, `-html`, `-yaml`, `-markdown`, `-text`, `-binary`, `-archive`, `-spreadsheet`, `-docs`, `-slides`, `-bibliography`, `-metadata`, `-rdf` | One triplifier module per format family |
| `sparql-anything-s3-support` | `ResourceService` for S3-compatible object stores |
| `sparql-anything-libs` | Aggregates the engine and all triplifiers. This is the dependency to use from Java. |
| `sparql-anything-cli` | Command line interface |
| `sparql-anything-fuseki` | SPARQL server (Fuseki-based) with YASGUI |
| `sparql-anything-testutils` | Shared test harness (`AbstractTriplifierTester`, `TestUtils`) |
| `sparql-anything-it` | Integration and regression tests across modules |
| `sparql-anything-documentation-generator` | Generates `docs/formats/*.md` from triplifier annotations. Not published to Maven Central. |

`sparql-anything-jdbc` is in the repository but excluded from the build.

## Coding conventions

- Java 21 (`<release>21</release>`).
- Formatting follows `.editorconfig`: tabs for indentation in Java files, UTF-8, final newline.
- Every source file starts with the Apache 2.0 licence header used throughout the code base (`Copyright (c) <year> SPARQL Anything Contributors @ http://github.com/sparql-anything`).
- Log through SLF4J (`LoggerFactory.getLogger(...)`), never `System.out`.
- Triplifier options are declared with `@Option` (name, description, default value, valid values) and examples with `@Example`. These annotations are the source of the format documentation, so keep descriptions accurate and self-contained.
- New engine options and properties belong to the Engine namespace (`fxe:`, `http://sparql.xyz/facade-x/engine/`). The old `fx:` forms are kept for backward compatibility only.
- Reference the GitHub issue in commit messages (`#NNN`; `Fix #NNN` closes it on merge).

## Testing

Tests use JUnit 4 and run with `mvn test` (or as part of `mvn install`).

**Triplifier tests** extend `AbstractTriplifierTester` (module `sparql-anything-testutils`). The harness derives file names from the test method name: `testFoo` reads the input `Foo.<extension>` and the expected output `Foo.<expectedExtension>` from the test resources. It then runs the triplifier with the given properties and checks that the result is isomorphic to the expected graph. Adding a case usually means adding a pair of resource files and a short test method.

**Integration tests** live in `sparql-anything-it` and run full SPARQL queries through the engine.

**Regression tests for issues** go in `sparql-anything-it/.../IssuesTest.java` as `testIssueNNN`, with the query and input files under `src/test/resources/issues/` (`issueNNN.sparql`, plus any input data). Every bug fix should come with one.

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

## Documentation

### Rationale

- **Single source.** All documentation lives in `docs/`, next to the code, so it is versioned with it. Each development branch carries the documentation for its own version. The root `README.md` is only a welcome page that links here.
- **Generated where possible.** The format pages in `docs/formats/` are produced from the `@Format`, `@Option` and `@Example` annotations on each triplifier. Option names, defaults and examples therefore always match the code.
- **Published per version.** Read the Docs builds the site with MkDocs from `mkdocs.yaml` on every push, one version per branch.

### Workflow

- Edit pages in `docs/`. New pages need an entry in the `nav` section of `mkdocs.yaml`.
- Do not edit `docs/formats/*.md` by hand. Change the triplifier annotations, rebuild, and run `./update-docs.sh`, which runs the documentation generator and writes to `docs/formats/`.
- Preview locally:
```bash
  pip install -r docs/requirements.txt
  mkdocs serve
```
- Links to issues written as `#NNN` are turned into GitHub links by `hooks/gh_issue_links.py`.

## Branches and continuous integration

- Development happens on `vX.Y-DEV` (currently `v1.3-DEV`), which is the default branch on GitHub. Older `vX.Y-DEV` branches are kept for reference.
- Larger changes go on a feature branch (for example `docs/604-profile`) and are merged through a pull request into `vX.Y-DEV`.
- GitHub Actions (`.github/workflows/`):

| Workflow | Trigger | What it does |
|---|---|---|
| `build_on_maven_java21.yml`, `…_windows.yml` | push / PR on `vX.Y-DEV` | `mvn clean install` on Linux and Windows |
| `codeql-analysis.yml` | push / PR on `vX.Y-DEV`, weekly | CodeQL security analysis |
| `nightly-release.yml` | nightly, on issue close, manual | Builds the CLI and server jars as `X.Y.Z-NIGHTLY-SNAPSHOT` and publishes them as a GitHub pre-release `snapshot-<date>` |
| `mvn-nightly-release.yml` | nightly, on issue close, manual | Deploys `X.Y.Z-NIGHTLY-SNAPSHOT` to the Maven Central snapshots repository |
| `draft-release.yml` | milestone closed | Creates a draft GitHub release from the milestone |
| `docker-image.yml` | release published | Builds and pushes the Docker image, attaches the CLI and server jars (with GeoSPARQL variants) to the release |
| `publish_to_mvn-central.yml` | release published | Deploys the release to Maven Central |

Dependabot opens pull requests for dependency updates against the development branch.

## Releasing

Each release corresponds to a GitHub milestone (for example `v1.3.0`).

1. **Prepare.** All issues in the milestone are closed or moved to the next one. The CI builds on `vX.Y-DEV` are green. `docs/` is up to date (run `./update-docs.sh` if any triplifier annotations changed).
2. **Release candidate (optional).** Create a GitHub release with tag `vX.Y.Z-RC1` on `vX.Y-DEV`, marked as pre-release. Write the notes for the community: what to test, notable changes, and install instructions for the CLI jar, the server jar and the Maven dependency.
3. **Close the milestone.** `draft-release.yml` creates a draft release listing the milestone's issues.
4. **Edit the draft.** Set the tag to `vX.Y.Z` on `vX.Y-DEV` and review the notes.
5. **Publish.** Publishing the release triggers:
    - `publish_to_mvn-central.yml`: deploys to Maven Central with `-Drevision=X.Y.Z` (the tag without the leading `v`), signing with the project GPG key (`-DperformRelease=true` activates the `release-and-sign-artifacts` profile);
    - `docker-image.yml`: builds and pushes the Docker image and attaches the jars to the release.
6. **Check.** The artifacts are on Maven Central, the jars are attached to the release, the Docker image is on Docker Hub, and Read the Docs shows the release version.
7. **Start the next development cycle** (see commit `6290130f` for an example):
    - create the branch `vX.(Y+1)-DEV` and set it as the default branch on GitHub;
    - update the branch filters in the workflows (`build_on_maven_java21*.yml`, `codeql-analysis.yml`), the badges in `README.md` and `docs/README.md`, and `edit_uri` in `mkdocs.yml`;
    - bump `<revision>` in the root `pom.xml` to `X.(Y+1).0-SNAPSHOT`;
    - activate the new branch as a version on Read the Docs.

Secrets used by the release workflows: `OSSRH_USERNAME`, `OSSRH_TOKEN`, `OSSRH_GPG_SECRET_KEY`, `OSSRH_GPG_SECRET_KEY_PASSWORD`, `DOCKER_USERNAME`, `DOCKER_PASSWORD`. The repository variable `DOCKER_REPO` is also required.

## Contributing

- Questions and ideas: [GitHub Discussions](https://github.com/SPARQL-Anything/sparql.anything/discussions).
- Bugs and feature requests: [GitHub Issues](https://github.com/SPARQL-Anything/sparql.anything/issues). For bugs, include a minimal query and input file.
- Pull requests target the current `vX.Y-DEV` branch. They should reference the issue they address, include tests (a regression test in `IssuesTest` for bugs), and update the documentation in `docs/` when behaviour or options change.
- Contributions are licensed under Apache 2.0.
- The Façade-X approach is being standardised in the [W3C Data Façades Community Group](https://www.w3.org/community/data-facades/), which is open to anyone interested.
