package com.educery.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BinaryOperator;
import static com.educery.utils.Logging.Empty;
import static com.educery.utils.Exceptional.runSafely;

/**
 * Convenience methods for streams and such.
 *
 * @author nik <nikboyd@sonic.net>
 * @see "Copyright 2010,2019 Nikolas S Boyd."
 * @see "Permission is granted to copy this work provided this copyright statement is retained in all copies."
 * @see <a href="https://gitlab.com/hoot-smalltalk/hoot-smalltalk/tree/master/LICENSE.txt">LICENSE for more details</a>
 */
public interface Utils {

    // NOTE: in the following T = ItemType and R = ResultType

    public static void doIf(boolean p, Runnable r) { if (p) runSafely(r); }

    public static <T> T itemOr(T defaultValue, T item) { return hasNo(item) ? defaultValue : item; }
    public static <T, R> R nullOr(Function<? super T, ? extends R> f, T item) { return hasNo(item) ? null : f.apply(item); }
    public static <T> String emptyOr(Function<? super T, String> f, T item) { return hasNo(item) ? Empty : f.apply(item); }
    public static <T> boolean falseOr(Predicate<? super T> p, T item) { return hasNo(item) ? false : p.test(item); }

    public static <T> boolean hasSome(T... items) { return !hasNo(items); }
    public static <T> boolean hasAny(T... items) { return !hasNo(items); }
    public static <T> boolean hasNo(T... items) {
        return items == null || items.length == 0 || items[0] == null; }

    public static <T> boolean hasAny(Collection<T> items) { return !hasNo(items); }
    public static <T> boolean hasNo(Collection<T> items) {
        return items == null || items.isEmpty() || items.iterator().next() == null; }

    static <T> List<T> wrapped(T... items) { return new ArrayList(Arrays.asList(items)); }
    public static <T> List<T> wrap(T... items) { return hasNo(items) ? emptyList() : wrapped(items); }
    public static <T> List<T> wrap(T head, List<T> items) { items.add(0, head); return items; }
    public static <T> List<T> wrap(T head, T... items) { List<T> list = wrap(items); list.add(0, head); return list; }
    public static <T> T[] unwrap(List<T> items, T[] sample) { return hasNo(items) ? sample : items.toArray(sample); }

    public static <T> T findFirst(Collection<T> items, Predicate<? super T> p) {
        return hasNo(items) ? null : items.stream().filter(p).findFirst().orElse(null); }

    public static <T> List<T> select(Collection<T> items, Predicate<? super T> p) { return selectList(items, p); }
    public static <T> List<T> selectList(Collection<T> items, Predicate<? super T> p) {
        return hasNo(items) ? new ArrayList() : items.stream().filter(p).collect(Collectors.toList()); }

    public static <T> Set<T> selectSet(Collection<T> items, Predicate<? super T> p) {
        return hasNo(items) ? new HashSet() : items.stream().filter(p).collect(Collectors.toSet()); }

    public static <T, R> List<R> map(Collection<T> items, Function<? super T, ? extends R> m) { return mapList(items, m); }
    public static <T, R> List<R> mapList(Collection<T> items, Function<? super T, ? extends R> m) {
        return mapList(items, it -> hasAny(it), m); }

    public static <T, R> List<R> mapList(Collection<T> items, Predicate<? super T> p, Function<? super T, ? extends R> m) {
        return hasNo(items) ? new ArrayList() : items.stream().filter(p).map(m).collect(Collectors.toList()); }

    public static <T, R> Set<R> mapSet(Collection<T> items, Function<? super T, ? extends R> m) {
        return hasNo(items) ? new HashSet() : mapSet(items, it -> hasAny(it), m); }

    public static <T, R> Set<R> mapSet(Collection<T> items, Predicate<? super T> p, Function<? super T, ? extends R> m) {
        return hasNo(items) ? new HashSet() : items.stream().filter(p).map(m).collect(Collectors.toSet()); }

    public static <T> int countAny(Collection<T> items, Predicate<? super T> p) {
        return (int)items.stream().filter(p).count(); }

    public static <T> boolean matchAny(Collection<T> items, Predicate<? super T> p) {
        return hasNo(items) ? false : items.stream().anyMatch(p); }

    public static <T> boolean matchAll(Collection<T> items, Predicate<? super T> p) {
        return hasNo(items) ? false : items.stream().allMatch(p); }

    public static String joinWith(String joint, List<String> names) {
        return hasNo(names) ? Empty : names.stream().collect(Collectors.joining(joint)); }

    public static <R> R reduce(Collection<R> items, BinaryOperator<R> op, R identity) {
        return items.stream().reduce(identity, op); }

    public static <K,V> HashMap<K,V> emptyMap() { return new HashMap(); }
    public static <R> ArrayList<R> emptyList() { return new ArrayList(); }
    public static <R> ArrayList<R> copyList(List<R> list) { return new ArrayList(list); }
    public static <R> ArrayList<R> buildList(Consumer<List<R>> c) { return buildList(emptyList(), c); }
    public static <R> ArrayList<R> buildList(ArrayList<R> list, Consumer<List<R>> c) { c.accept(list); return list; }
    public static <R> ArrayList<R> fillList(int count, final R item) {
        return buildList(results -> { int n = count; while (n-- > 0) results.add(item);} ); }

} // Utils
