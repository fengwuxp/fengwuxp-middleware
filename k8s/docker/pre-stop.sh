#!/bin/bash

# 等待 20s ，给 k8s 足够的时间摘流以及应用处理未完成的请求
sleep 20
echo "exec graceful shutdown"
# springboot 优雅停机
curl -X POST 127.0.0.1:8899/actuator/shutdown
[ $? -eq 0 ] && echo "springboot 优雅关闭"
sleep 5