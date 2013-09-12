package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import com.dianping.phoenix.bootstrap.meta.entity.Artifact;
import com.dianping.phoenix.bootstrap.meta.entity.Meta;
import com.dianping.phoenix.bootstrap.meta.transform.DefaultSaxParser;

public class MetaBasedClasspathBuilder extends AbstractClasspathBuilder {
	private VersionComparator m_comparator = new VersionComparator();
	private Map<String, Artifact> m_artifacts = new HashMap<String, Artifact>();

	public MetaBasedClasspathBuilder(File metafile, String app) {
		if (metafile.exists()) {
			try {
				Meta meta = DefaultSaxParser.parse(new FileInputStream(metafile));
				if (meta != null && app != null && meta.getDomainMetas().get(app) != null) {
					m_artifacts.putAll(meta.getDomainMetas().get(app).getArtifacts());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected Entry pickup(Entry kernelEntry, Entry appEntry) {
		if (m_artifacts.size() > 0 && m_artifacts.containsKey(kernelEntry.getArtifactId())) {
			String supposeVersion = m_artifacts.get(kernelEntry.getArtifactId()).getVersion();
			if (m_comparator.compare(kernelEntry.getVersion(), supposeVersion) == 0) {
				return kernelEntry;
			} else if (m_comparator.compare(appEntry.getVersion(), supposeVersion) == 0) {
				return appEntry;
			}
		}
		return defaultPickup(kernelEntry, appEntry);
	}

	protected Entry defaultPickup(Entry kernelEntry, Entry appEntry) {
		if (m_comparator.compare(kernelEntry.getVersion(), appEntry.getVersion()) > 0) {
			return kernelEntry;
		} else {
			return appEntry;
		}
	}

}
