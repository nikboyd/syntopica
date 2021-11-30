package com.educery.concepts;

import java.io.*;
import com.educery.utils.*;
import static com.educery.utils.Exceptional.*;

/**
 * Reads facts from a domain model file.
 *
 * <h4>FactReader Responsibilities:</h4>
 * <ul>
 * <li>creates the facts and topics found in a model file</li>
 * </ul>
 */
public class FactReader extends LineReader {

    protected FactReader(InputStream s) { super(s); }
    public static FactReader with(InputStream s) { return new FactReader(s); }
    public static FactReader from(File file) { return nullOrTryLoudly(() -> with(new FileInputStream(file))); }
//    private void readFact(String line) { if (!line.trim().isEmpty()) Fact.parseFrom(line); }
//    public void readFacts() { readLines(line -> readFact(line)); }

} // FactReader
