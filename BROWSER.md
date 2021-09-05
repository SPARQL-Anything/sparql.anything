# Headless Browser

## SPARQL-Anything-powered Fuseki Endpoint Inside a Docker Container


The [headless browser](https://github.com/microsoft/playwright-java) has quite a few dependencies.
You can run it natively if you get them all but it is easier to just run SPARQL-Anything in a docker container (containing all the required dependencies) when you know you need to the use the browser.

Follow these steps (in a bash shell) to build and run the docker container:

0) have Docker [installed](https://docs.docker.com/get-docker/)
1) `git clone` this repository and cd into it
2) ``docker build -f Dockerfile.development -t sparql-anything-development .``
3) ``docker run -v sparql-anything_playwright:/ms-playwright -v sparql-anything_m2:/root/.m2 -p 3000:3000 --rm -it -v `pwd`:/app sparql-anything-development``

## Notes

The first time you run a SPARQL query using the headless browser [Playwright](https://playwright.dev/java/) will download the browsers.
The docker volume at /ms-playwright is to save the browsers so they don't have to be downloaded each time you re-run (3).
The docker volume at /root/.m2 is to make subsequent builds faster.
