package model;

import java.util.Objects;

public class UserData {
    private final String username;
    private final String password;
    private final String email;

    public UserData(String username, String password, String email){
        this.username=username;
        this.password=password;
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    public boolean comparePassword(String comp) {
        return (Objects.equals(this.password, comp));
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserData userData)) {
            return false;
        }
        return Objects.equals(username, userData.username) && Objects.equals(password, userData.password) && Objects.equals(email, userData.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }
}
