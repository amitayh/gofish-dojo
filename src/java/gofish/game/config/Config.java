package gofish.game.config;

import gofish.game.Engine;

public class Config {
    
    private int numHumanPlayers = 0;
    
    private int numComputerPlayers = 0;
    
    private boolean allowMutipleRequests = true;
    
    private boolean forceShowOfSeries = true;
    
    public void setNumHumanPlayers(int num) {
        numHumanPlayers = num;
    }

    public int getNumHumanPlayers() {
        return numHumanPlayers;
    }
    
    public void setNumComputerPlayers(int num) {
        numComputerPlayers = num;
    }

    public int getNumComputerPlayers() {
        return numComputerPlayers;
    }
    
    public int getTotalNumPlayers() {
        return numHumanPlayers + numComputerPlayers;
    }
    
    public void setAllowMutipleRequests(boolean flag) {
        allowMutipleRequests = flag;
    }
    
    public boolean getAllowMutipleRequests() {
        return allowMutipleRequests;
    }

    public void setForceShowOfSeries(boolean flag) {
        forceShowOfSeries = flag;
    }
    
    public boolean getForceShowOfSeries() {
        return forceShowOfSeries;
    }
    
    public void validate() throws ValidationException {
        int totalNumPlayers = getTotalNumPlayers();
        if (totalNumPlayers < Engine.MIN_NUM_PLAYERS) {
            throw new ValidationException("Not enough players");
        } else if (totalNumPlayers > Engine.MAX_NUM_PLAYERS) {
            throw new ValidationException("Too many players");
        }
    }

}
