package com.dianping.kernel.inspect.page.classpath;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.dianping.kernel.inspect.page.classpath.Artifact;
import com.dianping.kernel.inspect.page.classpath.ArtifactResolver;
import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class ArtifactResolverTest extends ComponentTestCase {
	@Test
	public void testResolve() throws Exception {
		ArtifactResolver resolver = lookup(ArtifactResolver.class);
		String resource = "/" + ComponentTestCase.class.getName().replace('.', '/') + ".class";
		URL url = getClass().getResource(resource);
		String path = url.getPath();
		int off = path.lastIndexOf(':');
		int pos = path.lastIndexOf('!');
		Artifact artifact = resolver.resolve(new File(path.substring(off + 1, pos)));

		Assert.assertEquals("com.site.common", artifact.getGroupId());
		Assert.assertEquals("lookup", artifact.getArtifactId());
		Assert.assertEquals("1.1.4", artifact.getVersion());
	}
}
