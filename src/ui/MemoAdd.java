package ui;

import ui.MemoGui;
import memo.MemoManager;
import memo.Memo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

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

public class MemoAdd extends JDialog {
    private JTextField titleField;
    private JTextField tagField;
    private JTextArea bodyArea;
    private JButton submitButton;
	
    public MemoAdd(JFrame owner, MemoManager manager, DefaultListModel<Memo> memoModel) {
        super(owner, "メモ追加", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        titleField = new JTextField();
        tagField = new JTextField();
        bodyArea = new JTextArea();
        bodyArea.setLineWrap(true);

        JPanel formPanel = new JPanel(new GridLayout(6, 1));
        formPanel.add(new JLabel("タイトル"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("タグ（カンマ区切り）"));
        formPanel.add(tagField);
        formPanel.add(new JLabel("本文"));
        formPanel.add(new JScrollPane(bodyArea));

        submitButton.setText("登録");
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String body = bodyArea.getText().trim();
            List<String> tags = Arrays.stream(tagField.getText().split(",")).map(String::trim).toList();
                        
            if (!title.isEmpty() && !body.isEmpty()) {
                Memo memo = new Memo(title, body, tags);
                manager.add(memo);
                memoModel.addElement(memo);
                dispose(); // 閉じる
            } else {
                JOptionPane.showMessageDialog(this, "タイトルと本文を入力してください", "エラー", JOptionPane.ERROR_MESSAGE);
            }
        });

        
        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }
    
    public MemoAdd(JFrame owner, MemoManager manager, DefaultListModel<Memo> model, Memo existingMemo) {
    	this(owner, manager, model);
    	
        setTitle("メモ編集");
        submitButton.setText("更新");
        titleField.setText(existingMemo.getTitle());
        tagField.setText(String.join(", ", existingMemo.getTags()));
        bodyArea.setText(existingMemo.getBody());

        for (ActionListener al : submitButton.getActionListeners()) {
            submitButton.removeActionListener(al);
        }

        submitButton.addActionListener(e -> {
            existingMemo.setTitle(titleField.getText().trim());
            existingMemo.setBody(bodyArea.getText().trim());
            existingMemo.setTags(Arrays.stream(tagField.getText().split(",")).map(String::trim).toList());
            model.removeElement(existingMemo); // リスト更新のため一度削除
            model.addElement(existingMemo);  
            dispose();
        });
    }
}

