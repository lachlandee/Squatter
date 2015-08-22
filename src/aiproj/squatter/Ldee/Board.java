package aiproj.squatter.Ldee;
/**
 * COMP30024 Project 2
 *
 * Lachlan Dee 638671
 * Ryan O'Kane 587723
 *
 * Board.java
 */


import aiproj.squatter.Move;
import aiproj.squatter.Piece;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

public class Board implements Piece {
    public static final String CAPTURED_FREE    = "-";
    public static final String CAPTURED_BLACK   = "b";
    public static final String CAPTURED_WHITE   = "w";
    public static final String FREE             = "+";
    public static final String PLAY_BLACK       = "B";
    public static final String PLAY_WHITE       = "W";
    public static final int VALID               =   0;

    private Cell[][] layout;
    private int size;
    private int blackScore;
    private int whiteScore;
    private int numFreeCells;

    /**
     * @return Current score of black player.
     */
    public int getBlackScore() {
        return blackScore;
    }

    /**
     * @return Current score of white player.
     */
    public int getWhiteScore() {
        return whiteScore;
    }

    /**
     * Given row and column, returns cell at this location.
     * @param row Row value.
     * @param col Column value.
     */
    public Cell getCell(int row, int col) {
        return this.layout[row][col];
    }

    /**
     * Set specified cell to the specified value. Uses the provided Piece
     * interface.
     * @param row Row value of cell.
     * @param col Column value of cell.
     */
    public void setCell(int row, int col, int value) {
        numFreeCells--;
        layout[row][col].setValue(value);
        layout[row][col].setRow(row);
        layout[row][col].setCol(col);
    }

    /**
     * Set specified cell to the specified value.
     * @param row Row value of cell.
     * @param col Column value of cell.
     * @param value String value we want to set the cell to hold.
     */
    public void setCellString(int row, int col, String value) {
        layout[row][col].setValueString(value);
        layout[row][col].setRow(row);
        layout[row][col].setCol(col);
    }

    /**
     * Returns true if cell at provided row and col is free, otherwise false.
     */
    public boolean cellIsFree(int row, int col) {
        return getCell(row, col).getValue().equals(FREE);
    }

    public boolean cellEqualsValue(int row, int col, String value) {
        return getCell(row, col).getValue().equals(value);
    }

    /**
     * @return Size of the board
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @return PrintStream object containing the board
     */
    public PrintStream printLayout() {
        PrintStream output = new PrintStream(System.out);
        for (int row = 0; row < this.getSize(); row++) {
            for (int col = 0; col < this.getSize(); col++) {
                output.print(layout[row][col].getValue()+ " ");
            }
            output.println();
        }
        output.println();
        return output;
    }

    /**
     * Initialises each cell in the board to free.
     */
    public void init() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                layout[row][col] = new Cell(FREE, row, col);
            }
        }
    }

    /**
     * Checks if there is a winner of the game.
     * @return Winner of the game (if one exists)
     */
    public int getWinner() {
        // Check if free cells exist
        if (numFreeCells > 0)
            return EMPTY;
        // Check scores
        else {
            if (blackScore > whiteScore)
                return BLACK;
            else if (whiteScore > blackScore)
                return WHITE;
            else
                return DEAD;
        }
    }

    /**
     * Updates the score of each player by looking for captured pieces.
     */
    public void updateScore() {
        int black = 0, white = 0, thisRun = 0;
        Cell current;

        // Check all cells
        for (int row = 0; row < this.getSize(); row++) {
            for (int col = 0; col < this.getSize(); col++) {
                // Get current cell
                current = this.getCell(row, col);
                // Check if cell is captured
                if (current.equalsValue(CAPTURED_FREE) ||
                        current.equalsValue(CAPTURED_BLACK) ||
                        current.equalsValue(CAPTURED_WHITE)) {
                    thisRun += 1;
                }
                // Cell is not captured, if previous cells were then counter
                // will be greater than zero
                else if (thisRun > 0) {
                    // Check value of current cell, increment their score
                    if (current.equalsValue(PLAY_BLACK))
                        black += thisRun;
                    else
                        white += thisRun;
                    thisRun = 0;
                }
            }
        }
        // Update scores
        whiteScore = white; blackScore = black;
    }

    /**
     * Checks if a given move is valid
     * @param m Move to be played
     * @return Boolean
     */
    public int checkValid(Move m) {
        // Check if row value is off board
        if (m.Row >= getSize() || m.Row < 0)
            return INVALID;
        // Check if col value is off board
        if (m.Col >= getSize() || m.Col < 0)
            return INVALID;
        // Check if cell already captured
        if (!Objects.equals(getCell(m.Row, m.Col).getValue(), FREE))
            return INVALID;
        return VALID;
    }

    /**
     * Update the board to contain the given move
     * @param m Move
     */
    public void update(Move m) {

        // Cell below
        Move next = new Move();
        do {
            if (m.Row + 1 < getSize()) {
                next.Row = m.Row + 1;
                next.Col = m.Col;
                next.P = m.P;
                if (floodFill(next) > 0)
                    break;
            }
            // Cell above
            if (m.Row - 1 >= 0) {
                next.Row = m.Row - 1;
                next.Col = m.Col;
                next.P = m.P;
                if (floodFill(next) > 0)
                    break;
            }
            // Cell to right
            if (m.Col + 1 < getSize()) {
                next.Row = m.Row;
                next.Col = m.Col + 1;
                next.P = m.P;
                if (floodFill(next) > 0)
                    break;
            }
            // Cell to left
            if (m.Col - 1 >= 0) {
                next.Row = m.Row;
                next.Col = m.Col - 1;
                next.P = m.P;
                floodFill(next);
            }
        } while (false);

        // Update the score
        updateScore();
    }

    /**
     * Checks if a piece is surrounded, ie checks if the piece has been
     * captured
     * @param m Contains row and column to check, and which player to be
     *          surrounded by
     * @return Number of captured pieces.
     */
    public int floodFill(Move m) {
        List<Cell> checklist = new ArrayList<>();
        List<Cell> complete = new ArrayList<>();
        List<Cell> neighbours;
        Cell next;
        String current = PLAY_WHITE;

        if (m.P == BLACK) {
            current = PLAY_BLACK;
        }

        // Check if same
        if (cellEqualsValue(m.Row, m.Col, current))
            return 0;

        // While checklist isn't empty
        checklist.add(getCell(m.Row, m.Col));
        while (!checklist.isEmpty()) {

            // Get next item in the queue
            next = checklist.get(0);

            // If any neighbours are borders, cannot be a loop
            if (next.getRow() + 1 == getSize() || next.getRow() - 1 == -1 ||
                    next.getCol() + 1 == getSize() || next.getCol() - 1 == -1)
                return 0;

            // Get neighbours
            neighbours = new ArrayList<>();
            neighbours.add(getCell(next.getRow() + 1, next.getCol()));
            neighbours.add(getCell(next.getRow() - 1, next.getCol()));
            neighbours.add(getCell(next.getRow(), next.getCol() + 1));
            neighbours.add(getCell(next.getRow(), next.getCol() - 1));

            // Check each neighbour
            for (Cell neighbour : neighbours) {
                // If neighbour has not already been checked
                if (!complete.contains(neighbour)) {
                    // If current players cell, don't explore
                    if (!neighbour.equalsValue(current)) {
                        checklist.add(neighbour);
                    }
                }
            }

            // This cell is directly surrounded, add to complete list
            checklist.remove(next);
            complete.add(next);
        }

        // If algorithm makes it here, all cells in complete list are
        // surrounded
        for (Cell toCapture : complete) {
            // Decrease free cell counter if cell was free
            if (toCapture.isFree())
                numFreeCells--;

            // Capture cell
            toCapture.capture();
        }
        return complete.size();
    }

    /**
     * Deep copy of the board.
     * @return Copy.
     */
    public Board copyBoard() {
        Board newBoard = new Board(this.size);
        newBoard.init();
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                newBoard.setCellString(i, j, this.getCell(i, j).getValue());
            }
        }
        newBoard.blackScore = this.blackScore;
        newBoard.whiteScore = this.whiteScore;
        newBoard.numFreeCells = this.numFreeCells;
        return newBoard;
    }

    /**
     * Find all the free cells on the board.
     * @return List of free cells.
     */
    public List<Cell> getFreeCells() {
        List<Cell> freeCells = new ArrayList<>();
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.getCell(i, j).getValue().equals(FREE)) {
                    freeCells.add(this.getCell(i, j));
                }
            }
        }
        return freeCells;
    }

    public int getNumFreeCells() {
        return this.numFreeCells;
    }

    /**
     * Constructor.
     * @param N Size of the board.
     */
    public Board(int N) {
        this.size = N;
        layout = new Cell[N][N];
        whiteScore = 0;
        blackScore = 0;
        numFreeCells = size * size;
    }
}
