package com.neoby.dm.middleware.threads;

import com.neoby.dm.middleware.domain.DataContent;
import com.neoby.dm.middleware.service.RedisService;
import com.neoby.dm.middleware.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.*;

@Component
public class CustomerThreadPoolManager implements BeanFactoryAware {

    // 线程池维护线程的最少数量
    private final static int CORE_POOL_SIZE = 5;
    // 线程池维护线程的最大数量
    private final static int MAX_POOL_SIZE = 10;
    // 线程池维护线程所允许的空闲时间
    private final static int KEEP_ALIVE_TIME = 1;
    // 线程池所使用的缓冲队列大小
    private final static int WORK_QUEUE_SIZE = 50;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RedisService redisService;


    public static Logger logger = LoggerFactory.getLogger(CustomerThreadPoolManager.class);
    /**
     * 线程池的定时任务----> 称为(调度线程池)。此线程池支持 定时以及周期性执行任务的需求。
     */
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(CORE_POOL_SIZE);

    /**
     * 订单的缓冲队列,当线程池满了，则将数据存入到此缓冲队列
     */
    Queue<Object> msgQueue = new LinkedBlockingQueue<Object>();

    /**
     * 当线程池的容量满了，执行下面代码，将数据存入到缓冲队列
     */
    final RejectedExecutionHandler handler = (r, executor) -> {
        //数据加入到缓冲队列
        msgQueue.offer(((BusinessThread) r).getDocument());
        logger.debug("系统任务繁忙,把此数据交给(调度线程池)逐一处理，数据：" + ((BusinessThread) r).getDocument());
    };

    /**
     * 创建线程池
     */
    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS, new ArrayBlockingQueue(WORK_QUEUE_SIZE), this.handler);

    /**
     * 检查(调度线程池)，每秒执行一次，查看订单的缓冲队列是否有记录，则重新加入到线程池
     */
    final ScheduledFuture scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            //判断缓冲队列是否存在记录
            if (!msgQueue.isEmpty()) {
                //当线程池的队列容量少于WORK_QUEUE_SIZE，则开始把缓冲队列的订单 加入到 线程池
                if (threadPool.getQueue().size() < WORK_QUEUE_SIZE) {
                    String document = (String) msgQueue.poll();
                    BusinessThread businessThread = new BusinessThread(document, repositoryService);
                    threadPool.execute(businessThread);
                    logger.debug("(调度线程池)缓冲队列出现待处理数据，重新添加到线程池");
                }
            }
        }
    }, 0, 1, TimeUnit.SECONDS);
    //用于从IOC里取对象
    private BeanFactory factory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.factory = beanFactory;
    }

    /**
     * 将任务加入线程池
     */
    public void addQue(DataContent dataContent) {
        logger.debug("此数据准备添加到线程池，数据：" + dataContent);
        //验证当前进入的数据是否已经存在
        if (redisService.get(dataContent.key().toString()) == null) {
            redisService.put(dataContent.key().toString(), dataContent.value());
            BusinessThread businessThread = new BusinessThread(dataContent.value().toString(), repositoryService);
            threadPool.execute(businessThread);
        }
    }

    /**
     * 获取消息缓冲队列
     */
    public Queue<Object> getMsgQueue() {
        return msgQueue;
    }

    /**
     * 终止订单线程池+调度线程池
     */
    public void shutdown() {
        //true表示如果定时任务在执行，立即中止，false则等待任务结束后再停止
        logger.info("终止订单线程池+调度线程池：" + scheduledFuture.cancel(false));
        scheduler.shutdown();
        threadPool.shutdown();
    }

}
