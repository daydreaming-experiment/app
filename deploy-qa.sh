#!/bin/bash

set -e

if [ $# != 1 ]; then
    echo "Usage: $(basename $0) <version-number>"
    exit 1
fi

version=$1

echo "##"
echo "## Copying daydreaming-$version+qa.apk to server"
echo "##"
echo

rsync -vhP daydreaming/build/outputs/apk/daydreaming-$version+qa.apk daydreaming@74.207.250.156:daydreaming/srv/releases/

echo
echo "##"
echo "## Setting symlink for daydreaming-latest.apk on server"
echo "##"
echo

ssh daydreaming@74.207.250.156 "cd daydreaming/srv/releases; unlink daydreaming-latest.apk; ln -s daydreaming-$version+qa.apk daydreaming-latest.apk"

echo "##"
echo "## All done!"
echo "##"
echo
