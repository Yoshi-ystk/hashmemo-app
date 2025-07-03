package storage;

// SQLite用のクラスを読み込む
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MemoRepository {

    // SQLiteデータベースへのパスを定数として定義
    // ファイルの場所は src/storage/memos.db
    private static final String DB_URL = "jdbc:sqlite:src/storage/memos.db";

    /**     
     * データベースに接続するメソッド
     * 接続が成功すると Connection オブジェクトを返す
     * 失敗した場合は null を返す
     */
    public Connection connect() {
        try {
            // DriverManager を使って SQLite に接続
            Connection conn = DriverManager.getConnection(DB_URL);

            // 接続成功時の確認メッセージ
            System.out.println("SQLite に接続しました。");
            return conn;
        } catch (SQLException e) {
            // 接続失敗時のエラーメッセージ表示
            System.out.println("データベース接続に失敗しました: " + e.getMessage());
            return null;
        }
    }
}
