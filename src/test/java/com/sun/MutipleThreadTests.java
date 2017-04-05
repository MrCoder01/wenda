package com.sun;

import java.util.concurrent.*;

import static java.lang.System.out;

/**
 * Created by ty on 2017/3/30.
 */
public class MutipleThreadTests {


    public static void testThread(){
        MyThread t1 = new MyThread(111);
        MyThread t2 = new MyThread(222);
        t1.start();
        t2.start();
    }

    public static void testBlockingQueue(){
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Consumer(q),"消费者1:").start();
        new Thread(new Consumer(q),"消费者2:").start();
        new Thread(new Producer(q),"生产者").start();
    }

    public static void testBox(){
        Box box = new Box(0);
        new Thread(new Producer1(box),"生产者").start();
        new Thread(new Consumer1(box),"消费者1:").start();
        new Thread(new Consumer1(box),"消费者2:").start();
    }

    private static ThreadLocal<Integer> userIds = new ThreadLocal<>();
    private static int uid;


    public  static void testThreadLocal(){
        for(int i=0;i<10;i++){
            final int fi=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        userIds.set(fi);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:" + userIds.get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        for(int i=0;i<10;i++){
            int fi=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        uid=fi;
                        //睡之前赋值，然后其他线程执行，并再次赋值，
                        // 再回到之前的线程时，uid已经发生改变，
                        // 所以所有线程只会以最后一次赋值的线程为准
                        //在为UID赋值与取值这两个时间点时，uid被多个线程操作，
                        // 且int类型是不安全的，就会出现此状况
                        Thread.sleep(1000);
                        System.out.println("uid:" + uid);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    public static void testExecutor(){
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <10 ; i++) {
                    try {
                        // 按任务提交顺序执行，即使有sleep，也不执行下个提交任务
                        //内部跳转
                        Thread.sleep(1000);
                        System.out.println("Executor1:"+i);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2:" + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(1000);
                System.out.println("Wait for termination.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
//        testThread();
//        testBlockingQueue();
//        testBox();
//        testThreadLocal();
        testExecutor();
    }
}

class MyThread extends Thread{
    private int id;
    public MyThread(int id){
        this.id=id;
    }

    private static Object obj = new Object();
    @Override
    //创建线程就是创建任务，run方法即为多线程共享任务
    public void run() {

        synchronized (obj){
        try{
            for(int i=0;i<10;i++){
                //Thread.sleep(1000);
                System.out.println(String.format("%d:%d", id, i));
            }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable{
    private BlockingQueue<String> q;
    public Consumer(BlockingQueue<String> q){
        this.q=q;
    }

    @Override
    public void run() {
        try{
            while(true){
                System.out.println(Thread.currentThread().getName()+q.take());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Producer implements Runnable{
    private BlockingQueue<String> q;
    public Producer(BlockingQueue<String> q){
        this.q=q;
    }

    @Override
    public void run() {
        try{
            for (int i = 0; i <10 ; i++) {
                q.put(String.valueOf(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Box{
    private int size;
    public Box(int size){
        this.size =size;
    }

    public int add(){
        return ++size;
    }

    public int delete(){
        return --size;
    }
}
class Consumer1 implements Runnable{
    private Box box;
    public Consumer1(Box box){
        this.box=box;
    }


    @Override
    public void run() {
        try{
            for (int i = 0; i <10 ; i++) {
                System.out.println(Thread.currentThread().getName()+box.delete());

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Producer1 implements Runnable{
    private Box box;
    public Producer1(Box box){
        this.box=box;
    }

    @Override
    public void run() {
        try{
            for (int i = 0; i <10 ; i++) {
                box.add();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

