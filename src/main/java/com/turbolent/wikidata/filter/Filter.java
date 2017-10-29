package com.turbolent.wikidata.filter;

import com.google.common.base.Stopwatch;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.parser.Callback;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.util.CallbackNxBufferedWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static com.google.common.collect.Sets.newHashSet;

class Filter {

    private static Logger logger =
        LoggerFactory.getLogger(Main.class);

    private Set<Node> filteredPredicates =
        newHashSet(Schema.NAME, SKOS.PREF_LABEL);

    private final List<Path> inputPaths;
    private final Map<Node, Integer> predicateCounts = new HashMap<>();
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private final Callback callback;
    private final BufferedWriter bufferedWriter;
    private int total = 0;
    private int kept = 0;
    private final boolean printPropertyCounts;

    Filter(Arguments arguments) throws IOException {
        inputPaths = arguments.inputPaths;
        printPropertyCounts = arguments.printPropertyCounts;

        OutputStream stream = System.out;

        if (arguments.outputPath != null)
            stream = Files.newOutputStream(Paths.get(arguments.outputPath));

        if (arguments.gzipOutput)
            stream = new GZIPOutputStream(stream);

        OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        bufferedWriter = new BufferedWriter(writer);
        callback = new CallbackNxBufferedWriter(bufferedWriter);

        for (Path path : arguments.predicateBlacklistPaths) {
            try (Stream<String> predicates = Files.lines(path)) {
                predicates
                    .map(Resource::new)
                    .forEach(filteredPredicates::add);
            }
        }
    }

    void run() throws IOException {
        callback.startDocument();
        stopwatch.start();

        for (Path inputPath : inputPaths)
            filter(inputPath);

        stopwatch.stop();
        callback.endDocument();

        bufferedWriter.flush();
        bufferedWriter.close();

        float percentage = kept * 100.f / total;
        logger.info(String.format("%d/%d (-%.2f%%) in %s",
                                  kept, total,
                                  100.f - percentage,
                                  stopwatch.toString()));

        if (printPropertyCounts) {
            predicateCounts.entrySet().stream()
                .sorted(Map.Entry.<Node, Integer>comparingByValue().reversed())
                .map(Object::toString)
                .forEach(logger::info);
        }
    }

    private void filter(Path path) throws IOException {
        NxParser nxp = new NxParser();
        try (InputStream stream = GzipUtils.getInputStream(path)) {

            nxp.parse(stream);

            for (Node[] statement : nxp) {
                total += 1;

                if (filter(statement))
                    continue;

                callback.processStatement(statement);

                if (printPropertyCounts) {
                    predicateCounts.merge(statement[1], 1,
                                          (old, one) -> old + one);
                }

                kept += 1;
            }

        }
    }

    private boolean filter(Node[] statement) {
        Node predicate = statement[1];
        if (filteredPredicates.contains(predicate))
            return true;

        Node object = statement[2];
        if (object instanceof Literal) {
            Literal literal = (Literal) object;
            String languageTag = literal.getLanguageTag();
            if (!(languageTag == null || languageTag.equals("en")))
                return true;
        }

        return false;
    }
}
