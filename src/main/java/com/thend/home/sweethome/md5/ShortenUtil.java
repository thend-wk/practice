package com.thend.home.sweethome.md5;
import java.util.Stack;

/**
 * @author wangkai
 */
public class ShortenUtil {
	
    public static final int radix = 62;// [0-9A-Za-z]

    public static final char[] elements = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };

    public static final String elementsString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String numberToString(long x) {
        x = (long) (x + Math.pow(2, 31));
        StringBuilder sb = new StringBuilder();
        Stack<Character> stack = new Stack<Character>();
        long quotient, remainder;
        while (x >= radix) {
            quotient = x / radix;
            remainder = x % radix;
            x = quotient;
            stack.add(elements[(int) remainder]);
        }
        stack.add(elements[(int) x]);
        while (!stack.empty()) {
            sb.append(stack.pop());
        }
        return sb.toString();
    }

    public static int StringToInt(String str) {
        int x = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            int charCode = elementsString.indexOf(str.charAt(i));
            x += charCode * Math.pow(radix, (str.length() - i - 1));
        }
        return x;
    }
}
