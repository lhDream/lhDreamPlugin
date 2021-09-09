package luhua.site.util;

import java.util.concurrent.*;

/**
 * @description: 线程池
 * @author: lhDream
 * @create: 2021-09-09 09:23
 **/
public class Pool {

    /**
     * 私有化构造方法
     */
    private Pool() {}

    /**
     * 线程池
     */
    private static final ExecutorService THREAD_POOL = createThreadPool();

    /**
     * @description: 获取线程池对象
     * @author: lhDream
     * @create: 2021-09-09 09:23
     **/
    public static ExecutorService getThreadPool(){
        return THREAD_POOL;
    }

    /**
     * @description: 创建线程池
     * @author: lhDream
     * @create: 2021-09-09 09:23
     **/
    private static ExecutorService createThreadPool() {
        return new ThreadPoolExecutor(10, 20,
                10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5, true),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

}
