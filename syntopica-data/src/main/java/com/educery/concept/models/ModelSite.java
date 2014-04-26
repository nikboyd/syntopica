package com.educery.concept.models;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

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
		HashMap<String, Object> root = new HashMap<String, Object>();
		root.put("topic", topic);
		root.put("domain", Domain.getCurrentDomain());
		
		try {
			File pageFile = new File(this.pageFolder, topic.getLinkFileName());
			Writer writer = new OutputStreamWriter(new FileOutputStream(pageFile));
			getTemplate("page-template.html").process(root, writer);
		}
		catch (Exception e ) {
			Logger.error(e.getMessage(), e);
		}
	}

	private Template getTemplate(String templateName) throws Exception {
		return this.cfg.getTemplate(templateName);
	}

} // ModelSite