cd `dirname $0`
cd ..

CLOUB_NAME=cloub
LISTEN_PORT=9000
DEBUG_PORT=9998
LOG_PATH=/var/www/html/
JAVA_HOME=/usr/java/jdk1.7.0_72

BASE_PATH=`pwd`
echo $BASE_PATH

CLASS_PATH=`ls lib/*`
CLASS_PATH=`echo $CLASS_PATH | sed -e 's/ /:/g'`:.
echo $CLASS_PATH

MEM="-server -Xms256m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=512m"
# YJP="-DYJP=enable -agentpath:$BASE_PATH/bin/libyjpagent.so"
# JDEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=n"

JAVA_COMMAND="$JAVA_HOME/bin/java -DcloubName=$CLOUB_NAME  $MEM $YJP $JDEBUG -classpath $CLASS_PATH"
nohup $JAVA_COMMAND app.cloub.StartupCloub $LISTEN_PORT 1>$LOG_PATH/outlinecloub.log 2>$LOG_PATH/errlinecloub.log &
