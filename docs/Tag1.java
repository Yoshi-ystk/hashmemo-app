import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Tag1 extends JFrame {
    private JTextArea textArea;
    private JTextField searchField;
    private JButton searchButton;
    private JScrollPane scrollPane;

    public Tag1() {
        super("メモ帳 検索");

        // テキストエリア
        textArea = new JTextArea();
        textArea.setFont(new Font("MS Gothic", Font.PLAIN, 12));
        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 検索フィールド
        searchField = new JTextField(20);

        // 検索ボタン
        searchButton = new JButton("検索");

        // 検索ボタンのイベントハンドラ
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                if (searchText != null && !searchText.isEmpty()) {
                    search(searchText);
                }
            }
        });

        // コンポーネントをレイアウト
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(searchField);
        panel.add(searchButton);

        // フレームの設定
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // 検索処理
    private void search(String searchText) {
        int startPos = 0;
        int foundPos = -1;

        // 検索
        while ((foundPos = textArea.getText().indexOf(searchText, startPos)) != -1) {
            // 見つかった位置をハイライト
            textArea.setCaretPosition(foundPos);
            textArea.moveCaretPosition(foundPos + searchText.length());
            break; // 一番最初の検索結果を表示
        }

        // 見つからない場合
        if (foundPos == -1) {
            JOptionPane.showMessageDialog(this, "検索キーワードが見つかりませんでした。", "検索結果", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Tag1();
            }
        });
    }
}