package com.educery.utils;

import java.io.*;
import java.util.function.Consumer;
import static com.educery.utils.Exceptional.*;

/**
 * Reads lines from a stream.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class LineReader implements Logging {

    protected BufferedReader reader;
    protected BufferedReader reader() { return this.reader; }
    protected LineReader(InputStream s) { this.reader = new BufferedReader(new InputStreamReader(s)); }
    public void readLines(Consumer<String> c) { runLoudly(() -> { readAllLines(c); }); }
    private void readAllLines(Consumer<String> c) throws IOException {
        try (BufferedReader r = reader()) {
            for (String line = r.readLine(); line != null; line = r.readLine()) c.accept(line); }}

} // LineReader
