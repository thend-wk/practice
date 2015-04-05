package com.thend.home.sweethome.poker;
/**
 * 牌的数字
 *
 */
public enum PokerActor {
	TWO('2'),
	THREE('3'),
	FOUR('4'),
	FIVE('5'),
	SIX('6'),
	SEVEN('7'),
	EIGHT('8'),
	NIME('9'),
	TEN('T'),
	J('J'),
	Q('Q'),
	K('K'),
	A('A');
	
    private char num;
    
    private PokerActor(char num) {
        this.num = num;
    }

    private char getNum() {
        return num;
    }
    /**
     * 根据牌面数字找到扑克牌对应的牌面枚举对象
     * @param num
     * @return
     */
    public static PokerActor getPokerActor(char num) {
        for(PokerActor pokerActor : PokerActor.values()) {
            if(pokerActor.getNum() == num) {
                return pokerActor;
            }
        }
        return null;
    }
}
