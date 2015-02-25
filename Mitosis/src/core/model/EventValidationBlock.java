package core.model;

import java.util.LinkedList;

/**
 * Created by Hadi on 2/25/2015 1:41 PM.
 */
public class EventValidationBlock {

    public int x, y;
    public LinkedList<EventValidationCell> cells;

    public EventValidationBlock(int x, int y) {
        this.x = x;
        this.y = y;
        this.cells = new LinkedList<>();
    }

}
