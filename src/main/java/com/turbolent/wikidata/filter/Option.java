package com.turbolent.wikidata.filter;

enum Option {
    PREDICATE_BLACKLIST,
    GZIP_OUTPUT,
    OUTPUT,
    PRINT_PROPERTY_COUNTS;

    private static final String PREFIX = "--";

    public static Option fromArgument(String arg) {
        if (!arg.startsWith(PREFIX))
            return null;
        int prefixLength = PREFIX.length();
        if (arg.length() <= prefixLength)
            return null;
        String name = arg.substring(prefixLength)
            .replace('-', '_').toUpperCase();
        try {
            return Option.valueOf(name);
        } catch (IllegalArgumentException e) {
            String message = String.format("Unknown option: %s", arg);
            throw new IllegalArgumentException(message);
        }
    }
}
