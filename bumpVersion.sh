#!/bin/bash

if [ $# != 1 ]
then
  echo "Usage: $(basename $0) version-name"
  exit
fi

VERSION_NAME="$1"
MANIFEST=daydreaming/src/main/AndroidManifest.xml

echo "Updating version name to ${VERSION_NAME} ..."

## Update AndroidManifest.xml
echo "Updating $MANIFEST ..."
TMP1="$(tempfile)"
TMP2="$(tempfile)"

# First the version name
cat $MANIFEST | sed "s/android:versionName=\"[^\"]*\"/android:versionName=\"${VERSION_NAME}\"/" > "$TMP1"

# Then bump the version code
VERSION_CODE=$(cat $MANIFEST | grep 'android:versionCode=' | sed 's/^[^\"]*\"\([^\"]*\)\".*$/\1/g')
(( VERSION_CODE += 1 ))
cat "$TMP1" | sed "s/android:versionCode=\"[^\"]*\"/android:versionCode=\"${VERSION_CODE}\"/" > "$TMP2"

cp "$TMP2" $MANIFEST
rm "$TMP1"
rm "$TMP2"

echo "Done."
