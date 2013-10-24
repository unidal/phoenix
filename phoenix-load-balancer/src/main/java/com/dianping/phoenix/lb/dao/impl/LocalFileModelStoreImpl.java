/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-18
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.dianping.phoenix.lb.dao.ModelStore;
import com.dianping.phoenix.lb.model.configure.entity.Configure;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;
import com.dianping.phoenix.lb.model.configure.transform.DefaultMerger;
import com.dianping.phoenix.lb.model.configure.transform.DefaultSaxParser;

/**
 * @author Leo Liang
 * 
 */
public class LocalFileModelStoreImpl extends AbstractModelStore implements ModelStore {

    private static final Logger log                     = Logger.getLogger(LocalFileModelStoreImpl.class);

    private String              baseDir;
    private static final String BASE_CONFIG_FILE_SUFFIX = "_base";
    private static final String CONFIG_FILE_PREFIX      = "configure_";
    private static final String XML_SUFFIX              = ".xml";

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public void init() {
        try {
            File baseDirFile = new File(baseDir);
            if (baseDirFile.exists() && baseDirFile.isDirectory()) {

                Collection<File> subFiles = FileUtils.listFiles(baseDirFile, new String[] { "xml" }, false);

                for (File subFile : subFiles) {
                    String fileName = subFile.getName();
                    if (fileName.startsWith(CONFIG_FILE_PREFIX)) {
                        String xml = FileUtils.readFileToString(subFile);
                        Configure tmpConfigure = DefaultSaxParser.parse(xml);

                        if (fileName.endsWith(BASE_CONFIG_FILE_SUFFIX + XML_SUFFIX)) {
                            baseConfigMeta = new ConfigMeta(fileName, tmpConfigure);
                        } else {
                            for (Map.Entry<String, VirtualServer> entry : tmpConfigure.getVirtualServers().entrySet()) {
                                virtualServerConfigFileMapping.put(entry.getKey(), new ConfigMeta(fileName,
                                        tmpConfigure));
                            }
                        }

                        new DefaultMerger().merge(configure, tmpConfigure);
                    }
                }

            } else {
                if (!baseDirFile.exists()) {
                    FileUtils.forceMkdir(baseDirFile);
                } else {
                    throw new IOException(String.format("%s already exists but not a dir.", baseDir));
                }
            }
        } catch (Exception e) {
            log.error("Init local file model store failed.");
            throw new RuntimeException("Init local file model store failed.", e);
        }
    }

    protected void save(String key, Configure configure) throws IOException {
        FileUtils.writeStringToFile(new File(baseDir, key), configure.toString());
    }

    protected boolean delete(String key) {
        return new File(baseDir, key).delete();
    }

    protected String convertToKey(String virtualServerName) {
        return CONFIG_FILE_PREFIX + virtualServerName + XML_SUFFIX;
    }
}
