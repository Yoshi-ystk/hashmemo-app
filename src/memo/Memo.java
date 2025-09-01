package memo;

import java.util.ArrayList;
import java.util.List;

/**
 * 1件のメモ情報を保持するデータクラス（POJO）です。
 * このクラスは、データベースの`memos`テーブルの1レコードに対応します。
 * メモのID、タイトル、本文、タグ、作成日時、更新日時を管理します。
 *
 * @apiNote 日時情報は現在String型で保持していますが、将来的には`java.time.LocalDateTime`など
 *          専用の型にすることで、より堅牢な設計になります。
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
        this.tags = new ArrayList<>(tags); // 防御的コピー
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
        this.tags = new ArrayList<>(tags); // 防御的コピー
    }

    /**
     * このメモに関連付けられたタグのリストを返します。
     * 外部からの変更を防ぐため、リストの防御的コピーを返します。
     *
     * @return タグのリスト。
     */
    public List<String> getTags() {
        return new ArrayList<>(tags); // 防御的コピー
    }

    /**
     * このメモに新しいタグのリストを設定します。
     * 外部の変更から影響を受けないよう、受け取ったリストの防御的コピーを格納します。
     *
     * @param tags 新しく設定するタグのリスト。
     */
    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags); // 防御的コピー
    }

    /**
     * メモのタイトルを返します。
     * @return メモのタイトル。
     */
    public String getTitle() {
        return title;
    }

    /**
     * メモのタイトルを設定します。
     * @param title 新しいタイトル。
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * メモの本文を返します。
     * @return メモの本文。
     */
    public String getBody() {
        return body;
    }

    /**
     * メモの本文を設定します。
     * @param body 新しい本文。
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * メモの一意なIDを返します。
     * @return メモのID。
     */
    public int getId() {
        return id;
    }

    /**
     * メモの作成日時を文字列として返します。
     * @return 作成日時。取得できない場合は"(未設定)"を返します。
     */
    public String getCreatedAt() {
        return createdAt != null ? createdAt : "(未設定)";
    }

    /**
     * メモの最終更新日時を文字列として返します。
     * @return 最終更新日時。取得できない場合は"(未設定)"を返します。
     */
    public String getUpdatedAt() {
        return updatedAt != null ? updatedAt : "(未設定)";
    }

    /**
     * メモの主要な情報を文字列として返します。デバッグやログ出力に利用できます。
     *
     * @return メモのタイトル、タグ、本文、作成・更新日時を含む文字列
     */
    @Override
    public String toString() {
        return "[タイトル] " + title + "\n[タグ] " + String.join(", ", tags) + "\n[本文] " + body +
                "\n[作成日時] " + getCreatedAt() + "\n[更新日時] " + getUpdatedAt();
    }
}