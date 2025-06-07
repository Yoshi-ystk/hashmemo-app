//メモの表示に問題あり
import java.util.ArrayList;
import java.util.Scanner;

public class Memo1 {
    private static ArrayList<String> memos = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== メモアプリ ===");
            System.out.println("1. メモを追加");
            System.out.println("2. メモを表示");
            System.out.println("3. メモを削除");
            System.out.println("4. 終了");
            System.out.print("選択してください: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            switch (choice) {
                case 1:
                    addMemo(scanner);
                    break;
                case 2:
                    displayMemos();
                    break;
                case 3:
                    deleteMemo(scanner);
                    break;
                case 4:
                    System.out.println("アプリを終了します。");
                    break;
                default:
                    System.out.println("無効な選択です。もう一度お試しください。");
            }
        } while (choice != 4);

        scanner.close();
    }

    private static void addMemo(Scanner scanner) {
        System.out.print("メモを入力してください: ");
        String memo = scanner.nextLine();
        memos.add(memo);
        System.out.println("メモを追加しました！");
    }

    private static void displayMemos() {
        if (memos.isEmpty()) {
            System.out.println("メモはありません。");
        } else {
            System.out.println("\n=== メモ一覧 ===");
            for (int i = 0; i < memos.size(); i++) {
                System.out.println((i + 1) + ". " + memos.get(i));
            }
        }
    }

    private static void deleteMemo(Scanner scanner) {
        displayMemos();
        if (!memos.isEmpty()) {
            System.out.print("削除するメモの番号を入力してください: ");
            int index = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            if (index > 0 && index <= memos.size()) {
                memos.remove(index - 1);
                System.out.println("メモを削除しました！");
            } else {
                System.out.println("無効な番号です。");
            }
        }
    }
}
