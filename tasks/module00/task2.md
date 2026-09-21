# Задание 2: Предсказать и сравнить

*Сделай `Lab.dump` класса, который сгенерировал ASM, и прогони его через `javap -v -p -c`. Сравни с javac-овским от JDK 25. Сначала запиши предсказание: что совпадёт и что нет.*

Мое предсказание - разница будет только в ConstantPool, причем только в порядке. Обоснование - код простой, не вижу, в чем будет разница, а порядок в ConstantPool совершенно не важен.

```bash
set -e
cd ..
cd ..
source jdks.env
./multi-javac.sh samples/Hello.java
# получили javac-версию Hello.class
mvn -q compile dependency:build-classpath -Dmdep.outputFile=cp.txt
# запаслись зависимостями
java -cp "$(cat cp.txt)" org.objectweb.asm.util.ASMifier out/jdk25/Hello/Hello.class > tasks/HelloDump.java
# получили исходник от ASMifier
javac -cp "target/classes:$(cat cp.txt)" -d target/classes tasks/HelloDump.java
# скомпилировали его
java -cp "target/classes:$(cat cp.txt)" tasks/Dump.java HelloDump out/asm/Hello.class
# сдампили байты от ASM на диск через Lab.dump
javap -v -p -c out/jdk25/Hello/Hello.class > out/javap-javac.txt
javap -v -p -c out/asm/Hello.class > out/javap-asm.txt
# разложили оба дизассема по файлам
diff -u out/javap-javac.txt out/javap-asm.txt || true
# сравнили
rm -f tasks/HelloDump.java
# почистили за собой
```

Кусок вывода

```
kdtemnen@0000NBB0203LZC9:~/IdeaProjects/jbc$ diff -u out/javap-javac.txt out/javap-asm.txt || true
--- out/javap-javac.txt 2026-09-21 17:47:54.256617436 +0300
+++ out/javap-asm.txt   2026-09-21 17:47:54.520618309 +0300
@@ -1,48 +1,48 @@
-Classfile /home/kdtemnen/IdeaProjects/jbc/out/jdk25/Hello/Hello.class
+Classfile /home/kdtemnen/IdeaProjects/jbc/out/asm/Hello.class
   Last modified 21 сент. 2026 г.; size 519 bytes
-  SHA-256 checksum d9d83993e12018a91dbf51cfdb975bb5f4384affcf0d73e9fbeb8d118abd576b
+  SHA-256 checksum becb9430e47a1751930434380d4c4c9458046a8e67fd2b865e09ee92e65e6e57
   Compiled from "Hello.java"
 public class Hello
   minor version: 0
   major version: 69
   flags: (0x0021) ACC_PUBLIC, ACC_SUPER
-  this_class: #21                         // Hello
-  super_class: #2                         // java/lang/Object
+  this_class: #2                          // Hello
+  super_class: #4                         // java/lang/Object
   interfaces: 0, fields: 0, methods: 2, attributes: 1
 Constant pool:
-   #1 = Methodref          #2.#3          // java/lang/Object."<init>":()V
-   #2 = Class              #4             // java/lang/Object
-   #3 = NameAndType        #5:#6          // "<init>":()V
-   #4 = Utf8               java/lang/Object
-   #5 = Utf8               <init>
-   #6 = Utf8               ()V
-   #7 = Fieldref           #8.#9          // java/lang/System.out:Ljava/io/PrintStream;
-   #8 = Class              #10            // java/lang/System
-   #9 = NameAndType        #11:#12        // out:Ljava/io/PrintStream;
-  #10 = Utf8               java/lang/System
-  #11 = Utf8               out
-  #12 = Utf8               Ljava/io/PrintStream;
-  #13 = String             #14            // Hello World!
-  #14 = Utf8               Hello World!
-  #15 = Methodref          #16.#17        // java/io/PrintStream.println:(Ljava/lang/String;)V
-  #16 = Class              #18            // java/io/PrintStream
-  #17 = NameAndType        #19:#20        // println:(Ljava/lang/String;)V
-  #18 = Utf8               java/io/PrintStream
-  #19 = Utf8               println
-  #20 = Utf8               (Ljava/lang/String;)V
-  #21 = Class              #22            // Hello
-  #22 = Utf8               Hello
-  #23 = Utf8               Code
-  #24 = Utf8               LineNumberTable
-  #25 = Utf8               LocalVariableTable
-  #26 = Utf8               this
-  #27 = Utf8               LHello;
-  #28 = Utf8               main
-  #29 = Utf8               ([Ljava/lang/String;)V
-  #30 = Utf8               args
-  #31 = Utf8               [Ljava/lang/String;
-  #32 = Utf8               SourceFile
-  #33 = Utf8               Hello.java
+   #1 = Utf8               Hello
+   #2 = Class              #1             // Hello
+   #3 = Utf8               java/lang/Object
+   #4 = Class              #3             // java/lang/Object
+   #5 = Utf8               Hello.java
+   #6 = Utf8               <init>
+   #7 = Utf8               ()V
+   #8 = NameAndType        #6:#7          // "<init>":()V
+   #9 = Methodref          #4.#8          // java/lang/Object."<init>":()V
+  #10 = Utf8               this
+  #11 = Utf8               LHello;
+  #12 = Utf8               main
+  #13 = Utf8               ([Ljava/lang/String;)V
+  #14 = Utf8               java/lang/System
+  #15 = Class              #14            // java/lang/System
+  #16 = Utf8               out
+  #17 = Utf8               Ljava/io/PrintStream;
+  #18 = NameAndType        #16:#17        // out:Ljava/io/PrintStream;
+  #19 = Fieldref           #15.#18        // java/lang/System.out:Ljava/io/PrintStream;
+  #20 = Utf8               Hello World!
+  #21 = String             #20            // Hello World!
+  #22 = Utf8               java/io/PrintStream
+  #23 = Class              #22            // java/io/PrintStream
+  #24 = Utf8               println
+  #25 = Utf8               (Ljava/lang/String;)V
+  #26 = NameAndType        #24:#25        // println:(Ljava/lang/String;)V
+  #27 = Methodref          #23.#26        // java/io/PrintStream.println:(Ljava/lang/String;)V
+  #28 = Utf8               args
+  #29 = Utf8               [Ljava/lang/String;
+  #30 = Utf8               Code
+  #31 = Utf8               LineNumberTable
+  #32 = Utf8               LocalVariableTable
+  #33 = Utf8               SourceFile
 {
   public Hello();
     descriptor: ()V
@@ -50,7 +50,7 @@
     Code:
       stack=1, locals=1, args_size=1
          0: aload_0
-         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
+         1: invokespecial #9                  // Method java/lang/Object."<init>":()V
          4: return
       LineNumberTable:
         line 1: 0
@@ -63,9 +63,9 @@
     flags: (0x0009) ACC_PUBLIC, ACC_STATIC
     Code:
       stack=2, locals=1, args_size=1
-         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
-         3: ldc           #13                 // String Hello World!
-         5: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
+         0: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
+         3: ldc           #21                 // String Hello World!
+         5: invokevirtual #27                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
          8: return
       LineNumberTable:
         line 3: 0

```

В принципе да - ConstantPool, ну если придираться - то еще и контрольная сумма разная (опять же из-за ConstantPool) и номера ссылок на пул другие (а текст тот же самый, то есть код не изменился). Размер - идентиичен!
ASM в порядке visit, видимо, расставляет.