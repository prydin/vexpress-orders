#!/bin/bash
export MODULE_NAME=vexpress-orders
set -e
mkdir /opt/$MODULE_NAME
chown $1 /opt/$MODULE_NAME
cd /opt/$MODULE_NAME
mv /tmp/application.properties .
wget --auth-no-challenge --user=$2 --password=$3 $4/artifact/build/libs/$MODULE_NAME-$5.jar -O $MODULE_NAME.jar
mv /tmp/$MODULE_NAME.service /etc/systemd/system
chmod 664 /etc/systemd/system/$MODULE_NAME.service

# Wait until Java is installed
until [ -f /usr/bin/java ]; do
  sleep 10
done

systemctl enable $MODULE_NAME
systemctl start $MODULE_NAME
