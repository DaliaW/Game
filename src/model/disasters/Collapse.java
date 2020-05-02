package model.disasters;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.DisasterException;
import model.infrastructure.ResidentialBuilding;


public class Collapse extends Disaster {

	public Collapse(int startCycle, ResidentialBuilding target) {
		super(startCycle, target);
		
	}
	public void strike() throws DisasterException{
		ResidentialBuilding target= (ResidentialBuilding)getTarget();	
		if(target.getStructuralIntegrity()==0)
		{
			throw new BuildingAlreadyCollapsedException(this,"The building is already collapsed");
		}
		target.setFoundationDamage(target.getFoundationDamage()+10);
		super.strike();
	}
	public void cycleStep()
	{
		ResidentialBuilding target= (ResidentialBuilding)getTarget();
		target.setFoundationDamage(target.getFoundationDamage()+10);
	}
	public String toString() {
		return "Collapse Disaster affecting the building at "+((ResidentialBuilding)getTarget()).getLocation();
	}
}
