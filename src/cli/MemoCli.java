package cli;

import memo.MemoManager;
import java.util.Scanner;

public class MemoCli {
    private final MemoManager manager;
    private final Scanner scanner = new Scanner(System.in);

    public MemoCli(MemoManager manager) {
        this.manager = manager;
    }

    public void run() {
        int choice;
        do {
            System.out.println("\n=== メモアプリ ===");
            System.out.println("1. メモを追加");
            System.out.println("2. メモを表示");
            System.out.println("3. メモを削除");
            System.out.println("4. メモを検索");
            System.out.println("5. 終了");
            System.out.print("選択してください: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                choice = 0;
            }

            switch (choice) {
                case 1 -> addMemo();
                case 2 -> displayMemos();
                case 3 -> deleteMemo();
                case 4 -> searchMemo();
                case 5 -> System.out.println("アプリを終了します。");
                default -> System.out.println("無効な選択です。もう一度お試しください。");
            }
        } while (choice != 5);
    }

    private void addMemo() {
        System.out.println("メモを入力してください（`:end` で終了）:");
        StringBuilder builder = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals(":end")) {
            builder.append(line).append(System.lineSeparator());
        }
        manager.add(builder.toString().trim());
        System.out.println("メモを追加しました！");
    }

    private void displayMemos() {
        var memos = manager.getAll();
        if (memos.isEmpty()) {
            System.out.println("メモはありません。");
        } else {
            System.out.println("\n=== メモ一覧 ===");
            for (int i = 0; i < memos.size(); i++) {
                System.out.println((i + 1) + ". " + memos.get(i));
            }
        }
    }

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

    private void searchMemo() {
        System.out.print("検索キーワード: ");
        String keyword = scanner.nextLine();
        var results = manager.search(keyword);
        if (results.isEmpty()) {
            System.out.println("キーワードに一致するメモはありません。");
        } else {
            results.forEach(m -> System.out.println("見つかったメモ: " + m));
        }
    }
}