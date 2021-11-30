package com.educery.cogs;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 * A basic term.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class BasicTerm implements Logging {

    protected BasicTerm() {}
    private BasicTerm(String value) { this(); this.value = value; }
    private BasicTerm(BasicTerm term, Message m) { this.base = term; this.message = m; }

    protected String value = "";
    public String value() { return this.value; }
    public static BasicTerm with(String value) { return new BasicTerm(value); }

    protected BasicTerm base;
    public BasicTerm base() { return this.base; }
    public static BasicTerm with(BasicTerm term, Message m) { return new BasicTerm(term, m); }

    protected Message message;
    public Message message() { return this.message; }
    public static BasicTerm with(Message m) { return new BasicTerm(null, m); }

} // BasicTerm
