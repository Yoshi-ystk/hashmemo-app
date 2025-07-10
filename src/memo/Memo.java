package memo;

import java.util.List;

/**
 * 1件のメモ情報を保持するデータクラス（POJO）です。
 * このクラスは、データベースの`memos`テーブルの1レコードに対応します。
 * メモのID、タイトル、本文、タグ、作成日時、更新日時を管理します。
 */
public class Memo {
    /**
     * データベースで自動採番される一意のID。
     */
    private int id;

    /**
     * メモのタイトル。
     */
    private String title;

    /**
     * メモの本文。
     */
    private String body;

    /**
     * メモに関連付けられたタグのリスト。
     */
    private List<String> tags;

    /**
     * メモの作成日時。データベースによって自動的に設定されます。
     */
    private String createdAt;

    /**
     * メモの最終更新日時。メモが更新されるたびに更新されます。
     */
    private String updatedAt;

    /**
     * データベースから取得したデータを使ってMemoオブジェクトを生成するコンストラクタです。
     *
     * @param id        メモのID
     * @param title     メモのタイトル
     * @param body      メモの本文
     * @param tags      関連付けられたタグのリスト
     * @param createdAt 作成日時
     * @param updatedAt 最終更新日時
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
     * 新しいメモをアプリケーションで作成する際に使用するコンストラクタです。
     * ID、作成日時、更新日時はデータベース側で設定されるため、ここでは初期化しません。
     *
     * @param title メモのタイトル
     * @param body  メモの本文
     * @param tags  関連付けられたタグのリスト
     */
    public Memo(String title, String body, List<String> tags) {
        this.title = title;
        this.body = body;
        this.tags = tags;
    }

    // 以下、各フィールドのゲッターおよびセッター

    public List<String> getTags() {
        return tags;
    }

    /**
     * このメモが指定されたタグを持っているかどうかを判定します。
     *
     * @param keyword 確認したいタグ文字列
     * @return タグが存在すればtrue、そうでなければfalse
     */
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
     * メモの主要な情報を文字列として返します。デバッグやログ出力に利用できます。
     *
     * @return メモのタイトル、タグ、本文、作成・更新日時を含む文字列
     */
    @Override
    public String toString() {
        return "[タイトル] " + title + "\n[タグ] " + tags + "\n[本文] " + body +
                "\n[作成日時] " + getCreatedAt() + "\n[更新日時] " + getUpdatedAt();
    }
}
