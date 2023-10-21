FROM openjdk:8-alpine
LABEL authors="wuxp"

# 构建参数
## 应用名
ARG SPRING_APPLICATION_NAME=""
##  时区
ARG TIME_ZONE="Asia/Shanghai"
ARG TIME_ZONE_GMT="GMT+08"
## 暴露的端口
ARG EXPOSE_PORT=8080

# 设置工作目录
WORKDIR /home/admin/run
# 拷贝 boot jar 到设置工作目录
COPY ./bootstrap.jar /home/admin/run
# 拷贝启动
COPY ./startup.sh /home/admin/run

# 环境变量
ENV SPRING_APPLICATION_NAME=${SPRING_APPLICATION_NAME}

# add font, fix bug for excel font missing
RUN apk add --no-cache --update ttf-dejavu fontconfig

# 更改时区
RUN echo ${TIME_ZONE} > /etc/timezone \
    && ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && chmod +x /home/admin/run/startup.sh

# 端口暴露
EXPOSE ${EXPOSE_PORT}

# 启动命令
ENTRYPOINT ["sh","-c","/home/admin/run/startup.sh"]