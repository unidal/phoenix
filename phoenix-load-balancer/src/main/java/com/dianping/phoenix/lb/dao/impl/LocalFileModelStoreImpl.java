/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-18
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

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

    private static final Logger                  log                     = Logger.getLogger(LocalFileModelStoreImpl.class);

    private String                               baseDir;
    private static final String                  BASE_CONFIG_FILE_SUFFIX = "_base";
    private static final String                  TAG_DIR                 = "tag";
    private static final String                  CONFIG_FILE_PREFIX      = "configure_";
    private static final String                  XML_SUFFIX              = ".xml";
    private static final String                  TAGID_SEPARATOR         = "_";
    private ConcurrentMap<String, AtomicInteger> tagMetas                = new ConcurrentHashMap<String, AtomicInteger>();

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    protected void initCustomizedMetas() {
        initTagMetas();
    }

    @Override
    protected void initConfigMetas() {
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
            log.error("Init local file model's configMetas store failed.");
            throw new RuntimeException("Init local file model's configMetas store failed.", e);
        }
    }

    protected void save(String key, Configure configure) throws IOException {
        doSave(new File(baseDir, key), configure);
    }

    private void doSave(File file, Configure configure) throws IOException {
        FileUtils.writeStringToFile(file, configure.toString());
    }

    protected boolean delete(String key) {
        return new File(baseDir, key).delete();
    }

    protected String convertToKey(String virtualServerName) {
        return CONFIG_FILE_PREFIX + virtualServerName + XML_SUFFIX;
    }

    protected void initTagMetas() {
        try {
            File baseDirFile = new File(baseDir, TAG_DIR);
            if (baseDirFile.exists() && baseDirFile.isDirectory()) {

                File[] virtualServers = baseDirFile.listFiles();

                for (File subFile : virtualServers) {
                    String vsName = subFile.getName();

                    File[] tags = new File(baseDirFile, vsName).listFiles();

                    for (File tag : tags) {
                        String fileName = tag.getName();
                        if (fileName.startsWith(CONFIG_FILE_PREFIX)) {
                            int tagIdStart = fileName.lastIndexOf(TAGID_SEPARATOR);
                            int extStart = fileName.lastIndexOf(XML_SUFFIX);
                            if (tagIdStart != -1 && extStart != -1 && extStart < tagIdStart) {
                                String tagIdStr = fileName.substring(tagIdStart + 1);
                                if (StringUtils.isNumeric(tagIdStr)) {
                                    String xml = FileUtils.readFileToString(tag);
                                    Configure tmpConfigure = DefaultSaxParser.parse(xml);
                                    int tagId = Integer.valueOf(tagIdStr);
                                    for (Map.Entry<String, VirtualServer> entry : tmpConfigure.getVirtualServers()
                                            .entrySet()) {
                                        tagMetas.putIfAbsent(entry.getKey(), new AtomicInteger(0));
                                        if (tagMetas.get(entry.getKey()).intValue() < tagId) {
                                            tagMetas.get(entry.getKey()).set(tagId);
                                        }
                                    }
                                }
                            }
                        }
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
            log.error("Init local file model's configMetas store failed.");
            throw new RuntimeException("Init local file model's configMetas store failed.", e);
        }
    }

    @Override
    protected String saveTag(String key, String vsName, Configure configure) throws IOException {
        tagMetas.putIfAbsent(vsName, new AtomicInteger(0));
        int tagId = tagMetas.get(vsName).incrementAndGet();
        File tagFile = getTagFile(key, String.valueOf(tagId), vsName);
        FileUtils.forceMkdir(tagFile.getParentFile());
        doSave(tagFile, configure);
        return String.valueOf(tagId);
    }

    @Override
    protected Configure loadTag(String key, String vsName, String tagId) throws IOException, SAXException {
        return DefaultSaxParser.parse(FileUtils.readFileToString(getTagFile(key, tagId, vsName)));
    }

    private File getTagFile(String key, String tagId, String vsName) {
        return new File(getTagFileBase(vsName), key + TAGID_SEPARATOR + tagId);
    }

    private File getTagFileBase(String vsName) {
        return new File(new File(baseDir, TAG_DIR), vsName);
    }

    @Override
    protected List<String> doListTagIds(String vsName) throws IOException {
        File tagFileBase = getTagFileBase(vsName);
        if (tagFileBase.exists() && tagFileBase.isDirectory()) {
            String[] fileNames = tagFileBase.list();
            List<String> tagIds = new ArrayList<String>();
            for (String fileName : fileNames) {
                int tagIdStart = fileName.lastIndexOf(TAGID_SEPARATOR);
                if (tagIdStart != -1) {
                    String tagIdStr = fileName.substring(tagIdStart + 1);
                    if (StringUtils.isNumeric(tagIdStr)) {
                        tagIds.add(tagIdStr);
                    }
                }
            }
            return tagIds;
        }
        return null;
    }

}
