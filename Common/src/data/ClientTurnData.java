package data;

import model.Transient;

import java.util.ArrayList;

/**
 * Created by Razi on 2/11/2015.
 */
public class ClientTurnData {
    ArrayList<StaticData> statics;
    ArrayList<DynamicData> dynamics;
    ArrayList<Transient> transients;

    public void setStatics(ArrayList<StaticData> statics) {
        this.statics = statics;
    }

    public void setDynamics(ArrayList<DynamicData> dynamics) {
        this.dynamics = dynamics;
    }

    public void setTransients(ArrayList<Transient> transients) {
        this.transients = transients;
    }

    public ArrayList<StaticData> getStatics() {
        return statics;
    }

    public ArrayList<DynamicData> getDynamics() {
        return dynamics;
    }

    public ArrayList<Transient> getTransients() {
        return transients;
    }
}
