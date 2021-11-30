package com.educery.facts;

import com.educery.cogs.BlockClosure;

/**
 * A concept with some associated logic (closure).
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public class Block extends Alias {

    // block => ( topic named: name ) = fact: ( name equals: closure ) "defined"
    BlockClosure closure;

    private Block(String name, BlockClosure c) { super(name); this.closure = c; }
    public static Block named(String name, BlockClosure c) { return new Block(name, c); }

} // Block
