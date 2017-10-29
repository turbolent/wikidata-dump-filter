package com.turbolent.wikidata.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

class GzipUtils {
    static boolean isGZipped(Path path) {
        try (InputStream stream = Files.newInputStream(path)) {
            return isGZipped(stream);
        } catch (IOException e) {
            return false;
        }
    }

    static boolean isGZipped(InputStream in) {
        try {
            int magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
            return magic == GZIPInputStream.GZIP_MAGIC;
        } catch (IOException e) {
            return false;
        }
    }

    public static InputStream getInputStream(Path path) throws IOException {
        File file = path.toFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        if (isGZipped(path))
            return new GZIPInputStream(fileInputStream);
        return fileInputStream;
    }
}
