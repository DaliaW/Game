package model.units;

import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.events.SOSResponder;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Unit implements Simulatable, SOSResponder {
	private String unitID;
	private UnitState state;
	private Address location;
	private Rescuable target;
	private int distanceToTarget;
	private int stepsPerCycle;
	private WorldListener worldListener;

	private boolean done = false;
	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Unit(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		this.unitID = unitID;
		this.location = location;
		this.stepsPerCycle = stepsPerCycle;
		this.state = UnitState.IDLE;
		this.worldListener = worldListener;
	}

	public void setWorldListener(WorldListener listener) {
		this.worldListener = listener;
	}

	public WorldListener getWorldListener() {
		return worldListener;
	}

	public UnitState getState() {
		return state;
	}

	public void setState(UnitState state) {
		this.state = state;
	}

	public Address getLocation() {
		return location;
	}

	public void setLocation(Address location) {
		this.location = location;
	}

	public String getUnitID() {
		return unitID;
	}

	public Rescuable getTarget() {
		return target;
	}

	public int getStepsPerCycle() {
		return stepsPerCycle;
	}

	public void setDistanceToTarget(int distanceToTarget) {
		this.distanceToTarget = distanceToTarget;
	}

	@Override
	public void respond(Rescuable r) throws IncompatibleTargetException, CannotTreatException {
			if (target != null && state == UnitState.TREATING) {
				reactivateDisaster();}
			finishRespond(r);
			
		
	}

	public void reactivateDisaster() {
		target.setUnit(null);
		done = false;
		Disaster curr = target.getDisaster();
		curr.setActive(true);
	}

	public void finishRespond(Rescuable r) {
		target = r;
		r.setUnit(this);
		state = UnitState.RESPONDING;

		Address t = r.getLocation();
		distanceToTarget = Math.abs(t.getX() - location.getX())
				+ Math.abs(t.getY() - location.getY());
	//	System.out.print("Responding" + distanceToTarget);

	}

	public abstract void treat();

	public void cycleStep() {
		if (state == UnitState.IDLE)
			return;
		if (distanceToTarget > 0) {
			distanceToTarget = distanceToTarget - stepsPerCycle;
			if (distanceToTarget <= 0) {
				distanceToTarget = 0;
				Address t = target.getLocation();
				worldListener.assignAddress(this, t.getX(), t.getY());
			}
		} else {
			state = UnitState.TREATING;
			treat();
		}
	}

	public void jobsDone() {
		target.setUnit(null);
		target = null;
		state = UnitState.IDLE;
		done = true;
	}
	public boolean canTreat(Rescuable r){
		if(r instanceof ResidentialBuilding)
		{
			if(((ResidentialBuilding) r).safe(this))
			{
				return false;
			}
			
			
		}
		else
		{
			if(((Citizen) r).safe(this))
			{
				return false;
			}
		}
		return true;
	}
	public void setTarget(Rescuable target) {
		this.target = target;
	}

	public String getType() {
		if(this instanceof Evacuator)
			return "Evacuator";
		if(this instanceof Ambulance)
			return "Ambulance";
		if(this instanceof FireTruck)
			return "Fire Truck";
		if(this instanceof DiseaseControlUnit)
			return "Disease Control Unit";
		if(this instanceof GasControlUnit)
			return "Gas Control Unit";
		return "";
	}
	public String getTargetType() {
		if(target instanceof Citizen)
		{
			return "the Citizen "+((Citizen)target).getName();
		}
		if(target instanceof ResidentialBuilding)
		{
			return "the building at "+((ResidentialBuilding)target).getLocation();
		}
		return "";
	}
	public String toString() {
		return "The unit ID: "+unitID+"\n"+
				"The unit type: "+getType()+"\n"+
				"The unit is at location: "+location+"\n"+
				"The unit steps per cycle: "+stepsPerCycle+"\n"+
				"The target is: "+getTargetType()+"\n"+
				"The unit state is: "+state;
	}
}
