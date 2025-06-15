package memo;

import java.util.ArrayList;
import java.util.List;

/**
 * MemoManager は、メモの追加・取得・削除・検索といった
 * 基本的な操作を管理するクラスです。
 * 
 * 現段階ではインメモリでの保持のみ対応しており、
 * データは実行中のみ有効です（v0.1）。
 */
public class MemoManager {
    // メモ一覧（実行中のみ保持）
    private final List<String> memos = new ArrayList<>();

    /**
     * 新しいメモを追加します。
     * 
     * @param memo 追加するメモの内容
     */
    public void add(String memo) {
        memos.add(memo);
    }

    /**
     * 現在登録されているすべてのメモを取得します。
     * 
     * @return メモの一覧（コピー）
     */
    public List<String> getAll() {
        return new ArrayList<>(memos);// 外部に影響を与えないようコピーを返す
    }

    /**
     * 指定したインデックスのメモを削除します。
     * 
     * @param index 削除したいメモのインデックス（0始まり）
     * @return 成功時は true、不正なインデックスの場合は false
     */
    public boolean delete(int index) {
        if (index >= 0 && index < memos.size()) {
            memos.remove(index);
            return true;
        }
        return false;
    }

    /**
     * 指定されたキーワードを含むメモを検索します。
     * 
     * @param keyword 検索対象のキーワード
     * @return キーワードを含むメモの一覧
     */
    public List<String> search(String keyword) {
        List<String> results = new ArrayList<>();
        for (String memo : memos) {
            if (memo.contains(keyword)) {
                results.add(memo);
            }
        }
        return results;
    }
}