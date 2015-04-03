package com.thend.home.sweethome.poker;

/**
 * 一张扑克牌对象
 * 1.实现Comparable接口，通过compareTo方法进行比较大小
 * 2.比较规则：
 * 1)先看牌面数字，数字大的就大;
 * 2)牌面数字相同时，花色大的就大;
 */
public class Poker implements Comparable<Poker> {
	
	public static final String[] POKER = {"H2","H3","H4","H5","H6","H7","H8","H9","HT","HJ","HQ","HK","HA",
										 "X2","X3","X4","X5","X6","X7","X8","X9","XT","XJ","XQ","XK","XA",
										 "M2","M3","M4","M5","M6","M7","M8","M9","MT","MJ","MQ","MK","MA",
										 "F2","F3","F4","F5","F6","F7","F8","F9","FT","FJ","FQ","FK","FA"};

	/**
     * 扑克牌的牌面数字
     */
    private PokerActor pokerActor;
     
    /**
     * 扑克牌的花色
     */
    private PokerColor pokerColor;
     
    /**
     * 长度为2的字符串，接收扑克牌的数字和花色：第0位为数字，第1位为花色
     */
    public Poker(String pokerAttr) {
        char pokerActor = pokerAttr.charAt(1);
        char pokerColor = pokerAttr.charAt(0);
         
        setPokerActor(PokerActor.getPokerActor(pokerActor));
        setPokerColor(PokerColor.getPokerColor(pokerColor));
    }

    public PokerActor getPokerActor() {
        return pokerActor;
    }
     
    public void setPokerActor(PokerActor pokerActor)
    {
        this.pokerActor = pokerActor;
    }
     
    public PokerColor getPokerColor() {
        return pokerColor;
    }
     
    public void setPokerColor(PokerColor pokerColor) {
        this.pokerColor = pokerColor;
    }
     
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pokerActor == null) ? 0 : pokerActor.hashCode());
        result = prime * result + ((pokerColor == null) ? 0 : pokerColor.hashCode());
        return result;
    }
     
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Poker other = (Poker)obj;
        if (pokerActor != other.pokerActor) {
            return false;
        }
        if (pokerColor != other.pokerColor) {
            return false;
        }
        return true;
    }
    
    public int compareTo(Poker o) {
        // 先去比较牌面大小
        int compare = getPokerActor().compareTo(o.getPokerActor());
         
        // 牌面相同时
        if (compare == 0) {
            // 比较花色
            return getPokerColor().compareTo(o.getPokerColor());
        }
        return compare;
    }
     
    @Override
    public String toString() {
        return "Poker [pokerActor=" + pokerActor + ", pokerColor=" + pokerColor + "]";
    }
}
