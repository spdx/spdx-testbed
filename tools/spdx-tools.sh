#!/usr/bin/env bash

set -euo pipefail

version=2.2.8
script_dir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cachedir="$script_dir/spdx-tools.sh-cache"
jar="$cachedir/spdx-tools-$version-jar-with-dependencies.jar"

getJarIfNotInCache() (
    if ! [[ -f "$jar" ]]; then
        local url="https://github.com/spdx/tools/releases/download/v$version/spdx-tools-$version.zip"
        tmpdir="$(mktemp -d)"
        cd "$tmpdir"
        wget \
            -nc -q \
            -O "$tmpdir/spdx-tools-$version.zip" \
            "$url" || echo "already existing"
        7z x -y "$tmpdir/spdx-tools-$version.zip" > NUL:

        mkdir -p "$cachedir"
        cp "$tmpdir/$(basename "$jar")" "$jar"
        rm -r "$tmpdir"
    fi
)

getJarIfNotInCache

exec java -jar "$jar" "$@"