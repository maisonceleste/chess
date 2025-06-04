import responseexception.ResponseException;

public class PlayClient implements Client{

    private final String serverUrl;
    private final ServerFacade server;
    private final Repl repl;

    public PlayClient(String serverUrl, Repl repl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    @Override
    public String eval(String input) {
        return "";
    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public String quit() throws ResponseException {
        return "quit";
    }
}
