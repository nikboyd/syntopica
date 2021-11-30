package com.educery.cogs;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 * A binary message.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class BinaryMessage extends Message {

    private BinaryMessage(String op, List<BasicTerm> args) { this.selector = op; this.args.addAll(args); }
    public static BinaryMessage with(String op, BasicTerm term) {
        BasicTerm[] args = { term }; return new BinaryMessage(op, wrap(args)); }

} // BinaryMessage
