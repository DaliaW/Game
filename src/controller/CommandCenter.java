package controller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.nio.channels.AlreadyBoundException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import View.GameView;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.DisasterException;
import exceptions.IncompatibleTargetException;
import exceptions.SimulationException;
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
import model.units.UnitState;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;
import simulation.Simulator;

public class CommandCenter implements SOSListener, ActionListener {

	private GameView view;
	Simulator engine;
	ArrayList<ResidentialBuilding> visibleBuildings;
	ArrayList<Citizen> visibleCitizens;
	ArrayList<Unit> emergencyUnits;
	
	private Rescuable toRescue = null;
	private int currentCycle;
	private JTextArea currentCycleLabel;
	private JTextArea causalities;
	private JButton[][] worldBtns;
	private ArrayList<Simulatable>[][] btnContents;
	private JTextArea info;
	private JTextArea struckDisasters; //log
	private JTextArea activeDisasters;
	//bonus
	private ArrayList<Disaster> dataBase;
	public CommandCenter() throws Exception {
		view = new GameView();
		btnContents = new ArrayList[10][10];
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<10;j++)
			{
				btnContents[i][j]=new ArrayList<>();
			}
		}
		engine = new Simulator(this);
		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		emergencyUnits = engine.getEmergencyUnits();
		worldBtns = new JButton[10][10];
		dataBase = new ArrayList<Disaster>();
		constructTheView();
	}

	
	public void constructTheView() {

		for (int i = 0; i < 10; i++) 
		{
			for (int j = 0; j < 10; j++) 
			{
				JButton location = new JButton();
				worldBtns[i][j] = location;
				if (i == 0 && j == 0) 
				{
				//	location.setIcon(new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Base.png"));
				}
				location.setSize(location.getPreferredSize().width,location.getPreferredSize().height);
				location.setActionCommand(i + " " + j);
				location.addActionListener(this);
				view.getPnlCity().add(location);
				refresh(view.getPnlCity());
				location.putClientProperty("i", i);
				location.putClientProperty("j", j);
				location.putClientProperty("type", "Rescuable");
			}
		}

		constructTheUnits();

		constructTheText();
		
		constructTheControls();
	}

	@Override
	public void receiveSOSCall(Rescuable r) {

		if (r instanceof ResidentialBuilding) {

			if (!visibleBuildings.contains(r))
				visibleBuildings.add((ResidentialBuilding) r);

		} else {

			if (!visibleCitizens.contains(r))
				visibleCitizens.add((Citizen) r);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		String action = e.getActionCommand();
		if (action == null) 
		{
			JOptionPane.showMessageDialog(view, "you must choose anything else");
		}
		else 
		{
			if (action.equals("nextCycle")) // next cycle proceeding
			{
				if(engine.checkGameOver())
                { 
					if(engine.lost()) 
					{
						JLabel jl=new JLabel("Game Over all dead.\n"+"     "+"The causalities: "+engine.calculateCasualties());
						jl.setPreferredSize(new Dimension(400,400));
						view.getPnlCity().removeAll();
						refresh(view.getPnlCity());
						view.getPnlCity().add(jl,BorderLayout.CENTER);
						view.getControls().removeAll();
						refresh(view.getControls());
					}
					else 
					{
						JLabel jl=new JLabel("You Won! \n"+ "      "+"The causalities: "+engine.calculateCasualties());
						jl.setPreferredSize(new Dimension(400,400));
						view.getPnlCity().removeAll();
						refresh(view.getPnlCity());
						view.getPnlCity().add(jl,BorderLayout.CENTER);
						view.getControls().removeAll();
						refresh(view.getControls());
					}
                }
                else
                {
                	engine.nextCycle();
                	fillTheArrayLists();
                	resetDataBase();
                	updateDataBase();
                	resetTheUnits(); 
                	constructTheUnits();
                	currentCycleLabel.setText("                     " + ++currentCycle);
                	causalities.setText("                     " + engine.calculateCasualties());
                	putTheIcons();
                	resetTheText();
                	constructTheText();
                	resetTheView();
                }
			}
			else 
			{
				if(action.equals("recommend"))
				{
					recommend();
					//treating();
				}
			else
			{
				if (source.getClientProperty("type").equals("Rescuable")) 
				{
					Rescuable r = (Rescuable) source.getClientProperty("instance");
					toRescue = r;
					try
					{
						if(action.equals("0 0"))//base
						{
							String res="";
							res+= "The base contains: ";
							for(Unit u:emergencyUnits)
							{
								if(u.getState() == UnitState.IDLE && u.getLocation().getX()==0 && u.getLocation().getY()==0)
								{
									res+=u+"\n";
								}
							}
							info.setText(res);
						}
						else
						{
							int x=r.getLocation().getX();
							int y=r.getLocation().getY();
							ArrayList<Simulatable>content=btnContents[x][y];
							String res="";
							boolean containUnit=false;
							Unit u=null;
							for(Simulatable sim:content)
							{
								if(sim instanceof ResidentialBuilding)
								{
									res+="The cell contains the building\n"+((ResidentialBuilding)sim).toString()+"\n";
									if(((ResidentialBuilding)sim).getDisaster()!=null)
									{
										res+="The building is affected by the disaster "+((ResidentialBuilding)sim).getDisaster().toString()+"\n";
									}
								}
								if(sim instanceof Citizen)
								{
									res+="The cell contains the citizen\n"+((Citizen)sim).toString()+"\n";
									if(((Citizen)sim).getDisaster()!=null)
									{
										res+="The citizen is affected by the disaster "+((Citizen)sim).getDisaster().toString()+"\n";
									}
								}					
								if(sim instanceof Unit)
								{
									u = (Unit)sim;
									containUnit=true;
								}
							}
							if(containUnit)
							{
								res+="The cell contains the unit\n"+((Unit)u).toString()+"\n";
							}
							info.setText(res);
						}	
					}
					catch(Exception exc)
					{
						info.setText("Empty cell");
					}
				}
				else 
				{
					if(source.getClientProperty("type").equals("Unit") && toRescue !=null) 
					{
						Unit u = (Unit) source.getClientProperty("instance");
						try 
						{
							u.respond(toRescue);
						}
						catch (IncompatibleTargetException e1) 
						{

							Disaster d = toRescue.getDisaster();
							if(toRescue instanceof Citizen) 
							{
								
								if(d instanceof Injury) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: Ambulance ");
								}
								if(d instanceof Infection) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: Disease control unit");
								}
							}
							else 
							{
								if(d instanceof GasLeak) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: Gas control unit");
								}
								if(d instanceof Collapse) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: evacuator");
								}
								if(d instanceof Fire) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: Fire Truck");
								}
							}
						}
						catch (CannotTreatException e1) 
						{
							Disaster d = toRescue.getDisaster();
							if(toRescue instanceof Citizen) 
							{
								
								if(d instanceof Injury) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: Ambulance ");
								}
								if(d instanceof Infection) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: Disease control unit");
								}
							}
							else 
							{
								if(d instanceof GasLeak) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: Gas control unit");
								}
								if(d instanceof Collapse) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: evacuator");
								}
								if(d instanceof Fire) 
								{
									JOptionPane.showMessageDialog(view, "Cannot be treated. Recommended: Fire Truck");
								}
							}
						}
						finally 
						{
							toRescue=null;
							System.out.println("Process completed");
						}
					}
					else 
					{
						if(source.getClientProperty("type").equals("Unit")) 
						{
							Unit u = (Unit) source.getClientProperty("instance");
							info.setText(u.toString());
						}		
					}
				}
			}
		}
	}		
}
	
	public void updateDataBase() {
		for(ResidentialBuilding b:visibleBuildings)
		{
			Disaster d=b.getDisaster();
			if(d.isActive())
				dataBase.add(d);
		}
		for(Citizen c:visibleCitizens)
		{
			Disaster d=c.getDisaster();
			if(d.isActive())
				dataBase.add(d);
		}
	}
	public void resetDataBase() {
		dataBase = new ArrayList<>();
	}
	public void fillTheArrayLists() {
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<10;j++)
			{
				btnContents[i][j]=new ArrayList<>();
			}
		}
		for(ResidentialBuilding b:visibleBuildings)
		{
			int x=b.getLocation().getX();
			int y=b.getLocation().getY();
			btnContents[x][y].add(b);
		}
		for(Citizen c:visibleCitizens)
		{
			int x=c.getLocation().getX();
			int y=c.getLocation().getY();
			btnContents[x][y].add(c);
			
		}
		for(Unit u:emergencyUnits)
		{
			int x=u.getLocation().getX();
			int y=u.getLocation().getY();
			btnContents[x][y].add(u);
		}
	}

	public void resetTheView() {

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (worldBtns[i][j].getClientProperty("instance") instanceof Citizen) {
					if (((Citizen) worldBtns[i][j].getClientProperty("instance")).getDisaster().isActive() == false) {
						worldBtns[i][j].repaint();
					}
				} else {
					if (worldBtns[i][j].getClientProperty("instance") instanceof ResidentialBuilding) {
						if (((ResidentialBuilding) worldBtns[i][j].getClientProperty("instance")).getDisaster()
								.isActive() == false) {
							worldBtns[i][j].repaint();
						}
					}
				}

			}
		}
	}
	public void putTheIcons() {
		for (ResidentialBuilding b : visibleBuildings) 
    	{
    		int x = b.getLocation().getX();
    		int y = b.getLocation().getY();
    		Disaster d=b.getDisaster();
    		if(b.getStructuralIntegrity()==0)
    		{
    			ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Collapsed.png");
				icon = resizeIcon(icon,worldBtns[x][y].getWidth(),worldBtns[x][y].getHeight());
				worldBtns[x][y].setIcon(icon);//fire icon
    		}
    		else
    		{
    			if(d instanceof Fire)
    			{
    				ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Fire.png");
    				icon = resizeIcon(icon,worldBtns[x][y].getWidth(),worldBtns[x][y].getHeight());
    				worldBtns[x][y].setIcon(icon);//fire icon
    			}
    			if(d instanceof Collapse)
    			{
    				ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Collapse.png");
    				icon = resizeIcon(icon,worldBtns[x][y].getWidth(),worldBtns[x][y].getHeight());
    				worldBtns[x][y].setIcon(icon);//fire icon
    			}
    			if(d instanceof GasLeak)
    			{
    				ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\GasLeak.png");
    				icon = resizeIcon(icon,worldBtns[x][y].getWidth(),worldBtns[x][y].getHeight());
    				worldBtns[x][y].setIcon(icon);//fire icon
    			}
    			worldBtns[x][y].putClientProperty("instance", b);
    		}
    	}
    	for (Citizen c : visibleCitizens) 
    	{
    		int x = c.getLocation().getX();
    		int y = c.getLocation().getY();
    		Disaster d=c.getDisaster();
    		worldBtns[x][y].setText(c.getDisaster().getClass().getSimpleName() );
    		if(c.getState()==CitizenState.DECEASED)
    		{
    			ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Deceased.png");
    			icon = resizeIcon(icon,worldBtns[x][y].getWidth(),worldBtns[x][y].getHeight());
    			worldBtns[x][y].setIcon(icon);	
    		}
    		else 
			{
				if(d instanceof Injury)
				{
					ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Injury.png");
        			icon = resizeIcon(icon,worldBtns[x][y].getWidth(),worldBtns[x][y].getHeight());
        			worldBtns[x][y].setIcon(icon);//injury icon
				}
				if(d instanceof Infection)
				{
					ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Infection.png");
        			icon = resizeIcon(icon,worldBtns[x][y].getWidth(),worldBtns[x][y].getHeight());
					worldBtns[x][y].setIcon(icon);//infection icon
				}
			}
    		worldBtns[x][y].putClientProperty("instance", c);
    	}
	}
	public void resetTheUnits() {
		view.getPnlAvUnits().removeAll();
		view.getPnlResUnits().removeAll();
		view.getPnlTrUnits().removeAll();
		refresh(view.getPnlAvUnits());
		refresh(view.getPnlResUnits());
		refresh(view.getPnlTrUnits());
	}
	public void resetTheText() {
		view.getText().removeAll();
		refresh(view.getText());
	}
	public void constructTheUnits() {
		for (Unit u : emergencyUnits) 
		{
			if (u instanceof Evacuator) 
			{
				JButton btnEvacuator = new JButton();
				btnEvacuator.setPreferredSize(new Dimension(50,50));
				btnEvacuator.setSize(50,50);
				ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Evacuator.png");
    			icon = resizeIcon(icon,btnEvacuator.getWidth(),btnEvacuator.getHeight());
				btnEvacuator.setIcon(icon);
				btnEvacuator.setActionCommand("Evac");
				btnEvacuator.addActionListener(this);
				btnEvacuator.putClientProperty("type", "Unit");
				btnEvacuator.putClientProperty("subtype", "Evac");
				btnEvacuator.putClientProperty("instance", u);

				if (u.getState() == UnitState.IDLE) 
				{
					view.getPnlAvUnits().add(btnEvacuator);
				} 
				else 
				{
					if (u.getState() == UnitState.RESPONDING) 
					{
						view.getPnlResUnits().add(btnEvacuator);
					} 
					else 
					{
						view.getPnlTrUnits().add(btnEvacuator);
					}
				}
			}
			if (u instanceof Ambulance) 
			{
				JButton btnAmbulance = new JButton();
				btnAmbulance.setPreferredSize(new Dimension(50, 50));
				btnAmbulance.setSize(50,50);
				ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\Ambulance.png");
    			icon = resizeIcon(icon,btnAmbulance.getWidth(),btnAmbulance.getHeight());
				btnAmbulance.setIcon(icon);
				btnAmbulance.setActionCommand("Amb");
				btnAmbulance.addActionListener(this);
				btnAmbulance.putClientProperty("type", "Unit");
				btnAmbulance.putClientProperty("subtype", "Amb");
				btnAmbulance.putClientProperty("instance", u);

				if (u.getState() == UnitState.IDLE) 
				{
					view.getPnlAvUnits().add(btnAmbulance);
				} 
				else 
				{
					if (u.getState() == UnitState.RESPONDING) 
					{
						view.getPnlResUnits().add(btnAmbulance);
					} 
					else 
					{
						view.getPnlTrUnits().add(btnAmbulance);
					}
				}
			}
			if (u instanceof FireTruck) 
			{
				JButton btnFireTruck = new JButton();
				btnFireTruck.setPreferredSize(new Dimension(50,50));
				btnFireTruck.setSize(50,50);
				ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\FireTruck.png");
    			icon = resizeIcon(icon,btnFireTruck.getWidth(),btnFireTruck.getHeight());
				btnFireTruck.setIcon(icon);
				btnFireTruck.setActionCommand("FT");
				btnFireTruck.addActionListener(this);
				btnFireTruck.putClientProperty("type", "Unit");
				btnFireTruck.putClientProperty("subtype", "FT");
				btnFireTruck.putClientProperty("instance", u);

				if (u.getState() == UnitState.IDLE) 
				{
					view.getPnlAvUnits().add(btnFireTruck);
				} 
				else 
				{
					if (u.getState() == UnitState.RESPONDING) 
					{
						view.getPnlResUnits().add(btnFireTruck);
					}
					else 
					{
						view.getPnlTrUnits().add(btnFireTruck);
					}
				}
			}
			if (u instanceof DiseaseControlUnit) 
			{
				JButton btnDCU = new JButton();
				btnDCU.setPreferredSize(new Dimension(50,50));
				btnDCU.setSize(50,50);
				ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\DCU.png");
    			icon = resizeIcon(icon,btnDCU.getWidth(),btnDCU.getHeight());
				btnDCU.setIcon(icon);
				btnDCU.setActionCommand("DCU");
				btnDCU.addActionListener(this); 
				btnDCU.putClientProperty("type", "Unit");
				btnDCU.putClientProperty("subtype", "DCU");
				btnDCU.putClientProperty("instance", u);

				if (u.getState() == UnitState.IDLE) 
				{
					view.getPnlAvUnits().add(btnDCU);
				}
				else 
				{
					if (u.getState() == UnitState.RESPONDING) 
					{
						view.getPnlResUnits().add(btnDCU);
					} 
					else 
					{
						view.getPnlTrUnits().add(btnDCU);
					}
				}
			}
			if (u instanceof GasControlUnit) 
			{
				JButton btnGCU = new JButton();
				btnGCU.setPreferredSize(new Dimension(50,50));
				btnGCU.setSize(50,50);
				ImageIcon icon = new ImageIcon("D:\\A GUCian\\H S4\\CS\\Our Game\\Icons\\GCU.png");
    			icon = resizeIcon(icon,btnGCU.getWidth(),btnGCU.getHeight());
				btnGCU.setIcon(icon);
				btnGCU.setActionCommand("GCU");
				btnGCU.addActionListener(this);
				btnGCU.putClientProperty("type", "Unit");
				btnGCU.putClientProperty("subtype", "GCU");
				btnGCU.putClientProperty("instance", u);

				if (u.getState() == UnitState.IDLE) 
				{
					view.getPnlAvUnits().add(btnGCU);
				}
				else 
				{
					if (u.getState() == UnitState.RESPONDING) 
					{
						view.getPnlResUnits().add(btnGCU);
					}
					else 
					{
						view.getPnlTrUnits().add(btnGCU);
					}
				}
			}
		}
	}
	public void constructTheText() {
		// information
		info = new JTextArea();
		TitledBorder infoTitle = BorderFactory.createTitledBorder("Information");
		infoTitle.setTitleFont(new Font(Font.MONOSPACED, Font.ITALIC, 15));
		info.setBorder(infoTitle);
		info.setPreferredSize(new Dimension(1500,1500));
		info.setEditable(false);
		info.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15)); //
		
		JScrollPane jsp1 = new JScrollPane(info);
		jsp1.setPreferredSize(new Dimension(400, 150));
		view.getText().add(jsp1);
		refresh(view.getText());
		
		//log
		struckDisasters = new JTextArea();
		TitledBorder struckDisastersTitle = BorderFactory.createTitledBorder("Log");
		struckDisastersTitle.setTitleFont(new Font(Font.MONOSPACED, Font.ITALIC, 15));
		struckDisasters.setBorder(struckDisastersTitle);
		struckDisasters.setPreferredSize(new Dimension(1500,1500));
		struckDisasters.setEditable(false);
		struckDisasters.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15)); //
		String res ="";
		for(Citizen c:visibleCitizens)
		{
			Disaster d = c.getDisaster();
			if(d.getStartCycle()==currentCycle)
			{
				res+=d+"\n";
			}
		}
		for(ResidentialBuilding b:visibleBuildings)
		{
			Disaster d = b.getDisaster();
			if(d.getStartCycle()==currentCycle)
			{
				res+=d+"\n";
			}
		}
		int counter1=0;
		res+= "The people(s) dead in this cycle: \n";
		for(Citizen c:visibleCitizens)
		{
			if(c.isNewlyDead()&&c.getState()==CitizenState.DECEASED)
			{
				c.setNewlyDead(false);
				counter1++;
				res+=c+"\n";
			}
		}
		res+="There are "+ counter1+" dead people(s) \n";
		int counter2 = 0;
		res+= "The building(s) collapsed in this cycle: \n";
		for(ResidentialBuilding b:visibleBuildings)
		{
			if(b.isNewlyCollapsed()&&b.getStructuralIntegrity()==0)
			{
				b.setNewlyCollapsed(false);
				counter2++;
				res+=b+"\n";
			}
		}
		res+="There are "+ counter2+" buildings collapsed";
		struckDisasters.setText(res);
	
		JScrollPane jsp2 = new JScrollPane(struckDisasters);
		jsp2.setPreferredSize(new Dimension(400, 150));
		view.getText().add(jsp2);
		refresh(view.getText());
		
		//active disasters
		activeDisasters = new JTextArea();
		TitledBorder activeDisastersTitle = BorderFactory.createTitledBorder("Active Disasters");
		activeDisastersTitle.setTitleFont(new Font(Font.MONOSPACED, Font.ITALIC, 15));
		activeDisasters.setBorder(activeDisastersTitle);
		activeDisasters.setPreferredSize(new Dimension(1500,1500));
		activeDisasters.setEditable(false);
		activeDisasters.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		String res2="";
		int counter3=0;
		res2+="The active disasters are\n";
		for(ResidentialBuilding b:visibleBuildings)
		{
			Disaster d=b.getDisaster();
			if(d.isActive())
			{
				res2+=d+"\n";
				counter3++;
			}
		}
		for(Citizen c:visibleCitizens)
		{
			Disaster d=c.getDisaster();
			if(d.isActive())
			{
				res2+=d+"\n";
				counter3++;
			}
		}
		res2+="There are "+counter3+" active disasters";
		activeDisasters.setText(res2);

		JScrollPane jsp3 = new JScrollPane(activeDisasters);
		jsp3.setPreferredSize(new Dimension(400, 150));
		view.getText().add(jsp3);
		refresh(view.getText());
	}
	public void constructTheControls() {
		JButton nextCycle = new JButton("Next Cycle");
		nextCycle.addActionListener(this);
		nextCycle.setActionCommand("nextCycle");
		nextCycle.setPreferredSize(new Dimension(150, 42));
		view.getControls().add(nextCycle);

		currentCycleLabel = new JTextArea("                     " + currentCycle);
		currentCycleLabel.setPreferredSize(new Dimension(150, 42));
		currentCycleLabel.setEditable(false);
		TitledBorder currentCycleTitle = BorderFactory.createTitledBorder("Current Cycle");
		currentCycleTitle.setTitleFont(new Font(Font.MONOSPACED, Font.ITALIC, 15));
		currentCycleLabel.setBorder(currentCycleTitle);
		view.getControls().add(currentCycleLabel);

		causalities = new JTextArea("                     " + engine.calculateCasualties());
		causalities.setPreferredSize(new Dimension(150, 42));
		causalities.setEditable(false);
		TitledBorder causalitiesTitle = BorderFactory.createTitledBorder("Causalities");
		causalitiesTitle.setTitleFont(new Font(Font.MONOSPACED, Font.ITALIC, 15));
		causalities.setBorder(causalitiesTitle);
		view.getControls().add(causalities);
		
		JButton recommend = new JButton("Recommend");
		recommend.addActionListener(this);
		recommend.setActionCommand("recommend");
		recommend.setPreferredSize(new Dimension(150, 42));
		view.getControls().add(recommend);
		
		refresh(view.getControls());
	}
	public void recommend() {
		boolean recommended =false;
		for(Disaster d:dataBase)
		{
			Rescuable r =d.getTarget();
			if(r instanceof Citizen)
			{
				if(r.getUnit()==null) // has no unit assigned
				{
					for(Unit u : emergencyUnits)
					{
						if((u instanceof DiseaseControlUnit && d instanceof Infection) || (u instanceof Ambulance && d instanceof Injury))
						{
							if(u.getState() == UnitState.IDLE)
							{
								try 
								{
									if(Citizen.proceedTheSim((Citizen)r,u))
									{
										JOptionPane.showMessageDialog(view, "Treat the citizen @ "+r.getLocation().getX()
																	+" , "+r.getLocation().getY()+"\nby "+u);
										recommended=true;
										break;
									}
								} 
								catch (Exception e1) 
								{
									System.out.println("Error in recommendation");
								}
							}
						}
					}
				}
			}
			else
			{
				if(r.getUnit()==null)
				{
					for(Unit u : emergencyUnits)
					{
						if((u instanceof GasControlUnit && d instanceof GasLeak) || ( u instanceof FireTruck && d instanceof Fire) || (u instanceof Evacuator && d instanceof Collapse))
						{
							if(u.getState() == UnitState.IDLE)
							{
								try 
								{
									if(ResidentialBuilding.proceedTheSim((ResidentialBuilding)r,u))
									{
										JOptionPane.showMessageDialog(view, "Treat the building @ "+r.getLocation().getX()
																	+" , "+r.getLocation().getY()+"\nby "+u);
										recommended=true;
										break;
									}
								} 
								catch (Exception e1) 
								{
									//System.out.println(r.getDisaster());
									System.out.println("Error in recommendation building");
									//e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		if(!recommended)
		{
			JOptionPane.showMessageDialog(view, "Nothing to recommend.");
		}
	}
	public void refresh(Component component) {
		component.revalidate();
		component.repaint();
	}
	public ArrayList<Simulatable>[][] getBtnContents() {
		return btnContents;
	}

	private static ImageIcon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
	    Image img = icon.getImage();  
	    Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
	    return new ImageIcon(resizedImage);
	}
	
//	public void testing() {
//		System.out.print("started");
//		int i =0;
//		for (Unit u: emergencyUnits) {
//			if(u.getState()==UnitState.IDLE) {
//				System.out.print("unit"+ i++);
//				if(u instanceof Evacuator) {
//					for(ResidentialBuilding b: visibleBuildings) {
//						if(b.getStructuralIntegrity()/10> (b.getLocation().getX()+b.getLocation().getY())/u.getStepsPerCycle() && b.getDisaster() instanceof Collapse) {
//							
//							JOptionPane.showMessageDialog(view, "Treat the building @ "+b.getLocation().getX()
//									+" , "+b.getLocation().getY()+"\nby "+u);
//		
//							break;	
//						}
//					}
//				}
//				
//				if (u instanceof FireTruck) {
//					System.out.println("Fire truck");
//					int j=0;
//					for(ResidentialBuilding b: visibleBuildings) {
//						System.out.print("bulding" + j++);
//						testStr = b.getStructuralIntegrity();
//						System.out.println("initial testStr" + testStr);
//						testDist = b.getLocation().getX()+b.getLocation().getX();
//						if(b.getDisaster() instanceof Fire) {
//							System.out.println("FIRE");
//						testBld = b.getFireDamage();	
//						while(testDist>0) {
//							testBld+=10;
//							System.out.println("testStr" + testStr + "Dist " + testDist);
//							testDist-=u.getStepsPerCycle();
//							if(testBld>0 &&testBld<30)
//								testStr-=3;
//							else if(testBld>=30 &&testBld<70)
//								testStr-=5;
//							else if(testBld>=70)
//								testStr-=7;
//							
//						}
//						System.out.println(testStr);
//
//					while(testBld>0 && testBld<100) {
//					//		for(int k=0;k<21;k++) {
//							System.out.println("testStr" + testStr);
//							testBld-=10;
//							if(testBld>0 &&testBld<30)
//								testStr-=3;
//							else if(testBld>=30 &&testBld<70)
//								testStr-=5;
//							else if(testBld>=70)
//								testStr-=7;
//						}
//						System.out.println(testStr);
//
//						if(testStr>0) {
//							JOptionPane.showMessageDialog(view, "Treat the building @ "+b.getLocation().getX()
//									+" , "+b.getLocation().getY()+"\nby "+u);
//						}
//					
//					}
//					}
//				}
//				if (u instanceof GasControlUnit) {
//					//j=0;
//					for(ResidentialBuilding b: visibleBuildings) {
//					//	System.out.print("bulding" + j++);
//						testStr = b.getStructuralIntegrity();
//						System.out.println("initial testStr" + testStr);
//						testDist = b.getLocation().getX()+b.getLocation().getX();
//						if(b.getDisaster() instanceof GasLeak) {
//							System.out.println("FIRE");
//						testBld = b.getGasLevel();	
//						while(testDist>0) {
//							System.out.println("testStr" + testStr + "Dist " + testDist);
//							testDist-=u.getStepsPerCycle();
//							testBld+=15;
//							
//						}
//						System.out.println(testStr);
//
//					while(testBld>0 && testBld<100) {
//					//		for(int k=0;k<21;k++) {
//							System.out.println("testStr" + testStr);
//							testBld-=15;
//						}
//						System.out.println(testStr);
//
//						if(testBld<100) {
//							JOptionPane.showMessageDialog(view, "Treat the building @ "+b.getLocation().getX()
//									+" , "+b.getLocation().getY()+"\nby "+u);
//						}
//					
//					}
//					}
//				}
//				if(u instanceof Ambulance) {
//					//int j=0;
//					for(Citizen c: visibleCitizens) {
//					//	System.out.print("bulding" + j++);
//						testHp = c.getHp();
//						//System.out.println("initial testStr" + testStr);
//						testDist = c.getLocation().getX()+c.getLocation().getX();
//						if(c.getDisaster() instanceof Injury) {
//							System.out.println("BLOOD");
//						testCit = c.getBloodLoss();	
//						while(testDist>0) {
//							testBld+=10;
//						//	System.out.println("testStr" + testHp + "Dist " + testDist);
//							testDist-=u.getStepsPerCycle();
//							if(testCit>0 && testCit<30)
//								testHp-=5;
//							else if(testCit>=30 && testCit<70)
//								testHp-=10;
//							else if(testCit >=70)
//								testHp-=15;
//							
//						}
//						//System.out.println(testHp);
//
//					while(testCit>0 && testCit<100) {
//					//		for(int k=0;k<21;k++) {
//							System.out.println("testHp" + testHp);
//							testCit-=10;
//							if(testCit>0 && testCit<30)
//								testHp-=5;
//							else if(testCit>=30 && testCit<70)
//								testHp-=10;
//							else if(testCit >=70)
//								testHp-=15;
//						}
//						System.out.println(testHp);
//
//						if(testHp>0) {
//							JOptionPane.showMessageDialog(view, "Treat the citizen @ "+c.getLocation().getX()
//									+" , "+c.getLocation().getY()+"\nby "+u);
//						}
//					
//					}
//					}
//					
//				}
//				if(u instanceof DiseaseControlUnit) {
//					//int j=0;
//					for(Citizen c: visibleCitizens) {
//					//	System.out.print("bulding" + j++);
//						testHp = c.getHp();
//						//System.out.println("initial testStr" + testStr);
//						testDist = c.getLocation().getX()+c.getLocation().getX();
//						if(c.getDisaster() instanceof Injury) {
//							System.out.println("BLOOD");
//						testCit = c.getToxicity();	
//						while(testDist>0) {
//							testBld+=10;
//						//	System.out.println("testStr" + testHp + "Dist " + testDist);
//							testDist-=u.getStepsPerCycle();
//							if(testCit>0 && testCit<30)
//								testHp-=5;
//							else if(testCit>=30 && testCit<70)
//								testHp-=10;
//							else if(testCit >=70)
//								testHp-=15;
//							
//						}
//						//System.out.println(testHp);
//
//					while(testCit>0 && testCit<100) {
//					//		for(int k=0;k<21;k++) {
//							System.out.println("testHp" + testHp);
//							testCit-=10;
//							if(testCit>0 && testCit<30)
//								testHp-=5;
//							else if(testCit>=30 && testCit<70)
//								testHp-=10;
//							else if(testCit >=70)
//								testHp-=15;
//						}
//						System.out.println(testHp);
//
//						if(testHp>0) {
//							JOptionPane.showMessageDialog(view, "Treat the citizen @ "+c.getLocation().getX()
//									+" , "+c.getLocation().getY()+"\nby "+u);
//						}
//					
//					}
//					}
//					
//				}
//				
//			}
//		}
//		System.out.println("ended");
//		testStr=100;
//		testBld = 0;
//		testCit = 0;
//		testHp = 100;
//	}
//	
//	
	
	public static void main(String[] args) throws Exception {
		CommandCenter cm = new CommandCenter();
	}
}
