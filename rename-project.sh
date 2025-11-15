#!/usr/bin/env bash

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to convert string to PascalCase
to_pascal_case() {
    echo "$1" | sed -r 's/(^|[_-])([a-z])/\U\2/g'
}

# Function to convert string to camelCase
to_camel_case() {
    local pascal=$(to_pascal_case "$1")
    local first_lower=$(echo "${pascal:0:1}" | tr '[:upper:]' '[:lower:]')
    echo "${first_lower}${pascal:1}"
}

# Function to convert string to snake_case
to_snake_case() {
    echo "$1" | sed -r 's/([A-Z])/_\L\1/g' | sed 's/^_//' | tr '[:upper:]' '[:lower:]' | tr '-' '_'
}

# Function to convert string to kebab-case
to_kebab_case() {
    echo "$1" | sed -r 's/([A-Z])/-\L\1/g' | sed 's/^-//' | tr '[:upper:]' '[:lower:]' | tr '_' '-'
}

echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   KMP Template Project Rename Script      ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
echo

# Get user input
read -p "$(echo -e ${YELLOW}Enter new project name ${NC}[e.g., MyAwesomeApp]: )" NEW_PROJECT_NAME
if [ -z "$NEW_PROJECT_NAME" ]; then
    echo -e "${RED}Error: Project name cannot be empty${NC}"
    exit 1
fi

read -p "$(echo -e ${YELLOW}Enter app display name ${NC}[e.g., My Awesome App] [Press Enter to use: $NEW_PROJECT_NAME]: )" APP_DISPLAY_NAME
APP_DISPLAY_NAME=${APP_DISPLAY_NAME:-$NEW_PROJECT_NAME}

read -p "$(echo -e ${YELLOW}Enter package domain ${NC}[e.g., com.example]: )" PACKAGE_DOMAIN
if [ -z "$PACKAGE_DOMAIN" ]; then
    echo -e "${RED}Error: Package domain cannot be empty${NC}"
    exit 1
fi

# Generate all case variants for NEW names
NEW_PASCAL=$(to_pascal_case "$NEW_PROJECT_NAME")
NEW_CAMEL=$(to_camel_case "$NEW_PROJECT_NAME")
NEW_SNAKE=$(to_snake_case "$NEW_PROJECT_NAME")
NEW_KEBAB=$(to_kebab_case "$NEW_PROJECT_NAME")

# Old variants (current template name)
OLD_PASCAL="KmpTemplate"
OLD_CAMEL="kmpTemplate"
OLD_SNAKE="kmp_template"
OLD_KEBAB="kmp-template"
OLD_PACKAGE_DOMAIN="com.example"

# Display summary
echo
echo -e "${GREEN}═══════════════════════════════════════════${NC}"
echo -e "${GREEN}Renaming Summary:${NC}"
echo -e "${GREEN}═══════════════════════════════════════════${NC}"
echo -e "PascalCase:     ${BLUE}$OLD_PASCAL${NC} → ${GREEN}$NEW_PASCAL${NC}"
echo -e "camelCase:      ${BLUE}$OLD_CAMEL${NC} → ${GREEN}$NEW_CAMEL${NC}"
echo -e "snake_case:     ${BLUE}$OLD_SNAKE${NC} → ${GREEN}$NEW_SNAKE${NC}"
echo -e "kebab-case:     ${BLUE}$OLD_KEBAB${NC} → ${GREEN}$NEW_KEBAB${NC}"
echo -e "Package domain: ${BLUE}$OLD_PACKAGE_DOMAIN${NC} → ${GREEN}$PACKAGE_DOMAIN${NC}"
echo -e "App name:       ${GREEN}$APP_DISPLAY_NAME${NC}"
echo -e "${GREEN}═══════════════════════════════════════════${NC}"
echo

read -p "$(echo -e ${YELLOW}Proceed with renaming? ${NC}[y/N]: )" CONFIRM
if [[ ! "$CONFIRM" =~ ^[Yy]$ ]]; then
    echo -e "${RED}Aborted.${NC}"
    exit 0
fi

echo
echo -e "${BLUE}[1/4] Replacing text in files...${NC}"

# Find all files (excluding .git, build, .gradle, .idea, and binary files)
FILES=$(find . -type f \
    -not -path "*/.git/*" \
    -not -path "*/build/*" \
    -not -path "*/gradle/*" \
    -not -path "*/.gradle/*" \
    -not -path "*/.idea/*" \
    -not -path "*/rename-project.sh")

COUNT=0
for file in $FILES; do
    # Check if file is a text file
    if file "$file" | grep -q text; then
        # Replace all variants - order matters! Replace longer strings first
        sed -i.bak \
            -e "s/$OLD_PACKAGE_DOMAIN\.$OLD_SNAKE/$PACKAGE_DOMAIN.$NEW_SNAKE/g" \
            -e "s/$OLD_PACKAGE_DOMAIN/$PACKAGE_DOMAIN/g" \
            -e "s/$OLD_PASCAL/$NEW_PASCAL/g" \
            -e "s/$OLD_CAMEL/$NEW_CAMEL/g" \
            -e "s/$OLD_SNAKE/$NEW_SNAKE/g" \
            -e "s/$OLD_KEBAB/$NEW_KEBAB/g" \
            "$file"

        # Remove backup files
        rm -f "${file}.bak"
        COUNT=$((COUNT + 1))
    fi
done

echo -e "${GREEN}✓ Processed $COUNT files${NC}"

echo
echo -e "${BLUE}[2/4] Renaming directories...${NC}"

# Convert old package path to new package path
OLD_PACKAGE_PATH=$(echo "$OLD_PACKAGE_DOMAIN.$OLD_SNAKE" | tr '.' '/')
NEW_PACKAGE_PATH=$(echo "$PACKAGE_DOMAIN.$NEW_SNAKE" | tr '.' '/')

# Track renamed directories to avoid duplicates
declare -A RENAMED_DIRS

# Find and rename directories with old package structure (SHALLOWEST FIRST)
# This ensures we move parent directories before children
while IFS= read -r dir; do
    new_dir=$(echo "$dir" | sed "s|$OLD_PACKAGE_PATH|$NEW_PACKAGE_PATH|g")
    if [ "$dir" != "$new_dir" ] && [ -d "$dir" ] && [ ! -e "$new_dir" ] && [ -z "${RENAMED_DIRS[$dir]}" ]; then
        # Create parent directory only if it doesn't exist
        parent_dir=$(dirname "$new_dir")
        if [ ! -d "$parent_dir" ]; then
            mkdir -p "$parent_dir"
        fi
        mv "$dir" "$new_dir"
        RENAMED_DIRS[$dir]=1
        echo -e "  ${GREEN}✓${NC} $dir → $new_dir"
    fi
done < <(find . -type d -path "*/$OLD_PACKAGE_PATH*" -not -path "*/build/*" -not -path "*/.gradle/*" -not -path "*/.git/*" 2>/dev/null | sort)

# Rename other directories with old snake_case name (not in package path)
while IFS= read -r dir; do
    # Skip if already renamed or contains the old package path (already handled above)
    if [[ "$dir" != *"$OLD_PACKAGE_PATH"* ]] && [ -z "${RENAMED_DIRS[$dir]}" ]; then
        # Replace kmp_template in directory names (handles both path components and dot-separated names)
        new_dir=$(echo "$dir" | sed "s|$OLD_SNAKE|$NEW_SNAKE|g")
        if [ "$dir" != "$new_dir" ] && [ -d "$dir" ] && [ ! -e "$new_dir" ]; then
            parent_dir=$(dirname "$new_dir")
            if [ ! -d "$parent_dir" ]; then
                mkdir -p "$parent_dir"
            fi
            mv "$dir" "$new_dir"
            RENAMED_DIRS[$dir]=1
            echo -e "  ${GREEN}✓${NC} $dir → $new_dir"
        fi
    fi
done < <(find . -type d -name "*$OLD_SNAKE*" -not -path "*/build/*" -not -path "*/.gradle/*" -not -path "*/.git/*" 2>/dev/null | sort)

# Clean up all empty directories (handles old package paths, etc.)
echo -e "\n${BLUE}Cleaning up empty directories...${NC}"
find . -mindepth 1 -type d -not -path "*/.git/*" -print0 | sort -z -r | xargs -0 rmdir --ignore-fail-on-non-empty
echo -e "  ${GREEN}✓${NC} Cleaned up empty directories"

echo
echo -e "${BLUE}[3/4] Renaming files...${NC}"

# Find and rename files
while IFS= read -r file; do
    if [ -f "$file" ]; then
        dir=$(dirname "$file")
        filename=$(basename "$file")
        new_filename=$(echo "$filename" | sed -e "s/$OLD_PASCAL/$NEW_PASCAL/g" -e "s/$OLD_SNAKE/$NEW_SNAKE/g" -e "s/$OLD_KEBAB/$NEW_KEBAB/g")

        if [ "$filename" != "$new_filename" ] && [ ! -e "$dir/$new_filename" ]; then
            mv "$file" "$dir/$new_filename"
            echo -e "  ${GREEN}✓${NC} $filename → $new_filename"
        fi
    fi
done < <(find . -type f \( -name "*$OLD_PASCAL*" -o -name "*$OLD_SNAKE*" -o -name "*$OLD_KEBAB*" \) -not -path "*/build/*" -not -path "*/.gradle/*" -not -path "*/.git/*" -not -name "rename-project.sh" | sort -r)

echo
echo -e "${BLUE}[4/4] Cleaning up build artifacts...${NC}"

# Clean build directories to avoid stale references
if [ -d "build" ]; then
    rm -rf build
    echo -e "  ${GREEN}✓${NC} Removed build directory"
fi

find . -type d -name "build" -not -path "*/.gradle/*" -exec rm -rf {} + 2>/dev/null || true
find . -type d -name ".gradle" -exec rm -rf {} + 2>/dev/null || true

echo -e "  ${GREEN}✓${NC} Cleaned build artifacts"

echo
echo -e "${GREEN}╔════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║         Renaming Complete! 🎉              ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════╝${NC}"
echo
echo -e "${YELLOW}Next steps:${NC}"
echo -e "  1. Review the changes with: ${BLUE}git diff${NC}"
echo -e "  2. Build the project to verify: ${BLUE}./gradlew build${NC}"
echo -e "  3. Update your iOS team ID in: ${BLUE}iosApp/Configuration/Config.xcconfig${NC}"
echo -e "  4. Commit the changes: ${BLUE}git add . && git commit -m 'Rename project to $NEW_PASCAL'${NC}"
echo

# Ask if user wants to delete this script
read -p "$(echo -e ${YELLOW}Delete this rename script? ${NC}[y/N]: )" DELETE_SCRIPT
if [[ "$DELETE_SCRIPT" =~ ^[Yy]$ ]]; then
    SCRIPT_PATH="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/$(basename "${BASH_SOURCE[0]}")"
    rm -f "$SCRIPT_PATH"
    echo -e "${GREEN}✓ Deleted rename-project.sh${NC}"
    echo -e "${BLUE}Note: Don't forget to remove it from git: ${NC}git rm rename-project.sh"
else
    echo -e "${BLUE}Script kept. You can delete it manually later if needed.${NC}"
fi
echo
