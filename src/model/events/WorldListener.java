package model.events;

import simulation.Simulatable;

public interface WorldListener {
	public void assignAddress(Simulatable s, int x , int y);
}
