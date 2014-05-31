package com.educery.utils;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class Registry<ItemType extends Registry.KeySource> {

	private static final Log Logger = LogFactory.getLog(Registry.class);
	
	/**
	 * Defines a protocol for accessing a registry key.
	 * 
	 * <h4>KeySource Responsibilities:</h4>
	 * <ul>
	 * <li>provides a key for locating items in a registry</li>
	 * <li>provides a few useful constants for implementors</li>
	 * </ul>
	 */
	public interface KeySource {

		public static final String Empty = "";
		public static final String Blank = " ";
		public static final String Colon = ":";
		public static final String Comma = ",";
		public static final String Period = ".";
		public static final String Equals = " = ";

		/**
		 * Returns a registry key.
		 * @return a registry key
		 */
		public String getKey();

	} // KeySource

	private ArrayList<String> order = new ArrayList<String>();
	private HashMap<String, ItemType> items = new HashMap<String, ItemType>();	
	private Registry() { } // prevent external construction
	
	/**
	 * Returns a new empty Registry.
	 * @return a new empty Registry
	 */
	public static <ItemType extends Registry.KeySource> Registry<ItemType> empty() {
		return new Registry<ItemType>();
	}

	/**
	 * Adds an item to this registry.
	 * @param item a registered item
	 * @return this Registry
	 */
	public Registry<ItemType> with(ItemType item) {
		register(item);
		return this;
	}

	/**
	 * Removes an item from this registry.
	 * @param item a registered item
	 * @return this Registry
	 */
	public Registry<ItemType> without(ItemType item) {
		remove(item);
		return this;
	}

	/**
	 * Adds an item to this registry.
	 * @param item a registered item
	 * @return the item
	 */
	public ItemType register(ItemType item) {
		if (item == null) return null;
		String key = item.getKey().trim();
		if (!this.items.containsKey(key)) {
			this.items.put(key, item);
			this.order.add(key);
		}
		return getItem(key);
	}
	
	/**
	 * Removes an item from this registry.
	 * @param item a registered item
	 */
	public void remove(ItemType item) {
		if (item.getKey() == null) {
			reportMissingItem(item.getKey());
			return;
		}

		this.order.remove(item.getKey().trim());
		this.items.remove(item.getKey().trim());
	}

	/**
	 * Indicates whether this registry contains an item.
	 * @param key an item key
	 * @return whether this registry contains an item
	 */
	public boolean hasItem(String key) {
		return this.items.containsKey(key);
	}
	
	/**
	 * Removes all items from this registry.
	 */
	public void clear() {
		this.items.clear();
		this.order.clear();
	}
	
	/**
	 * Indicates whether this registry is empty.
	 * @return whether empty
	 */
	public boolean isEmpty() {
		return this.items.isEmpty();
	}
	
	/**
	 * Returns a registered item.
	 * @param itemKey an item key
	 * @return a registered item, or null
	 */
	public ItemType getItem(String itemKey) {
		if (itemKey == null) return null;
		return this.items.get(itemKey.trim());
	}
	
	/**
	 * Counts the items in this registry.
	 * @return a count of the registered items
	 */
	public int countItems() {
		return this.items.size();
	}

	/**
	 * The registered items.
	 * @return the items
	 */
	public List<ItemType> getItems() {
		ArrayList<ItemType> results = new ArrayList<ItemType>();
		for (String key : this.order) {
			results.add(this.items.get(key));
		}
		return results;
	}
	
	private void reportMissingItem(String itemKey) {
		Logger.warn("can't find an item: " + itemKey);
	}

} // Registry