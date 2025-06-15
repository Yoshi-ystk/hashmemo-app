package cli;

import memo.Memo;
import memo.MemoManager;

import java.util.List;
import java.util.Scanner;

/**
 * CLI（コマンドラインインターフェース）を通じてメモ操作を行うクラス。
 * MemoManager と連携して、メモの追加・表示・削除・検索などを実行します。
 */
public class MemoCli {
    private final MemoManager manager;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * MemoCli のコンストラクタ
     * @param manager メモの追加・取得・削除・検索を管理する MemoManager
     */
    public MemoCli(MemoManager manager) {
        this.manager = manager;
    }

    /**
     * CLI メニューを表示してユーザー入力を受け付けるループ処理
     */
    public void run() {
        int choice;
        do {
            // メニューの表示
            System.out.println("\n=== メモアプリ ===");
            System.out.println("1. メモを追加");
            System.out.println("2. メモを表示");
            System.out.println("3. メモを削除");
            System.out.println("4. メモを検索");
            System.out.println("5. 終了");
            System.out.print("選択してください: ");

            // 入力のバリデーション
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                choice = 0;
            }

            // 選択肢に応じて処理を振り分ける
            switch (choice) {
                case 1 -> addMemo();       // メモの追加
                case 2 -> displayMemos();  // メモ一覧の表示
                case 3 -> deleteMemo();    // メモの削除
                case 4 -> searchMemo();    // メモの検索
                case 5 -> System.out.println("アプリを終了します。");
                default -> System.out.println("無効な選択です。もう一度お試しください。");
            }
        } while (choice != 5);
    }

    /**
     * メモを追加する処理（タイトルと本文をユーザーから入力）
     */
    private void addMemo() {
        System.out.print("タイトルを入力してください: ");
        String title = scanner.nextLine();

        System.out.println("メモ本文を入力してください（`:end` で終了）:");
        StringBuilder builder = new StringBuilder();
        String line;
        // 複数行の本文入力を受け付ける
        while (!(line = scanner.nextLine()).equals(":end")) {
            builder.append(line).append(System.lineSeparator());
        }

        String content = builder.toString().trim();
        manager.add(title, content); // MemoManager に渡して追加
        System.out.println("メモを追加しました！");
    }

    /**
     * 保存されているすべてのメモのタイトルを一覧表示
     */
    private void displayMemos() {
        List<Memo> memos = manager.getAll();
        if (memos.isEmpty()) {
            System.out.println("メモはありません。");
        } else {
            System.out.println("\n=== メモ一覧 ===");
            for (int i = 0; i < memos.size(); i++) {
                Memo memo = memos.get(i);
                System.out.println((i + 1) + ". " + memo.getTitle());
            }
        }
    }

    /**
     * メモを一覧表示してから、削除したいメモの番号を指定
     */
    private void deleteMemo() {
        displayMemos();
        if (!manager.getAll().isEmpty()) {
            System.out.print("削除するメモの番号を入力してください: ");
            try {
                int index = Integer.parseInt(scanner.nextLine());
                boolean success = manager.delete(index - 1);
                System.out.println(success ? "メモを削除しました！" : "無効な番号です。");
            } catch (Exception e) {
                System.out.println("無効な入力です。");
            }
        }
    }

    /**
     * 指定されたキーワードに一致するタイトルまたは本文を持つメモを検索
     */
    private void searchMemo() {
        System.out.print("検索キーワード: ");
        String keyword = scanner.nextLine();
        List<Memo> results = manager.search(keyword);
        if (results.isEmpty()) {
            System.out.println("キーワードに一致するメモはありません。");
        } else {
            System.out.println("=== 検索結果 ===");
            for (Memo memo : results) {
                System.out.println("- " + memo.getTitle() + "\n" + memo.getContent());
            }
        }
    }
}