package boardgame.model;

import javafx.beans.property.*;
import model.Position;
import model.Square;

import java.util.*;

public class BoardGameModel {
    /**
     * Abstract model for the game
     */

    private final Square[] squares;

    private int [][] winningPositionCombos= {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    /**
     * Static variable storing the board size.
     */
    public static int BOARD_SIZE = 3;


    /**
     * Enum with the two players as values. Includes a method to change the value of an object
     */
    public enum Player {
        PLAYER1, PLAYER2;

        public Player next() {
            return switch (this) {
                case PLAYER1 -> PLAYER2;
                case PLAYER2 -> PLAYER1;
            };
        }
    }

    private ReadOnlyObjectWrapper<Player> currentPlayer = new ReadOnlyObjectWrapper<>();
    private ReadOnlyIntegerWrapper remaining_spaces = new ReadOnlyIntegerWrapper();
    private ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper();

    public BoardGameModel(Square... squares) {
        checkSquares(squares);
        this.squares = squares.clone();
        remaining_spaces.set(BOARD_SIZE * BOARD_SIZE);
        currentPlayer.set(Player.PLAYER1);
        gameOver.bind(remaining_spaces.isEqualTo(0));
    }

    private void checkSquares(Square[] squares) {
        var seen = new HashSet<Position>();
        for (var square : squares) {
            if (!isOnBoard(square.getPosition()) || seen.contains(square.getPosition())) {
                throw new IllegalArgumentException();
            }
            seen.add(square.getPosition());
        }
    }

    public void occupy_square(int squarenum, Player currPlayer) {
        squares[squarenum].takeSquare(currPlayer);
        currentPlayer.set(currentPlayer.get().next());
        remaining_spaces.set(getRemainingSpaces() - 1);
    }

    /**
     * Returns the position of a piece
     *
     * @param pieceNumber
     * @return
     */
    public Position getPiecePosition(int pieceNumber) {
        return squares[pieceNumber].getPosition();
    }

    public ObjectProperty<Square.SquareStates> positionStateProperty(int pieceNumber) {
        return squares[pieceNumber].state();
    }

    public boolean isGameOver() {
        return gameOver.get();
    }

    public ReadOnlyBooleanProperty gameOverProperty() {
        return gameOver.getReadOnlyProperty();
    }

    public Player getCurrentPlayer() {
        return currentPlayer.get();
    }

    public ReadOnlyObjectProperty<Player> currentPlayer() {
        return currentPlayer.getReadOnlyProperty();
    }

    public int getRemainingSpaces() {
        return remaining_spaces.get();
    }

    public ReadOnlyIntegerProperty remainingSpaces() {
        return remaining_spaces.getReadOnlyProperty();
    }

    /**
     * Checks if a given position is within the confines of the board
     *
     * @param position
     * @return
     */
    public static boolean isOnBoard(Position position) {
        return 0 <= position.row() && position.row() < BOARD_SIZE
                && 0 <= position.col() && position.col() < BOARD_SIZE;
    }

    public List<Position> getSquarePositions() {
        List<Position> positions = new ArrayList<>(squares.length);
        for (var square : squares) {
            positions.add(square.getPosition());
        }
        return positions;
    }

    /**
     * For a provided position on the board, return the pieceNumber of the piece located there
     */
    public OptionalInt getPieceNumber(Position position) {
        for (int i = 0; i < squares.length; i++) {
            if (squares[i].getPosition().equals(position)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    public int getPieceCount() {
        return squares.length;
    }


    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (var square : squares) {
            joiner.add(square.toString());
        }
        return joiner.toString();
    }

    public static void main(String[] args) {
        BoardGameModel model = new BoardGameModel();
    }

    public boolean checkIfCurrentPlayerWins() {
        for (int[] winningPositionCombo : winningPositionCombos) {
            if (
                    (squares[winningPositionCombo[0]].getState() == Square.SquareStates.TAKEN &&
                            squares[winningPositionCombo[1]].getState() == Square.SquareStates.TAKEN &&
                            squares[winningPositionCombo[2]].getState() == Square.SquareStates.TAKEN
                    )
                            &&
                            (squares[winningPositionCombo[0]].getOccupant().toString().equals(squares[winningPositionCombo[1]].getOccupant().toString())
                                    && squares[winningPositionCombo[0]].getOccupant().toString().equals(squares[winningPositionCombo[2]].getOccupant().toString())
                            )
            ) {
                return true;
            }
        }
        return false;
    }
}
