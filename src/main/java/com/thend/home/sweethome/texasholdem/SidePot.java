package com.thend.home.sweethome.texasholdem;

import java.util.ArrayList;
import java.util.List;

public class SidePot {

    private   PokerMoney   pot;                  // Amount of money in this side pot
    private   List<Long> included;             // List of player names eligble to win this pot
    private   List<Long> excluded;             // List of player names not-elible to win this pot.
    public    PokerMoney[] toCall;               // Amount required to call this side pot.

    
	//----------------------
	//Constructor
	//
    public SidePot( List<Long> in, List<Long> out,
    		float amount, int playerSize, int currPlayerIndex) {
        included = in;
        excluded = out;
        pot = new PokerMoney(amount);
        toCall = new PokerMoney[playerSize];
        for ( int i = 0; i < playerSize; i++ ) {
            toCall[i] = new PokerMoney(amount);
        }
        toCall[currPlayerIndex] = new PokerMoney();
    }

	//----------------------
	//addMoney() is called to increase the amount of money in this side pot
	//
    public void addMoney(float amount) {
        pot.add(amount);
    }

	//----------------------
	//remove() is used to remove a player from the pot (if they folded or quit or something)
	//
    public boolean remove(String name) { 
        boolean res1 = included.remove( name );
        boolean res2 = excluded.remove( name );
        return ( res1 || res2 );
    }

	//----------------------
	//getPot() is used to access the private class variable
	//
    public PokerMoney getPot() {
        return pot;       
    }

	//----------------------
	//getIncluded() is used to access the private class variable
	//
    public List<Long> getIncluded() {
        return included;
    }

	//----------------------
	//getExcluded() is used to access the private class variable
	//
    public List<Long> getExcluded() {
        return excluded;
    }
}