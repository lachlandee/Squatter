package aiproj.squatter.Ldee;

import aiproj.squatter.Piece;

/**
 * COMP30024 Project 2
 *
 * Lachlan Dee 638671
 * Ryan O'Kane 587723
 *
 * Cell.java
 * Represents one cell on the board
 */


public class Cell implements Piece {
    private String value;
    public static final String CAPTURED_FREE = "-";
    public static final String CAPTURED_BLACK = "b";
    public static final String CAPTURED_WHITE = "w";
    public static final String FREE = "+";
    public static final String PLAY_BLACK = "B";
    public static final String PLAY_WHITE = "W";
    private int row;
    private int col;

    /**
     * @return Current value of cell
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of cell
     * @param value Using Piece interface.
     */
    public void setValue(int value) {
        if (value == WHITE)
            this.value = PLAY_WHITE;
        else if (value == BLACK)
            this.value = PLAY_BLACK;
        else if (value == DEAD)
            if (this.getValue().equals(PLAY_BLACK))
                this.value = CAPTURED_BLACK;
            else if (this.getValue().equals(PLAY_WHITE))
                this.value = CAPTURED_WHITE;
            else
                this.value = CAPTURED_FREE;
    }

    /** Set string value of cell, bypassing Piece interface.
     */
    public void setValueString(String value) {
        this.value = value;
    }

    /**
     * @return Row value of cell.
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Sets row value of cell to parameter value.
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return Column value of cell
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Sets column value of cell to parameter value.
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Returns true if value of cell is equal to parameter.
     */
    public Boolean equalsValue(String value) {
        return this.getValue().equals(value);
    }

    /**
     * Returns true if cell is free.
     */
    public Boolean isFree() {
        return this.getValue().equals(FREE);
    }

    /**
     * Changes value cell to signify that it is captured.
     */
    public void capture() {
        switch (this.getValue()) {
            case PLAY_WHITE:
                this.value = CAPTURED_WHITE;
                break;
            case PLAY_BLACK:
                this.value = CAPTURED_BLACK;
                break;
            case FREE:
                this.value = CAPTURED_FREE;
                break;
        }
    }

    /**
     * Constructor.
     * @param value State of cell
     * @param row Row location
     * @param col Column location
     */
    public Cell(String value, int row, int col) {
        this.value = value;
        this.row = row;
        this.col = col;
    }
}
