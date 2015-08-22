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

public class Ldee implements Player, Piece {
	
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

        // Increment depth if needed
        if (movesPlayed == STEP_SIZE) {
            depth++;
            movesPlayed = 0;
        } else if (STEP_SIZE == NO_STEP)
            depth = STANDARD_DEPTH;

        if (board.getNumFreeCells() <= board.getSize())
            depth = LOW_DEPTH;

        // Make move using minimax
        move = minimaxDecision(copyBoard, this.player, depth);

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
     * Finds all possible moves for a given board
     * @param board Game board with possible moves on it
     * @param player Player whos turn it is to move
     * @return List of all the possible moves
     */
    public List<Move> allPossibleMoves(Board board, int player){
    	
        List<Move> possibleMoves = new ArrayList<>();

        // Get free cells in this state
        List<Cell> freeCells = board.getFreeCells();

        // Add each free cell as a possible move
        for (Cell freeCell : freeCells) {
            Move move = new Move();
            move.Row = freeCell.getRow();
            move.Col = freeCell.getCol();
            move.P = player;

            possibleMoves.add(move);
        }

        return possibleMoves;
    }

    /**
     * Minmax Algorithm: Assesses possible moves on current board 
     * and looks ahead to see what is the best possible move. 
     * Uses alpha-beta pruning to deepen search.
     * @param board Current board state
     * @param player Player who is making move
     * @param depth Max depth of search
     * @return Best possible move
     */
    public Move minimaxDecision(Board board, int player, int depth){

        Move bestMove = new Move();

        // Initialize alpha beta bounds to +/- infinity
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        double currentValue;
        
        // Need to pass opponent player value into minmax for next move
        int other = (player == BLACK) ? WHITE : BLACK;

    	// Get available moves
        List<Move> possibleMoves = allPossibleMoves(board, this.player);

    	// Check if game has finished
        if (possibleMoves.size() == 0 || depth == 0) {

        }
        else{
        	
            // For each move, update board and call minimax
            for(Move m : possibleMoves){
            	
            	// Make a copy of the board & update with move
                Board newState = board.copyBoard();
                newState.setCell(m.Row, m.Col, m.P);
                newState.update(m);

                // Get the value of this move
                currentValue = minimaxValue(newState, other, depth,
                		alpha, beta);

    			// Check if this move is better than current best
                if(currentValue > alpha){
                    alpha = currentValue;
                    bestMove = m;
                }
            }
        }
        return bestMove;
    }

    /**
     * Called by minmax decision. Will return a value indicating the
     * desirability of the state. High value = good state.
     * @param currentState Current board state with move added.
     * @param player Player whos move is next.
     * @param depth Max depth of search.
     * @param alpha High bound for pruning.
     * @param beta Low bound for pruning.
     * @return Value corresponding to utility of state
     */
    public double minimaxValue(Board currentState, int player,
    			int depth, double alpha, double beta){

        double currentValue;
        
        // Need to pass opponent player value into minmax for next move
        int other = (player == BLACK) ? WHITE : BLACK;

    	// Get available moves
        List<Move> possibleMoves = allPossibleMoves(currentState, player);

    	// Terminate if no moves available or at depth
        if (possibleMoves.size() == 0 || depth == 0) {
        	
        	// Return utility value corresponding to state
            return utilityFunction(currentState, this.player);
        }
    	// Still have moves to check
        else {
            // For each move, update the board and get minimaxValue
            for (Move m : possibleMoves) {
            	
            	// Make a copy of the board and add move
                Board newState = currentState.copyBoard();
                newState.setCell(m.Row, m.Col, m.P);
                newState.update(m);

                // Get value for this move
                currentValue = minimaxValue(newState, other, depth-1, 
                		alpha, beta);
                
                // Alpha-beta pruning. 
                // Update alpha if this is our player
                if (player == this.player){
                   if(currentValue > alpha){
                	   alpha = currentValue;
                   }
                   // Check if alpha is in bounds
                   if(alpha >= beta){
                	   return beta;
                   }
                }
                // Update beta if it is the opponent
                else{
                	if(currentValue < beta){
                		beta = currentValue;
                	}
                	// Check to see if beta is in bounds
                	if(beta <= alpha){
                		return alpha;
                	}
                }
            }
            // Return alpha for our player or beta for opponent
            if (player == this.player)
                return alpha;
            else
                return beta;
        }
    }
    
    /**
     * Returns a value indicating the desirability of the state.
     * @param state Current board state
     * @param player Player who wants best state
     * @return Value indicating desirability of this position
     */
    public double utilityFunction(Board state, int player){
    	
        double utilityValue = 0.0;
        int adj;
        int diag;
        int size = state.getSize();
        
        // Find out what piece our player is on the board
        String playerValue = Board.PLAY_WHITE;
        if (player == Piece.BLACK)
            playerValue = Board.PLAY_BLACK;

        // Check each cell
        for(int row=0; row<size; row++){
            for(int col=0; col<size; col++){

                // Check corner cells - minus points
                if ((row == 0 || row == size - 1) &&
                		(col == 0 || col == size - 1)) {
                    // Check if corner has your value
                    if(state.cellEqualsValue(row, col, playerValue))
                        utilityValue -= CORNER_WEIGHT;
                }

                // Add points for middle placed pieces
                if (state.cellEqualsValue(row, col, playerValue)
                		&& checkMiddle(row, col, state)){
                    utilityValue += MIDDLE_WEIGHT;
                }
                
                // For each cell that is ours
                if(state.cellEqualsValue(row, col, playerValue)){
                	
                	// Check for adj cells above and side
                	adj = countAdjacent(row, col, state, playerValue);
                    if(adj >= 1){
                    	utilityValue -= (adj*ADJ_WEIGHT);
                    }
                    // Add points for adjacent diagonal
                    diag = countDiagonal(row, col, state, playerValue);
                    utilityValue += (diag*DIAG_WEIGHT);
                }
   
            }
        }

        // Add points for captured pieces, minus for opponents captured
        if(playerValue.equals(Board.PLAY_BLACK)){
            utilityValue += (state.getBlackScore() * OFFENSIVE_WEIGHT);
            utilityValue -= (state.getWhiteScore() * DEFENSIVE_WEIGHT);
        }
        else{
            utilityValue += (state.getWhiteScore() * OFFENSIVE_WEIGHT);
            utilityValue -= (state.getBlackScore() * DEFENSIVE_WEIGHT);
        }

        return utilityValue;
    }
    
    /**
     * Indicates if a cell is not on the edge of a board.
     * @param row Row of the cell
     * @param col Column of the cell
     * @param state Board we are checking
     * @return True if it isn't on the edge. False if it is
     */
    public boolean checkMiddle(int row, int col, Board state){

        boolean flag;

        flag = (row > 0) && (row < state.getSize()) 
        		&& (col > 0) && (col < state.getSize());

        return flag;
    }

    /** 
     * Book of initial moves to perform for the start of the game while
     * minmax has no real value to us. Looks to cut off the corner pieces
     * as they are good starting moves.
     * @param board Game board
     * @return Move to play
     *
     * This code is currently not in use
     */
    public Move startMovesBook(Board board){
    	
    	String playedPiece;
    	Move move = new Move();
        movesPlayed++;
        int size = board.getSize();
    	
    	// Find what piece we are playing
        playedPiece = Board.PLAY_BLACK;
    	if (this.player == Piece.WHITE)
    		playedPiece = Board.PLAY_WHITE;

    	// Check if top left is free
    	if(board.cellIsFree(0, 1) && board.cellIsFree(1, 0)){
    		move.Row = 0;
    		move.Col = 1;
    		move.P = this.player;
    		return move;
    	}
    	else if(board.cellEqualsValue(0, 1, playedPiece)
                && board.cellIsFree(1, 0)){
    		move.Row = 1;
    		move.Col = 0;
    		move.P = this.player;
    		return move;
    	}

    	// Check if bottom left is free
    	if(board.cellIsFree(size-1, 1) && board.cellIsFree(size-2, 0)){
    		move.Row = size-1;
    		move.Col = 1;
    		move.P = this.player;
    		return move;
    	}
    	else if(board.cellEqualsValue(size-1, 1, playedPiece)
                && board.cellIsFree(size-2, 0)){
    		move.Row = size-2;
    		move.Col = 0;
    		move.P = this.player;
    		return move;
    	}
    	
    	// Check if bottom right is free
    	if(board.cellIsFree(size-1, size-2) &&
                board.cellIsFree(size-2,size-1)){
    		
    		// Claim this cell
    		move.Row = size-1;
    		move.Col = size-2;
    		move.P = this.player;
    		return move;
    	}
    	else if(board.cellEqualsValue(size-1,size-2, playedPiece) &&
    			board.cellIsFree(size-2, size-1)){
    		
    		move.Row = size-2;
    		move.Col = size-1;
    		move.P = this.player;
    		return move;
    	}
    	
    	// Check if top right is free
    	if(board.cellIsFree(0, size-2)
    			&& board.cellIsFree(1, size-1)){

    		// Claim this cell
    		move.Row = 0;
    		move.Col = size-2;
    		move.P = this.player;
    		return move;
    	}
    	else if(board.cellEqualsValue(0, size-2, playedPiece)
    			&& board.cellIsFree(1, size-1)){

    		move.Row = 1;
    		move.Col = size-1;
    		move.P = this.player;
    		return move;
    	}
    	
    	move = getRandomMove();
    	return move;
    }
    
    /**
     * We don't want to be in a position where we have the same value
     * piece adjacent vertically * horizontally. This becomes a useless 
     * piece. Method returns count of adjacent vertical & horizontal pieces.
     * @param row Row we are checking on board.
     * @param col Column we are checking on board.
     * @param state Game board.
     * @param player Player who's value we are checking.
     * @return Count of adjacent vertical & horizontal pieces.
     */
    public int countAdjacent(int row, int col, Board state, String player){
    	
    	int count = 0;
    	
    	// Check cell above 
    	if(((row-1) >= 0)
                && (state.getCell(row-1, col).getValue().equals(player))){
    		
    		// Check to the left
    		if((col-1 >= 0)
                    &&(state.getCell(row, col-1).getValue().equals(player))){
    			count++;
    		}
    		// Check to the right
    		if((col+1 < state.getSize())
                    && (state.getCell(row, col+1).getValue().equals(player))){
    			count++;
    		}
    	}
    	// Check cell below
    	if(((row+1) < state.getSize())
                && (state.getCell(row+1, col).getValue().equals(player))){
    		
    		// Check to the left
    		if((col-1 >= 0)
                    &&(state.getCell(row, col-1).getValue().equals(player))){
    			count++;
    		}
    		// Check to the right
    		if((col+1 < state.getSize())
                    && (state.getCell(row, col+1).getValue().equals(player))){
    			count++;
    		}
    	}
    	
    	// If count is >= 2 the cell is in a bad position
    	return count;
    }
    
    /**
     * Counts the number of same value pieces which are diagonal to the
     * current piece.
     * @param row Row we are checking on board.
     * @param col Column we are checking on board.
     * @param state Current Board.
     * @param player Player who has value in checking position.
     * @return Number of diagonal pieces we have to the checked position.
     */
    public int countDiagonal(int row, int col, Board state, String player){
    	
    	int count = 0;
    	
    	// Top left diagonal
    	if((col-1 >= 0) && (row-1 >= 0)){
    		if(state.getCell(row-1, col-1).getValue().equals(player)){
    			count++;
    		}
    	}
    	// Top Right diagonal
    	if((col+1 < state.getSize()) && (row-1 >= 0)){
    		if(state.getCell(row-1, col+1).getValue().equals(player)){
    			count++;
    		}
    	}
    	// Bottom left diagonal
    	if((col-1 >= 0) && (row+1 < state.getSize())){
    		if(state.getCell(row+1, col-1).getValue().equals(player)){
    			count++;
    		}
    	}
    	// Bottom right diagonal
    	if((col+1 < state.getSize()) && (row+1 < state.getSize())){
    		if(state.getCell(row+1, col+1).getValue().equals(player)){
    			count++;
    		}
    	}

    	return count;
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

    /**
     * Testing function to verse the player as a human user
     * @return Move
     */
    public Move getUserMove(int p) {
        int row, col;
        Move m = new Move();
        Scanner kb = new Scanner(System.in);
        while (true) {
            row = kb.nextInt();
            col = kb.nextInt();
            if (board.cellIsFree(row, col))
                break;
        }
        m.Row = row;
        m.Col = col;
        m.P = p;
        return m;
    }
}
