package com.phoenixhell.zerokaraspring;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author チヨウ　カツヒ
 * @date 2023-10-21 10:00
 */
public class ExecutorTest {
    static ReentrantLock lock = new ReentrantLock();
    static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        int numberOfThreads = 3*3;
        CountDownLatch startLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < 3; i++) {
            int finalI = i;
            executor.execute(() -> {
                Path of = Path.of("D:\\test\\test-"+finalI+".txt");

                try {
                    Thread.sleep(1000);
                    for (int j = 0; j < 3; j++) {
                        int finalJ = j;
                        executor.execute(() ->{
                            check2(of,finalI,finalJ);
                            startLatch.countDown();
                        });
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                startLatch.countDown();
                System.out.println("check "+ finalI +" 完成");
            });
        }

        try {
            startLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 关闭线程池，不再接受新的任务
        executor.shutdown();
        try {
            // 等待所有任务执行完成或达到指定的等待时间
            if (executor.awaitTermination(30, TimeUnit.MINUTES)) {
                System.out.println("所有任务已完成");
            } else {
                System.out.println("等待超时");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void check1() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("check 1 完成");
    }

    private static void check2(Path of, int i, int j) {
        lock.lock();
        try {
            Files.writeString(of, "check2 "+i+"-"+j+" 完成 \n",  StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
        System.out.println("check2 "+i+"-"+j+" 完成");
    }
}
