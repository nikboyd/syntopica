package com.educery.utils;

import java.util.*;
import static com.educery.utils.Utils.*;

/**
 * Contains registered items.
 *
 * <h4>Registry Responsibilities:</h4>
 * <ul>
 * <li>knows the registered items and their keys</li>
 * <li>registers item</li>
 * <li>provides an items given its key</li>
 * <li>removes an item if requested</li>
 * </ul>
 *
 * @param <ItemType> an item type
 */
public class Registry<ItemType extends Registry.KeySource> implements Logging {

    /**
     * Defines a protocol for accessing a registry key.
     *
     * <h4>KeySource Responsibilities:</h4>
     * <ul>
     * <li>provides a key for locating items in a registry</li>
     * <li>provides a few useful constants for implementing classes</li>
     * </ul>
     */
    public interface KeySource extends Logging {

        default String getKey() { return Empty; }

    } // KeySource

    private Registry() { } // prevent external construction
    public Registry<ItemType> with(ItemType item) { register(item); return this; }
    public Registry<ItemType> without(ItemType item) { remove(item); return this; }
    public static <ItemType extends Registry.KeySource> Registry<ItemType> empty() { return new Registry(); }

    static final String[] NoItems = { };
    private final ArrayList<String> order = emptyList();
    private List<String> order() { return this.order; }
    public String[] getItemOrder() { return unwrap(order(), NoItems); }

    private final HashMap<String, ItemType> items = new HashMap();
    private Map<String, ItemType> items() { return this.items; }
    public ItemType register(ItemType item) { return addItem(item); }
    private ItemType addItem(ItemType item) { if (!okKey(item)) return item; 
        if (!hasItem(item.getKey())) adopt(item); return items().get(item.getKey()); }

    public void remove(ItemType item) { if (okKey(item)) {
        if (hasItem(item.getKey())) orphan(item); else reportMissing(item); } }

    private void orphan(ItemType item) { String key = item.getKey().trim(); order().remove(key); items().remove(key); }
    private void adopt(ItemType item) { String key = item.getKey().trim(); items().put(key, item); order().add(key); }

    public boolean okKey(String key) { return !noKey(key); }
    public boolean okKey(ItemType item) { return hasSome(item) && okKey(item.getKey()); }
    public boolean noKey(String key) { return hasNo(key) || key.trim().isEmpty(); }
    public boolean hasItem(String key) { return okKey(key) && items().containsKey(key.trim()); }
    public boolean hasItem(ItemType item) { return hasSome(item) && hasItem(item.getKey()); }
    public boolean isEmpty() { return items().isEmpty(); }
    public void clear() { items().clear(); order().clear(); }
    public int countItems() { return items().size(); }

    public ItemType getItem(String key) { return noKey(key) ? null : items().get(key.trim()); }
    public List<ItemType> getItems() { return buildList(list -> order().forEach((key) -> list.add(items().get(key)))); }

    static final String MissingItem = "can't find an item: '%s'";
    private void reportMissing(ItemType item) { warn(format(MissingItem, item.getKey())); }

} // Registry
