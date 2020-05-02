package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.JFrame;

import model.disasters.Collapse;
import model.disasters.Disaster;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.SOSListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.Unit;

import org.junit.Test;

import simulation.Address;
import simulation.Rescuable;
import simulation.Simulator;
import controller.CommandCenter;
import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class M3Tests {

	String simulationExceptionPath = "exceptions.SimulationException";
	String disasterExceptionPath = "exceptions.DisasterException";
	String buildingAlreadyCollapsedExceptionPath = "exceptions.BuildingAlreadyCollapsedException";
	String citizenAlreadyDeadExceptionPath = "exceptions.CitizenAlreadyDeadException";
	String unitExceptionPath = "exceptions.UnitException";
	String cannotTreatExceptionPath = "exceptions.CannotTreatException";
	String incompatibleTargetExceptionPath = "exceptions.IncompatibleTargetException";
	JFrame frame;
	SOSListener sos = new SOSListener() {

		@Override
		public void receiveSOSCall(Rescuable r) {

		}
	};

	@Test()
	public void testConstructorSimulationException1() throws Exception {

		Class[] inputs = { String.class };
		testConstructorExists(Class.forName(simulationExceptionPath), inputs);
	}

	@Test()
	public void testConstructorSimulationException2() throws Exception {

		Class[] inputs = {};
		testConstructorExists(Class.forName(simulationExceptionPath), inputs);
	}

	@Test()
	public void testConstructorDisasterException1() throws Exception {
		Class[] inputs = { Disaster.class };
		testConstructorExists(Class.forName(disasterExceptionPath), inputs);
	}

	@Test()
	public void testConstructorDisasterException2() throws Exception {
		Class[] inputs = { Disaster.class, String.class };
		testConstructorExists(Class.forName(disasterExceptionPath), inputs);
	}

	@Test()
	public void testConstructorBuildingAlreadyCollapsedException1()
			throws Exception {
		Class[] inputs = { Disaster.class };
		testConstructorExists(
				Class.forName(buildingAlreadyCollapsedExceptionPath), inputs);
	}

	@Test()
	public void testConstructorBuildingAlreadyCollapsedException2()
			throws Exception {
		Class[] inputs = { Disaster.class, String.class };
		testConstructorExists(
				Class.forName(buildingAlreadyCollapsedExceptionPath), inputs);
	}

	@Test()
	public void testConstructorCitizenAlreadyDeadException1() throws Exception {
		Class[] inputs = { Disaster.class, String.class };
		testConstructorExists(Class.forName(citizenAlreadyDeadExceptionPath),
				inputs);
	}

	@Test()
	public void testConstructorCitizenAlreadyDeadException2() throws Exception {
		Class[] inputs = { Disaster.class };
		testConstructorExists(Class.forName(citizenAlreadyDeadExceptionPath),
				inputs);
	}

	@Test()
	public void testConstructorUnitException1() throws Exception {
		Class[] inputs = { Unit.class, Rescuable.class, String.class };
		testConstructorExists(Class.forName(unitExceptionPath), inputs);
	}

	@Test()
	public void testConstructorUnitException2() throws Exception {
		Class[] inputs = { Unit.class, Rescuable.class };
		testConstructorExists(Class.forName(unitExceptionPath), inputs);
	}

	@Test()
	public void testConstructorAlreadySafeException1() throws Exception {
		Class[] inputs = { Unit.class, Rescuable.class };
		testConstructorExists(Class.forName(cannotTreatExceptionPath), inputs);
	}

	@Test()
	public void testConstructorAlreadySafeException2() throws Exception {
		Class[] inputs = { Unit.class, Rescuable.class, String.class };
		testConstructorExists(Class.forName(cannotTreatExceptionPath), inputs);
	}

	@Test()
	public void testConstructorIncompatibleTargetException1() throws Exception {
		Class[] inputs = { Unit.class, Rescuable.class };
		testConstructorExists(Class.forName(incompatibleTargetExceptionPath),
				inputs);
	}

	@Test()
	public void testConstructorIncompatibleTargetException2() throws Exception {
		Class[] inputs = { Unit.class, Rescuable.class, String.class };
		testConstructorExists(Class.forName(incompatibleTargetExceptionPath),
				inputs);
	}

	@Test()
	public void testClassIsAbstractSimulationException() throws Exception {
		testClassIsAbstract(Class.forName(simulationExceptionPath));
	}

	@Test()
	public void testClassIsAbstractDisasterException() throws Exception {
		testClassIsAbstract(Class.forName(disasterExceptionPath));
	}

	@Test()
	public void testClassIsAbstractUnitException() throws Exception {
		testClassIsAbstract(Class.forName(unitExceptionPath));
	}

	@Test()
	public void testClassIsSubclassSimulationException() throws Exception {
		testClassIsSubclass(Class.forName(simulationExceptionPath),
				Exception.class);
	}

	@Test()
	public void testClassIsSubclassDisasterException() throws Exception {
		testClassIsSubclass(Class.forName(disasterExceptionPath),
				Class.forName(simulationExceptionPath));
	}

	@Test()
	public void testClassIsSubclassBuildingAlreadyCollapsedException()
			throws Exception {
		testClassIsSubclass(
				Class.forName(buildingAlreadyCollapsedExceptionPath),
				Class.forName(disasterExceptionPath));
	}

	@Test()
	public void testClassIsSubclassCitizenAlreadyDeadException()
			throws Exception {
		testClassIsSubclass(Class.forName(citizenAlreadyDeadExceptionPath),
				Class.forName(disasterExceptionPath));
	}

	@Test()
	public void testClassIsSubclassUnitException() throws Exception {
		testClassIsSubclass(Class.forName(unitExceptionPath),
				Class.forName(simulationExceptionPath));
	}

	@Test()
	public void testClassIsSubclassAlreadySafeException() throws Exception {
		testClassIsSubclass(Class.forName(cannotTreatExceptionPath),
				Class.forName(unitExceptionPath));
	}

	@Test()
	public void testClassIsSubclassIncompatibleTargetException()
			throws Exception {
		testClassIsSubclass(Class.forName(incompatibleTargetExceptionPath),
				Class.forName(unitExceptionPath));
	}

	@Test(expected = CannotTreatException.class)
	public void testAmbulanceRespondtoSafeCitizenException() throws Exception {
		Simulator s = new Simulator(sos);
		Address ambulanceAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);

		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		Ambulance a = new Ambulance("ambulance1", ambulanceAddress, 4, s);
		a.respond(c);
	}

	@Test(expected = CannotTreatException.class)
	public void testAmbulanceRespondtoSafeCitizenWithInfectionException()
			throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);

		Simulator s = new Simulator(cc);
		Address ambulanceAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);

		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		c.setEmergencyService(cc);
		Infection inf = new Infection(0, c);
		inf.strike();

		Ambulance a = new Ambulance("ambulance1", ambulanceAddress, 4, s);

		a.respond(c);

	}

	private void frameGetter(CommandCenter cc) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		Field[] f = null;
		Class curr = cc.getClass();
		while (f == null) {
			if (curr == Object.class)
				return;
			f = curr.getDeclaredFields();
			for (int i = 0; i < f.length; i++) {
				Class currField = f[i].getType();
				while (Object.class != currField && currField != null) {

					if (currField == JFrame.class) {
						f[i].setAccessible(true);
						JFrame x = (JFrame) f[i].get(cc);
						if (x != null)
							x.dispose();
						// return x;
					}
					currField = currField.getSuperclass();

				}
			}
		}
		// return null;
	}

	@Test()
	public void testAmbulanceRespondtoCorrectCitizenExceptionNotThrown()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address ambulanceAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);

		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		c.setEmergencyService(cc);
		Injury i = new Injury(0, c);
		i.strike();
		c.setHp(20);
		Ambulance a = new Ambulance("ambulance1", ambulanceAddress, 4, s);
		try {
			a.respond(c);
		} catch (Exception e) {
			fail(e.getClass().getSimpleName()
					+ " should not be thrown as Ambulance should respond to and treat the citizen correctly.");
		}
	}

	@Test(expected = IncompatibleTargetException.class)
	public void testAmbulanceRespondtoResidentialBuildingException()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address ambulanceAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		Fire f = new Fire(0, b);
		f.strike();
		Ambulance a = new Ambulance("ambulance1", ambulanceAddress, 4, s);
		a.respond(b);
	}

	@Test(expected = CannotTreatException.class)
	public void testDiseaseControlUnitRespondtoSafeCitizenException()
			throws Exception {
		Simulator s = new Simulator(sos);
		Address diseaseControlUnitAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);

		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		DiseaseControlUnit a = new DiseaseControlUnit("DiseaseControlUnit1",
				diseaseControlUnitAddress, 4, s);
		a.respond(c);
	}

	@Test(expected = CannotTreatException.class)
	public void testDiseaseControlUnitRespondtoSafeCitizenWithInjuryException()
			throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Simulator s = new Simulator(cc);
		Address diseaseControlUnitAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);

		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		c.setEmergencyService(cc);
		DiseaseControlUnit a = new DiseaseControlUnit("DiseaseControlUnit1",
				diseaseControlUnitAddress, 4, s);
		Injury inj = new Injury(0, c);
		inj.strike();
		a.respond(c);
	}

	@Test()
	public void testDiseaseControlUnitRespondtoCorrectCitizenExceptionNotThrown()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address diseaseControlUnitAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);

		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		c.setEmergencyService(cc);
		Infection i = new Infection(0, c);
		i.strike();
		c.setHp(20);
		DiseaseControlUnit a = new DiseaseControlUnit("DiseaseControlUnit1",
				diseaseControlUnitAddress, 4, s);
		try {
			a.respond(c);
		} catch (Exception e) {
			fail(e.getClass().getSimpleName()
					+ " should not be thrown as DiseaseControlUnit should respond to and treat the citizen correctly.");
		}
	}

	@Test(expected = IncompatibleTargetException.class)
	public void testDiseaseControlUnitRespondtoResidentialBuildingException()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address diseaseControlUnitAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		Fire f = new Fire(0, b);
		f.strike();
		DiseaseControlUnit a = new DiseaseControlUnit("DiseaseControlUnit1",
				diseaseControlUnitAddress, 4, s);
		a.respond(b);
	}

	@Test(expected = CannotTreatException.class)
	public void testFireTruckRespondtoSafeResidentialBuildingException()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address fireTruckAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		FireTruck a = new FireTruck("FireTruck1", fireTruckAddress, 4, s);
		a.respond(b);
	}

	@Test()
	public void testFireTruckRespondtoSafeResidentialBuildingWithOtherThanFireDisasterException()
			throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Simulator s = new Simulator(cc);

		Address fireTruckAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);

		FireTruck a = new FireTruck("fireTruck1", fireTruckAddress, 4, s);
		try {
			a.respond(b);
			fail("While there is no disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("While there is no disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of "
					+ e.getClass().getSimpleName());
		}

		Collapse f = new Collapse(0, b);
		f.strike();

		try {
			a.respond(b);
			fail("When FireTruck tries to treat a building while there is a Collapse Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("When FireTruck tries to treat a building while there is a Collapse Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of \""
					+ e.getClass().getSimpleName() + "\"");
		}

		b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		GasLeak g = new GasLeak(0, b);
		g.strike();

		try {
			a.respond(b);
			fail("When FireTruck tries to treat a building while there is a GasLeak Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("When FireTruck tries to treat a building while there is a GasLeak Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of \""
					+ e.getClass().getSimpleName() + "\"");
		}
	}

	@Test(expected = CannotTreatException.class)
	public void testFireTruckRespondtoSafeResidentialBuildingWithException()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address fireTruckAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		FireTruck a = new FireTruck("FireTruck1", fireTruckAddress, 4, s);
		a.respond(b);
	}

	@Test()
	public void testFireTruckRespondtoCorrectResidentialBuildingExceptionNotThrown()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address fireTruckAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setFireDamage(50);
		b.setEmergencyService(cc);
		Fire f = new Fire(0, b);
		f.strike();
		FireTruck a = new FireTruck("FireTruck1", fireTruckAddress, 4, s);
		try {
			a.respond(b);
		} catch (Exception e) {
			fail(e.getClass().getSimpleName()
					+ " should not be thrown as FireTruck should respond to and treat the building correctly.");
		}
	}

	@Test(expected = IncompatibleTargetException.class)
	public void testFireTruckRespondtoCitizenException() throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address fireTruckAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);
		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		c.setEmergencyService(cc);
		Injury i = new Injury(0, c);
		i.strike();
		FireTruck a = new FireTruck("FireTruck1", fireTruckAddress, 4, s);
		a.respond(c);
	}

	@Test(expected = CannotTreatException.class)
	public void testGasControlUnitRespondtoSafeResidentialBuildingException()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address gasControlUnitAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		GasControlUnit a = new GasControlUnit("GasControlUnit1",
				gasControlUnitAddress, 4, s);
		a.respond(b);
	}

	@Test()
	public void testGasControlUnitRespondtoSafeResidentialBuildingWithOtherThanGasLeakDisasterException()
			throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Simulator s = new Simulator(cc);

		Address gasControlUnitAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);

		GasControlUnit a = new GasControlUnit("gasControlUnit1",
				gasControlUnitAddress, 4, s);
		try {
			a.respond(b);
			fail("While there is no disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("While there is no disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of "
					+ e.getClass().getSimpleName());
		}

		Collapse f = new Collapse(0, b);
		f.strike();

		try {
			a.respond(b);
			fail("When GasControlUnit tries to treat a building while there is a Collapse Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("When GasControlUnit tries to treat a building while there is a Collapse Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of \""
					+ e.getClass().getSimpleName() + "\"");
		}

		b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		Fire g = new Fire(0, b);
		g.strike();

		try {
			a.respond(b);
			fail("When GasControlUnit tries to treat a building while there is a Fire Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("When GasControlUnit tries to treat a building while there is a Fire Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of \""
					+ e.getClass().getSimpleName() + "\"");
		}
	}

	@Test()
	public void testGasControlUnitRespondtoCorrectResidentialBuildingExceptionNotThrown()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address gasControlUnitAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setGasLevel(50);
		b.setEmergencyService(cc);
		GasLeak f = new GasLeak(0, b);
		f.strike();
		GasControlUnit a = new GasControlUnit("GasControlUnit1",
				gasControlUnitAddress, 4, s);
		try {
			a.respond(b);
		} catch (Exception e) {
			fail(e.getClass().getSimpleName()
					+ " should not be thrown as GasControlUnit should respond to and treat the building correctly.");
		}
	}

	@Test(expected = IncompatibleTargetException.class)
	public void testGasControlUnitRespondtoCitizenException() throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address gasControlUnitAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);
		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		c.setEmergencyService(cc);
		Injury i = new Injury(0, c);
		i.strike();
		GasControlUnit a = new GasControlUnit("GasControlUnit1",
				gasControlUnitAddress, 4, s);
		a.respond(c);
	}

	@Test(expected = CannotTreatException.class)
	public void testEvacuatorRespondtoSafeResidentialBuildingException()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address evacuatorAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		Evacuator a = new Evacuator("Evacuator1", evacuatorAddress, 4, s, 5);
		a.respond(b);
	}

	@Test()
	public void testEvacuatorRespondtoSafeResidentialBuildingWithOtherThanCollapseDisasterExceptions()
			throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Simulator s = new Simulator(cc);
		Address evacuatorAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);

		Evacuator a = new Evacuator("evacuator1", evacuatorAddress, 5, s, 4);
		try {
			a.respond(b);
			fail("While there is no disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("While there is no disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of "
					+ e.getClass().getSimpleName());
		}

		GasLeak f = new GasLeak(0, b);
		f.strike();

		try {
			a.respond(b);
			fail("When Evacuator tries to treat a building while there is a GasLeak Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("When Evacuator tries to treat a building while there is a GasLeak Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of \""
					+ e.getClass().getSimpleName() + "\"");
		}

		b = new ResidentialBuilding(buildingAddress);
		b.setEmergencyService(cc);
		Fire g = new Fire(0, b);
		g.strike();

		try {
			a.respond(b);
			fail("When Evacuator tries to treat a building while there is a Fire Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown");
		} catch (CannotTreatException e) {

		} catch (Exception e) {
			fail("When Evacuator tries to treat a building while there is a Fire Disaster on the building, "
					+ CannotTreatException.class.getSimpleName()
					+ " should be thrown instead of \""
					+ e.getClass().getSimpleName() + "\"");
		}

	}

	@Test()
	public void testEvacuatorRespondtoCorrectResidentialBuildingExceptionNotThrown()
			throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address evacuatorAddress = getAddressFromWorld(s, 3, 9);
		Address buildingAddress = getAddressFromWorld(s, 3, 4);
		ResidentialBuilding b = new ResidentialBuilding(buildingAddress);
		b.setGasLevel(50);
		b.setEmergencyService(cc);
		Collapse f = new Collapse(0, b);
		f.strike();
		Evacuator a = new Evacuator("Evacuator1", evacuatorAddress, 4, s, 5);
		try {
			a.respond(b);
		} catch (Exception e) {
			fail(e.getClass().getSimpleName()
					+ " should not be thrown as Evacuator should respond to and treat the building correctly.");
		}
	}

	@Test(expected = IncompatibleTargetException.class)
	public void testEvacuatorRespondtoCitizenException() throws Exception {
		Simulator s = new Simulator(sos);
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Address evacuatorAddress = getAddressFromWorld(s, 3, 9);
		Address citizenAddress = getAddressFromWorld(s, 3, 4);
		Citizen c = new Citizen(citizenAddress, "1", "citizen1", 20, s);
		c.setEmergencyService(cc);
		Injury i = new Injury(0, c);
		i.strike();
		Evacuator a = new Evacuator("Evacuator1", evacuatorAddress, 4, s, 5);
		a.respond(c);
	}

	// Disasters exceptions

	@Test(expected = CitizenAlreadyDeadException.class)
	public void testStrikeInjuryThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Simulator s = new Simulator(sos);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		String String7 = "izw";
		Citizen citizen0 = new Citizen(address1, String7, String7, int9, s);
		citizen0.setEmergencyService(cc);
		citizen0.setState(CitizenState.DECEASED);
		Injury injury2 = new Injury(0, citizen0);
		injury2.strike();
	}

	@Test()
	public void testStrikeInjuryNotThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Simulator s = new Simulator(sos);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		String String7 = "izw";
		Citizen citizen0 = new Citizen(address1, String7, String7, int9, s);
		citizen0.setEmergencyService(cc);
		Injury injury2 = new Injury(0, citizen0);
		try {
			injury2.strike();
		} catch (Exception e) {
			fail("Exception \""
					+ e.getClass().getSimpleName()
					+ "\" should not be thrown by class \"Injury\" as the citizen targeted by injury should be normally struck");
		}
	}

	@Test(expected = CitizenAlreadyDeadException.class)
	public void testStrikeInfectionThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Simulator s = new Simulator(sos);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		String String7 = "izw";
		Citizen citizen0 = new Citizen(address1, String7, String7, int9, s);
		citizen0.setEmergencyService(cc);
		citizen0.setState(CitizenState.DECEASED);
		Infection infection2 = new Infection(0, citizen0);
		infection2.strike();
	}

	@Test()
	public void testStrikeInfectionNotThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		Simulator s = new Simulator(sos);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		String String7 = "izw";
		Citizen citizen0 = new Citizen(address1, String7, String7, int9, s);
		citizen0.setEmergencyService(cc);
		Infection infection2 = new Infection(0, citizen0);
		try {
			infection2.strike();
		} catch (Exception e) {
			fail("Exception \""
					+ e.getClass().getSimpleName()
					+ "\" should not be thrown by class \"Infection\" as the citizen targeted by infection should be normally struck");
		}
	}

	@Test(expected = BuildingAlreadyCollapsedException.class)
	public void testStrikeCollapseThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		ResidentialBuilding b = new ResidentialBuilding(address1);
		b.setEmergencyService(cc);
		b.setStructuralIntegrity(0);
		Collapse collapse2 = new Collapse(0, b);
		collapse2.strike();
	}

	@Test()
	public void testStrikeCollapseNotThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		ResidentialBuilding b = new ResidentialBuilding(address1);
		b.setEmergencyService(cc);
		Collapse collapse2 = new Collapse(0, b);
		try {
			collapse2.strike();
		} catch (Exception e) {
			fail("Exception \""
					+ e.getClass().getSimpleName()
					+ "\" should not be thrown by class \"Collapse\" as the building targeted by collapse should be normally struck");
		}
	}

	@Test(expected = BuildingAlreadyCollapsedException.class)
	public void testStrikeFireThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		ResidentialBuilding b = new ResidentialBuilding(address1);
		b.setEmergencyService(cc);
		b.setStructuralIntegrity(0);
		Fire fire2 = new Fire(0, b);
		fire2.strike();
	}

	@Test()
	public void testStrikeFireNotThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		ResidentialBuilding b = new ResidentialBuilding(address1);
		b.setEmergencyService(cc);
		Fire fire2 = new Fire(0, b);
		try {
			fire2.strike();
		} catch (Exception e) {
			fail("Exception \""
					+ e.getClass().getSimpleName()
					+ "\" should not be thrown by class \"Fire\" as the building targeted by fire should be normally struck");
		}
	}

	@Test(expected = BuildingAlreadyCollapsedException.class)
	public void testStrikeGasLeakThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		ResidentialBuilding b = new ResidentialBuilding(address1);
		b.setEmergencyService(cc);
		b.setStructuralIntegrity(0);
		GasLeak gasLeak2 = new GasLeak(0, b);
		gasLeak2.strike();
	}

	@Test()
	public void testStrikeGasLeakNotThrown() throws Exception {
		CommandCenter cc = new CommandCenter();
		frameGetter(cc);
		int int0 = 0;
		int int9 = 9;
		Address address1 = new Address(int0, int9);
		ResidentialBuilding b = new ResidentialBuilding(address1);
		b.setEmergencyService(cc);
		GasLeak gasLeak2 = new GasLeak(0, b);
		try {
			gasLeak2.strike();
		} catch (Exception e) {
			fail("Exception \""
					+ e.getClass().getSimpleName()
					+ "\" should not be thrown by class \"GasLeak\" as the building targeted by gasLeak should be normally struck");
		}
	}

	// ////////////////////////////////////////////////////////////////////
	private static Address getAddressFromWorld(Simulator s, int x, int y)
			throws IllegalArgumentException, IllegalAccessException {

		Field f = null;
		Class curr = s.getClass();
		while (f == null) {
			if (curr == Object.class)
				fail("Class " + s.getClass().getSimpleName()
						+ " should have the instance variable \"" + "world"
						+ "\".");
			try {
				f = curr.getDeclaredField("world");
			} catch (NoSuchFieldException e) {
				curr = curr.getSuperclass();
			}
		}
		f.setAccessible(true);
		Address myWorld[][] = (Address[][]) f.get(s);
		return myWorld[x][y];
	}

	private void testClassIsAbstract(Class aClass) {
		assertTrue("You should not be able to create new instances from "
				+ aClass.getSimpleName() + " class.",
				Modifier.isAbstract(aClass.getModifiers()));
	}

	private void testClassIsSubclass(Class subClass, Class superClass) {
		assertTrue(
				subClass.getSimpleName() + " class should be a subclass from "
						+ superClass.getSimpleName() + ".",
				superClass == subClass.getSuperclass());
	}

	private void testConstructorExists(Class aClass, Class[] inputs) {
		boolean thrown = false;
		try {
			aClass.getConstructor(inputs);
		} catch (NoSuchMethodException e) {
			thrown = true;
		}
		if (inputs.length > 0) {
			String msg = "";
			int i = 0;
			do {
				msg += inputs[i].getSimpleName() + " and ";
				i++;
			} while (i < inputs.length);
			msg = msg.substring(0, msg.length() - 4);
			assertFalse(
					"Missing constructor with " + msg + " parameter"
							+ (inputs.length > 1 ? "s" : "") + " in "
							+ aClass.getSimpleName() + " class.", thrown);
		} else
			assertFalse(
					"Missing constructor with zero parameters in "
							+ aClass.getSimpleName() + " class.", thrown);
	}

}
