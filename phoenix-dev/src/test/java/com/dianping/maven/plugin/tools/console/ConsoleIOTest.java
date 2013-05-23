package com.dianping.maven.plugin.tools.console;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConsoleIOTest {

	@Test
	public void shouldReadSingleChoice() throws IOException {
		
		List<String> availableValues = new ArrayList<String>();
		availableValues.add("A");
		availableValues.add("B");
		availableValues.add("C");
		availableValues.add("D");
		
		int column = 3;
		PrintWriter out = new PrintWriter(new StringWriter());
		Reader in = new StringReader("1\n");
		List<String> choosed = new ConsoleIO().choose(availableValues, column, "", out, in);
		assertEquals(availableValues.subList(1, 2), choosed);
	}
	
	@Test
	public void shouldReadMultipleChoice() throws IOException {
		
		List<String> availableValues = new ArrayList<String>();
		availableValues.add("A");
		availableValues.add("B");
		availableValues.add("C");
		availableValues.add("D");
		
		int column = 3;
		PrintWriter out = new PrintWriter(new StringWriter());
		Reader in = new StringReader("0,1\n");
		List<String> choosed = new ConsoleIO().choose(availableValues, column, "", out, in);
		assertEquals(availableValues.subList(0, 2), choosed);
	}
	
	@Test
	public void shouldIgnoreSpace() throws IOException {
		
		List<String> availableValues = new ArrayList<String>();
		availableValues.add("A");
		availableValues.add("B");
		availableValues.add("C");
		availableValues.add("D");
		
		int column = 3;
		PrintWriter out = new PrintWriter(new StringWriter());
		Reader in = new StringReader(" 0, 1 \n");
		List<String> choosed = new ConsoleIO().choose(availableValues, column, "", out, in);
		assertEquals(availableValues.subList(0, 2), choosed);
	}
	
	@Test
	public void shouldIgnoreMultipleComma() throws IOException {
		
		List<String> availableValues = new ArrayList<String>();
		availableValues.add("A");
		availableValues.add("B");
		availableValues.add("C");
		availableValues.add("D");
		
		int column = 3;
		PrintWriter out = new PrintWriter(new StringWriter());
		Reader in = new StringReader(" 0,, 1, \n");
		List<String> choosed = new ConsoleIO().choose(availableValues, column, "", out, in);
		assertEquals(availableValues.subList(0, 2), choosed);
	}
	
	@Test
	public void shouldIgnoreUnknownCharacter() throws IOException {
		
		List<String> availableValues = new ArrayList<String>();
		availableValues.add("A");
		availableValues.add("B");
		availableValues.add("C");
		availableValues.add("D");
		
		int column = 3;
		PrintWriter out = new PrintWriter(new StringWriter());
		Reader in = new StringReader("0x1\n");
		List<String> choosed = new ConsoleIO().choose(availableValues, column, "", out, in);
		assertEquals(0, choosed.size());
	}

}
