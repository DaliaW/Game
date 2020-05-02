package model.events;

import simulation.Rescuable;

public interface SOSListener {
public void receiveSOSCall(Rescuable r);
}
