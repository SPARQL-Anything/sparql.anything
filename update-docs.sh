#!/bin/bash

git add README.md .readthedocs.yaml mkdocs.yaml
cp -f README.md docs/
cp -f BROWSER.md docs/
cp -f LICENSE docs/
git add docs/*
git commit -m "Update documentation #201 $1"
git push

