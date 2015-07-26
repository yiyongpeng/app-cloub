cd `dirname $0`

CLOUB_NAME=cloub
LISTEN_PORT=9000
DEBUG_PORT=9998
LOG_PATH=../logs
JAVA_HOME=/usr/java/default

cd ..
BASE_PATH=`pwd`
echo $BASE_PATH
cd bin

CLASS_PATH=`ls ../lib/*`
CLASS_PATH=`echo $CLASS_PATH | sed -e 's/ /:/g'`:.
echo $CLASS_PATH

MEM="-server -Xms1024m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=512m"
#YJP="-DYJP=enable -agentpath:$BASE_PATH/bin/jyp/linux-x86-64/libyjpagent.so"
#JDEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=n"
JAVA_COMMAND="$JAVA_HOME/bin/java -DcloubName=$CLOUB_NAME -Dconf=../conf/ -DdeployPath=../deploy/  $MEM $YJP $JDEBUG -classpath $CLASS_PATH"
$JAVA_COMMAND app.cloub.StartupCloub $LISTEN_PORT
