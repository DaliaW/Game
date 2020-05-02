package model.disasters;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.DisasterException;
import model.infrastructure.ResidentialBuilding;


public class GasLeak extends Disaster {

	public GasLeak(int startCycle, ResidentialBuilding target) {
		super(startCycle, target);
	}
	
	@Override
	public void strike() throws DisasterException{
		ResidentialBuilding target= (ResidentialBuilding)getTarget();
		if(target.getStructuralIntegrity()==0)
		{
			throw new BuildingAlreadyCollapsedException(this,"cannot apply disaster, building is already collapsed");
		}
		target.setGasLevel(target.getGasLevel()+10);
		super.strike();
	}
	@Override
	public void cycleStep() {
		ResidentialBuilding target= (ResidentialBuilding)getTarget();
		target.setGasLevel(target.getGasLevel()+15);
		
	}
	public String toString() {
		return "Gas Leak Disaster affecting the building at "+((ResidentialBuilding)getTarget()).getLocation();
	}
}
