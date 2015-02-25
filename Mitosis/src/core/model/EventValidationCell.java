package core.model;

import model.Position;

/**
 * Created by Hadi on 2/25/2015 1:43 PM.
 */
public class EventValidationCell {

    public boolean moved;
    public Position from, to;

    public EventValidationCell(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    public Position getPosition() {
        return moved ? to : from;
    }

}
