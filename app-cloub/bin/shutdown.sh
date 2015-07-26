cd `dirname $0`
pwd

SHUTDOWN_ADDR=127.0.0.1
SHUTDOWN_PORT=9000

JAVA_HOME=/usr/java/default
CLASS_PATH=`ls ../lib/*`
CLASS_PATH=`echo $CLASS_PATH | sed -e 's/ /:/g'`:.
echo $CLASS_PATH
JAVA_COMMAND="$JAVA_HOME/bin/java"

$JAVA_COMMAND -classpath $CLASS_PATH  app.rpc.Shutdown "$SHUTDOWN_ADDR:$SHUTDOWN_PORT" "shutdown" "shutdownpwd"
