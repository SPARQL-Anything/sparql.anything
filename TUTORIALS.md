# Tutorials
Here is a list of step-by-step tutorials covering several features of SPARQL Anything:

- [A Gentle introduction to SPARQL Anything](A_GENTLE_INTRODUCTION_TO_SPARQL_ANYTHING.md): this tutorial presents the Facade-X model and shows basic transformations of JSON, CSV and XML. [Video](https://www.dropbox.com/s/bc31v0klg68op0z/SPARQLAnythingTutorial-highres.mp4?dl=0) 
- [The PySPARQL-Anything Showacase](https://bit.ly/pysa-demo): this tutorial shows how to install and execute SPARQL Anything from Python, getting both a readily available CLI as well as leveraging SPARQL Anything output as Pandas or RDFLib objects in your scripts.
- [SPARQL Anything showcase: open data from the Tate Gallery](https://github.com/SPARQL-Anything/showcase-tate): this tutorial covers formats such as CSV and JSON and features such as the function `fx:anySlot` and the chaining of multiple `SERVICE` clauses. It is based on the SPARQL Anything [CLI](README.md#Usage).
- [Construct a KG of artists and artworks of the IMMA museum website](https://github.com/SPARQL-Anything/showcase-imma): This showcase demonstrates the use of SPARQL Anything for constructing a Knowledge Graph from data encoded in HTML pages. Apart from examples with the HTML input format, it covers features such as parametrised queries and the use of SPARQL result set files as parameters. It is based on the SPARQL Anything [CLI](README.md#Usage).
- [Construct a KG from the Propbank dataset](https://github.com/SPARQL-Anything/showcase-propbank): An advanced example of transformation of XML data, including querying a Zip archive.
- [Construct a KG from YAML annotations in Markdown file headers](https://github.com/SPARQL-Anything/showcase-polifonia-ecosystem): A short but complex case demonstrating how to chain multiple transformations starting from a set of Markdown files, queried to extract the YAML header, which is in turn queried to derive the annotations, that are in turn projected into a KG!
- [Populate a Music Ontology from MusicXML files](https://github.com/SPARQL-Anything/showcase-musicxml): An advanced application of SPARQL Anything to query MusicXML files and derive note sequences, computing n-grams, and populating a Music Notation ontology.
- [Querying a Relational Database](https://github.com/justin2004/weblog/blob/master/relational_as_graph/README.md): this tutorials shows you how to query a relational database by using SPARQL Anything (even if the tool does not support yet this feature natively).
- [How cool would it be to have a single query to get an overview of the content of any input sources?](https://colab.research.google.com/drive/1R5zeIx4IutF0cc4oTc45CrXjdTSGP4eW?usp=sharing#scrollTo=MgVgJ526sd3e): This is a Colab notebook describing how to build a SPARQL Anything query to get an overview of the schema of any source. The query is plain SPARQL 1.1 but thanks to Facade-X model and SPARQL Anything it also works with any format. Related Discussion: [#508](https://github.com/SPARQL-Anything/sparql.anything/discussions/508)


# Online presentations
- [SPARQL Anything@Estes Park Group, May 4 2023](https://youtu.be/geH7eAeQdVY)
