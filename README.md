# Wikidata Dump Filter

Filter a [Wikidata RDF Dump](https://www.mediawiki.org/wiki/Wikibase/Indexing/RDF_Dump_Format) 

## Usage

- Split the dump into parts: `./split.sh latest-truthy.nt.gz parts/` 
- Filter the parts in parallel: `ls parts/part_* | ./run.sh out/`
