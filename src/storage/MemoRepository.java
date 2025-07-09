package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import memo.Memo;

/**
 * メモデータの永続化を担当するリポジトリクラスです。
 * SQLiteデータベースと直接やり取りを行い、メモのCRUD（作成、読み取り、更新、削除）処理を実装します。
 * SQLの実行やリソース管理は、このクラス内で完結します。
 */
public class MemoRepository {

    // SQLiteデータベースへの接続URL。プロジェクトルートからの相対パスで指定します。
    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/src/storage/hashmemo.db";

    /**
     * MemoRepositoryのコンストラクタです。
     * クラスのインスタンス化時に、SQLiteのJDBCドライバをロードします。
     * ドライバが見つからない場合は、エラーメッセージを出力します。
     */
    public MemoRepository() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBCドライバのロードに失敗しました: " + e.getMessage());
        }
    }

    /**
     * 新しいメモをデータベースに保存します（INSERT）。
     * 作成日時はSQLの`datetime`関数を使い、データベース側で自動的に設定されます。
     *
     * @param memo 保存するMemoオブジェクト。
     * @throws DataAccessException データベースへの保存に失敗した場合。
     */
    public void save(Memo memo) {
        String sql = "INSERT INTO memos (title, body, tags, created_at) VALUES (?, ?, ?, datetime('now', 'localtime'))";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memo.getTitle());
            pstmt.setString(2, memo.getBody());
            // タグのリストをカンマ区切りの単一文字列に変換して保存
            pstmt.setString(3, String.join(",", memo.getTags()));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("メモの保存に失敗しました", e);
        }
    }

    /**
     * 既存のメモをデータベースで更新します（UPDATE）。
     * 更新日時もデータベース側で自動的に現在の時刻に設定されます。
     *
     * @param memo 更新するMemoオブジェクト。IDが必須です。
     * @throws DataAccessException データベースの更新に失敗した場合。
     */
    public void update(Memo memo) {
        String sql = "UPDATE memos SET title = ?, body = ?, tags = ?, updated_at = datetime('now', 'localtime') WHERE id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memo.getTitle());
            pstmt.setString(2, memo.getBody());
            pstmt.setString(3, String.join(",", memo.getTags()));
            pstmt.setInt(4, memo.getId());

            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                // 更新対象のレコードが存在しなかった場合
                System.out.println("更新対象のメモが見つかりませんでした（id: " + memo.getId() + "）");
            }

        } catch (SQLException e) {
            throw new DataAccessException("メモの更新に失敗しました", e);
        }
    }

    /**
     * SQLiteデータベースへの接続を確立します。
     *
     * @return データベース接続を表すConnectionオブジェクト。
     * @throws SQLException 接続に失敗した場合。
     */
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * タイトルまたは本文に指定されたキーワードが含まれるメモを検索します。
     * あいまい検索（LIKE句）を使用します。
     *
     * @param keyword 検索キーワード。
     * @return 条件に一致したMemoオブジェクトのリスト。
     * @throws DataAccessException 検索処理に失敗した場合。
     */
    public List<Memo> findByKeyword(String keyword) {
        List<Memo> list = new ArrayList<>();
        String sql = "SELECT id, title, body, tags, created_at, updated_at FROM memos WHERE title LIKE ? OR body LIKE ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            // 結果セットをループしてMemoオブジェクトに変換
            while (rs.next()) {
                list.add(createMemoFromResultSet(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("キーワード検索に失敗しました", e);
        }
        return list;
    }

    /**
     * 指定されたタグを持つメモを検索します。
     * まずLIKE句で候補を絞り込み、その後Java側で厳密なタグの一致を確認します。
     *
     * @param tag 検索するタグ。
     * @return 条件に一致したMemoオブジェクトのリスト。
     * @throws DataAccessException 検索処理に失敗した場合。
     */
    public List<Memo> findByTag(String tag) {
        List<Memo> list = new ArrayList<>();
        // まずはLIKE検索で、タグが含まれる可能性のあるレコードを絞り込む
        String sql = "SELECT id, title, body, tags, created_at, updated_at FROM memos WHERE tags LIKE ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + tag + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String tagsStr = rs.getString("tags");
                List<String> tags = parseTags(tagsStr);

                // 取得したタグリストに、検索対象のタグが厳密に含まれているかを確認
                if (tags.contains(tag)) {
                    list.add(createMemoFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("タグでの検索に失敗しました", e);
        }
        return list;
    }

    /**
     * データベースからすべてのメモを取得します（SELECT *）。
     *
     * @return データベース内の全Memoオブジェクトのリスト。存在しない場合は空のリストを返します。
     * @throws DataAccessException 取得処理に失敗した場合。
     */
    public List<Memo> getAll() {
        List<Memo> list = new ArrayList<>();
        String sql = "SELECT id, title, body, tags, created_at, updated_at FROM memos";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(createMemoFromResultSet(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("メモ一覧の取得に失敗しました", e);
        }

        return list;
    }

    /**
     * 指定されたメモをデータベースから削除します（DELETE）。
     *
     * @param memo 削除するMemoオブジェクト。IDが使用されます。
     * @return 削除が成功した場合はtrue、そうでなければfalse。
     * @throws DataAccessException 削除処理に失敗した場合。
     */
    public boolean delete(Memo memo) {
        String sql = "DELETE FROM memos WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memo.getId());
            int affected = pstmt.executeUpdate();
            return affected > 0; // 1行以上削除されたら成功

        } catch (SQLException e) {
            throw new DataAccessException("メモの削除に失敗しました", e);
        }
    }

    /**
     * ResultSetからMemoオブジェクトを生成するヘルパーメソッドです。
     * コードの重複を避けるために使用します。
     *
     * @param rs Memoデータを含むResultSetオブジェクト。
     * @return 生成されたMemoオブジェクト。
     * @throws SQLException ResultSetからのデータ取得に失敗した場合。
     */
    private Memo createMemoFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String body = rs.getString("body");
        String tagsStr = rs.getString("tags");
        String createdAt = rs.getString("created_at");
        String updatedAt = rs.getString("updated_at");

        List<String> tags = parseTags(tagsStr);

        return new Memo(id, title, body, tags, createdAt, updatedAt);
    }

    /**
     * カンマ区切りのタグ文字列を文字列のリストに変換します。
     *
     * @param tagsStr データベースから取得したカンマ区切りのタグ文字列。
     * @return タグのリスト。
     */
    private List<String> parseTags(String tagsStr) {
        if (tagsStr == null || tagsStr.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(tagsStr.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }
}
