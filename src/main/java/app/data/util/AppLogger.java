package app.data.util;

public class AppLogger {
    // 製品版（リリース時）はここを false にするだけで、全出力が完全に消えます
    private static final boolean IS_DEBUG = true;

    /**
     * 標準出力（System.out.println）の代わりにこれを使用します。
     */
    public static void print(String message) {
        if (IS_DEBUG) {
            System.out.println(message);
        }
    }

    /**
     * 改行なしの出力（System.out.print）の代わりです。
     */
    public static void printSameLine(String message) {
        if (IS_DEBUG) {
            System.out.print(message);
        }
    }

    /**
     * エラーのスタックトレース出力用です。
     */
    public static void error(Throwable throwable) {
        if (IS_DEBUG) {
            throwable.printStackTrace();
        }
    }
}