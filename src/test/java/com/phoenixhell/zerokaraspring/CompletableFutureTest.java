package com.phoenixhell.zerokaraspring;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author チヨウ　カツヒ
 * @date 2023-10-21 11:38
 */
public class CompletableFutureTest {
    static ReentrantLock lock = new ReentrantLock();
    static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        int numberOfThreads = 3 * 3;

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int finalI = i;
            CompletableFuture<Void> mainFuture = CompletableFuture.runAsync(() -> {
                Path of = Path.of("D:\\test\\test-" + finalI + ".txt");

                try {
                    Thread.sleep(1000);
                    List<CompletableFuture<Void>> subFutures = new ArrayList<>();
                    for (int j = 0; j < 3; j++) {
                        int finalJ = j;
                        CompletableFuture<Void> subFuture = CompletableFuture.runAsync(() -> {
                            check2(of, finalI, finalJ);
                        }, executor);
                        subFutures.add(subFuture);
                    }

                    CompletableFuture<Void>[] subFutureArray = subFutures.toArray(new CompletableFuture[0]);
                    CompletableFuture<Void> allOfSubFutures = CompletableFuture.allOf(subFutureArray);
                    allOfSubFutures.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("check " + finalI + " 完成");
            }, executor);
            futures.add(mainFuture);
        }

        System.out.println(futures.size());
        CompletableFuture<Void>[] futureArray = futures.toArray(new CompletableFuture[0]);
        CompletableFuture<Void> allOfFutures = CompletableFuture.allOf(futureArray);
        allOfFutures.join();

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
            Files.writeString(of, "check2 " + i + "-" + j + " 完成 \n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        System.out.println("check2 " + i + "-" + j + " 完成");
    }
}
