package model;
import java.util.Objects;
import java.util.UUID;

public class AuthData {
    private final String authToken;
    private final String username;

    public AuthData(String username){
        this.username=username;
        this.authToken = UUID.randomUUID().toString();
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthData authData)) {
            return false;
        }
        return Objects.equals(authToken, authData.authToken) && Objects.equals(username, authData.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }
}
