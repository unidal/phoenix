/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-6-5
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.phoenix.dev.core.tools.generator.stable;

import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Leo Liang
 * 
 */
public class GitRepositoryListGenerator {
	private static final String REPO_BASEURL = "http://code.dianpingoa.com/";
	private static final String PATTERN_PRE = "git clone http://code.dianpingoa.com/";
	private static final String PATTERN_SUF = "</pre>";
	private static final String PATTERN_GIT = ".git";

	public static void main(String[] args) throws Exception {
		List<ProjectRepositoryPair> pairs = parse();
		persist(pairs, new File("/Users/marsqing/gitRepo.properties"));
	}

	private static void persist(List<ProjectRepositoryPair> pairs, File file) throws Exception {
		StringBuilder content = new StringBuilder();
		for (ProjectRepositoryPair pair : pairs) {
			content.append(pair.name).append("=").append(pair.repoUrl).append("\n");
		}
		FileUtils.writeStringToFile(file, content.toString());
	}

	private static List<ProjectRepositoryPair> parse() throws Exception {
		List<ProjectRepositoryPair> pairs = new ArrayList<ProjectRepositoryPair>();

		List<ProjectRepositoryPair> temp = null;
		int pageNo = 1;

		while (true) {
			temp = parsePage(pageNo++);
			if (temp == null) {
				break;
			} else {
				pairs.addAll(temp);
			}
		}

		return pairs;
	}

	private static List<ProjectRepositoryPair> parsePage(int pageNo) throws Exception {
		String page = curl(REPO_BASEURL + "/public/projects?page=" + pageNo);
		List<ProjectRepositoryPair> pairs = null;

		if (StringUtils.isNotBlank(page)) {
			String[] lines = StringUtils.split(page, "\n");
			for (String line : lines) {
				if (StringUtils.isNotBlank(line)) {
					int pos = line.indexOf(PATTERN_PRE);
					if (pos >= 0) {
						pairs = pairs == null ? new ArrayList<ProjectRepositoryPair>() : pairs;
						String repoUrl = line.substring(pos + PATTERN_PRE.length() - REPO_BASEURL.length(),
								line.length() - PATTERN_SUF.length());
						String projectName = repoUrl.substring(repoUrl.lastIndexOf("/") + 1, repoUrl.length()
								- PATTERN_GIT.length());

						if(hasPom(repoUrl)) {
							pairs.add(new ProjectRepositoryPair(projectName, repoUrl));
						}
					}
				}
			}
		}

		return pairs;
	}
	

	private static boolean hasPom(String repoUrl) throws Exception {
		// http://code.dianpingoa.com/f2e-ba/group.git
		// http://code.dianpingoa.com/f2e-ba/group/blob/master/pom.xml
		String pomUrl = repoUrl.substring(0, repoUrl.length() - 4) + "/blob/master/pom.xml";
		return curl_code(pomUrl) != 404;
	}

	private static int curl_code(String url) throws Exception {
		URL reqUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) reqUrl.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(3000);

		conn.setRequestProperty(
				"Cookie",
				"uid=code51c3bceea328e0.87696853; remember_user_token=BAhbB1sGaXFJIiIkMmEkMTAkQUR2YXNENGNqT2NSaDBvVWRSS2guTwY6BkVU--bd148a50388e51e524e89809afd6d98e69793711; request_method=GET; _gitlab_session=BAh7CEkiD3Nlc3Npb25faWQGOgZFRkkiJTMzY2U3YzM3YjlhZWQ1ODcwNDljNDA0MjFkOTdjZjA4BjsAVEkiEF9jc3JmX3Rva2VuBjsARkkiMTNqS1V3V2paUCtCWU0rbVZtTUVraFBpTDR3MGFpZ2FtaExXeWphYjJRSVE9BjsARkkiGXdhcmRlbi51c2VyLnVzZXIua2V5BjsAVFsHWwZpcUkiIiQyYSQxMCRBRHZhc0Q0Y2pPY1JoMG9VZFJLaC5PBjsAVA%3D%3D--af5d6859bcba9e86e72ca6c3b2430400db75dcad");
		return conn.getResponseCode();
	}
	
	private static String curl(String url) throws Exception {
		URL reqUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) reqUrl.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(3000);
		System.out.println(url);
		return IOUtils.toString(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	}

	private static class ProjectRepositoryPair {
		String name;
		String repoUrl;

		public ProjectRepositoryPair(String name, String repoUrl) {
			this.name = name;
			this.repoUrl = repoUrl;
		}

	}
}
