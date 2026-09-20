import java.nio.file.Path;

public final class Lab {

    private Lab() {
        /* This utility class should not be instantiated */
    }

    /// Прогоняет байты через `CheckClassAdapter.verify(...)` и выводит результат в stderr
    ///
    /// @param rawArray массив байт
    public static void verify(byte[] rawArray) {

    }

    /// Пишет `.class` на диск, чтобы на него можно было натравить настоящий `javap`
    public static void dump(byte[] rawArray, Path path) {
    }

    /// Возвращает строку через `TraceClassVisitor` + `Textifier`
    public static String textify(byte[] rawArray) {
        return null;
    }

    /// Возвращает `Class<?>` через свой одноразовый `ClassLoader` с публичным `defineClass`.
    /// Каждый вызов создаёт новый загрузчик
    public static Class<?> define(byte[] rawArray) {
        return null;
    }

    /// Делает verify, потом define, потом ищет `main` и вызывает его
    public static void run(byte[] rawArray, String... args) {
    }

}
