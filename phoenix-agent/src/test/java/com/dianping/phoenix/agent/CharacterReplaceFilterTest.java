package com.dianping.phoenix.agent;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.phoenix.agent.util.CharacterReplaceFilterWriter;

public class CharacterReplaceFilterTest {

	@Test
	public void testWriteString() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(out);
		Writer filtedWriter = new CharacterReplaceFilterWriter(writer, '\n', "<br/>");
		filtedWriter.write("a\nb\nc\n");
		filtedWriter.close();
		Assert.assertEquals("a<br/>b<br/>c<br/>", out.toString());
	}
	
	@Test
	public void testWriteStringWithOffset() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(out);
		Writer filtedWriter = new CharacterReplaceFilterWriter(writer, '\n', "<br/>");
		filtedWriter.write("a\nb\nc\n", 1, 2);
		filtedWriter.close();
		Assert.assertEquals("<br/>b", out.toString());
	}
	
	@Test
	public void testWriteSingleChar() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(out);
		Writer filtedWriter = new CharacterReplaceFilterWriter(writer, '\n', "<br/>");
		filtedWriter.write('a');
		filtedWriter.write('\n');
		filtedWriter.write('b');
		filtedWriter.close();
		Assert.assertEquals("a<br/>b", out.toString());
	}
	
	@Test
	public void testWriteCharArray() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(out);
		Writer filtedWriter = new CharacterReplaceFilterWriter(writer, '\n', "<br/>");
		filtedWriter.write(new char[]{'a', '\n', 'b'});
		filtedWriter.close();
		Assert.assertEquals("a<br/>b", out.toString());
	}
	
	@Test
	public void testWriteCharArrayWithOffset() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(out);
		Writer filtedWriter = new CharacterReplaceFilterWriter(writer, '\n', "<br/>");
		filtedWriter.write(new char[]{'a', '\n', 'b', '\n', 'c'}, 1, 2);
		filtedWriter.close();
		Assert.assertEquals("<br/>b", out.toString());
	}
	
}
