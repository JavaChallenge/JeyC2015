package core.model;

import core.Context;
import model.Direction;
import model.GameEvent;
import model.Position;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Hadi on 2/25/2015 1:41 PM.
 */
public class EventValidationMap {

    private Map map;
    private Context ctx;
    private EventValidationBlock[][] blocks;

    public EventValidationMap(Context ctx, Map map, int w, int h) {
        this.map = map;
        this.ctx = ctx;
        this.blocks = new EventValidationBlock[h][w];
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                blocks[i][j] = new EventValidationBlock(j, i);
    }

    public boolean[] validate(ArrayList<GameEvent> validEvents) {
//        System.out.println("validate");
        int n = validEvents.size();
        Cell[] cells = new Cell[n];
        Position[] starts = new Position[n];
        Position[] ends = new Position[n];
        EventValidationCell[] evcs = new EventValidationCell[n];
//        System.out.println("init");
        for (int i = 0; i < n; i++) {
            GameEvent event = validEvents.get(i);
            Cell cell = ctx.getCell(event.getGameObjectId());
            Direction dir = Direction.valueOf(event.getArgs()[GameEvent.ARG_INDEX_MOVE_DIRECTION]);
            Position start = cell.getPos();
            Position end = start.getNextPos(dir);
            cells[i] = cell;
            starts[i] = start;
            ends[i] = end;
//            System.out.printf("%20s, %10s, %10s%n", cells[i].getId(), starts[i].toString(), ends[i].toString());
        }
//        System.out.println("adding");
        for (int i = 0; i < n; i++) {
            EventValidationCell evc = new EventValidationCell(starts[i], ends[i]);
            evcs[i] = evc;
            blocks[starts[i].y][starts[i].x].cells.add(evc);
        }
        LinkedList<EventValidationCell> invalidQueue = new LinkedList<>();
        LinkedList<EventValidationCell> secondQueue = new LinkedList<>();
        LinkedList<Position> invalidPositions = new LinkedList<>();
        // primary invalid moves
//        System.out.println("primary invalids");
        for (int i = 0; i < n; i++) {
            Position pos = ends[i];
            if (blocks[pos.y][pos.x].cells.isEmpty() && !map.at(pos).isEmpty()) {
                evcs[i].moved = false;
                invalidQueue.addLast(evcs[i]);
            } else {
                evcs[i].moved = true;
            }
        }
//        System.out.println("primary invalids = " + totalInvalid);
//        System.out.println("reverting primary invalids");
        for (int i = 0; i < n; i++) {
            if (evcs[i].moved) {
                evcs[i].moved = false;
                int noc = doMove(evcs[i]);
                if (noc > 1)
                    invalidPositions.add(evcs[i].getPosition());
            } else {
//                revertMove(evcs[i]);
//                invalidQueue.add(evcs[i]);
            }
        }
        //
//        System.out.println("start while");
        boolean cont = true;
        while (cont) {
            while (!invalidQueue.isEmpty()) {
//                System.out.println("inner while");
                for (EventValidationCell evc : invalidQueue) {
                    Position pos = evc.from;
                    LinkedList<EventValidationCell> events = blocks[pos.y][pos.x].cells;
                    while (!events.isEmpty()) {
                        EventValidationCell evc2 = events.pollFirst();
                        if (evc2 != evc) {
                            int noc = revertMove(evc2);
                            if (noc > 1)
                                invalidPositions.add(evc2.getPosition());
                            secondQueue.addLast(evc2);
                        }
                    }
                    events.add(evc);
                }
                LinkedList<EventValidationCell> tmp = invalidQueue;
                invalidQueue = secondQueue;
                secondQueue = tmp;
                secondQueue.clear();
            }
            cont = !invalidPositions.isEmpty();
            for (Position pos : invalidPositions) {
                LinkedList<EventValidationCell> cells1 = blocks[pos.y][pos.x].cells;
                int k = cells1.size();
                if (k > 1) {
//                        System.out.println("randomizing " + k);
                    int r = (int) (Math.random() * k);
                    EventValidationCell evc1 = cells1.get(r);
                    while (!cells1.isEmpty()) {
                        EventValidationCell evc2 = cells1.pollFirst();
                        if (evc2 != evc1) {
                            revertMove(evc2);
                            invalidQueue.add(evc2);
                        }
                    }
                    cells1.add(evc1);
                    cont = true;
                }
            }
            invalidPositions.clear();
//            for (EventValidationCell evc : evcs) {
////                System.out.println("inner for");
//                if (evc.moved) {
//                    Position pos = evc.to;
//                    LinkedList<EventValidationCell> cells1 = blocks[pos.y][pos.x].cells;
//                    int k = cells1.size();
//                    if (k > 1) {
////                        System.out.println("randomizing " + k);
//                        int r = (int) (Math.random()*k);
//                        EventValidationCell evc1 = cells1.get(r);
//                        while (!cells1.isEmpty()) {
//                            EventValidationCell evc2 = cells1.pollFirst();
//                            if (evc2 != evc1) {
//                                revertMove(evc2);
//                                invalidQueue.add(evc2);
//                            }
//                        }
//                        cells1.add(evc1);
//                        cont = true;
//                        break;
//                    }
//                }
//            }
        }
        boolean[] validationResult = new boolean[n];
        for (int i = 0; i < n; i++) {
            validationResult[i] = evcs[i].moved;
            Position pos = evcs[i].getPosition();
            blocks[pos.y][pos.x].cells.clear();
        }
        return validationResult;
    }

    private int doMove(EventValidationCell evc) {
        if (evc.moved)
            return 0;
        Position start = evc.from, end = evc.to;
        blocks[start.y][start.x].cells.remove(evc);
        blocks[end.y][end.x].cells.add(evc);
        evc.moved = true;
        return blocks[end.y][end.x].cells.size();
    }

    private int revertMove(EventValidationCell evc) {
        if (!evc.moved)
            return 0;
        Position start = evc.from, end = evc.to;
        blocks[end.y][end.x].cells.remove(evc);
        blocks[start.y][start.x].cells.add(evc);
        evc.moved = false;
        return blocks[start.y][start.x].cells.size();
    }

}
