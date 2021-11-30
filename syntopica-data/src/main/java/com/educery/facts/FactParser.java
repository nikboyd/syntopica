package com.educery.facts;

import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.educery.utils.Logging;
import com.educery.concepts.Domain;
import static com.educery.utils.Exceptional.*;

/**
 * Parses facts from lines in a text file.
 * See grammar file Syntopica.g4 for the grammar.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class FactParser implements Logging {

    public String parsePhrase() { return Empty; }
    public String parseClause() { return Empty; }
    public String parseEquality() { return Empty; }
    public String parseFormula() { return Empty; }

    File tokenFile;
    public FactParser(File tokenFile) { this.tokenFile = tokenFile; }
    public String tokenFilepath() { return tokenFile.getAbsolutePath(); }
    CharStream createInputStream() { return nullOrTryLoudly(() -> new ANTLRFileStream(tokenFilepath())); }
    TokenSource createLexer() { return new SyntopicaLexer(createInputStream()); }

    CommonTokenStream tokenStream;
    public CommonTokenStream tokenStream() { return tokenStream; }
    CommonTokenStream tokenStream(CommonTokenStream stream) { this.tokenStream = stream; return tokenStream; }
    TokenStream createTokenStream() { return tokenStream(new CommonTokenStream(createLexer())); }
    SyntopicaParser createParser() { return new SyntopicaParser(createTokenStream()); }

    SyntopicaParser parser;
    public boolean wasParsed() { return parser != null; }
    public boolean notParsed() { return parser == null; }
    public void parseTokens() { if (wasParsed()) return; runLoudly(() -> parseUnit()); }
    void parseUnit() {
        parser = createParser();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new SyntopicaBaseListener(), parser.unit());
        Domain.getCurrentDomain().dump();
    }

} // FactParser
