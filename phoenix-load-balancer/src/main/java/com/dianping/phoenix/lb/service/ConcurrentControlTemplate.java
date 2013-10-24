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

/**
 * @author Leo Liang
 * 
 */
public class ConcurrentControlTemplate {

    private ReentrantReadWriteLock lock      = new ReentrantReadWriteLock();
    private ReadLock               readLock  = lock.readLock();
    private WriteLock              writeLock = lock.writeLock();

    public <T> T read(ReadOperation<T> readOp) throws BizException {
        readLock.lock();
        try {
            return readOp.doRead();
        } finally {
            readLock.unlock();
        }
    }

    public <T> T write(WriteOperation<T> writeOp) throws BizException {
        writeLock.lock();
        try {
            return writeOp.doWrite();
        } finally {
            writeLock.unlock();
        }
    }

    public interface ReadOperation<T> {
        T doRead() throws BizException;
    }

    public interface WriteOperation<T> {
        T doWrite() throws BizException;
    }

}
