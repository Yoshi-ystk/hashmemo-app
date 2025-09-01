// /**
// * MemoCli.java
// *
// * v0.1 - CLI版メモアプリ（DB未実装）
// * -----------------------------------------
// * 本ファイルは、コンソール上でメモを追加・一覧表示・検索・削除できる基本機能を提供するクラスです。
// * DB連携は未実装であり、メモはアプリ実行中のメモリ上のみで管理されます。
// * 今後のバージョンでは、データの永続化（SQLite等）やGUI化の実装を予定しています。
// */
// package cli;

// import memo.Memo;
// import memo.MemoManager;

// import java.util.List;
// import java.util.Scanner;
// import java.util.Arrays;
// import java.util.stream.Collectors;

// /**
// * コマンドラインインターフェース（CLI）を通じてメモを操作するためのクラスです。
// * ユーザーからの入力を受け取り、MemoManagerと連携して、
// * メモの追加、表示、削除、検索などの機能を提供します。
// */
// public class MemoCli {
// // アプリケーションのロジックを管理するMemoManager
// private final MemoManager manager;

// /**
// * MemoCliのコンストラクタです。
// *
// * @param manager メモの管理を行うMemoManagerのインスタンス。
// */
// public MemoCli(MemoManager manager) {
// this.manager = manager;
// }

// /**
// * CLIアプリケーションのメインループを起動します。
// * ユーザーにメニューを表示し、選択に応じた処理を呼び出します。
// * `7`が入力されるまでループを続けます。
// */
// public void run() {
// // try-with-resources構文でScannerを管理し、リソースリークを防ぎます
// try (Scanner scanner = new Scanner(System.in)) {
// int choice;
// do {
// // メインメニューの表示
// System.out.println("\n=== メモアプリ ===");
// System.out.println("1. メモを追加");
// System.out.println("2. メモを表示");
// System.out.println("3. メモを削除");
// System.out.println("4. メモを検索");
// System.out.println("5. タグで検索");
// System.out.println("6. タグ一覧");
// System.out.println("7. 終了");
// System.out.print("選択してください: ");

// // 数字以外の入力に対応するための例外処理
// try {
// choice = Integer.parseInt(scanner.nextLine());
// } catch (NumberFormatException e) {
// choice = 0; // 無効な選択として扱う
// }

// // ユーザーの選択に応じて処理を分岐
// switch (choice) {
// case 1 -> addMemo(scanner); // メモの追加
// case 2 -> viewDetails(manager.getAll(), scanner); // 全メモの表示
// case 3 -> deleteMemo(scanner); // メモの削除
// case 4 -> searchMemo(scanner); // キーワード検索
// case 5 -> searchByTag(scanner); // タグ検索
// case 6 -> showMemosBySelectedTag(scanner); // タグ一覧からの表示
// case 7 -> System.out.println("アプリを終了します。");
// default -> System.out.println("無効な選択です。もう一度お試しください。");
// }
// } while (choice != 7);
// }
// }

// /**
// * ユーザーからの入力（タイトル、本文、タグ）を受け取り、新しいメモを追加します。
// */
// private void addMemo(Scanner scanner) {
// System.out.print("タイトルを入力してください: ");
// String title = scanner.nextLine();

// String body = promptForBody(scanner);
// List<String> tags = promptForTags(scanner);

// // 新しいMemoオブジェクトを作成して、Manager経由で保存
// Memo memo = new Memo(title, body, tags);
// manager.add(memo);

// System.out.println("メモを追加しました！");
// }

// /**
// * メモのリストを番号付きでコンソールに表示します。
// * 更新日時があればそれを、なければ作成日時を表示します。
// */
// private void displayMemos(List<Memo> memos) {
// if (memos.isEmpty()) {
// System.out.println("メモはありません。");
// } else {
// System.out.println("\n=== メモ一覧 ===");
// for (int i = 0; i < memos.size(); i++) {
// Memo memo = memos.get(i);
// String dateLabel;
// String dateValue;
// // 更新日時が存在し、作成日時と異なる場合のみ「最終更新日」として表示
// if (memo.getUpdatedAt() != null &&
// !memo.getUpdatedAt().equals(memo.getCreatedAt())) {
// dateLabel = "最終更新日";
// dateValue = memo.getUpdatedAt();
// } else {
// dateLabel = "作成日";
// dateValue = memo.getCreatedAt();
// }
// System.out.println((i + 1) + ". " + memo.getTitle()
// + " [" + String.join(", ", memo.getTags()) + "]"
// + " - " + dateLabel + ": " + dateValue);
// }
// }
// }

// /**
// * メモの一覧を表示し、ユーザーに削除したいメモの番号を入力させ、削除を実行します。
// */
// private void deleteMemo(Scanner scanner) {
// List<Memo> memos = manager.getAll();
// displayMemos(memos);
// if (!memos.isEmpty()) {
// System.out.print("削除するメモの番号を入力してください: ");
// try {
// int index = Integer.parseInt(scanner.nextLine()) - 1;
// if (index >= 0 && index < memos.size()) {
// Memo memoToDelete = memos.get(index);
// boolean success = manager.delete(memoToDelete);
// System.out.println(success ? "メモを削除しました！" : "削除に失敗しました。");
// } else {
// System.out.println("無効な番号です。");
// }
// } catch (NumberFormatException e) {
// System.out.println("無効な入力です。数字で入力してください。");
// }
// }
// }

// /**
// * ユーザーに検索キーワードを入力させ、タイトルまたは本文に一致するメモを検索・表示します。
// */
// private void searchMemo(Scanner scanner) {
// System.out.print("検索キーワード: ");
// String keyword = scanner.nextLine();
// List<Memo> results = manager.search(keyword);
// if (results.isEmpty()) {
// System.out.println("一致するメモはありません。");
// } else {
// viewDetails(results, scanner); // 検索結果の一覧から詳細表示へ
// }
// }

// /**
// * ユーザーに検索タグを入力させ、そのタグを持つメモを検索・表示します。
// */
// private void searchByTag(Scanner scanner) {
// System.out.print("検索するタグを入力してください: ");
// String tag = scanner.nextLine().trim();
// List<Memo> results = manager.searchByTag(tag);
// if (results.isEmpty()) {
// System.out.println("指定されたタグに一致するメモはありません。");
// } else {
// System.out.println("=== タグ検索結果 ===");
// viewDetails(results, scanner); // 検索結果の一覧から詳細表示へ
// }
// }

// /**
// * メモのリストを表示し、ユーザーが選択したメモの詳細を表示します。
// */
// private void viewDetails(List<Memo> memos, Scanner scanner) {
// displayMemos(memos); // まずメモ一覧を表示
// System.out.print("詳細を見たい番号を入力（Enterでキャンセル）: ");
// String input = scanner.nextLine().trim();
// if (input.isEmpty()) {
// System.out.println("キャンセルしました。");
// return;
// }
// try {
// int index = Integer.parseInt(input) - 1;
// Memo selected = manager.getMemoByIndex(memos, index);
// if (selected != null) {
// showMemoDetails(selected, scanner); // 詳細表示処理を呼び出し
// } else {
// System.out.println("その番号のメモはありません。");
// }
// } catch (NumberFormatException e) {
// System.out.println("数字を入力してください！");
// }
// }

// /**
// * 登録されているすべてのタグを一覧表示し、ユーザーが選択したタグを持つメモを表示します。
// */
// private void showMemosBySelectedTag(Scanner scanner) {
// List<String> tagList = new java.util.ArrayList<>(manager.getAllTags());
// if (tagList.isEmpty()) {
// System.out.println("タグが登録されていません。");
// return;
// }

// System.out.println("\n=== タグ一覧 ===");
// for (int i = 0; i < tagList.size(); i++) {
// System.out.println((i + 1) + ". " + tagList.get(i));
// }

// System.out.print("メモを見たいタグの番号を入力（Enterでキャンセル）: ");
// String input = scanner.nextLine().trim();
// if (input.isEmpty()) {
// System.out.println("キャンセルしました。");
// return;
// }

// try {
// int index = Integer.parseInt(input) - 1;
// if (index >= 0 && index < tagList.size()) {
// String selectedTag = tagList.get(index);
// List<Memo> matchedMemos = manager.searchByTag(selectedTag);
// viewDetails(matchedMemos, scanner); // 該当メモの一覧から詳細表示へ
// } else {
// System.out.println("無効な番号です。");
// }
// } catch (NumberFormatException e) {
// System.out.println("数字を入力してください！");
// }
// }

// /**
// * 1件のメモの詳細（タイトル、タグ、日時、本文）を表示し、
// * その後の操作（閉じる、編集、削除）をユーザーに促します。
// */
// private void showMemoDetails(Memo memo, Scanner scanner) {
// System.out.println("\n--- メモ詳細 ---");
// System.out.println("[タイトル] " + memo.getTitle());
// System.out.println("[タグ] " + String.join(", ", memo.getTags()));

// String dateLabel = (memo.getUpdatedAt() != null &&
// !memo.getUpdatedAt().equals(memo.getCreatedAt())) ? "最終更新日" : "作成日";
// String dateValue = (memo.getUpdatedAt() != null &&
// !memo.getUpdatedAt().equals(memo.getCreatedAt())) ? memo.getUpdatedAt() :
// memo.getCreatedAt();
// System.out.println(dateLabel + ": " + dateValue);
// System.out.println("[本文]\n" + memo.getBody());

// System.out.println("\n操作を選択してください：");
// System.out.println("1. 閉じる");
// System.out.println("2. メモを編集する");
// System.out.println("3. メモを削除する");
// System.out.print("番号で選択: ");
// try {
// int choice = Integer.parseInt(scanner.nextLine());
// switch (choice) {
// case 1 -> { return; } // 何もせずメソッドを抜ける
// case 2 -> editMemo(memo, scanner);
// case 3 -> {
// manager.delete(memo);
// System.out.println("メモを削除しました");
// }
// default -> System.out.println("無効な選択です。");
// }
// } catch (NumberFormatException e) {
// System.out.println("数字を入力してください！");
// }
// }

// /**
// * 既存のメモを対話的に編集します（タイトル、本文、タグ）。
// */
// public void editMemo(Memo memo, Scanner scanner) {
// System.out.println("\n編集オプション: ");
// System.out.println("[1] タイトルを編集");
// System.out.println("[2] 本文を編集");
// System.out.println("[3] タグを編集");
// System.out.print("選択（Enterで戻る）: ");
// String choice = scanner.nextLine().trim();

// switch (choice) {
// case "1" -> {
// System.out.print("新しいタイトル: ");
// String newTitle = scanner.nextLine().trim();
// memo.setTitle(newTitle);
// manager.update(memo);
// System.out.println("タイトルを更新しました。");
// }
// case "2" -> {
// String newBody = promptForBody(scanner);
// memo.setBody(newBody);
// manager.update(memo);
// System.out.println("本文を更新しました。");
// }
// case "3" -> {
// List<String> newTags = promptForTags(scanner);
// memo.setTags(newTags);
// manager.update(memo);
// System.out.println("タグを更新しました。");
// }
// case "" -> System.out.println("編集をスキップしました。");
// default -> System.out.println("無効な選択です。");
// }
// }

// /**
// * ユーザーに複数行の本文入力を促すヘルパーメソッドです。
// * ":end"という単語が単独で入力されるまで入力を受け付けます。
// *
// * @param scanner 使用するScannerインスタンス。
// * @return 入力された本文文字列。
// */
// private String promptForBody(Scanner scanner) {
// System.out.println("本文を入力してください（`:end`で終了）:");
// StringBuilder bodyBuilder = new StringBuilder();
// String line;
// while (!(line = scanner.nextLine()).equals(":end")) {
// bodyBuilder.append(line).append(System.lineSeparator());
// }
// return bodyBuilder.toString().trim();
// }

// /**
// * ユーザーにタグの入力を促すヘルパーメソッドです。
// * カンマ区切りの文字列をタグのリストに変換します。
// *
// * @param scanner 使用するScannerインスタンス。
// * @return 入力されたタグのリスト。
// */
// private List<String> promptForTags(Scanner scanner) {
// System.out.print("タグをカンマ区切りで入力してください（例: 仕事,勉強）: ");
// String tagInput = scanner.nextLine();
// // カンマで分割し、各要素の空白を除去し、空の要素をフィルタリングしてリストに変換
// return Arrays.stream(tagInput.split(","))
// .map(String::trim)
// .map(tag -> tag.replaceFirst("^#", "")) // 先頭の#を削除
// .filter(s -> !s.isEmpty())
// .collect(Collectors.toList());
// }
// }
