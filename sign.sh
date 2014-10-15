#!/bin/bash
# Sign APKs for publication

set -e

if [ $# != 1 ]; then
    echo "Usage: $(basename $0) <version-number>"
    exit 1
fi

version=$1
KEYSTORE="$HOME/Keys/Android Developer/daydreaming.jks"
PRODUCTIONAPK=daydreaming/build/outputs/apk/daydreaming-production-release-unsigned.apk
BETAAPK=daydreaming/build/outputs/apk/daydreaming-beta-release-unsigned.apk
SPRODUCTIONAPK=daydreaming/build/outputs/apk/daydreaming-production-release-signed.apk
SBETAAPK=daydreaming/build/outputs/apk/daydreaming-beta-release-signed.apk
ZPRODUCTIONAPK=daydreaming/daydreaming-production-${version}+release.apk
ZBETAAPK=daydreaming/daydreaming-beta-${version}+release.apk

echo
echo "##"
echo "## Signing Beta and Production APKs"
echo "##"

cp "$PRODUCTIONAPK" "$SPRODUCTIONAPK"
cp "$BETAAPK" "$SBETAAPK"
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore "$KEYSTORE" "$SPRODUCTIONAPK" "daydreaming release key"
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore "$KEYSTORE" "$SBETAAPK" "daydreaming release key"

echo
echo "##"
echo "## Checking signatures"
echo "##"

jarsigner -verify -certs "$SPRODUCTIONAPK"
jarsigner -verify -certs "$SBETAAPK"

echo
echo "##"
echo "## Zip-aligning Beta and Production APKs"
echo "##"

zipalign -v 4 $SPRODUCTIONAPK "$ZPRODUCTIONAPK"
zipalign -v 4 $SBETAAPK "$ZBETAAPK"

echo
echo "##"
echo "## Checking signatures for zip-aligned APKs"
echo "##"

jarsigner -verify -certs "$ZPRODUCTIONAPK"
jarsigner -verify -certs "$ZBETAAPK"
