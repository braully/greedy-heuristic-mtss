#!/bin/bash

message=$( cat )

echo $message$|grep xls|sed 's/xls: //g'|sed 's/\t/,/g'