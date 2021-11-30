package com.educery.cogs;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 * A closure that evaluates to a result, often a logical or numerical value.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class BlockClosure extends BasicTerm {

    Signature sign;

    private BlockClosure(Signature s) { this.sign = s; }
    public static BlockClosure with(Signature s, BasicTerm body) {
        BlockClosure result = new BlockClosure(s);
        result.base = body.base();
        result.message = body.message();
        return result;
    }

} // BlockClosure
