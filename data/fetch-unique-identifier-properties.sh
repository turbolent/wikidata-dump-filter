#/bin/sh

rsparql --results=csv --query=unique-identifier-properties.sparql --service="https://query.wikidata.org/sparql" \
	| tail -n +2 \
	| grep 'http://www.wikidata.org/entity/P' \
	| sed -e 's|http://www.wikidata.org/entity/P|http://www.wikidata.org/prop/direct/P|' \
	| sort \
	| uniq

