package com.dianping.kernel.inspect.page.classpath;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.dianping.kernel.inspect.page.classpath.Artifact;
import com.dianping.kernel.inspect.page.classpath.ArtifactResolver;
import org.unidal.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class ArtifactResolverTest extends ComponentTestCase {
	@Test
	public void testResolve() throws Exception {
		ArtifactResolver resolver = lookup(ArtifactResolver.class);
		String resource = "/" + ComponentTestCase.class.getName().replace('.', '/') + ".class";
		URL url = getClass().getResource(resource);
		String path = url.getPath();
		int off = path.indexOf(':');
		int pos = path.lastIndexOf('!');
		Artifact artifact = resolver.resolve(new File(path.substring(off + 1, pos)));

		Assert.assertEquals("org.unidal.framework", artifact.getGroupId());
		Assert.assertEquals("foundation-service", artifact.getArtifactId());
		Assert.assertEquals("2.0.1-SNAPSHOT", artifact.getVersion());
	}
}
