package com.thend.home.sweethome.texasholdem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.gs.collections.impl.map.mutable.ConcurrentHashMap;

/**
 * 德州扑克（一桌）
 * 
 * @author kaiwang
 *
 */
public class TexasHoldEm {
	// 游戏房间ID
	private int roomId;
	// 一副牌
	private Deck deck;
	// 当前步骤
	private int actionNum = 0;
	// 总步骤数
	private int maxActionNum = 5;
	// Dealer位置
	private int dealerIndex = 0;
	// 小盲注座位
	private int smallBlindSeat = -1;
	// 大盲注座位
	private int bigBlindSeat = -1;
	// 最大游戏人数
	private static final int MAX_PLAYERS = 6;
	// 在位游戏人员列表
	private List<Player> playerList;
	private ConcurrentHashMap<Integer, Player> seatPlayerMap;
	// 评分工具
	private HandEvaluator he;
	// 是否跳过展示结果
	private boolean skipShow = false;
	// 总奖池
	private PokerMoney pot;
	// 大盲注
	public boolean blinds;
	// 底注
	public boolean antes;
	// 小盲注
	public PokerMoney smallBlind;
	// 地注
	public PokerMoney ante;
	// 游戏是否进行中
	private boolean inGame;
	// 开牌用户手牌得分
	private float playerHandValues[];
	// 边池
	public List<SidePot> sidePots;
	// 当前牌手
	private int currPlayerIndex;
	// 当前最高投注
	private PokerMoney currBet;
	// raise
	private PokerMoney initialBet;
	// minimum
	private PokerMoney minimumBet;
	// 首次盲注
	private boolean firstBlindGamePlayed;
	// 离开玩家
	private List<Long> leavingPlayerList;
	// 上一轮玩家
	private List<Long> playedInLastGame;
	// empty值
	private float MINIMUM = 0.01f;
	// 小盲注值
	public static final int SMALL_BLIND = 10;
	// 底注值
	public static final int ANTE = 500;

	private ReentrantReadWriteLock playerLock;

	/**
	 * 构造方法
	 * 
	 * @param roomId
	 */
	public TexasHoldEm(int roomId) {
		this.roomId = roomId;
		deck = new Deck();
		he = new HandEvaluator();
		blinds = true;
		antes = false;
		firstBlindGamePlayed = false;
		pot = new PokerMoney();
		smallBlind = new PokerMoney(SMALL_BLIND);
		ante = new PokerMoney(ANTE);
		minimumBet = new PokerMoney(SMALL_BLIND * 2);
		initialBet = new PokerMoney();
		playerList = new ArrayList<Player>(MAX_PLAYERS);
		seatPlayerMap = new ConcurrentHashMap<Integer, Player>(MAX_PLAYERS);
		playerLock = new ReentrantReadWriteLock();
		sidePots = new ArrayList<SidePot>(MAX_PLAYERS - 1);
		playedInLastGame = new ArrayList<Long>(MAX_PLAYERS);
		leavingPlayerList = new ArrayList<Long>();
	}

	/**
	 * 打印日志
	 */
	private void print() {
		System.out.println("*********************************");
		System.out.println("currentPlayerIndex : " + currPlayerIndex);
		System.out.println("pot : " + pot.amount());
		for (Player player : playerList) {
			System.out.println("userId : " + player.getUserId());
			System.out.print("petOk : " + player.potOK + " ");
			System.out.print("in : " + player.in + " ");
			System.out.print("allin : " + player.allin + " ");
			System.out.print("seat : " + player.seat + " ");
			System.out.println("hand : ");
			int holeCount = player.getHand().getNumHole();
			for (int i = 0; i < holeCount; i++) {
				System.out.println(player.getHand().getHoleCard(i));
			}
			int shardCount = player.getHand().getNumShared();
			for (int i = 0; i < shardCount; i++) {
				System.out.println(player.getHand().getSharedCard(i));
			}
			System.out.print("bankroll : " + player.getBankroll().amount()
					+ " ");
			System.out.print("bet : " + player.getBet().amount() + " ");
			System.out.println("prevBet : " + player.getPrevBet().amount()
					+ " ");
		}
	}

	/**
	 * 开始新一轮游戏
	 */
	private boolean begin() {
		if (inGame || playerList.size() < 2) {
			return false;
		}
		// 清除离开用户
		for (long userId : leavingPlayerList) {
			dropPlayer(userId);
		}
		// 初始化变量
		inGame = true;
		deck.shuffle();
		float bl = blind();
		if (bl <= 0.0f) {
			return false;
		}
		pot.add(bl);
		firstBlindGamePlayed = true;
		actionNum++;
		deal();

		currPlayerIndex = firstToBet();
		if ((currPlayerIndex < 0) || (currPlayerIndex >= MAX_PLAYERS)) {
			return false;
		}
		// TODO 广播
		print();
		return true;
	}

	protected boolean checkForCash(float amount) {
		List<Long> playersToRemove = new ArrayList<Long>();
		int size = playerList.size();
		boolean needNewDealer = false;

		for (int i = 0; i < size; i++) {
			Player player = playerList.get(i);
			if (player.getBankroll().amount() < amount) {
				System.out.println(player.getUserId()
						+ " does not have enough money to play!");
				playersToRemove.add(player.getUserId());
			}
		}
		int removeSize = playersToRemove.size();
		Player dealer = playerList.get(dealerIndex);
		for (int i = 0; i < removeSize; i++) {
			long removedUserId = playersToRemove.get(i);
			dropPlayer(removedUserId);
			if (removedUserId == dealer.getUserId()) {
				needNewDealer = true;
			}
		}
		if (needNewDealer) {
			nextDealer();
			needNewDealer = false;
		}
		if (removeSize > 0) {
			int dind = playerIndex(dealer.getUserId());
			if (dind != dealerIndex) {
				dealerIndex = dind;
			}
		}
		playersToRemove.clear();
		if (size < 2) {
			System.out.println("Need more people to play!");
			return false;
		}
		return true;
	}

	/***************************
	 * dropPlayer() removes a player from the game.
	 *
	 * @param n
	 *            The player name to drop
	 *
	 **/
	public void dropPlayer(long userId) {
		int index = playerIndex(userId);
		if (index != -1) {
			playerList.remove(index);
			System.out
					.println("Player " + userId + " removed from playerList.");
		}
	}

	/**
	 * 加入游戏
	 * 
	 * @param userId
	 * @param seat
	 * @return
	 */
	public boolean attend(long userId, int seat) {
		if (seatPlayerMap.contains(seat)) {
			return false;
		}
		Player player = new Player(userId, seat);
		player.setBankroll(ANTE);
		playerList.add(player);
		seatPlayerMap.put(seat, player);
		return begin();
	}

	/**
	 * 加入游戏
	 * 
	 * @param userId
	 * @param seat
	 * @param bankRoll
	 * @return
	 */
	public boolean attend(long userId, int seat, int bankRoll) {
		if (seatPlayerMap.contains(seat)) {
			return false;
		}
		Player player = new Player(userId, seat);
		player.setBankroll(bankRoll);
		playerList.add(player);
		seatPlayerMap.put(seat, player);
		return begin();
	}

	/**********************
	 * blind() function is similar to the ante function in that it takes the
	 * blinds and puts that amount in the pot
	 *
	 * @return Thr amount of blinds collected.
	 *
	 **/
	public float blind() {
		int size = playerList.size();
		float r = 0.0f;

		if (!checkForCash(smallBlind.amount() * 2.0f)) {
			return r;
		}

		int dealerSeat = playerList.get(dealerIndex).seat;
		if (playerList.size() == 2) {
			smallBlindSeat = dealerSeat;
		} else {
			smallBlindSeat = nextSeat(dealerSeat);
		}

		bigBlindSeat = nextSeat(smallBlindSeat);
		int smallIndex = getPlayerInSeat(smallBlindSeat);
		int bigIndex = getPlayerInSeat(bigBlindSeat);

		Player smallPlayer = playerList.get(smallIndex);
		Player bigPlayer = playerList.get(bigIndex);

		smallPlayer.subtract(smallBlind.amount());
		smallPlayer.setPrevBet(smallBlind.amount());
		r = r + smallBlind.amount();

		bigPlayer.subtract(2.0f * smallBlind.amount());
		bigPlayer.setPrevBet(2.0f * smallBlind.amount());
		r = r + 2.0f * smallBlind.amount();
		currBet = new PokerMoney(2.0f * smallBlind.amount());
		for (int i = 0; i < size; i++) {
			Player player = playerList.get(i);
			player.in = true;
			//
			// Check for players who have just joined the game. If they are new
			// to the table, they must pay the big blind.
			//

			if (firstBlindGamePlayed) {
				if (!playedInLastGame.contains(player.getUserId())) {
					if (i == smallIndex) {
						//
						// If the player is already the small blind, they only
						// have to pay another small blind amount
						//
						player.subtract(smallBlind.amount());
						player.setPrevBet(2.0f * smallBlind.amount());
						r = r + smallBlind.amount();
					} else if (i == bigIndex) {
						//
						// No need to do anything if they are already in the big
						// blind position.
						//
					} else {
						player.subtract(2.0f * smallBlind.amount());
						player.setPrevBet(2.0f * smallBlind.amount());
						r = r + 2.0f * smallBlind.amount();
					}
				}
			}
		}
		initialBet = new PokerMoney(2.0f * smallBlind.amount());
		return r;
	}

	/***********************
	 * nextAction() determines what happens next. The next action function gets
	 * called by the main PokerGame class at the end of a "round"
	 **/
	private void nextAction() {
		actionNum++;
		int size = playerList.size();
		for (int i = 0; i < size; i++) {
			Player player = playerList.get(i);
			if (player.in) {
				player.potOK = false;
				player.setBet(0.0f);
				player.setPrevBet(0.0f);
			}
		}
		if (actionNum != maxActionNum) {
			currBet = new PokerMoney(0.0f);
			currPlayerIndex = firstToBet();
			if ((currPlayerIndex < 0) || (currPlayerIndex >= size)) {
				actionNum = maxActionNum;
				skipShow = true;
			}
		}

		switch (actionNum) {
		case 2:
			flop();
			break;
		case 3:
			turn();
			break;
		case 4:
			river();
			break;
		case 5:
			show();
			break;
		default:
			break;
		}
	}

	/***********************
	 * deal() deals the initial cards. This function must be included in the
	 * game definition.
	 **/
	private void deal() {
		Card c = null;
		for (Player player : playerList) {
			// 发第一张牌
			c = deck.deal();
			player.getHand().addHoleCard(c);
			// 发第二张牌
			c = deck.deal();
			player.getHand().addHoleCard(c);
		}
	}

	/***********************
	 * flop() deals the Flop - 3 shared cards
	 **/
	private void flop() {
		int size = playerList.size();
		Card c = null;
		for (int i = 0; i < 3; i++) {
			c = deck.deal();
			for (int j = 0; j < size; j++) {
				Player player = playerList.get(j);
				if (player.in) {
					player.getHand().addSharedCard(c);
				}
			}
		}
	}

	/***********************
	 * turn() deals the 4th shared card Very similar to the flop() logic -
	 * exception the position has changed to positon 3.
	 **/
	private void turn() {
		int size = playerList.size();
		Card c = deck.deal();
		for (int i = 0; i < size; i++) {
			Player player = playerList.get(i);
			if (player.in) {
				player.getHand().addSharedCard(c);
			}
		}
	}

	/***********************
	 * river() deals the 5th and final shared card same as the turn() logic,
	 * exception card is shown at position 4
	 **/
	private void river() {
		int size = playerList.size();
		Card c = deck.deal();
		for (int i = 0; i < size; i++) {
			Player player = playerList.get(i);
			if (player.in) {
				player.getHand().addSharedCard(c);
			}
		}
	}

	/**********************
	 * show() is called at the showdown - it handles showing the cards,
	 * determining the winner and cleaning up the game.
	 **/
	private void show() {
		int size = playerList.size();
		playedInLastGame.clear();
		for (int i = 0; i < size; i++) {
			playedInLastGame.add(playerList.get(i).getUserId());
		}
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
		int size = playerList.size();
		int highPlayer = 0;
		float highHand = 0.0f;
		int numWinners = 0;
		int winner[] = new int[MAX_PLAYERS];
		playerHandValues = new float[MAX_PLAYERS];

		for (int i = 0; i < size; i++) {
			Player p = playerList.get(i);
			if (p.in) {
				playerHandValues[p.seat] = bestHand(p.getHand());
				if (playerHandValues[p.seat] > highHand) {
					highHand = playerHandValues[p.seat];
					highPlayer = i;
				}
			}
		}
		for (int i = 0; i < size; i++) {
			Player p = playerList.get(i);
			if (p.in) {
				if (playerHandValues[p.seat] == highHand) {
					winner[numWinners] = i;
					numWinners++;
				}
			}
		}

		PokerMoney winnersTake = new PokerMoney(pot.amount());
		int sidePotsSize = sidePots.size();
		if (sidePotsSize != 0) {
			for (int i = 0; i < sidePotsSize; i++) {
				winnersTake.subtract(sidePots.get(i).getPot().amount());
			}
		}

		if (numWinners == 1) {
			playerList.get(highPlayer).add(winnersTake.amount());
		} else {
			PokerMoney money = new PokerMoney(winnersTake.amount()
					/ (float) numWinners);
			playerList.get(winner[0]).add(money.amount());
			for (int i = 1; i < numWinners; i++) {
				playerList.get(winner[i]).add(money.amount());
			}
		}

		for (int i = 0; i < sidePotsSize; i++) {
			calcSidePotWinners(i);
		}
	}

	/**********************
	 * calcSidePotWinners() calculate the winner(s) of the side pots.
	 *
	 * @param potNumber
	 *            Which side pot to calculate the winner of
	 * @retrun The string which indicates who won the side pot.
	 *
	 **/
	private void calcSidePotWinners(int potNumber) {
		int highPlayer = 0;
		float highHand = 0.0f;
		String winningHand = new String();
		int numWinners = 0;
		SidePot side = sidePots.get(potNumber);
		PokerMoney pot = side.getPot();
		Long[] players = new Long[side.getIncluded().size()];
		for (int i = 0; i < players.length; i++) {
			players[i] = side.getIncluded().get(i);
		}

		int winner[] = new int[MAX_PLAYERS];
		float newPlayerHandValues[] = new float[MAX_PLAYERS];

		int numPlayers = 0;
		List<Integer> playerIdxList = new ArrayList<Integer>();
		int len = players.length;
		for (int i = 0; i < len; i++) {
			if (players[i] != null) {
				int idx = playerIndex(players[i]);
				if (idx >= 0) {
					playerIdxList.add(idx);
					numPlayers++;
				}
			}
		}

		if (numPlayers == 0) {
			return;
		}

		if (numPlayers == 1) {
			int pindex = playerIdxList.get(0);
			playerList.get(pindex).add(pot.amount());
			return;
		}

		for (int i = 0; i < numPlayers; i++) {
			int idx = playerIdxList.get(i);
			Player p = playerList.get(idx);
			newPlayerHandValues[i] = playerHandValues[p.seat];
			if (newPlayerHandValues[i] > highHand) {
				highHand = newPlayerHandValues[i];
				highPlayer = idx;
			}
		}
		for (int i = 0; i < numPlayers; i++) {
			if (newPlayerHandValues[i] == highHand) {
				winner[numWinners] = playerIdxList.get(i);
				numWinners++;
			}
		}
		winningHand = HandEvaluator.nameHand(highHand);

		PokerMoney winnersTake = new PokerMoney(pot.amount());

		if (numWinners == 1) {
			playerList.get(highPlayer).add(winnersTake.amount());
		} else {
			PokerMoney money = new PokerMoney(winnersTake.amount()
					/ (float) numWinners);
			playerList.get(winner[0]).add(money.amount());
			for (int i = 1; i < numWinners; i++) {
				playerList.get(winner[i]).add(money.amount());
			}
		}
	}

	public int playerIndex(long userId) {
		for (int i = 0; i < playerList.size(); i++) {
			if (userId == playerList.get(i).getUserId()) {
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
				dealerIndex = getPlayerInSeat(dealerSeat);
				if ((dealerIndex >= playerList.size()) || (dealerIndex < 0)) {
					dealerLeaving = true;
				} else {
					dealer = playerList.get(dealerIndex);
					float mincash = smallBlind.amount();
					if (blinds) {
						mincash = 2.0f * smallBlind.amount();
					}
					if (antes) {
						mincash = mincash + ante.amount();
					}

					if (dealer.getBankroll().amount() < mincash) {
						dealerLeaving = true;
					}
				}
				if (attempts >= MAX_PLAYERS) {
					dealerLeaving = false;
				}
			}
		}
	}

	private void resetPot() {
		pot = new PokerMoney();
	}

	private void showCards() {
		if (!skipShow) {
			int i = 0;
			for (Player player : playerList) {
				Hand hand = player.getHand();
				System.out.println("player" + i++ + ":");
				System.out.println(hand.getHoleCard(0));
				System.out.println(hand.getHoleCard(1));
				for (int j = 0; j < 5; j++) {
					System.out.println(hand.getSharedCard(j));
				}
				float score = playerHandValues[player.seat];
				System.out.println("score : " + score);
				System.out.println("best hand name : "
						+ HandEvaluator.nameHand(score));
			}
		}
	}

	private void nullifyGame() {
		inGame = false;
		smallBlindSeat = -1;
		bigBlindSeat = -1;
		actionNum = 0;
		System.out.println("END GAME");
	}

	/**********************
	 * firstToBet_HoldEm() is a generic function that can be used for HoldEm
	 * type games defining which player bets first
	 *
	 * @return The integer player index of the player who will be betting first
	 *
	 **/
	private int firstToBet() {
		int size = playerList.size();
		int dealerSeat = playerList.get(dealerIndex).seat;
		if (actionNum == 1) {
			int prevSeat = nextSeat(dealerSeat);
			prevSeat = nextSeat(prevSeat);
			if (size == 2) {
				return getPlayerInSeat(dealerSeat);
			} else {
				return getPlayerInSeat(nextSeat(prevSeat));
			}
		} else {
			if (dealerSeat < 0) {
				return -9;
			}
			int prevSeat = nextSeat(dealerSeat);
			int pindex = getPlayerInSeat(prevSeat);
			if ((pindex >= 0) && (pindex < size)) {
				while (!(playerList.get(pindex)).in) {
					prevSeat = nextSeat(prevSeat);
					pindex = getPlayerInSeat(prevSeat);
					if ((pindex < 0) || (pindex >= size)) {
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
	 * @param currSeat
	 *            The seat from which to find the next
	 * @return The next occupied seat after currSeat
	 *
	 **/
	public int nextSeat(int currSeat) {
		if ((currSeat < 0) || (currSeat >= MAX_PLAYERS)) {
			return -9;
		}
		for (int i = currSeat + 1; i < MAX_PLAYERS; i++) {
			int pindex = getPlayerInSeat(i);
			if (pindex != -9) {
				boolean playerLeaving = false;
				if (leavingPlayerList.contains(playerList.get(pindex)
						.getUserId())) {
					playerLeaving = true;
				}
				if (!playerLeaving) {
					return i;
				}
			}
		}
		for (int j = 0; j < currSeat; j++) {
			int pindex = getPlayerInSeat(j);
			if (pindex != -9) {
				boolean playerLeaving = false;
				if (leavingPlayerList.contains(playerList.get(pindex)
						.getUserId())) {
					playerLeaving = true;
				}
				if (!playerLeaving) {
					return j;
				}
			}
		}
		return -9;
	}

	/***************************
	 * getPlayerInSeat() returns the number of the player currently occupying
	 * the seat in question
	 *
	 * @param s
	 *            The seat number which is being inquired.
	 * @return The player index which is their location in playerList.
	 *
	 **/
	public int getPlayerInSeat(int s) {
		if ((s < 0) || (s >= MAX_PLAYERS)) {
			return -9;
		}
		for (int i = 0; i < playerList.size(); i++) {
			if (s == playerList.get(i).seat) {
				return i;
			}
		}
		return -9;
	}

	/***********************
	 * bestHand() returns the float value of the best possible hand that can be
	 * made from given hand h. This function is called at the showdown for each
	 * player to determine which hand they can make. The HandEvaluator class is
	 * used to figure out the best possible hand the player can make. This
	 * function is required for all games, but may look very different depending
	 * from this depending on what the rules of the game are.
	 **/
	public float bestHand(Hand h) {
		// Count total number of cards available and set of the c array to
		// contain that many cards.
		// This won't always work if the game does not allow all cards to be
		// used equally (ie Omaha HoldEm)
		//
		int numCards = h.getNumHole() + h.getNumShared();
		Card[] c = new Card[numCards];

		// Initialize the best and test variables - used for comparing ranks of
		// hands.
		//
		float best = 0.0f;
		float test = 0.0f;

		// ci is the array of 5 cards that are going to be played as the actual
		// hand.
		//
		int[] ci = new int[5];

		// Loop through all hole and shared cards and add them to the single
		// array of available cards.
		//
		for (int i = 0; i < h.getNumHole(); i++) {
			if (h.getHoleCard(i) != null) {
				c[i] = h.getHoleCard(i);
			}
		}

		for (int i = 0; i < h.getNumShared(); i++) {
			if (h.getSharedCard(i) != null) {
				c[h.getNumHole() + i] = h.getSharedCard(i);
			}
		}

		// If there are only 5 or less cards available, only one hand can be
		// made, so return a numerical value for these 5 cards. This is
		// necessary so the AI
		// can determine its hand strength - also required if betting order
		// depends on shown hand strength (7 card stud).
		//
		if (numCards == 5) {
			return he.rankHand(c[0], c[1], c[2], c[3], c[4]);
		} else if (numCards == 4) {
			return he.rankHand(c[0], c[1], c[2], c[3]);
		} else if (numCards == 3) {
			return he.rankHand(c[0], c[1], c[2]);
		} else if (numCards == 2) {
			return he.rankHand(c[0], c[1]);
		} else if (numCards == 1) {
			return he.rankHand(c[0]);
		} else if (numCards == 0) {
			return 0.0f;

			// If there are 6 or more cards, loop through all possible
			// combinations and find the best possible 5 card hand that can be
			// made.
			//
		} else if (numCards >= 6) {
			for (int i = 0; i < numCards - 4; i++) {
				for (int j = i + 1; j < numCards - 3; j++) {
					for (int k = j + 1; k < numCards - 2; k++) {
						for (int l = k + 1; l < numCards - 1; l++) {
							for (int m = l + 1; m < numCards; m++) {

								// This is where the hand is ranked. If this
								// hand is better than all previous hands that
								// could be made, then set it as the best hand.
								// Use the ci array to store card locations for
								// the best hand.
								//
								test = he
										.rankHand(c[i], c[j], c[k], c[l], c[m]);
								if (test > best) {
									best = test;
									ci[0] = i;
									ci[1] = j;
									ci[2] = k;
									ci[3] = l;
									ci[4] = m;
								}
							}
						}
					}
				}
			}
		}

		// Use the rankHand function along with the ci array to return the
		// numerical ranking of this hand.
		// (Could just return the best variable here, but in some other games
		// using this method is better so its done this way)
		//
		return he.rankHand(c[ci[0]], c[ci[1]], c[ci[2]], c[ci[3]], c[ci[4]]);
	}

	/**********************
	 * bet() is called whenever a player bets. This is a hugely complicated
	 * function, but it works pretty well I think.
	 **/
	private void bet(long userId, float bet) {
		int size = playerList.size();
		Player currPlayer = playerList.get(currPlayerIndex);
		currPlayer.setBet(bet);
		if (currPlayer.getUserId() != userId) {
			System.out.println("bad request!");
			return;
		}
		float betP = currPlayer.getPrevBet().amount();
		float betS = currPlayer.getBet().amount();
		if (currPlayer.getBankroll().compareTo(new PokerMoney()) == 1) {
			currPlayer.allin = false;
		}
		if ((currPlayer.getBankroll().amount() - betS) < MINIMUM) {
			currPlayer.allin = true;
		}
		if (((betP + betS) < currBet.amount()) && (!currPlayer.allin)) {
			if ((currBet.amount() - betP) >= currPlayer.getBankroll().amount()) {
				System.out.println("You must go all in to match the bet.");
				return;
			} else {
				System.out
						.println("You must bet at least "
								+ new PokerMoney(currBet.amount() - betP)
								+ " to call.");
				return;
			}
		} else {
			boolean potOK = true;
			boolean betOK = false;
			PokerMoney raise = new PokerMoney(betS + betP - currBet.amount());
			if ((betS + betP) > currBet.amount()) {
				if (currPlayer.allin) {
					System.out
							.println(currPlayer.getUserId() + " went All In!");
					if (initialBet.amount() == 0.0f) {
						initialBet = new PokerMoney(raise.amount());
					}
					betOK = true;
				} else if (currBet.amount() == 0.0f) {
					if (raise.compareTo(minimumBet) == -1) {
						System.out
								.println("You must bet the minimum bet of at least "
										+ minimumBet);
						return;
					} else {
						System.out.println(currPlayer.getUserId() + " bet "
								+ raise + ".");
						// TODO
						initialBet = new PokerMoney(raise.amount());
						betOK = true;
					}
				} else {
					if (raise.amount() < initialBet.amount()) {
						System.out
								.println("If you wish to raise, you must raise by at least "
										+ initialBet);
						return;
					} else {
						System.out.println(currPlayer.getUserId() + " raised "
								+ raise + ".");
						// TODO
						betOK = true;
					}
				}

				if (betOK) {
					currBet = new PokerMoney(betS + betP);
					currPlayer.subtract(betS);
					pot.add(betS);
					currPlayer.betInGame();
					currPlayer.potOK = true;
					if (currPlayer.getBankroll().compareTo(
							new PokerMoney(MINIMUM)) == -1) {
						currPlayer.allin = true;
					}
					//
					// New side pot may need to be created if this player bet
					// more than one or more other player's have in their
					// bankroll.
					//
					boolean newSidePot = false;
					for (int i = 0; i < size; i++) {
						Player player = playerList.get(i);
						if (player.in
								&& ((player.getBankroll().amount() + player
										.getPrevBet().amount()) < (betS + betP))) {
							newSidePot = true;
						}
					}
					boolean sidepot_just_created = false;
					if (newSidePot) {
						sidepot_just_created = createSidePots(betS + betP);
					}
					if (!sidepot_just_created) {
						//
						// Add raise money to latest side pot if one was not
						// just created.
						//
						int lastPot = sidePots.size() - 1;
						if (lastPot >= 0) {
							List<Long> incLst = sidePots.get(lastPot)
									.getIncluded();
							float tc = sidePots.get(lastPot).toCall[currPlayerIndex]
									.amount();
							if (incLst.contains(currPlayer.getUserId())) {
								sidePots.get(lastPot).addMoney(
										tc + raise.amount());
								for (int i = 0; i < size; i++) {
									sidePots.get(lastPot).toCall[i].add(raise
											.amount());
								}
								sidePots.get(lastPot).toCall[currPlayerIndex] = new PokerMoney();
							}
							//
							// Must loop through previous pots to make sure
							// toCall is satisfied.
							//
							for (int i = 0; i < lastPot; i++) {
								List<Long> incLst2 = sidePots.get(i)
										.getIncluded();
								float tc2 = sidePots.get(i).toCall[currPlayerIndex]
										.amount();
								if (incLst2.contains(currPlayer.getUserId())) {
									sidePots.get(i).addMoney(tc2);
									sidePots.get(i).toCall[currPlayerIndex] = new PokerMoney();
								}
							}
						}
					} else {
						int newPot = sidePots.size() - 1;
						float amount = betS
								- sidePots.get(newPot).getPot().amount();
						for (int i = 0; i < sidePots.size() - 1; i++) {
							SidePot sp = sidePots.get(i);
							List<Long> incLst = sp.getIncluded();
							if ((incLst.contains(currPlayer.getUserId()))
									&& (sp.toCall[currPlayerIndex].amount() > 0.0f)) {
								float amt = 0.0f;
								if (i == sidePots.size() - 2) {
									float tc = sp.toCall[currPlayerIndex]
											.amount();
									amt = tc
											+ raise.amount()
											- sidePots.get(newPot).getPot()
													.amount();
									for (int k = 0; k < size; k++) {
										sidePots.get(i).toCall[k].add(raise
												.amount()
												- sidePots.get(newPot).getPot()
														.amount());
									}
								} else if (amount > sp.toCall[currPlayerIndex]
										.amount()) {
									amt = sp.toCall[currPlayerIndex].amount();
								} else {
									amt = amount;
								}
								sidePots.get(i).addMoney(amt);
								sidePots.get(i).toCall[currPlayerIndex] = new PokerMoney();
								amount = amount - amt;
							}
						}
					}
					//
					// Make sure all the players have their potOK flag set to
					// false if required.
					//
					for (int i = 0; i < size; i++) {
						Player player = playerList.get(i);
						player.potOK = false;
						if (player.allin) {
							player.potOK = true;
						}
						if (leavingPlayerList.contains(player.getUserId())) {
							player.potOK = true;
						}
					}
					playerList.get(currPlayerIndex).potOK = true;
					for (int i = 0; i < size; i++) {
						Player player = playerList.get(i);
						if (player.in && !player.potOK) {
							potOK = false;
						}
					}
				}
			} else {
				currPlayer.subtract(betS);
				pot.add(betS);
				currPlayer.betInGame();
				currPlayer.potOK = true;
				if (currPlayer.getBankroll().compareTo(new PokerMoney(MINIMUM)) == -1) {
					currPlayer.allin = true;
				}
				for (int i = 0; i < size; i++) {
					Player player = playerList.get(i);
					if (player.in && !player.potOK && !player.allin) {
						potOK = false;
					}
				}
				//
				// Add money to side pots as required.
				//
				float amount = betS;
				for (int i = 0; i < sidePots.size(); i++) {
					SidePot sp = sidePots.get(i);
					List<Long> incLst = sp.getIncluded();
					if ((incLst.contains(currPlayer.getUserId()))
							&& (sp.toCall[currPlayerIndex].amount() > 0.0f)) {
						float amt = 0.0f;
						if (amount > sp.toCall[currPlayerIndex].amount()) {
							amt = sp.toCall[currPlayerIndex].amount();
						} else {
							amt = amount;
						}
						sidePots.get(i).addMoney(amt);
						sidePots.get(i).toCall[currPlayerIndex].subtract(amt);
						amount = amount - amt;
					}
				}
				if (currPlayer.allin) {
					System.out
							.println(currPlayer.getUserId() + " went All In!");
					betOK = true;
				} else if (currBet.amount() == 0.0f) {
					System.out.println(currPlayer.getUserId() + " checked.");
					betOK = true;
				} else {
					betOK = true;
				}
			}

			if (betOK && !potOK) {
				int prevSeat = currPlayer.seat;
				boolean foundNext = false;
				while (!foundNext) {
					prevSeat = nextSeat(prevSeat);
					int playerIdx = getPlayerInSeat(prevSeat);
					Player player = playerList.get(playerIdx);
					if (player.in && !player.allin) {
						foundNext = true;
						currPlayerIndex = getPlayerInSeat(prevSeat);
					}
				}
				if ((actionNum + 1) <= maxActionNum) {
					// TODO
				} else {
					// TODO
				}
			}

			if (betOK) {
				int allins = 0, ins = 0;
				for (int i = 0; i < size; i++) {
					Player player = playerList.get(i);
					if (player.allin) {
						allins++;
					}
					if (player.in) {
						ins++;
					}
				}
				if (allins >= (ins - 1)) {
					if (potOK) {
						while (actionNum < maxActionNum) {
							endAction();
						}
					}
				} else {
					if (potOK) {
						endAction();
					}
				}
			}
		}
		print();
	}

	/**
	 * 让牌
	 * 
	 * @param userId
	 */
	private void passBet(long userId) {
		int size = playerList.size();
		Player currPlayer = playerList.get(currPlayerIndex);
		if (currPlayer.getUserId() != userId) {
			System.out.println("bad request!");
			return;
		}
		currPlayer.potOK = true;

		boolean potOK = true;

		for (int i = 0; i < size; i++) {
			Player player = playerList.get(i);
			if (player.in && !player.potOK && !player.allin) {
				potOK = false;
			}
		}

		if (!potOK) {
			int prevSeat = currPlayer.seat;
			boolean foundNext = false;
			while (!foundNext) {
				prevSeat = nextSeat(prevSeat);
				int playerIdx = getPlayerInSeat(prevSeat);
				Player player = playerList.get(playerIdx);
				if (player.in && !player.allin) {
					foundNext = true;
					currPlayerIndex = getPlayerInSeat(prevSeat);
				}
			}
			if ((actionNum + 1) <= maxActionNum) {
				// TODO
			} else {
				// TODO
			}
		}

		if (true) {
			int allins = 0, ins = 0;
			for (int i = 0; i < size; i++) {
				Player player = playerList.get(i);
				if (player.allin) {
					allins++;
				}
				if (player.in) {
					ins++;
				}
			}
			if (allins >= (ins - 1)) {
				if (potOK) {
					while (actionNum < maxActionNum) {
						endAction();
					}
				}
			} else {
				if (potOK) {
					endAction();
				}
			}
		}

		print();
	}

	/**
	 * 弃牌
	 * 
	 * @param userId
	 */
	private void giveupBet(long userId) {
		int size = playerList.size();
		Player currPlayer = playerList.get(currPlayerIndex);
		if (currPlayer.getUserId() != userId) {
			System.out.println("bad request!");
			return;
		}
		currPlayer.in = false;

		boolean potOK = true;

		for (int i = 0; i < size; i++) {
			Player player = playerList.get(i);
			if (player.in && !player.potOK && !player.allin) {
				potOK = false;
			}
		}

		if (!potOK) {
			int prevSeat = currPlayer.seat;
			boolean foundNext = false;
			while (!foundNext) {
				prevSeat = nextSeat(prevSeat);
				int playerIdx = getPlayerInSeat(prevSeat);
				Player player = playerList.get(playerIdx);
				if (player.in && !player.allin) {
					foundNext = true;
					currPlayerIndex = getPlayerInSeat(prevSeat);
				}
			}
			if ((actionNum + 1) <= maxActionNum) {
				// TODO
			} else {
				// TODO
			}
		}

		if (true) {
			int allins = 0, ins = 0;
			for (int i = 0; i < size; i++) {
				Player player = playerList.get(i);
				if (player.allin) {
					allins++;
				}
				if (player.in) {
					ins++;
				}
			}
			if (allins >= (ins - 1)) {
				if (potOK) {
					while (actionNum < maxActionNum) {
						endAction();
					}
				}
			} else {
				if (potOK) {
					endAction();
				}
			}
		}

		print();
	}

	/**
	 * 站起
	 * 
	 * @param userId
	 */
	private void stand(long userId) {
		int idx = playerIndex(userId);
		Player p = playerList.get(idx);
		if (p == null) {
			System.out.println("bad request!");
			return;
		}
		p.in = false;
		leavingPlayerList.add(userId);
		seatPlayerMap.removeKey(p.seat);

		if (currPlayerIndex == idx) {
			Player currPlayer = playerList.get(currPlayerIndex);
			int size = playerList.size();
			boolean potOK = true;

			for (int i = 0; i < size; i++) {
				Player player = playerList.get(i);
				if (player.in && !player.potOK && !player.allin) {
					potOK = false;
				}
			}

			if (!potOK) {
				int prevSeat = currPlayer.seat;
				boolean foundNext = false;
				while (!foundNext) {
					prevSeat = nextSeat(prevSeat);
					int playerIdx = getPlayerInSeat(prevSeat);
					Player player = playerList.get(playerIdx);
					if (player.in && !player.allin) {
						foundNext = true;
						currPlayerIndex = getPlayerInSeat(prevSeat);
					}
				}
				if ((actionNum + 1) <= maxActionNum) {
					// TODO
				} else {
					// TODO
				}
			}

			if (true) {
				int allins = 0, ins = 0;
				for (int i = 0; i < size; i++) {
					Player player = playerList.get(i);
					if (player.allin) {
						allins++;
					}
					if (player.in) {
						ins++;
					}
				}
				if (allins >= (ins - 1)) {
					if (potOK) {
						while (actionNum < maxActionNum) {
							endAction();
						}
					}
				} else {
					if (potOK) {
						endAction();
					}
				}
			}
			print();
		}
	}

	/**********************
	 * endAction() is used to clean up the current round of action and call the
	 * game defined nextAction() function.
	 **/
	private void endAction() {
		initialBet = new PokerMoney(0.0f);
		for (int i = 0; i < sidePots.size(); i++) {
			for (int j = 0; j < playerList.size(); j++) {
				sidePots.get(i).toCall[j] = new PokerMoney();
			}
		}
		nextAction();
	}

	/**********************
	 * createSidePots() is called after somebody raises. If necessary, it will
	 * create side pots because some player(s) in the game cannot match the bet.
	 *
	 * @param bet
	 *            The bet value which is needed to stay in the game.
	 * @return Whether of not the side pots were successfully created.
	 *
	 **/
	protected boolean createSidePots(float bet) {
		boolean ret = false;
		//
		// First need to order players in a list of how much they have relative
		// to how much they owe.
		//
		int size = playerList.size();
		long[] names = new long[size];
		float[] amounts = new float[size];
		int count = 0;
		for (int i = 0; i < size; i++) {
			Player p = playerList.get(i);
			if (p.in) {
				names[count] = p.getUserId();
				amounts[count] = p.getBankroll().amount()
						+ p.getPrevBet().amount() - bet;
				count++;
			}
		}
		long tempN = -1L;
		float tempA = 0.0f;
		for (int i = 0; i < count; i++) {
			for (int j = i; j < count; j++) {
				if (amounts[j] < amounts[i]) {
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
		// Create a side pot for all players which amount is negative
		//
		for (int i = 0; i < count; i++) {
			if (amounts[i] < 0) {
				List<Long> excludedList = new ArrayList<Long>();
				List<Long> includedList = new ArrayList<Long>();
				float amount = -amounts[i];
				for (int j = i + 1; j < count; j++) {
					if (amounts[j] < 0.0f) {
						amount = amount + amounts[j];
					}
				}
				for (int j = 0; j <= i; j++) {
					excludedList.add(names[j]);
				}
				for (int j = i + 1; j < count; j++) {
					includedList.add(names[j]);
				}
				//
				// Do not create a side pot with the exact same list of excluded
				// players - that side pot will be redundant
				//
				boolean needToCreate = true;
				int sidePotsSize = sidePots.size();
				for (int j = 0; j < sidePotsSize; j++) {
					boolean listsSame = true;
					List<Long> spExLst = sidePots.get(j).getExcluded();
					if (excludedList.size() != spExLst.size()) {
						listsSame = false;
					} else {
						for (int k = 0; k < excludedList.size(); k++) {
							if (!excludedList.get(k).equals(spExLst.get(k))) {
								listsSame = false;
							}
						}
						if (listsSame) {
							needToCreate = false;
						}
					}
				}
				if (needToCreate) {
					sidePots.add(new SidePot(includedList, excludedList,
							amount, size, currPlayerIndex));
					ret = true;
				}
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		int roomId = 100015;
		TexasHoldEm texasHoldEm = new TexasHoldEm(roomId);
		/*
		 * texasHoldEm.attend(1L, 0, 100); texasHoldEm.attend(2L, 1, 90);
		 * texasHoldEm.attend(3L, 2, 90); texasHoldEm.attend(4L, 3, 50);
		 */
		// 第一轮
		// 第一步
		texasHoldEm.attend(1L, 0);
		texasHoldEm.attend(2L, 1);
		texasHoldEm.attend(3L, 2);
		texasHoldEm.attend(4L, 3);
		// 第二步
		texasHoldEm.bet(1, 50);
		texasHoldEm.bet(2, 40);
		// 第三步
		texasHoldEm.bet(2, 20);
		texasHoldEm.bet(1, 20);
		// 第四步
		texasHoldEm.passBet(2);
		texasHoldEm.passBet(1);
		// 第五步
		texasHoldEm.passBet(2);
		texasHoldEm.passBet(1);
		// 第二轮
		texasHoldEm.begin();
		texasHoldEm.bet(1, 40);
		texasHoldEm.bet(2, 40);
		texasHoldEm.bet(3, 30);
		texasHoldEm.bet(4, 20);
		/*
		 * texasHoldEm.attend(1L, 0); texasHoldEm.attend(2L, 1);
		 * 
		 * texasHoldEm.bet(1, 30); texasHoldEm.bet(2, 20);
		 * 
		 * texasHoldEm.bet(2, 100); texasHoldEm.giveupBet(1);
		 */
		/*
		 * texasHoldEm.passBet(2); texasHoldEm.passBet(3);
		 * texasHoldEm.passBet(4); texasHoldEm.passBet(1);
		 * 
		 * texasHoldEm.passBet(2); texasHoldEm.passBet(3);
		 * texasHoldEm.passBet(4); texasHoldEm.passBet(1);
		 * 
		 * texasHoldEm.passBet(2); texasHoldEm.passBet(3);
		 * texasHoldEm.passBet(4); texasHoldEm.passBet(1);
		 */
	}
}