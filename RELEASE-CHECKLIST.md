Release checklist
=================

Once you've started a release branch, *immediately* do the following:
* Bump app version (use the `bumpVersion.sh` script)
* Update `GRAMMAR-VERSIONS.md` with the parameter grammar version used in the release you're about to do
* Update `CHANGELOG.md` with this version's changes
* Build, check everything still works, upload the apks to the app's website, and update the symbolic links
* If doing a public release, think this over again since there's signing involved
