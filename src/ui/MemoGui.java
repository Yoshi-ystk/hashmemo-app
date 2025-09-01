package ui;

import memo.Memo;
import memo.MemoManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * アプリケーションのメインGUIウィンドウです。
 * `JFrame`を継承し、メモの一覧表示、検索、追加、詳細表示などの機能を提供します。
 */
public class MemoGui extends JFrame {
    // 検索フィールドをインスタンス変数として保持
    private JTextField searchField;
    // ビジネスロジックとデータアクセスを担当するマネージャー
    private MemoManager manager;
    // メモ一覧を表示するためのリストモデル
    private DefaultListModel<Memo> model = new DefaultListModel<>();
    // タグ検索用のコンボボックス
    private JComboBox<String> tagCombo;

    /**
     * MemoGuiのコンストラクタです。
     * UIの初期化とセットアップを行います。
     *
     * @param manager MemoManagerのインスタンス。
     */
    public MemoGui(MemoManager manager) {
        this.manager = manager;

        // --- JFrameの基本設定 ---
        setTitle("hashmemo");
        setSize(600, 500);
        setLocationRelativeTo(null); // 画面中央に表示
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- UI全体のフォント設定 ---
        Font customFont = new Font("Yu Gothic UI", Font.PLAIN, 12);
        UIManager.put("Label.font", customFont);
        UIManager.put("Button.font", customFont);
        UIManager.put("ComboBox.font", customFont);
        UIManager.put("TextField.font", customFont);
        UIManager.put("List.font", customFont);
        // ... 他のUIコンポーネントにもフォントを適用

        // --- ヘッダー部分（ロゴ） ---
        JLabel logoLabel = createLogoLabel();

        // --- トップパネル（操作ボタン、検索フィールド） ---
        JPanel topPanel = createTopPanel();
        // createTopPanelでsearchFieldを初期化

        // --- メインエリア（メモ一覧） ---
        JList<Memo> memoList = createMemoList();
        JScrollPane scroll = new JScrollPane(memoList);
        scroll.setPreferredSize(new Dimension(500, 0));

        // メモ一覧を囲むパネル
        JPanel memoPanel = new JPanel(new BorderLayout());
        JLabel memoLabel = new JLabel("メモ一覧");
        setupLabelStyle(memoLabel);
        memoPanel.add(memoLabel, BorderLayout.NORTH);
        memoPanel.add(scroll, BorderLayout.CENTER);

        // --- 中央部分のレイアウト ---
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(topPanel, BorderLayout.NORTH);
        centerWrapper.add(memoPanel, BorderLayout.CENTER);

        // --- 全体をフレームに追加 ---
        add(logoLabel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        // 背景色設定
        getContentPane().setBackground(new Color(240, 240, 240));

        // --- イベントリスナーの設定 ---
        setupEventListeners(memoList, (JTextField) topPanel.getComponent(2), (JButton) topPanel.getComponent(3),
                (JButton) topPanel.getComponent(4));
    }

    /**
     * ヘッダーに表示するロゴのJLabelを生成します。
     * 画像はパネルサイズに合わせてアスペクト比を維持して描画されます。
     */
    private JLabel createLogoLabel() {
        JLabel logoLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // src/assets に移動したので、パスを修正
                ImageIcon icon = new ImageIcon("assets/hashmemo_logo.png");

                if (icon.getIconWidth() == -1) {
                    // 画像が見つからない場合のエラー表示
                    g.setColor(Color.RED);
                    g.drawString("Image not found: assets/hashmemo_logo.png", 10, 20);
                    return;
                }

                Image img = icon.getImage();
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = img.getWidth(this);
                int imgHeight = img.getHeight(this);

                // アスペクト比を維持した描画サイズと位置を計算
                double panelRatio = (double) panelWidth / panelHeight;
                double imgRatio = (double) imgWidth / imgHeight;

                int drawWidth;
                int drawHeight;
                int x;
                int y;

                if (panelRatio > imgRatio) {
                    drawHeight = panelHeight;
                    drawWidth = (int) (imgWidth * ((double) panelHeight / imgHeight));
                } else {
                    drawWidth = panelWidth;
                    drawHeight = (int) (imgHeight * ((double) panelWidth / imgWidth));
                }

                x = (panelWidth - drawWidth) / 2;
                y = (panelHeight - drawHeight) / 2;

                // 計算したサイズと位置で画像を描画
                g.drawImage(img, x, y, drawWidth, drawHeight, this);
            }
        };
        logoLabel.setPreferredSize(new Dimension(200, 80));
        logoLabel.setBackground(Color.BLACK);
        logoLabel.setOpaque(true);
        return logoLabel;
    }

    /**
     * ウィンドウ上部の操作パネル（タグ選択、検索、追加ボタン）を生成します。
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tagCombo = new JComboBox<>();
        refreshTagComboBox();

        searchField = new JTextField(20);
        JButton searchButton = new JButton("検索");
        JButton addMemoButton = new JButton("メモ追加");

        topPanel.add(new JLabel("タグ:"));
        topPanel.add(tagCombo);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addMemoButton);
        return topPanel;
    }

    /**
     * メモ一覧を表示するJListを生成・設定します。
     */
    private JList<Memo> createMemoList() {
        manager.getAll().forEach(model::addElement);
        JList<Memo> memoList = new JList<>(model);
        memoList.setFixedCellHeight(60);
        memoList.setSelectionBackground(new Color(100, 100, 100));
        memoList.setSelectionForeground(Color.WHITE);
        // 各セルの表示方法をカスタマイズ
        memoList.setCellRenderer(new MemoListCellRenderer());
        return memoList;
    }

    /**
     * ラベルのスタイルを設定するヘルパーメソッドです。
     */
    private void setupLabelStyle(JLabel label) {
        label.setFont(new Font("Yu Gothic UI", Font.BOLD, 16));
        label.setForeground(new Color(90, 90, 90));
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * UIコンポーネントのイベントリスナーをまとめて設定します。
     */
    private void setupEventListeners(JList<Memo> memoList, JTextField searchField, JButton searchButton,
            JButton addMemoButton) {
        // 「メモ追加」ボタン
        addMemoButton.addActionListener(e -> {
            MemoAdd addDialog = new MemoAdd(this, manager, model);
            addDialog.setVisible(true);
            // メモ追加後は全件リストを再表示し、検索条件をリセット

            // --- tagComboのリスナーを一時的に外す ---
            ActionListener[] tagListeners = tagCombo.getActionListeners();
            for (ActionListener l : tagListeners)
                tagCombo.removeActionListener(l);

            searchField.setText("");
            tagCombo.setSelectedIndex(0); // "すべて表示"を選択
            filterMemos("", "すべて表示", true); // ポップアップ抑制
            refreshTagComboBox(); // タグリストも更新

            // --- リスナーを戻す ---
            for (ActionListener l : tagListeners)
                tagCombo.addActionListener(l);
        });

        // 「タグ」コンボボックス
        tagCombo.addActionListener(e -> filterMemos(searchField.getText(), (String) tagCombo.getSelectedItem()));

        // 「検索」ボタン
        searchButton.addActionListener(e -> filterMemos(searchField.getText(), (String) tagCombo.getSelectedItem()));

        // メモリストのダブルクリックで詳細表示
        memoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Memo selected = memoList.getSelectedValue();
                    if (selected != null) {
                        showMemoDetails(selected);
                    }
                }
            }
        });
    }

    /**
     * キーワードとタグに基づいてメモ一覧をフィルタリングします。
     */
    private void filterMemos(String keyword, String selectedTag) {
        filterMemos(keyword, selectedTag, false);
    }

    /**
     * フィルタ時にポップアップ表示を抑制するオプション付き
     */
    private void filterMemos(String keyword, String selectedTag, boolean suppressPopup) {
        String normalizedKeyword = keyword.trim().toLowerCase();
        model.clear();

        List<Memo> filtered = manager.getAll().stream()
                .filter(memo -> {
                    // キーワードに一致するかどうか
                    boolean matchKeyword = normalizedKeyword.isEmpty() ||
                            memo.getTitle().toLowerCase().contains(normalizedKeyword) ||
                            memo.getBody().toLowerCase().contains(normalizedKeyword);
                    // 選択されたタグに一致するかどうか
                    boolean matchTag = "すべて表示".equals(selectedTag) ||
                            memo.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(selectedTag));
                    return matchKeyword && matchTag;
                })
                .toList();

        filtered.forEach(model::addElement);

        // 検索条件が指定されている場合のみポップアップを表示
        boolean isSearching = !(keyword.trim().isEmpty() && ("すべて表示".equals(selectedTag) || selectedTag == null));
        if (!suppressPopup && filtered.isEmpty() && isSearching) {
            JOptionPane.showMessageDialog(this, "一致するメモが見つかりません", "検索結果", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * タグ選択用のコンボボックスの内容を最新の状態に更新します。
     */
    public void refreshTagComboBox() {
        Set<String> tags = manager.getAllTags();
        List<String> tagList = new ArrayList<>(tags);
        tagList.add(0, "すべて表示");

        tagCombo.removeAllItems();
        for (String tag : tagList) {
            tagCombo.addItem(tag);
        }
    }

    /**
     * 選択されたメモの詳細情報を表示するダイアログを開きます。
     *
     * @param memo 表示するMemoオブジェクト。
     */
    public void showMemoDetails(Memo memo) {
        JDialog dialog = new JDialog(this, "メモの詳細", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // --- 情報パネル（タイトル、タグ、日付） ---
        JPanel infoPanel = createDetailsInfoPanel(memo);

        // --- 本文表示エリア ---
        JTextArea detailsArea = createDetailsTextArea(memo);
        JScrollPane scrollPane = new JScrollPane(detailsArea);

        // --- ボタンパネル（編集、削除、閉じる） ---
        JPanel buttonPanel = createDetailsButtonPanel(dialog, memo);

        // --- パネルをダイアログに追加 ---
        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * 詳細表示ダイアログの情報パネルを生成します。
     */
    private JPanel createDetailsInfoPanel(Memo memo) {
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel titleLabel = new JLabel(memo.getTitle());
        titleLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 17));

        JLabel tagLabel = new JLabel(String.join(", ", memo.getTags()));
        tagLabel.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));

        String dateStr = memo.getUpdatedAt() != null && !memo.getUpdatedAt().equals(memo.getCreatedAt())
                ? "最終更新日: " + memo.getUpdatedAt()
                : "作成日: " + memo.getCreatedAt();
        JLabel dateInfo = new JLabel(dateStr);
        dateInfo.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
        dateInfo.setForeground(new Color(100, 100, 100));

        infoPanel.add(titleLabel);
        infoPanel.add(tagLabel);
        infoPanel.add(dateInfo);
        return infoPanel;
    }

    /**
     * 詳細表示ダイアログの本文表示エリアを生成します。
     */
    private JTextArea createDetailsTextArea(Memo memo) {
        JTextArea detailsArea = new JTextArea(memo.getBody(), 15, 40);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        detailsArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        detailsArea.setBackground(new Color(245, 245, 245));
        return detailsArea;
    }

    /**
     * 詳細表示ダイアログのボタンパネルを生成します。
     */
    private JPanel createDetailsButtonPanel(JDialog dialog, Memo memo) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("編集");
        JButton deleteButton = new JButton("削除");
        JButton doneButton = new JButton("閉じる");

        // 「編集」ボタン
        editButton.addActionListener(e -> {
            dialog.dispose();
            MemoAdd editDialog = new MemoAdd(this, manager, model, memo);
            editDialog.setVisible(true);
        });

        // 「削除」ボタン
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "このメモを削除しますか？", "確認", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.delete(memo);
                // --- tagComboのリスナーを一時的に外す ---
                ActionListener[] tagListeners = tagCombo.getActionListeners();
                for (ActionListener l : tagListeners)
                    tagCombo.removeActionListener(l);
                // --- 値リセット ---
                tagCombo.setSelectedIndex(0); // "すべて表示"を選択
                if (searchField != null)
                    searchField.setText("");
                // --- リスナーを戻す ---
                for (ActionListener l : tagListeners)
                    tagCombo.addActionListener(l);
                // --- 検索リスト再表示 ---
                filterMemos("", "すべて表示", true); // ポップアップ抑制
                refreshTagComboBox();
                dialog.dispose();
            }
        });
        // 「閉じる」ボタン
        doneButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(doneButton);
        return buttonPanel;
    }

}

/**
 * JListのセル表示をカスタマイズするためのレンダラークラスです。
 * メモのタイトル、タグ、日付を整形して表示します。
 */
class MemoListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Memo memo = (Memo) value;

        String dateLabel = memo.getUpdatedAt() != null && !memo.getUpdatedAt().equals(memo.getCreatedAt())
                ? "最終更新日: " + memo.getUpdatedAt()
                : "作成日: " + memo.getCreatedAt();

        // HTMLを使ってテキストをフォーマット
        label.setText("<html><b>" + memo.getTitle() + " </b><br><span style='color:gray'> "
                + String.join(", ", memo.getTags())
                + "  " + dateLabel + "</span></html>");

        label.setBorder(new EmptyBorder(12, 10, 20, 8));
        return label;
    }
}
