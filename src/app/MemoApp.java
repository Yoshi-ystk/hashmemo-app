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
/*
// 以下は接続テスト用です
package app;

import storage.MemoRepository;

public class MemoApp {

    public static void main(String[] args) {
        // MemoRepositoryのインスタンスを作成
        MemoRepository repo = new MemoRepository();

        // 接続テスト
        repo.connect();
    }
}
*/