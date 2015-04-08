package com.thend.home.sweethome.texasholdem;

import java.util.ArrayList;
import java.util.List;
/**
 * 德州扑克
 * @author kaiwang
 *
 */
public class TexasHoldEm {
	
	//一副牌
	private Deck deck;
	//当前游戏人数
	private int playerSize = 6;
	//步骤
	private int actionNum = 0;
	//最多步骤
	private int maxActionNum = 5;
	//起注位置
    private int dealerIndex = 0;
	//最大游戏人数
	private static final int MAX_PLAYERS = 9;
	//游戏人员列表
	private List<Player> playerList;
	//评分工具
    private HandEvaluator he;
    //是否跳过展示结果
    private boolean skipShow = false;
    //总奖池
    private PokerMoney pot;
    //大盲注
	public boolean blinds;
	//底注
	public boolean antes;
	//小盲注
	public PokerMoney smallBlind;
	//地注
	public PokerMoney ante;
	//是否游戏中
	private boolean inGame;
	//开牌用户手牌得分
    private float playerHandValues[];
    //边池
    public ArrayList<SidePot> sidePots;
    //当前牌手
    private int currPlayerIndex;
    //当前最高投注
    private PokerMoney currBet;
    //raise
    private PokerMoney initialBet;
    //minimum
    private PokerMoney minimumBet;

    
    public TexasHoldEm() {
    	init();
    	deal();
    	for(int i=0;i<4;i++) {
    		nextAction();
    	}
    }
    /**
     * 初始化
     */
    private void init() {
    	deck = new Deck();
    	deck.shuffle();
    	inGame = true;
    	he = new HandEvaluator();
     	blinds = true;
     	antes = true;
     	smallBlind = new PokerMoney(200f);
     	ante = new PokerMoney(1000f);
     	minimumBet = new PokerMoney(20f);
    	playerList = new ArrayList<Player>(playerSize);
    	for(int i=0;i<playerSize;i++) {
    		playerList.add(new Player());
    	}
    }


	/***********************
	 * deal() deals the initial cards.  This function must be included in the game definition.  
	 **/
    private void deal() {

    	actionNum++;
    	
//    Initialize variable c - which is a Card.
//
        Card  c = new Card();
        

//   First card down
//     Loop through all players and give them their first card.
//
        for ( int i = 0; i < playerList.size(); i++ ) {
        
//     Set c to the top card from the deck and add it to the players hand (in the hole).
//     Can also add it to their hand face up by calling addUpCard( c ) instead of addHoleCard( c ).
//   
            c = deck.deal();
            ((Player)playerList.get(i) ).getHand().addHoleCard( c );
        }

//   Second card down
//     Again loop through all players and give them their second card.  Same thing happens as the first card except the second card is placed in position 1.
//
        for ( int i = 0; i < playerList.size(); i++ ) {
            c = deck.deal();
            ((Player)playerList.get(i)).getHand().addHoleCard( c );
        }
    }
    
    /***********************
     * nextAction() determines what happens next.  The next action function gets called by the main PokerGame class at the end of a "round"
     **/
    private void nextAction() {

//         Increment the actionNum variable - required.
//
        actionNum++;

//         Reset some betting variables for all of the players - required.
//
        for ( int i = 0; i < playerList.size(); i++ ) {
            playerList.get(i).potOK = false;
            playerList.get(i).setBet(0.0f);
            playerList.get(i).setPrevBet(0.0f);
        }

//         Reset some game variables if this game is not finished.  If actionNum = maxActionNum, all thats left to do is the showdown.
//
        if ( actionNum != maxActionNum ) {
        	PokerMoney currBet = new PokerMoney(0.0f);
            int currPlayerIndex = firstToBet();
            if ( ( currPlayerIndex < 0 ) || ( currPlayerIndex >= playerList.size() ) ) {    
                actionNum = maxActionNum;
                skipShow = true;
            }
        }

//         This switch statement is the important piece of code which designates what to do for each action.  actionNum is initialized = 1, so the switch must
//         check for the cases where actionNum goes from 2 to maxActionNum (defined in the constructor above).  For each case it calls the required functions.
//         Setting bestPossible is not necessary, but can be helpful for any AI players that use that variable.  The show() function should always be called when
//         actionNum = maxActionNum.
//
        switch ( actionNum ) {
            case 2 :
                flop();
                break;
            case 3 :
                turn();
                break;
            case 4 :
                river();
                break;
            case 5 :
                show();
                break;
            default :
                break;  
        }
    }

	/***********************
	 * flop() deals the Flop - 3 shared cards
	 **/
    private void flop() {

//     This is very similar to the deal() function in that it deals cards from the deck, gives them to players, and also indicates where to display them on the table.
//     Create a new blank card.
//
        Card  c = new Card();

//     Set c equal to the next card in the deck.
//
        c = deck.deal();

//     Loop through all the players and add the card to their hands.
//
        for ( int i = 0; i < playerList.size(); i++ ) {
            ((Player)playerList.get(i)).getHand().addSharedCard( c );
        }

//     Repeat for second flop card.
//
        c = deck.deal();
        for ( int i = 0; i < playerList.size(); i++ ) {
            ( (Player)playerList.get(i) ).getHand().addSharedCard( c );
        }

//     Repeat for third flop card
//
        c = deck.deal();
        for ( int i = 0; i < playerList.size(); i++ ) {
            ((Player)playerList.get(i)).getHand().addSharedCard( c );
        }
    }

	/***********************
	 * turn() deals the 4th shared card
	 * Very similar to the flop() logic - exception the position has changed to positon 3.
	 **/
    private void turn() {
        Card  c = new Card();
        c = deck.deal();
        for ( int i = 0; i < playerList.size(); i++ ) {
            ((Player)playerList.get(i)).getHand().addSharedCard( c );
        }
    }

	/***********************
	 * river() deals the 5th and final shared card
	 * same as the turn() logic, exception card is shown at position 4
	 **/
    private void river() {
        Card  c = new Card();
        c = deck.deal();
        for ( int i = 0; i < playerList.size(); i++ ) {
            ((Player)playerList.get(i)).getHand().addSharedCard( c );
        }
    }
    
    /**********************
     * show() is called at the showdown - it handles showing the cards, determining the winner and cleaning up the game.
     **/
    private void show() {
        calcWinner();
        showCards();
        resetPot();
        nextDealer();
        nullifyGame();
    }
    
    
    /**********************
     * calcWinner() caluclates the winner(s) of the hand.
     **/
    private void calcWinner() {
        int highPlayer = 0;
        float highHand = 0.0f;
        int numWinners = 0;
        int winner[] = new int[MAX_PLAYERS];
        playerHandValues = new float[MAX_PLAYERS];

        for ( int i = 0; i < playerList.size(); i++ ) {
            Player p = playerList.get(i);
            if (p.in) {
                playerHandValues[p.seat] = bestHand(p.getHand());
                if (playerHandValues[p.seat] > highHand) {
                    highHand = playerHandValues[p.seat];
                    highPlayer = i;
                }
            }
        }
        for ( int i = 0; i < playerList.size(); i++ ) {
            Player p = playerList.get(i);
            if (p.in) {
                if (playerHandValues[p.seat] == highHand) {
                    winner[numWinners] = i;
                    numWinners++;
                }
            }
        }

        PokerMoney winnersTake = new PokerMoney(pot.amount());
        if ( sidePots.size() != 0 ) {
            for ( int i = 0; i < sidePots.size(); i++ ) {
                winnersTake.subtract(sidePots.get(i).getPot().amount());
            }
        }

        if ( numWinners == 1 ) {
            playerList.get(highPlayer).add(winnersTake.amount());
        } else {
            PokerMoney money = new PokerMoney( winnersTake.amount() / (float)numWinners);
            playerList.get(winner[0]).add(money.amount());
            for ( int i = 1; i < numWinners; i++ ) {
                playerList.get(winner[i]).add(money.amount());
            }
        }
        
        for (int i = 0; i < sidePots.size(); i++) {
            calcSidePotWinners(i);
        }
    }
    
    /**********************
     * calcSidePotWinners() calculate the winner(s) of the side pots.
     *
     * @param potNumber Which side pot to calculate the winner of
     * @retrun The string which indicates who won the side pot.
     *
     **/
    private void calcSidePotWinners(int potNumber) {
        int highPlayer = 0;
        float highHand = 0.0f;
        String winningHand = new String();
        int numWinners = 0;
        SidePot side = (SidePot)sidePots.get(potNumber);
        PokerMoney pot = side.getPot();
        String [] players = new String[side.getIncluded().size()];
        for (int i = 0; i < players.length; i++ ) {
            players[i] = (String)side.getIncluded().get(i);
        }

        int winner[] = new int[MAX_PLAYERS];
        float newPlayerHandValues[] = new float[MAX_PLAYERS];

        int numPlayers = 0;
        List<Integer> playerIdxList = new ArrayList<Integer>();
        for ( int i = 0; i < players.length; i++ ) {
            if ( players[i] != null ) {
            	int idx = playerIndex(players[i]);
                if (idx >= 0) {
                	playerIdxList.add(idx);
                    numPlayers++;
                }
            }
        }

        if ( numPlayers == 1 ) {
            int pindex = playerIdxList.get(0);
            playerList.get(pindex).add(pot.amount());
        }

        for ( int i = 0; i < numPlayers; i++ ) {
        	int idx = playerIdxList.get(i);
            Player p = playerList.get(idx);
            newPlayerHandValues[i] = playerHandValues[p.seat];
            if (newPlayerHandValues[i] > highHand) {
                highHand = newPlayerHandValues[i];
                highPlayer = idx;
            }
        }
        for ( int i = 0; i < numPlayers; i++ ) {
            Player p = playerList.get(playerIdxList.get(i));
            if ( newPlayerHandValues[i] == highHand ) {
                winner[numWinners] = playerIdxList.get(i);
                numWinners++;
            }
        }
        winningHand = HandEvaluator.nameHand( highHand );

        PokerMoney winnersTake = new PokerMoney(pot.amount());

        if (numWinners == 1) {
            playerList.get(highPlayer).add(winnersTake.amount());
        } else {
            PokerMoney money = new PokerMoney(winnersTake.amount() / (float)numWinners);
            playerList.get(winner[0]).add(money.amount());
            for ( int i = 1; i < numWinners; i++ ) {
                playerList.get(winner[i]).add(money.amount());
            }
        }
    }
    
    public int playerIndex( String name ) {
        if ( name == null ) {
            return -1;
        }
        for ( int i=0; i < playerList.size(); i++ ) {
            String pname = ((Player)playerList.get(i)).getName();
            if ( name.equals( pname ) ) {
                return i;
            }
        }
        return -1;
    }
    
    /***********************
     * nextDealer() finds the next available dealer.
     **/
    private void nextDealer() {
    	Player dealer = playerList.get(dealerIndex); 
        boolean dealerLeaving = true;
        int attempts = 0;
        if (dealer != null) {
            while (dealerLeaving) {
                attempts++;
                dealerLeaving = false;
                int dealerSeat = nextSeat(dealer.seat);
                if (dealerSeat < 0) {
                    dealerSeat = dealer.seat;
                }
                dealerIndex = getPlayerInSeat( dealerSeat );
                if ( ( dealerIndex >= playerList.size() ) || ( dealerIndex < 0 ) ) {
                    dealerLeaving = true;
                } else {
                    dealer = playerList.get(dealerIndex);
                    float mincash = smallBlind.amount();   
                    if ( blinds ) {
                        mincash = 2.0f*smallBlind.amount();
                    }
                    if ( antes ) {
                        mincash = mincash + ante.amount();
                    }
            
                    if (dealer.getBankroll().amount() < mincash) {
                        dealerLeaving = true;
                    }
                }
                if ( attempts >= MAX_PLAYERS ) {
                    dealerLeaving = false;
                }
            }
        }
    }
    
    private void resetPot() {
        pot = new PokerMoney();
    }
    
    private void showCards() {
    	if(!skipShow) {
	        int i=0;
	        for(Player player : playerList) {
	        	Hand hand = player.getHand();
	        	System.out.println("player" + i++ + ":");
	        	System.out.println(hand.getHoleCard(0));
	        	System.out.println(hand.getHoleCard(1));
	        	for(int j=0;j<5;j++) {
	        		System.out.println(hand.getSharedCard(j));
	        	}
	        	float score = playerHandValues[player.seat];
	        	System.out.println("score : " + score);
	        	System.out.println("best hand name : " + HandEvaluator.nameHand(score));
	        }
    	}
    }
    
    private void nullifyGame() {
        inGame = false;
    }
    
    /**********************
     *  firstToBet_HoldEm() is a generic function that can be used for HoldEm type games defining which player bets first
     *
     * @return The integer player index of the player who will be betting first
     *
     **/
        private int firstToBet() {
        	int dealerSeat = playerList.get(dealerIndex).seat;
            if (actionNum == 1) {
                int prevSeat = nextSeat(dealerSeat);
                prevSeat = nextSeat(prevSeat);
                if (playerList.size() == 2) {
                    return getPlayerInSeat(dealerSeat);
                } else {
                    return getPlayerInSeat(nextSeat(prevSeat));
                }
            } else {
                if (dealerSeat < 0) {
                    return -9;
                }
                int prevSeat = nextSeat( dealerSeat );
                int pindex = getPlayerInSeat( prevSeat );
                if ( ( pindex >= 0 ) && ( pindex < playerList.size() ) ) {
                    while (!(playerList.get(pindex)).in) {
                        prevSeat = nextSeat(prevSeat);
                        pindex = getPlayerInSeat(prevSeat);  
                        if ((pindex < 0) || (pindex >= playerList.size())) {
                        	return -9;
                        }
                    }
                    return pindex;
                }
                return -9;
            }
        }
    
        /***********************
         * nextSeat() is used by nextDealer to find the next occupied seat
         *
         * @param currSeat The seat from which to find the next
         * @return The next occupied seat after currSeat
         *
         **/
            public int nextSeat( int currSeat ) {
                if ( ( currSeat < 0 ) || ( currSeat >= MAX_PLAYERS ) ) {
                    return -9;
                }
                for ( int i = currSeat+1; i < MAX_PLAYERS; i++ ) {
                    int pindex = getPlayerInSeat(i);
                    if ( pindex != -9 ) {
                    	 if(playerList.get(pindex).in) {
                    		 return i;
                    	 }
                    }
                }
                for ( int j = 0; j < currSeat; j++ ) {
                    int pindex = getPlayerInSeat(j);
                    if ( pindex != -9 ) {
                    	if(playerList.get(pindex).in) {
                   		 	return j;
                   	 	}
                    }
                }
                return -9;
            }
            
            
            /***************************
             * getPlayerInSeat() returns the number of the player currently occupying the seat in question
             *
             * @param s The seat number which is being inquired.
             * @return The player index which is their location in playerList.
             *
             **/
            public int getPlayerInSeat( int s ) {
                if ( ( s < 0 ) || ( s >= MAX_PLAYERS) ) {
                    return -9;
                }
                for ( int i = 0; i < playerList.size(); i++ ) {
                    if ( s == playerList.get(i).seat ) {
                        return i;
                    }
                }
                return -9;
            }
        
    /***********************
     * bestHand() returns the float value of the best possible hand that can be made from given hand h.
     * This function is called at the showdown for each player to determine which hand they can make.
     * The HandEvaluator class is used to figure out the best possible hand the player can make.
     * This function is required for all games, but may look very different depending from this depending
     * on what the rules of the game are.
     **/ 
        public float bestHand( Hand h ) {
//         Count total number of cards available and set of the c array to contain that many cards.
//         This won't always work if the game does not allow all cards to be used equally (ie Omaha HoldEm)
    //
            int numCards = h.getNumHole() + h.getNumShared();
            Card[] c = new Card[numCards];   

//         Initialize the best and test variables - used for comparing ranks of hands.
    //
            float best = 0.0f;
            float test = 0.0f;

//         ci is the array of 5 cards that are going to be played as the actual hand.
    //
            int[] ci = new int[5];

//         Loop through all hole and shared cards and add them to the single array of available cards.
    //
            for ( int i = 0; i < h.getNumHole(); i++ ) { 
                if ( h.getHoleCard( i ) != null ) {
                    c[i] = h.getHoleCard( i );
                }
            }

            for ( int i = 0; i < h.getNumShared(); i++ ) {
                if ( h.getSharedCard( i ) != null ) {
                    c[h.getNumHole()+i] = h.getSharedCard( i );
                }
            }

//         If there are only 5 or less cards available, only one hand can be made, so return a numerical value for these 5 cards.  This is necessary so the AI
//         can determine its hand strength - also required if betting order depends on shown hand strength (7 card stud).
    //
            if ( numCards == 5 ) {
                return he.rankHand( c[0], c[1], c[2], c[3], c[4] );
            } else if ( numCards == 4 ) {
                return he.rankHand( c[0], c[1], c[2], c[3] );
            } else if ( numCards == 3 ) {
                return he.rankHand( c[0], c[1], c[2] );
            } else if ( numCards == 2 ) {
                return he.rankHand( c[0], c[1] );
            } else if ( numCards == 1 ) {
                return he.rankHand( c[0] );
            } else if ( numCards == 0 ) {
                return 0.0f;

//         If there are 6 or more cards, loop through all possible combinations and find the best possible 5 card hand that can be made.
    // 
            } else if ( numCards >= 6 ) { 
                for ( int i = 0; i < numCards-4; i++ ) {
                    for ( int j = i+1; j < numCards-3; j++ ) {
                        for ( int k = j+1; k < numCards-2; k++ ) {
                            for ( int l = k+1; l < numCards-1; l++ ) {
                                for ( int m = l+1; m < numCards; m++ ) {             

//         This is where the hand is ranked.  If this hand is better than all previous hands that could be made, then set it as the best hand.
//         Use the ci array to store card locations for the best hand.
    //
                                    test = he.rankHand( c[i],c[j],c[k],c[l],c[m] );
                                    if ( test > best ) {
                                        best = test;
                                        ci[0] = i; ci[1] = j; ci[2] = k; ci[3] = l; ci[4] = m;
                                    }
                                }
                            }
                        }
                    }
                }
            }

//         Use the rankHand function along with the ci array to return the numerical ranking of this hand.
//         (Could just return the best variable here, but in some other games using this method is better so its done this way)
    //    
            return he.rankHand( c[ci[0]], c[ci[1]], c[ci[2]], c[ci[3]], c[ci[4]] );
        }  
  
	    /**********************
	     * bet() is called whenever a player bets.  This is a hugely complicated function, but it works pretty well I think.
	     **/
        private void bet() {
            int size = playerList.size();
            Player currPlayer = playerList.get(currPlayerIndex);
            float betS = currPlayer.getBet().amount();
            float betP = currPlayer.getPrevBet().amount();
            if ( currPlayer.getBankroll().compareTo( new PokerMoney() ) == 1 ) {
                currPlayer.allin = false;
            }
            if ( ( currPlayer.getBankroll().amount() - betS ) < 0.009f ) {
                currPlayer.allin = true;
            }
            if ( ( ( betP + betS  ) < currBet.amount() ) && ( !currPlayer.allin ) ) {
                if ( ( currBet.amount() - betP ) >= currPlayer.getBankroll().amount() ) {
                    System.out.println("You must go all in to match the bet.");
                    //TODO
                } else {
                    System.out.println("You must bet at least " + new PokerMoney( currBet.amount() - betP ) + " to call.");
                    //TODO
                }
            } else {
                boolean    potOK = true;
                boolean    betOK = false;
                PokerMoney raise = new PokerMoney( betS + betP - currBet.amount() );
                if ( ( betS + betP ) > currBet.amount() ) {
                    if ( currPlayer.allin ) {
                        System.out.println(currPlayer.getName() + " went All In!");
                        if ( initialBet.amount() == 0.0f ) {
                            initialBet = new PokerMoney( raise.amount() );
                        }
                        betOK = true;
                    } else if ( currBet.amount() == 0.0 ) {
                        if ( raise.compareTo( minimumBet ) == -1 ) {
                            System.out.println("You must bet the minimum bet of at least " + minimumBet);
                            //TODO
                        } else {
                            System.out.println(currPlayer.getName() + " bet " + raise + ".");
                            //TODO
                            initialBet = new PokerMoney(raise.amount());
                            betOK = true;
                        }
                    } else {
                        if ( raise.amount() < initialBet.amount() ) {
                            System.out.println("If you wish to raise, you must raise by at least " + initialBet);
                            //TODO
                        } else {
                            System.out.println(currPlayer.getName() + " raised " + raise + ".");
                            //TODO
                            betOK = true;
                        }
                    }

                    if ( betOK ) {
                        currBet = new PokerMoney( betS + betP );
                        currPlayer.subtract( betS );
                        pot.add( betS );
                        currPlayer.betInGame();
                        currPlayer.potOK = true;
                        if ( currPlayer.getBankroll().compareTo( new PokerMoney( 0.01f ) ) == -1 ) {
                            currPlayer.allin = true;
                        }
                        playerList.set(currPlayerIndex,currPlayer);
//               
//                  New side pot may need to be created if this player bet more than one or more other player's have in their bankroll.
//                
                        boolean newSidePot = false;
                        for ( int i = 0; i < playerList.size(); i++ ) {
                        	Player player = playerList.get(i);
                            if(player.in && ((player.getBankroll().amount() + player.getPrevBet().amount()) < (betS+betP))) {
                                newSidePot = true;
                            }
                        }
                        boolean sidepot_just_created = false;
                        if ( newSidePot ) {
                            sidepot_just_created = createSidePots( betS + betP );
                        } 
                        if ( !sidepot_just_created ) {
//                        
//                     Add raise money to latest side pot if one was not just created.
//                             
                            int lastPot = sidePots.size() - 1;
                            if ( lastPot >= 0 ) {
                                ArrayList<String> incLst = sidePots.get(lastPot).getIncluded();
                                float tc = sidePots.get(lastPot).toCall[currPlayerIndex].amount();
                                if ( incLst.contains(currPlayer.getName())) {
                                    sidePots.get(lastPot).addMoney( tc + raise.amount());
                                    for ( int i = 0; i < size; i++ ) {
                                        sidePots.get(lastPot).toCall[i].add( raise.amount() );
                                    }
                                    sidePots.get(lastPot).toCall[currPlayerIndex] = new PokerMoney();
                                }
//                             
//                      Must loop through previous pots to make sure toCall is satisfied.
//                              
                                for ( int i = 0; i < lastPot; i++ ) {
                                    ArrayList<String> incLst2 = sidePots.get(i).getIncluded();
                                    float tc2 = sidePots.get(i).toCall[currPlayerIndex].amount();
                                    if ( incLst2.contains( currPlayer.getName() ) ) {
                                        sidePots.get(i).addMoney( tc2 );
                                        sidePots.get(i).toCall[currPlayerIndex] = new PokerMoney();
                                    }
                                }
                            }
                        } else {
                            int newPot = sidePots.size() - 1;
                            float amount =  betS - sidePots.get(newPot).getPot().amount();
                            for ( int i = 0; i < sidePots.size()-1; i++ ) {
                                SidePot sp = sidePots.get(i);
                                ArrayList<String> incLst = sp.getIncluded();
                                if ( ( incLst.contains( currPlayer.getName() ) ) && ( sp.toCall[currPlayerIndex].amount() > 0.0f ) ) {
                                    float amt = 0.0f;
                                    if ( i == sidePots.size()-2 ) {
                                        float tc = sp.toCall[currPlayerIndex].amount();
                                        amt = tc+raise.amount() - sidePots.get(newPot).getPot().amount();
                                        for ( int k = 0; k < size; k++ ) {
                                            sidePots.get(i).toCall[k].add( raise.amount() - sidePots.get(newPot).getPot().amount());
                                        }
                                    } else if ( amount > sp.toCall[currPlayerIndex].amount() ) {
                                        amt = sp.toCall[currPlayerIndex].amount();
                                    } else {
                                        amt = amount;
                                    }
                                    sidePots.get(i).addMoney( amt );
                                    sidePots.get(i).toCall[currPlayerIndex] = new PokerMoney();
                                    amount = amount - amt;
                                }
                            }
                        }
    //
//                  Make sure all the players have their potOK flag set to false if required.
//              
                        for ( int i = 0; i < size; i++ ) {
                        	Player player = playerList.get(i);
                        	player.potOK = false;
                            if (player.allin ) {
                            	player.potOK = true; 
                            }
                        }
                        playerList.get(currPlayerIndex).potOK = true;
                        for ( int i = 0; i < size; i++ ) {
                        	Player player = playerList.get(i);
                            if (player.in && !player.potOK) {
                                potOK = false;
                            }
                        }
                    }
                } else {
                    currPlayer.subtract( betS );
                    pot.add( betS );
                    currPlayer.betInGame();
                    currPlayer.potOK = true;
                    if ( currPlayer.getBankroll().compareTo( new PokerMoney( 0.010f ) ) == -1 ) {
                        currPlayer.allin = true;
                    }
                    playerList.set(currPlayerIndex,currPlayer);
                    for ( int i = 0; i < size; i++ ) {
                    	Player player = playerList.get(i);
                        if (player.in && !player.potOK && !player.allin) {
                            potOK = false;
                        }
                    }
    //
//               Add money to side pots as required.
    //
                    float amount = betS;
                    for ( int i = 0; i < sidePots.size(); i++ ) {
                        SidePot sp = sidePots.get(i);
                        ArrayList<String> incLst = sp.getIncluded();
                        if ( ( incLst.contains( currPlayer.getName() ) ) && ( sp.toCall[currPlayerIndex].amount() > 0.0f ) ) {
                            float amt = 0.0f;
                            if ( amount > sp.toCall[currPlayerIndex].amount() ) {
                                amt = sp.toCall[currPlayerIndex].amount();
                            } else {
                                amt = amount;
                            }
                            sidePots.get(i).addMoney( amt );
                            sidePots.get(i).toCall[currPlayerIndex].subtract( amt );
                            amount = amount - amt;
                        }
                    }
                    if ( currPlayer.allin ) {
                        System.out.println(currPlayer.getName() + " went All In!");
                        betOK = true;
                    } else if ( currBet.amount() == 0.0 ) {
                        System.out.println(currPlayer.getName() + " checked.");
                        betOK = true;
                    } else {
                        betOK = true;
                    }
                }

                if ( betOK && !potOK ) {
                    int prevSeat = currPlayer.seat;     
                    boolean foundNext = false;
                    while ( !foundNext ) {
                        prevSeat = nextSeat(prevSeat);
                        int playerIdx = getPlayerInSeat(prevSeat);
                        Player player = playerList.get(playerIdx);
                        if (player.in && !player.allin) {
                            foundNext = true;
                            currPlayerIndex = getPlayerInSeat(prevSeat);
                        }
                    }
                    if ( ( actionNum+1 ) <= maxActionNum ) {
                        //TODO
                    } else {
                       //TODO
                    }
                }

                if ( betOK ) {
                    int allins = 0, ins = 0;
                    for ( int i = 0; i < size; i++ ) {
                    	Player player = playerList.get(i);
                       if (player.allin ) {
                            allins++;
                        }
                        if ( player.in ) {
                            ins++;
                        }
                    }
                    if ( allins >= ( ins-1 ) ) {
                        if ( potOK ) {
                            while ( actionNum < maxActionNum ) {
                                endAction();
                            }
                        }
                    } else {
                        if ( potOK ) { 
                        	endAction();
                        }
                    }
                }
            }
        }
        
        
        /**********************
         * endAction() is used to clean up the current round of action and call the game defined nextAction() function.
         **/
        private void endAction() {
            initialBet = new PokerMoney( 0.0f );
            for ( int i = 0; i < sidePots.size(); i++ ) {
                for ( int j = 0; j < playerList.size(); j++ ) {
                    sidePots.get(i).toCall[j] = new PokerMoney();
                }
            }
            nextAction();
        }

    /**********************
     * createSidePots() is called after somebody raises.  If necessary, it will create side pots because some player(s)
     * in the game cannot match the bet.
     *
     * @param bet The bet value which is needed to stay in the game.
     * @return Whether of not the side pots were successfully created.
     *
     **/
        protected boolean createSidePots( float bet ) {
            boolean ret = false;
    //
//            First need to order players in a list of how much they have relative to how much they owe.
    //
            int size = playerList.size();
            String[] names = new String[size];
            float[] amounts = new float[size];
            int count = 0;
            for ( int i = 0; i < size; i++ ) {
                Player p = playerList.get(i);
                if ( p.in ) {
                    names[count] = new String( p.getName() );
                    amounts[count] = p.getBankroll().amount() + p.getPrevBet().amount()- bet;
                    count++;
                }
            }
            String tempN = new String();
            float tempA = 0.0f;
            for ( int i = 0; i < count; i++ ) {
                for ( int j = i; j < count; j++ ) {
                    if ( amounts[j] < amounts[i] ) {
                        tempN = names[i];
                        tempA = amounts[i];
                        names[i] = names[j];
                        amounts[i] = amounts[j];
                        names[j] = tempN;
                        amounts[j] = tempA;
                    }
                }
            }
    //
//            Create a side pot for all players which amount is negative
    //
            for ( int i = 0; i < count; i++ ) {
                if ( amounts[i] < 0 ) {
                    ArrayList<String> excludedList = new ArrayList<String>();
                    ArrayList<String> includedList = new ArrayList<String>();
                    float amount = -amounts[i];
                    for ( int j = i+1; j < count; j++ ) {
                        if ( amounts[j] < 0.0f ) {
                            amount = amount + amounts[j];
                        }
                    }
                    for ( int j = 0; j <= i; j++ ) {
                        excludedList.add( names[j] );
                    }
                    for ( int j = i+1; j < count; j++ ) {
                        includedList.add( names[j] );
                    }
    //
//                Do not create a side pot with the exact same list of excluded players - that side pot will be redundant
    //
                    boolean needToCreate = true;
                    for ( int j = 0; j < sidePots.size(); j++ ) {
                        boolean listsSame = true;
                        ArrayList<String> spExLst = sidePots.get(j).getExcluded();
                        if ( excludedList.size() != spExLst.size() ) {
                            listsSame = false;
                        } else {
                            for ( int k = 0; k < excludedList.size(); k++ ) {
                                if (!excludedList.get(k).equals(spExLst.get(k))) {
                                    listsSame = false;
                                }
                            }
                            if ( listsSame ) {
                                needToCreate = false;
                            }
                        }
                    }
                    if ( needToCreate ) {
                        sidePots.add(new SidePot(includedList, excludedList, amount, size, currPlayerIndex));
                        ret = true;
                    }
                }
            }
            return ret;
        }

        
    public static void main(String[] args) {
		TexasHoldEm texasHoldEm = new TexasHoldEm();
	}
}