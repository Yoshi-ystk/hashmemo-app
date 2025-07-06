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
        // try-with-resources 構文で Scanner を管理し、自動的にクローズする
        try (Scanner scanner = new Scanner(System.in)) {
            int choice;
            do {
                // メニューの表示
                System.out.println("\n=== メモアプリ ===");
                System.out.println("1. メモを追加");
                System.out.println("2. メモを表示");
                System.out.println("3. メモを削除");
                System.out.println("4. メモを検索");
                System.out.println("5. タグで検索");
                System.out.println("6. タグ一覧");
                System.out.println("7. 終了");
                System.out.print("選択してください: ");

                // 入力のバリデーション
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    choice = 0;
                }

                // 選択肢に応じて処理を振り分ける
                switch (choice) {
                    case 1 -> addMemo(scanner); // メモの追加
                    case 2 -> viewDetails(manager.getAll(), scanner); // メモ一覧の表示
                    case 3 -> deleteMemo(scanner); // メモの削除
                    case 4 -> searchMemo(scanner); // メモの検索
                    case 5 -> searchByTag(scanner); // タグで検索
                    case 6 -> showMemosBySelectedTag(scanner); // タグ一覧 → 選択 → 詳細へ
                    case 7 -> System.out.println("アプリを終了します。");
                    default -> System.out.println("無効な選択です。もう一度お試しください。");
                }
            } while (choice != 7);
        }
    }

    /**
     * メモを追加する処理
     */
    private void addMemo(Scanner scanner) {
        System.out.print("タイトルを入力してください: ");
        String title = scanner.nextLine();

        String body = promptForBody(scanner);
        List<String> tags = promptForTags(scanner);

        // Memo インスタンスを作成し追加
        Memo memo = new Memo(title, body, tags);
        manager.add(memo);

        System.out.println("メモを追加しました！");
    }

    /**
     * メモの一覧を表示する
     */
    private void displayMemos(List<Memo> memos) {
        if (memos.isEmpty()) {
            System.out.println("メモはありません。");
        } else {
            System.out.println("\n=== メモ一覧 ===");
            for (int i = 0; i < memos.size(); i++) {
                Memo memo = memos.get(i);
                String dateLabel;
                String dateValue;
                if (memo.getUpdatedAt() != null && !memo.getUpdatedAt().equals(memo.getCreatedAt())) {
                    dateLabel = "最終更新日";
                    dateValue = memo.getUpdatedAt();
                } else {
                    dateLabel = "作成日";
                    dateValue = memo.getCreatedAt();
                }
                System.out.println((i + 1) + ". " + memo.getTitle()
                        + " [" + String.join(", ", memo.getTags()) + "]"
                        + " - " + dateLabel + ": " + dateValue);
            }
        }
    }

    /**
     * メモを削除する処理
     */
    private void deleteMemo(Scanner scanner) {
        List<Memo> memos = manager.getAll();
        displayMemos(memos);
        if (!memos.isEmpty()) {
            System.out.print("削除するメモの番号を入力してください: ");
            try {
                int index = Integer.parseInt(scanner.nextLine()) - 1;
                if (index >= 0 && index < memos.size()) {
                    Memo memoToDelete = memos.get(index);
                    boolean success = manager.delete(memoToDelete);
                    System.out.println(success ? "メモを削除しました！" : "削除に失敗しました。");
                } else {
                    System.out.println("無効な番号です。");
                }
            } catch (Exception e) {
                System.out.println("無効な入力です。");
            }
        }
    }

    /**
     * キーワードでメモを検索する処理
     */
    private void searchMemo(Scanner scanner) {
        System.out.print("検索キーワード: ");
        String keyword = scanner.nextLine();
        List<Memo> results = manager.search(keyword);
        if (results.isEmpty()) {
            System.out.println("一致するメモはありません。");
        } else {
            viewDetails(results, scanner); // 検索結果をそのまま渡す
        }
    }

    /**
     * タグでメモを検索する処理
     */
    private void searchByTag(Scanner scanner) {
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
            viewDetails(results, scanner);
        }
    }

    /**
     * メモの詳細を表示する処理
     */
    private void viewDetails(List<Memo> memos, Scanner scanner) {
        displayMemos(memos); // メモ一覧を表示
        System.out.print("詳細を見たい番号を入力（Enterでキャンセル）: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("キャンセルしました。");
            return;
        }
        try {
            int index = Integer.parseInt(input) - 1;
            Memo selected = manager.getMemoByIndex(memos, index);
            if (selected != null) {
                showMemoDetails(selected, scanner); // 詳細表示
            } else {
                System.out.println("その番号のメモはありません。");
            }
        } catch (NumberFormatException e) {
            System.out.println("数字を入力してください！");
        }
    }

    /**
     * タグ一覧からメモを表示する処理
     */
    private void showMemosBySelectedTag(Scanner scanner) {
        List<String> tagList = new java.util.ArrayList<>(manager.getAllTags());
        if (tagList.isEmpty()) {
            System.out.println("タグが登録されていません。");
            return;
        }

        System.out.println("\n=== タグ一覧 ===");
        for (int i = 0; i < tagList.size(); i++) {
            System.out.println((i + 1) + ". " + tagList.get(i));
        }

        System.out.print("メモを見たいタグの番号を入力（Enterでキャンセル）: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("キャンセルしました。");
            return;
        }

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < tagList.size()) {
                String selectedTag = tagList.get(index);
                List<Memo> matchedMemos = manager.searchByTag(selectedTag);
                if (matchedMemos.isEmpty()) {
                    System.out.println("このタグに該当するメモはありません。");
                } else {
                    viewDetails(matchedMemos, scanner);
                }
            } else {
                System.out.println("無効な番号です。");
            }
        } catch (NumberFormatException e) {
            System.out.println("数字を入力してください！");
        }
    }

    /**
     * メモの詳細を表示し、編集・削除メニューに誘導
     */
    private void showMemoDetails(Memo memo, Scanner scanner) {
        System.out.println("\n--- メモ詳細 ---");
        System.out.println("[タイトル] " + memo.getTitle());
        System.out.println("[タグ] " + String.join(", ", memo.getTags()));

        String dateLabel = (memo.getUpdatedAt() != null && !memo.getUpdatedAt().equals(memo.getCreatedAt())) ? "最終更新日" : "作成日";
        String dateValue = (memo.getUpdatedAt() != null && !memo.getUpdatedAt().equals(memo.getCreatedAt())) ? memo.getUpdatedAt() : memo.getCreatedAt();
        System.out.println(dateLabel + ": " + dateValue);
        System.out.println("[本文] " + memo.getBody());

        System.out.println("操作を選択してください：");
        System.out.println("1. 閉じる");
        System.out.println("2. メモを編集する");
        System.out.println("3. メモを削除する");
        System.out.print("番号で選択: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> { return; }
                case 2 -> editMemo(memo, scanner);
                case 3 -> {
                    manager.delete(memo);
                    System.out.println("メモを削除しました");
                }
                default -> System.out.println("無効な選択です。");
            }
        } catch (NumberFormatException e) {
            System.out.println("数字を入力してください！");
        }
    }

    /**
     * メモを編集する処理
     */
    public void editMemo(Memo memo, Scanner scanner) {
        System.out.println("\n編集オプション: ");
        System.out.println("[1] タイトルを編集");
        System.out.println("[2] 本文を編集");
        System.out.println("[3] タグを編集");
        System.out.print("選択（Enterで戻る）: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("新しいタイトル: ");
                String newTitle = scanner.nextLine().trim();
                memo.setTitle(newTitle);
                manager.update(memo);
                System.out.println("タイトルを更新しました。");
            }
            case "2" -> {
                String newBody = promptForBody(scanner);
                memo.setBody(newBody);
                manager.update(memo);
                System.out.println("本文を更新しました。");
            }
            case "3" -> {
                List<String> newTags = promptForTags(scanner);
                memo.setTags(newTags);
                manager.update(memo);
                System.out.println("タグを更新しました。");
            }
            case "" -> System.out.println("編集をスキップしました。");
            default -> System.out.println("無効な選択です。");
        }
    }

    /**
     * ユーザーに本文の入力を促すヘルパーメソッド
     * @param scanner 使用するScannerインスタンス
     * @return 入力された本文
     */
    private String promptForBody(Scanner scanner) {
        System.out.println("本文を入力してください（`:end`で終了）:");
        StringBuilder bodyBuilder = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals(":end")) {
            bodyBuilder.append(line).append(System.lineSeparator());
        }
        return bodyBuilder.toString().trim();
    }

    /**
     * ユーザーにタグの入力を促すヘルパーメソッド
     * @param scanner 使用するScannerインスタンス
     * @return 入力されたタグのリスト
     */
    private List<String> promptForTags(Scanner scanner) {
        System.out.print("タグをカンマ区切りで入力してください（例: 仕事,勉強）: ");
        String tagInput = scanner.nextLine();
        return Arrays.stream(tagInput.split(","))
                .map(String::trim)
                .map(tag -> tag.replaceFirst("^#", ""))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}