package com.educery.facts;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 *
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class TermList implements Logging {

    ArrayList<String> ops = emptyList();
    ArrayList<String> args = emptyList();
    public List<String> terms() { return this.args; }

    private TermList(List<String> ops, List<String> args) { this.ops.addAll(ops); this.args.addAll(args); }
    public static TermList with(List<String> ops, List<String> args) { return new TermList(ops, args); }
    public void dump() {
        String sigKeys = this.ops.toString();
        String sigArgs = this.args.toString();
        report(sigKeys + sigArgs);
    }

} // TermList
