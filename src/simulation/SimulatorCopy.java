package simulation;

import java.util.ArrayList;

import model.events.SOSListener;
import model.events.WorldListener;
import model.people.Citizen;
import model.units.Unit;

public class SimulatorCopy implements WorldListener {

	private Address[][] world;
	private SOSListener emergencyService;
	private ArrayList<Unit> emergencyUnits;
	
	public SimulatorCopy(SOSListener l) {
		emergencyService = l;
		world = new Address[10][10];
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				world[i][j] = new Address(i, j);
		emergencyUnits = new ArrayList<Unit>();
	}
	
	public ArrayList<Unit> getEmergencyUnits() {

		return emergencyUnits;
	}
	
	public void assignAddress(Simulatable s, int x, int y) {
		if (s instanceof Citizen)
			((Citizen) s).setLocation(world[x][y]);
		else
			((Unit) s).setLocation(world[x][y]);

	}

	public void setEmergencyService(SOSListener emergency) {
		this.emergencyService = emergency;
	}
	
}
