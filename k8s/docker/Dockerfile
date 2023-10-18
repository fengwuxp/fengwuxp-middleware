FROM openjdk:8-alpine
LABEL authors="wuxp"

# 构建参数
## 应用名
ARG SPRING_APPLICATION_NAME=""
## 工作目录
ARG WORK_PATH="/home/admin/run"
##  时区
ARG TIME_ZONE="Asia/Shanghai"
ARG TIME_ZONE_GMT="GMT+08"
## 暴露的端口
ARG EXPOSE_PORT=8080

# 设置工作目录
WORKDIR ${WORK_PATH}
# 拷贝 boot jar 到设置工作目录
COPY ./bootstrap.jar ${WORK_PATH}

# 环境变量
ENV SPRING_APPLICATION_NAME=${SPRING_APPLICATION_NAME}

# add font, fix bug for excel font missing
RUN apk add --no-cache --update ttf-dejavu fontconfig

# 安装 curl
RUN apk add --no-cache curl

# 更改时区
RUN echo ${TIME_ZONE} > /etc/timezone \
    && ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime

# 端口暴露
EXPOSE ${EXPOSE_PORT}

# 启动命令 JAVA_OPTS、SPRING_OPTS 从环境变量中来 -e
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dfile.encoding=UTF8 -Dsun.jnu.encoding=UTF8 -jar bootstrap.jar ${SPRING_APPLICAITON_OPTS}"]