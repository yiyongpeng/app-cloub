cd `dirname $0`

CLOUB_NAME=cloub
LISTEN_PORT=9000
DEBUG_PORT=8000
LOG_PATH=../logs

cd ..
BASE_PATH=`pwd`
echo $BASE_PATH
cd bin

CLASS_PATH=`ls ../lib/*`
CLASS_PATH=`echo $CLASS_PATH | sed -e 's/ /:/g'`:.
echo $CLASS_PATH

MEM="-server -Xmx5g -Xms5g -XX:PermSize=16M -XX:MaxPermSize=16M -Xss228K -Xmn640M -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:SoftRefLRUPolicyMSPerMB=0"
# -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:$LOG_PATH/gc.log"
#YJP="-DYJP=enable -agentpath:$BASE_PATH/bin/jyp/linux-x86-64/libyjpagent.so"
#JDEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=n"
JAVA_COMMAND="java -DcloubName=$CLOUB_NAME -Dconf=../conf/ -DdeployPath=../deploy/ $MEM $YJP $JDEBUG -classpath $CLASS_PATH"

nohup $JAVA_COMMAND app.cloub.StartupCloub $LISTEN_PORT 1>$LOG_PATH/outlinecloub.log 2>$LOG_PATH/errlinecloub.log &
