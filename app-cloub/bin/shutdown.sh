cd `dirname $0`
cd ..
pwd

SHUTDOWN_ADDR=127.0.0.1
SHUTDOWN_PORT=9000

JAVA_HOME=/opt/java/jdk1.6.0_45
CLASS_PATH=`ls lib/*`
CLASS_PATH=`echo $CLASS_PATH | sed -e 's/ /:/g'`:.
echo $CLASS_PATH
JAVA_COMMAND="$JAVA_HOME/bin/java"

$JAVA_COMMAND -classpath $CLASS_PATH  app.rpc.Shutdown "$SHUTDOWN_ADDR:$SHUTDOWN_PORT" "shutdown" "shutdownpwd"