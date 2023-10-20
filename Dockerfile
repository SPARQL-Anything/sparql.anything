#### build layer
FROM maven:3.8.6-openjdk-11-slim as build
ARG GITHUB_REF
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME

ADD pom.xml $HOME
ADD sparql-anything-archive/pom.xml $HOME/sparql-anything-archive/
ADD sparql-anything-bib/pom.xml $HOME/sparql-anything-bib/
ADD sparql-anything-binary/pom.xml $HOME/sparql-anything-binary/
ADD sparql-anything-cli/pom.xml $HOME/sparql-anything-cli/
ADD sparql-anything-csv/pom.xml $HOME/sparql-anything-csv/
ADD sparql-anything-docs/pom.xml $HOME/sparql-anything-docs/
ADD sparql-anything-engine/pom.xml $HOME/sparql-anything-engine/
ADD sparql-anything-fuseki/pom.xml $HOME/sparql-anything-fuseki/
ADD sparql-anything-html/pom.xml $HOME/sparql-anything-html/
ADD sparql-anything-it/pom.xml $HOME/sparql-anything-it/
ADD sparql-anything-json/pom.xml $HOME/sparql-anything-json/
ADD sparql-anything-markdown/pom.xml $HOME/sparql-anything-markdown/
ADD sparql-anything-metadata/pom.xml $HOME/sparql-anything-metadata/
ADD sparql-anything-model/pom.xml $HOME/sparql-anything-model/
ADD sparql-anything-parser/pom.xml $HOME/sparql-anything-parser/
ADD sparql-anything-rdf/pom.xml $HOME/sparql-anything-rdf/
ADD sparql-anything-spreadsheet/pom.xml $HOME/sparql-anything-spreadsheet/
ADD sparql-anything-testutils/pom.xml $HOME/sparql-anything-testutils/
ADD sparql-anything-text/pom.xml $HOME/sparql-anything-text/
ADD sparql-anything-xml/pom.xml $HOME/sparql-anything-xml/
ADD sparql-anything-yaml/pom.xml $HOME/sparql-anything-yaml/

RUN mvn verify clean --fail-never
ADD . $HOME
RUN mvn clean install -DskipTests -Dgenerate-server-jar=true -Drevision=$GITHUB_REF

#### runtime layer
FROM mcr.microsoft.com/playwright/java:focal
ARG GITHUB_REF
LABEL description="SPARQL Anything"
RUN apt-get update && apt-get install locales
RUN sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV HOME "/app"

WORKDIR $HOME
COPY --from=build $HOME/sparql-anything-fuseki/target/sparql-anything-server-$GITHUB_REF.jar $HOME/sparql-anything-server.jar

RUN chown -R 10001:0 $HOME && chmod -R og+rwx $HOME
USER 10001

VOLUME /data
EXPOSE 3000

CMD java -jar /app/sparql-anything-server.jar