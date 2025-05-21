package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

}
