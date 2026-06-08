package pt.ipvc.estg.desktop.api;

public class ApiException extends RuntimeException {

    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isConnectionError() {
        return statusCode == 0;
    }
}
