package com.educery.concepts;

import java.util.*;
import com.educery.utils.*;
import static com.educery.utils.Utils.*;

/**
 * Converts between singular and plural forms of a subject.
 *
 * <h4>Number Responsibilities:</h4>
 * <ul>
 * <li>converts plural forms to their singular forms</li>
 * <li>converts singular forms to their plural forms</li>
 * <li>knows the proper article for singular and plural forms</li>
 * </ul>
 */
public class Number implements Logging {

    static final String Plural = "s";
    static final String[] Plurals = {"sses", "ues", "ies"};
    static final String[] Singulars = {"ss", "ue", "y"};
    public static final Number PluralNumber = new Number(Plurals);
    public static final Number SingularNumber = new Number(Singulars);
    public static Number getNumber(boolean plural) { return plural ? PluralNumber : SingularNumber; }

    static {
        PluralNumber.replacements.put("ues", "ue");
        PluralNumber.replacements.put("ies", "y");
        PluralNumber.replacements.put("sses", "ss");
        // PluralNumber.replacements.put(Plural, Empty);
        PluralNumber.replacements.put(Empty, Empty);
        SingularNumber.replacements.put("ue", "ues");
        SingularNumber.replacements.put("y", "ies");
        SingularNumber.replacements.put("ss", "sses");
        SingularNumber.replacements.put(Empty, Empty);
    }

    public boolean isPlural() { return this == PluralNumber; }

    public static Number getNumber(String subject) { return isSingular(subject) ? SingularNumber : PluralNumber; }
    public static boolean isSingular(String subject) { return !endsPlurally(subject); }
    public static String asSingular(String subject) { return PluralNumber.convert(subject); }
    public static String asPlural(String subject) { return toPlural(SingularNumber.convert(subject)); }
    private static String toPlural(String subject) { return subject.endsWith(Plural) ? subject : subject + Plural; }

    private final String[] suffixKeys;
    private String[] suffixKeys() { return this.suffixKeys; }
    private Number(String[] keys) { this.suffixKeys = keys; }
    protected String suffixFrom(String subject) {
        for (String suffix : suffixKeys()) {
            if (subject.endsWith(suffix)) {
                return suffix;
            }
        }
        return Empty; // no suffix found
    }

    private final HashMap<String, String> replacements = new HashMap();
    String convert(String subject) { return replaceSuffix(subject.trim()); }
    private String replaceSuffix(String subject) {
        String suffix = suffixFrom(subject);
        int rootLength = subject.length() - suffix.length();
        subject = subject.substring(0, rootLength);
        subject += this.replacements.get(suffix);
        return subject;
    }

    static final String[] PluralEnds = {
        "aes", "bs", "cs", "ds", "es",
        "fs", "gs", "hs", "ies", "js",
        "ks", "ls", "ms", "ns", "oes",
        "ps", "qs", "rs", "sses", "ts",
        "ues", "vs", "ws", "xes", "zs", // no ys!
    };
    static final List<String> EndsList = wrap(PluralEnds);

    static final Character[] Vowels = { 'a', 'e', 'i', 'o', 'u', };
    static final Character[] Vowelish = { 'a', 'e', 'i', 'o', 'u', 'h' };
    static final List<Character> VowelList = wrap(Vowelish);
    private static String normal(String s) { return s.trim().toLowerCase(); }
    private Character head(String s) { return normal(s).charAt(0); }
    private Character tail(String s) { return s.charAt(s.length() - 1); }
    public boolean needsAn(String s) { return VowelList.contains(head(s)); }
    public static boolean endsPlurally(String s) {
        String norm = normal(s); return matchAny(EndsList, (end) -> norm.endsWith(end)); }

    static final String An = "an";
    static final String Some = "some";
    static final String[] Articles = { "a", An, Some };
    public static String getArticle(String s) { return getNumber(s).getProperArticle(s); }
    public String getProperArticle(String s) {
        return isPlural() ? Articles[2] : needsAn(s) ? Articles[1] : Articles[0]; }

} // Number
