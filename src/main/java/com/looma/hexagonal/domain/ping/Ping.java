package main.java.com.looma.hexagonal.domain.ping;

public final class Ping {
    private final String message;

    public Ping(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
