/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-21
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.Availability;
import com.dianping.phoenix.lb.model.State;
import com.dianping.phoenix.lb.model.configure.entity.Configure;
import com.dianping.phoenix.lb.model.configure.entity.Directive;
import com.dianping.phoenix.lb.model.configure.entity.Instance;
import com.dianping.phoenix.lb.model.configure.entity.Location;
import com.dianping.phoenix.lb.model.configure.entity.Member;
import com.dianping.phoenix.lb.model.configure.entity.Pool;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.model.configure.entity.Template;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;
import com.dianping.phoenix.lb.model.configure.transform.DefaultMerger;
import com.dianping.phoenix.lb.model.configure.transform.DefaultSaxParser;

/**
 * @author Leo Liang
 * 
 */
public class LocalFileModelStoreTest {
    private LocalFileModelStoreImpl store;
    private File                    baseDir;
    private File                    tmpDir;

    @Before
    public void before() throws Exception {
        baseDir = new File(".", "src/test/resources");
        tmpDir = new File(System.getProperty("java.io.tmpdir"), "model-test");
        if (tmpDir.exists()) {
            FileUtils.forceDelete(tmpDir);
        }
        FileUtils.copyDirectory(baseDir, tmpDir);
        store = new LocalFileModelStoreImpl();
        store.setBaseDir(tmpDir.getAbsolutePath());
        store.init();
    }

    @After
    public void after() throws Exception {
        if (tmpDir.exists()) {
            tmpDir.setWritable(true);
            FileUtils.forceDelete(tmpDir);
        }
    }

    @Test
    public void testListTemplates() throws Exception {
        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Template>(configure.getTemplates().values()), store.listTemplates());

    }

    @Test
    public void testListVirtualServers() throws Exception {
        Configure wwwConfigure = DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir,
                "configure_www.xml")));

        Configure tuangouConfigure = DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir,
                "configure_tuangou.xml")));

        List<VirtualServer> expected = new ArrayList<VirtualServer>(tuangouConfigure.getVirtualServers().values()
                .size()
                + wwwConfigure.getVirtualServers().values().size());
        expected.addAll(tuangouConfigure.getVirtualServers().values());
        expected.addAll(wwwConfigure.getVirtualServers().values());

        List<VirtualServer> actual = store.listVirtualServers();

        assertEquals(expected, actual);
    }

    @Test
    public void testListStrategies() throws Exception {
        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());
    }

    @Test
    public void testFindTemplate() throws Exception {
        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        Template expected = configure.findTemplate("default-template");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, store.findTemplate("default-template"), true));
    }

    @Test
    public void testFindStrategy() throws Exception {
        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        Strategy expected = configure.findStrategy("uri-hash");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, store.findStrategy("uri-hash"), true));
    }

    @Test
    public void testFindVirtualServer() throws Exception {
        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        VirtualServer expected = configure.findVirtualServer("www");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, store.findVirtualServer("www"), true));
    }

    @Test
    public void testAddTemplate() throws Exception {
        Template newTemplate = new Template("test-template");
        newTemplate.setContent("test");
        store.updateOrCreateTemplate("test-template", newTemplate);

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        configure.addTemplate(newTemplate);

        assertEquals(new ArrayList<Template>(configure.getTemplates().values()), store.listTemplates());
        Assert.assertNotNull(newTemplate.getCreationDate());
        Assert.assertEquals(newTemplate.getLastModifiedDate(), newTemplate.getCreationDate());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testUpdateTemplate() throws Exception {
        Template modifiedTemplate = new Template("default-template");
        modifiedTemplate.setContent("test-mod");
        Date now = new Date();
        store.updateOrCreateTemplate("default-template", modifiedTemplate);

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        Template expectedTemplate = configure.findTemplate("default-template");
        expectedTemplate.setContent("test-mod");
        expectedTemplate.setLastModifiedDate(now);

        assertEquals(new ArrayList<Template>(configure.getTemplates().values()), store.listTemplates());
        Assert.assertEquals(now, modifiedTemplate.getLastModifiedDate());
        Assert.assertEquals(expectedTemplate.getCreationDate(), modifiedTemplate.getCreationDate());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testUpdateTemplateRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        Template modifiedTemplate = new Template("default-template");
        modifiedTemplate.setContent("test-mod");
        try {
            store.updateOrCreateTemplate("default-template", modifiedTemplate);
            Assert.fail();
        } catch (IOException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Template>(configure.getTemplates().values()), store.listTemplates());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testAddTemplateRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        Template newTemplate = new Template("test-template");
        newTemplate.setContent("test");
        try {
            store.updateOrCreateTemplate("test-template", newTemplate);
            Assert.fail();
        } catch (IOException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Template>(configure.getTemplates().values()), store.listTemplates());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testRemoveTemplate() throws Exception {
        store.removeTemplate("default-template");

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        configure.removeTemplate("default-template");

        assertEquals(new ArrayList<Template>(configure.getTemplates().values()), store.listTemplates());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testRemoveTemplateRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        try {
            store.removeTemplate("default-template");
            Assert.fail();
        } catch (IOException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Template>(configure.getTemplates().values()), store.listTemplates());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testAddStrategy() throws Exception {
        Strategy newStrategy = new Strategy("dper-hash");
        newStrategy.setType("hash");
        newStrategy.setDynamicAttribute("target", "dper");
        newStrategy.setDynamicAttribute("method", "crc32");
        store.updateOrCreateStrategy("dper-hash", newStrategy);

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        configure.addStrategy(newStrategy);

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());
        Assert.assertNotNull(newStrategy.getCreationDate());
        Assert.assertEquals(newStrategy.getLastModifiedDate(), newStrategy.getCreationDate());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testUpdateStrategy() throws Exception {
        Strategy modifiedStrategy = new Strategy("uri-hash");
        modifiedStrategy.setDynamicAttribute("target", "uri");
        modifiedStrategy.setDynamicAttribute("method", "md5");
        modifiedStrategy.setType("hash");
        Date now = new Date();
        store.updateOrCreateStrategy("uri-hash", modifiedStrategy);

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        Strategy expectedStrategy = configure.findStrategy("uri-hash");
        expectedStrategy.setDynamicAttribute("method", "md5");
        expectedStrategy.setLastModifiedDate(now);

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());
        Assert.assertEquals(now, modifiedStrategy.getLastModifiedDate());
        Assert.assertEquals(expectedStrategy.getCreationDate(), modifiedStrategy.getCreationDate());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testAddStrategyRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        Strategy newStrategy = new Strategy("dper-hash");
        newStrategy.setType("hash");
        newStrategy.setDynamicAttribute("target", "dper");
        newStrategy.setDynamicAttribute("method", "crc32");

        try {
            store.updateOrCreateStrategy("dper-hash", newStrategy);
            Assert.fail();
        } catch (IOException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testUpdateStrategyRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        Strategy modifiedStrategy = new Strategy("uri-hash");
        modifiedStrategy.setDynamicAttribute("target", "uri");
        modifiedStrategy.setDynamicAttribute("method", "md5");
        modifiedStrategy.setType("hash");
        try {
            store.updateOrCreateStrategy("uri-hash", modifiedStrategy);
            Assert.fail();
        } catch (IOException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testRemoveStrategy() throws Exception {
        store.removeStrategy("uri-hash");

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        configure.removeStrategy("uri-hash");

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testRemoveStrategyRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        try {
            store.removeStrategy("uri-hash");
            Assert.fail();
        } catch (IOException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());

        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_base.xml"))).toString());
    }

    @Test
    public void testUpdateVirtualServer() throws Exception {

        VirtualServer newVirtualServer = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);
        Pool newPool = new Pool("static-pool");
        newPool.setLoadbalanceStrategyName("ip-hash");
        newPool.setMinAvailableMemberPercentage(20);
        Member newMember = new Member("static01");
        newMember.setIp("100.100.100.1");
        newPool.addMember(newMember);
        newVirtualServer.addPool(newPool);

        VirtualServer originalVirtualServer = store.findVirtualServer("www");
        int originalVersion = originalVirtualServer.getVersion();
        Date originalCreationDate = originalVirtualServer.getCreationDate();

        Date now = new Date();
        store.updateVirtualServer("www", newVirtualServer);
        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));

        configure.addVirtualServer(newVirtualServer);

        Assert.assertEquals(originalVersion + 1, store.findVirtualServer("www").getVersion());
        Assert.assertEquals(originalCreationDate, store.findVirtualServer("www").getCreationDate());
        Assert.assertEquals(now, store.findVirtualServer("www").getLastModifiedDate());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(newVirtualServer, store.findVirtualServer("www"), "m_version",
                "m_creationDate", "m_lastModifiedDate"));
        // assert the whole model
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        Configure wwwConfigure = DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir,
                "configure_www.xml")));
        wwwConfigure.addVirtualServer(newVirtualServer);
        // assert www configure has updated
        Assert.assertEquals(wwwConfigure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_www.xml"))).toString());
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));
    }

    @Test
    public void testUpdateVirtualServerConcurrentModification() throws Exception {

        VirtualServer newVirtualServer = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);
        Pool newPool = new Pool("static-pool");
        newPool.setLoadbalanceStrategyName("ip-hash");
        newPool.setMinAvailableMemberPercentage(20);
        Member newMember = new Member("static01");
        newMember.setIp("100.100.100.1");
        newPool.addMember(newMember);
        newVirtualServer.addPool(newPool);

        VirtualServer originalVirtualServer = store.findVirtualServer("www");
        int originalVersion = originalVirtualServer.getVersion();
        Date originalCreationDate = originalVirtualServer.getCreationDate();

        Date now = new Date();
        store.updateVirtualServer("www", newVirtualServer);

        // modify concurrent
        VirtualServer newVirtualServer1 = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance1 = new Instance();
        newInstance1.setIp("10.1.2.5");
        newVirtualServer1.addInstance(newInstance1);
        newVirtualServer1.setAvailability(Availability.OFFLINE);
        newVirtualServer1.setState(State.DISABLED);
        newVirtualServer1.setDefaultPoolName("test-pool1");
        Location newLocation1 = new Location();
        newLocation1.setCaseSensitive(true);
        newLocation1.setMatchType("prefix");
        newLocation1.setPattern("/");
        Directive newDirective1 = new Directive();
        newDirective1.setType("static-resource1");
        newDirective1.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs1");
        newDirective1.setDynamicAttribute("expires", "300d");
        newLocation1.addDirective(newDirective1);
        newVirtualServer1.addLocation(newLocation1);
        Pool newPool1 = new Pool("static-pool1");
        newPool1.setLoadbalanceStrategyName("ip-hash1");
        newPool1.setMinAvailableMemberPercentage(20);
        Member newMember1 = new Member("static01");
        newMember1.setIp("100.100.100.11");
        newPool1.addMember(newMember1);
        newVirtualServer1.addPool(newPool1);
        try {
            store.updateVirtualServer("www", newVirtualServer1);
            Assert.fail();
        } catch (ConcurrentModificationException e) {

        } catch (Exception e1) {
            Assert.fail();
        }
        // modify concurrent end

        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));

        configure.addVirtualServer(newVirtualServer);

        Assert.assertEquals(originalVersion + 1, store.findVirtualServer("www").getVersion());
        Assert.assertEquals(originalCreationDate, store.findVirtualServer("www").getCreationDate());
        Assert.assertEquals(now, store.findVirtualServer("www").getLastModifiedDate());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(newVirtualServer, store.findVirtualServer("www"), "m_version",
                "m_creationDate", "m_lastModifiedDate"));
        // assert the whole model
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        Configure wwwConfigure = DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir,
                "configure_www.xml")));
        wwwConfigure.addVirtualServer(newVirtualServer);
        // assert www configure has updated
        Assert.assertEquals(wwwConfigure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_www.xml"))).toString());
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));

    }

    @Test
    public void testUpdateVirtualServerNotExists() throws Exception {

        VirtualServer newVirtualServer = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);
        Pool newPool = new Pool("static-pool");
        newPool.setLoadbalanceStrategyName("ip-hash");
        newPool.setMinAvailableMemberPercentage(20);
        Member newMember = new Member("static01");
        newMember.setIp("100.100.100.1");
        newPool.addMember(newMember);
        newVirtualServer.addPool(newPool);

        try {
            store.updateVirtualServer("test", newVirtualServer);
            Assert.fail();
        } catch (BizException e) {

        } catch (Exception e1) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));

        // assert the whole model
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        // assert www configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(tmpDir, "configure_www.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_www.xml")));
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));

    }

    @Test
    public void testUpdateVirtualServerRollback() throws Exception {
        new File(tmpDir, "configure_www.xml").setWritable(false);

        VirtualServer newVirtualServer = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);
        Pool newPool = new Pool("static-pool");
        newPool.setLoadbalanceStrategyName("ip-hash");
        newPool.setMinAvailableMemberPercentage(20);
        Member newMember = new Member("static01");
        newMember.setIp("100.100.100.1");
        newPool.addMember(newMember);
        newVirtualServer.addPool(newPool);

        try {
            store.updateVirtualServer("www", newVirtualServer);
            Assert.fail();
        } catch (IOException e) {

        } catch (Exception e) {
            Assert.fail();

        }
        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));

        // assert the whole model
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        // assert www configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_www.xml")));
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));
    }

    @Test
    public void testAddVirtualServer() throws Exception {
        VirtualServer newVirtualServer = new VirtualServer("testVs");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);
        Pool newPool = new Pool("static-pool");
        newPool.setLoadbalanceStrategyName("ip-hash");
        newPool.setMinAvailableMemberPercentage(20);
        Member newMember = new Member("static01");
        newMember.setIp("100.100.100.1");
        newPool.addMember(newMember);
        newVirtualServer.addPool(newPool);

        Date now = new Date();
        store.addVirtualServer("testVs", newVirtualServer);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(newVirtualServer, store.findVirtualServer("testVs"), true));
        Assert.assertEquals(1, newVirtualServer.getVersion());
        Assert.assertEquals(now, newVirtualServer.getCreationDate());
        Assert.assertEquals(now, newVirtualServer.getLastModifiedDate());

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));
        configure.addVirtualServer(newVirtualServer);
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        // assert new file created
        Assert.assertTrue(new File(tmpDir, "configure_testVs.xml").exists());
        Assert.assertEquals(1,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_testVs.xml")))
                        .getVirtualServers().size());
        VirtualServer virtualServerFromFile = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(tmpDir, "configure_testVs.xml"))).getVirtualServers()
                .get("testVs");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(newVirtualServer, virtualServerFromFile, "m_creationDate",
                "m_lastModifiedDate"));
        Assert.assertEquals(sdf.format(newVirtualServer.getCreationDate()),
                sdf.format(virtualServerFromFile.getCreationDate()));
        Assert.assertEquals(sdf.format(newVirtualServer.getLastModifiedDate()),
                sdf.format(virtualServerFromFile.getLastModifiedDate()));

        // assert www configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_www.xml")));
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));

    }

    @Test
    public void testAddVirtualServerExists() throws Exception {
        VirtualServer newVirtualServer = new VirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);
        Pool newPool = new Pool("static-pool");
        newPool.setLoadbalanceStrategyName("ip-hash");
        newPool.setMinAvailableMemberPercentage(20);
        Member newMember = new Member("static01");
        newMember.setIp("100.100.100.1");
        newPool.addMember(newMember);
        newVirtualServer.addPool(newPool);

        try {
            store.addVirtualServer("www", newVirtualServer);
            Assert.fail();
        } catch (BizException e) {

        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        // assert new file not created
        Assert.assertFalse(new File(tmpDir, "configure_testVs.xml").exists());
        // assert www configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_www.xml")));
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));

    }

    @Test
    public void testAddVirtualServerRollback() throws Exception {
        tmpDir.setWritable(false);

        VirtualServer newVirtualServer = new VirtualServer("test");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);
        Pool newPool = new Pool("static-pool");
        newPool.setLoadbalanceStrategyName("ip-hash");
        newPool.setMinAvailableMemberPercentage(20);
        Member newMember = new Member("static01");
        newMember.setIp("100.100.100.1");
        newPool.addMember(newMember);
        newVirtualServer.addPool(newPool);

        try {
            store.addVirtualServer("test", newVirtualServer);
            Assert.fail();
        } catch (IOException e) {

        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        // assert new file not created
        Assert.assertFalse(new File(tmpDir, "configure_testVs.xml").exists());
        // assert www configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_www.xml")));
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));

    }

    @Test
    public void testRemoveVirtualServer() throws Exception {
        store.removeVirtualServer("www");

        Assert.assertNull(store.findVirtualServer("www"));
        // assert www configure deleted
        Assert.assertFalse(new File(tmpDir, "configure_www.xml").exists());
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));
    }

    @Test
    public void testRemoveVirtualServerRollback() throws Exception {
        tmpDir.setWritable(false);
        new File(tmpDir, "configure_www.xml").setWritable(false);
        try {
            store.removeVirtualServer("www");
            Assert.fail();
        } catch (IOException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertNotNull(store.findVirtualServer("www"));
        // assert www configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_www.xml")));
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));
    }
    
    @Test
    public void testRemoveVirtualServerNotExist() throws Exception {
        try {
            store.removeVirtualServer("sss");
            Assert.fail();
        } catch (BizException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        // assert www configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_www.xml")));
        // assert tuangou & base configure not changed
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_tuangou.xml")));
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, "configure_base.xml")),
                FileUtils.readFileToString(new File(tmpDir, "configure_base.xml")));
    }

    private <T> void assertEquals(List<T> expectedList, List<T> actualList) {
        Assert.assertEquals(expectedList.size(), actualList.size());
        for (T expected : expectedList) {
            Assert.assertTrue(actualList.contains(expected));
            Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, actualList.get(actualList.indexOf(expected)),
                    true));
        }
    }

}
