package com.educery.concepts;

import java.io.*;
import java.util.*;

import com.educery.utils.*;
import com.educery.tags.*;
import static com.educery.utils.Exceptional.*;

/**
 * Reads a topic discussion from a file.
 *
 * <h4>TopicReader Responsibilities:</h4>
 * <ul>
 * <li>reads a topic discussion</li>
 * <li>prepares a topic discussion for page rendering</li>
 * </ul>
 */
public class TopicReader extends LineReader {

    private static final String Break = "<br/>";

    private final HashMap<String, String> linkMap = new HashMap<>();
    public Map<String, String> getLinkMap() { return this.linkMap; }

    protected TopicReader(InputStream stream) { super(stream); }
    public static TopicReader with(InputStream stream) { return new TopicReader(stream); }
    public static TopicReader from(File modelFile) { return nullOrTryLoudly(() -> with(new FileInputStream(modelFile))); }

    private final StringBuilder builder = new StringBuilder();
    private void append(String text) { this.builder.append(text); }
    public String readDiscussion() { builder.setLength(0); readTopic(); return this.builder.toString(); }
    private void readTopic() { readLines(line -> readLine(line)); }

    private static final String NewLine = "\n";
    private void readTopic(String line) { append(line.trim()); append(NewLine); }
    private void readLine(String line) { if (line.contains(Tag.Equals)) readLink(line); else readTopic(line); }
    private void readLink(String line) { saveLink(line.split(Tag.Equals.trim())); }
    private void saveLink(String... links) {
        String definedTerm = links[0].trim();
//        Domain.getCurrentDomain().getTopic(definedTerm).makeDefined();
        this.linkMap.put(definedTerm, links[1].trim()); }

} // TopicReader
