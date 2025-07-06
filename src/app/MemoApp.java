package app;

import cli.MemoCli;
import memo.MemoManager;
import storage.DataAccessException;

public class MemoApp {
    public static void main(String[] args) {
        try {
            MemoManager manager = new MemoManager();
            MemoCli cli = new MemoCli(manager);
            System.out.println("HashMemo CLI アプリケーションを起動します...");
            cli.run();
        } catch (DataAccessException e) {
            System.err.println("データベースエラーが発生しました。アプリケーションを終了します。");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("予期せぬエラーが発生しました。アプリケーションを終了します: " + e.getMessage());
            e.printStackTrace();
        }
    }
}