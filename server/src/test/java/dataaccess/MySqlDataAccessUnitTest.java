package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responseexception.ResponseException;
import service.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MySqlDataAccessUnitTest {
    static final DataAccess TEST_DAO;
    static final DataAccess EXPTECTED_DAO;

    static {
      try{
          TEST_DAO= new MySqlDataAccess();
          EXPTECTED_DAO = new MemoryDataAccess();
      } catch (ResponseException e) {
          throw new RuntimeException(e);
      }
    }

    @BeforeEach
    void clear() throws ResponseException {
        TEST_DAO.deleteAll();
        EXPTECTED_DAO.deleteAll();
    }

    @Test
    void positiveCreateUser() throws ResponseException {
        UserData result = TEST_DAO.createUser("thisUsername", "thisPassword", "thisEmail");

        UserData expectedResult = EXPTECTED_DAO.createUser("thisUsername", "thisPassword", "thisEmail");

        assertEquals(expectedResult.username(), result.username());
    }

    @Test
    void negativeCreateUser() throws ResponseException {
        TEST_DAO.createUser("thisUsername", "thisPassword", "thisEmail");
        assertThrows(ResponseException.class, () -> TEST_DAO.createUser("evilUsername", null, "thisEmail"));
        assertThrows(ResponseException.class, () -> TEST_DAO.createUser("thisUsername", "evilPassword", "thisEmail"));

    }

    @Test
    void positiveGetUser() throws ResponseException {
        TEST_DAO.createUser("thisUsername", "thisPassword", "thisEmail");
        EXPTECTED_DAO.createUser("thisUsername", "thisPassword", "thisEmail");

        UserData result = TEST_DAO.getUser("thisUsername");
        UserData expectedResult = EXPTECTED_DAO.getUser("thisUsername");

        assertEquals(expectedResult.username(), result.username());
    }

    @Test
    void negativeGetUser() throws ResponseException {
        TEST_DAO.createUser("thisUsername", "thisPassword", "thisEmail");
        assertNull(TEST_DAO.getUser("fakeUser"));

    }

    @Test
    void positiveCreateAuth() throws ResponseException {
        AuthData result = TEST_DAO.createAuth("thisUsername");
        AuthData expectedResult = EXPTECTED_DAO.createAuth("thisUsername");
        assertEquals(expectedResult.username(), result.username());
    }

    @Test
    void negativeCreateAuth() throws ResponseException {
        assertThrows(ResponseException.class, () -> TEST_DAO.createAuth(null));
    }

    @Test
    void positiveGetAuth() throws ResponseException {
        String testAuthID = TEST_DAO.createAuth("thisUsername").authToken();
        String expectedAuthID = EXPTECTED_DAO.createAuth("thisUsername").authToken();

        AuthData result = TEST_DAO.getAuth(testAuthID);
        AuthData expectedResult = EXPTECTED_DAO.getAuth(expectedAuthID);

        assertEquals(expectedResult.username(), result.username());
    }

    @Test
    void negativeGetAuth() throws ResponseException {
        TEST_DAO.createAuth("thisUsername");
        assertNull(TEST_DAO.getAuth("fakeAuthID"));

    }

    @Test
    void positiveDeleteAuth() throws ResponseException {
        String testAuthID = TEST_DAO.createAuth("thisUsername").authToken();

        TEST_DAO.deleteAuth(testAuthID);

        assertNull(TEST_DAO.getAuth(testAuthID));
    }

    @Test
    void negativeDeleteAuth() throws ResponseException {
        String testAuthID = TEST_DAO.createAuth("thisUsername").authToken();

        TEST_DAO.deleteAuth("differentAuthID");

        assertNotNull(TEST_DAO.getAuth(testAuthID));
    }

    @Test
    void positiveCreateGame() throws ResponseException {
        GameData result = TEST_DAO.createGame("testGame");
        GameData expectedResult = EXPTECTED_DAO.createGame("testGame");
        assertEquals(expectedResult.gameName(), result.gameName());
    }

    @Test
    void negativeCreateGame() throws ResponseException {
        assertThrows(ResponseException.class, () -> TEST_DAO.createGame(null));
    }

    @Test
    void positiveGetGame() throws ResponseException {
        GameData expectedResult = TEST_DAO.createGame("testGame");
        GameData result = TEST_DAO.getGame(expectedResult.gameID());
        assertEquals(expectedResult, result);
    }

    @Test
    void negativeGetGame() throws ResponseException {
        TEST_DAO.createGame("testGame");
        assertNull(TEST_DAO.getGame(0));
    }

    @Test
    void positiveListGames() throws ResponseException {
        TEST_DAO.createGame("testGame1");
        TEST_DAO.createGame("testGame2");
        EXPTECTED_DAO.createGame("testGame1");
        EXPTECTED_DAO.createGame("testGame2");

        ArrayList<GameData> result = TEST_DAO.listGames();
        ArrayList<GameData> expectedResult = EXPTECTED_DAO.listGames();
        assertEquals(expectedResult, result);
    }

    @Test
    void negativeListGames() throws ResponseException {
        assertEquals(0, TEST_DAO.listGames().size());
    }

    @Test
    void positiveUpdateGame() throws ResponseException {
        GameData originalGame = TEST_DAO.createGame("testGame");
        GameData result = TEST_DAO.updateGame("BLACK", originalGame.gameID(), "player");
        assertEquals("player", result.blackUsername());
    }

    @Test
    void negativeUpdateGame() throws ResponseException {
        GameData originalGame = TEST_DAO.createGame("testGame");
        GameData result = TEST_DAO.updateGame("BLACK", 0 , "player");
        assertNull(result);
    }

    @Test
    void deleteAllTest() throws ResponseException {
        AuthData authID = TEST_DAO.createAuth("thisUsername");
        UserData user = TEST_DAO.createUser("thisUsername", "thisPassword", "thisEmail");
        GameData game = TEST_DAO.createGame("testGame");
        TEST_DAO.deleteAll();
        assertNull(TEST_DAO.getAuth(authID.authToken()));
        assertNull(TEST_DAO.getUser(user.username()));
        assertNull(TEST_DAO.getGame(game.gameID()));

    }
}
