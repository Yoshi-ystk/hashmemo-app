package ui;

import memo.Memo;
import memo.MemoManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MemoGui extends JFrame { 
    private MemoManager manager;
    private DefaultListModel<Memo> model = new DefaultListModel<>();
    private JComboBox<String> tagCombo;

    public MemoGui(MemoManager manager) {
        this.manager = manager;
        setTitle("hashmemo");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
  
        //  ヘッダー（上部ボタン）
        
        //メモ追加イベント
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addMemoButton = new JButton("メモ追加");
        addMemoButton.addActionListener(e -> {
            MemoAdd add = new MemoAdd(this, manager, model);
            add.setVisible(true);
        });
        
        // 全てのタグを取得してコンボボックスにセット
        Set<String> tags = manager.getAllTags();
        List<String> tagList = new ArrayList<>(tags);
        tagList.add(0, "すべて表示");
        tagCombo = new JComboBox<>(tagList.toArray(new String[0])); 
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("検索");

        //画面レイアウト　上部
        topPanel.add(addMemoButton);
        topPanel.add(new JLabel("タグ:"));
        topPanel.add(tagCombo);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // メモ一覧表示
        manager.getAll().forEach(model::addElement);
        JList<Memo> memoList = new JList<>(model);
        JScrollPane scroll = new JScrollPane(memoList);
        scroll.setPreferredSize(new Dimension(500, 0));
        
        //画面レイアウト　メモ一覧
        JPanel memoPanel = new JPanel(new BorderLayout());
        memoPanel.add(new JLabel("メモ一覧", SwingConstants.CENTER), BorderLayout.NORTH);
        memoPanel.add(scroll, BorderLayout.CENTER);
        add(memoPanel,BorderLayout.CENTER);

        //タグ検索前は全て表示
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
        
        //メモ詳細表示イベント
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
    
    //メモの詳細を表示するダイアログを作成
    public void showMemoDetails(Memo memo) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("タイトル: ").append(memo.getTitle()).append("\n\n")
          .append("タグ: ").append(String.join(", ", memo.getTags())).append("\n\n")
          .append("本文:\n").append(memo.getBody());

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
            editDialog.setVisible(true);
            dialog.dispose();
        });

        //削除モードで呼び出し
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "このメモを削除しますか？", "確認", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.delete(memo);
                model.removeElement(memo);
                dialog.dispose();
            }
        });

        doneButton.addActionListener(e -> {
            dialog.dispose();
        });
        dialog.setVisible(true);
    }
}