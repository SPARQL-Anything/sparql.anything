## v1.2.0-RC1 — Release Candidate

This is a **release candidate** for 1.2.0, published for the community to test before the final release. Please try it out and report any problems on the issue tracker.

⚠️ Pre-release: behaviour may still change before 1.2.0.

Notable changes since v1.1.0 include streaming CONSTRUCT output (`--stream`, #636 / #635), and fixes for `-f trig` / `-f trix` (#642), the stray `tmp` folder and the SLF4J NOP-logger warnings (#640), plus clearer `read-from-std-in` docs (#641). Full list of planned changes: see the 1.2.0 milestone.

### Install

Download the attached CLI jar and run:

```bash
java -jar sparql-anything-1.2.0-RC1.jar -q "SELECT * { ... }"
```

or the server:

```bash
java -jar sparql-anything-server-1.2.0-RC1.jar
```

Maven:

```xml
<dependency>
    <groupId>io.github.sparql-anything</groupId>
    <artifactId>sparql-anything-libs</artifactId>
    <version>1.2.0-RC1</version>
</dependency>
```
