package com.educery.cogs;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 * A closure signature.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class Signature implements Logging {

    ArrayList<String> keys = emptyList();
    ArrayList<String> args = emptyList();

    public Signature() {}
    private Signature(List<String> keys, List<String> args) { this.keys.addAll(keys); this.args.addAll(args); }
    public static Signature with(List<String> keys, List<String> args) { return new Signature(keys, args); }
    public void dump() {
        String sigKeys = this.keys.toString();
        String sigArgs = this.args.toString();
        report(sigKeys + sigArgs);
    }

} // Signature
