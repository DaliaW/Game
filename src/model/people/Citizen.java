package model.people;

import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;
import simulation.Simulator;
import controller.CommandCenter;
import controller.CommandCenterCopy;
import exceptions.DisasterException;
import exceptions.IncompatibleTargetException;
import exceptions.SimulationException;
import model.disasters.Disaster;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.SOSListener;
import model.events.WorldListener;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Unit;
import model.units.UnitState;

public class Citizen implements Rescuable,Simulatable{
	private CitizenState state;
	private Disaster disaster;
	private String name;
	private String nationalID;
	private int age;
	private int hp;
	private int bloodLoss;
	private int toxicity;
	private Address location;
	private SOSListener emergencyService;
	private WorldListener worldListener;
	
	private boolean newlyDead=true;
	
	private Unit unit;
	
	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public void setNewlyDead(boolean newlyDead) {
		this.newlyDead = newlyDead;
	}

	public boolean isNewlyDead() {
		return newlyDead;
	}

	public Citizen(Address location,String nationalID, String name, int age
			,WorldListener worldListener) {
		this.name = name;
		this.nationalID = nationalID;
		this.age = age;
		this.location = location;
		this.state=CitizenState.SAFE;
		this.hp=100;
		this.worldListener = worldListener;
	}
	
	public WorldListener getWorldListener() {
		return worldListener;
	}

	public void setWorldListener(WorldListener listener) {
		this.worldListener = listener;
	}

	public CitizenState getState() {
		return state;
	}
	public void setState(CitizenState state) {
		this.state = state;
	}
	public String getName() {
		return name;
	}
	public int getAge() {
		return age;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
		if(this.hp>=100)
			this.hp=100;
		else if(this.hp<=0){
			this.hp = 0;
			state=CitizenState.DECEASED;
		}
	}
	public int getBloodLoss() {
		return bloodLoss;
	}
	public void setBloodLoss(int bloodLoss) {
		this.bloodLoss = bloodLoss;
		if(bloodLoss<=0)
			this.bloodLoss=0;
		else if(bloodLoss>=100)
		{
			this.bloodLoss=100;
			setHp(0);
		}
	}
	public int getToxicity() {
		return toxicity;
	}
	public void setToxicity(int toxicity) {
		this.toxicity = toxicity;
		if(toxicity>=100)
		{
			this.toxicity=100;
			setHp(0);
		}
		else if(this.toxicity<=0)
			this.toxicity=0;
	}
	public Address getLocation() {
		return location;
	}
	public void setLocation(Address location) {
		this.location = location;
	}
	public Disaster getDisaster() {
		return disaster;
	}
	public String getNationalID() {
		return nationalID;
	}
	
	public void setEmergencyService(SOSListener emergency) {
		this.emergencyService = emergency;
	}
	@Override
	public void cycleStep() {
		if(bloodLoss>0 && bloodLoss<30)
			setHp(hp-5);
		else if(bloodLoss>=30 && bloodLoss<70)
			setHp(hp-10);
		else if(bloodLoss >=70)
			setHp(hp-15);
		if (toxicity >0 && toxicity < 30)
			setHp(hp-5);
		else if(toxicity>=30 &&toxicity<70)
			setHp(hp-10);
		else if(toxicity>=70)
			setHp(hp-15);
	}
	public static Citizen copy(Citizen c) {
		CommandCenterCopy emServiceCopy = new CommandCenterCopy();
		Citizen citizenCopy =new Citizen(c.location,c.nationalID, c.name, c.age, emServiceCopy.getEngine());
		citizenCopy.setEmergencyService(emServiceCopy);
		citizenCopy.setBloodLoss(c.bloodLoss);
		citizenCopy.setHp(c.hp);
		citizenCopy.setToxicity(c.toxicity);
		citizenCopy.setState(c.state);
		return citizenCopy;
	}
	public static boolean proceedTheSim(Citizen c,Unit u) throws DisasterException {
		CommandCenterCopy emServiceCopy = new CommandCenterCopy();
		Citizen citizenCopy =new Citizen(c.location,c.nationalID, c.name, c.age, emServiceCopy.getEngine());
		Disaster disasterCopy = null;
		if(c.disaster instanceof Injury)
		{
			disasterCopy = new Injury(0,citizenCopy);
		}
		if(c.disaster instanceof Infection)
		{
			disasterCopy = new Infection(0,citizenCopy);
		}
		citizenCopy.setEmergencyService(emServiceCopy);
		disasterCopy.strike();
		citizenCopy.setBloodLoss(c.bloodLoss);
		citizenCopy.setHp(c.hp);
		citizenCopy.setToxicity(c.toxicity);
		citizenCopy.setState(c.state);
		Unit unitCopy = null;
		if(u instanceof Ambulance)
		{
			unitCopy =new Ambulance(u.getUnitID(), u.getLocation(), u.getStepsPerCycle(),u.getWorldListener());
		}
		if(u instanceof DiseaseControlUnit)
		{
			unitCopy =new DiseaseControlUnit(u.getUnitID(), u.getLocation(), u.getStepsPerCycle(),u.getWorldListener());
		}
		try
		{
			unitCopy.respond(citizenCopy);
			while(unitCopy.getState() != UnitState.IDLE)
			{
				unitCopy.cycleStep();
				if(disasterCopy.isActive())
					disasterCopy.cycleStep();
				citizenCopy.cycleStep();
			}
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
		if(citizenCopy.getState()==CitizenState.DECEASED)
		{
			return false;
		}
		return true;
	}

	@Override
	public void struckBy(Disaster d) {
		if(disaster!=null)
			disaster.setActive(false);
		disaster=d;
		state= CitizenState.IN_TROUBLE;
		emergencyService.receiveSOSCall(this);
		
	}

	public boolean safe(Unit u) {
		if( u instanceof Ambulance)
		{
			if(this.bloodLoss ==0)
			{
				return true;
			}
			return false;
		}
		if( u instanceof DiseaseControlUnit)
		{
			if(this.toxicity ==0 )
			{
				return true;
			}
			return false;
		}
		return false;
		
	}
	public String toString() {
		return "The Citizen location is: "+location+"\n"
				+"Name: "+name+"\n"
				+"Age: "+age+"\n"
				+"NationalID: "+nationalID+"\n"
				+"The hp is: "+hp+"\n"
				+"The blood loss is: "+bloodLoss+"\n"
				+"The toxicity: "+toxicity+"\n"
				+"The citizen state is: "+state
				+(disaster == null?"":"\nThe disaster suffering from: "+disaster);
	}
	
}
