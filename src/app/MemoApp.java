package app;

import javax.swing.SwingUtilities;

import memo.MemoManager;
import storage.MemoRepository;
import ui.MemoGui;

/**
 * メモアプリケーションのエントリーポイント（開始点）となるクラスです。
 * アプリケーションの主要なコンポーネント（リポジトリ、マネージャー、UI）を
 * 生成し、それらの依存関係を解決（注入）する役割を担います。
 */
public class MemoApp {
    /**
     * アプリケーションのメインメソッド。
     *
     * @param args コマンドライン引数（このアプリケーションでは使用しません）。
     */
    public static void main(String[] args) {
        try {
            // --- 依存関係の構築（Dependency Injection） ---

            // 1. データ永続化層のインスタンスを生成
            MemoRepository repository = new MemoRepository();

            // 2. ビジネスロジック層のインスタンスを生成し、リポジトリを注入
            MemoManager manager = new MemoManager(repository);

            // --- GUIの起動 ---
            // Swingのコンポーネントはイベントディスパッチスレッド（EDT）で操作する必要があるため、
            // `SwingUtilities.invokeLater` を使用してGUIの生成と表示をスケジュールします。
            System.out.println("HashMemo GUI アプリケーションを起動します...");
            SwingUtilities.invokeLater(() -> {
                // 3. UI層のインスタンスを生成し、マネージャーを注入
                MemoGui gui = new MemoGui(manager);
                gui.setVisible(true);
            });

        } catch (Exception e) {
            // アプリケーションの初期化中または実行中に予期せぬエラーが発生した場合
            System.err.println("アプリケーションの起動に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
