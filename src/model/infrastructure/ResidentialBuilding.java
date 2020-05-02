package model.infrastructure;

import java.util.ArrayList;

import controller.CommandCenter;
import controller.CommandCenterCopy;
import exceptions.DisasterException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Injury;
import model.events.SOSListener;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.Unit;
import model.units.UnitState;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;
import simulation.Simulator;
import simulation.SimulatorCopy;

public class ResidentialBuilding implements Rescuable, Simulatable 
{

	private Address location;
	private int structuralIntegrity;
	private int fireDamage;
	private int gasLevel;
	private int foundationDamage;
	private ArrayList<Citizen> occupants;
	private Disaster disaster;
	private SOSListener emergencyService;
	
	private boolean newlyCollapsed=true;
	
	private Unit unit;
	private int initialOccupants;
	public int getInitialOccupants() {
		return initialOccupants;
	}
	public void setInitialOccupants(int initialOccupants) {
		this.initialOccupants = initialOccupants;
	}
	public boolean isNewlyCollapsed() {
		return newlyCollapsed;
	}
	public void setNewlyCollapsed(boolean newlyCollapsed) {
		this.newlyCollapsed = newlyCollapsed;
	}
	public ResidentialBuilding(Address location) {
		this.location = location;
		this.structuralIntegrity=100;
		occupants= new ArrayList<Citizen>();
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public int getStructuralIntegrity() {
		return structuralIntegrity;
	}
	public void setStructuralIntegrity(int structuralIntegrity) {
		this.structuralIntegrity = structuralIntegrity;
		if(structuralIntegrity<=0)
		{
			this.structuralIntegrity=0;
			for(int i = 0 ; i< occupants.size(); i++)
				occupants.get(i).setHp(0);
		}
	}
	public int getFireDamage() {
		return fireDamage;
	}
	public void setFireDamage(int fireDamage) {
		this.fireDamage = fireDamage;
		if(fireDamage<=0)
			this.fireDamage=0;
		else if(fireDamage>=100)
			this.fireDamage=100;
	}
	public int getGasLevel() {
		return gasLevel;
	}
	public void setGasLevel(int gasLevel) {
		this.gasLevel = gasLevel;
		if(this.gasLevel<=0)
			this.gasLevel=0;
		else if(this.gasLevel>=100)
		{
			this.gasLevel=100;
			for(int i = 0 ; i < occupants.size(); i++)
			{
				occupants.get(i).setHp(0);
			}
		}
	}
	public int getFoundationDamage() {
		return foundationDamage;
	}
	public void setFoundationDamage(int foundationDamage) {
		this.foundationDamage = foundationDamage;
		if(this.foundationDamage>=100)
		{
			
			setStructuralIntegrity(0);
		}
			
	}
	public Address getLocation() {
		return location;
	}
	public ArrayList<Citizen> getOccupants() {
		return occupants;
	}
	public Disaster getDisaster() {
		return disaster;
	}
	public void setEmergencyService(SOSListener emergency) {
		this.emergencyService = emergency;
	}
	@Override
	public void cycleStep() {
	
		if(foundationDamage>0)
		{
			
			int damage= (int)((Math.random()*6)+5);
			setStructuralIntegrity(structuralIntegrity-damage);
			
		}
		if(fireDamage>0 &&fireDamage<30)
			setStructuralIntegrity(structuralIntegrity-3);
		else if(fireDamage>=30 &&fireDamage<70)
			setStructuralIntegrity(structuralIntegrity-5);
		else if(fireDamage>=70)
			setStructuralIntegrity(structuralIntegrity-7);
		
	}
	public static boolean proceedTheSim(ResidentialBuilding b,Unit u) throws DisasterException {
		ResidentialBuilding buildingCopy =new ResidentialBuilding(b.location);
		Disaster disasterCopy = null;
		if(b.disaster instanceof GasLeak)
		{
			disasterCopy = new GasLeak(0,buildingCopy);
		}
		if(b.disaster instanceof Fire)
		{
			disasterCopy = new Fire(0,buildingCopy);
		}
		if(b.disaster instanceof Collapse)
		{
			disasterCopy = new Collapse(0,buildingCopy);
		}
		CommandCenterCopy emServiceCopy = new CommandCenterCopy();
		buildingCopy.setEmergencyService(emServiceCopy);
		disasterCopy.strike();
		buildingCopy.setFireDamage(b.fireDamage);
		buildingCopy.setFoundationDamage(b.foundationDamage);
		buildingCopy.setGasLevel(b.gasLevel);
		buildingCopy.setStructuralIntegrity(b.structuralIntegrity);
		buildingCopy.setInitialOccupants(b.getInitialOccupants());
		buildingCopy.occupants = new ArrayList<>();
		for(Citizen c:b.getOccupants())
		{
			buildingCopy.occupants.add(Citizen.copy(c));
		}
		Unit unitCopy = null;
		if(u instanceof GasControlUnit)
		{
			unitCopy = new GasControlUnit(u.getUnitID(), u.getLocation(), u.getStepsPerCycle(),u.getWorldListener());
		}
		if(u instanceof FireTruck)
		{
			unitCopy = new FireTruck(u.getUnitID(), u.getLocation(), u.getStepsPerCycle(),u.getWorldListener());
		}
		if(u instanceof Evacuator)
		{
			unitCopy = new Evacuator(u.getUnitID(), u.getLocation(), u.getStepsPerCycle(),u.getWorldListener(),((Evacuator) u).getMaxCapacity());
		}
		try
		{
			unitCopy.respond(buildingCopy);
		//	System.out.println(buildingCopy.structuralIntegrity +"   " +buildingCopy.fireDamage);
			while(unitCopy.getState() != UnitState.IDLE)
			{
				unitCopy.cycleStep();
				if(disasterCopy.isActive())
					disasterCopy.cycleStep();
				buildingCopy.cycleStep();
		//		System.out.println(buildingCopy.structuralIntegrity +"   " +buildingCopy.fireDamage);
			}
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
		if(disasterCopy instanceof Collapse)
		{
			if(buildingCopy.occupants.size() == buildingCopy.getInitialOccupants())
			{
				return false;
			}
		}
		else
		{
			boolean allDead = true;
			int n =buildingCopy.getOccupants().size();
			System.out.println("n "+ n);
			for(int i=0;i<n;i++)
			{
				if(buildingCopy.getOccupants().get(i).getState() != CitizenState.DECEASED)
				{
					allDead = false;
					break;
				}
			}
			if(allDead)
				return false;
		}
		return true;
	}
	@Override
	public void struckBy(Disaster d) {
		if(disaster!=null)
			disaster.setActive(false);
		disaster=d;
		emergencyService.receiveSOSCall(this);
	}
	public boolean safe(Unit u){
		if( u instanceof FireTruck)
		{
			if(this.fireDamage == 0) // check that it is fire disaster ?
			{
				return true;
			}else
			return false;
		}
		if( u instanceof GasControlUnit)
		{
			if(this.gasLevel ==0)
			{
				return true;
			}
			return false;
		}
		if( u instanceof Evacuator)
		{
			//if(this.getOccupants().isEmpty())
		if(this.foundationDamage==0)
			{
				return true;
			}
			return false;
		}
		return false;
	}
	public String citizensInfo() {
		String res = "";
		for(Citizen c : occupants)
		{
			res+=c+"\n";
		}
		return res;
	}
	public String toString() {
		return "The building is at the location: "+location+"\n"
				+"The structural integrity is: "+structuralIntegrity+"\n"
				+"The fire damage is: "+fireDamage+"\n"
				+"The gas Level is: "+gasLevel+"\n"
				+"The foundation damage is: "+foundationDamage+"\n"
				+"The number of occupants is: "+occupants.size()+"\n"
				+citizensInfo();
	}
}
