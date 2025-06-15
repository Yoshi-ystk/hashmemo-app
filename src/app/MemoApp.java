package app;

import cli.MemoCli;
import memo.MemoManager;

/**
 * アプリケーションのエントリーポイント。
 * MemoManagerとMemoCliを連携させてCLIメモアプリを起動する。
 */
public class MemoApp {
    public static void main(String[] args) {
        // メモの保存・検索・削除などを管理するオブジェクト
        MemoManager manager = new MemoManager();

        // CLI操作を担当するオブジェクトにMemoManagerを注入
        MemoCli cli = new MemoCli(manager);

        // CLIインターフェースの起動（ユーザー入力待ち）
        cli.run();
    }
}