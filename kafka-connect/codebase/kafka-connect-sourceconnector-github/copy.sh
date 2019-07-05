#!/usr/bin/env bash
_mydir="$(pwd)" ## or ##
_mydir="`pwd`" echo "My working dir: $_mydir"
cp "$(pwd)"/build/libs/kafka-connect-github-source-1.0-SNAPSHOT.jar "$CONFLUENT_HOME/share/java/kafka-connect-github/"
echo "file Copied to " "$CONFLUENT_HOME/share/java/kafka-connect-github/"