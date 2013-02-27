package com.dianping.phoenix.dal;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.console.dal.deploy.DeliverableDao;
import com.dianping.phoenix.console.dal.deploy.DeliverableEntity;

public class DeliverableDaoTest extends ComponentTestCase {
	@Test
	public void test() throws Exception {
		DeliverableDao dao = lookup(DeliverableDao.class);
		Deliverable d = dao.findByPK(2, DeliverableEntity.READSET_FULL);

		System.out.println(d);
	}
}
