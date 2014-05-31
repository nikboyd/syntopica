package com.educery.sites;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import freemarker.template.*;

import com.educery.concepts.*;
import com.educery.graphics.Point;
import com.educery.tags.*;
import com.educery.utils.Tag;

/**
 * Generates a web site from a domain model.
 * 
 * <h4>ModelSite Responsibilities:</h4>
 * <ul>
 * <li>reads domain model facts from a model file</li>
 * <li>generates model pages from the domain model</li>
 * </ul>
 *
 * <h4>Client Responsibilities:</h4>
 * <ul>
 * <li>provide locations for a model file and page folder during construction</li>
 * </ul>
 */
public class ModelSite {

	private static final Log Logger = LogFactory.getLog(ModelSite.class);

	private static final String Format = "UTF-8";

	private File pageFolder;
	private Configuration cfg = new Configuration();
	
	/**
	 * Returns a new ModelSite.
	 * @param templateFolder a page template folder
	 * @return a new ModelSite
	 */
	public static ModelSite withTemplates(String templateFolder) {
		ModelSite result = new ModelSite();
		result.initialize(templateFolder);
		return result;
	}

	private ModelSite() { }
	
	private void initialize(String templateFolder) {
		try {
			cfg.setDirectoryForTemplateLoading(new File(templateFolder));
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setDefaultEncoding(Format);
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Configures this site with a page folder.
	 * @param pageFolder a page folder
	 * @return this ModelSite
	 */
	public ModelSite withPages(String pageFolder) {
		this.pageFolder = new File(pageFolder);
		return this;
	}

	/**
	 * Configures this site with a model.
	 * @param modelFile a model file
	 * @return this ModelSite
	 */
	public ModelSite withModel(String modelFile) {
		FactReader.with(getClass().getResourceAsStream(modelFile)).readFacts();
		return this;
	}

	/**
	 * Generates the model site pages.
	 */
	public void generatePages() {
		List<Topic> topics = Domain.getCurrentDomain().getTopics().getItems();
		for (Topic topic : topics) generatePage(topic);
		Logger.info("generated " + topics.size() + " pages");
	}
	
	private void generatePage(Topic topic) {
		HashMap<String, Object> rootMap = new HashMap<String, Object>();
		rootMap.put("topic", topic);
		rootMap.put("domain", Domain.getCurrentDomain());
		rootMap.put("diagram", buildDiagram(topic));
		
		try {
			File pageFile = new File(this.pageFolder, topic.getLinkFileName());
			Writer writer = new OutputStreamWriter(new FileOutputStream(pageFile));
			getTemplate("page-template.html").process(rootMap, writer);
		}
		catch (Exception e ) {
			Logger.error(e.getMessage(), e);
		}
	}
	
	public String buildDiagram(Topic topic) {
		int[] viewbox = { 10, 10, 500, 500 };
		Tag.Factory[] tags = buildTags(topic.getFacts()[0]);
		Canvas canvas = Canvas.with(12, 13).with(viewbox).with(tags);
		return canvas.drawElement().format();
	}
	
	private Tag.Factory[] buildTags(Fact fact) {
		ModelElement[] elements = buildModels(fact);
		Connector[] connectors = buildConnectors(fact.getPredicate(), elements);
		ArrayList<Tag.Factory> results = new ArrayList<>();
		results.addAll(Arrays.asList(elements));
		results.addAll(Arrays.asList(connectors));
		return results.stream().toArray(Tag.Factory[]::new);
	}

	private Connector[] buildConnectors(Selector factSelector, ModelElement[] elements) {
		if (elements.length < 2) return new Connector[0];
		String[] labels = factSelector.getParts();
		Connector[] results = new Connector[labels.length];
		for (int index = 0; index < labels.length; index++) {
			results[index] = Connector.named(labels[index]);
			if (index > 0) results[index].emptyHeads();
			if (index < elements.length - 1) {
				results[index].between(elements[index + 1], elements[0]);
			}
		}
		elements[0].addTails(results);
		for (int index = 0; index < results.length; index++) {
			elements[index + 1].addHeads(results[index]);
		}
		return results;
	}
	
	private ModelElement[] buildModels(Fact fact) {
		Point p = Point.at(50, 50);
		String[] topics = fact.getTopics();
		ModelElement[] results = new ModelElement[topics.length];
		for (int index = 0; index < topics.length; index++) {
			results[index] = ModelElement.named(topics[index]);
			if (index > 0) {
				Point delta = Point.at(index * 160, 0);
				results[index].withGrey().at(p.plus(delta));
			}
			else {
				results[index].withCyan().at(p);
				p = p.plus(Point.at(-20, 150));
			}
		}
		return results;
	}

	private Template getTemplate(String templateName) throws Exception {
		return this.cfg.getTemplate(templateName);
	}

} // ModelSite