/**
 * Chess AI
 * <br><br>
 * Board's top right corner is white square<br>
 * White player at bottom<br>
 * 
 * @author Taylor Born
 * @version 2/4/14 - 2/10/14
 */

public class AI {
    
    // Piece types
    private static final int PIECE_TYPE_DEAD   = -1;
    public static final int PIECE_TYPE_PAWN   =  0;
    public static final int PIECE_TYPE_ROOK   =  1;
    public static final int PIECE_TYPE_KNIGHT =  2;
    public static final int PIECE_TYPE_BISHOP =  3;
    public static final int PIECE_TYPE_QUEEN  =  4;
    public static final int PIECE_TYPE_KING   =  5;
    
    
    private GameState mainGameState = new GameState();
    
    /** How many moves ahead to look */
    private int MAX_DEPTH;
    
    
    public AI(int maxDepth)
    {
        MAX_DEPTH = maxDepth;
    }
    public AI()
    {
        this(5);
    }
    
    public int getMaxDepth()
    {
        return MAX_DEPTH;
    }
    
    /**
     * Updates the game status according to the given human player's move, unless the result puts that player in check.
     * Meant to update the board of the human's move.
     * Not meant to be used by the AI computation.
     * Assume that there is a piece where the given move originates.
     * Does not ensure the given move is valid according to the rules of moving according to piece type.
     * @param from The square the move originated from.
     * @param to The Square the move ended on.
     * @param pawnPromotion The piece type for pawn promotion if it occurs.
     * @param commit Whether the move should be applied (if valid). Use false to test if a move will be valid.
     * @return Whether the given move is valid (It won't place this Player in check).
     */
    public boolean tryMove(int color, int from, int to, int pawnPromotion, boolean commit)
    {
        return mainGameState.players[color].tryMove(from, to, pawnPromotion, commit);
    }
    
    public void go(int color)
    {
        if (!done)
            throw new IllegalStateException("Not done running AI from the last call!");
        done = false;
        Thread thread = new Thread(new AI_Thread(color));
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
    private volatile boolean done = true;
    
    private class AI_Thread implements Runnable
    {
        private GameState.Player player;
        
        public AI_Thread(int color)
        {
            player = mainGameState.players[color];
        }
        
        @Override
        public void run()
        {
            player.searchAsRoot();
            done = true;
        }
    }
    
    public boolean isDone()
    {
        return done;
    }
    
    private static int resultFrom, resultTo, pawnPromotion = PIECE_TYPE_QUEEN;
    
    /**
     * Should only be called when isDone() return true;
     * @return Array of size 3. First entry is the square the move originated from. Second entry is the square the move ended on.
     * Square values range from 0 to 63, reading left to right, starting at the top left square of the game board.
     * Third entry is piece type, in case of pawn promotion.
     * @see isDone()
     */
    public int[] getMove()
    {
        return new int[]{ resultFrom, resultTo, pawnPromotion };
    }
    
    /**
     * 
     * @return If the given color is in check.
     */
    public boolean isInCheck(int color)
    {
        return mainGameState.players[color].isInCheck();
    }
    
    /**
     * @return If the the given color is in checkmate/stalemate.
     * @see isInCheck(int)
     */
    public boolean canMove(int color)
    {
        try {
            mainGameState.players[color].search(MAX_DEPTH - 1, color == WHITE ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        } catch (WorsePathException e) {
            System.err.println("Error: Found worse path at root!");
        }
        return mainGameState.players[color].best != null;
    }
    

    // Individual spaces
    // <Square>
    private static final long[] SPACE_VALUES = new long[64];
    
    // Color of player
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    
    // __________________________________________________________________________________________
    // 
    // NORMAL PATHS (PIECE MOVING TYPES THAT MOVE AND ATTACK THE SAME)
    
    // Path styles
    private static final int PATH_UP          = 0;
    private static final int PATH_DOWN        = 1;
    private static final int PATH_LEFT        = 2;
    private static final int PATH_RIGHT       = 3;
    private static final int PATH_UP_LEFT     = 4;
    private static final int PATH_UP_RIGHT    = 5;
    private static final int PATH_DOWN_LEFT   = 6;
    private static final int PATH_DOWN_RIGHT  = 7;
    private static final int PATH_KNIGHTS     = 8;
    private static final int PATH_KING        = 9;
    
    // Squares that can be moved/attacked to, given a style and piece's square
    // <Square> <Path> <i>
    private static final int[][][] PATHS = new int[64][10][];
    
    // __________________________________________________________________________________________
    //
    // PAWNS:
    
    // Squares which Pawns can attack to, given their square
    // <Color> <Square> <i>
    private static final int[][][] PAWN_ATTACKS = new int[2][64][];
    
    // Squares which Pawns can move to, given their square
    // <Color> <Square> <i>
    private static final int[][][] PAWN_MOVES = new int[2][64][];
    
    // Mapping of squares of rows where Pawns will be promoted
    private static final long PAWN_PROMOTION_ROWS = (255l << 56) | 255l;
    
    // __________________________________________________________________________________________
    // 
    // USED TO DETERMINE IF KING IS IN CHECK:
    
    // Mapping of possible squares where Knights may be who can attack the given square
    // <King's Square>
    private static final long[] KING_CHECK_KNIGHTS = new long[64];
    
    // Mapping of possible squares where Pawns may be who can attack the given square
    // <King's Color> <King's Square>
    private static final long[][] KING_CHECK_PAWNS = new long[2][64];
    
    // Mapping of surrounding squares of a given square
    // <King's Square>
    private static final long[] KING_CHECK_KING = new long[64];
    
    // __________________________________________________________________________________________
    // 
    // USED IN CASTLING:
    
    // Which side of the board (King will castle on)
    private static final int CASTLE_LEFT  = 0;
    private static final int CASTLE_RIGHT = 1;
    
    // Squares of the corners of the board
    // <Color> <Side>
    private static final int[][] CASTLE_ROOK_CORNERS = new int[][]{ { 56, 63 }, { 0, 7 } };
    
    // Mapping of squares that must be empty
    // <Color> <Side>
    private static final long[][] CASTLE_CLEARANCE = new long[2][2];
    
    // Role in the castling move
    private static final int CASTLE_KING = 0;
    private static final int CASTLE_ROOK = 1;
    
    // Squares of where the King and Rook will end up at after castling
    // <Color> <Side> <Role>
    private static final int[][][] CASTLE_DESTINATIONS = new int[][][]{ { { 58, 59 }, { 62, 61 } }, { { 2, 3 }, { 6, 5 } } };
    
    // The occupancy state before/after castling
    private static final int CASTLE_OCCUPANCY_BEFORE = 0;
    private static final int CASTLE_OCCUPANCY_AFTER  = 1;
    
    // Mapping of squares that change occupancy
    // <Color> <Side> <Before/After>
    private static final long[][][] CASTLE_OCCUPANCY = new long[2][2][2];
    
    // Piece index of Rook
    // <Side>
    private static final int[] PIECE_INDEX_ROOKS = new int[]{ 0, 7 };
    
    // __________________________________________________________________________________________
    
    // 
    private static final int[] PIECE_BASE_SCORES = new int[]{ 100, 300, 275, 300, 500, 9999 };
    
    // Point value given for having particular pieces
    // <Piece Type> <Square>
    private static final int[][] PIECE_SCORES = new int[6][64];
    
    static {
        int i;
        for (i = 0; i < 64; i++)
            SPACE_VALUES[i] = 1l << i;
        
        CASTLE_CLEARANCE[WHITE][CASTLE_LEFT] = SPACE_VALUES[57] | SPACE_VALUES[58] | SPACE_VALUES[59];
        CASTLE_CLEARANCE[WHITE][CASTLE_RIGHT] = SPACE_VALUES[61] | SPACE_VALUES[62];
        CASTLE_CLEARANCE[BLACK][CASTLE_LEFT] = SPACE_VALUES[1] | SPACE_VALUES[2] | SPACE_VALUES[3];
        CASTLE_CLEARANCE[BLACK][CASTLE_RIGHT] = SPACE_VALUES[5] | SPACE_VALUES[6];
        
        CASTLE_OCCUPANCY[WHITE][CASTLE_LEFT][CASTLE_OCCUPANCY_BEFORE] = SPACE_VALUES[56] | SPACE_VALUES[60];
        CASTLE_OCCUPANCY[WHITE][CASTLE_LEFT][CASTLE_OCCUPANCY_AFTER] = SPACE_VALUES[59] | SPACE_VALUES[58];
        CASTLE_OCCUPANCY[WHITE][CASTLE_RIGHT][CASTLE_OCCUPANCY_BEFORE] = SPACE_VALUES[63] | SPACE_VALUES[60];
        CASTLE_OCCUPANCY[WHITE][CASTLE_RIGHT][CASTLE_OCCUPANCY_AFTER] = SPACE_VALUES[61] | SPACE_VALUES[62];
        
        CASTLE_OCCUPANCY[BLACK][CASTLE_LEFT][CASTLE_OCCUPANCY_BEFORE] = SPACE_VALUES[0] | SPACE_VALUES[4];
        CASTLE_OCCUPANCY[BLACK][CASTLE_LEFT][CASTLE_OCCUPANCY_AFTER] = SPACE_VALUES[3] | SPACE_VALUES[2];
        CASTLE_OCCUPANCY[BLACK][CASTLE_RIGHT][CASTLE_OCCUPANCY_BEFORE] = SPACE_VALUES[7] | SPACE_VALUES[4];
        CASTLE_OCCUPANCY[BLACK][CASTLE_RIGHT][CASTLE_OCCUPANCY_AFTER] = SPACE_VALUES[5] | SPACE_VALUES[6];
        
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                
                int space = row * 8 + col;
                
                int length;
                
                // King
                if (row != 0 && row != 7 && col != 0 && col != 7)
                    length = 8;
                else if ((row == 7 || row == 0) && (col == 7 || col == 0))
                    length = 3;
                else
                    length = 5;
                PATHS[space][PATH_KING] = new int[length];
                i = 0;
                for (int r = -1; r <= 1; r++)
                    if (row + r >= 0 && row + r <= 7)
                        for (int c = -1; c <=1; c++)
                            if (col + c >= 0 && col + c <= 7)
                                if (r != 0 || c != 0)
                                    PATHS[space][PATH_KING][i++] = (row + r) * 8 + (col + c);
                
                
                PATHS[space][PATH_UP] = new int[row];
                for (i = 0; i < row; i++)
                    PATHS[space][PATH_UP][i] = space - 8 * (i + 1);
                
                PATHS[space][PATH_DOWN] = new int[7 - row];
                for (i = 0; i < 7 - row; i++)
                    PATHS[space][PATH_DOWN][i] = space + 8 * (i + 1);
                
                PATHS[space][PATH_LEFT] = new int[col];
                for (i = 0; i < col; i++)
                    PATHS[space][PATH_LEFT][i] = space - 1 - i;
                
                PATHS[space][PATH_RIGHT] = new int[7 - col];
                for (i = 0; i < 7 - col; i++)
                    PATHS[space][PATH_RIGHT][i] = space + 1 + i;
                
                length = Math.min(row, col);
                PATHS[space][PATH_UP_LEFT] = new int[length];
                for (i = 0; i < length; i++)
                    PATHS[space][PATH_UP_LEFT][i] = space - 9 * (i + 1);
                
                length = Math.min(row, 7 - col);
                PATHS[space][PATH_UP_RIGHT] = new int[length];
                for (i = 0; i < length; i++)
                    PATHS[space][PATH_UP_RIGHT][i] = space - 7 * (i + 1);
                
                length = Math.min(7 - row, col);
                PATHS[space][PATH_DOWN_LEFT] = new int[length];
                for (i = 0; i < length; i++)
                    PATHS[space][PATH_DOWN_LEFT][i] = space + 7 * (i + 1);
                
                length = Math.min(7 - row, 7 - col);
                PATHS[space][PATH_DOWN_RIGHT] = new int[length];
                for (i = 0; i < length; i++)
                    PATHS[space][PATH_DOWN_RIGHT][i] = space + 9 * (i + 1);

                length = 0;
                if (col > 0) {
                    if (row > 1)
                        length++;
                    if (row < 6)
                        length++;
                    if (col > 1) {
                        if (row > 0)
                            length++;
                        if (row < 7)
                            length++;
                    }
                }
                if (col < 7) {
                    if (row > 1)
                        length++;
                    if (row < 6)
                        length++;
                    if (col < 6) {
                        if (row > 0)
                            length++;
                        if (row < 7)
                            length++;
                    }
                }
                PATHS[space][PATH_KNIGHTS] = new int[length];
                i = 0;
                if (col > 0) {
                    if (row > 1)
                        PATHS[space][PATH_KNIGHTS][i++] = (row - 2) * 8 + (col - 1);
                    if (row < 6)
                        PATHS[space][PATH_KNIGHTS][i++] = (row + 2) * 8 + (col - 1);
                    if (col > 1) {
                        if (row > 0)
                            PATHS[space][PATH_KNIGHTS][i++] = (row - 1) * 8 + (col - 2);
                        if (row < 7)
                            PATHS[space][PATH_KNIGHTS][i++] = (row + 1) * 8 + (col - 2);
                    }
                }
                if (col < 7) {
                    if (row > 1)
                        PATHS[space][PATH_KNIGHTS][i++] = (row - 2) * 8 + (col + 1);
                    if (row < 6)
                        PATHS[space][PATH_KNIGHTS][i++] = (row + 2) * 8 + (col + 1);
                    if (col < 6) {
                        if (row > 0)
                            PATHS[space][PATH_KNIGHTS][i++] = (row - 1) * 8 + (col + 2);
                        if (row < 7)
                            PATHS[space][PATH_KNIGHTS][i++] = (row + 1) * 8 + (col + 2);
                    }
                }
                for (i = 0; i < length; i++)
                    KING_CHECK_KNIGHTS[space] = KING_CHECK_KNIGHTS[space] | SPACE_VALUES[PATHS[space][PATH_KNIGHTS][i]];
                
                
                if (row != 0) {
                    
                    int first = 0;
                    if (col != 0)
                        first = space - 9;
                    if (col != 7) {
                        if (first == 0) {
                            PAWN_ATTACKS[WHITE][space] = new int[1];
                            PAWN_ATTACKS[WHITE][space][0] = space - 7;
                            KING_CHECK_PAWNS[WHITE][space] = SPACE_VALUES[space - 7];
                        }
                        else {
                            PAWN_ATTACKS[WHITE][space] = new int[2];
                            PAWN_ATTACKS[WHITE][space][0] = first;
                            PAWN_ATTACKS[WHITE][space][1] = space - 7;
                            KING_CHECK_PAWNS[WHITE][space] = SPACE_VALUES[first] | SPACE_VALUES[space - 7];
                        }
                    }
                    else if (first != 0) {
                        PAWN_ATTACKS[WHITE][space] = new int[1];
                        PAWN_ATTACKS[WHITE][space][0] = first;
                        KING_CHECK_PAWNS[WHITE][space] = SPACE_VALUES[first];
                    }
                    else
                        PAWN_ATTACKS[WHITE][space] = new int[0];
                }
                
                if (row != 7) {
                    
                    int first = 0;
                    if (col != 0)
                        first = space + 7;
                    if (col != 7) {
                        if (first == 0) {
                            PAWN_ATTACKS[BLACK][space] = new int[1];
                            PAWN_ATTACKS[BLACK][space][0] = space + 9;
                            KING_CHECK_PAWNS[BLACK][space] = SPACE_VALUES[space + 9];
                        }
                        else {
                            PAWN_ATTACKS[BLACK][space] = new int[2];
                            PAWN_ATTACKS[BLACK][space][0] = first;
                            PAWN_ATTACKS[BLACK][space][1] = space + 9;
                            KING_CHECK_PAWNS[BLACK][space] = SPACE_VALUES[first] | SPACE_VALUES[space + 9];
                        }
                    }
                    else if (first != 0) {
                        PAWN_ATTACKS[BLACK][space] = new int[1];
                        PAWN_ATTACKS[BLACK][space][0] = first;
                        KING_CHECK_PAWNS[BLACK][space] = SPACE_VALUES[first];
                    }
                    else
                        PAWN_ATTACKS[BLACK][space] = new int[0];
                }
                
                PAWN_MOVES[WHITE][space] = new int[row == 6 ? 2 : 1];
                PAWN_MOVES[WHITE][space][0] = space - 8;
                if (row == 6)
                    PAWN_MOVES[WHITE][space][1] = space - 16;
                
                PAWN_MOVES[BLACK][space] = new int[row == 1 ? 2 : 1];
                PAWN_MOVES[BLACK][space][0] = space + 8;
                if (row == 1)
                    PAWN_MOVES[BLACK][space][1] = space + 16;
                
                for (int r = -1; r <= 1; r++)
                    if (row + r >= 0 && row + r <= 7)
                        for (int c = -1; c <= 1; c++)
                            if (col + c >= 0 && col + c <= 7 && (r != 0 || c != 0))
                                KING_CHECK_KING[space] = KING_CHECK_KING[space] | SPACE_VALUES[(row + r) * 8 + (col + c)];
                
                for (i = 0; i < 6; i++)
                    PIECE_SCORES[i][space] = PIECE_BASE_SCORES[i] + (i != 5 && col != 0 && col != 7 && row != 0 && col != 7 ? 2 : 0);
            }
    }

    // Piece index of King
    private static final int PIECE_INDEX_KING = 4;
    
    // Occupancy record type index
    private static final int OCCUPANCY_ALL               = 0;
    private static final int OCCUPANCY_KNIGHTS           = 1;
    private static final int OCCUPANCY_PAWNS             = 2;
    private static final int OCCUPANCY_ROW_COLUMN_MOVERS = 3;
    private static final int OCCUPANCY_DIAGONAL_MOVERS   = 4;
    
    // Collection of occupancy record types that should be adjusted when a particular piece moves
    // <Piece Type> <i>
    private static final int[][] OCCUPANCY_BUNDLES =
            new int[][] { { OCCUPANCY_ALL, OCCUPANCY_PAWNS },
                          { OCCUPANCY_ALL, OCCUPANCY_ROW_COLUMN_MOVERS },
                          { OCCUPANCY_ALL, OCCUPANCY_KNIGHTS },
                          { OCCUPANCY_ALL, OCCUPANCY_DIAGONAL_MOVERS },
                          { OCCUPANCY_ALL, OCCUPANCY_DIAGONAL_MOVERS, OCCUPANCY_ROW_COLUMN_MOVERS },
                          { OCCUPANCY_ALL } };
    
    // __________________________________________________________________________________________
    
    
//     private final int[][][] CACHE_SCORES = new int[MAX_DEPTH - 1][1500000][2]; // ~ 45.77 Mb
    
    private class GameState
    {
        Player[] players = new Player[2];
        
        /** Mapping of occupancy of both players together */
        long universalOccupancy;
        
        /**
         * Create new GameState, initiated as new game.
         */
        public GameState()
        {
            players[WHITE] = new Player(WHITE);
            players[BLACK] = new Player(BLACK);
            universalOccupancy = players[WHITE].occupancy[OCCUPANCY_ALL] | players[BLACK].occupancy[OCCUPANCY_ALL];
        }
        
        
        /**
         * Create new GameState as a clone of another.
         * @param oldState GameState to be cloned.
         */
        private GameState(GameState oldState)
        {
            players[WHITE] = new Player(oldState.players[WHITE]);
            players[BLACK] = new Player(oldState.players[BLACK]);
            universalOccupancy = oldState.universalOccupancy;
        }
        
        /**
         * Clone this GameState.
         * @return Clone of this GameState.
         */
        private GameState cloneState()
        {
            return new GameState(this);
        }
        
        private class Player
        {
            long[] occupancy = new long[5];
            int[] piece = new int[16];
            int[] pieceType = new int[16];
            int color;
            int opponentColor;
            boolean kingMoved;
            boolean[] rookMoved;
            boolean castled;
            
            int depth;
            GameState best;
            int bestScore;
            int parentBestScore;
            
            /**
             * Create a new Player of specified color, to be initiated as a new game.
             * @param color Color of the Player's pieces.
             */
            public Player(int color)
            {
                this.color = color;
                opponentColor = 1 - color;
                
                for (int i = 0; i < 16; i++)
                    occupancy[OCCUPANCY_ALL] |= SPACE_VALUES[color == WHITE ? 48 + i : i];
                
                for (int i = 0; i < 8; i++) {
                    // Pawns
                    piece[8 + i] = (color == WHITE ? 48 : 8) + i;
                    
                    // Other pieces
                    piece[i] = (color == WHITE ? 56 + i : i);
                }
                
                for (int i = 8; i < 16; i++)
                    occupancy[OCCUPANCY_PAWNS] = occupancy[OCCUPANCY_PAWNS] | SPACE_VALUES[piece[i]];
                
                pieceType[0] = PIECE_TYPE_ROOK;
                pieceType[1] = PIECE_TYPE_KNIGHT;
                pieceType[2] = PIECE_TYPE_BISHOP;
                pieceType[3] = PIECE_TYPE_QUEEN;
                pieceType[PIECE_INDEX_KING] = PIECE_TYPE_KING;
                pieceType[5] = PIECE_TYPE_BISHOP;
                pieceType[6] = PIECE_TYPE_KNIGHT;
                pieceType[7] = PIECE_TYPE_ROOK;
                
                occupancy[OCCUPANCY_KNIGHTS] = SPACE_VALUES[piece[1]] | SPACE_VALUES[piece[6]];
                occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] = SPACE_VALUES[piece[0]] | SPACE_VALUES[piece[7]] | SPACE_VALUES[piece[3]];
                occupancy[OCCUPANCY_DIAGONAL_MOVERS] = SPACE_VALUES[piece[2]] | SPACE_VALUES[piece[5]] | SPACE_VALUES[piece[3]];
                
                rookMoved = new boolean[2];
            }
            
            /**
             * Create new Player as a clone of another.
             * @param player Player to be cloned.
             */
            public Player(Player player)
            {
                for (int o = 0; o < occupancy.length; o++)
                    occupancy[o] = player.occupancy[o];
                for (int p = 0; p < piece.length; p++) {
                    piece[p] = player.piece[p];
                    pieceType[p] = player.pieceType[p];
                }
                color = player.color;
                opponentColor = player.opponentColor;
                
                kingMoved = player.kingMoved;
                rookMoved = new boolean[]{ player.rookMoved[0], player.rookMoved[1] };
                
                castled = player.castled;
            }
            
            /**
             * Updates the game status according to the given human player's move, unless the result puts that player in check.
             * Meant to update the board of the human's move.
             * Not meant to be used by the AI computation.
             * @param from The square the move originated from.
             * @param to The Square the move ended on.
             * @param pawnPromotion The piece type for pawn promotion if it occurs.
             * @param commit Whether the move should be applied (if valid). Use false to test if a move will be valid.
             * @return Whether the given move is valid (It won't place this Player in check).
             */
            public boolean tryMove(int from, int to, int pawnPromotion, boolean commit)
            {
                GameState gameState = cloneState();
                
                for (int pieceIndex = 0; pieceIndex < piece.length; pieceIndex++)
                    // If found piece that moved
                    if (pieceType[pieceIndex] != PIECE_TYPE_DEAD && piece[pieceIndex] == from) {
                        // Update that piece's location
                        gameState.players[color].piece[pieceIndex] = to;
                        // If a Rook, make note that they have moved, for castling purposes
                        if (pieceType[pieceIndex] == PIECE_TYPE_ROOK) {
                            if (from == CASTLE_ROOK_CORNERS[color][CASTLE_LEFT])
                                gameState.players[color].rookMoved[CASTLE_LEFT] = true;
                            else if (from == CASTLE_ROOK_CORNERS[color][CASTLE_RIGHT])
                                gameState.players[color].rookMoved[CASTLE_RIGHT] = true;
                        }
                        // If the King, possible castling
                        else if (!kingMoved && pieceType[pieceIndex] == PIECE_TYPE_KING) {
                            // Check if King is trying to castle
                            int castleSide = -1;
                            if (to == from - 2)
                                castleSide = CASTLE_LEFT;
                            else if (to == from + 2)
                                castleSide = CASTLE_RIGHT;
                            // Castling
                            if (castleSide != -1) {
                                // Can't castle if King was in check
                                if (isInCheck())
                                    return false;
                                // Update location of Rook that is involved
                                gameState.players[color].piece[PIECE_INDEX_ROOKS[castleSide]] = CASTLE_DESTINATIONS[color][castleSide][CASTLE_ROOK];
                                // Update occupancies
                                gameState.players[color].occupancy[OCCUPANCY_ALL] = (occupancy[OCCUPANCY_ALL] ^ CASTLE_OCCUPANCY[color][castleSide][CASTLE_OCCUPANCY_BEFORE]) | CASTLE_OCCUPANCY[color][castleSide][CASTLE_OCCUPANCY_AFTER];
                                gameState.universalOccupancy = (universalOccupancy ^ CASTLE_OCCUPANCY[color][castleSide][CASTLE_OCCUPANCY_BEFORE]) | CASTLE_OCCUPANCY[color][castleSide][CASTLE_OCCUPANCY_AFTER];
                                gameState.players[color].occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] = (occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] ^ SPACE_VALUES[CASTLE_ROOK_CORNERS[color][castleSide]]) | SPACE_VALUES[CASTLE_DESTINATIONS[color][castleSide][CASTLE_ROOK]];
                                // Note that we castled
                                gameState.players[color].kingMoved = true;
                                gameState.players[color].castled = true;
                                // Make sure we are not in check after castling
                                if (gameState.players[color].isInCheck())
                                    return false;
                                if (commit)
                                    mainGameState = gameState;
                                return true;
                            }
                        }
                        
                        // Update occupancies
                        for (int o : OCCUPANCY_BUNDLES[pieceType[pieceIndex]])
                            gameState.players[color].occupancy[o] = (occupancy[o] ^ SPACE_VALUES[from]) | SPACE_VALUES[to];
                        
                        if (pieceType[pieceIndex] == PIECE_TYPE_PAWN && (PAWN_PROMOTION_ROWS & SPACE_VALUES[to]) != 0) {
                            // Change Pawn to promotion piece type
                            gameState.players[color].pieceType[pieceIndex] = pawnPromotion;
                            // Remove Pawn occupancy
                            gameState.players[color].occupancy[OCCUPANCY_PAWNS] ^= SPACE_VALUES[to];
                            // Update occupancy according to promotion
                            if (pawnPromotion == PIECE_TYPE_QUEEN || pawnPromotion == PIECE_TYPE_ROOK)
                                gameState.players[color].occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] |= SPACE_VALUES[to];
                            if (pawnPromotion == PIECE_TYPE_QUEEN || pawnPromotion == PIECE_TYPE_BISHOP)
                                gameState.players[color].occupancy[OCCUPANCY_DIAGONAL_MOVERS] |= SPACE_VALUES[to];
                        }
                        break;
                    }
                gameState.universalOccupancy = (universalOccupancy ^ SPACE_VALUES[from]) | SPACE_VALUES[to];
                if ((players[opponentColor].occupancy[OCCUPANCY_ALL] & SPACE_VALUES[to]) != 0)
                    gameState.players[opponentColor].attacked(SPACE_VALUES[to]);
                
                if (gameState.players[color].isInCheck())
                    return false;
                if (commit) {
                    mainGameState = gameState;
//                     print();
                }
                return true;
            }
            
            /**
             * Invoked only when a piece belonging to this Player is attacked/removed.
             * @param square Mapping of location of piece being removed.
             */
            public void attacked(long square)
            {
                for (int pieceIndex = 0; pieceIndex < piece.length; pieceIndex++)
                    if (pieceType[pieceIndex] != PIECE_TYPE_DEAD && SPACE_VALUES[piece[pieceIndex]] == square) {
                        for (int o : OCCUPANCY_BUNDLES[pieceType[pieceIndex]])
                            occupancy[o] ^= square;
                        pieceType[pieceIndex] = PIECE_TYPE_DEAD;
                        break;
                    }
                if (square == SPACE_VALUES[CASTLE_ROOK_CORNERS[color][CASTLE_LEFT]])
                    rookMoved[CASTLE_LEFT] = true;
                else if (square == SPACE_VALUES[CASTLE_ROOK_CORNERS[color][CASTLE_RIGHT]])
                    rookMoved[CASTLE_RIGHT] = true;
            }
            
            public void searchAsRoot()
            {
                try {
                    search(0, color == WHITE ? Integer.MAX_VALUE : Integer.MIN_VALUE);
                } catch (WorsePathException e) {
                    System.err.println("Error: Found worse path at root!");
                }
                mainGameState = best;
//                 print();
            }
            
            public void search(int depth, int parentBestScore) throws WorsePathException
            {
                // If max depth has been reached
                if (depth == MAX_DEPTH) {
                    
                    bestScore = 0;
                    for (int pieceIndex = 0; pieceIndex < piece.length; pieceIndex++) {
                        if (players[BLACK].pieceType[pieceIndex] != PIECE_TYPE_DEAD)
                            bestScore -= PIECE_SCORES[players[BLACK].pieceType[pieceIndex]][players[BLACK].piece[pieceIndex]];
                        if (players[BLACK].castled)
                            bestScore -= 5;
                        
                        if (players[WHITE].pieceType[pieceIndex] != PIECE_TYPE_DEAD)
                            bestScore += PIECE_SCORES[players[WHITE].pieceType[pieceIndex]][players[WHITE].piece[pieceIndex]];
                        if (players[WHITE].castled)
                            bestScore += 5;
                    }
                    return;
                }
                this.depth = ++depth;
                this.parentBestScore = parentBestScore;
                
//                 int hash, hash2;
//                 if (depth != 1) {
//                     char[] chars = new char[64 + 6];
//                     char[] chars2 = new char[64 + 6];
//                     chars2[0] = (char)(kingMoved ? 1 : 2);
//                     chars2[1] = (char)(rookMoved[0] ? 1 : 2);
//                     chars2[2] = (char)(rookMoved[1] ? 1 : 2);
//                     chars2[3] = (char)(players[opponentColor].kingMoved ? 1 : 2);
//                     chars2[4] = (char)(players[opponentColor].rookMoved[0] ? 1 : 2);
//                     chars2[5] = (char)(players[opponentColor].rookMoved[1] ? 1 : 2);
//                     for (int pieceIndex = 0; pieceIndex < piece.length; pieceIndex++) {
//                         if (pieceType[pieceIndex] != PIECE_TYPE_DEAD) {
//                             chars[piece[pieceIndex]] = (char)(1 + pieceType[pieceIndex]);
//                             chars2[6 + piece[pieceIndex]] = (char)(1 + pieceType[pieceIndex]);
//                         }
//                         if (players[opponentColor].pieceType[pieceIndex] != PIECE_TYPE_DEAD) {
//                             chars[players[opponentColor].piece[pieceIndex]] = (char)(10 + players[opponentColor].pieceType[pieceIndex]);
//                             chars2[6 + players[opponentColor].piece[pieceIndex]] = (char)(10 + players[opponentColor].pieceType[pieceIndex]);
//                         }
//                     }
//                     chars[64] = (char)(kingMoved ? 1 : 2);
//                     chars[65] = (char)(rookMoved[0] ? 1 : 2);
//                     chars[66] = (char)(rookMoved[1] ? 1 : 2);
//                     chars[67] = (char)(players[opponentColor].kingMoved ? 1 : 2);
//                     chars[68] = (char)(players[opponentColor].rookMoved[0] ? 1 : 2);
//                     chars[69] = (char)(players[opponentColor].rookMoved[1] ? 1 : 2);
//                     
//                     hash = (int)Math.abs((new String(chars)).hashCode()) % CACHE_SCORES[0].length;
//                     hash2 = (new String(chars2)).hashCode();
//                     
//                     
//                     for (int d = (color == BLACK ? 0 : 1); d + 1 < depth; d += 2)
//                         if (CACHE_SCORES[d][hash][0] == hash2) {
//                             bestScore = CACHE_SCORES[d][hash][1];
//                             
//                             return;
//                         }
//                 }
//                 else {
//                     hash = 0;
//                     hash2 = 0;
//                 }

                best = null;
                bestScore = color == WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                
                for (int pieceIndex = 0; pieceIndex < piece.length; pieceIndex++)
                    switch (pieceType[pieceIndex]) {
                        case PIECE_TYPE_KNIGHT:
                            followPaths(pieceIndex, PATH_KNIGHTS, PATH_KNIGHTS, false);
                            break;
                        case PIECE_TYPE_ROOK:
                            followPaths(pieceIndex, PATH_UP, PATH_RIGHT, true);
                            break;
                        case PIECE_TYPE_BISHOP:
                            followPaths(pieceIndex, PATH_UP_LEFT, PATH_DOWN_RIGHT, true);
                            break;
                        case PIECE_TYPE_QUEEN:
                            followPaths(pieceIndex, PATH_UP, PATH_DOWN_RIGHT, true);
                            break;
                        case PIECE_TYPE_KING:
                            followPaths(pieceIndex, PATH_KING, PATH_KING, false);
                            if (!kingMoved && !isInCheck()) {
                                for (int castleSide = CASTLE_LEFT; castleSide <= CASTLE_RIGHT; castleSide++)
                                    if (!rookMoved[castleSide] && (CASTLE_CLEARANCE[color][castleSide] & universalOccupancy) == 0) {
                                        GameState gameState = cloneState();
                                        
                                        gameState.players[color].piece[pieceIndex] = CASTLE_DESTINATIONS[color][castleSide][CASTLE_KING];
                                        gameState.players[color].piece[PIECE_INDEX_ROOKS[castleSide]] = CASTLE_DESTINATIONS[color][castleSide][CASTLE_ROOK];
                                        
                                        gameState.players[color].occupancy[OCCUPANCY_ALL] = (occupancy[OCCUPANCY_ALL] ^ CASTLE_OCCUPANCY[color][castleSide][CASTLE_OCCUPANCY_BEFORE]) | CASTLE_OCCUPANCY[color][castleSide][CASTLE_OCCUPANCY_AFTER];
                                        gameState.universalOccupancy = (universalOccupancy ^ CASTLE_OCCUPANCY[color][castleSide][CASTLE_OCCUPANCY_BEFORE]) | CASTLE_OCCUPANCY[color][castleSide][CASTLE_OCCUPANCY_AFTER];
                                        
                                        gameState.players[color].kingMoved = true;
                                        gameState.players[color].castled = true;
                                        
                                        gameState.players[color].occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] = (occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] ^ SPACE_VALUES[CASTLE_ROOK_CORNERS[color][castleSide]]) | SPACE_VALUES[CASTLE_DESTINATIONS[color][castleSide][CASTLE_ROOK]];
                                        
                                        if (!gameState.players[color].isInCheck())
                                            if (testGameState(gameState)) {
                                                resultFrom = piece[pieceIndex];
                                                resultTo = CASTLE_DESTINATIONS[color][castleSide][CASTLE_KING];
                                            }
                                    }
                            }
                            break;
                        case PIECE_TYPE_PAWN:
                            for (int i = 0; i < PAWN_MOVES[color][piece[pieceIndex]].length; i++) {
                                long destination = SPACE_VALUES[PAWN_MOVES[color][piece[pieceIndex]][i]];
                                if ((destination & universalOccupancy) == 0) {
                                    GameState gameState = cloneState();
                                    
                                    // Set the Pawn's square to where it moves to
                                    gameState.players[color].piece[pieceIndex] = PAWN_MOVES[color][piece[pieceIndex]][i];
                                    
                                    // Update Player's overall occupancy
                                    gameState.players[color].occupancy[OCCUPANCY_ALL] = occupancy[OCCUPANCY_ALL] ^ SPACE_VALUES[piece[pieceIndex]] | destination;
                                    
                                    // Remove occupancy from where Pawn moves from, and add occupancy to where it moves to
                                    gameState.universalOccupancy = universalOccupancy ^ SPACE_VALUES[piece[pieceIndex]] | destination;
                                    
                                    gameState.players[color].occupancy[OCCUPANCY_PAWNS] = occupancy[OCCUPANCY_PAWNS] ^ SPACE_VALUES[piece[pieceIndex]];
                                    
                                    // Account for trading in pawn for new piece
                                    // Allow promotion choice of Queen or Knight.
                                    // Assume player will not make a bad choice of Rook or Bishop (Queen encapsulates both!)
                                    if ((destination & PAWN_PROMOTION_ROWS) != 0) {
                                        GameState gameState2 = gameState.cloneState();
                                        gameState2.players[color].pieceType[pieceIndex] = PIECE_TYPE_QUEEN;
                                        gameState2.players[color].occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] = occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] | destination;
                                        gameState2.players[color].occupancy[OCCUPANCY_DIAGONAL_MOVERS] = occupancy[OCCUPANCY_DIAGONAL_MOVERS] | destination;
                                        
                                        if (!gameState2.players[color].isInCheck())
                                            if (testGameState(gameState2)) {
                                                resultFrom = piece[pieceIndex];
                                                resultTo = PAWN_MOVES[color][piece[pieceIndex]][i];
                                                pawnPromotion = PIECE_TYPE_QUEEN;
                                            }
                                        
                                        gameState.players[color].pieceType[pieceIndex] = PIECE_TYPE_KNIGHT;
                                        gameState.players[color].occupancy[OCCUPANCY_KNIGHTS] = occupancy[OCCUPANCY_KNIGHTS] | destination;
                                    }
                                    else
                                        // Still a Pawn, include in occupancy record of Pawns
                                        gameState.players[color].occupancy[OCCUPANCY_PAWNS] = gameState.players[color].occupancy[OCCUPANCY_PAWNS] | destination;
                                    
                                    if (!gameState.players[color].isInCheck())
                                        if (testGameState(gameState)) {
                                            resultFrom = piece[pieceIndex];
                                            resultTo = PAWN_MOVES[color][piece[pieceIndex]][i];
                                            pawnPromotion = PIECE_TYPE_KNIGHT;
                                        }
                                }
                                else
                                    break;
                            }
                            for (int i = 0; i < PAWN_ATTACKS[color][piece[pieceIndex]].length; i++) {
                                long destination = SPACE_VALUES[PAWN_ATTACKS[color][piece[pieceIndex]][i]];
                                if ((destination & players[opponentColor].occupancy[OCCUPANCY_ALL]) != 0) {
                                    GameState gameState = cloneState();
                                    
                                    // Take out opponent's piece
                                    gameState.players[opponentColor].attacked(destination);
                                    
                                    // Set the Pawn's square to where it moves to
                                    gameState.players[color].piece[pieceIndex] = PAWN_ATTACKS[color][piece[pieceIndex]][i];
                                    
                                    // Update Player's overall occupancy
                                    gameState.players[color].occupancy[OCCUPANCY_ALL] = (occupancy[OCCUPANCY_ALL] ^ SPACE_VALUES[piece[pieceIndex]]) | destination;
                                    
                                    // Remove occupancy from where Pawn moves from
                                    // Don't need to add occupancy to where it moves to for universalOccupancy, since square moving to was already occupied
                                    gameState.universalOccupancy = universalOccupancy ^ SPACE_VALUES[piece[pieceIndex]];
                                    gameState.players[color].occupancy[OCCUPANCY_PAWNS] = occupancy[OCCUPANCY_PAWNS] ^ SPACE_VALUES[piece[pieceIndex]];
                                    
                                    // Account for trading in pawn for new piece
                                    // Allow promotion choice of Queen or Knight.
                                    // Assume player will not make a bad choice of Rook or Bishop (Queen encapsulates both!)
                                    if ((destination & PAWN_PROMOTION_ROWS) != 0) {
                                        GameState gameState2 = gameState.cloneState();
                                        gameState2.players[color].pieceType[pieceIndex] = PIECE_TYPE_QUEEN;
                                        gameState2.players[color].occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] = occupancy[OCCUPANCY_ROW_COLUMN_MOVERS] | destination;
                                        gameState2.players[color].occupancy[OCCUPANCY_DIAGONAL_MOVERS] = occupancy[OCCUPANCY_DIAGONAL_MOVERS] | destination;
                                        
                                        if (!gameState2.players[color].isInCheck())
                                            if (testGameState(gameState2)) {
                                                resultFrom = piece[pieceIndex];
                                                resultTo = PAWN_ATTACKS[color][piece[pieceIndex]][i];
                                                pawnPromotion = PIECE_TYPE_QUEEN;
                                            }
                                        
                                        gameState.players[color].pieceType[pieceIndex] = PIECE_TYPE_KNIGHT;
                                        gameState.players[color].occupancy[OCCUPANCY_KNIGHTS] = occupancy[OCCUPANCY_KNIGHTS] | destination;
                                    }
                                    else
                                        // Still a Pawn, include in occupancy record of Pawns
                                        gameState.players[color].occupancy[OCCUPANCY_PAWNS] = gameState.players[color].occupancy[OCCUPANCY_PAWNS] | destination;
                                    
                                    if (!gameState.players[color].isInCheck())
                                        if (testGameState(gameState)) {
                                            resultFrom = piece[pieceIndex];
                                            resultTo = PAWN_ATTACKS[color][piece[pieceIndex]][i];
                                            pawnPromotion = PIECE_TYPE_KNIGHT;
                                        }
                                }
                            }
                    }
                
//                 if (depth != 1) {
//                     CACHE_SCORES[depth - 2][hash][0] = hash2;
//                     CACHE_SCORES[depth - 2][hash][1] = bestScore;
//                 }
                
                if (best != null)
                    best.players[opponentColor].best = null;
                else
                    bestScore = PIECE_BASE_SCORES[PIECE_TYPE_KING] * (color == WHITE ? -1 : 1);
            }
            
            private void followPaths(int pieceIndex, int pathInitial, int pathEnd, boolean pathChain) throws WorsePathException
            {
                for (int path = pathInitial; path <= pathEnd; path++)
                    for (int k = 0; k < PATHS[piece[pieceIndex]][path].length; k++) {
                        long destination = SPACE_VALUES[PATHS[piece[pieceIndex]][path][k]];
                        // Check if own piece is in the way, which case done trying to move in the path
                        if ((destination & occupancy[OCCUPANCY_ALL]) != 0) {
                            if (pathChain)
                                break;
                        }
                        else {
                            GameState gameState = cloneState();
                            
                            if (!rookMoved[CASTLE_LEFT] && piece[pieceIndex] == CASTLE_ROOK_CORNERS[color][CASTLE_LEFT])
                                gameState.players[color].rookMoved[CASTLE_LEFT] = true;
                            if (!rookMoved[CASTLE_RIGHT] && piece[pieceIndex] == CASTLE_ROOK_CORNERS[color][CASTLE_RIGHT])
                                gameState.players[color].rookMoved[CASTLE_RIGHT] = true;
                            if (!kingMoved && pieceIndex == PIECE_INDEX_KING)
                                gameState.players[color].kingMoved = true;
                            
                            // Set the Piece's square to where it moves to
                            gameState.players[color].piece[pieceIndex] = PATHS[piece[pieceIndex]][path][k];
                            
                            // Remove occupancy from where the Piece moves from
                            // If attacking, Don't need to add occupancy to where it moves to for universalOccupancy, otherwise do
                            gameState.universalOccupancy = universalOccupancy ^ SPACE_VALUES[piece[pieceIndex]];
                            boolean capture = (destination & universalOccupancy) != 0;
                            if (capture)
                                // Take out opponent's Piece
                                gameState.players[opponentColor].attacked(destination);
                            else
                                gameState.universalOccupancy |= destination;
                            
                            // Update Player's occupancy records
                            for (int o : OCCUPANCY_BUNDLES[pieceType[pieceIndex]])
                                gameState.players[color].occupancy[o] = (occupancy[o] ^ SPACE_VALUES[piece[pieceIndex]]) | destination;
                            
                            // Check if King is in danger, which case: not a valid move
                            // Attacking another square may eliminate threat (if we can continue with the path)
                            if (gameState.players[color].isInCheck()) {
                                if (capture && pathChain)
                                    break;
                                continue;
                            }
                            
                            if (testGameState(gameState)) {
                                resultFrom = piece[pieceIndex];
                                resultTo = PATHS[piece[pieceIndex]][path][k];
                            }
                            if (capture && pathChain)
                                break;
                        }
                    }
            }
            
            private boolean testGameState(GameState gameState) throws WorsePathException
            {
                try {
                    gameState.players[opponentColor].search(depth, bestScore);
                } catch (WorsePathException e) {
                    return false;
                }
                if (color == WHITE ? gameState.players[opponentColor].bestScore > bestScore : gameState.players[opponentColor].bestScore < bestScore) {
                    bestScore = gameState.players[opponentColor].bestScore;
                    if (depth != 1 && (color == WHITE ? parentBestScore <= bestScore : parentBestScore >= bestScore))
                        throw worsePathException;
                    best = gameState;
                    if (depth == 1)
                        return true;
                }
                return false;
            }
            
            /**
             * Determine whether or not this Player's King is in check.
             * @return Whether or not this Player's King is in check.
             */
            public boolean isInCheck()
            {
                int king = piece[PIECE_INDEX_KING];
                
                // Check if an opponent's Knight is in place
                if ((KING_CHECK_KNIGHTS[king] & players[opponentColor].occupancy[OCCUPANCY_KNIGHTS]) != 0)
                    return true;
                
                // Check if an opponent's Pawn is in place
                if ((KING_CHECK_PAWNS[color][king] & players[opponentColor].occupancy[OCCUPANCY_PAWNS]) != 0)
                    return true;
                
                // Check if an opponent's Rook or Queen is in place
                for (int path = PATH_UP; path <= PATH_RIGHT; path++)
                    for (int k = 0; k < PATHS[king][path].length; k++)
                        if ((SPACE_VALUES[PATHS[king][path][k]] & universalOccupancy) != 0)
                            if ((SPACE_VALUES[PATHS[king][path][k]] & players[opponentColor].occupancy[OCCUPANCY_ROW_COLUMN_MOVERS]) != 0)
                                return true;
                            else
                                break;
                
                // Check if an opponent's Bishop or Queen is in place
                for (int path = PATH_UP_LEFT; path <= PATH_DOWN_RIGHT; path++)
                    for (int k = 0; k < PATHS[king][path].length; k++)
                        if ((SPACE_VALUES[PATHS[king][path][k]] & universalOccupancy) != 0)
                            if ((SPACE_VALUES[PATHS[king][path][k]] & players[opponentColor].occupancy[OCCUPANCY_DIAGONAL_MOVERS]) != 0)
                                return true;
                            else
                                break;
                
                // Finally check if opponent's King is in place
                return (KING_CHECK_KING[king] & SPACE_VALUES[players[opponentColor].piece[PIECE_INDEX_KING]]) != 0;
            }
        }
    }
    
    private static WorsePathException worsePathException = new WorsePathException();
    private static class WorsePathException extends Exception
    {
        public WorsePathException() {
            super("Worse path found.");
        }
    }
    
    
    public void print()
    {
        System.out.println("#############################################");
        
        print("Universal", mainGameState.universalOccupancy);
        
        for (int i = 0; i < 5; i++)
            print("White" + i, mainGameState.players[WHITE].occupancy[i]);
        for (int i = 0; i < 5; i++)
            print("Black" + i, mainGameState.players[BLACK].occupancy[i]);
    }
    private void print(String text, long board)
    {
        System.out.println(text);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++)
                System.out.print((board & SPACE_VALUES[row * 8 + col]) == 0 ? "0" : "1");
            System.out.println("");
        }
        System.out.println("");
    }
}