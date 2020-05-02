package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import model.infrastructure.ResidentialBuilding;
import model.units.*;

public class GameView extends JFrame{
	private JPanel pnlCity;
	private JPanel pnlUnits;
	private JPanel pnlAvUnits;
	private JPanel pnlResUnits;
	private JPanel pnlTrUnits;
	private JPanel controls;
	private JPanel text;
	public JButton nextCycle;

	
	public GameView() {
		super();
		setTitle("Rescue Simulation");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBounds(50, 50, 800, 600);
		
		pnlCity = new JPanel();
		TitledBorder cityBorder = BorderFactory.createTitledBorder("The World");
		cityBorder.setTitleFont(new Font(Font.MONOSPACED,Font.BOLD,30));
		pnlCity.setBorder(cityBorder);
		pnlCity.setLayout(new GridLayout(10, 10));
		add(pnlCity, BorderLayout.CENTER); 
		
		pnlUnits = new JPanel();
		pnlAvUnits =new JPanel();
		pnlResUnits =new JPanel();
		pnlTrUnits =new JPanel();
		
		TitledBorder units = BorderFactory.createTitledBorder("Units");
		units.setTitleFont(new Font(Font.MONOSPACED,Font.BOLD,30));
		pnlUnits.setBorder(units);
		
		TitledBorder availableUnits= BorderFactory.createTitledBorder("Available Units");
		availableUnits.setTitleFont(new Font(Font.MONOSPACED,Font.ITALIC,25));
		pnlAvUnits.setBorder(availableUnits);
		
		TitledBorder RespondingUnits = BorderFactory.createTitledBorder("Responding Units");
		RespondingUnits.setTitleFont(new Font(Font.MONOSPACED,Font.ITALIC,25));
		pnlResUnits.setBorder(RespondingUnits);
		
		TitledBorder TreatingUnits = BorderFactory.createTitledBorder("Treating Units");
		TreatingUnits.setTitleFont(new Font(Font.MONOSPACED,Font.ITALIC,25));
		pnlTrUnits.setBorder(TreatingUnits);
		
		pnlUnits.setLayout(new GridLayout(3,0));
		pnlAvUnits.setLayout(new FlowLayout());
		pnlResUnits.setLayout(new FlowLayout());
		pnlTrUnits.setLayout(new FlowLayout());
		
		pnlAvUnits.setPreferredSize(new Dimension(300,300));
		pnlAvUnits.setBackground(new Color(235, 245, 251));
		
		pnlResUnits.setPreferredSize(new Dimension(300,300));
		pnlResUnits.setBackground(new Color(214, 234, 248));
		
		pnlTrUnits.setPreferredSize(new Dimension(300,300));
		pnlTrUnits.setBackground(new Color(174, 214, 241));
		
		pnlUnits.add(pnlAvUnits,BorderLayout.NORTH);
		pnlUnits.add(pnlResUnits,BorderLayout.CENTER);
		pnlUnits.add(pnlTrUnits,BorderLayout.SOUTH);
		pnlUnits.revalidate();
		pnlUnits.repaint();
		
		pnlUnits.setPreferredSize(new Dimension(300,300));
		add(pnlUnits, BorderLayout.EAST);
		
		controls = new JPanel();
		controls.setLayout(new FlowLayout());
		TitledBorder cycleControl =BorderFactory.createTitledBorder("Controller");
		cycleControl.setTitleFont(new Font(Font.MONOSPACED,Font.BOLD,25));
		controls.setBorder(cycleControl);
		add(controls, BorderLayout.SOUTH);
		
		text =new JPanel();
		text.setLayout(new GridLayout(3,0));
		text.setBackground(Color.CYAN);
		add(text,BorderLayout.WEST);
	    this.setVisible(true);
	    this.validate();
	    this.repaint();
	    	    
	}
	
	public JPanel getText() {
		return text;
	}

	public void updateCity(ArrayList<ResidentialBuilding> b) { //shows building info on the left side (should be set to visible when building is clicked)
		for(int i=0;i<10;i++) {
				JButton comp = new JButton("living citizens: " +b.get(i).getOccupants().size() );
				pnlCity.add(comp, b.get(i).getLocation().getX(), b.get(i).getLocation().getY());
			JPanel buildingInfo = new JPanel();
			JTextArea infoBuilding = new JTextArea("Info: "+ "/n" + "Location: " + b.get(i).getLocation().getY() +" , "+ b.get(i).getLocation().getY()+"/n" + "Occupant number: " +b.get(i).getOccupants().size() + "/n" + "CurrentDisaster: " + b.get(i).getLocation().getClass().getCanonicalName()+"/n");
			this.add(buildingInfo, BorderLayout.WEST);
			buildingInfo.add(infoBuilding, BorderLayout.NORTH);
		}
	}
	
	public void updateUnits(ArrayList<Unit> u) { //simulator.getEmergencyUnits()
		for(int i=0;i<10;i++) {
				JButton comp = new JButton("" + Unit.class.getName() );
				switch(u.get(i).getState()) {
				case IDLE : pnlUnits.add(comp, BorderLayout.NORTH);break;
				case RESPONDING : pnlUnits.add(comp, BorderLayout.CENTER);break;
				case TREATING : pnlUnits.add(comp, BorderLayout.SOUTH);break;
				}
				
			
		}
	}
	
	public void updateGameState(int casualties) { //to be called when next cycle button is pressed
		String state = "causalities:" + casualties; // simulator.getCasualties()
		
//		gameState.setText(state);
	}
	
	public JPanel getPnlCity() {
		return pnlCity;
	}

	public JPanel getPnlUnits() {
		return pnlUnits;
	}
	public JPanel getPnlAvUnits() {
		return pnlAvUnits;
	}


	public JPanel getPnlResUnits() {
		return pnlResUnits;
	}

	public JPanel getPnlTrUnits() {
		return pnlTrUnits;
	}

	public JPanel getControls() {
		return controls;
	}

	public static void main(String[] args) {
		GameView game = new GameView();
	}
}
