/**
 * MessageContents
 * <p>
 * 
 * Usually you will know what information a message will have and what fields were used to store them (based on state of a game), so hasInt(int) and hasString(int) might not be necessary.
 * 
 * @author Taylor Born
 * @version January 2014
 */
public class MessageContents
{
    private Integer[] ints;
    private String[] strings;
    
    public MessageContents(Integer[] ints, String[] strings)
    {
        this.ints = ints;
        this.strings = strings;
    }
    
    /**
     * Check if the given int field is included in the message.
     * @param i Index of int field.
     * @see getInt(int)
     */
    public boolean hasInt(int i)
    {
        return ints[i] != null;
    }
    
    /**
     * Retrieve the specified int from message that opponent sent us.
     * @param i Index of int field.
     * @see hasInt(int)
     */
    public int getInt(int i)
    {
        return ints[i];
    }
    
    /**
     * Check if the given String field is included in the message.
     * @param i Index of String field.
     * @see getString(int)
     */
    public boolean hasString(int i)
    {
        return strings[i] != null;
    }
    
    /**
     * Retrieve the specified String from message that opponent sent us.
     * @param i Index of String field.
     * @see hasString(int)
     */
    public String getString(int i)
    {
        return strings[i];
    }
}