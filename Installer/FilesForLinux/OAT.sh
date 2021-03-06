#!/bin/bash
#
#
#    /etc/rc.d/init.d/OAT
#
# OAT  This shell script takes care of starting and stopping
#    the OAT daemon.
#
# chkconfig: 2345 99 99
# description: Host Integrity at Startup (OAT) sends a TCG defined Integrity Report on startup..
# processname: hisd

# Source function library.
. /etc/init.d/functions

JAVA=/usr/bin/java
TROUSERS=/usr/sbin/tcsd
prog="java"
OATD=/OAT/OAT_Standalone.jar
pid_file=/var/run/his.pid
lock_file=/var/lock/subsys/his
log_file=/var/log/OAT.log
RETVAL=0

[ -x ${TROUSERS} ] || exit 0
service tcsd status || failure $"tcsd needs to be running" || exit 0
[ -x ${JAVA} ] || exit 0

OAT_status(){
	if [ -e "$pid_file" ]; then
		pid=$"`cat $pid_file`"
		item=`ps aux | grep "$pid\ "`
		if [ $"$item" ]; then
			echo $"OAT (pid $pid) is running..."
		else
			echo $"OAT is stopped"
		fi
	else
		echo $"OAT is stopped"
	fi
}

start() {
	#[ -x $OATD ] || exit 5
	[ -f /OAT/OAT.properties ] || exit 6

	echo -n $"Starting $OATD: "
	$JAVA -jar $OATD /OAT/ -d > "$log_file" 2>&1 &
	PID=$!
	RETVAL=$?
	[ "$RETVAL" = 0 ] && touch $lock_file && echo $PID > $pid_file
	echo
	return $RETVAL
}

stop() {
	#[ -x $OATD ] || exit 5
	[ -f /OAT/OAT.properties ] || exit 6
	#echo $pid_file
	if [ -e "$pid_file" ] ; then
		pid=$"`cat $pid_file`"
		kill -9 $pid
		item=`ps aux | grep "$pid\ "`
		#echo $item
		if [ $"$item" ]; then
			failure $"Stopping $OATD"
		else
			success $"Stopping $OATD"
		fi
	else
		failure $"Stopping $OATD"
	fi
	RETVAL=$?
	# if we are in halt or reboot runlevel kill all running sessions
	# so the OAT connections are closed cleanly
	if [ "x$runlevel" = x0 -o "x$runlevel" = x6 ] ; then
		trap '' TERM
		killall $prog 2>/dev/null
		trap TERM
	fi
	[ "$RETVAL" = 0 ] && rm -f $lock_file && rm -f $pid_file
	echo
}

restart() {
	stop
	start
}

status() {
	OAT_status
	RETURN=$?
}

case "$1" in
	start)
		start
		;;
	stop)
		stop
		;;
	restart)
		restart
		;;
	status)
		status
		;;
	*)
		echo "Usage: his {start|stop|restart|status}"
		exit 1
		;;
esac
exit $?
