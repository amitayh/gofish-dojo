package gofish.game;

import gofish.game.card.Card;
import gofish.game.card.CardsCollection;
import gofish.game.config.Config;
import gofish.game.config.ValidationException;
import gofish.game.engine.AddPlayerException;
import gofish.game.engine.GameStatusException;
import gofish.game.engine.PlayerActionException;
import gofish.game.engine.StartGameException;
import gofish.game.event.AskCardEvent;
import gofish.game.event.CardMovedEvent;
import gofish.game.event.ChangeTurnEvent;
import gofish.game.event.Event;
import gofish.game.event.GoFishEvent;
import gofish.game.event.PlayerJoinEvent;
import gofish.game.event.PlayerOutEvent;
import gofish.game.event.StartGameEvent;
import gofish.game.player.ComputerPlayerObserver;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
import gofish.game.player.action.Action;
import gofish.game.player.action.AskCardAction;
import java.util.Observable;
import java.util.Set;

public class Engine extends Observable {
    
    final public static int COMPLETE_SERIES_SIZE = 4;
    
    final public static int MIN_NUM_PLAYERS = 3;
        
    final public static int MAX_NUM_PLAYERS = 6;

    final public static int MIN_NUM_CARDS = 28;
    
    public enum Status {
        IDLE,
        CONFIGURED,
        STARTED,
        ENDED
    }
    
    private Status status = Status.IDLE;
    
    private Config config;
    
    private PlayersList players;
    
    /**
     * Map containing all cards still available in the game
     * (cards that don't belong to complete series)
     */
    private CardsCollection availableCards = new CardsCollection();
    
    private int currentPlayerIndex = -1;
    
    public Engine() {
        // Add observer to make computer players play automatically on their turn
        addObserver(new ComputerPlayerObserver());
    }
    
    public void configure(Config config) throws GameStatusException, ValidationException {
        ensureStatus(Status.IDLE);
        config.validate();
        this.config = config;
        this.players = new PlayersList(config.getTotalNumPlayers());
        status = Status.CONFIGURED;
    }
    
    public void reset() {
        config = null;
        players = null;
        availableCards.clear();
        currentPlayerIndex = -1;
        status = Status.IDLE;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public Config getConfig() {
        return config;
    }
    
    public PlayersList getPlayers() {
        return players;
    }
    
    public Player getPlayer(Integer playerId) {
        return players.getPlayerById(playerId);
    }
    
    public Set<Card> findCards(String property) {
        return availableCards.getByProperty(property);
    }
    
    public void addPlayer(Player player) throws GameStatusException, AddPlayerException {
        ensureStatus(Status.CONFIGURED);
        
        String name = player.getName();
        if (name.isEmpty()) {
            throw new AddPlayerException("Empty player name");
        }
        if (players.containsName(name)) {
            throw new AddPlayerException("Duplicate player name '" + name + "'");
        }
        if (
            (player.isHuman() && players.getNumHumanPlayers() == config.getNumHumanPlayers()) ||
            (player.isComputer() && players.getNumComputerPlayers() == config.getNumComputerPlayers())
        ) {
            throw new AddPlayerException("Game is full");
        }
        
        players.add(player);
        dispatchEvent(new PlayerJoinEvent(player));
    }
    
    public boolean gameIsFull() {
        return (players.size() == config.getTotalNumPlayers());
    }
    
    public void startGame() throws GameStatusException, StartGameException {
        ensureStatus(Status.CONFIGURED);
        if (!gameIsFull()) {
            throw new StartGameException("Game is not full");
        }
        
        setAvailableCards();
        int numCards = availableCards.size();
        if (numCards < MIN_NUM_CARDS) {
            throw new StartGameException("Not enough cards (minimum: " +
                MIN_NUM_CARDS + ", actual: " + numCards + ")");
        }
        
        status = Status.STARTED;
        dispatchEvent(new StartGameEvent());
        
        nextTurn();
    }
    
    public void performPlayerAction(Action action) throws GameStatusException, PlayerActionException {
        ensureStatus(Status.STARTED);
        
        if (action.getPlayer() != getCurrentPlayer()) {
            throw new PlayerActionException("Only current player can perform actions");
        }
        
        if (action instanceof AskCardAction) {
            playTurn((AskCardAction) action);
        }
    }
    
    private void playTurn(AskCardAction action) throws PlayerActionException {
        if (!validateCardRequest(action)) {
            throw new PlayerActionException("Invalid card request");
        }
        
        boolean anotherTurn = false;

        // Check if player being asked has the requested card
        Player player = action.getPlayer();
        Player askFrom = action.getAskFrom();
        String cardName = action.getCardName();
        Card card = askFrom.getHand().getCard(cardName);
        dispatchEvent(new AskCardEvent(player, askFrom, cardName));
        if (card == null) {
            // GoFish!
            dispatchEvent(new GoFishEvent(askFrom, player));
        } else {
            // Give away card
            moveCard(player, askFrom, card);
            if (player.isPlaying() && players.size() > 1) {
                anotherTurn = config.getAllowMutipleRequests();
            }
        }

        if (!anotherTurn) {
            nextTurn();
        } else {
            dispatchEvent(new ChangeTurnEvent(player));
        }
    }
    
    private boolean validateCardRequest(AskCardAction action) {
        boolean result = false;
        
        // Check if requested card is in the game
        Card card = availableCards.getCard(action.getCardName());
        if (card != null) {
            // Check that the player is allowed to ask for this card
            CardsCollection hand = action.getPlayer().getHand();
            for (String property : card.getProperties()) {
                if (hand.hasSeries(property)) {
                    result = true;
                    break;
                }
            }
        }
        
        return result;
    }
    
    private void moveCard(Player from, Player to, Card card) {
//        from.removeCard(card);
//        to.addCard(card);
        dispatchEvent(new CardMovedEvent(from, to, card));
//        checkPlayer(from);
    }
    
    private void checkPlayer(Player player) {
        if (!player.isPlaying()) {
            // No cards left - player is out
            CardsCollection hand = player.getHand();
            availableCards.removeAll(hand);
            hand.clear();
            dispatchEvent(new PlayerOutEvent(player));
        }
    }
    
    private void nextTurn() {
        Player currentPlayer;
        do {
            // TODO: check endless loop
            // Cycle players who are still playing
            currentPlayerIndex = nextPlayerIndex();
            currentPlayer = getCurrentPlayer();
        } while (!currentPlayer.isPlaying());
        
        dispatchEvent(new ChangeTurnEvent(currentPlayer));
    }
    
    private int nextPlayerIndex() {
        return (currentPlayerIndex + 1) % players.size();
    }
    
    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    private void setAvailableCards() {
        for (Player player : players) {
            availableCards.addAll(player.getHand());
        }
    }
    
    private void ensureStatus(Status requiredStatus) throws GameStatusException {
        if (status != requiredStatus) {
            throw new GameStatusException();
        }
    }
    
    private void dispatchEvent(Event event) {
        setChanged();
        notifyObservers(event);
    }

}
