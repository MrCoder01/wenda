package com.sun.service;


import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by TY on 2017/8/14.
 */
@Service
public class MyExecutorService {
    private static final Executor executor = Executors.newFixedThreadPool(3);

    public void execute(Runnable r){
        executor.execute(r);
    }
}
