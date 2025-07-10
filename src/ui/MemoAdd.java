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
 * メモの追加および編集を行うためのダイアログクラスです。
 * 新規作成と既存メモの更新の両方の機能を持ち、コンストラクタによって動作が切り替わります。
 */
public class MemoAdd extends JDialog {
    // UIコンポーネント
    private JTextField titleField; // タイトル入力用
    private JTextField tagField; // タグ入力用
    private JTextArea bodyArea; // 本文入力用
    private JButton submitButton; // 送信（登録・更新）ボタン

    // 親ウィンドウ（MemoGui）への参照
    private MemoGui parent;

    /**
     * 新しいメモを追加するためのコンストラクタです。
     *
     * @param owner     親となるJFrame（MemoGui）。
     * @param manager   ビジネスロジックを担当するMemoManager。
     * @param memoModel メモリストを管理するListModel。
     */
    public MemoAdd(JFrame owner, MemoManager manager, DefaultListModel<Memo> memoModel) {
        super(owner, "メモ追加", true); // モーダルダイアログとして設定
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // UIコンポーネントの初期化
        titleField = new JTextField();
        tagField = new JTextField();
        bodyArea = new JTextArea();
        bodyArea.setLineWrap(true); // 自動折り返しを有効に

        // 本文エリアをスクロール可能にする
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyScroll.setPreferredSize(new Dimension(400, 200));

        // 本文エリアのラッパーパネル（ラベルとスクロールペインをまとめる）
        JPanel bodyWrapper = new JPanel(new BorderLayout());
        bodyWrapper.add(new JLabel("本文"), BorderLayout.NORTH);
        bodyWrapper.add(bodyScroll, BorderLayout.CENTER);

        // 入力フォーム全体のパネル
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(new JLabel("タイトル"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("タグ（カンマ区切り）"));
        formPanel.add(tagField);
        formPanel.add(bodyWrapper); // 本文エリアのラッパーを追加

        // 送信ボタンの初期化と設定
        submitButton = new JButton("登録");

        // 「登録」ボタンのクリックイベント
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String body = bodyArea.getText().trim();
            List<String> tags = Arrays.stream(tagField.getText().split(",")).map(String::trim).toList();

            // タイトルと本文が入力されているかチェック
            if (!title.isEmpty() && !body.isEmpty()) {
                Memo memo = new Memo(title, body, tags);
                manager.add(memo); // マネージャー経由でメモを保存
                memoModel.addElement(memo); // GUIのリストモデルにも追加
                dispose(); // ダイアログを閉じる
            } else {
                JOptionPane.showMessageDialog(this, "タイトルと本文を入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
            }
        });

        // パネルをダイアログに追加
        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    /**
     * 既存のメモを編集するためのコンストラクタです。
     *
     * @param owner        親となるJFrame（MemoGui）。
     * @param manager      ビジネスロジックを担当するMemoManager。
     * @param model        メモリストを管理するListModel。
     * @param existingMemo 編集対象のMemoオブジェクト。
     */
    public MemoAdd(JFrame owner, MemoManager manager, DefaultListModel<Memo> model, Memo existingMemo) {
        // まず追加用コンストラクタを呼び出してUIの基本設定を行う
        this(owner, manager, model);
        this.parent = (MemoGui) owner;

        // --- 編集モード用の設定 ---
        setTitle("メモ編集");
        submitButton.setText("更新");

        // 既存のメモ情報をUIコンポーネントに設定
        titleField.setText(existingMemo.getTitle());
        tagField.setText(String.join(", ", existingMemo.getTags()));
        bodyArea.setText(existingMemo.getBody());
        bodyArea.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        bodyArea.setBackground(new Color(250, 250, 250));

        // 追加用コンストラクタで設定されたActionListenerを一旦すべて削除
        for (ActionListener al : submitButton.getActionListeners()) {
            submitButton.removeActionListener(al);
        }

        // 「更新」ボタンのクリックイベントを新たに設定
        submitButton.addActionListener(e -> {
            // UIから最新の情報を取得してexistingMemoオブジェクトを更新
            existingMemo.setTitle(titleField.getText().trim());
            existingMemo.setBody(bodyArea.getText().trim());
            existingMemo.setTags(Arrays.stream(tagField.getText().split(",")).map(String::trim).toList());

            // GUIのリストモデルから一度削除して、再度追加することで表示を更新
            model.removeElement(existingMemo);
            model.addElement(existingMemo);

            manager.update(existingMemo); // マネージャー経由でデータベースを更新
            dispose(); // ダイアログを閉じる

            // 親ウィンドウの表示を更新
            parent.showMemoDetails(existingMemo); // 詳細表示を更新
            parent.refreshTagComboBox(); // タグのコンボボックスを更新
        });
    }
}
