import chess.*;
import org.eclipse.jetty.server.Server;

public class Main {
    public static void main(String[] args) {
        var port = 8080;
        var service = new PetService(dataAccess);
        var server = new PetServer(service).run(port);
        port = server.port();
        port = server.port();

    }
}