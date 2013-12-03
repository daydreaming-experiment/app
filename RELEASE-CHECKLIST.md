Release checklist
=================

Once you've started a release branch, *immediately* do the following:
* Bump app version (use the `bumpVersion.sh` script)
* Update `GRAMMAR-VERSIONS.md` with the parameter grammar version used in the release you're about to do
* Turn off the `DEV` flag in the code
