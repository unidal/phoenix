package com.dianping.phoenix.environment;

import org.junit.Assert;
import org.junit.Test;

public class PhoenixEnvironmentTest {
    private final String key1 = "key1";
    private final String key2 = "key2";

    @Test
    public void testThreadLocal() throws Exception {

        TestThread t = new TestThread(false);
        t.start();
        t.join();
        Assert.assertTrue(t.success);

    }

    @Test
    public void testInheritableThreadLocal() throws Exception {
        TestThread t = new TestThread(true);
        t.start();
        t.join();
        Assert.assertTrue(t.success);

    }

    class TestThread extends Thread {
        boolean         success = false;
        private boolean inheritable;

        private TestThread(boolean inheritable) {
            super();
            this.inheritable = inheritable;
        }

        @Override
        public void run() {
            final String value1 = "threadlocal value1:" + getName();
            final String value2 = "threadlocal value2:" + getName();
            PhoenixEnvironment.set(key1, value1, inheritable);
            PhoenixEnvironment.set(key2, value2, inheritable);

            Thread t = new Thread(new Runnable() {
                public void run() {
                    if (inheritable) {
                        //继承的ThreadLocal，取值和父亲线程相同。
                        success = value1.equals((String) PhoenixEnvironment.get(key1, inheritable)) && value2.equals((String) PhoenixEnvironment.get(key2, inheritable));
                    } else {
                        //非继承的ThreadLocal，取值为null
                        success = PhoenixEnvironment.get(key1, inheritable) == null && PhoenixEnvironment.get(key2, inheritable) == null;
                    }
                    System.out.println("inheritable:" + inheritable + ",success:" + success);

                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            success = success && value1.equals((String) PhoenixEnvironment.get(key1, inheritable)) && value2.equals((String) PhoenixEnvironment.get(key2, inheritable));
            System.out.println("success:" + success);

        }
    }

}
