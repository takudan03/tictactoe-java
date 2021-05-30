
import boardgame.model.BoardGameModel;
import model.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BoardGameController {

    private String player1_name="P1";
    private String player2_name="P2";

    /**
     * Enum for the phase of play for each player.
     * Either the player is choosing a random piece on the board
     * or the player is selecting adjacent pieces to selected pieces.
     */


    private List<Position> selectablePositions = new ArrayList<>();
    private List<Position> selectedPositions = new ArrayList<>();
    private List<Position> allPositions = new ArrayList<>();

    private Position selected;


    private boardgame.model.BoardGameModel model ;

    List<Square> squares=new ArrayList<>();
    private Square[] allsquares=new Square[9];

    private List<Position> remaining_squares = new ArrayList<>();
    private Date dateGameStart;

    private String winner;
    @FXML
    private TextField current_player;

    @FXML
    private TextField remaining_squares_field;

    @FXML
    private GridPane board;

    BoardGameModel.Player lastPlayer;

    @FXML
    private void initialize() {
        getGameStartTime();
        createBoard();
        createPieces();
        setSelectablePositions();
        Platform.runLater(() -> {
            current_player.textProperty().setValue(player1_name);
        });

        model.currentPlayer().addListener(
                (observableValue, oldPlayer, newPlayer) -> {
                    System.out.printf("Current Player is %s\n", newPlayer);
                    lastPlayer=oldPlayer;
                    switch (newPlayer){
                        case PLAYER1 -> {
                            current_player.textProperty().setValue(player1_name);
                        }
                        case PLAYER2 -> {
                            current_player.textProperty().setValue(player2_name);
                        }
                    }
                }
        );
        remaining_squares_field.textProperty().bind(model.remainingSpaces().asString());
        model.gameOverProperty().addListener((
                (observableValue, oldValue, newValue) -> {
                    if(newValue){
                        Logger.info("Game Over");
                        gameOver();
                    }
                }));
    }

    private void getGameStartTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateGameStart = new Date();
        Logger.info("Game Start: " + formatter.format(dateGameStart)+"\n");
    }

    private void createBoard() {
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColumnCount(); j++) {
                var square = createSquare();
                board.add(square, j, i);
                remaining_squares.add(new Position(i,j));
                squares.add(new Square(new Position(i, j)));
            }
        }
        allPositions.addAll(remaining_squares);
        model= new boardgame.model.BoardGameModel(squares.toArray(allsquares));
    }

    private StackPane createSquare() {
        var square = new StackPane();
        square.getStyleClass().add("square");
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    private void createPieces() {
        for (int i = 0; i < model.getPieceCount(); i++) {
            model.positionStateProperty(i).addListener(this::pieceStateChange);
        }
    }

    private Circle createPiece(Color color) {
        var piece = new Circle(50);
        piece.setFill(color);
        return piece;
    }

    public void setPlayerNames(String p1, String p2){
        player1_name=p1;
        player2_name=p2;
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        var position = new Position(row, col);
        handleClickOnSquare(position);
    }

    private void handleClickOnSquare(Position position) {
        if (selectablePositions.contains(position)){
                Logger.debug("Selected square {}, unoccupied position", position);
                selectedPositions.add(position);
                selectPosition(position);
                fillSquare();
                if (model.checkIfCurrentPlayerWins()){
                    winner=lastPlayer.toString();
                    gameOver();
                }
                setSelectablePositions();
        } else {
            Logger.debug("Selected square {}, occupied position", position);
        }
    }

    private void fillSquare(){
        var piece = new Circle();
        switch (model.getCurrentPlayer()){
            case PLAYER1 ->{
                piece = createPiece(Color.GREEN);
            }
            case PLAYER2 -> {
                piece = createPiece(Color.RED);
            }
        };
        getSquare(model.getPiecePosition(model.getPieceNumber(selected).getAsInt())).getChildren().add(piece);
        remaining_squares.remove(selected);
        model.occupy_square(model.getPieceNumber(selected).getAsInt(), model.getCurrentPlayer());
        Logger.info("Remaining squares: {}", remaining_squares);
    }

    private void gameOver( ){
        Alert gameover=new Alert(Alert.AlertType.INFORMATION);
        gameover.setTitle("Game Over");
        gameover.setHeaderText("Winner: " + winner);
        gameover.show();
    }


    private void selectPosition(Position position) {
        selected = position;
        var square = getSquare(selected);
        square.getStyleClass().add("selected");
        selectedPositions.add(position);
        showSelectedPosition();
    }

    private void showSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().add("selected");
    }

    private void setSelectablePositions() {
        selectablePositions.clear();
        selectablePositions.addAll(remaining_squares);
    }


    private StackPane getSquare(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    private void pieceStateChange(ObservableValue<? extends Square.SquareStates> observable, Square.SquareStates oldState, Square.SquareStates  newState) {
        Logger.debug("Piece at position: {} {} -> {}", selected, oldState, newState);
    }
}
