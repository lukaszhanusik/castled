#!/usr/bin/env bash

set -e

OLD_VERSION=$(grep VERSION .env | cut -d"=" -f2)

[[ -z "$PART_TO_BUMP" ]] && echo "Usage ./tools/bin/release_version.sh (major|minor|patch)" && exit 1

# .bumpversion.cfg has all the files with version number
pip install bumpversion
bumpversion "$PART_TO_BUMP"

NEW_VERSION=$(grep VERSION .env | cut -d"=" -f2)

# Also bump version for the jars
mvn versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
git status

echo "Version bumped from ${OLD_VERSION} to ${NEW_VERSION}"