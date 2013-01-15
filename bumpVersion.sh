#!/bin/bash

if [ $# != 1 ]
then
  echo "Usage: $(basename $0) version-name"
  exit
fi

VERSION_NAME="$1"

echo "Updating version name to ${VERSION_NAME} ..."

## Update AndroidManifest.xml
echo "Updating AndroidManifest.xml ..."
TMP1="$(tempfile)"
TMP2="$(tempfile)"

# First the version name
cat AndroidManifest.xml | sed "s/android:versionName=\"[^\"]*\"/android:versionName=\"${VERSION_NAME}\"/" > "$TMP1"

# Then bump the version code
VERSION_CODE=$(cat AndroidManifest.xml | grep 'android:versionCode=' | sed 's/^[^\"]*\"\([^\"]\)\".*$/\1/g')
(( VERSION_CODE += 1 ))
cat "$TMP1" | sed "s/android:versionCode=\"[^\"]*\"/android:versionCode=\"${VERSION_CODE}\"/" > "$TMP2"

cp "$TMP2" AndroidManifest.xml
rm "$TMP1"
rm "$TMP2"

## Update pom.xml
echo "Updating pom.xml ..."
TMP="$(tempfile)"
cat pom.xml | sed "/<groupId>com\.brainydroid<\/groupId>/,/<\/version>/{
    s/<version>[^<]*<\/version>$/<version>${VERSION_NAME}<\/version>/
}" > "$TMP"
cp "$TMP" pom.xml
rm "$TMP"

echo "Done."
