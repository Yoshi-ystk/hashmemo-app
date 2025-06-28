package memo;

import java.util.ArrayList;
import java.util.List;

/**
 * メモの追加・取得・検索・削除を管理するクラス
 */
public class MemoManager {
    private final List<Memo> memos = new ArrayList<>();

    // メモを追加
    public void add(Memo memo) {
        memos.add(memo);
    }

    // 全メモを取得
    public List<Memo> getAll() {
        return new ArrayList<>(memos);
    }

    //検索されたメモ一覧を取得
    public Memo getMemoByIndex(List<Memo> list, int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
            return null;
        }

    // 指定インデックスのメモを削除
    public boolean delete(int index) {
        if (index >= 0 && index < memos.size()) {
            memos.remove(index);
            return true;
        }
        return false;
    }

    // キーワードで本文またはタイトルを検索
    public List<Memo> search(String keyword) {
        List<Memo> results = new ArrayList<>();
        for (Memo memo : memos) {
            if (memo.getTitle().contains(keyword) || memo.getBody().contains(keyword)) {
                results.add(memo);
            }
        }
        return results;
    }

    // タグで検索
    public List<Memo> searchByTag(String tag) {
    String normalizedTag = tag.replaceFirst("^#", "").trim(); // #を除去して正規化

    List<Memo> results = new ArrayList<>();
    for (Memo memo : memos) {
        if (memo.getTags().contains(normalizedTag)) {
            results.add(memo);
        }
    }
    return results;
    }
}