package app;

import javax.swing.SwingUtilities;
import ui.MemoGui;
import memo.MemoManager;

public class MemoApp {
    public static void main(String[] args) {
        try {
            MemoManager manager = new MemoManager();
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



/*
package app;

import javax.swing.SwingUtilities;
import ui.MemoGui;
import memo.MemoManager;

public class MemoApp {
    public static void main(String[] args) {
        MemoManager manager = new MemoManager();
        SwingUtilities.invokeLater(() -> {
            MemoGui gui = new MemoGui(manager);
            gui.setVisible(true);
        });
    }
}


package app;

import cli.MemoCli;
import memo.MemoManager;

public class MemoApp {
    public static void main(String[] args) {
        try {
            MemoManager manager = new MemoManager();
            MemoCli cli = new MemoCli(manager);
            System.out.println("HashMemo CLI アプリケーションを起動します...");
            cli.run();
        } catch (Exception e) {
            System.err.println("アプリケーションの起動に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
*/