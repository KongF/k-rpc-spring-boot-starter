package com.kong.rpc.io;

import java.util.concurrent.*;

public class KrpcFuture<T> implements Future<T> {
    private T response;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private long beginTime = System.currentTimeMillis();
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
