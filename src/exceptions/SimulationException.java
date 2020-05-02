package exceptions;

public abstract class SimulationException extends Exception{
	public SimulationException() {
		super();
	}
	public SimulationException(String message) {
		super(message);
	}

}
