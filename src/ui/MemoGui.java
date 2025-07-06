package ui;

import memo.Memo;
import memo.MemoManager;

import javax.swing.*;
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
        
        // コンボボックスの初期設定
        tagCombo = new JComboBox<>();
        refreshTagComboBox(); 

        // タイトルとタグの検索フィールドの設定
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("検索");
        JButton addMemoButton = new JButton("メモ追加");

        JLabel tagLabel = new JLabel("タグ:");
        topPanel.add(tagLabel);
        topPanel.add(tagCombo);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addMemoButton);
        add(topPanel, BorderLayout.NORTH);

        // メモ一覧表示の設定
        manager.getAll().forEach(model::addElement);
        JList<Memo> memoList = new JList<>(model);
        JScrollPane scroll = new JScrollPane(memoList);
        scroll.setPreferredSize(new Dimension(500, 0));
        
        //メモ一覧パネルの設定
        JPanel memoPanel = new JPanel(new BorderLayout());
        JLabel memoLabel = new JLabel("メモ一覧", SwingConstants.CENTER);
        memoPanel.add(memoLabel, BorderLayout.NORTH);
        memoPanel.add(scroll, BorderLayout.CENTER);
        add(memoPanel,BorderLayout.CENTER);


        // フレームの背景色とコンポーネントの色設定
        Color background = new Color(34, 34, 34);     // 黒っぽいグレー
        Color foreground = new Color(220, 220, 220);  // 明るい文字色
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

        searchField.setBackground(new Color(50,50,50));
        searchField.setForeground(foreground);
        searchButton.setBackground(new Color(64,64,64));
        searchButton.setForeground(foreground);

        memoLabel.setForeground(new Color(220, 220, 220));
        getContentPane().setBackground(new Color(34, 34, 34));


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
        
        // メモの詳細を表示するためのJTextAreaを作成
        StringBuilder sb = new StringBuilder();
        sb.append("タイトル: ").append(memo.getTitle()).append("\n\n")
          .append("タグ: ").append(String.join(", ", memo.getTags())).append("\n\n")
          .append("本文:\n").append(memo.getBody());

        // JTextAreaの設定
        JTextArea detailsArea = new JTextArea(sb.toString(), 15, 40);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Meiryo", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        
     // 操作ボタン（編集・削除）
        JButton editButton = new JButton("編集");
        JButton deleteButton = new JButton("削除");
        JButton doneButton = new JButton("閉じる");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(doneButton);

        // 全体パネル
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

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

/* 
getContentPane().getBackground(); // フレームの背景

topPanel.getBackground();
topPanel.getForeground();
memoPanel.getBackground();
memoPanel.getForeground();
scroll.getBackground();
memoList.getBackground();
memoList.getForeground();

addMemoButton.setBackground(new Color(64,64,64));
addMemoButton.getForeground();

tagCombo.setBackground(new Color(50,50,50));
tagCombo.getForeground();

searchField.setBackground(new Color(50,50,50));
searchField.getForeground();
searchButton.setBackground(new Color(64,64,64));
searchButton.getForeground();   */