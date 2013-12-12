package GUI;

import main.Parameter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class Marketplace_Main extends JPanel implements ActionListener, WindowListener {
	SpringLayout layout	= new SpringLayout();
	Marketplace_Simulation	simConfig;
	JButton save_Config;
	JButton import_Config;
	JButton reset;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	
	//ImageIcon runIcon = new ImageIcon("Run.png");
	//ImageIcon nextIcon = new ImageIcon("Next.png");
	//ImageIcon previousIcon = new ImageIcon("Previous.png");
	
	//JButton run;
	//JButton next;
	//JButton previous;
	
	Simulated_Env main;
	//Marketplace_Controls marketControls;

	public Marketplace_Main(Simulated_Env main) {
		this.main = main;
		this.setLayout(layout);
		Border blackline = BorderFactory.createLineBorder(Color.BLACK);

		simConfig = new Marketplace_Simulation();
		TitledBorder simTitle = BorderFactory
				.createTitledBorder(blackline, "Marketplace Configuration");
		simConfig.setBorder(simTitle);
		
		
		save_Config = new JButton("Save Configuration");
		save_Config.addActionListener(this);
		import_Config = new JButton("Import Configuration");
		import_Config.addActionListener(this);
		reset = new JButton("Reset");
		reset.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);
		
		//next = new JButton(nextIcon);
		//next.addActionListener(this);
		//previous = new JButton(previousIcon);
		//previous.setVisible(false);
		//previous.addActionListener(this);
		//run = new JButton(runIcon);
		//run.addActionListener(this);
		//this.setPreferredSize(new Dimension(50, 50));
		
		this.add(simConfig);
		this.add(save_Config);
		this.add(import_Config);
		this.add(reset);
		//this.add(next);
		//this.add(previous);
		//this.add(run);
		this.add(ok);
		this.add(cancel);
		
		layout.putConstraint(SpringLayout.WEST, simConfig, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, simConfig, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, simConfig, -10, SpringLayout.EAST, this);
		
		layout.putConstraint(SpringLayout.WEST, save_Config, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, save_Config, 10, SpringLayout.SOUTH, simConfig);
		
		layout.putConstraint(SpringLayout.WEST, import_Config, 10, SpringLayout.EAST, save_Config);
		layout.putConstraint(SpringLayout.NORTH, import_Config, 0, SpringLayout.NORTH, save_Config);
		
		layout.putConstraint(SpringLayout.WEST, reset, 10, SpringLayout.EAST, import_Config);
		layout.putConstraint(SpringLayout.NORTH, reset, 0, SpringLayout.NORTH, import_Config);
		
//		layout.putConstraint(SpringLayout.WEST, next, 10, SpringLayout.EAST, reset);
//		layout.putConstraint(SpringLayout.NORTH, next, 0, SpringLayout.NORTH, import_Config);
//		layout.putConstraint(SpringLayout.SOUTH, next, 0, SpringLayout.SOUTH, import_Config);
//		
//		layout.putConstraint(SpringLayout.WEST, previous, 10, SpringLayout.EAST, reset);
//		layout.putConstraint(SpringLayout.NORTH, previous, 0, SpringLayout.NORTH, import_Config);
//		layout.putConstraint(SpringLayout.SOUTH, previous, 0, SpringLayout.SOUTH, import_Config);
//		
//		layout.putConstraint(SpringLayout.WEST, run, 10, SpringLayout.EAST, next);
//		layout.putConstraint(SpringLayout.NORTH, run, 0, SpringLayout.NORTH, import_Config);
//		layout.putConstraint(SpringLayout.SOUTH, run, 0, SpringLayout.SOUTH, import_Config);
		
		layout.putConstraint(SpringLayout.WEST, ok, 10, SpringLayout.EAST, reset);
		layout.putConstraint(SpringLayout.NORTH, ok, 0, SpringLayout.NORTH, import_Config);
		layout.putConstraint(SpringLayout.SOUTH, ok, 0, SpringLayout.SOUTH, import_Config);
		
		layout.putConstraint(SpringLayout.WEST, cancel, 10, SpringLayout.EAST, ok);
		layout.putConstraint(SpringLayout.NORTH, cancel, 0, SpringLayout.NORTH, import_Config);
		layout.putConstraint(SpringLayout.SOUTH, cancel, 0, SpringLayout.SOUTH, import_Config);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save_Config)
		{
			String[] filename = initFileSaveChooser();
			System.out.println(filename[1]);
			String[] check = filename[1].split("\\.", 0);
			String file = check[0];
			String[] key = simConfig.configuration(file); //Store temp file
						
			if ((check.length == 1) || (!check[check.length - 1].equalsIgnoreCase("dat")))
			{
				filename[0] = filename[0] + ".dat";
				filename[1] = filename[1] + ".dat";
			}
			try
			{
				PrintWriter output = new PrintWriter(filename[0]);
				output.print("simConfig=");
				output.println(key[4]);
				output.print("agentConfig=");
				output.println(key[0]);
				output.print("productConfig=");
				output.println(key[1]);
				output.print("schedConfig=");
				output.println(key[3]);
				output.print("masterConfig=");
				output.println(key[2]);
				output.close();
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
			
			
		}
		else if (e.getSource() == import_Config)
		{
			String[] filename = initFileOpenChooser();
			File file = new File(filename[0]);
			try
			{
				String[] check = filename[1].split("\\.", 0);
				if (check[check.length-1].equalsIgnoreCase("dat"))
				{
					Scanner input = new Scanner(file);
					String[] configFile = new String[5];
					String key = null;
					int i = 0;
					while (input.hasNext())
					{
						key = input.nextLine();
						String[] partKey = key.split("=", 0);
						configFile[i] = partKey[1];
						i++;
					}
					simConfig.importConfig(configFile);
				}
				else
				{
					System.out.println("Wrong File Format!!!");
				}
			}
			catch (Exception ex)
			{
				System.out.println("File Not Found!!!");
			}
		}
		else if (e.getSource() == reset)
		{
			simConfig.setTextField();
			simConfig.agentConfig.setTextField();
			simConfig.productConfig.setTextField();
			simConfig.schedConfig.setTextField();
			
		}
		else if (e.getSource() == ok) 
		{
			simConfig.saveParam();
			simConfig.agentConfig.saveParam();
			simConfig.productConfig.saveParam();
			simConfig.schedConfig.saveParam();
			Parameter.ENV_EMPTY = false;
			main.dispose();
			
		}
		else if (e.getSource() == cancel) 
		{
			main.dispose();
		}
		/*
		else if (e.getSource() == next)
		{
			simConfig.agentConfig.setVisible(false);
			simConfig.productConfig.setVisible(false);
			simConfig.schedConfig.setVisible(false);
			this.next.setVisible(false);
			this.previous.setVisible(true);
		}
		else if (e.getSource() == previous)
		{
			simConfig.agentConfig.setVisible(true);
			simConfig.productConfig.setVisible(true);
			simConfig.schedConfig.setVisible(true);
			this.next.setVisible(true);
			this.previous.setVisible(false);
			
		}
		else if (e.getSource() == run)
		{
			

		}
		*/
	}
	
	
	//Import file method
	public String[] initFileOpenChooser()
	{
		String[] filename = new String[2];
		
		try
		{
			JFileChooser fc = new JFileChooser();
			int value = fc.showOpenDialog(this);
			if (value == fc.APPROVE_OPTION)
			{
				filename[0] = fc.getSelectedFile().getAbsolutePath();
				filename[1] = fc.getSelectedFile().getName();
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return filename;
	}
	
	//Auto save the file in the backend, execute only during run button
	public String autoSave()
	{
		//Configuration file name parameters
		String tempFileName="Default";
		String configurationFileName="SavedConfiguration\\";
			
		//Create temp file
		String[] key = simConfig.configuration(tempFileName); 
		
		return configurationFileName+tempFileName+"\\SimulationConfiguration.ini";	
	}
	
	//To save file method
	public String[] initFileSaveChooser()
	{
		String[] filename = new String[2];
		try
		{
			JFileChooser fc = new JFileChooser();
			int value = fc.showSaveDialog(this);
			if (value == fc.APPROVE_OPTION)
			{
				filename[0] = fc.getSelectedFile().getAbsolutePath();
				filename[1] = fc.getSelectedFile().getName();
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		return filename;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
