package controller;

import java.util.ArrayList;

import model.events.SOSListener;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.units.Unit;
import simulation.Rescuable;
import simulation.SimulatorCopy;

public class CommandCenterCopy implements SOSListener {

	SimulatorCopy engine;
	ArrayList<ResidentialBuilding> visibleBuildings;
	ArrayList<Citizen> visibleCitizens;
	ArrayList<Unit> emergencyUnits;
	
	public CommandCenterCopy() {
		engine = new SimulatorCopy(this);
		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		emergencyUnits = engine.getEmergencyUnits();
	}
	
	public void receiveSOSCall(Rescuable r) {

		if (r instanceof ResidentialBuilding) {

			if (!visibleBuildings.contains(r))
				visibleBuildings.add((ResidentialBuilding) r);

		} else {

			if (!visibleCitizens.contains(r))
				visibleCitizens.add((Citizen) r);
		}
	}
	public WorldListener getEngine() {
		return this.engine;
	}
}
