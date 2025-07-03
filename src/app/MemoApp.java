package app;

import cli.MemoCli;
import memo.MemoManager;

public class MemoApp {
    public static void main(String[] args) {
        try {
            MemoManager manager = new MemoManager();
            MemoCli cli = new MemoCli(manager);
            System.out.println("HashMemo CLI アプリケーションを起動します...");
            cli.run();
        } catch (Exception e) {
            System.err.println("アプリケーションの起動に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}