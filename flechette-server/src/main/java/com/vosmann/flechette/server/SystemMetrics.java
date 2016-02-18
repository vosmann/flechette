package com.vosmann.flechette.server;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Tcp;

public class SystemMetrics {

    CpuPerc cpuPerc;
    Mem mem;
    Tcp tcp;
    int tcpEstablishedCount;
    int tcpIdleCount;
    int tcpCloseCount;
    int tcpClosingCount;
    int tcpCloseWaitCount;
    int tcpTimeWaitCount;
    int tcpBoundCount;

    public CpuPerc getCpu() {
        return cpuPerc;
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
        cpuPerc = builder.cpuPerc;
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

        private CpuPerc cpuPerc;
        private Mem mem;
        private Tcp tcp;
        private int tcpEstablishedCount;
        private int tcpIdleCount;
        private int tcpCloseCount;
        private int tcpClosingCount;
        private int tcpCloseWaitCount;
        private int tcpTimeWaitCount;
        private int tcpBoundCount;

        public Builder cpuPerc(CpuPerc val) {
            cpuPerc = val;
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
