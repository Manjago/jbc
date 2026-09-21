import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Lab {

    private Lab() {
        /* This utility class should not be instantiated */
    }

    /// Прогоняет байты через `CheckClassAdapter.verify(...)` и выводит результат в stderr
    /// printResult: false - осознанный выбор
    /// хотя всё равно выполняет анализ потока данных, просто при ошибке печатает только
    /// сообщение об ошибке, без дампа всего класса
    ///
    /// @param bytes массив байт
    /// @return true, если ошибок нет
    @SuppressWarnings("java:S106")
    public static boolean verify(byte[] bytes) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter, true);

        CheckClassAdapter.verify(new ClassReader(bytes), false, printWriter);

        // verify исключения не бросит, только ошибки - ну давайте это обработаем
        final String output = stringWriter.toString();
        if (!output.isEmpty()) {
            System.err.print(output);
            return false;
        }
        return true;
    }

    /// Пишет `.class` на диск, чтобы на него можно было натравить настоящий `javap`
    ///
    /// @param bytes массив байт
    /// @param path  путь к файлу
    public static void dump(byte[] bytes, Path path) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, bytes); // CREATE, TRUNCATE_EXISTING, WRITE - то что нужно
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось записать класс в " + path, e);
        }
    }

    /// Возвращает строку через `TraceClassVisitor` + `Textifier`
    public static String textify(byte[] bytes) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        new ClassReader(bytes).accept(new TraceClassVisitor(null, new Textifier(), printWriter), 0);

        printWriter.flush(); // для StringWriter некритичен, но так правильно
        return stringWriter.toString();
    }

    /// Возвращает `Class<?>` через свой одноразовый `ClassLoader` с публичным `defineClass`.
    /// Каждый вызов создаёт новый загрузчик
    ///
    /// @param bytes массив байт
    public static Class<?> define(byte[] bytes) {
        return new OneShotLoader().define(bytes);
    }

    /// Делает verify, потом define, потом ищет `main` и вызывает его
    ///
    /// @param rawArray массив байт
    /// @param args     аргументы, передаваемые в `main`
    /// @return true, если verify прошёл и `main` отработал без исключений
    public static boolean run(byte[] rawArray, String... args) {
        if (!verify(rawArray)) {
            return false;
        }

        final Class<?> clazz = define(rawArray);
        try {
            final Method main = clazz.getDeclaredMethod("main", String[].class);
            // cast к Object заставляет передать массив аргументов как отдельный аргумент
            // потому что `invoke(Object obj, Object... args)` - это varargs
            main.invoke(null, (Object) args);
            return true;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("В классе " + clazz.getName() + " нет main(String[])", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Нет доступа к main в " + clazz.getName(), e);
        } catch (InvocationTargetException e) {
            // исключение приходит завернутым
            final Throwable cause = e.getCause();
            throw new IllegalStateException(
                    "main выбросил исключение: " + cause, cause);
        }
    }

    private static final class OneShotLoader extends ClassLoader {
        OneShotLoader() {
            super(Lab.class.getClassLoader());
        }

        // define(null...) - имя класса берется из байт0кода
        Class<?> define(byte[] bytes) {
            return defineClass(null, bytes, 0, bytes.length);
        }
    }

}
