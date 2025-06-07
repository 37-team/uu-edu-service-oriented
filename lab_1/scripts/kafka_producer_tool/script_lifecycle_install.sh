#!/usr/bin/env bash

# ensure correct current directory as script uses rm -Rf
app_name="kafka_producer_tool"
current_directory=${PWD##*/}
if [ "$current_directory" != "$app_name" ]; then
    echo "ERROR: not inside '$app_name' directory, exiting"
    exit 1
fi

if [[ "$OSTYPE" == "msys" ]]; then
    rm -Rf env && \
    py -m venv env && \
    source env/Scripts/activate && \
    pip3 install -r requirements.txt && \
    echo ""
else
    rm -Rf env && \
    python3 -m venv env && \
    source ./env/bin/activate && \
    pip3 install -r requirements.txt && \
    echo ""
fi

echo "Setup finished"
