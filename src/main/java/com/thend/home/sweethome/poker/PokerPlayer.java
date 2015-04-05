package com.thend.home.sweethome.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * 炸金花玩家
 *
 */
public class PokerPlayer implements Comparable<PokerPlayer> {
	
	private String allpokers;
	     
    /**
     * 玩家有三张扑克牌
     */
    private List<Poker> pokers;
     
    /**
     * 三张扑克牌的类型：豹子、同花顺等
     */
    private PokerType pokerType;
     
    /**
     * 每个玩家默认有3张扑克牌 
     */
    public PokerPlayer(String pokersStr) {
        this.allpokers = pokersStr;
        init(pokersStr);
    }
     
    public String getAllPokers() {
        return allpokers;
    }
     
    /**
     * 根据发的3张牌计算出玩家的牌的类型
     * @param pokersStr
     */
    private void init(String pokersStr) {
        pokers = new ArrayList<Poker>(3);
        int index = 0;
        int size = pokersStr.length();
         
        Poker poker = null;
        while (index < size) {
            poker = new Poker(pokersStr.substring(index, index + 2));
            pokers.add(poker);
            index += 2;
        }
         
        // 对三张牌从小到大排序
        Collections.sort(pokers);
         
        // 确定三张牌的类型
        if (isBaozi()) {
            pokerType = PokerType.BAOZI;
        } else if (isTonghuashun()) {
            pokerType = PokerType.TONGHUASHUN;
        } else if (isShunzi()) {
            pokerType = PokerType.SHUNZI;
        } else if (isDuizi()) {
            pokerType = PokerType.DUIZI;
        } else {
            pokerType = PokerType.SANPAI;
        }
    }
     
    /**
     * 
     * 判断是否是豹子(豹子要求3张牌面大小相同)
     * @return
     */
    private boolean isBaozi() {
        Poker poker = pokers.get(0);
        for (int i = 1, size = pokers.size(); i < size; i++) {
            if(poker.getPokerActor() != pokers.get(i).getPokerActor()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断是否是顺子
     * @return
     */
    private boolean isShunzi() {
        for (int i = 1, size = pokers.size(); i < size; i++) {
            if (pokers.get(i - 1).getPokerActor().compareTo(pokers.get(i).getPokerActor()) != -1) {
                return false;
            }
        }
        return true;
    }
     
    /**
     * 判断是否是同花顺
     * @return
     */
    private boolean isTonghuashun() {
        if (!isShunzi()) {
            return false;
        }
         
        Poker poker = pokers.get(0);
        for (int i = 1, size = pokers.size(); i < size; i++) {
            if (poker.getPokerColor() != pokers.get(i).getPokerColor()) {
                return false;
            }
        }
        return true;
    }
     
    /**
     * 是否是对子
     * @return
     */
    private boolean isDuizi()
    {
        for (int i = 1, size = pokers.size(); i < size; i++) {
            if (pokers.get(i - 1).getPokerActor().compareTo(pokers.get(i).getPokerActor()) == 0) {
                return true;
            }
        }
        return false;
    }
     
    /**
     * 获取扑克玩家手中的对子对应的扑克牌(不区分花色)
     * @return
     */
    private Poker getDuiziPoker() {
        for (int i = 1, size = pokers.size(); i < size; i++) {
            if (pokers.get(i - 1).getPokerActor().compareTo(pokers.get(i).getPokerActor()) == 0) {
                return pokers.get(i);
            }
        }
        return null;
    }
     
    /**
     * 获取玩家手中非成对的那张牌
     * @return
     */
    private Poker getNoDuiziPoker()
    {
        // 玩家只有3张牌，且是对子，而牌又是经过排序的，前2张相等，则最后一张是不成对的，否则后2张成对，第0张不同
        if (pokers.get(0).compareTo(pokers.get(1)) == 0) {
            return pokers.get(2);
        } else {
            return pokers.get(0);
        }
    }
     
    public PokerType getPokerType()
    {
        return pokerType;
    }
     
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pokerType == null) ? 0 : pokerType.hashCode());
        result = prime * result + ((pokers == null) ? 0 : pokers.hashCode());
        return result;
    }
     
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PokerPlayer other = (PokerPlayer)obj;
        if (pokerType != other.pokerType) {
            return false;
        }
        if (pokers == null) {
            if (other.pokers != null) {
                return false;
            }
        } else if (!pokers.equals(other.pokers)) {
            return false;
        }
        return true;
    }
     
    @Override
    public String toString()
    {
        return "pokerPlayer [pokers=" + pokers + ", pokerType=" + pokerType + "]";
    }
    
    public int compareTo(PokerPlayer o)
    {
    	int compare = getPokerType().compareTo(o.getPokerType());
        if (compare == 0) {
            switch (getPokerType()) {
                /**
                 * 豹子、同花顺、顺子直接比较最大牌（最大牌会先比大小，再比花色）
                 */
                case BAOZI:
                case TONGHUASHUN:
                case SHUNZI: {
                    return pokers.get(2).compareTo(o.pokers.get(2));
                }
                case DUIZI: {
                    /**
                     * 对子比较
                     */
                    Poker duizi1 = getDuiziPoker();
                    Poker duizi2 = o.getDuiziPoker();
                    // 先比较对子大小，对子大小相同时，比较散牌大小
                    if (duizi1.getPokerActor() == duizi2.getPokerActor()) {
                        compare = getNoDuiziPoker().getPokerActor().compareTo(o.getNoDuiziPoker().getPokerActor());
                        // 散牌大小相同时，比较对子中牌的花色
                        if (compare == 0) {
                            return duizi1.getPokerColor().compareTo(duizi2.getPokerColor());
                        }
                        return compare;
                    } else {
                        // 对子大小不同时，直接比较对子大小
                        return duizi1.getPokerActor().compareTo(duizi2.getPokerActor());
                    }
                }
                case SANPAI: {
                    // 散牌依次从最大数开始比较，只比较牌面值大小，如果相同，则从第二大值开始比较，直到不同或者全部比较完毕为止
                    for (int size = pokers.size(), i = size - 1; i >= 0; i--) {
                        compare = pokers.get(i).getPokerActor().compareTo(o.pokers.get(i).getPokerActor());
                        if (compare != 0) {
                            return compare;
                        }
                    }
                    // 说明三张牌的牌面值全部相同，则比较最大牌的花色
                    return pokers.get(2).getPokerColor().compareTo(o.pokers.get(2).getPokerColor());
                }
            }
        }
        return compare;
    }
}
