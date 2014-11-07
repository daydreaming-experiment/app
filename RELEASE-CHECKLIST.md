Release checklist
=================

Once you've started a release branch, *immediately* do the following:
* Bump app version (use the `bumpVersion.sh` script)
* Check the parameters URLs in the `ServerConfig` classes (debug, qa, release) are good (e.g. non-dev)
* Update `GRAMMAR-VERSIONS.md` with the parameter grammar version used in the release you're about to do
* Update `CHANGELOG.md` with this version's changes
* Build, check everything still works
* Run `sign.sh <version-name>` to create signed and zip-aligned APKs
* Run `deploy.sh <version-name>` to deploy APKs to website
* Upload APK(s) to the Play Store
