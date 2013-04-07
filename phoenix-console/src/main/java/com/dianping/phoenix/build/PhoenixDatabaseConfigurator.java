package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import org.unidal.lookup.configuration.Component;

final class PhoenixDatabaseConfigurator extends AbstractJdbcResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(defineJdbcDataSourceComponent("phoenix", "com.mysql.jdbc.Driver", "jdbc:mysql://root:root@192.168.26.23:3306/phoenix", "root", "", "<![CDATA[useUnicode=true&autoReconnect=true]]>"));

      defineSimpleTableProviderComponents(all, "phoenix", com.dianping.phoenix.console.dal.deploy._INDEX.getEntityClasses());
      defineDaoComponents(all, com.dianping.phoenix.console.dal.deploy._INDEX.getDaoClasses());

      return all;
   }
}
