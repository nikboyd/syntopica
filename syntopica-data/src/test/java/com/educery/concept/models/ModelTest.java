package com.educery.concept.models;

import java.io.*;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.junit.*;

import com.educery.sites.ModelSite;

/**
 * Generates model pages from a sample model.
 */
public class ModelTest {

//	private static final Log Logger = LogFactory.getLog(ModelTest.class);

	private static final String PageBase = "/sample-domain";
	private static final String LinkBase = "https://github.com/nikboyd/sample-domain/blob/master";
	private static final String ImageBase = LinkBase.replace("github", "rawgithub").replace("/blob", "");

	/**
	 * Generates web pages from a model.
	 */
	@Test
	public void generatePages() {
		String templateFolder = getClass().getResource("/templates").getFile();
		String domainFolder = getClass().getResource("/sample").getFile();
		ModelSite.withTemplates(templateFolder)
			.withBases(LinkBase, ImageBase)
			.withPages(getPagesFolder(domainFolder))
			.withModel(domainFolder, "/domain.txt")
			.withMarkdown()
			.generatePages();
	}
	
	/**
	 * Locate the folder in which to generate pages (companion repo).
	 * @param domainFolder a domain sample folder
	 * @return a companion repo folder
	 */
	private String getPagesFolder(String domainFolder) {
		// domainFolder = /git-code/syntopica/syntopica-data/target/test-classes/sample
		// pagesFolder  = /git-code/sample-domain
		File result = new File(domainFolder);
		for (int parent = 0; parent < 5; parent++) {
			result = result.getParentFile();
		}
		return result.getPath() + PageBase;
	}

} // ModelTest