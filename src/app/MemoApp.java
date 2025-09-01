package app;

import memo.MemoManager;
import ui.MemoGui;

import javax.swing.SwingUtilities;

/**
 * メモアプリケーションのエントリーポイント（開始点）となるクラスです。
 * この`main`メソッドから、GUIモードとCLIモードの両方を初期化し、起動します。
 */
public class MemoApp {
    /**
     * アプリケーションのメインメソッドです。
     *
     * @param args コマンドライン引数（このアプリケーションでは使用しません）。
     */
    public static void main(String[] args) {
        try {
            // アプリケーション全体で共有するビジネスロジック層のインスタンスを生成します。
            MemoManager manager = new MemoManager();

            // --- GUIの起動 ---
            // Swingのコンポーネントはイベントディスパッチスレッド（EDT）で操作する必要があるため、
            // `SwingUtilities.invokeLater` を使用してGUIの生成と表示をスケジュールします。
            SwingUtilities.invokeLater(() -> {
                MemoGui gui = new MemoGui(manager);
                gui.setVisible(true);
            });
            System.out.println("HashMemo GUI アプリケーションを起動します...");
            // CLI起動部分はmainブランチでは省略（デバッグ用途のみ）
        } catch (Exception e) {
            // アプリケーションの初期化中または実行中に予期せぬエラーが発生した場合
            System.err.println("予期せぬエラーが発生しました。アプリケーションを終了します: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
