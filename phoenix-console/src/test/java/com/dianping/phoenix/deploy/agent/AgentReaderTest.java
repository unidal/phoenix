package com.dianping.phoenix.deploy.agent;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Random;

import org.junit.Test;

public class AgentReaderTest {

	@Test
	public void testUnterminatedLog() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("Progress: 1/4");
		sb.append("\n\n");
		sb.append("line 1\n");
		sb.append("--9ed2b78c112fbd17a8511812c554da62941629a8--\n");
		sb.append("--255220d51dc7fb4aacddadedfe252a346da267d4--\n");
		Reader reader = new StringReader(sb.toString());
		AgentReader agentReader = new AgentReader(reader);
		while (agentReader.hasNext()) {
			AgentProgress progress = new AgentProgress();
			System.out.println(agentReader.next(progress));
		}
	}
	
	public static String randomString(int i)
	{
	    Random ran = new Random(i);
	    StringBuilder sb = new StringBuilder();
	    for (int n = 0; ; n++)
	    {
	        int k = ran.nextInt(27);
	        if (k == 0)
	            break;

	        sb.append((char)('`' + k));
	    }

	    return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(randomString(-229985452) + " " + randomString(-147909649));

		
	}

}
