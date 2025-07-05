package app;

import cli.MemoCli;
import memo.MemoManager;
import ui.MemoGui;

import javax.swing.SwingUtilities;

public class MemoApp {
    public static void main(String[] args) {
        try {
            MemoManager manager = new MemoManager();

            SwingUtilities.invokeLater(() -> {
                MemoGui gui = new MemoGui(manager);
                gui.setVisible(true);
            });
            System.out.println("HashMemo GUI アプリケーションを起動します...");
            
            MemoCli cli = new MemoCli(manager);
            System.out.println("HashMemo CLI アプリケーションを起動します...");
            cli.run();
            
        } catch (Exception e) {
            System.err.println("アプリケーションの起動に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}