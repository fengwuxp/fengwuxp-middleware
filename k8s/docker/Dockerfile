FROM openjdk:8-jdk-alpine
LABEL authors="wuxp"

# 镜像默认识别此环境变量控制时区
ENV TZ="Asia/Shanghai"

WORKDIR /home/admin/run
COPY ./startup.sh /home/admin/run
COPY ./prestop.sh /home/admin/run
# 拷贝 boot jar 到设置工作目录
COPY ./bootstrap.jar /home/admin/run

# 构建参数
## 暴露的端口
ARG EXPOSE_PORT=8080

# 端口暴露
EXPOSE ${EXPOSE_PORT}

RUN set -ex \
    && sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories \
    && apk update && apk add --no-cache ttf-dejavu fontconfig curl tzdata wget bash-completion busybox \
	&& ln -s /bin/busybox /usr/local/bin/ip \
	&& ln -s /bin/busybox /usr/local/bin/telnet \
	&& ln -s /bin/busybox /usr/local/bin/ping \
	&& ln -s /bin/busybox /usr/local/bin/nslookup \
	&& mkdir /opt/arthas && curl -o /opt/arthas/arthas-boot.jar https://arthas.aliyun.com/arthas-boot.jar \
    && mkdir /home/admin/nas && chmod +x /home/admin/run/startup.sh && chmod +x /home/admin/run/pre-stop.sh

# 启动命令
ENTRYPOINT ["sh","-c","/home/admin/run/startup.sh"]
