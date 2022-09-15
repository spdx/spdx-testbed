#!/usr/bin/env bash

set -euo pipefail

version=2.2.8
script_dir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cachedir="$script_dir/spdx-tools.sh-cache"
jar="$cachedir/spdx-tools-$version-jar-with-dependencies.jar"

getJarIfNotInCache() (
    if [ -f "$jar" ]; then
        >&2 echo "$jar already downloaded"
    else
        local url="https://github.com/spdx/tools/releases/download/v$version/spdx-tools-$version.zip"
        tmpdir="$(mktemp -d)"
        cd "$tmpdir"
        wget \
            -nc \
            -O "$tmpdir/spdx-tools-$version.zip" \
            "$url" || echo "already existing"
        7z x -y "$tmpdir/spdx-tools-$version.zip"

        mkdir -p "$cachedir"
        cp "$tmpdir/$(basename "$jar")" "$jar"
        rm -r "$tmpdir"
    fi
)

getJarIfNotInCache

set -x
exec java -jar "$jar" "$@"
