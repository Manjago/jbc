# Список команд

## Сгенерировать классы для разных jdk

Предварительно выполнить - устаналиваем переменные окружения `JDK8_HOME`, `JDK11_HOME` ...

```bash
source jdks.env
```

```bash
./multi-javac.sh samples/Hello.java
```

## ASMifier и Textifier из командной строки

Предварительно получить список зависимостей для classpath

```bash
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
```

Последующий запуск ASMifier

```bash
java -cp "$(cat cp.txt)" org.objectweb.asm.util.ASMifier out/jdk25/Hello/Hello.class
```

Последующий запуск Textifier

```bash
java -cp "$(cat cp.txt)" org.objectweb.asm.util.Textifier out/jdk25/Hello/Hello.class
```