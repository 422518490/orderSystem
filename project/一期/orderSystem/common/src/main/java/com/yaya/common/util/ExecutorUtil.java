package com.yaya.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/10/12
 * @description 线程池
 */
public class ExecutorUtil {

    public final static ExecutorService executorService = new ThreadPoolExecutor(200,1000,
            60L, TimeUnit.SECONDS,new SynchronousQueue<>());

}
