package GUI;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import main.CentralAuthority;
import main.Parameter;
//import environment.Simulated_Env;

/*
 * DialogDemo.java requires these files:
 *   CustomDialog.java
 *   images/middle.gif
 */


public class MainGUI extends JPanel {

	public static ArrayList<String> selectedAttack = null;
	public static ArrayList<String> selectedDetect = null;
	public static String selectedEvaluate = null;
	public JPanel panels[] = {new SimulationAnalyzer_Main(), new ChartAnalyzer_Main()};
	//ArrayList<> newList;
	JLabel label;
	JFrame frame;
	String simpleDialogDesc = "Please select option";
	DetectionListSelection LSD;
	AttackListSelection ALS;

	// Wenxu's input
	// CustomDialog customDialog;
	JOptionPane pane = new JOptionPane();
	Reset reset = new Reset();
	//Import2 importfunc = new Import2();

	// Wenxu's input

	/** Creates the GUI shown inside the frame's content pane. */
	public MainGUI(JFrame frame) {
		super(new BorderLayout());
		this.frame = frame;

		// Create the components.
		JPanel frequentPanel = createSimpleDialogBox();

		label = new JLabel("Click the \"Show it!\" button"
				+ " to bring up the selected dialog.", JLabel.CENTER);

		// Lay them out.
		Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);
		frequentPanel.setBorder(padding);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("MainGUI", null, frequentPanel, simpleDialogDesc); // tooltip
																				// text

		add(tabbedPane, BorderLayout.CENTER);

		add(label, BorderLayout.PAGE_END);

		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/** Sets the text displayed at the bottom of the frame. */
	void setLabel(String newText) {
		label.setText(newText);
	}

	/** Creates the panel shown by the first tab. */
	private JPanel createSimpleDialogBox() {
		final int numButtons = 4;
		JRadioButton[] radioButtons = new JRadioButton[numButtons];
		final ButtonGroup group = new ButtonGroup();
		final ButtonGroup group2 = new ButtonGroup();

		JButton[] buttonPanel = new JButton[3];

		JButton showItButton = null;

		final String pickEnvironmentCommand = "default";
		final String pickAttackCommand = "yesno";
		final String pickDetectionCommand = "yeahnah";
		final String evaluate = "ync";

		final JButton saveBtn;
		final JButton runBtn;
		final JButton importBtn;
		final JButton resetBtn;

		final String BTN_SAVE = "Save";
		final String BTN_IMPORT = "Import";
		final String BTN_RUN = "Run";
		final String BTN_RESET = "Reset";

		radioButtons[0] = new JRadioButton("Environment");
		radioButtons[0].setActionCommand(pickEnvironmentCommand);

		radioButtons[1] = new JRadioButton("Attack Model");
		radioButtons[1].setActionCommand(pickAttackCommand);

		radioButtons[2] = new JRadioButton("Detection");
		radioButtons[2].setActionCommand(pickDetectionCommand);

		radioButtons[3] = new JRadioButton("Evaluation Metrics");
		radioButtons[3].setActionCommand(evaluate);

		for (int i = 0; i < numButtons; i++) {
			group.add(radioButtons[i]);
		}

		radioButtons[0].setSelected(true);

		showItButton = new JButton("Show it!");
		showItButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String command = group.getSelection().getActionCommand();

				// ok dialog
				if (command == pickEnvironmentCommand) {
					Object[] possibilities = { "Simulated Environment", "Real Environment" };
					String s = (String) JOptionPane.showInputDialog(frame,
							"Select the desired environment:\n",
							"Environment Selection Phase",
							JOptionPane.PLAIN_MESSAGE, null, possibilities,
							"Simulated Environment");// initial selection value

					// Wenxu's input
					if (s != null) { // Make sure the ok button is clicked
						if (s.equalsIgnoreCase(possibilities[0].toString())) { // For Simulated environment
							new Simulated_Env("test");
						}
					}
					// Wenxu's input

					// yes/no dialog
				} else if (command == pickAttackCommand) {
					
//					 DetectionModelsParameters DMP = new  DetectionModelsParameters();
					try {
						ALS = new AttackListSelection();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
					ALS.initialise();
					


					// yes/no (not in those words)
				} else if (command == pickDetectionCommand) {
					
				//	 DetectionModelsParameters DMP = new  DetectionModelsParameters();
				LSD = new DetectionListSelection();	
				LSD.initialise();
				
			
					// BRS
					// TRAVOS
					// iCLUB
					// Personalized
					// WMA
					/*
					Object[] possibilities = { "brs", "travos", "iclub",
							"personalized", "WMA" };
					String t = (String) JOptionPane.showInputDialog(frame,
							"Select the desired attack model:\n",
							"Detection model Selection Phase",
							JOptionPane.PLAIN_MESSAGE, icon, possibilities,
							"brs");// initial selection value
					selectedDetect = t;
					
					
					if (t != null) { // Make sure the ok button is clicked
						if (t.equals(possibilities[0])) { // BRS selected
						
							MasterConfigPanel MCP = new MasterConfigPanel();
							
														
						} else { // for the stimulated environment

							//Simulated_Env gui = new Simulated_Env("test", paramObj);

						}
					}
					*/
					

					// yes/no/cancel (not in those words)
				} else if (command == evaluate) {

					Object[] possibilities = {
							"Robustness [-1,1]",
							"MAE-DS repDiff(Reputation difference of target dishonest seller ([0, 1])",
							"MAE-HS repDiff(Reputation difference of target honest seller ([0, 1])",
							"MCC-DS (Classification of target dishonest seller ([-1,1])",
							"MCC-HS (Classification of target honest seller ([-1,1])"
							};
					String u = (String) JOptionPane.showInputDialog(frame,
							"Select the desired evaluation metric:\n",
							"Evaluation Metric Selection Phase",
							JOptionPane.PLAIN_MESSAGE, null, possibilities,
							"Robustness [-1,1]");// initial selection value
					if (!u.isEmpty()){
						Parameter.EVA_EMPTY = false;
						selectedEvaluate = u;
					}
					

				}
			}
		});

		runBtn = new JButton(BTN_RUN);
		importBtn = new JButton(BTN_IMPORT);
		resetBtn = new JButton(BTN_RESET);


		runBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				//make getlist return arraylist
				selectedDetect = (ArrayList<String>) LSD.getList();
				selectedAttack = (ArrayList<String>) ALS.getList();
				Parameter.TOTAL_NO_OF_BUYERS = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
				Parameter.TOTAL_NO_OF_SELLERS = Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS;
				Parameter.changeTargetValues();
				Parameter.updateValues();
				Parameter.createPdtCatalog();
				Parameter.atkNameList = selectedAttack;
				Parameter.defNameList = selectedDetect;
				CentralAuthority ca = new CentralAuthority();
				try {
					if (Parameter.ENV_EMPTY) {
						JOptionPane.showMessageDialog(null, "There is no input for environment paremeters.\nYou may continue the simulation with default environment parameter values by pressing \"Run\" again, or go to \"Environment\" to input your parameters.");
						Parameter.ENV_EMPTY = false;
					} else if (Parameter.ATK_EMPTY)
					{
						JOptionPane.showMessageDialog(null, "Please choose an attack model from \"Attack Model\"");
					} else if (Parameter.DEF_EMPTY)
					{
						JOptionPane.showMessageDialog(null, "Please choose a detecion model from \"Detection\"");
					} else if (Parameter.EVA_EMPTY)
					{
						JOptionPane.showMessageDialog(null, "Please choose an evaluation metric from \"Evaluation Metric\"");
					} else {
					//	System.out.println("=====Come to MainGUI: ca.evaluateDefenses======");
					//	System.out.println("selectedDetect "+selectedDetect.get(0));
					//	System.out.println("selectedAttack "+selectedAttack.get(0));
						ca.evaluateDefenses(selectedDetect, selectedAttack, selectedEvaluate);
					}
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
									
				//Display display = new Display("Display");
				
			}
		});

		importBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Import importfunc = new Import();
				//String configFileName = selectFile();
				//System.out.println("Imported config file: " + configFileName);
				importfunc.initialise();
			}
		});

		resetBtn.addActionListener(new ActionListener() {
			// reset the configurations
			public void actionPerformed(ActionEvent e) {
				selectedAttack = null;
				selectedDetect = null;
				selectedEvaluate = null;
				Parameter.ATK_EMPTY=true;
				Parameter.DEF_EMPTY=true;
				Parameter.ENV_EMPTY=true;
				Parameter.EVA_EMPTY=true;
				reset.initialise();
			}
		});

		buttonPanel[0] = importBtn;
		buttonPanel[1] = runBtn;
		buttonPanel[2] = resetBtn;
		// still 4 buttons
		for (int j = 0; j < 3; j++) {
			group2.add(buttonPanel[j]);
		}

		return createPane(simpleDialogDesc + ":", radioButtons, showItButton,
				buttonPanel);
	}

	/* Open file chooser dialog and return the filename chosen */
	public String selectFile() {
		String filename = "";
		JFileChooser fc = new JFileChooser();
		int value = fc.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION)
			filename = fc.getSelectedFile().getAbsolutePath();
		return filename;

	}

	/**
	 * Used by createSimpleDialogBox and createFeatureDialogBox to create a pane
	 * containing a description, a single column of radio buttons, and the Show
	 * it! button.
	 */
	private JPanel createPane(String description, JRadioButton[] radioButtons,
			JButton showButton, JButton[] buttonPanel) {

		int numChoices = radioButtons.length;
		JPanel box = new JPanel();

		JPanel box2 = new JPanel();

		JLabel label = new JLabel(description);

		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(label);

		for (int i = 0; i < numChoices; i++) {
			box.add(radioButtons[i]);
		}

		box2.setLayout(new BoxLayout(box2, BoxLayout.X_AXIS));
		box2.add(label);

		for (int j = 0; j < 3; j++) {
			box2.add(buttonPanel[j]);
		}

		JPanel pane = new JPanel(new BorderLayout());

		pane.add(box, BorderLayout.PAGE_START);
		pane.add(showButton, BorderLayout.EAST);
		pane.add(box2, BorderLayout.SOUTH);
		return pane;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("TestBed Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		MainGUI newContentPane = new MainGUI(frame);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}