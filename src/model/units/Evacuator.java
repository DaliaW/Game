package model.units;

import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import simulation.Address;
import simulation.Rescuable;

public class Evacuator extends PoliceUnit {

	public Evacuator(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener, int maxCapacity) {
		super(unitID, location, stepsPerCycle, worldListener, maxCapacity);

	}

	@Override
	public void treat() {
		ResidentialBuilding target = (ResidentialBuilding) getTarget();
		if (target.getStructuralIntegrity() == 0
				|| target.getOccupants().size() == 0) {
			jobsDone();
			return;
		}
		
		

		for (int i = 0; getPassengers().size() != getMaxCapacity()
				&& i < target.getOccupants().size(); i++) {
			getPassengers().add(target.getOccupants().remove(i));
			i--;
		}

		setDistanceToBase(target.getLocation().getX()
				+ target.getLocation().getY());

	}
	
	@Override
	public void respond(Rescuable r) throws  IncompatibleTargetException, CannotTreatException {	
		
		if(!(r instanceof ResidentialBuilding))
		{
			throw new IncompatibleTargetException(this,r,"Evacuator responds to buildings only");
		}else if(!(canTreat(r))) {
			throw new CannotTreatException(this, r,"can not treated");
		}
		
		super.respond(r);
		
	}
	public String getPassengersInfo() {
		String res="";
		for(Citizen c:getPassengers())
		{
			res+= c;
		}
		return res;
	}
	public String toString() {
		return super.toString()+"\n"+
			   "The evacuator has "+getPassengers().size()+" passenger(s)\n"+
				"The passengers information is: "+getPassengersInfo();
	}
}
