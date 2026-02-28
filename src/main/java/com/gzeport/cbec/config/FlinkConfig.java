package com.gzeport.cbec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Flink配置管理类
 */
@Component
public class FlinkConfig {
    @Value("${flink.parallelism}")
    private int parallelism;
    
    @Value("${flink.checkpoint.interval}")
    private long checkpointInterval;
    
    @Value("${flink.checkpoint.timeout}")
    private long checkpointTimeout;
    
    @Value("${flink.state.backend}")
    private String stateBackend;
    
    @Value("${flink.state.checkpoints.dir}")
    private String stateCheckpointsDir;
    
    @Value("${flink.batch.size}")
    private int batchSize;
    
    @Value("${flink.window.size.seconds}")
    private int windowSizeSeconds;
    
    @Value("${flink.restart.strategy.max-attempts:3}")
    private int restartStrategyMaxAttempts;
    
    @Value("${flink.restart.strategy.delay.ms:10000}")
    private long restartStrategyDelayMs;
    
    @Value("${flink.state.backend.incremental:true}")
    private boolean stateBackendIncremental;
    
    @Value("${flink.checkpoint.min-pause-between-checkpoints:5000}")
    private long checkpointMinPauseBetweenCheckpoints;
    
    @Value("${flink.checkpoint.max-concurrent-checkpoints:1}")
    private int checkpointMaxConcurrentCheckpoints;
    
    @Value("${flink.checkpoint.externalized-cleanup:RETAIN_ON_CANCELLATION}")
    private String checkpointExternalizedCleanup;

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public long getCheckpointInterval() {
        return checkpointInterval;
    }

    public void setCheckpointInterval(long checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
    }

    public long getCheckpointTimeout() {
        return checkpointTimeout;
    }

    public void setCheckpointTimeout(long checkpointTimeout) {
        this.checkpointTimeout = checkpointTimeout;
    }

    public String getStateBackend() {
        return stateBackend;
    }

    public void setStateBackend(String stateBackend) {
        this.stateBackend = stateBackend;
    }

    public String getStateCheckpointsDir() {
        return stateCheckpointsDir;
    }

    public void setStateCheckpointsDir(String stateCheckpointsDir) {
        this.stateCheckpointsDir = stateCheckpointsDir;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getWindowSizeSeconds() {
        return windowSizeSeconds;
    }

    public void setWindowSizeSeconds(int windowSizeSeconds) {
        this.windowSizeSeconds = windowSizeSeconds;
    }

    public int getRestartStrategyMaxAttempts() {
        return restartStrategyMaxAttempts;
    }

    public void setRestartStrategyMaxAttempts(int restartStrategyMaxAttempts) {
        this.restartStrategyMaxAttempts = restartStrategyMaxAttempts;
    }

    public long getRestartStrategyDelayMs() {
        return restartStrategyDelayMs;
    }

    public void setRestartStrategyDelayMs(long restartStrategyDelayMs) {
        this.restartStrategyDelayMs = restartStrategyDelayMs;
    }

    public boolean isStateBackendIncremental() {
        return stateBackendIncremental;
    }

    public void setStateBackendIncremental(boolean stateBackendIncremental) {
        this.stateBackendIncremental = stateBackendIncremental;
    }

    public long getCheckpointMinPauseBetweenCheckpoints() {
        return checkpointMinPauseBetweenCheckpoints;
    }

    public void setCheckpointMinPauseBetweenCheckpoints(long checkpointMinPauseBetweenCheckpoints) {
        this.checkpointMinPauseBetweenCheckpoints = checkpointMinPauseBetweenCheckpoints;
    }

    public int getCheckpointMaxConcurrentCheckpoints() {
        return checkpointMaxConcurrentCheckpoints;
    }

    public void setCheckpointMaxConcurrentCheckpoints(int checkpointMaxConcurrentCheckpoints) {
        this.checkpointMaxConcurrentCheckpoints = checkpointMaxConcurrentCheckpoints;
    }

    public String getCheckpointExternalizedCleanup() {
        return checkpointExternalizedCleanup;
    }

    public void setCheckpointExternalizedCleanup(String checkpointExternalizedCleanup) {
        this.checkpointExternalizedCleanup = checkpointExternalizedCleanup;
    }
}
