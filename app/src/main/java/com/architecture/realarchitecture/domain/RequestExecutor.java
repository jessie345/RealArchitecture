package com.architecture.realarchitecture.domain;

import android.os.Process;
import android.text.TextUtils;

import com.architecture.realarchitecture.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liushuo on 16/3/21.
 * 防止并发访问发生错误 submit,cancelTask,afterExecute 可能处于不同的线程
 */
public class RequestExecutor extends ThreadPoolExecutor {

    private
    static final int POOLSIZE = 3;

    Map<String, List<Future<String>>> mFutures = new HashMap<>();

    public RequestExecutor() {
        super(POOLSIZE, POOLSIZE, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new MyThreadFactory());
    }

    /**
     * 客户端使用本方法提交一个绑定到指定tag的任务，不同情况下可以根据tag取消任务
     *
     * @param tag
     * @param task
     * @return
     */
    public synchronized void submit(String tag, Runnable task) {
        //执行同步操作
        RunnableFuture<String> future = newTaskFor(task, tag);
        if (!TextUtils.isEmpty(tag)) {
            List<Future<String>> list = mFutures.get(tag);
            if (list == null) {
                list = new ArrayList<>();
                mFutures.put(tag, list);
            }
            list.add(future);
        }

        //执行异步任务
        execute(future);


    }

    /**
     * 取消正在执行的或者正在排队的请求
     *
     * @param tag
     */
    public synchronized void cancelTask(String tag) {
        if (TextUtils.isEmpty(tag)) return;
        if (!mFutures.containsKey(tag)) return;

        List<Future<String>> list = mFutures.get(tag);
        for (Future<String> future : list) {
            if (!future.isDone() && !future.isCancelled()) {
                future.cancel(true);
            }
        }
        list.clear();
    }

    @Override
    protected synchronized void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (!(r instanceof Future)) return;

        try {
            Future<String> f = (Future<String>) r;
            String tag = f.get();

            if (TextUtils.isEmpty(tag)) return;
            if (!mFutures.containsKey(tag)) return;

            List<Future<String>> list = mFutures.get(tag);
            list.remove(r);

        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    static class MyThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        MyThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Process.THREAD_PRIORITY_BACKGROUND)
                t.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            return t;
        }
    }
}
