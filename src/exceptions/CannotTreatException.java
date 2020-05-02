package exceptions;

import model.units.Unit;
import simulation.Rescuable;

public class CannotTreatException extends UnitException{
	public CannotTreatException(Unit unit, Rescuable target) {
		super(unit , target);
	}
	public CannotTreatException(Unit unit, Rescuable target, String message){
		super(unit , target ,message);
	}

}
