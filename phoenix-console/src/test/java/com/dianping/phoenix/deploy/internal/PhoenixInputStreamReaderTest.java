package com.dianping.phoenix.deploy.internal;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class PhoenixInputStreamReaderTest {

	@Test
	public void test() {

		final AtomicInteger readCnt = new AtomicInteger(0);
		final AtomicBoolean skipCalled = new AtomicBoolean(false);

		PhoenixInputStreamReader pr = new PhoenixInputStreamReader(null, 1000, 5) {

			@Override
			protected Reader getReader(String url) throws IOException {
				Reader r = mock(Reader.class);
				doAnswer(new Answer<Integer>() {

					@Override
					public Integer answer(InvocationOnMock invo) throws Throwable {
						int curCnt = readCnt.getAndIncrement();
						switch (curCnt) {
						case 0:	
							((char[]) invo.getArguments()[0])[0] = (char) readCnt.get();
							return 1;
						case 1:
							throw new IOException();
						case 2:
							((char[]) invo.getArguments()[0])[0] = (char) readCnt.get();
							((char[]) invo.getArguments()[0])[1] = (char) (readCnt.get() + 1);
							return 2;
						case 3:
							return -1;
						}
						throw new RuntimeException("...");
					}
				}).when(r).read(any(char[].class));

				doAnswer(new Answer<Long>() {

					@Override
					public Long answer(InvocationOnMock invocation) throws Throwable {
						skipCalled.set(true);
						Assert.assertEquals(1L, invocation.getArguments()[0]);
						return 1L;
					}
				}).when(r).skip(anyLong());

				return r;
			}

		};

		char[] cbuf = new char[1];
		int len = pr.read(cbuf);
		Assert.assertEquals(1, len);
		Assert.assertEquals((char) 1, cbuf[0]);
		cbuf = new char[10];
		len = pr.read(cbuf);
		Assert.assertTrue(skipCalled.get());
		Assert.assertEquals(2, len);
		Assert.assertEquals((char)3, cbuf[0]);
		Assert.assertEquals((char)4, cbuf[1]);
	}

}
