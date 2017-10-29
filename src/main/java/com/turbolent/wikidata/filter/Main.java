package com.turbolent.wikidata.filter;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1)
            throw new IllegalArgumentException("Missing paths");

        Arguments arguments = Arguments.parse(args);
        new Filter(arguments).run();
    }
}