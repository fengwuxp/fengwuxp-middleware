FROM openjdk:8-jdk-alpine

ENV TIME_ZONE="Asia/Shanghai"

# 构建参数
## 应用名
ARG SPRING_APPLICATION_NAME=""
## 暴露的端口
ARG EXPOSE_PORT=8080

# 拷贝 boot jar 到设置工作目录
COPY ./bootstrap.jar /home/admin/run

# 环境变量
ENV SPRING_APPLICATION_NAME=${SPRING_APPLICATION_NAME}

# 端口暴露
EXPOSE ${EXPOSE_PORT}

WORKDIR /home/admin/run
COPY ./startup.sh /home/admin/run
COPY ./pre-stop.sh /home/admin/run

RUN set -ex \
    && sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories \
    && apk update && apk add --no-cache ttf-dejavu fontconfig curl tzdata wget bash-completion busybox \
	&& ln -s /bin/busybox /usr/local/bin/ip \
	&& ln -s /bin/busybox /usr/local/bin/telnet \
	&& ln -s /bin/busybox /usr/local/bin/ping \
	&& ln -s /bin/busybox /usr/local/bin/nslookup \
	&& cp /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime && echo ${TIME_ZONE} > /etc/timezone \
	&& mkdir /opt/arthas && curl -o /opt/arthas/arthas-boot.jar https://arthas.aliyun.com/arthas-boot.jar \
    && mkdir /home/admin/nas && chmod +x /home/admin/run/startup.sh && chmod +x /home/admin/run/pre-stop.sh

# 启动命令
ENTRYPOINT ["sh","-c","/home/admin/run/startup.sh"]
