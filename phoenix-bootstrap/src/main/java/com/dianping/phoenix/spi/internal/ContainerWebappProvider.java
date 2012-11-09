package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.bootstrap.AbstractCatalinaWebappLoader;
import com.dianping.phoenix.spi.WebappProvider;

public class ContainerWebappProvider implements WebappProvider {

	private URLClassLoader parentLoader;

	public ContainerWebappProvider(ClassLoader parentLoader){
		if(parentLoader instanceof URLClassLoader){
			this.parentLoader = (URLClassLoader)parentLoader;
		}else{
			throw new IllegalArgumentException("The parent ClassLoader must be URLClassLoader");
		}
		
	}

	@Override
	public List<File> getClasspathEntries() {
		List<File> list = new ArrayList<File>();

		URL[] urls = this.parentLoader.getURLs();
		if(urls != null && urls.length > 0){
			for(URL url : urls){
				String urlStr = url.toString();
				if(urlStr.endsWith(".jar")){
					try {
						list.add(new File(url.toURI()));
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return list;
	}

	@Override
	public File getWarRoot() {
		return null;
	}
}