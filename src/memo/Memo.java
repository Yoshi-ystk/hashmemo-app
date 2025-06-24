package memo;

import java.util.List;

/**
 * 1件のメモ情報を保持するクラス
 */
public class Memo {
    private String title;
    private String body;
    private List<String> tags;

    public Memo(String title, String body, List<String> tags) {
        this.title = title;
        this.body = body;
        this.tags = tags;
    }

    // Getter / Setter を追加
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

    @Override
    public String toString() {
        return "[タイトル] " + title + "\n[タグ] " + tags + "\n[本文] " + body;
    }
}
