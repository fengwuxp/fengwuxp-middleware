#!/bin/bash

# dump jvm 线程状态
timestamp=`date +%s`
datetime=`date +%F`
[ ! -d /data/jvm-dump/$SPRING_APPLICATION_NAME/$datetime ] && mkdir -p /data/jvm-dump/$JVM_DUMP_DIR/$SPRING_APPLICATION_NAME/$datetime
jstack -l 1 > /data/jvm-dump/$JVM_DUMP_DIR/$SPRING_APPLICATION_NAME/$datetime/thread-$POD_NAME-$timestamp.log

# 等待 20s ，给 k8s 足够的时间摘流以及应用处理未完成的请求
sleep 20
echo "exec graceful shutdown"
# springboot 优雅停机
curl -X POST 127.0.0.1:8899/actuator/shutdown
[ $? -eq 0 ] && echo "springboot 优雅关闭"
sleep 10