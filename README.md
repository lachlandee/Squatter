# Squatter

The rules of the game of squatter are described in a pdf file located in the main directory.

This program uses a minimax search algorithm, combined with alpha-beta pruning to allow a greater depth of search, thus resulting in better performance. The depth of the search varies as the game goes on to ensure that time constraints are met, whilst still allowing a fullsearch in the cricial stages of the game.


There are 3 possible players which can be run with this program.
1. Ldee: Player which implements Artificial Intelligence search techniques
2. RandomPlayer: Player which picks a random move
3. UserPlayer: Player which you control by providing "row col" for your desired move (row and col start at 0)

#Usage:
Call program using Referee.class file

Input arguments: 

Board size, Player 1, Player 2

#Example Usage:

Assuming your current directory is the root of the bin directory, the following will allow the UserPlayer to verse the Ldee player with a board size of 6 rows and columns:

    aiproj.squatter.Referee 6 aiproj.squatter.Ldee.Ldee aiproj.squatter.Ldee.UserPlayer