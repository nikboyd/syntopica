package com.educery.cogs;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 * A keyword message.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class KeywordMessage extends Message {

    private KeywordMessage(String keyword, List<BasicTerm> args) { this.selector = keyword; this.args.addAll(args); }
    public static KeywordMessage with(String keyword, BasicTerm f) {
        BasicTerm[] args = { f }; return new KeywordMessage(keyword, wrap(args)); }

} // KeywordMessage
