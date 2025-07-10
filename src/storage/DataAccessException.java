package storage;

/**
 * データベースアクセス中にエラーが発生した場合にスローされる実行時例外です。
 * SQLExceptionをラップし、アプリケーション固有の例外として扱うために使用します。
 * これにより、データベース関連のエラーハンドリングを統一的に行うことができます。
 */
public class DataAccessException extends RuntimeException {

    /**
     * 指定された詳細メッセージと原因を持つ新しい実行時例外を構築します。
     *
     * @param message 詳細メッセージ（エラーの原因を示す）。
     * @param cause 原因（根本的な例外）。
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
