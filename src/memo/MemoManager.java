package memo;

import java.util.ArrayList;
import java.util.List;

/**
 * メモの追加・取得・検索・削除を管理するクラス
 */
public class MemoManager {
    private final List<Memo> memos = new ArrayList<>();

    // メモを追加（タイトルと本文の両方を受け取る）
    public void add(String title, String content) {
        memos.add(new Memo(title, content));
    }

    // 全メモを取得
    public List<Memo> getAll() {
        return new ArrayList<>(memos);
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
            if (memo.getTitle().contains(keyword) || memo.getContent().contains(keyword)) {
                results.add(memo);
            }
        }
        return results;
    }
}