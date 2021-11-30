package com.educery.cogs;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 * A simple message.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class UnaryMessage extends Message {

    private UnaryMessage(String s) { this.selector = s; }
    public static UnaryMessage with(String selector) { return new UnaryMessage(selector); }

} // UnaryMessage
