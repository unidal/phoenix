package com.dianping.phoenix.agent.page.deploy.shell;

import java.io.IOException;
import java.io.OutputStream;

public interface Shell {

	void prepare(String libVersion, OutputStream outputCollector) throws IOException;
	
	void activate(OutputStream outputCollector) throws IOException;
	
	void commit(OutputStream outputCollector) throws IOException;
	
	void rollback(String appVersion, OutputStream outputCollector) throws IOException;
	
	
	
}
