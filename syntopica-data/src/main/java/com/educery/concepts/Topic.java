package com.educery.concepts;

import java.util.*;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.educery.utils.Registry;
//import com.educery.utils.Tag;

/**
 * Describes a topic and its associations.
 * 
 * <h4>Topic Responsibilities:</h4>
 * <ul>
 * <li>knows a title</li>
 * <li>knows a discussion (if given)</li>
 * <li>knows some associations (if given)</li>
 * <li></li>
 * </ul>
 */
public class Topic implements Registry.KeySource {
	
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
	public static class Number {

		private static final String[] Articles = { "a", "an", "some" };
		private static final String[] Vowels = { "a", "e", "i", "o", "u", "h" };
		private static final List<String> VowelList = Arrays.asList(Vowels);

		private static final String Plural = "s";
		private static final String[] Plurals = { "ues", "ies", "ess", Plural };
		private static final String[] Singulars = { "ue", "y", "ess" };

		public static final Number PluralNumber = new Number(Plurals);
		public static final Number SingularNumber = new Number(Singulars);

		static {
			PluralNumber.replacements.put("ues", "ue");
			PluralNumber.replacements.put("ies", "y");
			PluralNumber.replacements.put("ess", "ess");
			PluralNumber.replacements.put(Plural, "");

			SingularNumber.replacements.put("ue", "ues");
			SingularNumber.replacements.put("y", "ies");
			SingularNumber.replacements.put("ess", "ess");
		}
		
		/**
		 * Returns an appropriate Number.
		 * @param plural indicates plural needed
		 * @return a Number
		 */
		public static Number getNumber(boolean plural) {
			return (plural ? PluralNumber : SingularNumber);
		}
		
		/**
		 * Converts a plural subject to its singular form.
		 * @param subject a subject
		 * @return a singular form of the subject
		 */
		public static String convertToSingular(String subject) {
			return PluralNumber.convert(subject);
		}
		
		/**
		 * Converts a singular subject to its plural form.
		 * @param subject a subject
		 * @return a plural form of the subject
		 */
		public static String convertToPlural(String subject) {
			String result = SingularNumber.convert(subject);
			return (result.endsWith(Plural) ? result : result + Plural);
		}
		
		private String[] suffixKeys;
		private HashMap<String, String> replacements = new HashMap<String, String>();
		
		/**
		 * Constructs a new Number.
		 * @param keys the keys used to find replacements.
		 */
		private Number(String[] keys) {
			this.suffixKeys = keys;
		}
		
		/**
		 * Converts a subject to its inverse number.
		 * @param subject a subject
		 * @return an inverse form of the subject
		 */
		String convert(String subject) {
			String result = subject.trim();
			for (String suffix : this.suffixKeys) {
				if (result.endsWith(suffix)) {
					int rootLength = result.length() - suffix.length();
					result = result.substring(0, rootLength);
					result += this.replacements.get(suffix);
					return result; // only one!
				}
			}
			return result;
		}
		
		/**
		 * Returns an appropriate article for a given subject.
		 * @param subject a subject
		 * @return an appropriate article
		 */
		public String getArticle(String subject) {
			if (this.isPlural()) return Articles[2];
			String first = String.valueOf((subject.charAt(0)));
			return (VowelList.contains(first) ? Articles[1] : Articles[0] );
		}
		
		/**
		 * Indicates whether this number is plural.
		 * @return whether this number is plural
		 */
		public boolean isPlural() {
			return this == PluralNumber;
		}

	} // Number


	private static final Log Logger = LogFactory.getLog(Topic.class);
	private static final String PageType = ".html";

	private String title = Empty;
	private boolean defined = false;
	private String discussion = Empty;
	private Registry<Fact> facts = Registry.empty();
	
	/**
	 * Returns an appropriate Number given a subject.
	 * @param subject a subject
	 * @return a Number
	 */
	public static Number getNumber(String subject) {
		String singular = Number.convertToSingular(subject);
		return Number.getNumber(!singular.equals(subject));
	}
	
	/**
	 * Returns a list of topic names extracted from a comma-separated list.
	 * @param topicNames a comma-separated list
	 * @return a list of topic names
	 */
	public static List<String> namesFrom(String topicNames) {
		return Arrays.asList(topicNames.split(Comma));
	}
	
	/**
	 * Returns a new Topic.
	 * @param title a title
	 * @return a new Topic
	 */
	public static Topic named(String title) {
		Topic result = new Topic();
		result.title = Number.convertToSingular(title);
		return result;
	}
	
	/**
	 * Adds a discussion to this topic.
	 * @param discussion a discussion
	 * @return this Topic
	 */
	public Topic with(String discussion) {
		setDiscussion(discussion);
		return this;
	}
	
	/**
	 * Adds some facts to this Topic.
	 * @param facts some facts
	 * @return this Topic
	 */
	public Topic with(Fact ... facts) {
		for (Fact fact : facts) {
			this.facts.register(fact);
		}
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String getKey() { return getTitle(); }

	/**
	 * The title of this topic.
	 * @return a title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * The discussion associated with this topic.
	 * @return a discussion
	 */
	public String getDiscussion() {
		return this.discussion;
	}

	/**
	 * Adds a discussion to the topic.
	 * @param discussion a discussion
	 */
	public void setDiscussion(String discussion) {
		this.discussion = discussion;
	}
	
	/**
	 * Makes this a definition.
	 * @return this Topic
	 */
	public Topic makeDefined() {
		this.defined = true;
		return this;
	}
	
	/**
	 * Returns the facts associated with this topic.
	 * @return facts
	 */
	public Fact[] getFacts() {
		Fact[] sample = { };
		List<Fact> results = getFactRegistry().getItems();
		return results.toArray(sample);
	}
	
	/**
	 * The fact registry.
	 */
	Registry<Fact> getFactRegistry() {
		return this.facts;
	}
	
	/**
	 * Formats a reference link as an HTML fragment.
	 * @param aNumber indicates whether plural or singular
	 * @return a formatted HTML fragment
	 */
//	public String formatReferenceLink(Number aNumber, String pageType) {
//		return Blank + getArticle(aNumber) + Blank + getReferenceLink(aNumber, Empty, pageType);
//	}
	
	/**
	 * Returns a link for this topic with its name capitalized.
	 * @param linkBase a link base path
	 * @param pageType indicates a page type
	 * @return a page reference link
	 */
//	public String getCapitalizedLink(String pageType) {
//		return Tag.linkWith(getLinkFileName(pageType))
//				.withContent(getSubject()).format();
//	}

	/**
	 * Returns a link for this topic with a pluralized name (if so indicated).
	 * @param aNumber indicates whether a plural is needed
	 * @return a page reference link
	 */
//	public String getReferenceLink(Number aNumber) {
//		if (getFactRegistry().isEmpty()) return getSubject(aNumber);
//		return Tag.linkWith(getLinkFileName())
//				.withContent(getSubject(aNumber)).format();
//	}
	
	/**
	 * Returns a singular article for this topic.
	 * @return an article
	 */
	public String getArticle() {
		return getArticle(Number.SingularNumber);
	}
	
	/**
	 * Returns an appropriate article for this topic
	 * @param aNumber indicates whether a plural is needed
	 * @return an article
	 */
	public String getArticle(Number aNumber) {
		return aNumber.getArticle(getTitle());
	}
	
	/**
	 * Returns the name of this topic.
	 * @param aNumber indicates whether a plural is needed
	 * @return a topic name
	 */
	public String getSubject(Number aNumber) {
		if (!aNumber.isPlural()) return getTitle();
		return Number.convertToPlural(getTitle());
	}
	
	/**
	 * Returns the name of the topic (capitalized).
	 * @return a topic name
	 */
	public String getSubject() {
		return  WordUtils.capitalize(getTitle());
	}
	
	/**
	 * Returns the link name of this topic.
	 * @return a link name
	 */
	public String getLinkName() {
		return getTitle().replace(Blank, Period);
	}
	
	/**
	 * Returns the page name of this topic.
	 * @return a page file name
	 */
	public String getLinkFileName() {
		return getLinkName() + PageType;
	}
	
	/**
	 * Returns the page name of this topic.
	 * @param pageType a page type
	 * @return a page file name
	 */
	public String getLinkFileName(String pageType) {
		return getLinkName() + pageType;
	}
	
	/**
	 * Dumps this topic.
	 */
	public void dump() {
		Logger.info("topic: " + getTitle());
		if (this.defined) {
			getFacts()[0].dumpMessage();
		}
	}

} // Topic