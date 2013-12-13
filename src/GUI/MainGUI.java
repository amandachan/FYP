package GUI;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import main.CentralAuthority;
import main.Parameter;

public class MainGUI extends JPanel {

	public static ArrayList<String> selectedAttack = null;
	public static ArrayList<String> selectedDetect = null;
	public static String selectedEvaluate = null;
	public static Simulated_Env se = null;
	public static Real_Env re = null;
	public JPanel panels[] = {new SimulationAnalyzer_Main(), new ChartAnalyzer_Main()};
	//ArrayList<> newList;
	JLabel label;
	JFrame frame;
	String simpleDialogDesc = "Please select option";
	DetectionListSelection LSD;
	AttackListSelection ALS;

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


		Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);
		frequentPanel.setBorder(padding);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("MainGUI", null, frequentPanel, simpleDialogDesc); // tooltip
		// text

		add(tabbedPane, BorderLayout.CENTER);

		//add(label, BorderLayout.PAGE_END);

	}


	/** Creates the panel shown by the first tab. */
	private JPanel createSimpleDialogBox() {

		final JButton saveBtn;
		final JButton runBtn;
		final JButton importBtn;
		final JButton resetBtn;

		final String BTN_SAVE = "Save";
		final String BTN_IMPORT = "Import";
		final String BTN_RUN = "Run";
		final String BTN_RESET = "Reset";

		JPanel envPanel = new JPanel(new FlowLayout());
		JPanel attackPanel = new JPanel();
		JPanel defensePanel = new JPanel();
		JPanel matrixPanel = new JPanel(new BorderLayout());
		JPanel runPanel = new JPanel();

		JLabel envLabel = new JLabel("Select the desired environment: ");
		final JList envList; 
		DefaultListModel listModel;
		listModel = new DefaultListModel();
		listModel.addElement("Simulated Environment");
		listModel.addElement("Real Environment");
		envList = new JList(listModel);
		envList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JButton envConfBtn = new JButton("Config");
		envConfBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					if (envList.getSelectedValue().toString().equalsIgnoreCase("Simulated Environment")){
						se = new Simulated_Env("Simulate Environment");
					}
					else if (envList.getSelectedValue().toString().equalsIgnoreCase("Real Environment")){
						re = new Real_Env("Real Environment");
					}
				}catch (NullPointerException ex)
				{
					JOptionPane.showMessageDialog(null, "Please Config the environment.");
				}
			}
		});

		envPanel.add(envLabel);
		envPanel.add(envList);
		envPanel.add(envConfBtn);

		JLabel matrixLabel = new JLabel("Select the evaluation matrix: ");
		final JList matrixList; 
		DefaultListModel matrixListModel;
		matrixListModel = new DefaultListModel();
		matrixListModel.addElement("Robustness [-1,1]");
		matrixListModel.addElement("MAE-DS repDiff(Reputation difference of target dishonest seller ([0, 1])");
		matrixListModel.addElement("MAE-HS repDiff(Reputation difference of target honest seller ([0, 1])");
		matrixListModel.addElement("MCC-DS (Classification of target dishonest seller ([-1,1])");
		matrixListModel.addElement("MCC-HS (Classification of target honest seller ([-1,1])");
		matrixList = new JList(matrixListModel);
		matrixList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JButton matrixOKBtn = new JButton("OK");
		matrixOKBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					String u = matrixList.getSelectedValue().toString();
					System.out.println("Evaluation matrix: "+u);
					if (!u.isEmpty()){
						Parameter.EVA_EMPTY = false;
						selectedEvaluate = u;
					}
				}catch (NullPointerException ex)
				{
					JOptionPane.showMessageDialog(null, "Please select one evaluation matrix.");
				}
			}
		});
		matrixPanel.add(matrixLabel,BorderLayout.NORTH);
		matrixPanel.add(matrixList,BorderLayout.CENTER);
		matrixPanel.add(matrixOKBtn,BorderLayout.SOUTH);



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
						if (se != null){
							ca.evaluateDefenses(selectedDetect, selectedAttack, selectedEvaluate, null);
						}
						else if (re !=null){
							ca.evaluateDefenses(selectedDetect, selectedAttack, selectedEvaluate, re.getName());
						}
					}
				} catch (ClassNotFoundException | NoSuchMethodException
						| SecurityException e1) {
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

		runPanel.add(runBtn);
		runPanel.add(importBtn);
		runPanel.add(resetBtn);

		try {
			ALS = new AttackListSelection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		//JPanel attP = ALS.contentPane;
		attackPanel = ALS.contentPane;   	

		LSD = new DetectionListSelection();	
		defensePanel = LSD.contentPane;

		JPanel adPane = new JPanel(new FlowLayout());
		adPane.add(attackPanel);
		adPane.add(defensePanel);

		JPanel topPane = new JPanel(new BorderLayout());
		topPane.add(envPanel, BorderLayout.NORTH);
		topPane.add(adPane, BorderLayout.SOUTH);

		JPanel bottomPane = new JPanel(new BorderLayout());		

		bottomPane.add(matrixPanel, BorderLayout.NORTH);
		bottomPane.add(runPanel, BorderLayout.SOUTH);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(topPane, BorderLayout.PAGE_START);
		contentPane.add(bottomPane, BorderLayout.PAGE_END);

		return contentPane;
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