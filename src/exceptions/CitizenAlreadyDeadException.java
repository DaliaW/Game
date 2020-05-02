package exceptions;

import model.disasters.Disaster;

public class CitizenAlreadyDeadException extends DisasterException{
	public CitizenAlreadyDeadException(Disaster disaster) {
		super(disaster);
	}
	public CitizenAlreadyDeadException(Disaster disaster,String message) {
		super(disaster,message);
	}
	

}
