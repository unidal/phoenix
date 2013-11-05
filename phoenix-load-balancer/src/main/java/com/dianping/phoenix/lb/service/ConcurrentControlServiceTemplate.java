/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.utils.ExceptionUtils;

/**
 * @author Leo Liang
 * 
 */
public class ConcurrentControlServiceTemplate {

    private ReentrantReadWriteLock lock      = new ReentrantReadWriteLock();
    private ReadLock               readLock  = lock.readLock();
    private WriteLock              writeLock = lock.writeLock();

    public <T> T read(ReadOperation<T> readOp) throws BizException {
        readLock.lock();
        try {
            return readOp.doRead();
        } catch (Exception e) {
            ExceptionUtils.logAndRethrowBizException(e);
            // unreachable
            return null;
        } finally {
            readLock.unlock();
        }
    }

    public <T> T write(WriteOperation<T> writeOp) throws BizException {
        writeLock.lock();
        try {
            return writeOp.doWrite();
        } catch (Exception e) {
            ExceptionUtils.logAndRethrowBizException(e);
            // unreachable
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    public interface ReadOperation<T> {
        T doRead() throws Exception;
    }

    public interface WriteOperation<T> {
        T doWrite() throws Exception;
    }

}
