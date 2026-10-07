[![DOI](https://zenodo.org/badge/303967701.svg)](https://zenodo.org/badge/latestdoi/303967701)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java 21](https://github.com/sparql-anything/sparql.anything/actions/workflows/build_on_maven_java21.yml/badge.svg?branch=v1.3-DEV)](https://github.com/sparql-anything/sparql.anything/actions/workflows/build_on_maven_java21.yml)
[![Docs](https://readthedocs.org/projects/sparql-anything/badge/?version=latest)](https://sparql-anything.readthedocs.io/)
[![Documentation Status](https://readthedocs.org/projects/sparql-anything/badge/?version=latest)](https://sparql-anything.readthedocs.io/en/latest/)

# SPARQL Anything

SPARQL Anything is a system for Semantic Web re-engineering that allows users to query anything with SPARQL: CSV, JSON, XML, spreadsheets, HTML, Markdown, archives and more, without converting them first. It implements [Façade-X](docs/Facade-X.md), a minimal meta-model that represents any data source as RDF.

## Quickstart

Download `sparql-anything-<version>.jar` from the [latest release](https://github.com/SPARQL-Anything/sparql.anything/releases/latest) (Java 21 or later), then:

```bash
java -jar sparql-anything-<version>.jar -q 'SELECT * WHERE { SERVICE <x-sparql-anything:https://sparql-anything.cc/example1.json> { ?s ?p ?o } }'
```

SPARQL Anything is also available as a [server](docs/README.md#using-the-server), a [Java library](docs/JAVA_LIBRARY.md), a [Python library](https://github.com/SPARQL-Anything/PySPARQL-Anything) and a [Docker image](docs/BROWSER.md).

## Documentation

- [Documentation](https://sparql-anything.readthedocs.io/) (sources in [docs/](docs/README.md))
- [Supported formats](docs/README.md#supported-formats)
- [Configuration](docs/Configuration.md) · [Functions](docs/FUNCTIONS_AND_MAGIC_PROPERTIES.md) · [Command line](docs/CLI.md)
- [Tutorials](docs/TUTORIALS.md)
- [Developer guide](docs/DEVELOPER_GUIDE.md)

## Community

Questions and ideas in [Discussions](https://github.com/SPARQL-Anything/sparql.anything/discussions), bugs and feature requests in [Issues](https://github.com/SPARQL-Anything/sparql.anything/issues). The approach is being standardised in the [W3C Data Façades Community Group](https://www.w3.org/community/data-facades/).

## How to cite

If you use SPARQL Anything in your research, please cite:

> Asprino, L., Daga, E., Gangemi, A., Mulholland, P. (2023). Knowledge Graph Construction with a Façade: A Unified Method to Access Heterogeneous Data Sources on the Web. ACM Transactions on Internet Technology, 23(1). https://doi.org/10.1145/3555312

More publications in [docs/README.md](docs/README.md#how-to-cite-our-work).

## Licence

[Apache 2.0](LICENSE)