package com.lyflexi.feignx.utils;

import java.util.concurrent.*;

/**
 * @Author: hmly
 * @Date: 2025/3/15 14:57
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: 加入线程池，提升HttpMappingInfo的初始化构建性能
 */
public class ThreadPoolUtils {
    //默认IOU密集型程序核心线程数为`2*N`
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors()*2; // 核心线程数
    private static final int MAX_POOL_SIZE = 2*CORE_POOL_SIZE; // 最大线程数
    private static final long KEEP_ALIVE_TIME = 60L; // 线程空闲时间
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS; // 时间单位
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>(100); // 工作队列
    /**
     * 调用 ThreadPoolUtils.executor.shutdown(); 会影响到 FeignClassScanUtils 和 ControllerClassScanUtils，因为它们共享同一个 ExecutorService 实例。
     *
     * 为了避免这个问题，可以为每个类创建单独的 ExecutorService 实例。以下是修改后的代码示例：
     * @return
     */
    public static ExecutorService createExecutor() {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TIME_UNIT,
                WORK_QUEUE,
                /**
                 * 使用 CallerRunsPolicy 作为拒绝策略，当线程池已满时，任务会在调用者线程中执行。
                 */
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
    public static void main(String[] args) {
        System.out.println("机器默认线程数："+Runtime.getRuntime().availableProcessors());
    }
}
