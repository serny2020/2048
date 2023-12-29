package game2048;

import java.util.Formatter;


/**
 * The state of a game of 2048.
 *
 * @author Xiaocheng Sun
 * Credit to P. N. Hilfinger and Josh Hug
 */
public class Model {
    /**
     * Current contents of the board.
     */
    private final Board board;
    /**
     * Current score.
     */
    private int score;
    /**
     * Maximum score so far.  Updated when game ends.
     */
    private int maxScore;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /**
     * Largest piece value.
     */
    public static final int MAX_PIECE = 2048;

    /**
     * A new 2048 game on a board of size SIZE with no pieces
     * and score 0.
     */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
    }

    /**
     * A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes.
     */
    public Model(int[][] rawValues, int score, int maxScore) {
        board = new Board(rawValues);
        this.score = score;
        this.maxScore = maxScore;
    }

    /**
     * Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     * 0 <= COL < size(). Returns null if there is no tile there.
     * Used for testing.
     */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /**
     * Return the number of squares on one side of the board.
     */
    public int size() {
        return board.size();
    }

    /**
     * Return the current score.
     */
    public int score() {
        return score;
    }

    /**
     * Return the current maximum game score (updated at end of game).
     */
    public int maxScore() {
        return maxScore;
    }

    /**
     * Clear the board to empty and reset the score.
     */
    public void clear() {
        score = 0;
        board.clear();
    }

    /**
     * Add TILE to the board. There must be no Tile currently at the
     * same position.
     */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
    }

    /**
     * Return true iff the game is over (there are no moves, or
     * there is a tile with value 2048 on the board).
     */
    public boolean gameOver() {
        return maxTileExists(board) || !atLeastOneMoveExists(board);
    }

    /**
     * Checks if the game is over and sets the maxScore variable
     * appropriately.
     */
    private void checkGameOver() {
        if (gameOver()) {
            maxScore = Math.max(score, maxScore);
        }
    }

    /**
     * Returns true if at least one space on the Board is empty.
     * Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        for (int c = 0; c < b.size(); c++) {
            for (int r = 0; r < b.size(); r++) {
                if (b.tile(c, r) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by this.MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        for (int c = 0; c < b.size(); c++) {
            for (int r = 0; r < b.size(); r++) {
                Tile t = b.tile(c, r);
                if (t != null) {
                    if (t.value() == MAX_PIECE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        for (int c = 0; c < b.size(); c++) {
            for (int r = 0; r < b.size(); r++) {
                Tile t = b.tile(c, r);
                if (t == null) {
                    return true;
                } else if (sameNeighboursValue(b, c, r)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * helper function for atLeastOneMoveExits to check if
     * any of the adjacent neighbours have the same value
     * as the current one. Note that the condition check
     * make sure the neighbour have a valid before retrieving
     * them.
     */
    public static boolean sameNeighboursValue(Board b, int c, int r) {

        if (validIndex(b, c - 1) && validIndex(b, c + 1) &&
                validIndex(b, r - 1) && validIndex(b, r + 1)) {
            // middle case: all the neighbours are within the bound
            return middleCase(b, c, r);
        } else if (!validIndex(b, c + 1) && validIndex(b, c - 1)
                && !validIndex(b, r - 1) && validIndex(b, r + 1)) {
            // bottom right corner: north and east neighbour are with
            // the bound
            return bottomRight(b, c, r);
        } else if (validIndex(b, c + 1) && !validIndex(b, c - 1)
                && !validIndex(b, r - 1) && validIndex(b, r + 1)) {
            // bottom left corner: north and west neighbour are with
            // the bound
            return bottomLeft(b, c, r);
        } else if (!validIndex(b, c + 1) && validIndex(b, c - 1)
                && validIndex(b, r - 1) && !validIndex(b, r + 1)) {
            // top right corner: south and west neighbour are with
            // the bound
            return topRight(b, c, r);
        } else if (validIndex(b, c + 1) && !validIndex(b, c - 1)
                && validIndex(b, r - 1) && !validIndex(b, r + 1)) {
            // top left corner: south and east neighbour are with
            // the bound
            return topLeft(b, c, r);
        } else if (!validIndex(b, r - 1) && validIndex(b, c - 1)
                && validIndex(b, c + 1) && validIndex(b, r + 1)) {
            // bottom case: north, west, east are within bound
            return bottomCase(b, c, r);
        } else if (!validIndex(b, r + 1) && validIndex(b, c - 1)
                && validIndex(b, c + 1) && validIndex(b, r - 1)) {
            // top case: south, west, east are within bound
            return topCase(b, c, r);
        } else if (validIndex(b, r + 1) && validIndex(b, c - 1)
                && validIndex(b, c + 1) && validIndex(b, r - 1)) {
            // left case: south, north, east are within bound
            return leftCase(b, c, r);
        } else if (!validIndex(b, c + 1) && validIndex(b, c - 1)
                && validIndex(b, r - 1) && validIndex(b, r + 1)) {
            // right case: north, south, west are within bound
            return rightCase(b, c, r);
        }
        return false;
    }

    /*
     * helper function for sameNeighboursValue
     * return true if any valid neighbours have the same value
     * false otherwise
     */
    public static boolean bottomLeft(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int north = 0;
        int east = 0;
        Tile north_t = b.tile(c, r + 1);
        Tile east_t = b.tile(c + 1, r);
        // neighbors should have a valid number
        if (hasValue(north_t) && hasValue(east_t)) {
            north = north_t.value();
            east = east_t.value();
        }
        return (current == north || current == east);
    }

    public static boolean bottomRight(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int north = 0;
        int west = 0;
        Tile north_t = b.tile(c, r + 1);
        Tile west_t = b.tile(c - 1, r);
        // neighbors should have a valid number
        if (hasValue(north_t) && hasValue(west_t)) {
            north = north_t.value();
            west = west_t.value();
        }
        return (current == north || current == west);
    }

    public static boolean topRight(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int south = 0;
        int west = 0;
        Tile south_t = b.tile(c, r - 1);
        Tile west_t = b.tile(c - 1, r);
        // neighbors should have a valid number
        if (hasValue(south_t) && hasValue(west_t)) {
            south = south_t.value();
            west = west_t.value();
        }
        return (current == south || current == west);
    }

    public static boolean topLeft(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int south = 0;
        int east = 0;
        Tile south_t = b.tile(c, r - 1);
        Tile east_t = b.tile(c + 1, r);
        // neighbors should have a valid number
        if (hasValue(south_t) && hasValue(east_t)) {
            south = south_t.value();
            east = east_t.value();
        }
        return (current == south || current == east);
    }

    public static boolean leftCase(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int south = 0;
        int east = 0;
        int north = 0;
        Tile north_t = b.tile(c, r + 1);
        Tile south_t = b.tile(c, r - 1);
        Tile east_t = b.tile(c + 1, r);
        // neighbors should have a valid number
        if (hasValue(south_t) && hasValue(east_t) && hasValue(north_t)) {
            south = south_t.value();
            east = east_t.value();
            north = north_t.value();

        }
        return (current == north || current == east || current == south);
    }

    public static boolean rightCase(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int west = 0;
        int south = 0;
        int north = 0;
        Tile north_t = b.tile(c, r + 1);
        Tile west_t = b.tile(c - 1, r);
        Tile south_t = b.tile(c, r - 1);
        // neighbors should have a valid number
        if (hasValue(west_t) && hasValue(south_t) && hasValue(north_t)) {
            west = west_t.value();
            south = south_t.value();
            north = north_t.value();
        }
        return (current == north || current == west || current == south);
    }


    public static boolean middleCase(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int west = 0;
        int east = 0;
        int north = 0;
        int south = 0;
        Tile north_t = b.tile(c, r + 1);
        Tile west_t = b.tile(c - 1, r);
        Tile east_t = b.tile(c + 1, r);
        Tile south_t = b.tile(c, r - 1);
        // neighbors should have a valid number
        if (hasValue(west_t) && hasValue(east_t) && hasValue(north_t) && hasValue(south_t)) {
            west = west_t.value();
            east = east_t.value();
            north = north_t.value();
            south = south_t.value();
        }
        return (current == north || current == west ||
                current == east || current == south);
    }

    public static boolean bottomCase(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int west = 0;
        int east = 0;
        int north = 0;
        Tile north_t = b.tile(c, r + 1);
        Tile west_t = b.tile(c - 1, r);
        Tile east_t = b.tile(c + 1, r);
        // neighbors should have a valid number
        if (hasValue(west_t) && hasValue(east_t) && hasValue(north_t)) {
            west = west_t.value();
            east = east_t.value();
            north = north_t.value();
        }

        return (current == north || current == west ||
                current == east);
    }

    public static boolean topCase(Board b, int c, int r) {
        int current = b.tile(c, r).value();
        int west = 0;
        int east = 0;
        int south = 0;
        Tile south_t = b.tile(c, r - 1);
        Tile west_t = b.tile(c - 1, r);
        Tile east_t = b.tile(c + 1, r);
        // neighbors should have a valid number
        if (hasValue(west_t) && hasValue(east_t) && hasValue(south_t)) {
            west = west_t.value();
            east = east_t.value();
            south = south_t.value();

        }
        return (current == south || current == west ||
                current == east);
    }

    /*
     * helper function return true if the neighbours are valid
     * within the bound; false otherwise
     */
    public static boolean validIndex(Board b, int index) {
        return (index >= 0 && index < b.size());
    }

    /*
     * helper function return true if the neighbours have an
     * actual value
     */
    public static boolean hasValue(Tile t) {
        return (t != null);
    }

    /**
     * Tilt the board toward SIDE.
     * <p>
     * 1. If two Tile objects are adjacent in the direction of motion and have
     * the same value, they are merged into one Tile of twice the original
     * value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     * tilt. So each move, every tile will only ever be part of at most one
     * merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     * value, then the leading two tiles in the direction of motion merge,
     * and the trailing tile does not.
     */
    public void tilt(Side side) {
        // TODO: Modify this.board (and if applicable, this.score) to account
        // for the tilt to the Side SIDE.
        if (side == Side.NORTH) {
            processNorth(board);
        } else if (side == Side.WEST) {
            board.setViewingPerspective(Side.WEST);
            processNorth(board);
            board.setViewingPerspective(Side.NORTH);
        } else if (side == Side.SOUTH) {
            board.setViewingPerspective(Side.SOUTH);
            processNorth(board);
            board.setViewingPerspective(Side.NORTH);
        } else if (side == Side.EAST) {
            board.setViewingPerspective(Side.EAST);
            processNorth(board);
            board.setViewingPerspective(Side.NORTH);
        }
        checkGameOver();
    }

    /*
     * helper function for tilt, follow the rules but process only
     * on the north direction, other directions are transformed to
     * the north accordingly.
     */
    public void processNorth(Board b) {
        for (int c = 0; c < b.size(); c++) {
            makeAdjacent(b, c);
            processColumn(b, c);
            makeAdjacent(b, c);
        }
    }

    /*
     * helper function for processNorth. Merge two Tile objects value
     * if they are adjacent to each other
     */
    public void processColumn(Board b, int c) {
        for (int r = b.size() - 1; r >= 1; r -= 1) {
            Tile current = b.tile(c, r);
            Tile next = b.tile(c, r - 1);
            if (current != null && next != null) {
                if (current.value() == next.value()) {
                    b.move(c, r, next);
                    this.score += next.value() * 2; //update score
                }

            }
        }
    }

    /*
     * helper function for processNorth.
     * move tiles such that there is no null in between
     * since all the tile move in sequence, use a single counter to keep track of
     * the number of nulls tiles.
     */
    public static void makeAdjacent(Board b, int c) {
        int nullCount = 0; //keep track of the number of moves before a number tile
        for (int r = b.size() - 1; r >= 0; r--) {
            Tile t = b.tile(c, r);
            if (t == null) { //a null tile, increase the counter
                nullCount += 1;
            } else { //if not a null, move it
                b.move(c, r + nullCount, t);
            }
        }
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}

