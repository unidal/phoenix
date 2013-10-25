/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-21
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.dianping.phoenix.lb.dao.ModelStore;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.Configure;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.model.configure.entity.Template;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;

/**
 * @author Leo Liang
 * 
 */
public abstract class AbstractModelStore implements ModelStore {
    private static final Logger                 log                            = Logger.getLogger(AbstractModelStore.class);

    protected Configure                         configure                      = new Configure();
    protected ConfigMeta                        baseConfigMeta;
    protected ConcurrentMap<String, ConfigMeta> virtualServerConfigFileMapping = new ConcurrentHashMap<String, ConfigMeta>();

    protected static class ConfigMeta {
        protected String                 key;
        protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        protected Configure              configure;

        public ConfigMeta(String key, Configure configure) {
            this.key = key;
            this.configure = configure;
        }

    }

    public List<VirtualServer> listVirtualServers() {
        // ignore concurrent issue, since it will introduce unnecessary
        // complexity
        return new ArrayList<VirtualServer>(configure.getVirtualServers().values());
    }

    public List<Strategy> listStrategies() {
        baseConfigMeta.lock.readLock().lock();
        try {
            return new ArrayList<Strategy>(configure.getStrategies().values());
        } finally {
            baseConfigMeta.lock.readLock().unlock();
        }
    }

    public List<Template> listTemplates() {
        baseConfigMeta.lock.readLock().lock();
        try {
            return new ArrayList<Template>(configure.getTemplates().values());
        } finally {
            baseConfigMeta.lock.readLock().unlock();
        }
    }

    public Template findTemplate(String name) {
        baseConfigMeta.lock.readLock().lock();
        try {
            return configure.findTemplate(name);
        } finally {
            baseConfigMeta.lock.readLock().unlock();
        }
    }

    public Strategy findStrategy(String name) {
        baseConfigMeta.lock.readLock().lock();
        try {
            return configure.findStrategy(name);
        } finally {
            baseConfigMeta.lock.readLock().unlock();
        }
    }

    public VirtualServer findVirtualServer(String name) {
        // ignore concurrent issue, since it will introduce unnecessary
        // complexity
        return configure.findVirtualServer(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.impl.ModelStore#updateOrCreateTemplate(java
     * .lang.String, com.dianping.phoenix.lb.model.configure.entity.Template)
     */
    @Override
    public void updateOrCreateTemplate(String name, Template template) throws IOException {
        Template originalTemplate = null;
        baseConfigMeta.lock.writeLock().lock();
        try {
            Date now = new Date();

            originalTemplate = baseConfigMeta.configure.findTemplate(name);
            template.setLastModifiedDate(now);

            if (baseConfigMeta.configure.findTemplate(name) == null) {
                template.setCreationDate(now);
            } else {
                template.setCreationDate(originalTemplate.getCreationDate());
            }
            baseConfigMeta.configure.addTemplate(template);
            configure.addTemplate(template);
            save(baseConfigMeta.key, baseConfigMeta.configure);

        } catch (IOException e) {
            if (originalTemplate == null) {
                baseConfigMeta.configure.removeTemplate(name);
                configure.removeTemplate(name);
            } else {
                baseConfigMeta.configure.addTemplate(originalTemplate);
                configure.addTemplate(originalTemplate);
            }
            log.error(String.format("Save template(%s) failed.", name), e);
            throw e;
        } finally {
            baseConfigMeta.lock.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.impl.ModelStore#removeTemplate(java.lang.
     * String)
     */
    @Override
    public void removeTemplate(String name) throws IOException {
        Template originalTemplate = null;
        baseConfigMeta.lock.writeLock().lock();

        try {
            originalTemplate = baseConfigMeta.configure.findTemplate(name);
            if (originalTemplate == null) {
                return;
            }

            baseConfigMeta.configure.removeTemplate(name);
            configure.removeTemplate(name);
            save(baseConfigMeta.key, baseConfigMeta.configure);

        } catch (IOException e) {
            baseConfigMeta.configure.addTemplate(originalTemplate);
            configure.addTemplate(originalTemplate);
            log.error(String.format("Save template(%s) failed.", name), e);
            throw e;
        } finally {
            baseConfigMeta.lock.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.impl.ModelStore#updateOrCreateStrategy(java
     * .lang.String, com.dianping.phoenix.lb.model.configure.entity.Strategy)
     */
    @Override
    public void updateOrCreateStrategy(String name, Strategy strategy) throws IOException {
        Strategy originalStrategy = null;
        baseConfigMeta.lock.writeLock().lock();
        try {
            Date now = new Date();

            originalStrategy = baseConfigMeta.configure.findStrategy(name);
            strategy.setLastModifiedDate(now);

            if (baseConfigMeta.configure.findStrategy(name) == null) {
                strategy.setCreationDate(now);
            } else {
                strategy.setCreationDate(originalStrategy.getCreationDate());
            }
            baseConfigMeta.configure.addStrategy(strategy);
            configure.addStrategy(strategy);
            save(baseConfigMeta.key, baseConfigMeta.configure);

        } catch (IOException e) {
            if (originalStrategy == null) {
                baseConfigMeta.configure.removeStrategy(name);
                configure.removeStrategy(name);
            } else {
                baseConfigMeta.configure.addStrategy(originalStrategy);
                configure.addStrategy(originalStrategy);
            }
            log.error(String.format("Save template(%s) failed.", name), e);
            throw e;
        } finally {
            baseConfigMeta.lock.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.impl.ModelStore#removeStrategy(java.lang.
     * String)
     */
    @Override
    public void removeStrategy(String name) throws IOException {
        Strategy originalStrategy = null;
        baseConfigMeta.lock.writeLock().lock();
        try {
            originalStrategy = baseConfigMeta.configure.findStrategy(name);

            if (originalStrategy == null) {
                return;
            }

            baseConfigMeta.configure.removeStrategy(name);
            configure.removeStrategy(name);
            save(baseConfigMeta.key, baseConfigMeta.configure);

        } catch (IOException e) {
            baseConfigMeta.configure.addStrategy(originalStrategy);
            configure.addStrategy(originalStrategy);
            log.error(String.format("Save template(%s) failed.", name), e);
            throw e;
        } finally {
            baseConfigMeta.lock.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.impl.ModelStore#updateVirtualServer(java.
     * lang.String,
     * com.dianping.phoenix.lb.model.configure.entity.VirtualServer)
     */
    @Override
    public void updateVirtualServer(String name, VirtualServer virtualServer) throws IOException, BizException {
        VirtualServer originalVirtualServer = null;
        ConfigMeta configFileEntry = virtualServerConfigFileMapping.get(name);
        if (configFileEntry != null) {
            configFileEntry.lock.writeLock().lock();

            try {
                if (configFileEntry.configure.findVirtualServer(name) == null
                        || configure.findVirtualServer(name) == null) {
                    return;
                }

                originalVirtualServer = configFileEntry.configure.findVirtualServer(name);

                if (originalVirtualServer.getVersion() != virtualServer.getVersion()) {
                    throw new ConcurrentModificationException(String.format(
                            "VirtualServer(%s) concurrent modification exception.", name));
                } else {
                    virtualServer.setVersion(originalVirtualServer.getVersion() + 1);
                    virtualServer.setLastModifiedDate(new Date());
                    virtualServer.setCreationDate(originalVirtualServer.getCreationDate());
                }

                configFileEntry.configure.addVirtualServer(virtualServer);
                configure.addVirtualServer(virtualServer);
                save(configFileEntry.key, configFileEntry.configure);
            } catch (IOException e) {
                configFileEntry.configure.addVirtualServer(originalVirtualServer);
                configure.addVirtualServer(originalVirtualServer);
                log.error(String.format("Save virtualServer(%s) failed.", name), e);
                throw e;
            } finally {
                configFileEntry.lock.writeLock().unlock();
            }
        } else {
            throw new BizException(String.format("VirtualServer(%s) not exists.", name));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.impl.ModelStore#removeVirtualServer(java.
     * lang.String)
     */
    @Override
    public void removeVirtualServer(String name) throws BizException, IOException {
        VirtualServer originalVirtualServer = null;
        ConfigMeta configFileEntry = virtualServerConfigFileMapping.get(name);
        if (configFileEntry != null) {
            configFileEntry.lock.writeLock().lock();
            try {
                if (configFileEntry.configure.findVirtualServer(name) == null
                        || configure.findVirtualServer(name) == null) {
                    return;
                }

                originalVirtualServer = configFileEntry.configure.findVirtualServer(name);

                if (originalVirtualServer == null) {
                    return;
                }

                configFileEntry.configure.removeVirtualServer(name);
                configure.removeVirtualServer(name);

                if (configFileEntry.configure.getVirtualServers().size() == 0) {
                    if (!delete(configFileEntry.key)) {
                        throw new IOException(String.format("Delete Virtual Server(%s) failed(file: %s).", name,
                                configFileEntry.key));
                    } else {
                        virtualServerConfigFileMapping.remove(name);
                    }
                } else {
                    save(configFileEntry.key, configFileEntry.configure);
                }
            } catch (IOException e) {
                configFileEntry.configure.addVirtualServer(originalVirtualServer);
                configure.addVirtualServer(originalVirtualServer);
                log.error(String.format("Save virtualServer(%s) failed.", name), e);
                throw e;
            } finally {
                configFileEntry.lock.writeLock().unlock();
            }
        } else {
            throw new BizException(String.format("VirtualServer(%s) not exists.", name));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.impl.ModelStore#addVirtualServer(java.lang
     * .String, com.dianping.phoenix.lb.model.configure.entity.VirtualServer)
     */
    @Override
    public void addVirtualServer(String name, VirtualServer virtualServer) throws IOException, BizException {
        if (virtualServerConfigFileMapping.containsKey(name)) {
            throw new BizException(String.format("Virtual Server %s already exist.", name));
        }

        Date now = new Date();

        virtualServer.setVersion(1);
        virtualServer.setCreationDate(now);
        virtualServer.setLastModifiedDate(now);

        Configure newConfigure = new Configure();
        newConfigure.addVirtualServer(virtualServer);

        ConfigMeta originalMeta = virtualServerConfigFileMapping.putIfAbsent(name, new ConfigMeta(convertToKey(name),
                newConfigure));

        if (originalMeta != null) {
            return;
        } else {
            ConfigMeta configMeta = virtualServerConfigFileMapping.get(name);
            configMeta.lock.writeLock().lock();
            try {
                if (configure.findVirtualServer(name) != null) {
                    throw new BizException(String.format("Virtual Server %s already exist.", name));
                } else {
                    configure.addVirtualServer(virtualServer);
                    save(configMeta.key, newConfigure);
                }

            } catch (IOException e) {
                configure.removeVirtualServer(name);
                virtualServerConfigFileMapping.remove(name);
                log.error(String.format("Add virtualServer(%s) failed.", name), e);
                throw e;
            } finally {
                configMeta.lock.writeLock().unlock();
            }
        }
    }

    protected abstract void save(String key, Configure configure) throws IOException;

    protected abstract boolean delete(String key);

    protected abstract String convertToKey(String virtualServerName);
}