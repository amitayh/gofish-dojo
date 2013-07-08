package gofish.game.card;

import java.util.Collection;

public class Card {
    
    private static int numInstances = 0;
    
    private final int id;
    
    private final String name;
    
    /**
     * Properties define which series does the card belong to.
     * For example, properties can be the array ["King", "Hearts"]
     * for a standard King of Hearts, or ["Winter"] for a card
     * belonging to a of seasons of the year deck
     */
    private final String[] properties;
    
    public Card(String name, String... properties) {
        id = ++numInstances;
        this.name = name;
        this.properties = properties;
    }
    
    public Card(String name, Collection<String> properties) {
        this(name, properties.toArray(new String[properties.size()]));
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String[] getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "Card{name=" + name + "}";
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + name.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Card other = (Card) obj;
        if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
}
