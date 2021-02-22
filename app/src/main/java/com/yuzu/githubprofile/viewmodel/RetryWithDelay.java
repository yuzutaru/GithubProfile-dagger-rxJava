package com.yuzu.githubprofile.viewmodel;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Created by mihuilk base on kjones on 27/09/2017
 */

public class RetryWithDelay implements Function<Flowable<Throwable>, Publisher<?>> {

    private final int maxRetries;
    private long retryDelayMillis;
    private int retryCount;
    private final int delayFactor;

    public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.retryCount = 0;
        this.delayFactor = 2;
    }

    @Override
    public Publisher<?> apply(Flowable<Throwable> throwableFlowable) throws Exception {
        return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Throwable throwable) throws Exception {
                if (++retryCount < maxRetries) {
                    retryDelayMillis = retryDelayMillis * delayFactor;
                    return Flowable.timer(retryDelayMillis,
                            TimeUnit.MILLISECONDS);
                }

                // Max retries hit. Just pass the error along.
                return Flowable.error(throwable);
            }
        });
    }
}