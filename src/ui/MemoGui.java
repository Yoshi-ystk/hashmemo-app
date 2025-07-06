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
 * メモの一覧表示と操作を行うGUIクラス。
 * メモの追加、タグ検索、キーワード検索、メモ詳細表示などを提供。
 */

public class MemoGui extends JFrame { 
    private MemoManager manager;
    private DefaultListModel<Memo> model = new DefaultListModel<>();
    private JComboBox<String> tagCombo;

    public MemoGui(MemoManager manager) {
        this.manager = manager;

        //JFrameの基本設定
        setTitle("hashmemo");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

         // フォントの設定
        Font customFont = new Font("Yu Gothic UI", Font.PLAIN, 12);

        UIManager.put("Label.font", customFont);
        UIManager.put("Button.font", customFont);
        UIManager.put("ComboBox.font", customFont);
        UIManager.put("TextField.font", customFont);
        UIManager.put("List.font", customFont);
        UIManager.put("Panel.font", customFont);
        UIManager.put("ScrollPane.font", customFont);
        UIManager.put("TextArea.font", customFont);
        
        // コンボボックスの初期設定
        tagCombo = new JComboBox<>();
        refreshTagComboBox(); 

        //アイコンの設定
        ImageIcon rawIcon = new ImageIcon("assets/darkicon1.jpg");
        Image rawImage = rawIcon.getImage();
        setIconImage(rawImage);

        //元サイズを取得
        int originalWidth = rawImage.getWidth(null);
        int originalHeight = rawImage.getHeight(null);

        int targetWidth = 200;
        int targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth); // 高さを比率で計算

        Image scaledImage = rawImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(resizedIcon);

        //選択パネルの設定
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("検索");
        JButton addMemoButton = new JButton("メモ追加");

        JLabel tagLabel = new JLabel("タグ:");
        topPanel.add(tagLabel);
        topPanel.add(tagCombo);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addMemoButton);

        // メモ一覧表示の設定
        manager.getAll().forEach(model::addElement);
        JList<Memo> memoList = new JList<>(model);
        memoList.setFixedCellHeight(30);
        memoList.setSelectionBackground(new Color(100, 100, 100));
        memoList.setSelectionForeground(Color.WHITE);
        memoList.setFixedCellHeight(60);

        memoList.setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Memo memo = (Memo) value;

            String dateLabel = memo.getUpdatedAt() != null && !memo.getUpdatedAt().equals(memo.getCreatedAt())
            ? "最終更新日: " + memo.getUpdatedAt()
            : "作成日: " + memo.getCreatedAt();

            label.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
            label.setText("<html><b>" + memo.getTitle() + " </b><br><span style='color:gray'> " + memo.getTags() + "  " + dateLabel+ "</span></html>");

            label.setBorder(new EmptyBorder(12, 10, 20, 8));

            return label;
            }
        });

        JScrollPane scroll = new JScrollPane(memoList);
        scroll.setPreferredSize(new Dimension(500, 0));
        
        //メモ一覧パネルの設定
        JPanel memoPanel = new JPanel(new BorderLayout());
        JLabel memoLabel = new JLabel("メモ一覧");
        memoLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 16));
        memoLabel.setForeground(new Color(90, 90, 90));              
        memoLabel.setBorder(new EmptyBorder(10, 0, 10, 0));         
        memoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        memoPanel.add(memoLabel, BorderLayout.NORTH);
        memoPanel.add(scroll, BorderLayout.CENTER);
        
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BorderLayout());
        centerWrapper.add(topPanel, BorderLayout.NORTH); //  ボタン群
        centerWrapper.add(memoPanel, BorderLayout.CENTER); // メモ一覧

        add(logoLabel, BorderLayout.NORTH); 
        add(centerWrapper, BorderLayout.CENTER); // 


        // フレームの背景色とコンポーネントの色設定
        Color background = new Color(0, 0, 0);     // 黒っぽいグレー
        /*Color foreground = new Color(220, 220, 220);  // 明るい文字色
        getContentPane().setBackground(background); // フレームの背景

        topPanel.setBackground(background);
        topPanel.setForeground(foreground);
        memoPanel.setBackground(background);
        memoPanel.setForeground(foreground);
        scroll.setBackground(background);
        memoList.setBackground(background);
        memoList.setForeground(foreground);

        addMemoButton.setBackground(new Color(64,64,64));
        addMemoButton.setForeground(foreground);

        tagLabel.setForeground(new Color(220, 220, 220));
        tagCombo.setBackground(new Color(50,50,50));
        tagCombo.setForeground(foreground);

        searchField.setBackground(background);
        searchField.setForeground(foreground);
        searchButton.setBackground(new Color(64,64,64));
        searchButton.setForeground(foreground);

        memoLabel.setForeground(new Color(220, 220, 220));*/
        getContentPane().setBackground(background);


        /**
         * 各イベントの設定
         */

        //メモ追加
        addMemoButton.addActionListener(e -> {
            MemoAdd add = new MemoAdd(this, manager, model);
            add.setVisible(true);

            refreshTagComboBox(); 
        });

        //タグ検索
        tagCombo.setSelectedIndex(0);
        model.clear();
        manager.getAll().forEach(model::addElement);

        tagCombo.addActionListener(e -> {
            String selectedTag = (String) tagCombo.getSelectedItem();
            model.clear();
            
            // タグが選択された場合はそのタグに一致するメモを表示
            if (selectedTag != null && !selectedTag.equals("すべて表示")) {
                String tag = selectedTag.trim();
                List<Memo> filtered = manager.getAll().stream()
                .filter(m -> m.getTags().stream()
                    .anyMatch(t -> t.equalsIgnoreCase(tag)))
                    .toList();
                filtered.forEach(model::addElement);

            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "一致するメモが見つかりません", "検索結果", JOptionPane.INFORMATION_MESSAGE);
            }
            } else {
                manager.getAll().forEach(model::addElement);
            }
        });

        // メモ検索
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            String selectedTag = (String) tagCombo.getSelectedItem();
            model.clear();

            List<Memo> filtered = manager.getAll().stream()
            .filter(memo -> {
                boolean matchKeyword = keyword.isEmpty() ||
                    memo.getTitle().toLowerCase().contains(keyword) ||
                    memo.getBody().toLowerCase().contains(keyword);

                boolean matchTag = selectedTag.equals("すべて表示") ||
                    memo.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(selectedTag));

                return matchKeyword && matchTag;
            })
            .toList();
            filtered.forEach(model::addElement);

            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "一致するメモが見つかりません", "検索結果", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        //メモ詳細表示
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

    // タグコンボボックスを更新
    public void refreshTagComboBox() {
        Set<String> tags = manager.getAllTags();
        List<String> tagList = new ArrayList<>(tags);
        tagList.add(0, "すべて表示");

        tagCombo.removeAllItems();
        for (String tag : tagList) {
            tagCombo.addItem(tag);
        }
    }
    
    //メモの詳細を表示するダイアログを作成
    public void showMemoDetails(Memo memo) {
        
        // メモのタイトル、タグ、日付情報を表示するためのJLabelを作成
        JLabel titleLabel = new JLabel(memo.getTitle());
        titleLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 17));

        JLabel tagLabel = new JLabel(String.join(", ", memo.getTags()));
        tagLabel.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));

        String dateLabel = memo.getUpdatedAt() != null && !memo.getUpdatedAt().equals(memo.getCreatedAt())
        ? "最終更新日: " + memo.getUpdatedAt()
        : "作成日: " + memo.getCreatedAt();
        JLabel dateInfo = new JLabel(dateLabel);
        dateInfo.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
 
        // メモの詳細を表示するためのJTextAreaを作成
        StringBuilder sb = new StringBuilder();
        sb.append(memo.getBody());

        JTextArea detailsArea = new JTextArea(sb.toString(), 15, 40);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        detailsArea.setBorder(BorderFactory.createLineBorder(new Color(180,180,180)));
        detailsArea.setBackground(new Color(245,245,245));

        JScrollPane scrollPane = new JScrollPane(detailsArea);

        // タイトル、タグ、日付情報を表示するパネルを作成
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBorder(new EmptyBorder(10, 10, 5, 10));
        dateInfo.setForeground(new Color(100, 100, 100));
        infoPanel.add(titleLabel);
        infoPanel.add(tagLabel);
        infoPanel.add(dateInfo);
        
        // 操作ボタン（編集・削除）
        JButton editButton = new JButton("編集");
        JButton deleteButton = new JButton("削除");
        JButton doneButton = new JButton("閉じる");

        // ボタンのスタイル設定
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        doneButton.setPreferredSize(new Dimension(80, 25));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(doneButton);

        // 全体パネル
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialogPanel.add(infoPanel, BorderLayout.NORTH);

        // ダイアログの設定
        JDialog dialog = new JDialog(this, "メモの詳細", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setContentPane(dialogPanel);

        //編集モードで呼び出し
        editButton.addActionListener(e -> {
            MemoAdd editDialog = new MemoAdd(this, manager, model, memo);
            dialog.dispose();
            editDialog.setVisible(true);
        });

        //削除モードで呼び出し
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "このメモを削除しますか？", "確認", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.delete(memo);
                model.removeElement(memo);
                refreshTagComboBox(); 
                dialog.dispose();
            }
        });

        // 閉じるボタン
        doneButton.addActionListener(e -> {
            dialog.dispose();
        });
        dialog.setVisible(true);
    }
}
