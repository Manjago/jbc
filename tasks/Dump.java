class Dump {
    public static void main(String[] args) throws Exception {
        final byte[] bytes = (byte[]) Class.forName(args[0])
                .getDeclaredMethod("dump")
                .invoke(null);
        Lab.dump(bytes, java.nio.file.Path.of(args[1]));
        System.out.println("Сдамплено в " + args[1]);
    }
}