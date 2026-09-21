class Smoke {
    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws Exception {
        final byte[] bytes = (byte[]) Class.forName(args[0])
                .getDeclaredMethod("dump")
                .invoke(null);
        System.out.println(Lab.run(bytes) ? "Smoke-test пройден" : "Smoke-test провален");
    }
}