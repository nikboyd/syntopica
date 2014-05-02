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
	private static final String Cyan = "#add8e6";
	private static final String Grey = "#bfbfbf";

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
		ModelElement activity = ModelElement.named("Activity").withColor(Cyan).at(80, 50);
		ModelElement value = ModelElement.named("Value").withColor(Grey).at(180, 200);
		ModelElement business = ModelElement.named("Business").withColor(Grey).at(350, 200);
		Connector va = Connector.between(value, activity).withHeads(1).fillHeads(true).withLabel("produces");
		Connector ba = Connector.between(business, activity).withHeads(1).fillHeads(false).withLabel("for");

		int[] viewbox = { 58, 18, 443, 506 };
		Tag.Factory[] tags = { activity, value, business, va, ba };
		GraphicsContext context = GraphicsContext.with(12, 13).with(viewbox).with(tags);
		Tag page = buildPage().with(Tag.named("body").with(context.buildElement()));

		String xml = page.format();
		String pageFolder = getClass().getResource("/pages").getFile();
		File sampleFile = new File(pageFolder, "sample.html");
		FileOutputStream stream = new FileOutputStream(sampleFile);
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		writer.write(xml);
		writer.close();
	}
	
	private Tag buildPage() {
		return Tag.named("html").with(buildHeader());
	}
	
	private Tag buildHeader() {
		return Tag.named("head").with(Tag.named("title").withContent("Sample"));
	}

} // ModelTest