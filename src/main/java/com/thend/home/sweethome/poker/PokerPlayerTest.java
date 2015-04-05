package com.thend.home.sweethome.poker;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class PokerPlayerTest {
	@Test
    public void test1()
    {
        PokerPlayer player1 = new PokerPlayer("H2F2X2");
        assertTrue(player1.getPokerType() == PokerType.BAOZI);
         
        PokerPlayer player2 = new PokerPlayer("H2H3H4");
        assertTrue(player2.getPokerType() == PokerType.TONGHUASHUN);
        
        PokerPlayer player3 = new PokerPlayer("H2F3H4");
        assertTrue(player3.getPokerType() == PokerType.SHUNZI);
         
        PokerPlayer player4 = new PokerPlayer("H3F4H4");
        assertTrue(player4.getPokerType() == PokerType.DUIZI);
         
        PokerPlayer player5 = new PokerPlayer("H3F6H4");
        assertTrue(player5.getPokerType() == PokerType.SANPAI);
    }
     
    @Test
    public void test2()
    {
        /**
         * case 1:都是豹子时，比较大小
         */
        PokerPlayer player11 = new PokerPlayer("H2F2X2");
        PokerPlayer player12 = new PokerPlayer("HAFAXA");
        assertTrue(player11.compareTo(player12) < 0);
         
        /**
         * case 2:豹子大于同花顺
         */
        PokerPlayer player21 = new PokerPlayer("H2F2X2");
        PokerPlayer player22 = new PokerPlayer("H3H5H4");
        assertTrue(player21.compareTo(player22) > 0);
         
        /**
         * case 3:都是同花顺时，比较大小
         */
        PokerPlayer player31 = new PokerPlayer("H3H5H4");
        PokerPlayer player32 = new PokerPlayer("F8F6F7");
        assertTrue(player31.compareTo(player32) < 0);
         
        /**
         * case 4:同花顺大于顺子
         */
        PokerPlayer player41 = new PokerPlayer("H3H5H4");
        PokerPlayer player42 = new PokerPlayer("F5H6F7");
        assertTrue(player41.compareTo(player42) > 0);
         
        /**
         * case 5:都是顺子时，比较最大牌（先比较最大牌的牌面值，相同则比较花色）
         */
        PokerPlayer player51 = new PokerPlayer("H3X5H4");
        PokerPlayer player52 = new PokerPlayer("F5H6F7");
        assertTrue(player51.compareTo(player52) < 0);
         
        /**
         * case 6:顺子大于对子
         */
        PokerPlayer player61 = new PokerPlayer("H3X5H4");
        PokerPlayer player62 = new PokerPlayer("F5H7F7");
        assertTrue(player61.compareTo(player62) > 0);
         
        /**
         * case 7.1:都是对子时，比较对子大小
         */
        PokerPlayer player71 = new PokerPlayer("H3F5H5");
        PokerPlayer player72 = new PokerPlayer("XAH6FA");
        assertTrue(player71.compareTo(player72) < 0);
         
        /**
         * case 7.2:都是对子时，对子大小相同，比较散牌的大小
         */
        PokerPlayer player73 = new PokerPlayer("HAF5MA");
        PokerPlayer player74 = new PokerPlayer("XAH6FA");
        assertTrue(player73.compareTo(player74) < 0);
         
        /**
         * case 7.3:都是对子时，比较大小(对子大小相同，比较散牌的大小)
         */
        PokerPlayer player75 = new PokerPlayer("HAF3MA");
        PokerPlayer player76 = new PokerPlayer("XAH6FA");
        assertTrue(player75.compareTo(player76) < 0);
         
        /**
         * case 7.4:都是对子时，三张牌牌面值相同，比较对子中最大牌的花色
         */
        PokerPlayer player77 = new PokerPlayer("HAX5MA");
        PokerPlayer player78 = new PokerPlayer("XAM5FA");
        assertTrue(player77.compareTo(player78) > 0);
         
        /**
         * case 8:对子大于散牌
         */
        PokerPlayer player81 = new PokerPlayer("H3F5X3");
        PokerPlayer player82 = new PokerPlayer("FQH9F7");
        assertTrue(player81.compareTo(player82) > 0);
         
        /**
         * case 9.1:都是散牌时，比较最大牌的牌面值大小
         */
        PokerPlayer player91 = new PokerPlayer("H3F5HJ");
        PokerPlayer player92 = new PokerPlayer("X4H6FT");
        assertTrue(player91.compareTo(player92) > 0);
         
        /**
         * case 9.2:都是散牌时，最大牌面值相同时，比较第2大牌的牌面值大小
         */
        PokerPlayer player93 = new PokerPlayer("H3X5HJ");
        PokerPlayer player94 = new PokerPlayer("X4M2FJ");
        assertTrue(player93.compareTo(player94) > 0);
         
        /**
         * case 9.3:都是散牌时，最大、第2大牌面值相同时，比较最小牌的牌面值大小
         */
        PokerPlayer player95 = new PokerPlayer("H3X5HJ");
        PokerPlayer player96 = new PokerPlayer("X4M5FJ");
        assertTrue(player95.compareTo(player96) < 0);
         
        /**
         * case 9.4:都是散牌时，三张牌的牌面值大小全部相同，则比较最大牌的花色
         */
        PokerPlayer player97 = new PokerPlayer("H3X5HJ");
        PokerPlayer player98 = new PokerPlayer("X3M5FJ");
        assertTrue(player97.compareTo(player98) > 0);
    }
    
    public static void main(String[] args) {
    	List<String> poker = new ArrayList<String>(Arrays.asList(Poker.POKER));
    	int size = poker.size();
    	Random ran = new Random();
    	List<PokerPlayer> table = new ArrayList<PokerPlayer>();
    	for(int i=0;i<4;i++) {
    		String playerPoker = "";
    		for(int j=0;j<3;j++) {
    			int idx = ran.nextInt(size);
    			playerPoker += poker.remove(idx);
    			size -= 1;
    		}
    		table.add(new PokerPlayer(playerPoker));
    	}
    	int winIdx = 0;
    	PokerPlayer winPlayer = table.get(0);
    	for(int i=1;i<4;i++) {
    		if(table.get(i).compareTo(winPlayer) > 0) {
    			winIdx = i;
    			winPlayer = table.get(i);
    		}
    	}
    	for(PokerPlayer player : table) {
    		System.out.println(player.getAllPokers() + "#" + player.getPokerType());
    	}
    	System.out.println("win index : " + winIdx);
    }
}
