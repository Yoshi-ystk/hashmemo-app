package storage;

/**
 * データベースアクセス中にエラーが発生した場合にスローされる実行時例外。
 * SQLExceptionをラップし、アプリケーション固有の例外として扱うために使用します。
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
