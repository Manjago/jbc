import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

/// Игровая площадка: прогоняет Hello.class, собранные разными JDK,
/// через verify / textify / define / run
private static final List<String> VERSIONS = List.of("jdk8", "jdk11", "jdk17", "jdk21", "jdk25");

/// Соответствие major version байткода и JDK (для красивого вывода)
private static String jdkFromMajor(int major) {
    return switch (major) {
        case 52 -> "Java 8";
        case 55 -> "Java 11";
        case 61 -> "Java 17";
        case 65 -> "Java 21";
        case 69 -> "Java 25";
        default -> "major " + major + " (JDK " + (major - 44) + ")";
    };
}

void main() throws Exception {
    IO.println("Текущая JVM: " + System.getProperty("java.version") + " (поддерживает байткод до ASM API versions " + Opcodes.ASM9 +")");
    IO.println("=".repeat(80));

    for (String version : VERSIONS) {
        final Path path = Path.of("out", version, "Hello", "Hello.class");
        IO.println("### " + version + "  (" + path + ")");

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (Exception e) {
            IO.println("  файл не найден, пропускаем: " + e.getMessage());
            continue;
        }

        // 1. Читаем версию байткода
        final ClassReader reader = new ClassReader(bytes);
        // Честный способ достать major version:
        final int major = reader.readByte(6) << 8 | reader.readByte(7);
        IO.println("  major=" + major + " (" + jdkFromMajor(major) + ")");

        // 2. verify
        IO.println("  verify: " + (Lab.verify(bytes) ? "OK" : "FAIL (см. stderr)"));

        // 3. textify — первые строки, чтобы не захламлять вывод
        String text = Lab.textify(bytes);
        String firstLines = text.lines().limit(8).reduce((a, b) -> a + "\n    " + b).orElse("");
        IO.println("  textify (первые 8 строк):\n    " + firstLines);

        // 4. define + run
        //    если текущая JVM старше, чем версия байткода — UnsupportedClassVersionError
        try {
            Class<?> clazz = Lab.define(bytes);
            IO.println("  define: OK -> " + clazz);
            boolean ok = Lab.run(bytes, "foo", "bar");
            IO.println("  run: " + (ok ? "OK" : "FAIL"));
        } catch (Throwable t) {
            IO.println("  define/run: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        IO.println("-".repeat(80));
    }

    // 5. dump + настоящий javap для одного из файлов
    Path jdk25 = Path.of("out", "jdk25", "Hello", "Hello.class");
    if (Files.exists(jdk25)) {
        byte[] bytes = Files.readAllBytes(jdk25);
        Path dumped = Path.of("out", "playground", "Hello.class");
        Lab.dump(bytes, dumped);
        IO.println("Сдамплено в " + dumped.toAbsolutePath() + " — можно натравить: javap -v " + dumped.toAbsolutePath());
    }
}