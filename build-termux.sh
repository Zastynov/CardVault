#!/data/data/com.termux/files/usr/bin/bash
set -e
echo "== CardVault Termux build =="
pkg update -y
pkg install -y openjdk-17 wget unzip
if [ ! -x "$HOME/.local/bin/gradle/bin/gradle" ]; then
  mkdir -p "$HOME/.local/bin"
  cd "$HOME/.local/bin"
  echo "Downloading Gradle 8.10.2..."
  wget -q https://services.gradle.org/distributions/gradle-8.10.2-bin.zip -O gradle.zip
  unzip -q gradle.zip
  rm gradle.zip
fi
export PATH="$HOME/.local/bin/gradle-8.10.2/bin:$PATH"
cd "$(dirname "$0")"
gradle --no-daemon assembleDebug
echo
echo "APK:"
find app/build/outputs/apk/debug -name '*.apk' -maxdepth 1 -print
