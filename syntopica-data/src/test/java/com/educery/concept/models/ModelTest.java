package com.educery.concept.models;

import org.junit.*;

/**
 * Generates model pages from a sample model.
 */
public class ModelTest {
	
	@Test
	public void generatePages() {
		String pageFolder = getClass().getResource("/pages").getFile();
		String templateFolder = getClass().getResource("/templates").getFile();
		ModelSite.withTemplates(templateFolder)
			.withPages(pageFolder)
			.withModel("/sample-domain.txt")
			.generatePages();
	}

} // ModelTest