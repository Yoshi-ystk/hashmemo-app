package ui;

import ui.MemoAdd;
import memo.Memo;
import memo.MemoManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;


public class MemoGui extends JFrame { 
    private MemoManager manager;
    private DefaultListModel<Memo> model = new DefaultListModel<>();

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
        
        //画面レイアウト　上部        
        JComboBox<String> tagCombo = new JComboBox<>(manager.getAllTags().toArray(new String[0]));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("検索");

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
    
    private void showMemoDetails(Memo memo) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("📌 タイトル: ").append(memo.getTitle()).append("\n\n")
          .append("🏷️ タグ: ").append(String.join(", ", memo.getTags())).append("\n\n")
          .append("📝 本文:\n").append(memo.getBody());

        JTextArea detailsArea = new JTextArea(sb.toString(), 15, 40);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Meiryo", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        
     // 操作ボタン（編集・削除）
        JButton editButton = new JButton("編集");
        JButton deleteButton = new JButton("削除");

        editButton.addActionListener(e -> {
            MemoAdd editDialog = new MemoAdd(this, manager, model, memo); // 編集モードで呼び出す
            editDialog.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "このメモを削除しますか？", "確認", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.delete(memo);
                model.removeElement(memo);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(this, dialogPanel, "メモの詳細", JOptionPane.INFORMATION_MESSAGE);
    }

/*    private void clearInput() {
        titleField.setText("");
        tagField.setText("");
    }*/
}