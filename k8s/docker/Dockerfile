FROM registry.us-east-1.aliyuncs.com/nobe/openjdk:8-alpine

ENV TIME_ZONE="Asia/Shanghai"

WORKDIR /home/admin/run
COPY ./startup.sh /home/admin/run
COPY pre-stop.sh /home/admin/run

RUN set -ex \
    && sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories \
    && apk update && apk add --no-cache ttf-dejavu fontconfig curl tzdata wget bash-completion busybox \
	&& ln -s /bin/busybox /usr/local/bin/ip \
	&& ln -s /bin/busybox /usr/local/bin/telnet \
	&& ln -s /bin/busybox /usr/local/bin/ping \
	&& ln -s /bin/busybox /usr/local/bin/nslookup \
	&& cp /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime && echo ${TIME_ZONE} > /etc/timezone \
	&& wget https://capte-nobe-v2-sit.oss-us-east-1.aliyuncs.com/binaries/openjdk/arthas-bin.zip -O /opt/arthas-bin.zip \
	&& mkdir /opt/arthas && unzip /opt/arthas-bin.zip -d /opt/arthas && rm -rf /opt/arthas-bin.zip \
    && mkdir /home/admin/nas && chmod +x /home/admin/run/startup.sh && chmod +x /home/admin/run/pre-stop.sh