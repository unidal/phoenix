package com.dianping.phoenix.dev.core.tools.remedy;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Test;
import org.w3c.dom.Document;

import com.dianping.phoenix.dev.core.tools.remedy.PomRemedy;

public class PomRemedyTest {

	@Test
	public void shouldAddPluginElementIfNotPresent() throws Exception {
		File pom = new File("src/test/resources/com/dianping/phoenix/dev/core/tools/remedy/sample_pom.xml");
		assertTrue(pom.exists());
		Document doc = PomRemedy.INSTANCE.remedy(pom);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PomRemedy.INSTANCE.writeDocument(out, doc);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><project>\n"
				+ "<build><plugins><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-eclipse-plugin</artifactId><version>2.9</version><configuration><ajdtVersion>none</ajdtVersion></configuration></plugin></plugins></build></project>";
		assertEquals(expected, new String(out.toByteArray()));
	}

	@Test
	public void shouldBumpVersionAndAddAjdt() throws Exception {
		File pom = new File("src/test/resources/com/dianping/phoenix/dev/core/tools/remedy/sample_pom2.xml");
		assertTrue(pom.exists());
		Document doc = PomRemedy.INSTANCE.remedy(pom);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PomRemedy.INSTANCE.writeDocument(out, doc);
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><project>\n"
				+ "<build>\n<plugins>\n" + "<plugin>\n" + "<groupId>org.apache.maven.plugins</groupId>\n"
				+ "<artifactId>maven-eclipse-plugin</artifactId>\n" + "<version>2.9</version>\n"
				+ "<configuration><ajdtVersion>none</ajdtVersion></configuration></plugin>\n"
				+ "</plugins>\n</build>\n" + "</project>";
		assertEquals(expected, new String(out.toByteArray()));
	}
}
