package com.educery.concept.models;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

/**
 * Generates model pages from a sample model.
 */
public class ModelTest {

	private static final Log Logger = LogFactory.getLog(ModelTest.class);

	/**
	 * Generates web pages from a model.
	 */
	@Test
	public void generatePages() {
		String pageFolder = getClass().getResource("/pages").getFile();
		String templateFolder = getClass().getResource("/templates").getFile();
		ModelSite.withTemplates(templateFolder)
			.withPages(pageFolder)
			.withModel("/sample-domain.txt")
			.generatePages();
	}
	
	/**
	 * Generates SVG sample page.
	 * @throws Exception
	 */
	@Test
	public void svgSample() throws Exception {
		GraphicsFactory factory = new GraphicsFactory();
		Tag example = factory.makeModelElement("Expector", "#add8e6", 80, 50);
		Tag sample = factory.makeModelElement("Requestor", "#add8e6", 80, 180);
		Tag page = 
			Tag.named("html")
				.with(Tag.named("head").with(Tag.named("title").withContent("Sample")))
				.with(Tag.named("body").with(factory.makeGraphicsContext().with(example).with(sample)))
				;

		String xml = page.format();
		String pageFolder = getClass().getResource("/pages").getFile();
		File sampleFile = new File(pageFolder, "sample.html");
		FileOutputStream stream = new FileOutputStream(sampleFile);
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		writer.write(xml);
		writer.close();
	}

} // ModelTest