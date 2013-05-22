package com.dianping.maven.plugin.phoenix;

public class DefaultF5Manager implements F5Manager {

	@Override
	public F5Pool poolForProject(String projectName) {
		// TODO
		F5Pool pool = new F5Pool();
		pool.setProjectName(projectName);
		if("user-web".equals(projectName)) {
			pool.setPoolName("Web.Web_X_Userweb");
			pool.setUrl("http://127.0.0.1:8080/_user-web%s");
		} else if("shop-web".equals(projectName)) {
			pool.setPoolName("Web.Web_X_Shop");
			pool.setUrl("http://127.0.0.1:8080/_shop-web%s");
		} else if("dpindex-web".equals(projectName)) {
			pool.setPoolName("Web.Web_X_Dpindex");
			pool.setUrl("http://127.0.0.1:8080/_dpindex-web%s");
		} else if("shoplist-web".equals(projectName)) {
			pool.setPoolName("Web.Web_X_Shoplist");
			pool.setUrl("http://127.0.0.1:8080/_shoplist-web%s");
		} else {
			pool = null;
		}
		return pool;
	}

}
