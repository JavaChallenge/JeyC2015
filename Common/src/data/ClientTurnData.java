package data;

import model.Transient;

import java.util.ArrayList;

/**
 * Created by Razi on 2/11/2015.
 */
public class ClientTurnData {
    ArrayList<ObjectDiff> statics;
    ArrayList<ObjectDiff> dynamics;
    ArrayList<Transient> transients;

    public void setStatics(ArrayList<ObjectDiff> statics) {
        this.statics = statics;
    }

    public void setDynamics(ArrayList<ObjectDiff> dynamics) {
        this.dynamics = dynamics;
    }

    public void setTransients(ArrayList<Transient> transients) {
        this.transients = transients;
    }

    /*public ArrayList<StaticData> getStatics() {
        return statics;
    }*/

    /*public ArrayList<DynamicData> getDynamics() {
        return dynamics;
    }*/

    /*public ArrayList<Transient> getTransients() {
        return transients;
    }*/
}
