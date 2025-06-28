package cli;

import memo.Memo;
import memo.MemoManager;

import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * CLI（コマンドラインインターフェース）を通じてメモ操作を行うクラス。
 * MemoManager と連携して、メモの追加・表示・削除・検索などを実行します。
 */
public class MemoCli {
    private final MemoManager manager;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * MemoCli のコンストラクタ
     * 
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
            System.out.println("5. タグで検索");
            System.out.println("6. 終了");
            System.out.print("選択してください: ");

            // 入力のバリデーション
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                choice = 0;
            }

            // 選択肢に応じて処理を振り分ける
            switch (choice) {
                case 1 -> addMemo(); // メモの追加
                case 2 -> ViewDetails(manager.getAll());   //ViewDetails();  メモ一覧の表示
                case 3 -> deleteMemo(); // メモの削除
                case 4 -> searchMemo(); // メモの検索
                case 5 -> searchByTag(); // タグで検索
                case 6 -> System.out.println("アプリを終了します。");
                default -> System.out.println("無効な選択です。もう一度お試しください。");
            }
        } while (choice != 6);
    }

    /**
     * メモを追加する処理（タイトルと本文をユーザーから入力）
     */
    private void addMemo() {
        System.out.print("タイトルを入力してください: ");
        String title = scanner.nextLine();

        System.out.println("本文を入力してください（`:end`で終了）:");
        StringBuilder bodyBuilder = new StringBuilder();
        String line;
        // ユーザーが `:end` と入力するまで本文を読み込む
        while (!(line = scanner.nextLine()).equals(":end")) {
            bodyBuilder.append(line).append(System.lineSeparator());
        }
        String body = bodyBuilder.toString().trim();

        System.out.print("タグをカンマ区切りで入力してください（例: 仕事,勉強）: ");
        String tagInput = scanner.nextLine();
        List<String> tags = Arrays.stream(tagInput.split(","))
        .map(String::trim)
        .map(tag -> tag.replaceFirst("^#", ""))  // ← # を除去
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());

        // Memo インスタンスを作成し追加
        Memo memo = new Memo(title, body, tags);
        manager.add(memo);

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
                System.out.println((i + 1) + ". " + memo.getTitle()+ " [" + String.join(", ", memo.getTags()) + "]");
            }
        }
    }

    private void displayMemos(List<Memo> memos) {
    if (memos.isEmpty()) {
        System.out.println("メモはありません。");
    } else {
        System.out.println("\n=== メモ一覧 ===");
        for (int i = 0; i < memos.size(); i++) {
            Memo memo = memos.get(i);
            System.out.println((i + 1) + ". " + memos.get(i).getTitle()+ " [" + String.join(", ", memo.getTags()) + "]");
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
            System.out.println("一致するメモはありません。");
        } else {
            ViewDetails(results);  // 検索結果をそのまま渡す
        }
    }

    /**
     * タグによる検索処理
     */
    private void searchByTag() {
        System.out.print("検索するタグを入力してください: ");
        String tag = scanner.nextLine().trim();
        List<Memo> results = manager.searchByTag(tag);
        if (results.isEmpty()) {
            System.out.println("指定されたタグに一致するメモはありません。");
        } else {
            System.out.println("=== タグ検索結果 ===");
            for (Memo memo : results) {
                System.out.println("- " + memo.getTitle() + " [" + String.join(", ", memo.getTags()) + "]");
            }
            ViewDetails(results);
        }
    }

    /**
     * メモ、タグ検索後の結果から、詳細表示するものを選択
     */
    private void ViewDetails(List<Memo> memos) {
        displayMemos(memos);  // 引数ありに変更して汎用化
        System.out.print("詳細を見たい番号を入力（Enterでキャンセル）: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("キャンセルしました。");
            return;
        }

        try {
            int index = Integer.parseInt(input) - 1;
            Memo selected = manager.getMemoByIndex(memos, index); // ← ここが責務分離
            if (selected != null) {
                showMemoDetails(selected);  // 詳細表示専用メソッドに切り出し
            } else {
                System.out.println("その番号のメモはありません。");
            }
        } catch (NumberFormatException e) {
            System.out.println("数字を入力してください！");
        }
    }

    /**
     * メモの詳細を表示
     */
    private void showMemoDetails(Memo memo) {
        System.out.println("\n--- メモ詳細 ---");
        System.out.println("[タイトル] " + memo.getTitle());
        System.out.println("[タグ] " + String.join(", ", memo.getTags()));
        System.out.println("[本文] " + memo.getBody());
    }
}