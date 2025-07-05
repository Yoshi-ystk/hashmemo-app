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
 * メモデータの永続化を担当するリポジトリクラス。
 * SQLiteを使って、メモの保存・更新・取得・削除を行う。
 */
public class MemoRepository {

    // SQLiteのデータベースURL
    // eclipsテスト用に一時的にコメントアウト
	// private static final String DB_URL = "jdbc:sqlite:src/storage/hashmemo.db";
	
	// eclipseテスト用コード
	private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/src/storage/hashmemo.db";
    
    /**
     * コンストラクタでJDBCドライバをロード
     */
    public MemoRepository() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBCドライバのロードに失敗しました: " + e.getMessage());
        }
    }

    /**
     * メモをデータベースに保存する（INSERT）
     * @param memo 保存対象のメモ
     */
    public void save(Memo memo) {
        String sql = "INSERT INTO memos (title, body, tags, created_at) VALUES (?, ?, ?, datetime('now', 'localtime'))";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memo.getTitle());
            pstmt.setString(2, memo.getBody());
            pstmt.setString(3, String.join(",", memo.getTags()));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("メモの保存に失敗しました: " + e.getMessage());
        }
    }

    /**
     * メモをデータベースで更新する（UPDATE）
     * @param memo 更新対象のメモ（idが必須）
     */
    public void update(Memo memo) {
        String sql = "UPDATE memos SET title = ?, body = ?, tags = ?, updated_at = datetime('now', 'localtime') WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memo.getTitle());
            pstmt.setString(2, memo.getBody());
            pstmt.setString(3, String.join(",", memo.getTags()));
            pstmt.setInt(4, memo.getId());

            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                System.out.println("更新対象のメモが見つかりませんでした（id: " + memo.getId() + "）");
            }

        } catch (SQLException e) {
            System.out.println("メモの更新に失敗しました: " + e.getMessage());
        }
    }

    /**
     * データベースから全メモを取得する（SELECT）
     * @return メモのリスト（存在しない場合は空リスト）
     */
    public List<Memo> getAll() {
        List<Memo> list = new ArrayList<>();
        String sql = "SELECT id, title, body, tags, created_at, updated_at FROM memos";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String body = rs.getString("body");
                String tagsStr = rs.getString("tags");
                String createdAt = rs.getString("created_at");
                String updatedAt = rs.getString("updated_at");

                List<String> tags = Arrays.stream(tagsStr.split(","))
                        .map(String::trim)
                        .filter(t -> !t.isEmpty())
                        .collect(Collectors.toList());

                Memo memo = new Memo(id, title, body, tags, createdAt, updatedAt);
                list.add(memo);
            }

        } catch (SQLException e) {
            System.out.println("メモ一覧の取得に失敗しました: " + e.getMessage());
        }

        return list;
    }

    /**
     * 指定されたメモをデータベースから削除する（DELETE）
     * @param memo 削除対象のメモ（idが必要）
     * @return 成功時は true、失敗時は false
     */
    public boolean delete(Memo memo) {
        String sql = "DELETE FROM memos WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memo.getId());
            int affected = pstmt.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            System.out.println("メモの削除に失敗しました: " + e.getMessage());
            return false;
        }
    }
}