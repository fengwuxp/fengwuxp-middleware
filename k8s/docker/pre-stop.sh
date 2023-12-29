#!/bin/bash

# 等待 5s ，给 k8s 足够的时间摘流
sleep 5
echo "exec graceful shutdown"
# springboot 优雅停机
curl -X POST 127.0.0.1:8899/actuator/shutdown
[ $? -eq 0 ] && echo "springboot 优雅关闭"
sleep 3