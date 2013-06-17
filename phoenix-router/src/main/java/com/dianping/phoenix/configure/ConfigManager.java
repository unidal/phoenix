package com.dianping.phoenix.configure;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;

import com.dianping.phoenix.router.model.entity.RouterRules;
import com.dianping.phoenix.router.model.transform.DefaultSaxParser;

public class ConfigManager implements Initializable {

    private final static Logger      log                         = Logger.getLogger(ConfigManager.class);
    private final static String      virtualServerConfigFileName = "virtualServer.properties";
    private Map<String, RouterRules> virtualServerRulesMapping   = new HashMap<String, RouterRules>();

    @Override
    public void initialize() throws InitializationException {
        initVirtualServersRulesMapping();
    }

    private void initVirtualServersRulesMapping() throws InitializationException {
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream(virtualServerConfigFileName);
            if (in != null) {
                Properties prop = new Properties();
                prop.load(in);
                for (String virtualServer : prop.stringPropertyNames()) {
                    String routerFile = prop.getProperty(virtualServer);
                    log.info(String.format("Found virtual server(%s), router file is %s", virtualServer, routerFile));
                    virtualServerRulesMapping.put(virtualServer, loadRouterRules(routerFile));
                }
            } else {
                String msg = String.format("%s not found on classpath", virtualServerConfigFileName);
                log.error(msg);
                throw new InitializationException(msg);
            }
        } catch (Exception e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
        }

    }

    private RouterRules loadRouterRules(String fileName) throws InitializationException {
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream(fileName);
            if (in != null) {
                String content = Files.forIO().readFrom(in, "utf-8");
                return DefaultSaxParser.parse(content);
            } else {
                String msg = String.format("%s not found on classpath", fileName);
                log.error(msg);
                throw new InitializationException(msg);
            }
        } catch (Exception e) {
            throw new InitializationException(
                    String.format("Unable to load router file(%s) from classpath!", fileName), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public Map<String, RouterRules> getVirtualServerRulesMapping() {
        return virtualServerRulesMapping;
    }

}
