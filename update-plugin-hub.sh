#!/usr/bin/env bash
set -e

# utility script that updates the plugin-hub version

pluginname=brimhaven-agility

currentdir=$(pwd)
scriptdir=$(dirname "$(readlink -f "$0")")

cd $scriptdir
hash=$(git rev-parse --verify HEAD)
version=$(cat gradle.properties | grep -Eo '[0-9]+\.[0-9]+\.[0-9]+')
echo "using commit hash $hash for version $version of $pluginname"
read -p "Is this correct? (Y/n): " confirm
if [[ ! $confirm =~ ^([Yy]([Ee][Ss])?)?$ ]]; then
    cd "$currentdir"
    exit 1
fi

cd ${PLUGIN_HUB_DIR:-"$scriptdir/../plugin-hub"}
git fetch upstream
git checkout -B $pluginname upstream/master
commitmsg="update $pluginname to $version"
sed -i "s/commit=[0-9a-f]*/commit=$hash/" "./plugins/${pluginname}"
echo
echo "new file contents: "
cat "./plugins/${pluginname}"
echo
echo
echo "commit message: \"$commitmsg\""
echo "pushing to branch \"$pluginname\""

read -p "Is this OK? (Y/n): " confirm
if [[ ! $confirm =~ ^([Yy]([Ee][Ss])?)?$ ]]; then
    echo "run \"git reset --hard\" in ${PLUGIN_HUB_DIR:-"$scriptdir/../plugin-hub"} to clean up"
    cd "$currentdir"
    exit 1
fi

git add "./plugins/${pluginname}"
git commit -m "$commitmsg"
git push -f -u origin $pluginname

cd $currentdir