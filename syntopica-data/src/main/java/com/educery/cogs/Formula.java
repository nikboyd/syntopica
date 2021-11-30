package com.educery.cogs;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 * A formula. A binary message sent to a term.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class Formula extends BasicTerm {

    private Formula(BasicTerm base, Message m) { this.base = base; this.message = m; }
    public static Formula with(BasicTerm base, BinaryMessage m) { return new Formula(base, m); }

} // Formula
