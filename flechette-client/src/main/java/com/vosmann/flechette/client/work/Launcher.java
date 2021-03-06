package com.vosmann.flechette.client.work;

import com.vosmann.flechette.client.work.workers.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.vosmann.flechette.client.Preconditions.checkMin;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private final int threadCount;
    private final long rampUpTime;
    private final TimeUnit rampUpTimeUnit;
    private final WorkerAdder workerAdder;

    private final long workerAddingPeriodInMs; // How long to wait before adding the next worker.
    private final long startDelayInMs; // How long to wait before beginning to add workers.

    private final ScheduledExecutorService workerService;
    private final ScheduledExecutorService workerAddingService;

    private Launcher(final Builder builder) {

        checkSanity(builder);

        threadCount = builder.threadCount;
        rampUpTime = builder.rampUpTime;
        rampUpTimeUnit = builder.rampUpTimeUnit;

        workerService = Executors.newScheduledThreadPool(threadCount);
        workerAddingService = Executors.newScheduledThreadPool(1);

        workerAdder = new WorkerAdder(workerService, builder.worker, threadCount,
                builder.executionPeriod, builder.executionPeriodTimeUnit);

        startDelayInMs = calculateStartDelay(builder.executionPeriod, builder.executionPeriodTimeUnit);
        workerAddingPeriodInMs = calculateWorkerAddingPeriod(threadCount, rampUpTime, rampUpTimeUnit);

        LOG.info("Worker adding will start in {} ms. A new worker will be added every {} ms.",
                startDelayInMs, workerAddingPeriodInMs);
        workerAddingService.scheduleAtFixedRate(workerAdder, startDelayInMs, workerAddingPeriodInMs, MILLISECONDS);
    }

    private static void checkSanity(final Builder builder) {
        checkMin(builder.threadCount, 1, "Thread count lower than 1.");

        requireNonNull(builder.rampUpTimeUnit, "Ramp-up time unit not set.");
        requireNonNull(builder.executionPeriodTimeUnit, "Execution period unit not set.");

        final long rampUpTimeInMinutes = MINUTES.convert(builder.rampUpTime, builder.rampUpTimeUnit);
        checkMin(rampUpTimeInMinutes, 5, "Ramp-up time lower than 5 minutes.");

        final long executionPeriodInMs = MILLISECONDS.convert(builder.executionPeriod,builder.executionPeriodTimeUnit);
        checkMin(executionPeriodInMs, 10, "Execution period lower than 10 milliseconds."); // Arbitrary.

        requireNonNull(builder.worker, "Worker not set.");
    }

    private long calculateStartDelay(final long executionPeriod,  final TimeUnit timeUnit) {
        final long delay = 2 * executionPeriod; // Arbitrary.
        final long delayInMs = MILLISECONDS.convert(delay, timeUnit);
        LOG.info("Workers will execute every: {} {}. Start delay: {} {}.", executionPeriod, timeUnit, delay, timeUnit);
        return delayInMs;
    }

    private long calculateWorkerAddingPeriod(final int threadCount,
                                             final long rampUpTime, final TimeUnit rampUpTimeUnit) {
        final long rampUpTimeInMs = MILLISECONDS.convert(rampUpTime, rampUpTimeUnit);
        // frequency == (threadCount / rampUpTimeInMs)
        // period == (1 / frequency) == (rampUpTimeInMs / threadCount)
        final long workerAddingPeriodInMs = rampUpTimeInMs / threadCount;
        checkMin(workerAddingPeriodInMs, 5, "Worker-adding period lower than 5 ms."); // Arbitrary.
        return workerAddingPeriodInMs;
    }

    @Override
    public String toString() {
        return "Launcher{" + "threadCount=" + threadCount + ", rampUpTime=" + rampUpTime + "," +
                "rampUpTimeUnit=" + rampUpTimeUnit + ", workerAdder=" + workerAdder + "," +
                "workerAddingPeriodInMs=" + workerAddingPeriodInMs + ", startDelayInMs=" + startDelayInMs + '}';
    }

    public static final class Builder {
        private int threadCount;
        private long rampUpTime;
        private TimeUnit rampUpTimeUnit;
        private long executionPeriod;
        private TimeUnit executionPeriodTimeUnit;
        private Worker worker;

        public Builder threadCount(int val) {
            threadCount = val;
            return this;
        }

        public Builder rampUpTime(long val) {
            rampUpTime = val;
            return this;
        }

        public Builder rampUpTimeUnit(TimeUnit val) {
            rampUpTimeUnit = val;
            return this;
        }

        public Builder executionPeriod(long val) {
            executionPeriod = val;
            return this;
        }

        public Builder executionPeriodTimeUnit(TimeUnit val) {
            executionPeriodTimeUnit = val;
            return this;
        }

        public Builder worker(Worker val) {
            worker = val;
            return this;
        }

        public Launcher build() {
            return new Launcher(this);
        }

    }

}
