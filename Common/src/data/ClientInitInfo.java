package data;

import util.ServerConstants;

/**
 * Created by Razi on 2/11/2015.
 */
public class ClientInitInfo {
    int turn;
    String[] teams;
    TeamInfo yourInfo;
    MapSize mapSize;
    int blockCoefficient = ServerConstants.BLOCK_HEIGHT_COEFFICIENT;

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setTeams(String[] teams) {
        this.teams = teams;
    }

    public void setYourInfo(TeamInfo yourInfo) {
        this.yourInfo = yourInfo;
    }

    public void setMapSize(MapSize mapSize) {
        this.mapSize = mapSize;
    }

    public int getTurn() {
        return turn;
    }

    public String[] getTeams() {
        return teams;
    }

    public TeamInfo getYourInfo() {
        return yourInfo;
    }

    public MapSize getMapSize() {
        return mapSize;
    }
}
