#!/bin/bash

WORKING_DIR=`cd $(dirname $0); pwd`
TARGET_DIR=`cd ${WORKING_DIR}/../target; pwd`

CLASS="pt.ist.fenixframework.vacation.VacationBenchmark"

#fenix framework
D_VARS="-Dfenixframework.appName=client"
D_VARS="$D_VARS -Dfenixframework.domainModelURLs=fenix-framework-domain-root.dml,vacation.dml"
D_VARS="$D_VARS -Dfenixframework.ispnConfigFile=ispn.xml"
#D_VARS="$D_VARS -Dfenixframework.coreThreadPoolSize=1"
#D_VARS="$D_VARS -Dfenixframework.maxThreadPoolSize=2"
#D_VARS="$D_VARS -Dfenixframework.keepAliveTime=60000"
#D_VARS="$D_VARS -Dfenixframework.messagingJgroupsFile=jgroups-ff.xml"
D_VARS="$D_VARS -Dfenixframework.jGroupsConfigFile=jgroups-ff.xml"
#D_VARS="$D_VARS -Dfenixframework.useGrouping=true"
D_VARS="$D_VARS -Dfenixframework.expectedInitialNodes=$8"

HOSTNAME=$(hostname)
#HOSTNAME="127.0.0.1"

#jgroups IPv4
D_VARS="$D_VARS -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr=$HOSTNAME"

#log4j
D_VARS="$D_VARS -Dlog4j.configuration=file:$WORKING_DIR/log4j.properties"

#jmx
#D_VARS="$D_VARS -Dcom.sun.management.jmxremote"
#D_VARS="$D_VARS -Dcom.sun.management.jmxremote.authenticate=false"
#D_VARS="$D_VARS -Dcom.sun.management.jmxremote.ssl=false"
#D_VARS="$D_VARS -Dcom.sun.management.jmxremote.port=9998"

#class path
CP="$TARGET_DIR/vacation-2.0-SNAPSHOT.jar:$WORKING_DIR"

for jar in `ls ${TARGET_DIR}/dependency/*.jar`; do
    CP="$CP:$jar";
done

CMD="java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=19999,suspend=y $D_VARS -cp $CP $CLASS -c $1 -n $2 -q $3 -r $4 -t $5 -u $6 -ro $7"
#CMD="java $D_VARS -cp $CP $CLASS -c $1 -n $2 -q $3 -r $4 -t $5 -u $6 -ro $7"
#echo $CMD
eval $CMD
