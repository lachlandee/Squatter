/**
 * COMP30024 Project 2
 *
 * Lachlan Dee 638671
 * Ryan O'Kane 587723
 *
 * Ldee.java
 * Agent to player squatter using minimax with alpha-beta pruning.
 */

package aiproj.squatter.Ldee;

import aiproj.squatter.Move;
import aiproj.squatter.Piece;
import aiproj.squatter.Player;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RandomPlayer implements Player, Piece {
	
    private int  player;
    private Board board;
    public int movesPlayed = 0;
    public int depth = 2;

    public static double OFFENSIVE_WEIGHT = 100;
    public static double DEFENSIVE_WEIGHT = 101;
    public static double CORNER_WEIGHT = 10.0;
    public static double MIDDLE_WEIGHT = 0.1;
    public static double DIAG_WEIGHT = 0.2;
    public static double ADJ_WEIGHT = 2.0;
    public static double NO_STEP = 0;
    public static double STEP_SIZE = NO_STEP;
    public static int STANDARD_DEPTH = 3;
    public static int LOW_DEPTH = 2;

    @Override
    public int init(int n, int p) {
        player = p;
        board = new Board(n);
        board.init();

        if (board.getSize() == 6)
            STEP_SIZE = 3;
        else if (board.getSize() == 7)
            STEP_SIZE = 6;

        return 0;
    }
    
    @Override
    public Move makeMove() {
        Move move;
        Board copyBoard;
        copyBoard = board.copyBoard();

        // Get a random move
        move = getRandomMove();

        // Place piece on game board
        board.setCell(move.Row, move.Col, player);

        // Update the captured cells
        move.P = player;
        board.update(move);

        movesPlayed++;
        return move;
    }

    @Override
    public int opponentMove(Move m) {
        // Check if opponents move is valid
        if (board.checkValid(m) == INVALID)
            return INVALID;

        // Place piece, update captured cells
        board.setCell(m.Row, m.Col, m.P);
        board.update(m);

        return 0;
    }

    @Override
    public int getWinner() {
        return board.getWinner();
    }

    @Override
    public void printBoard(PrintStream output) {
        board.printLayout();
    }

    /**
     * Testing function that finds a random, valid move to play.
     * @return Move.
     */
    public Move getRandomMove() {
        Random randomGenerator = new Random();
        int row, col, size = board.getSize();
        Move m = new Move();
        while (true) {
            row = randomGenerator.nextInt(size);
            col = randomGenerator.nextInt(size);
            if (board.cellIsFree(row, col))
                break;
        }
        m.Row = row;
        m.Col = col;
        return m;
    }

}
