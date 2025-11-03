package memo;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import storage.MemoRepository;

/**
 * アプリケーションのビジネスロジックを担当するクラスです。
 * UI（GUI）とデータ永続化層（MemoRepository）の間に位置し、
 * メモの追加、検索、更新、削除などの操作を調整します。
 * このクラスは特定のUI技術やデータ保存技術に依存しません。
 */
public class MemoManager {

    private final MemoRepository repository;

    /**
     * MemoManagerのコンストラクタです。
     * 依存性の注入（DI）パターンに基づき、データアクセスを担当するリポジトリを受け取ります。
     *
     * @param repository MemoRepositoryの実装インスタンス。
     */
    public MemoManager(MemoRepository repository) {
        this.repository = repository;
    }

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
        String normalizedTag = tag.replaceFirst("^#", "").trim();
        return repository.findByTag(normalizedTag);
    }

    /**
     * データベース内のすべてのメモから、ユニークなタグの一覧を取得。
     * 結果はアルファベット順にソートされた状態で返されます。
     *
     * @return すべてのユニークなタグを含むSet。
     */
    public Set<String> getAllTags() {
        return repository.getAll().stream()
                .flatMap(memo -> memo.getTags().stream())
                .filter(tag -> tag != null && !tag.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * 既存のメモの内容を更新します。
     *
     * @param memo 更新情報を含むMemoオブジェクト。
     */
    public void update(Memo memo) {
        repository.update(memo);
    }

    /**
     * キーワードとタグに基づいてメモ一覧をフィルタリングします。
     * UI層からビジネスロジックを分離するために、このメソッドで絞り込み処理を一元管理します。
     *
     * @param keyword     検索キーワード。空の場合はキーワードでの絞り込みは行いません。
     * @param selectedTag 選択されたタグ。「すべて表示」が選択されている場合はタグでの絞り込みは行いません。
     * @return フィルタリングされたMemoオブジェクトのリスト。
     */
    public List<Memo> filterMemos(String keyword, String selectedTag) {
        String normalizedKeyword = keyword.trim().toLowerCase();
        boolean isTagFiltered = selectedTag != null && !"すべて表示".equals(selectedTag);

        return getAll().stream()
                .filter(memo -> {
                    // キーワードに一致するかどうか
                    boolean matchKeyword = normalizedKeyword.isEmpty() ||
                            memo.getTitle().toLowerCase().contains(normalizedKeyword) ||
                            memo.getBody().toLowerCase().contains(normalizedKeyword);

                    // 選択されたタグに一致するかどうか
                    boolean matchTag = !isTagFiltered ||
                            memo.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(selectedTag));

                    return matchKeyword && matchTag;
                })
                .collect(Collectors.toList());
    }
}
