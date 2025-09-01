package ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import memo.Memo;
import memo.MemoManager;

/**
 * メモの追加および編集を行うためのダイアログクラスです。
 * 新規作成と既存メモの更新の両方の機能を持ち、コンストラクタによって動作が切り替わります。
 * このダイアログの責務は、ユーザー入力を受け取り、ビジネスロジック層に渡すことのみです。
 * UIのリスト更新は、呼び出し元のクラスが担当します。
 */
public class MemoAdd extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextField titleField;
    private final JTextField tagField;
    private final JTextArea bodyArea;
    private final JButton submitButton;
    private final MemoManager manager;

    /**
     * UIコンポーネントの初期化など、コンストラクタで共通の処理を行います。
     *
     * @param owner   親となるJFrame（MemoGui）。
     * @param manager ビジネスロジックを担当するMemoManager。
     * @param title   ダイアログのタイトル。
     */
    private MemoAdd(JFrame owner, MemoManager manager, String title) {
        super(owner, title, true);
        this.manager = manager;

        // --- UI Components Initialization ---
        titleField = new JTextField();
        tagField = new JTextField();
        bodyArea = new JTextArea();
        bodyArea.setLineWrap(true);
        submitButton = new JButton();

        // --- Layout Setup ---
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyScroll.setPreferredSize(new java.awt.Dimension(400, 200));

        JPanel bodyWrapper = new JPanel(new BorderLayout());
        bodyWrapper.add(new JLabel("本文"), BorderLayout.NORTH);
        bodyWrapper.add(bodyScroll, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(new JLabel("タイトル"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("タグ（カンマ区切り）"));
        formPanel.add(tagField);
        formPanel.add(bodyWrapper);

        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    /**
     * 新しいメモを追加するためのコンストラクタです。
     *
     * @param owner   親となるJFrame（MemoGui）。
     * @param manager ビジネスロジックを担当するMemoManager。
     */
    public MemoAdd(JFrame owner, MemoManager manager) {
        this(owner, manager, "メモ追加");
        submitButton.setText("登録");

        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String body = bodyArea.getText().trim();

            if (title.isEmpty() || body.isEmpty()) {
                JOptionPane.showMessageDialog(this, "タイトルと本文を入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<String> tags = parseTags(tagField.getText());
            Memo memo = new Memo(title, body, tags);
            manager.add(memo);
            dispose();
        });
    }

    /**
     * 既存のメモを編集するためのコンストラクタです。
     *
     * @param owner        親となるJFrame（MemoGui）。
     * @param manager      ビジネスロジックを担当するMemoManager。
     * @param existingMemo 編集対象のMemoオブジェクト。
     */
    public MemoAdd(JFrame owner, MemoManager manager, Memo existingMemo) {
        this(owner, manager, "メモ編集");
        submitButton.setText("更新");

        titleField.setText(existingMemo.getTitle());
        tagField.setText(String.join(", ", existingMemo.getTags()));
        bodyArea.setText(existingMemo.getBody());

        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String body = bodyArea.getText().trim();

            if (title.isEmpty() || body.isEmpty()) {
                JOptionPane.showMessageDialog(this, "タイトルと本文を入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }
            existingMemo.setTitle(title);
            existingMemo.setBody(body);
            existingMemo.setTags(parseTags(tagField.getText()));

            manager.update(existingMemo);
            dispose();
        });
    }

    /**
     * カンマ区切りのタグ文字列を文字列のリストに変換します。
     * @param rawTags タグ入力フィールドのテキスト
     * @return 空白や空要素が除去されたタグのリスト
     */
    private List<String> parseTags(String rawTags) {
        if (rawTags == null || rawTags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(rawTags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }
}