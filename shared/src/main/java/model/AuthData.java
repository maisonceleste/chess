package model;
import java.util.Objects;
import java.util.UUID;

public record AuthData (String authToken, String username){}
