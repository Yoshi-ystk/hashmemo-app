package memo;

/**
 * 1件のメモ情報を保持するクラス
 */
public class Memo {
    private String title;
    private String content;

    public Memo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "タイトル: " + title + "\n内容: " + content;
    }
}
