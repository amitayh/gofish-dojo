package gofish.game;

import gofish.game.card.Card;
import gofish.game.card.CardsCollection;
import gofish.game.card.Series;
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
import gofish.game.event.GameOverEvent;
import gofish.game.event.GoFishEvent;
import gofish.game.event.PlayerJoinEvent;
import gofish.game.event.PlayerOutEvent;
import gofish.game.event.QuitGameEvent;
import gofish.game.event.SeriesDroppedEvent;
import gofish.game.event.SkipTurnEvent;
import gofish.game.event.StartGameEvent;
import gofish.game.player.ComputerPlayerObserver;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
import gofish.game.player.action.Action;
import gofish.game.player.action.AskCardAction;
import gofish.game.player.action.DropSeriesAction;
import gofish.game.player.action.QuitGameAction;
import gofish.game.player.action.SkipTurnAction;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Engine extends Observable {
    
    final public static int COMPLETE_SERIES_SIZE = 4;
    
    final public static int MIN_NUM_PLAYERS = 3;
        
    final public static int MAX_NUM_PLAYERS = 6;

    final public static int MIN_NUM_CARDS = 28;
    
    final public static long AUTO_QUIT_DELAY = TimeUnit.MINUTES.toMillis(5);
    
    public enum Status {
        IDLE,
        CONFIGURED,
        STARTED,
        ENDED
    }
    
    private Status status = Status.IDLE;
    
    private Config config;
    
    private PlayersList players;
    
    private AutoQuitTask autoQuitTask;
    
    private Timer timer = new Timer(true);
    
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
        
        dispatchEvent(new StartGameEvent(players));
        status = Status.STARTED;
        
        nextTurn();
    }
    
    public void performPlayerAction(Action action) throws GameStatusException, PlayerActionException {
        ensureStatus(Status.STARTED);
        
        Player player = action.getPlayer();
        if (player != getCurrentPlayer()) {
            throw new PlayerActionException("Only current player can perform actions");
        }
        
        if (autoQuitTask != null && autoQuitTask.player == player) {
            restartAutoQuitTimer(player);
        }
        
        if (action instanceof AskCardAction) {
            askCard((AskCardAction) action);
        } else if (action instanceof DropSeriesAction) {
            dropSeries((DropSeriesAction) action);
        } else if (action instanceof SkipTurnAction) {
            skipTurn(player);
        } else if (action instanceof QuitGameAction) {
            quitGame(player);
            nextTurn();
        }
    }
    
    private void askCard(AskCardAction action) throws PlayerActionException {
        Player player = action.getPlayer();
        Player askFrom = action.getAskFrom();
        String cardName = action.getCardName();
        boolean anotherTurn = false;
        
        // Validate action
        if (config.getForceShowOfSeries() && player.hasCompleteSeries()) {
            throw new PlayerActionException("Must drop complete series before asking for cards");
        }
        if (!validateCardRequest(player, cardName)) {
            throw new PlayerActionException("Invalid card request");
        }
        
        dispatchEvent(new AskCardEvent(player, askFrom, cardName));
        
        // Check if player being asked has the requested card
        Card card = askFrom.getHand().getCard(cardName);
        if (card == null) {
            // GoFish!
            dispatchEvent(new GoFishEvent(askFrom, player));
        } else {
            // Give away card
            moveCard(askFrom, player, card);
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
    
    private boolean validateCardRequest(Player player, String cardName) {
        boolean result = false;
        
        // Check if requested card is in the game
        Card card = availableCards.getCard(cardName);
        if (card != null) {
            // Check that the player is allowed to ask for this card
            CardsCollection hand = player.getHand();
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
        from.removeCard(card);
        to.addCard(card);
        dispatchEvent(new CardMovedEvent(from, to, card));
        checkPlayer(from);
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
        if (countActivePlayers() > 1) {
            Player currentPlayer;
            do {
                // Cycle players who are still playing
                currentPlayerIndex = nextPlayerIndex();
                currentPlayer = getCurrentPlayer();
            } while (!currentPlayer.isPlaying());
            
            if (currentPlayer.isHuman()) {
                restartAutoQuitTimer(currentPlayer);
            } else {
                cancelAutoQuitTimer();
            }
            
            dispatchEvent(new ChangeTurnEvent(currentPlayer));
        } else {
            endGame();
        }
    }
    
    private void restartAutoQuitTimer(Player player) {
        cancelAutoQuitTimer();
        autoQuitTask = new AutoQuitTask(player);
        timer.schedule(autoQuitTask, AUTO_QUIT_DELAY);
    }
    
    private void cancelAutoQuitTimer() {
        if (autoQuitTask != null) {
            autoQuitTask.cancel();
            autoQuitTask = null;
        }
    }
    
    private void endGame() {
        Player winner = getWinner();
        for (Player player : players) {
            if (player.isPlaying()) {
                quitGame(player);
            }
        }
        dispatchEvent(new GameOverEvent(winner));
        status = Status.ENDED;
    }
    
    private Player getWinner() {
        return Collections.max(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                // Compare players by number of completed series
                return p1.getAllCompleteSeries().size() - p2.getAllCompleteSeries().size();
            }
        });
    }
    
    private int countActivePlayers() {
        int activePlayers = 0;
        for (Player player : players) {
            if (player.isPlaying()) {
                activePlayers++;
            }
        }
        return activePlayers;
    }
    
    private void dropSeries(DropSeriesAction action) throws PlayerActionException {
        Player player = action.getPlayer();
        Series series = action.getSeries();
        CardsCollection hand = player.getHand();
        Set<Card> cards = hand.getByProperty(series.getProperty());
        
        if (!cards.equals(series.getCards())) {
            throw new PlayerActionException("Player doesn't have all cards in series");
        }
        
        availableCards.removeAll(cards);
        player.dropCompleteSeries(series);
        dispatchEvent(new SeriesDroppedEvent(player, series));
    }
    
    private void skipTurn(Player player) {
        dispatchEvent(new SkipTurnEvent(player));
        nextTurn();
    }
    
    private void quitGame(Player player) {
        availableCards.removeAll(player.getHand());
        player.quitGame();
        dispatchEvent(new QuitGameEvent(player));
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
    
    private class AutoQuitTask extends TimerTask {
        
        private Player player;

        public AutoQuitTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            quitGame(player);
            nextTurn();
        }
        
    }

}
