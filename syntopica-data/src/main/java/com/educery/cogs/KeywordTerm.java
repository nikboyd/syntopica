package com.educery.cogs;

/**
 * A keyword term. A keyword message sent to a formula.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class KeywordTerm extends BasicTerm {

    private KeywordTerm(BasicTerm base, Message m) { this.base = base; this.message = m; }
    public static KeywordTerm with(Formula f, KeywordMessage m) { return new KeywordTerm(f, m); }

} // KeywordTerm
