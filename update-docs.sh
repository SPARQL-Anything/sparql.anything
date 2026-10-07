#!/bin/bash
set -e
cd sparql-anything-documentation-generator
mvn -q exec:java -Dexec.mainClass="io.github.sparqlanything.documentationgenerator.DocumentationGenerator" -Dexec.args="../docs/formats/"
cd ..
git status --short docs/