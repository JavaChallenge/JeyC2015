package data;

import model.Transient;

import java.util.ArrayList;

/**
 * Created by Razi on 2/23/2015.
 */
public class ReceivedClientTurnData {
    ArrayList<ReceivedObjectDiff> statics;
    ArrayList<ReceivedObjectDiff> dynamics;
    ArrayList<ReceivedObjectDiff> transients;

    public ArrayList<ReceivedObjectDiff> getStatics() {
        return statics;
    }

    public ArrayList<ReceivedObjectDiff> getDynamics() {
        return dynamics;
    }

    public ArrayList<ReceivedObjectDiff> getTransients() {
        return transients;
    }
}
