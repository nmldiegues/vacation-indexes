#!/bin/bash

WORKING_DIR=`cd $(dirname $0); pwd`
TARGET_DIR=`cd ${WORKING_DIR}/../target; pwd`

CLASS="org.jgroups.stack.GossipRouter"

#jgroups IPv4
D_VARS="-Djava.net.preferIPv4Stack=true"

#class path
CP="$WORKING_DIR"

for jar in `ls ${TARGET_DIR}/dependency/*.jar | grep jgroups`; do
    CP="$CP:$jar";
done

case $1 in
    start)
        shift 1;
        CMD="java $D_VARS -cp $CP $CLASS $*"
        echo $CMD
        nohup ${CMD} > gossip 2>&1 &
        ;;
    stop)
        PID=`ps -fU ${USER} | grep ${CLASS} | grep -v grep | awk '{print $2}'`
        if [ -z "$PID" ]; then
            echo "Gossip Router is not running"
        else
            kill -9 $PID
        fi
        ;;
    status)
        PID=`ps -fU ${USER} | grep ${CLASS} | grep -v grep | awk '{print $2}'`
        if [ -z "$PID" ]; then
            echo "Gossip Router is not running"
        else
            echo "Gossip Router is running. PID=$PID"
        fi
        ;;
    *)
        echo "Unknown option '$1'. usage: $0 [start,stop,status] [gossip router options]"
        ;;
esac
