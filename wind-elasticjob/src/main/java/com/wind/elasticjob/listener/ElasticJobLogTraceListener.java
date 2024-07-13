package com.wind.elasticjob.listener;

import com.wind.trace.WindTracer;
import org.apache.shardingsphere.elasticjob.infra.listener.ElasticJobListener;
import org.apache.shardingsphere.elasticjob.infra.listener.ShardingContexts;

/**
 * 用于日志 trace 的监听器
 *
 * @author wuxp
 * @date 2024-06-25 10:23
 **/
public class ElasticJobLogTraceListener implements ElasticJobListener {

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        WindTracer.TRACER.trace();
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        WindTracer.TRACER.clear();
    }

    @Override
    public String getType() {
        return ElasticJobLogTraceListener.class.getSimpleName();
    }
}
