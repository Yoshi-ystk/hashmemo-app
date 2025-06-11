package app;

import cli.MemoCli;
import memo.MemoManager;

public class MemoApp {
    public static void main(String[] args) {
        MemoManager manager = new MemoManager();
        MemoCli cli = new MemoCli(manager);
        cli.run();  // CLIインターフェースの起動
    }
}