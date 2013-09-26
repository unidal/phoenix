package com.dianping.phoenix.environment;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

public class PhoenixEnvironmentTest {
    private static final int THREAD_POOL_SIZE = 10;

    private ExecutorService  executorService  = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @SuppressWarnings("unchecked")
    @Test
    public void testThreadLocal() throws Exception {

        int i = 0, N = 20;
        Future<Boolean>[] fs = new Future[N];

        while (i < N) {
            fs[i++] = executorService.submit(new TestTask());
        }

        i = 0;
        while (i < N) {
            boolean success = (Boolean) fs[i++].get();
            Assert.assertTrue(success);
        }

    }

    class TestTask implements Callable<Boolean> {

        boolean success = false;

        private TestTask() {
            super();
        }

        @Override
        public Boolean call() {
            final String value1 = "request id at thread: " + Thread.currentThread().getName();
            final String value2 = "refer request id at thread: " + Thread.currentThread().getName();
            PhoenixContext.getInstance().setRequestId(value1);
            PhoenixContext.getInstance().setReferRequestId(value2);

            System.out.println(PhoenixContext.getInstance().getRequestId());
            System.out.println(PhoenixContext.getInstance().getReferRequestId());
            
            Thread t = new Thread(new Runnable() {
                public void run() {
                    //非继承的ThreadLocal，取值为null
                    success = PhoenixContext.getInstance().getRequestId() == null && PhoenixContext.getInstance().getReferRequestId() == null;
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            success = success && value1.equals(PhoenixContext.getInstance().getRequestId()) && value2.equals(PhoenixContext.getInstance().getReferRequestId());

            return success;
        }
    }

}
