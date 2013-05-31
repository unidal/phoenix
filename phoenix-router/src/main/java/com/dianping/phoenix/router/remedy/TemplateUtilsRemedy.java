package com.dianping.phoenix.router.remedy;

import java.io.File;

import com.dianping.phoenix.router.hotdeploy.classloader.PhoenixClassLoader;

/**
 * byteman helper class
 * modify com.dianping.w3c.pagelet.template.freemarker.TemplateUtils.freemarker(1, 2, 3)'s
 * second argument i.e. templateLoaderPath to webapp dir at runtime
 * @author marsqing
 *
 */
public class TemplateUtilsRemedy {

	public Object getWebappDir(Object sndArg) {
		File projectDir = ((PhoenixClassLoader)sndArg.getClass().getClassLoader()).getProjectDir();
		return "file:" + new File(projectDir, "src/main/webapp").getAbsolutePath();
	}
	
}
