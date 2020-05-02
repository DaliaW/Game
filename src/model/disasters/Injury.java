package model.disasters;

import exceptions.CitizenAlreadyDeadException;
import exceptions.DisasterException;
import model.people.Citizen;
import model.people.CitizenState;


public class Injury extends Disaster {

	public Injury(int startCycle, Citizen target) {
		super(startCycle, target);
	}
	@Override
	public void strike() throws DisasterException {
		Citizen target = (Citizen)getTarget();
		if(target.getState() == CitizenState.DECEASED)
		{
			throw new CitizenAlreadyDeadException(this,"The citizen has already dead");
		}
		target.setBloodLoss(target.getBloodLoss()+30);
		super.strike();
	}
	@Override
	public void cycleStep() {
		Citizen target = (Citizen)getTarget();
		target.setBloodLoss(target.getBloodLoss()+10);
		
	}
	public String toString() {
		return "Injury Disaster affecting the citizen whose name is "+((Citizen)getTarget()).getName();
	}
}
