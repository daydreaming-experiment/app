#!/bin/bash

set -e

if [ $# != 1 ]; then
    echo "Usage: $(basename $0) <version-number>"
    exit 1
fi

version=$1

echo "##"
echo "## Copying daydreaming-{production,beta}-$version+release.apk to server"
echo "##"
echo

rsync -vhP daydreaming/daydreaming-production-$version+release.apk daydreaming@74.207.250.156:daydreaming/srv/releases/
rsync -vhP daydreaming/daydreaming-beta-$version+release.apk daydreaming@74.207.250.156:daydreaming/srv/releases/

echo
echo "##"
echo "## Setting symlinks for daydreaming-{production,beta}-latest.apk on server"
echo "##"
echo

ssh daydreaming@74.207.250.156 "cd daydreaming/srv/releases; unlink daydreaming-production-latest.apk; ln -s daydreaming-production-$version+release.apk daydreaming-production-latest.apk"
ssh daydreaming@74.207.250.156 "cd daydreaming/srv/releases; unlink daydreaming-beta-latest.apk; ln -s daydreaming-beta-$version+release.apk daydreaming-beta-latest.apk"

echo "##"
echo "## All done!"
echo "##"
echo
