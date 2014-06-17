package com.thend.home.sweethome.resource;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.thend.home.sweethome.resource.FileResource.FileChangeListener;
/**
 * 监听资源抽象类
 * @author wangkai
 *
 * @param <T>
 */
public abstract class ResourceListener<T> implements FileChangeListener {

	private static final long DEFAULT_CHECK_INTERVAL = TimeUnit.MINUTES.toMillis(5);
	
    private FileResource fileResource;
    
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public ResourceListener(String localFile, long checkInterval) {
    	fileResource = new FileResource(new File(localFile), checkInterval);
    }
    
    public ResourceListener(String url, String localFile, long checkInterval) {
    	fileResource = new FileResource(url, new File(localFile), checkInterval);
    }

    public ResourceListener(String url, String localFile) {
       this(url, localFile, DEFAULT_CHECK_INTERVAL);
    }
    
    public ResourceListener(String localFile) {
        this(localFile, DEFAULT_CHECK_INTERVAL);
     }
    
    public void init() {
    	fileResource.registerListener(this);
    }
    
    public void onFileChange(File file) {
        lock.writeLock().lock();
        try {
           updateResource(file); 
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public T getResult(Object...arg) {
        lock.readLock().lock();
        try {
            return getResource(arg);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    protected abstract T getResource(Object...arg);
    
    protected abstract void updateResource(File file);
}
