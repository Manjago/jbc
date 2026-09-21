## Задание 1: smoke-тест,  генератор из ASMifier → `Lab.run` → «Hello World!».

Работаем с [исходником](../../samples/Hello.java)

```bash
set -e
cd ..
cd ..
source jdks.env
./multi-javac.sh samples/Hello.java
# получили Hello.class для ASMifier
mvn -q compile dependency:build-classpath -Dmdep.outputFile=cp.txt
# запаслись зависимостями для classpath
java -cp "$(cat cp.txt)" org.objectweb.asm.util.ASMifier out/jdk25/Hello/Hello.class > tasks/HelloDump.java
# получили исходник от ASMifier
javac -cp "target/classes:$(cat cp.txt)" -d target/classes tasks/HelloDump.java
# скомпилировали исходник от ASMifier
java -cp "target/classes:$(cat cp.txt)" tasks/Smoke.java HelloDump
# через helper tasks/Smoke.java дернули dump и запустили класс прямо в памяти
rm -f tasks/HelloDump.java
# почистили за собой
```
В результате получили 

```
Hello World!
Smoke-test пройден
```
