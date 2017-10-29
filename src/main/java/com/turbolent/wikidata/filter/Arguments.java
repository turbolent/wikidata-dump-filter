package com.turbolent.wikidata.filter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class Arguments {

    final List<Path> inputPaths;
    final List<Path> predicateBlacklistPaths;
    final String outputPath;
    final boolean gzipOutput;
    final boolean printPropertyCounts;

    private Arguments(List<Path> inputPaths,
                      List<Path> predicateBlacklistPaths,
                      String outputPath,
                      boolean gzipOutput,
                      boolean printPropertyCounts)
    {
        this.inputPaths = inputPaths;
        this.predicateBlacklistPaths = predicateBlacklistPaths;
        this.outputPath = outputPath;
        this.gzipOutput = gzipOutput;
        this.printPropertyCounts = printPropertyCounts;
    }

    static Arguments parse(String[] args) {
        Iterator<String> argIterator = Arrays.asList(args).iterator();
        boolean parseOptions = true;

        List<Path> inputPaths = new ArrayList<>();
        List<Path> predicateBlacklistPaths = new ArrayList<>();
        boolean gzipOutput = false;
        String outputPath = null;
        boolean printPropertyCounts = false;

        while (argIterator.hasNext()) {
            String arg = argIterator.next();

            if (parseOptions) {
                Option option = Option.fromArgument(arg);
                if (option != null) {
                    switch (option) {
                        case PREDICATE_BLACKLIST: {
                            Path path = Paths.get(argIterator.next());
                            predicateBlacklistPaths.add(path);
                            continue;
                        }
                        case GZIP_OUTPUT: {
                            gzipOutput = Boolean.valueOf(argIterator.next());
                            continue;
                        }
                        case OUTPUT: {
                            outputPath = argIterator.next();
                            continue;
                        }
                        case PRINT_PROPERTY_COUNTS: {
                            printPropertyCounts = Boolean.valueOf(argIterator.next());
                            continue;
                        }
                    }
                }

                parseOptions = false;
            }

            Path inputPath = Paths.get(arg);
            inputPaths.add(inputPath);
        }

        return new Arguments(inputPaths, predicateBlacklistPaths,
                             outputPath, gzipOutput, printPropertyCounts);
    }
}
