package com.thend.home.sweethome.poker;
/*
 * 牌的花色
 */
public enum PokerColor {
	F('F'),
	M('M'),
	X('X'),
	H('H');
    
    private char color;
    
    private PokerColor(char color) {
        this.color = color;
    }
    
    /**
     * 根据花色字符查找扑克牌的花色枚举对象
     * @param color
     * @return
     */
    public static PokerColor getPokerColor(char color) {
        for (PokerColor pokerColor : PokerColor.values()) {
            if(pokerColor.color == color) {
                return pokerColor;
            }
        }
        return null;
    }
}
