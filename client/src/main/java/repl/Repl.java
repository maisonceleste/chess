package repl;

import clients.CentralClient;
import clients.Client;
import clients.PlayClient;
import clients.PreLoginClient;
import responseexception.ResponseException;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Repl {

    private Client client;
    private State state;

    public static enum State{
        PRELOGIN,
        CENTRAL,
        PLAY
    }

    public Repl(String serverUrl){
        this.client = new PreLoginClient(serverUrl, this);
        this.state = State.PRELOGIN;
    }

    public void run(){
        System.out.println("Welcome to the Chess Server!");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    public void changeState (Repl.State newState, String serverUrl, String auth) throws ResponseException {
        switch (newState){
            case State.PRELOGIN -> this.client = new PreLoginClient(serverUrl, this);
            case State.CENTRAL -> this.client = new CentralClient(serverUrl, this, auth);
            case State.PLAY -> this.client = new PlayClient(serverUrl, this);
        }
        System.out.println(client.help());
    }

    public void notify(ServerMessage notification) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + notification.getMessage());
        printPrompt();
    }

    public Client getClient(){
        return client;
    }

}
