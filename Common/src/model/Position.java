package model;

/**
 * Created by rajabzz on 2/3/15.
 */
public class Position {

    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position(Position position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    public Position getNextPos(Direction dir) {
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            if (x == p.x && y == p.y)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = x;
        int i = Integer.SIZE-1;
        int y = this.y;
        while (y != 0) {
            h |= (y&1) << i;
            y >>= 1;
            i--;
        }
        return h;
    }


    @Override
    public String toString() {
        return "["+x+","+y+"]";
    }
}
