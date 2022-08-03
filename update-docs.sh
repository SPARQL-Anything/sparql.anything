#!/bin/bash

git add README.md .readthedocs.yaml mkdocs.yaml Configuration.md
git add formats/*
cp -f README.md docs/
cp -f BROWSER.md docs/
cp -f Configuration.md docs/
cp -f LICENSE docs/
cp -r formats docs/
git add docs/*
git commit -m "Update documentation #201 $1"
git push
