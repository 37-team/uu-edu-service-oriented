package com.bigitcompany.cloudaireadmodel.aggregation.configuration;

import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CARMContextTaskExecutor implements AsyncTaskExecutor {
    private final AsyncTaskExecutor delegate;

    public CARMContextTaskExecutor(AsyncTaskExecutor delegateExecutor) {
        delegate = delegateExecutor;
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        task = wrap(task);
        delegate.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        task = wrap(task);
        return delegate.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        task = wrap(task);
        return delegate.submit(task);
    }

    @Override
    public void execute(Runnable task) {
        task = wrap(task);
        delegate.execute(task);
    }

    private Runnable wrap(Runnable delegate) {
        return new CARMContextRunnable(delegate);
    }

    private <T> Callable<T> wrap(Callable<T> delegate) {
        return new CARMContextCallable<>(delegate);
    }
}
