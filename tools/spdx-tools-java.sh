#!/usr/bin/env bash

set -euo pipefail

version=1.1.0
script_dir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cachedir="$script_dir/spdx-tools-java.sh-cache"
jar="$cachedir/tools-java-${version}-jar-with-dependencies.jar"

getJarIfNotInCache() (
    if ! [[ -f "$jar" ]]; then
        local url="https://github.com/spdx/tools-java/releases/download/v${version}/tools-java-${version}.zip"
        tmpdir="$(mktemp -d)"
        cd "$tmpdir"
        wget \
            -nc -q\
            -O "$tmpdir/tools-java-$version.zip" \
            "$url" || echo "already existing"
        7z x -y "$tmpdir/tools-java-$version.zip" > /dev/null

        mkdir -p "$cachedir"
        cp "$tmpdir/$(basename "$jar")" "$jar"
        rm -r "$tmpdir"
    fi
)

getJarIfNotInCache

exec java -jar "$jar" "$@"
