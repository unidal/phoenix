package com.dianping.phoenix.agent.core.task;

import java.util.HashSet;
import java.util.Set;

public interface Task {

	public enum Status {
		INIT, REJECTED, PROCESSING, FAILED, SUCCESS;

		private final static Set<Status> COMPLETED_STATUS_SET = new HashSet<Task.Status>();
		static {
			COMPLETED_STATUS_SET.add(REJECTED);
			COMPLETED_STATUS_SET.add(FAILED);
			COMPLETED_STATUS_SET.add(SUCCESS);
		};

		/**
		 * 是否是终结状态
		 * @return
		 */
		public boolean isCompleted() {
			return COMPLETED_STATUS_SET.contains(this);
		}

	}

	Status getStatus();

}
