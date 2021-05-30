package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.tinylog.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.tinylog.Logger;

public class Square {

    private final ObjectProperty<Position> position = new SimpleObjectProperty<>();
    public enum SquareStates{UNOCCUPIED, TAKEN};
    public enum SquareOccupant{P1, P2}

    private ObjectProperty<SquareStates> state=new SimpleObjectProperty<>();
    private ObjectProperty<SquareOccupant> occupant=new SimpleObjectProperty<>();

    public Square(Position position) {
        this.position.set(position);
        this.state.set(SquareStates.UNOCCUPIED);
    }
    public Position getPosition() {
        return position.get();
    }
    public ObjectProperty<Position> positionProperty() {
        return position;
    }

    public void takeSquare(boardgame.model.BoardGameModel.Player player){
        state.set(SquareStates.TAKEN);
        switch (player){
            case PLAYER1 -> {occupant.set(SquareOccupant.P1);}
            case PLAYER2 -> {occupant.set(SquareOccupant.P2);}
        }
        Logger.info("Square {} occupied by {}", position.toString(), player.toString());
    }


    public String toString() {
        return position.get().toString() + "; " + state.toString();
    }

    public ObjectProperty<SquareStates> state() {
        return state;
    }

    public SquareStates getState() {
        return state.get();
    }

    public ObjectProperty<SquareOccupant> occupant() {
        return occupant;
    }

    public SquareOccupant getOccupant() {
        return occupant.get();
    }

}
