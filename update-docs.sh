#!/bin/bash

git add README.md .readthedocs.yaml mkdocs.yaml
git add format/*
cp -f README.md docs/
cp -f BROWSER.md docs/
cp -f LICENSE docs/
cp -r format docs/
git add docs/*
git commit -m "Update documentation #201 $1"
git push
