#!/bin/bash

#
# Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

git add README.md CLI.md EXTENSION_MECHANISMS.md JAVA_LIBRARY.md Facade-X.md facade-x-ER.png .readthedocs.yaml mkdocs.yaml Configuration.md TUTORIALS.md A_GENTLE_INTRODUCTION_TO_SPARQL_ANYTHING.md FUNCTIONS_AND_MAGIC_PROPERTIES.md sparql-anything-it/src/test/java/io/github/sparqlanything/it/DocumentationExampleSandbox.java
git add formats/*
cp -f README.md docs/
cp -f EXTENSION_MECHANISMS.md docs/
cp -f Facade-X.md docs/
cp -f JAVA_LIBRARY.md docs/
cp -f facade-x-ER.png docs/
cp -f Grammar.jpg docs/
cp -f BROWSER.md docs/
cp -f Configuration.md docs/
cp -f FUNCTIONS_AND_MAGIC_PROPERTIES.md docs/
cp -f TUTORIALS.md docs/
cp -f A_GENTLE_INTRODUCTION_TO_SPARQL_ANYTHING.md docs/
cp -f LICENSE docs/
cp -r formats docs/
git add docs/*
git commit -m "#201 Update documentation '$1'"
git push
