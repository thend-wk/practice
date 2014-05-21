package com.thend.home.sweethome.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thend.home.sweethome.httpclient.HttpClientUtil;
/**
 * 资源监听工具，包括本地资源和远程资源
 * @author wangkai
 *
 */
public class FileResource {
	
	private static final Log LOG = LogFactory.getLog(FileResource.class);

    private static boolean IS_WINDOWS = System.getProperty("os.name")
            .toLowerCase().startsWith("win");

    //远程路径
    private String url;
    //本地路径
    private File local;
    //观察者
    private List<ListenerItem> listeners = new ArrayList<ListenerItem>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
    	
    	public Thread newThread(Runnable r) {
    		Thread t = new Thread(r, "fileResource-check-thread");
            t.setDaemon(true);
            return t;
            }
     	});
    
    //本地监听
    public FileResource(File local, long checkInterval) {
        this.local = local;
        Runnable checkRunnable = new Runnable() {
            public void run() {
                try {
                    checkLocalUpdate();
                } catch (Throwable e) {
                    LOG.warn("check update failed!", e);
                }
            }
        };
        scheduler.scheduleWithFixedDelay(checkRunnable, 100, checkInterval,
                TimeUnit.MILLISECONDS);
    }
    //远程监听
    public FileResource(String url, File local, long checkInterval) {
        this.url = url;
        this.local = local;
        Runnable checkRunnable = new Runnable() {
            public void run() {
                try {
                    checkHttpUpdate();
                } catch (Throwable e) {
                    LOG.warn("check update failed!", e);
                }
            }
        };
        scheduler.scheduleWithFixedDelay(checkRunnable, 10, checkInterval,
                TimeUnit.MILLISECONDS);
    }
    
    /**
     * HTTP更新监控
     * @param type
     * @throws IOException
     */
    private void checkHttpUpdate() throws IOException {
		String remoteContent = HttpClientUtil.getInstance().execute(url);
		boolean isUpdate = false;
		if(!local.exists()) {
			isUpdate = true;
		} else {
			String localContent = FileUtils.readFileToString(local);
			if(!remoteContent.equals(localContent)) {
				isUpdate = true;
			}
		}
        if (isUpdate) {
        	File tmpFile = new File(local + ".tmp");
            LOG.info("New data saved in temp file " + tmpFile.getAbsolutePath() + " ...");
            FileUtils.writeStringToFile(tmpFile, remoteContent);
            if (IS_WINDOWS) {
                local.delete();
            }
            if (!tmpFile.renameTo(local)) {
                throw new IOException("rename " + tmpFile.getAbsolutePath()
                        + " to " + local.getAbsolutePath() + " failed");
            }
            LOG.info("New data renamed to " + local.getAbsolutePath());

            synchronized (this) {
                long lastModified = local.lastModified();
                for (ListenerItem item: listeners) {
                    if (item.lastModified != lastModified) {
                        LOG.info("Call onFileChange() on item " + item
                                + "(" + item.lastModified + " != "
                                + lastModified + ")");
                        item.listener.onFileChange(local);
                        item.lastModified = lastModified;
                    } else {
                        LOG.warn("Item time is same to the new file ("
                                + item.lastModified + "==" + lastModified
                                + "), could it be possible?");
                    }
                }
            }
        }
    }
    
    /**
     * 本地更新监控
     * @param type
     * @throws IOException
     */
    private void checkLocalUpdate() throws IOException {
    	synchronized (this) {
            long lastModified = local.lastModified();
            for (ListenerItem item: listeners) {
                if (item.lastModified != lastModified) {
                    LOG.info("Call onFileChange() on item " + item
                            + "(" + item.lastModified + " != "
                            + lastModified + ")");
                    item.listener.onFileChange(local);
                    item.lastModified = lastModified;
                } else {
                    LOG.warn("Item time is same to the new file ("
                            + item.lastModified + "==" + lastModified
                            + "), could it be possible?");
                }
            }
        }
    }
    
    /**
     * 注册观察者
     * @param listener
     */
    public void registerListener(FileChangeListener listener) {
        synchronized (this) {
            if (local.exists()) {
                listener.onFileChange(local);
                listeners.add(new ListenerItem(listener, local.lastModified()));
            } else {
                listeners.add(new ListenerItem(listener, 0));
            }
        }
    }
    
    /*
     * 回调接口
     */
    public static interface FileChangeListener {
        public void onFileChange(File file);
    }

    /*
     * 观察者对象
     */
    private class ListenerItem {
        private FileChangeListener listener;

        private long lastModified;

        public ListenerItem(FileChangeListener listener, long lastModified) {
            this.listener = listener;
            this.lastModified = lastModified;
        }
    }

}
