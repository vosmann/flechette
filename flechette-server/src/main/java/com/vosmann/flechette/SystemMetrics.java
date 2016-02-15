package com.vosmann.flechette;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Tcp;

public class SystemMetrics {

    Cpu cpu;
    Mem mem;
    Tcp tcp;
    int tcpEstablishedCount;
    int tcpIdleCount;
    int tcpCloseCount;
    int tcpClosingCount;
    int tcpCloseWaitCount;
    int tcpTimeWaitCount;
    int tcpBoundCount;

    public Cpu getCpu() {
        return cpu;
    }

    public Mem getMem() {
        return mem;
    }

    public Tcp getTcp() {
        return tcp;
    }

    public int getTcpEstablishedCount() {
        return tcpEstablishedCount;
    }

    public int getTcpIdleCount() {
        return tcpIdleCount;
    }

    public int getTcpCloseCount() {
        return tcpCloseCount;
    }

    public int getTcpClosingCount() {
        return tcpClosingCount;
    }

    public int getTcpCloseWaitCount() {
        return tcpCloseWaitCount;
    }

    public int getTcpTimeWaitCount() {
        return tcpTimeWaitCount;
    }

    public int getTcpBoundCount() {
        return tcpBoundCount;
    }

    private SystemMetrics(Builder builder) {
        cpu = builder.cpu;
        mem = builder.mem;
        tcp = builder.tcp;
        tcpEstablishedCount = builder.tcpEstablishedCount;
        tcpIdleCount = builder.tcpIdleCount;
        tcpCloseCount = builder.tcpCloseCount;
        tcpClosingCount = builder.tcpClosingCount;
        tcpCloseWaitCount = builder.tcpCloseWaitCount;
        tcpTimeWaitCount = builder.tcpTimeWaitCount;
        tcpBoundCount = builder.tcpBoundCount;
    }

    public static final class Builder {

        private Cpu cpu;
        private Mem mem;
        private Tcp tcp;
        private int tcpEstablishedCount;
        private int tcpIdleCount;
        private int tcpCloseCount;
        private int tcpClosingCount;
        private int tcpCloseWaitCount;
        private int tcpTimeWaitCount;
        private int tcpBoundCount;

        public Builder cpu(Cpu val) {
            cpu = val;
            return this;
        }

        public Builder mem(Mem val) {
            mem = val;
            return this;
        }

        public Builder tcp(Tcp val) {
            tcp = val;
            return this;
        }

        public Builder tcpEstablishedCount(int val) {
            tcpEstablishedCount = val;
            return this;
        }

        public Builder tcpIdleCount(int val) {
            tcpIdleCount = val;
            return this;
        }

        public Builder tcpCloseCount(int val) {
            tcpCloseCount = val;
            return this;
        }

        public Builder tcpClosingCount(int val) {
            tcpClosingCount = val;
            return this;
        }

        public Builder tcpCloseWaitCount(int val) {
            tcpCloseWaitCount = val;
            return this;
        }

        public Builder tcpTimeWaitCount(int val) {
            tcpTimeWaitCount = val;
            return this;
        }

        public Builder tcpBoundCount(int val) {
            tcpBoundCount = val;
            return this;
        }

        public SystemMetrics build() {
            return new SystemMetrics(this);
        }
    }

}
