package model;

/**
 * Created by rajabzz on 2/3/15.
 */
public class Position {
    public int x;
    public int y;

    public Position (int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position (Position position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    public Position getNextPos (Direction dir) {
        if ((x&1) == 1) {
            switch (dir) {
                case NORTH:
                    return new Position(x, y + 1);
                case SOUTH:
                    return new Position(x, y - 1);
                case NORTH_EAST:
                    return new Position(x + 1, y);
                case NORTH_WEST:
                    return new Position(x - 1, y);
                case SOUTH_EAST:
                    return new Position(x + 1, y - 1);
                case SOUTH_WEST:
                    return new Position(x - 1, y - 1);
                default:
//                    System.out.println("in chi bud dadi?!?!");
                    return null;
            }
        } else {
            switch (dir) {
                case NORTH:
                    return new Position (x, y + 1);
                case SOUTH:
                    return new Position(x, y - 1);
                case NORTH_EAST:
                    return new Position(x + 1, y + 1);
                case NORTH_WEST:
                    return new Position(x - 1, y + 1);
                case SOUTH_EAST:
                    return new Position(x + 1, y);
                case SOUTH_WEST:
                    return new Position(x - 1, y);
                default:
//                    System.out.println("in chi bud dadi?!?!");
                    return null;
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
