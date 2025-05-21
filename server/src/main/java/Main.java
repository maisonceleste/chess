import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.Server;
import service.ChessService;

public class Main {
    public static void main(String[] args) {
        var port = 8080;
        DataAccess dataAccess = new MemoryDataAccess();
        var service = new ChessService(dataAccess);
        var server = new Server().run(port);
        port = server.port();
        System.out.printf("Server started on port %d with %s%n", port, dataAccess.getClass());
        return;

    }
}