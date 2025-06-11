// 簡易メモ帳プログラム

import java.awt.*;
import java.awt.event.*;
// ファイルの読み込みに使用
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
// ファイルの書き込みに使用
import java.io.IOException;
import java.io.FileWriter;

// Frameクラスを継承
public class Memo2 extends Frame implements ActionListener {
    // メンバ変数
    private Frame fr; // フレーム
    private TextArea ta; // テキストエリア
    private String filename;// ファイルの名前

    // メイン関数
    public static void main(String[] args) {
        new Memo2();
    }

    // コンストラクタ
    Memo2() {
        // タイトルを付けてインスタンス化
        fr = new Frame("簡易メモ帳");

        // 変数クリア
        filename = "";

        // メニューバー
        MenuBar mb = new MenuBar();
        Menu mb_file = mb.add(new Menu("file"));
        MenuItem mi_open = mb_file.add(new MenuItem("open"));
        MenuItem mi_save = mb_file.add(new MenuItem("save"));

        // パネル定義
        Panel pn = new Panel();
        pn.setLayout(new GridLayout(1, 1));

        // テキストエリアコントロール
        ta = new TextArea("");

        // テキストエリアコントロールをパネルに追加
        pn.add(ta);

        // パネルをフレームに追加
        fr.add(pn);

        // メニューバーをフレームに追加
        fr.setMenuBar(mb);

        // フォームの表示
        fr.pack();
        fr.setSize(800, 600);
        fr.setVisible(true);

        // メニューバーのイベント登録
        mi_open.addActionListener(this);
        mi_save.addActionListener(this);
        // フォームのイベント登録
        fr.addWindowListener(new CloseWindowListener());
    }

    // メニューイベント
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            // file/openメニュー
            case "open":
                FileDialog dg = new FileDialog(
                        (Frame) null,
                        "file open",
                        FileDialog.LOAD);

                dg.setVisible(true);
                filename = dg.getDirectory() + dg.getFile();

                try {
                    File file = new File(filename);
                    Scanner scn = new Scanner(file);

                    ta.setText("");
                    while (scn.hasNextLine()) {
                        ta.append(scn.nextLine() + "\n");
                    }

                    scn.close();
                } catch (FileNotFoundException err) {

                }
                break;
            // file/saveメニュー
            case "save":
                try {
                    if (filename != "") {
                        FileWriter fw = new FileWriter(filename);

                        fw.write(ta.getText());
                        fw.close();
                    }
                } catch (IOException err) {

                }

                break;
        }
    }

    // フォームを閉じるイベント
    class CloseWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);

        }
    }
}