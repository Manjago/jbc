import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Files;
import java.nio.file.Path;

class Roundtrip {
    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws Exception {
        final byte[] in = Files.readAllBytes(Path.of(args[0]));

        final ClassReader reader = new ClassReader(in);
        final ClassWriter writer = new ClassWriter(0); // ничего не пересчитываем
        reader.accept(writer, 0);                      // пустой конвейер, без фильтров

        final byte[] out = writer.toByteArray();
        Lab.dump(out, Path.of(args[1]));               // каталоги создаст Lab
        System.out.println("Roundtrip: " + in.length + " → " + out.length + " байт");
    }
}