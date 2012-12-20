package com.dianping.phoenix.agent.core.task.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;

import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.tx.Transaction;

@SuppressWarnings("rawtypes")
public class TaskProcessorFactory extends ContainerHolder implements Initializable {
	
	private Map<Class<? extends Task>, TaskProcessor<Task>> processorMap;
	
	public TaskProcessorFactory() {
		processorMap = new ConcurrentHashMap<Class<? extends Task>, TaskProcessor<Task>>();
	}
	
	public void registerTaskProcessor(Class<? extends Task> clazz, TaskProcessor<Task> processor) {
		if(processorMap.containsKey(clazz)) {
			throw new RuntimeException(String.format("duplicate TaskProcessor for %s", clazz.getName()));
		}
		processorMap.put(clazz, processor);
	}

	public TaskProcessor<Task> findTaskProcessor(Task task) {
		Class<? extends Task> clazz = task == null ? null : task.getClass();
		TaskProcessor<Task> processor = null;
		for (Map.Entry<Class<? extends Task>, TaskProcessor<Task>>  entry : processorMap.entrySet()) {
			if(entry.getKey().isAssignableFrom(clazz)) {
				processor = entry.getValue();
				break;
			}
		}
		return processor;
	}

	public List<Transaction> currentTransactions() {
		List<Transaction> currentTransactions = new ArrayList<Transaction>();
		for (TaskProcessor<Task> taskProcessor : processorMap.values()) {
			List<Transaction> childTasks = taskProcessor.currentTransactions();
			if(childTasks != null && childTasks.size() > 0) {
				currentTransactions.addAll(childTasks);
			}
		}
		return currentTransactions;
	}

	@Override
	public void initialize() throws InitializationException {
		List<TaskProcessor> taskProcessorList = lookupList(TaskProcessor.class);
		for (TaskProcessor<Task> taskProcessor : taskProcessorList) {
			processorMap.put(taskProcessor.handle(), taskProcessor);
		}
	}

}
