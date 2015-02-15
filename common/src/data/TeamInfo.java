package data;

/**
 * Created by Razi on 2/11/2015.
 */
public class TeamInfo {
    public TeamInfo(String name, int id)
    {
        this.name = name;
        this.id = id;
    }
    String name;
    int id;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
