#!/bin/bash

### PUBLISHES ONE RECORD TO KAFKA

app_name="kafka_producer_tool"
current_directory=${PWD##*/}

if [ "$current_directory" != "$app_name" ]; then
    # ensure correct current directory as script uses rm -Rf
    echo "ERROR: not inside '$app_name' directory, exiting"
    exit
fi

python3 runner.py $@


