# Headless Browser

## SPARQL-Anything-powered Fuseki Endpoint Inside a Docker Container


The [headless browser](https://github.com/microsoft/playwright-java) has quite a few dependencies.
You can run it natively if you get them all but it is easier to just run SPARQL-Anything in a docker container (containing all the required dependencies) when you know you need to the use the browser.

Option 1 (use a prebuilt docker image)

Run this (in a bash shell):
```
# cd into the sparql-anything git repo
docker run --rm -it -p 3000:3000 -v `pwd`:/mnt mcr.microsoft.com/playwright/java:focal bash -c 'cd /mnt ; mvn package ; java -cp /root/.m2/repository/org/slf4j/slf4j-simple/1.7.7/slf4j-simple-1.7.7.jar:/mnt/sparql.anything.fuseki/target/sparql-anything-fuseki-0.3.0-SNAPSHOT.jar com.github.spiceh2020.sparql.anything.fuseki.Endpoint'
```

<br/>

Option 2 (build your own docker image)

Run this (in a bash shell):
```
git clone https://github.com/microsoft/playwright-java.git
cd playwright-java
build -t mcr.microsoft.com/playwright/java:focal . -f Dockerfile.focal
# cd back into the sparql-anything git repo
docker run --rm -it -p 3000:3000 -v `pwd`:/mnt mcr.microsoft.com/playwright/java:focal bash -c 'cd /mnt ; mvn package ; java -cp /root/.m2/repository/org/slf4j/slf4j-simple/1.7.7/slf4j-simple-1.7.7.jar:/mnt/sparql.anything.fuseki/target/sparql-anything-fuseki-0.3.0-SNAPSHOT.jar com.github.spiceh2020.sparql.anything.fuseki.Endpoint'
```



## Notes

The first time you run a SPARQL query using the headless browser [Playwright](https://playwright.dev/java/) will download the browsers.
If you deploy this SPARQL-Anything-powered Fuseki endpoint (in this docker container) you will likely want to put a volume at the path there the browsers are saved so they don't have to be downloaded each time you start the container.
