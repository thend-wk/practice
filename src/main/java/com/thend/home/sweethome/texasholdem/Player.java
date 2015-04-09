/******************************************************************************************
 * Player.java                     PokerApp                                               *
 *                                                                                        *
 *   Revision History                                                                     *
 * +---------+----------+---------------------------------------------------------------+ *
 * | Version | DATE     | Description                                                   | *
 * +---------+----------+---------------------------------------------------------------+ *
 * |  0.95   | 09/15/04 | Initial documented release                                    | *
 * |  0.97   | 10/26/04 | Allow PokerProtocol to set players bankroll.                  | *
 * |  0.99   | 02/04/05 | Don't allow unauthorized classes to call add or subtract      | *
 * |         |          | functions or the addBet or betInGame functions.               | *
 * |  1.00   | 07/09/07 | Prepare for open source.  Header/comments/package/etc...      | *
 * +---------+----------+---------------------------------------------------------------+ *
 *                                                                                        *
 * PokerApp Copyright (C) 2004  Dan Puperi                                                *
 *                                                                                        *
 *   This program is free software: you can redistribute it and/or modify                 *
 *   it under the terms of the GNU General Public License as published by                 *
 *   the Free Software Foundation, either version 3 of the License, or                    *
 *   (at your option) any later version.                                                  *
 *                                                                                        *
 *   This program is distributed in the hope that it will be useful,                      *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of                       *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                        *
 *   GNU General Public License for more details.                                         *
 *                                                                                        *
 *   You should have received a copy of the GNU General Public License                    *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>                 *
 *                                                                                        *
 ******************************************************************************************/

package com.thend.home.sweethome.texasholdem;


/****************************************************
 * The player class is the player in the poker game, either human or AI.
 *
 * @author Dan Puperi
 * @version 1.00
 *
 **/
public class Player {

/**
 * If this player is paid up to call
 **/
    public  boolean                 potOK;
/**
 * If this player is in the current game
 **/
    public  boolean                 in;
/**
 * If this player is going all in
 **/
    public  boolean                 allin;
/**
 * Seat # this player is sitting in.  = -9 if he's not sitting anywhere.
 **/
    public  int                     seat;

    private long userId; 									  //用户id
    private Hand                    hand;                     //  The card's in his hand
    private PokerMoney              bankroll;                 //  How much money this player has to his name
    private PokerMoney              bet;                      //  How much this player is about to bet
    private PokerMoney              prevBet;                  //  How much this player bet last time around - to calculate raises

/***************************
 * The constructor creates a new Player
 * 
 * @param n The name of the new Player
 *
 **/
    public Player(long userId, int seat) {
        hand = new Hand();
        bankroll = new PokerMoney();
        bet = new PokerMoney();
        prevBet = new PokerMoney();
        in = false;
        potOK = true;
        this.userId = userId;
        this.seat = seat;
    }

/***************************
 * getBet() is used to access the private variable
 *
 * @return This player's current bet
 *
 **/
    public PokerMoney getBet() {
        return bet;
    }

/***************************
 * getPrevBet() is used to access the private variable
 *
 * @return This player's previous bet
 *
 **/
    public PokerMoney getPrevBet() {
        return prevBet;
    }

/***************************
 * getBankroll() is used to access the private variable
 *
 * @return This player's bankroll
 *
 **/
    public PokerMoney getBankroll() {
        return bankroll;
    }

/***************************
 * getHand() is used to access the private variable.  Note that only certain classes are allowed to
 * access the players hand to try to limit cheating.
 *
 * @return This player's hand
 *
 **/
    public Hand getHand() {
    	return hand;
    }

/***************************
 * setBet() is used to set the player's bet.  Note that only certain classes are allowed to
 * access this function to try to limit cheating.
 *
 * @param b The float value to which the player's bet should be set
 *
 **/
    public void setBet( float b ) {
        if ( b > bankroll.amount() ) {
            bet = new PokerMoney( bankroll.amount() );
        } else {
            bet = new PokerMoney( b );
        }
    }

/***************************
 * setPrevBet() is used to set the player's previous bet.  Note that only certain classes are allowed to
 * access this function to try to limit cheating.
 *
 * @param b The float value to which the player's previous bet should be set
 *
 **/
    public void setPrevBet( float b ) {
    	prevBet = new PokerMoney( b );
    }

/***************************
 * setBankroll() is used to set the player's bankroll.  Note that only certain classes are allowed to
 * access this function to try to limit cheating.
 *
 * @param b The float value to which the player's bankroll should be set
 *
 **/
    public void setBankroll( float b ) {
    	bankroll = new PokerMoney( b );
    }

/***************************
 * add() is used to add to the player's bankroll.  Note that only certain classes are allowed to
 * access this function to try to limit cheating.
 *
 * @param a The float value which to add to the player's bankroll
 *
 **/
    public void add( float a ) {
    	bankroll.add( a );
    }

/***************************
 * subtract() is used to subtract from the player's bankroll.  Note that only certain classes are allowed to
 * access this function to try to limit cheating.
 *
 * @param s The float value which to subtract from the player's bankroll
 *
 **/
    public void subtract( float s ) {
    	bankroll.subtract( s );
    }

/***************************
 * addBet() is called when there is a need to increase the current bet.  Note that only certain classes are allowed
 * to access this function to try to limit cheating.
 *
 * @param b The float value which to add to the player's bet
 *
 **/
    public void addBet( float b ) {
        if ( ( b + bet.amount() ) > bankroll.amount() ) {
            setBet( bankroll.amount() );
        } else {
            bet.add( b );
        }
    }

/***********************
 * When betInGame() is called, the current bet is saved as the previous bet and then zeroed out
 **/
    public void betInGame() {
    	prevBet.add( bet.amount() );
        setBet( 0.0f );
    }

/***********************
 * newGame() is used to reinitialize the betting variables and hand to set up for a new game.
 **/
    public void newGame() {
        bet = new PokerMoney( 0.0f );
        prevBet = new PokerMoney( 0.0f );
        in = true;
        allin = false;
        potOK = false;
        hand.clearHand();
    }

public long getUserId() {
	return userId;
}

public void setUserId(long userId) {
	this.userId = userId;
}
}