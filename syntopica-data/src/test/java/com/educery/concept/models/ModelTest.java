package com.educery.concept.models;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

import com.educery.xml.tags.*;

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
		int[] viewbox = { 58, 18, 443, 506 };
		GraphicsContext context = 
			GraphicsContext.with(12, 13).with(viewbox)
			.with(TextBox.named("requests").withColor("#ffffff").at(300, 80))
			.with(ModelElement.named("Expector").withColor("#add8e6").at(80, 50))
			.with(ModelElement.named("Requestor").withColor("#add8e6").at(80, 180));

		Tag page = 
			Tag.named("html")
				.with(Tag.named("head").with(Tag.named("title").withContent("Sample")))
				.with(Tag.named("body").with(context.buildElement()))
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