#!/bin/bash

read_property(){
    grep "^$2=" "$1" | cut -d'=' -f2
}

if [ -z "$JAVA_HOME" ] ; then
  JAVACMD=`which java`
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "The JAVA_HOME environment variable is not defined correctly" >&2
  echo "This environment variable is needed to run this program" >&2
  echo "NB: JAVA_HOME should point to a JDK not a JRE" >&2
  exit 1
fi

saveddir=`pwd`
NOBE_HOME=`dirname "$0"`/..
NOBE_HOME=`cd "$NOBE_HOME" && pwd`
cd "$saveddir"

# JAVA_OPTS、$SPRING_CLOUD_OPTS、$SPRING_APPLICATION_OPTS 从环境变量中来
exec "$JAVACMD" \
  $JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/home/admin/nas/gc-$POD_IP-$(date '+%s').log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/admin/nas/dump-$POD_IP-$(date '+%s').hprof \
  $SPRING_CLOUD_OPTS \
  $SPRING_APPLICATION_OPTS \
  -Dfile.encoding=UTF8 \
  -Dsun.jnu.encoding=UTF8 \
  -jar bootstrap.jar $@