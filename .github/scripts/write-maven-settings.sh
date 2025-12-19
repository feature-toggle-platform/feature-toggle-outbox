#!/usr/bin/env bash
set -euo pipefail

: "${NEXUS_USERNAME:?Missing NEXUS_USERNAME}"
: "${NEXUS_PASSWORD:?Missing NEXUS_PASSWORD}"

mkdir -p "${HOME}/.m2"

cat > "${HOME}/.m2/settings.xml" <<EOF
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>nexus-public</id>
      <username>${NEXUS_USERNAME}</username>
      <password>${NEXUS_PASSWORD}</password>
    </server>

    <server>
      <id>nexus-releases</id>
      <username>${NEXUS_USERNAME}</username>
      <password>${NEXUS_PASSWORD}</password>
    </server>

    <server>
      <id>nexus-snapshots</id>
      <username>${NEXUS_USERNAME}</username>
      <password>${NEXUS_PASSWORD}</password>
    </server>
  </servers>
</settings>
EOF