package memo;

import java.util.ArrayList;
import java.util.List;

public class MemoManager {
    private final List<String> memos = new ArrayList<>();

    public void add(String memo) {
        memos.add(memo);
    }

    public List<String> getAll() {
        return new ArrayList<>(memos);
    }

    public boolean delete(int index) {
        if (index >= 0 && index < memos.size()) {
            memos.remove(index);
            return true;
        }
        return false;
    }

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