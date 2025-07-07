package memo;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import storage.MemoRepository;

/**
 * メモの追加・取得・検索・削除を管理するクラス（データはSQLiteと連携）
 */
public class MemoManager {
    private final MemoRepository repository = new MemoRepository();

    /**
     * メモを追加してDBに保存
     */
    public void add(Memo memo) {
        repository.save(memo);
    }

    /**
     * 全メモをDBから取得
     */
    public List<Memo> getAll() {
        return repository.getAll();
    }

    /**
     * 表示用の一覧などから選択されたインデックスに対応するメモを返す
     */
    public Memo getMemoByIndex(List<Memo> list, int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    /**
     * 指定したメモをDBから削除
     */
    public boolean delete(Memo memo) {
        return repository.delete(memo);
    }

    /**
     * タイトルまたは本文にキーワードが含まれるメモを検索（部分一致）
     */
    public List<Memo> search(String keyword) {
        return repository.findByKeyword(keyword);
    }

    /**
     * タグが部分一致するメモを検索
     */
    public List<Memo> searchByTag(String tag) {
        String normalizedTag = tag.replaceFirst("^#", "").trim();
        return repository.findByTag(normalizedTag);
    }

    /**
     * 全メモに含まれるタグを一覧として取得（重複排除・ソート済み）
     */
    public Set<String> getAllTags() {
        Set<String> allTags = new TreeSet<>();
        for (Memo memo : repository.getAll()) {
            allTags.addAll(memo.getTags());
        }
        return allTags;
    }

    /**
     * メモをDB上で更新
     */
    public void update(Memo memo) {
        repository.update(memo);
    }
}