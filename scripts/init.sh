#!/bin/bash
set -e

mkdir -p server

echo "Fetching latest Paper build..."
URL=$(curl -s "https://api.papermc.io/v2/projects/paper/versions/1.21.8/builds/" | \
  jq -r '.builds[-1] | "https://api.papermc.io/v2/projects/paper/versions/1.21.8/builds/\(.build)/downloads/\(.downloads.application.name)"')

echo "Downloading from: $URL"
curl -sLo server/paper.jar "$URL"

echo "eula=true" > server/eula.txt
echo "✓ Paper server downloaded to server/paper.jar"