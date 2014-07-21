#!/bin/bash

set -e

if [ $# != 1 ]
then
  echo "Usage: $(basename $0) version-name"
  exit
fi

VERSION_NAME="$1"
BUILD=daydreaming/build.gradle

echo "Updating version name to ${VERSION_NAME} ..."

## Update daydreaming/build.gradle
echo "Updating $BUILD ..."
TMP1="$(tempfile)"
TMP2="$(tempfile)"

# First the version name
cat $BUILD | sed "s/versionName \"[^\"]*\"/versionName \"${VERSION_NAME}\"/" > "$TMP1"

# Then bump the version code
VERSION_CODE=$(cat $BUILD | grep 'versionCode ' | sed 's/^ *versionCode //')
(( VERSION_CODE += 1 ))
cat "$TMP1" | sed "s/versionCode .*/versionCode ${VERSION_CODE}/" > "$TMP2"

cp "$TMP2" $BUILD
rm "$TMP1"
rm "$TMP2"

echo "Done."
