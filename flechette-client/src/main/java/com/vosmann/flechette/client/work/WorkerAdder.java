package com.vosmann.flechette.client.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class WorkerAdder implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(WorkerAdder.class);
    private static final Random RANDOM = new Random();

    private final ScheduledExecutorService workerService;
    private final Runnable worker;
    private final AtomicInteger workerCount;
    private final long executionPeriod;
    private final TimeUnit executionPeriodTimeUnit;

    WorkerAdder(final ScheduledExecutorService workerService, final Runnable worker, final int workerCount,
                       final long executionPeriod, final TimeUnit executionPeriodTimeUnit) {
        this.workerService = workerService;
        this.worker = worker;
        this.workerCount = new AtomicInteger(workerCount);
        this.executionPeriod = executionPeriod;
        this.executionPeriodTimeUnit = executionPeriodTimeUnit;
    }

    @Override
    public void run() {

        if (workerCount.get() <= 0) {
            throw new RuntimeException("All workers added. Suppressing subsequent WorkerAdder executions."); // Ugly
        }

        final long workerDelay = getRandomWorkerDelay(); // To avoid having a synchronized salvo of worker runs.
        LOG.info("Scheduling a worker to run every {} {}. Start delay will be {}.",
                executionPeriod, executionPeriodTimeUnit.toString(), workerDelay);

        workerService.scheduleAtFixedRate(worker, workerDelay, executionPeriod, executionPeriodTimeUnit);
        workerCount.decrementAndGet();
    }

    private long getRandomWorkerDelay() {
        return RANDOM.nextInt() % executionPeriod;
    }

    @Override
    public String toString() {
        return "WorkerAdder{" + "worker=" + worker +
                "executionPeriod=" + executionPeriod + ", executionPeriodTimeUnit=" + executionPeriodTimeUnit + '}';
    }

}
