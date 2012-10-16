package com.dianping.phoenix.spi;

import java.net.URL;
import java.util.List;


public interface ClasspathBuilder {
	public List<URL> build(WebappProvider kernelProvider, WebappProvider appProvider);
}