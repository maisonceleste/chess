package clients;

import responseexception.ResponseException;

public interface Client {

    String eval(String input);

    String help();

    String quit() throws ResponseException;
}
