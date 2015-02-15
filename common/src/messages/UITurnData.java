package messages;

import core.model.DynamicGameObject;
import core.model.StaticGameObject;
import core.model.Transient;

import java.util.ArrayList;

/**
 * Created by Razi on 2/11/2015.
 */
public class UITurnData {
    String view;
    ArrayList<StaticData> statics;
    ArrayList<DynamicData> dynamics;
    ArrayList<Transient> transients;

    public void setView(String view) {
        this.view = view;
    }

    public void setStatics(ArrayList<StaticData> statics) {
        this.statics = statics;
    }

    public void setDynamics(ArrayList<DynamicData> dynamics) {
        this.dynamics = dynamics;
    }

    public void setTransients(ArrayList<Transient> transients) {
        this.transients = transients;
    }
}
