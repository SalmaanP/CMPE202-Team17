/**
 * BitStringUtil
 * 
 * @author Taylor Born
 * @version January 2014
 */
public abstract class BitStringUtil
{
    protected static final int BITS_PER_CHAR = 15;
    
    /**
     * Convert 32 bits to a signed int. Where 32 0s represent the int min, and 32 1s represent the int max.
     * @param bitString Binary String of length 32.
     * @return Signed int.
     */
    public static int encodeBitStringTo32BitSignedInt(String bitString)
    {
        if (bitString.length() != 32)
            throw new IllegalArgumentException("Input must represent 32 bits (1s and 0s).");
        long num = 0;
        for (char ch : bitString.toCharArray())
        {
            num = num << 1;
            if (ch == '1')
                num++;
            else if (ch != '0')
                throw new IllegalArgumentException("Input must represent 32 bits (1s and 0s).");
        }
        return (int)(num - Integer.MIN_VALUE);
    }
    
    /**
     * Convert a signed int to 32 bits. Where 32 0s represent the int min, and 32 1s represent the int max.
     * @param n Number to be converted.
     * @return Binary String of length 32.
     */
    public static String decodeSignedIntTo32BitString(int n)
    {
        long value = (long)n -(long)Integer.MIN_VALUE;
        
        
        String bits = "";
        for (int i = 0; i < 32; i++)
        {
            bits = (value % 2 == 1 ? '1' : '0') + bits;
            value = value >>> 1;
        }
        return bits;
    }
    
//     public static void TEST()
//     {
//         int[] values = new int[]{ Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2, -3, -2, -1, 0, 1, 2, 3, Integer.MAX_VALUE - 2, Integer.MAX_VALUE - 1, Integer.MAX_VALUE };
//         
//         for (int n : values)
//         {
//             String bits = decodeSignedIntTo32BitString(n);
//             int newN = encodeBitStringTo32BitSignedInt(bits);
//             System.out.println(n + " : " + bits + " : " + newN);
//         }
//     }
    
    /**
     * Make a binary String to length 32 by adding 1s or 0s to the left or right side.
     * @param bitString Binary String of length less than or equal to 32.
     * @param toTheRight Whether additional bits are to go to the right/left side of the existing bits.
     * @param ones Whether additional bits will be 1s or 0s.
     * @return Binary String of length 32.
     */
    public static String padBitStringTo32(String bitString, boolean toTheRight, boolean ones)
    {
        if (bitString.length() > 32)
            throw new IllegalArgumentException("Input of bits exceeds 32 in length!");
        char ch = ones ? '1' : '0';
        if (toTheRight)
        {
            while (bitString.length() < 32)
                bitString += ch;
        }
        else
            while (bitString.length() < 32)
                bitString = ch + bitString;
        return bitString;
    }
    
    /**
     * Convert binary to decimal.
     * @param s String of 1s and 0s.
     * @return Unsigned according to input base 2.
     */
    public static int bitStringToNumber(String s)
    {
        int n = 0;
        for (char ch : s.toCharArray())
        {
            n = n << 1;
            if (ch == '1')
                n++;
            else if (ch != '0')
                throw new IllegalArgumentException("Input must represent bits (1s and 0s).");
        }
        
//         int n = 0;
//         for (int i = 0; i < s.length(); i++)
//             if (s.charAt(i) == '1')
//                 n += Math.pow(2, s.length() - 1 - i);
        return n;
    }
    
    /**
     * Convert unsigned number to binary String (1s and 0s).
     * @param n The unsigned number to be converted to base 2.
     * @param i Greatest power of 2 to consider. (Length of return String).
     * @return String of 1s and 0s representing inputed number, base 2.
     */
    public static String numberToBitString(int n, int i)
    {
        String s = "";
        for (i = i - 1; i > -1; i--)
            if (n >= Math.pow(2, i))
            {
                s += '1';
                n -= Math.pow(2, i);
            }
            else
                s += '0';
        return s;
    }
    
    /**
     * @param bitString String of 1s and 0s.
     * @return String of characters representing input.
     * @see evaluateToBitString(String)
     */
    public static String evaluateBitString(String bitString)
    {
        bitString = padBitString(bitString);
        String s = "";
        
        // Translate each group of BITS_PER_CHAR bits into a char
        for (int i = 0; i < bitString.length(); i += BITS_PER_CHAR)
        {
            char ch = (char)bitStringToNumber(bitString.substring(i, i + BITS_PER_CHAR));
            if (ch == 13)
                ch = (char)32768;
            else if (ch == 92)
                ch = (char)32769;
            s += ch;
        }
        
        return s;
    }
    
    /**
     * @param bitString String of 1s and 0s.
     * @return The input with added 0s at end, making the length divisible by number of bits that
     * represents a char.
     */
    private static String padBitString(String bitString)
    {
        for (int i = bitString.length() % BITS_PER_CHAR; i != 0 && i < BITS_PER_CHAR; i++)
            bitString += '0';
        return bitString;
    }
    
    /**
     * Abstracts bits from given text (from UserInfo's Strings).
     * @param data String of characters that represent bits/data.
     * @return String of 1s and 0s representing the input.
     * @see evaluateBitString(String)
     */
    public static String evaluateToBitString(String data)
    {
        String bitString = "";
        for (char ch : data.toCharArray())
            bitString += numberToBitString(ch == 32768 ? 13 : ch == 32769 ? 92 : ch, BITS_PER_CHAR);
        return bitString;
    }
}