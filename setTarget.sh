#!/bin/bash

if [ $# != 1 ]
then
  echo "Usage: $(basename $0) api-level"
  exit
fi

get_maven_sdk_version () {
  case "$1" in
    10 ) echo "2.3.3";;
    15 ) echo "4.0.1.2";;
    16 ) echo "4.1.1.4";;
    * ) echo "Unsupported API Level. Aborting"
        exit 1;;
  esac
}

API_LEVEL="$1"
MAVEN_SDK_VERSION=$(get_maven_sdk_version ${API_LEVEL})
echo "Updating target to API Level ${API_LEVEL}, Maven SDK Version $(get_maven_sdk_version ${MAVEN_SDK_VERSION}) ..."

# Update AndroidManifest.xml
echo "Updating AndroidManifest.xml ..."
TMP="$(tempfile)"
cat AndroidManifest.xml | sed "s/android:targetSdkVersion=\"[0-9]\{1,2\}\"/android:targetSdkVersion=\"${API_LEVEL}\"/" > "$TMP"
cp "$TMP" AndroidManifest.xml
rm "$TMP"

# Update pom.xml
echo "Updating pom.xml ..."
TMP1="$(tempfile)"
TMP2="$(tempfile)"
cat pom.xml | sed "/<groupId>com\.google\.android<\/groupId>/,/<\/dependency>/{
    /<artifactId>android<\/artifactId>/,/<\/dependency>/{
        s/<version>[^<]*<\/version>$/<version>${MAVEN_SDK_VERSION}<\/version>/
    }
}" > "$TMP1"
cat "$TMP1" | sed "/<groupId>com\.jayway\.maven\.plugins\.android\.generation2<\/groupId>/,/<\/configuration>/{
    s/<platform>[0-9]\{1,2\}<\/platform>/<platform>${API_LEVEL}<\/platform>/
}" > "$TMP2"
cp "$TMP2" pom.xml
rm "$TMP1"
rm "$TMP2"

echo "Done."
