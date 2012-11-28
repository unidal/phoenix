package com.dianping.phoenix.agent.core;

import java.lang.reflect.ParameterizedType;

import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTaskProcessor;
import com.dianping.phoenix.agent.core.tx.TransactionId;


public class TestUtil {

	public static TransactionId generateTxId(long id) {
		return new TransactionId(id);
	}
	
	public static interface A<T> {
		T get();
	}
	
	public static abstract class Aa<T> implements A<T> {
		
	}
	
	public static class Aaa extends Aa<TestUtil> {

		@Override
		public TestUtil get() {
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		DeployTaskProcessor a = new DeployTaskProcessor();
		Class clazz = (Class) ((ParameterizedType)a.getClass().getGenericSuperclass()).getRawType();
		System.out.println(clazz);
		System.out.println(clazz.getGenericInterfaces()[0]);
//		System.out.println(((ParameterizedType)(a.getClass().getGenericInterfaces()[0])).getOwnerType() == TestUtil.class);
	}
	
}
