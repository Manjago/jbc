#!/usr/bin/env bash
# usage: ./multi-javac.sh samples/Foo.java
# пути к JDK — из переменных окружения JDK8_HOME, JDK11_HOME, ...
set -u
src="$1"; name="$(basename "$src" .java)"
JAVAP="$JDK25_HOME/bin/javap"          # один javap на всех, см. ниже

for v in 8 11 17 21 25; do
  var="JDK${v}_HOME"; home="${!var}"
  out="out/jdk$v/$name"
  rm -rf "$out"; mkdir -p "$out"
  if "$home/bin/javac" -g -d "$out" "$src" 2> "$out/javac.err"; then
    for c in "$out"/*.class; do
      "$JAVAP" -v -p -c "$c" | tail -n +3 > "${c%.class}.javap.txt"
    done
    echo "jdk$v: ok"
  else
    echo "jdk$v: не скомпилировалось -> $out/javac.err"
  fi
done