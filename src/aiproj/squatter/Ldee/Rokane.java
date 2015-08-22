package aiproj.squatter.Ldee;

import aiproj.squatter.Move;
import aiproj.squatter.Piece;
import aiproj.squatter.Player;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class Rokane implements Player, Piece {
    private int   player;
    private Board board;
    
    public int counter = 0;
    
    @Override
    public int init(int n, int p) {
    	try{
    		player = p;
    		board = new Board(n);
    		board.init();
    		return 0;
    	}
    	catch(Exception e){
    		return -1;
    	}
    }

    @Override
    public Move makeMove() {
        Move move = new Move();
        int row, col;
        
        Board copyBoard = new Board(board.getSize());
        
        copyBoard = board.copyBoard();

        move = minimaxDecision(copyBoard, this.player, 3);

        // Place piece
        board.setCell(move.Row, move.Col, player);

        // Update the captured cells
        board.update(move);

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
//        return board.checkWin();
    }

    @Override
    public void printBoard(PrintStream output) {
        board.printLayout();
    }
    
    /* Returns a list of all possible moves given a current state */
    public List<Move> allPossibleMoves(Board board){
    	
    	
    	List<Move> possibleMoves = new ArrayList<>();
    	
    	for(int row = 0; row < board.getSize(); row++){
    		for(int col = 0; col < board.getSize(); col++){
    			
    			/* Check if cell is free */
    			if(board.getCell(row, col).getValue() == board.FREE){
    				
    				Move move = new Move();
    				
    				move.Row = row;
    				move.Col = col;
    				move.P = this.player;
    				possibleMoves.add(move);
    			}
    		}
    	}
    	
    	return possibleMoves;
    }
    
    public Move minimaxDecision(Board board, int player, int depth){
    	
    	Move bestMove = new Move();
    	
    	
    	double currentValue, highValue = Double.NEGATIVE_INFINITY;
    	int other = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
    	
    	/* Need to keep all minimax values of current players
    	 * moves */
    	List<Double> values = new ArrayList<>();
    	
    	/* Store all available moves */
    	List<Move> possibleMoves = new ArrayList<>();
    	possibleMoves = allPossibleMoves(board);
    	
    	/* Game has finished */
    	if(possibleMoves.size() == 0 || depth == 0){
    		
    		// Do Something
    	}
    	else{
    		
    
    		/* Go through each move and update state of board 
    		 * Apply minimax equation */
    		for(Move m : possibleMoves){
    			
    			Board newState = new Board(board.getSize());
    			
    			newState = board.copyBoard();
    			newState.setCell(m.Row, m.Col, player);
    			newState.update(m);
    		
    			currentValue = minimaxValue(newState, other, depth);
    		
    			/* If this is best value, keep track of move */
    			if(currentValue > highValue){
    				highValue = currentValue;
    				bestMove = m;
    			}
    		}
    	}

    	return bestMove;
    	
    }
    
    public double minimaxValue(Board currentState, int player, int depth){
    	
    	double currentValue;
    	double highValue = Double.NEGATIVE_INFINITY;
    	double lowValue = Double.POSITIVE_INFINITY;
    	
    	
    	int other = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
    	
    	List<Double> values = new ArrayList<>();
    	
    	/* Store all available moves */
    	List<Move> possibleMoves = new ArrayList<>();
    	possibleMoves = allPossibleMoves(currentState);
    	
    	/* Terminate if no more nodes */
    	if(possibleMoves.size() == 0 || depth == 0){
    		
    		return utilityFunction(currentState, this.player);
    	}
    	/* Still nodes to check */
    	else{
    		
    		for(Move m : possibleMoves){
    			
    			Board newState = new Board(currentState.getSize());
    			
    			newState = currentState.copyBoard();
    			newState.setCell(m.Row, m.Col, player);
    			newState.update(m);
    			
    			currentValue = minimaxValue(newState, other, depth-1);
    			
    			values.add(currentValue);
    			
    			/* Want to maximise the score */
    			if(player == this.player){
    				
    				if(currentValue > highValue){	
    					highValue = currentValue;
    				}
    			}
    			/* Opposition want to minimize the score */
    			else{
    				if(currentValue < lowValue){
    					lowValue = currentValue;
    				}
    			}
    			
    		}
    		
    		if(player == this.player){
    			return highValue;
    		}
    		else{
    			return lowValue;
    		}
    	}

    }
    
    public double utilityFunction(Board state, int player){
    	
    	double utilityValue = 0.0;
    	
    	String playerValue;
    	
    	if(player == Piece.BLACK){
    		playerValue = Board.PLAY_BLACK;
    	}
    	else{
    		playerValue = Board.PLAY_WHITE;
    	}
    	
    	for(int row=0; row<state.getSize(); row++){
    		for(int col=0; col<state.getSize(); col++){
    			
    			// Check corner cells - minus points
    			if((row == 0 || row == state.getSize()-1) && 
    					(col == 0 || col == state.getSize()-1)){
    				// Check if corner has your value
    				if(state.getCell(row, col).getValue()
    						== playerValue){
    					utilityValue -= 10;	
    				}
    			}
    			// Add points for middle placed pieces
    			if(state.getCell(row, col).getValue() == playerValue
    					&& checkMiddle(row, col, state)){
    				
    				utilityValue += 0.5;
    			}
	
    		}
    	}
    	
    	// Add any captured cells multiplied by 2
    	if(player == Piece.BLACK){
    		utilityValue += (state.getBlackScore() * 2);
    		utilityValue -= (state.getWhiteScore() * 2);
    	}
    	else{
    		utilityValue += (state.getWhiteScore() * 2);
    		utilityValue -= (state.getBlackScore() * 2);
    	}

    	return utilityValue;
    }
    
    public boolean checkMiddle(int row, int col, Board state){
    	
    	boolean flag;
    	
    	if(row > 0 && row < state.getSize() 
    			&& col > 0 && col < state.getSize()){
    		flag = true;
    	}
    	else{
    		flag = false;
    	}
    	
    	return flag;
    }
    
    public int countAdjacentVer(Board state, int row, int col, 
    		String val){
    	
    	int count = 0;
    	int tempRow = row;
    	boolean flag = false;
    	
    	// Count vertical adjacent
    	while(state.getCell(tempRow, col).getValue() == val){
    		
    		count++;
    		
    		// Increase row size - move down
    		if(((tempRow + 1) < (state.getSize())) && (!flag)){
    			
    			tempRow++;
    		}
    		// Decrese row size - move up
    		else if((tempRow - 1 >= 0) && flag){
    			
    			tempRow--;
    		}
    		else{
    			break;
    		}
    		// Reached edge of line downwards, go up
    		if(!(state.getCell(tempRow, col).getValue() == val) 
    				&& (row - 1 >= 0)){
    			
    			flag = true;
    			tempRow = row -1;
    		}
    	}
    	
    	return count;
    }
    
    
    
}
