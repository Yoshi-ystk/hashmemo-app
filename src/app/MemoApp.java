package app;

import cli.MemoCli;
import memo.MemoManager;
import ui.MemoGui;

import javax.swing.SwingUtilities;

public class MemoApp {
    public static void main(String[] args) {
        try {
            MemoManager manager = new MemoManager();
            MemoCli cli = new MemoCli(manager);

            System.out.println("HashMemo CLI アプリケーションを起動します...");
            cli.run();

            SwingUtilities.invokeLater(() -> {
                MemoGui gui = new MemoGui(manager);
                gui.setVisible(true);
            });
            System.out.println("HashMemoアプリケーションを起動します...");
        } catch (Exception e) {
            System.err.println("アプリケーションの起動に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}