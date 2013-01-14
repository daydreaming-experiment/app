#!/bin/bash

if [ $# != 1 ]
then
  echo "Usage: $(basename $0) api-level"
  exit
fi

API_LEVEL="$1"

# Get the SDK version in Maven Central corresponding to given API Level
case "${API_LEVEL}" in
  10 ) MAVEN_SDK_VERSION="2.3.3";;
  15 ) MAVEN_SDK_VERSION="4.0.1.2";;
  16 ) MAVEN_SDK_VERSION="4.1.1.4";;
  * ) echo "Unsupported API Level. Aborting" >&2
      exit 1;;
esac

echo "Updating target to API Level ${API_LEVEL}, Maven SDK Version ${MAVEN_SDK_VERSION} ..."

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
cat pom.xml | sed "/<groupId>com\.google\.android<\/groupId>/,/<\/version>/{
    /<artifactId>android<\/artifactId>/,/<\/version>/{
        s/<version>[^<]*<\/version>$/<version>${MAVEN_SDK_VERSION}<\/version>/
    }
}" > "$TMP1"
cat "$TMP1" | sed "/<groupId>com\.jayway\.maven\.plugins\.android\.generation2<\/groupId>/,/<\/configuration>/{
    /<sdk>/,/<\/platform>/{
        s/<platform>[0-9]\{1,2\}<\/platform>/<platform>${API_LEVEL}<\/platform>/
    }
}" > "$TMP2"
cp "$TMP2" pom.xml
rm "$TMP1"
rm "$TMP2"

echo "Done."
