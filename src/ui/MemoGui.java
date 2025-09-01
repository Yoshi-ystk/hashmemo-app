package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import memo.Memo;
import memo.MemoManager;

/**
 * アプリケーションのメインGUIウィンドウです。
 * `JFrame`を継承し、メモの一覧表示、検索、追加、詳細表示などの機能を提供します。
 * UIの構築とイベント処理を担当します。
 */
public class MemoGui extends JFrame {

    // --- UI Components ---
    private final DefaultListModel<Memo> memoListModel = new DefaultListModel<>();
    private JList<Memo> memoList;
    private JTextField searchField;
    private JComboBox<String> tagCombo;
    private JButton searchButton;
    private JButton addMemoButton;

    // --- Business Logic Layer ---
    private final MemoManager manager;

    /**
     * MemoGuiのコンストラクタです。
     * 依存性の注入により受け取ったMemoManagerを使い、UIの初期化とセットアップを行います。
     *
     * @param manager ビジネスロジックを担当するMemoManagerのインスタンス。
     */
    public MemoGui(MemoManager manager) {
        this.manager = manager;

        initStyle();
        initComponents();
        initLayout();
        initListeners();
    }

    /**
     * アプリケーション全体の外観（フォントなど）を設定します。
     */
    private void initStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("ルックアンドフィールの設定に失敗しました: " + e.getMessage());
        }
        Font customFont = new Font("Yu Gothic UI", Font.PLAIN, 12);
        UIManager.put("Label.font", customFont);
        UIManager.put("Button.font", customFont);
        UIManager.put("ComboBox.font", customFont);
        UIManager.put("TextField.font", customFont);
        UIManager.put("List.font", customFont);
    }

    /**
     * このフレームで使われるUIコンポーネントを初期化します。
     */
    private void initComponents() {
        // --- Top Panel Components ---
        tagCombo = new JComboBox<>();
        searchField = new JTextField(20);
        searchButton = new JButton("検索");
        addMemoButton = new JButton("メモ追加");

        // --- Memo List ---
        memoList = new JList<>(memoListModel);
        memoList.setCellRenderer(new MemoListCellRenderer());
        memoList.setFixedCellHeight(60);
        memoList.setSelectionBackground(new Color(100, 100, 100));
        memoList.setSelectionForeground(Color.WHITE);

        // --- Initial Data Loading ---
        refreshMemoList(null, "すべて表示"); // 初回は全件表示
        refreshTagComboBox();
    }

    /**
     * UIコンポーネントをフレームに配置します。
     */
    private void initLayout() {
        setTitle("hashmemo");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Logo ---
        add(createLogoLabel(), BorderLayout.NORTH);

        // --- Top Panel (Controls) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(new JLabel("タグ:"));
        topPanel.add(tagCombo);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addMemoButton);

        // --- Memo List Panel ---
        JScrollPane scrollPane = new JScrollPane(memoList);
        scrollPane.setPreferredSize(new Dimension(500, 0));
        JPanel memoPanel = new JPanel(new BorderLayout());
        JLabel memoLabel = new JLabel("メモ一覧");
        setupLabelStyle(memoLabel);
        memoPanel.add(memoLabel, BorderLayout.NORTH);
        memoPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Center Wrapper ---
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(topPanel, BorderLayout.NORTH);
        centerWrapper.add(memoPanel, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        getContentPane().setBackground(new Color(240, 240, 240));
    }

    /**
     * すべてのイベントリスナーを設定します。
     */
    private void initListeners() {
        addMemoButton.addActionListener(e -> openAddMemoDialog());
        searchButton.addActionListener(e -> searchMemos());
        tagCombo.addActionListener(e -> searchMemos());

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

    // --- Event Handler Methods ---

    /**
     * 「メモ追加」ボタンが押されたときの処理。
     * メモ追加ダイアログを開き、ダイアログが閉じられた後にリストを更新します。
     */
    private void openAddMemoDialog() {
        MemoAdd addDialog = new MemoAdd(this, manager);
        addDialog.setVisible(true); // モーダルなので、ここで処理がブロックされる

        // ダイアログが閉じた後に実行される
        resetSearchAndRefresh();
    }

    /**
     * 「検索」ボタンまたはタグコンボボックスが操作されたときの処理。
     */
    private void searchMemos() {
        String keyword = searchField.getText();
        String selectedTag = (String) tagCombo.getSelectedItem();
        List<Memo> filteredMemos = manager.filterMemos(keyword, selectedTag);
        refreshMemoList(filteredMemos, null);

        // 検索結果が0件だった場合にポップアップを表示
        boolean isSearching = !keyword.trim().isEmpty() || (selectedTag != null && !"すべて表示".equals(selectedTag));
        if (filteredMemos.isEmpty() && isSearching) {
            JOptionPane.showMessageDialog(this, "一致するメモが見つかりません", "検索結果", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 検索条件をリセットし、メモリストとタグリストを最新の状態に更新します。
     * メモの追加・削除・更新後に呼び出されます。
     */
    private void resetSearchAndRefresh() {
        ActionListener[] tagListeners = tagCombo.getActionListeners();
        for (ActionListener l : tagListeners) {
            tagCombo.removeActionListener(l);
        }

        searchField.setText("");
        refreshTagComboBox(); // 内部で `setSelectedIndex(0)` が呼ばれる可能性があるため、先に更新
        tagCombo.setSelectedIndex(0);
        refreshMemoList(null, "すべて表示");

        for (ActionListener l : tagListeners) {
            tagCombo.addActionListener(l);
        }
    }

    // --- UI Update Methods ---

    /**
     * メモリストの表示を更新します。
     * @param memos 表示するメモのリスト。nullの場合は全件取得し直します。
     * @param selectedTag タグでの絞り込み条件。memosがnullの場合のみ使用します。
     */
    private void refreshMemoList(List<Memo> memos, String selectedTag) {
        memoListModel.clear();
        List<Memo> memosToDisplay = (memos != null) ? memos : manager.filterMemos("", selectedTag);
        memosToDisplay.forEach(memoListModel::addElement);
    }

    /**
     * タグ選択用のコンボボックスの内容を最新の状態に更新します。
     */
    public void refreshTagComboBox() {
        Set<String> tags = manager.getAllTags();
        List<String> tagList = new ArrayList<>(tags);
        tagList.add(0, "すべて表示");

        tagCombo.removeAllItems();
        tagList.forEach(tagCombo::addItem);
    }

    // --- Dialogs and Sub-windows ---

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

        JPanel infoPanel = createDetailsInfoPanel(memo);
        JTextArea detailsArea = createDetailsTextArea(memo);
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        JPanel buttonPanel = createDetailsButtonPanel(dialog, memo);

        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // --- UI Helper Methods ---

    private JLabel createLogoLabel() {
        JLabel logoLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("assets/hashmemo_logo.png");

                if (icon.getIconWidth() == -1) {
                    g.setColor(Color.RED);
                    g.drawString("Image not found: assets/hashmemo_logo.png", 10, 20);
                    return;
                }

                Image img = icon.getImage();
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = img.getWidth(this);
                int imgHeight = img.getHeight(this);

                double panelRatio = (double) panelWidth / panelHeight;
                double imgRatio = (double) imgWidth / imgHeight;

                int drawWidth, drawHeight, x, y;

                if (panelRatio > imgRatio) {
                    drawHeight = panelHeight;
                    drawWidth = (int) (imgWidth * ((double) panelHeight / imgHeight));
                } else {
                    drawWidth = panelWidth;
                    drawHeight = (int) (imgHeight * ((double) panelWidth / imgWidth));
                }

                x = (panelWidth - drawWidth) / 2;
                y = (panelHeight - drawHeight) / 2;

                g.drawImage(img, x, y, drawWidth, drawHeight, this);
            }
        };
        logoLabel.setPreferredSize(new Dimension(200, 80));
        logoLabel.setBackground(Color.BLACK);
        logoLabel.setOpaque(true);
        return logoLabel;
    }

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

    private JPanel createDetailsButtonPanel(JDialog dialog, Memo memo) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("編集");
        JButton deleteButton = new JButton("削除");
        JButton doneButton = new JButton("閉じる");

        editButton.addActionListener(e -> {
            dialog.dispose();
            MemoAdd editDialog = new MemoAdd(this, manager, memo);
            editDialog.setVisible(true);
            resetSearchAndRefresh();
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "このメモを削除しますか？", "確認", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.delete(memo);
                dialog.dispose();
                resetSearchAndRefresh();
            }
        });

        doneButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(doneButton);
        return buttonPanel;
    }

    private void setupLabelStyle(JLabel label) {
        label.setFont(new Font("Yu Gothic UI", Font.BOLD, 16));
        label.setForeground(new Color(90, 90, 90));
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setHorizontalAlignment(SwingConstants.CENTER);
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
        if (value instanceof Memo) {
            Memo memo = (Memo) value;

            String dateLabel = memo.getUpdatedAt() != null && !memo.getUpdatedAt().equals(memo.getCreatedAt())
                    ? "最終更新日: " + memo.getUpdatedAt()
                    : "作成日: " + memo.getCreatedAt();

            label.setText("<html><b>" + memo.getTitle() + " </b><br><span style='color:gray'> "
                    + String.join(", ", memo.getTags())
                    + "  " + dateLabel + "</span></html>");

            label.setBorder(new EmptyBorder(12, 10, 20, 8));
        }
        return label;
    }
}