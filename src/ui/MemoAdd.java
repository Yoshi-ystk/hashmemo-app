package ui;

import memo.MemoManager;
import memo.Memo;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;

/**
 * メモの追加・編集用ダイアログクラス。
 * メモのタイトル、タグ、本文を入力し、登録または更新を行う。
 */
public class MemoAdd extends JDialog {
    private JTextField titleField;
    private JTextField tagField;
    private JTextArea bodyArea;
    private JButton submitButton;
    private MemoGui parent;

    // メモの追加用コンストラクタ
    public MemoAdd(JFrame owner, MemoManager manager, DefaultListModel<Memo> memoModel) {
        super(owner, "メモ追加", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // 入力フィールドの初期化
        titleField = new JTextField();
        tagField = new JTextField();
        bodyArea = new JTextArea();
        bodyArea.setLineWrap(true);

        // 入力フィールドの設定
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyScroll.setPreferredSize(new Dimension(400, 200)); // 高さにゆとり！

        JPanel bodyWrapper = new JPanel(new BorderLayout());
        bodyWrapper.add(new JLabel("本文"), BorderLayout.NORTH);
        bodyWrapper.add(bodyScroll, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(new JLabel("タイトル"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("タグ（カンマ区切り）"));
        formPanel.add(tagField);
        formPanel.add(new JLabel("本文"));
        formPanel.add(bodyWrapper);

        // 送信ボタンの初期化
        submitButton = new JButton();
        submitButton.setText("登録");

        // メモ追加イベント
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String body = bodyArea.getText().trim();
            List<String> tags = Arrays.stream(tagField.getText().split(",")).map(String::trim).toList();

            if (!title.isEmpty() && !body.isEmpty()) {
                Memo memo = new Memo(title, body, tags);
                manager.add(memo);
                memoModel.addElement(memo);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "タイトルと本文を入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    /**
     * 既存のメモを編集するためのコンストラクタ
     */
    public MemoAdd(JFrame owner, MemoManager manager, DefaultListModel<Memo> model, Memo existingMemo) {
        this(owner, manager, model);
        this.parent = (MemoGui) owner;

        // 既存のメモ情報を入力フィールドに設定
        setTitle("メモ編集");
        submitButton.setText("更新");
        titleField.setText(existingMemo.getTitle());
        tagField.setText(String.join(", ", existingMemo.getTags()));
        bodyArea.setText(existingMemo.getBody());
        bodyArea.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        bodyArea.setBackground(new Color(250, 250, 250));

        for (ActionListener al : submitButton.getActionListeners()) {
            submitButton.removeActionListener(al);
        }

        // 更新ボタンのアクションリスナーを設定
        submitButton.addActionListener(e -> {
            existingMemo.setTitle(titleField.getText().trim());
            existingMemo.setBody(bodyArea.getText().trim());
            existingMemo.setTags(Arrays.stream(tagField.getText().split(",")).map(String::trim).toList());
            model.removeElement(existingMemo);
            model.addElement(existingMemo);
            manager.update(existingMemo);
            dispose();
            parent.showMemoDetails(existingMemo);
            parent.refreshTagComboBox();
        });
    }
}
