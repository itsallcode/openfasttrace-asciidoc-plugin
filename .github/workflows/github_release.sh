#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

base_dir="$( cd "$(dirname "$0")/../.." >/dev/null 2>&1 ; pwd -P )"
readonly base_dir
readonly pom_file="$base_dir/pom.xml"

# Read project version from pom file
project_version=$(grep "<version>" "$pom_file" | sed --regexp-extended 's/\s*<version>(.*)<\/version>\s*/\1/g' | head --lines=1)
readonly project_version
echo "Read project version '$project_version' from $pom_file"

# Calculate checksum
readonly artifact_path="$base_dir/target/openfasttrace-asciidoc-plugin-${project_version}-with-dependencies.jar"
echo "Calculate sha256sum for file '$artifact_path'"
file_dir="$(dirname "$artifact_path")"
readonly file_dir
file_name=$(basename "$artifact_path")
readonly file_name
cd "$file_dir"
readonly checksum_file_name="${file_name}.sha256"
sha256sum "$file_name" > "$checksum_file_name"
readonly checksum_file_path="$file_dir/$checksum_file_name"
cd "$base_dir"

# Create GitHub release
readonly changes_file="$base_dir/doc/changes/changes_${project_version}.md"
readonly title="Release $project_version"
readonly tag="$project_version"
echo "Creating release:"
echo "Git tag      : $tag"
echo "Title        : $title"
echo "Changes file : $changes_file"
echo "Artifact file: $artifact_path"
echo "Checksum file: $checksum_file_path"

release_url=$(gh release create --latest --title "$title" --notes-file "$changes_file" --target main "$tag" "$artifact_path" "$checksum_file_path")
readonly release_url
echo "Release URL: $release_url"
