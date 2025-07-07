package memo;

import java.util.List;

/**
 * 1件のメモ情報を保持するクラス。
 * データベースの「memos」テーブル1行に相当。
 */
public class Memo {
    private int id; // データベースの主キー（自動採番）
    private String title; // メモのタイトル
    private String body; // メモの本文
    private List<String> tags; // メモに付けられたタグ（カンマ区切り）
    private String createdAt; // 作成日時（DB側で自動設定）
    private String updatedAt; // 最終更新日時（編集時に更新）

    /**
     * データベースから取得したときに使用するコンストラクタ
     */
    public Memo(int id, String title, String body, List<String> tags, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    
    /**
     * 新規メモ作成時に使用するコンストラクタ（id, createdAt, updatedAt はDBで自動設定）
     */
    public Memo(String title, String body, List<String> tags) {
        this.title = title;
        this.body = body;
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean hasTag(String keyword) {
        return tags.contains(keyword);
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt != null ? createdAt : "(未設定)";
    }

    public String getUpdatedAt() {
        return updatedAt != null ? updatedAt : "(未設定)";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * メモの概要を文字列で表示（デバッグ・ログ用）
     */
    @Override
    public String toString() {
        return "[タイトル] " + title + "\n[タグ] " + tags + "\n[本文] " + body +
                "\n[作成日時] " + getCreatedAt() + "\n[更新日時] " + getUpdatedAt();
    }
}