package facade;

import requests.CreateRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.RegisterResult;
import com.google.gson.Gson;
import model.GameData;
import responseexception.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult registerUser(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, request, null, RegisterResult.class);
    }

    public LoginResult loginUser(LoginRequest request) throws ResponseException{
        var path = "/session";
        return this.makeRequest("POST", path, request, null, LoginResult.class);
    }

    public void logoutUser(String authID) throws ResponseException{
        var path = "/session";
        this.makeRequest("DELETE", path, null, authID, null);
    }

    public CreateResult createGame(String gameName, String authID) throws ResponseException{
        var path = "/game";
        CreateRequest request= new CreateRequest(gameName, authID);
        return this.makeRequest("POST", path, request, authID, CreateResult.class);
    }

    public ListResult listGames(String authID) throws ResponseException{
        var path = "/game";
        return this.makeRequest("GET", path, null, authID, ListResult.class);
    }

    public GameData joinGame(JoinRequest request) throws ResponseException{
        var path = "/game";
        return this.makeRequest("PUT", path, request, request.authID(), GameData.class);
    }

    private <T> T makeRequest(String method, String path, Object request, String authID, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authID != null) {
                http.setRequestProperty("Authorization", authID);
            }
            if (request != null && (method.equals("POST") || method.equals("PUT"))) {
                http.setDoOutput(true);
                writeBody(request, http);
            }
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }

    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
