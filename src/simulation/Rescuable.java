package simulation;

import model.disasters.Disaster;
import model.units.Unit;

public interface Rescuable {
public void struckBy(Disaster d);
public Address getLocation();
public Disaster getDisaster();
public Unit getUnit();
public void setUnit(Unit unit);
//public boolean proceedTheSim(Unit u);
}
