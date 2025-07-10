package memo;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import storage.MemoRepository;

/**
 * アプリケーションのビジネスロジックを担当するクラスです。
 * UI（CLIやGUI）とデータ永続化層（MemoRepository）の間に位置し、
 * メモの追加、検索、更新、削除などの操作を調整します。
 */
public class MemoManager {
    // データアクセスを担当するリポジトリのインスタンス
    private final MemoRepository repository = new MemoRepository();

    /**
     * 新しいメモを受け取り、リポジトリを介してデータベースに保存します。
     *
     * @param memo 保存する新しいMemoオブジェクト。
     */
    public void add(Memo memo) {
        repository.save(memo);
    }

    /**
     * データベースに保存されているすべてのメモを取得します。
     *
     * @return すべてのMemoオブジェクトを含むリスト。
     */
    public List<Memo> getAll() {
        return repository.getAll();
    }

    /**
     * メモのリストとインデックスを受け取り、指定された位置のメモを返します。
     * UIでユーザーが選択した項目を特定するのに役立ちます。
     *
     * @param list  メモのリスト。
     * @param index 取得したいメモのインデックス（0から始まる）。
     * @return 指定されたインデックスのMemoオブジェクト。インデックスが無効な場合はnull。
     */
    public Memo getMemoByIndex(List<Memo> list, int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    /**
     * 指定されたメモをデータベースから削除します。
     *
     * @param memo 削除するMemoオブジェクト。
     * @return 削除が成功した場合はtrue、失敗した場合はfalse。
     */
    public boolean delete(Memo memo) {
        return repository.delete(memo);
    }

    /**
     * タイトルまたは本文にキーワードが含まれるメモを検索します。
     * 検索ロジックはリポジトリに委譲します。
     *
     * @param keyword 検索キーワード。
     * @return 検索条件に一致したMemoオブジェクトのリスト。
     */
    public List<Memo> search(String keyword) {
        return repository.findByKeyword(keyword);
    }

    /**
     * 指定されたタグを持つメモを検索します。
     * タグの正規化（`#`の削除、空白のトリム）を行った後、リポジトリに検索を委譲します。
     *
     * @param tag 検索するタグ文字列。
     * @return 検索条件に一致したMemoオブジェクトのリスト。
     */
    public List<Memo> searchByTag(String tag) {
        // タグの先頭にある`#`や前後の空白を削除して、正規化する
        String normalizedTag = tag.replaceFirst("^#", "").trim();
        return repository.findByTag(normalizedTag);
    }

    /**
     * データベース内のすべてのメモから、ユニークなタグの一覧を取得します。
     * 結果はソートされた状態で返されます。
     *
     * @return すべてのユニークなタグを含むSet。
     */
    public Set<String> getAllTags() {
        // TreeSetを使うことで、自動的にソートされ、重複も排除される
        Set<String> allTags = new TreeSet<>();
        for (Memo memo : repository.getAll()) {
            allTags.addAll(memo.getTags());
        }
        return allTags;
    }

    /**
     * 既存のメモの内容を更新します。
     *
     * @param memo 更新情報を含むMemoオブジェクト。
     */
    public void update(Memo memo) {
        repository.update(memo);
    }
}
