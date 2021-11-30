package com.educery.facts;

import java.util.*;
import org.antlr.v4.runtime.*;

import com.educery.cogs.*;
import com.educery.concepts.*;
import com.educery.utils.Logging;
import static com.educery.utils.Utils.*;
import static com.educery.facts.SyntopicaParser.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Provides glue between the parser and the facts.
 * @author nik <nikboyd@sonic.net>
 * @see "Copyright 2020 Nikolas S Boyd."
 * @see "Permission is granted to copy this work provided this copyright statement is retained in all copies."
 */
public abstract class ParserGlue extends Parser implements Logging {
    
    public ParserGlue(TokenStream s) { super(s); }
    void buildDomain(DomainContext ctx) { Domain.named(ctx.term.names); }

    void takeFact(FactContext ctx)    { Selector.withParts(predicateFrom(ctx)).buildFact(argsFrom(ctx)); }
    void takeBreed(BreedContext ctx)  { Breed.named(phraseFrom(ctx.n.names), phraseFrom(ctx.b.names)).makeTopic(); }
    void takeAlike(AliasContext ctx)  { Alike.named(aliasName(ctx), phraseFrom(ctx.n.names)).makeTopic(); }
    void takeRecord(AliasContext ctx) { Record.named(aliasName(ctx)).withParts(termsFrom(ctx.r.s)).makeTopic(); }
    void takeValues(AliasContext ctx) { ValueList.named(aliasName(ctx), ctx.v.list).makeTopic(); }
    void takeBlock(AliasContext ctx)  { Block.named(aliasName(ctx), closureFrom(ctx.b)); }
    void takeFact(AliasContext ctx)   { 
        Alike.nameFact(aliasName(ctx), Selector.withParts(predicateFrom(ctx.f)).buildFact(argsFrom(ctx.f))); }

    static String aliasName(AliasContext ctx) { return phraseFrom(ctx.a.names); }
    static List<TermList> listsFrom(List<TermListContext> cts) { return map(cts, (ctx) -> listFrom(ctx)); }
    static TermList listFrom(TermListContext ctx) { return TermList.with(opsFrom(ctx), phrasesFrom(ctx.terms)); }

    static List<String> predicateFrom(FactContext ctx) {
        List<String> list = emptyList();
        list.add(ctx.verb.getText());
        list.addAll(prepsFrom(ctx.preps));
        list = map(list, p -> chop(p));
        if (ctx.args.size() == ctx.preps.size()) {
            list.set(0, list.get(0) + Score + list.get(1));
            list.remove(1);
        }
        return list;
    }

    static List<String> argsFrom(FactContext ctx) {
        List<String> list = emptyList();
        list.add(phraseFrom(ctx.n.names));
        listsFrom(ctx.args).forEach((arg) -> list.addAll(arg.terms()));
        return list;
    }

    static BlockClosure closureFrom(BlockContext ctx)   { return BlockClosure.with(signFrom(ctx.sign), ctx.body.term); }
    static BlockClosure closureFrom(ClosureContext ctx) { return BlockClosure.with(signFrom(ctx.sign), ctx.body.term); }

    static Signature signFrom(BlockSignContext ctx) { return hasSome(ctx.ks) ? signFrom(ctx.ks): signFrom(ctx.ss); }
    static Signature signFrom(SimpleTermContext ctx) { return Signature.with(namesFrom(ctx), emptyList()); }
    static Signature signFrom(KeySelectContext ctx)  { return Signature.with(textsFrom(ctx.keys), termsFrom(ctx.args)); }
    static Signature signFrom(ClosedSignContext ctx) { 
        return hasSome(ctx) ? Signature.with(textsFrom(ctx.keys), termsFrom(ctx.args)) : new Signature(); }

    void takeSend(SendContext ctx) { ctx.term = KeywordTerm.with(ctx.f.term, nullOr(c -> keywordFrom(c), ctx.m)); }
    static KeywordMessage keywordFrom(MessageContext ctx) { return KeywordMessage.with(selectorFrom(ctx), ctx.f.term); }
    static String selectorFrom(MessageContext ctx) { return hasSome(ctx.verb) ? ctx.verb.getText() : ctx.prep.getText(); }

    void takeBinary(FormulaContext ctx) { ctx.term = Formula.with(ctx.p.term, nullOr(c -> binaryFrom(c), ctx.b)); }
    static BinaryMessage binaryFrom(BinaryContext ctx) { return BinaryMessage.with(ctx.op.getText(), ctx.b.term); };

    void takeUnary(BasicContext ctx)   { ctx.term = BasicTerm.with(unaryFrom(ctx.s)); }
    void takePrimary(BasicContext ctx) { ctx.term = BasicTerm.with(ctx.p.term, nullOr(c -> unaryFrom(c), ctx.s)); }
    static UnaryMessage unaryFrom(UnaryContext ctx) { return UnaryMessage.with(phraseFrom(ctx.selector.names)); }

    void takeNest(PrimaryContext ctx) { ctx.term = ctx.n.s.term; }
    void takeClosure(PrimaryContext ctx) { ctx.term = closureFrom(ctx.c); }
    void takeValue(PrimaryContext ctx) { ctx.term = BasicTerm.with(ctx.v.getText()); }

    void takeTerms(ValuesContext ctx)  { ctx.list.addAll(termsFrom(ctx.s)); }
    void takeValues(ValuesContext ctx) { ctx.list.addAll(valuesFrom(ctx.v)); }

    void takeProper(NamedTermContext ctx) { ctx.names.addAll(namesFrom(ctx.p)); }
    void takeSimple(NamedTermContext ctx) { ctx.names.addAll(namesFrom(ctx.s)); }

    static List<String> namesFrom(SimpleTermContext ctx) { return textsFrom(ctx.names); }
    static List<String> namesFrom(ProperTermContext ctx) { return textsFrom(ctx.names); }
    static List<String> valuesFrom(ValueListContext ctx) { return valuesFrom(ctx.terms); }
    static List<String> termsFrom(SimpleListContext ctx) { return termsFrom(ctx.terms); }
    static List<String> phrasesFrom(List<NamedTermContext> cts) { return map(cts, (term) -> phraseFrom(term.names)); }
    static List<String> termsFrom(List<SimpleTermContext> cts) { return map(cts, (term) -> phraseFrom(namesFrom(term))); }

    static List<String> prepsFrom(List<PrepositionContext> preps) { return map(preps, (prep) -> prep.p.getText()); }
    static List<String> valuesFrom(List<ValueContext> values) { return map(values, (v) -> v.n.getText()); }
    static List<String> textsFrom(List<Token> list) { return map(list, (token) -> token.getText()); }
    static List<String> opsFrom(TermListContext ctx) { return map(ctx.ops, (c) -> c.op.getText()); }

    static String phraseFrom(List<String> list) { return joinWith(Blank, list); }

} // ParserGlue
