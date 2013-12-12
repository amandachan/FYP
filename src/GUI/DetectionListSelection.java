package GUI;

import main.Parameter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DetectionListSelection extends JFrame implements ActionListener {
    JTextArea output;
    JList list; 
    JTable table;
    static List selected;
    String newline = "\n";
    ListSelectionModel listSelectionModel;
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");
    static JFrame frame = new JFrame();
    JSplitPane splitPane;
    JPanel contentPane;
    ArrayList<String> data;
 
    public DetectionListSelection() {
    	data = new ArrayList<String>();
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());
    	//super(new BorderLayout());

        try {
        String dirName = "SavedConfiguration\\defModels.txt";
        File directoryName = new File ("SavedConfiguration");
        if (!directoryName.exists() || !directoryName.isDirectory())
        {
        	directoryName.mkdirs();
        }
        File defFile = new File(dirName);
        if (!defFile.exists() || !defFile.isFile())
        {
        	defFile.createNewFile();
        	FileWriter fw = new FileWriter(defFile);
        	fw.write("BRS\r\neBay\r\nTRAVOS\r\nIClub\r\nPersonalized\r\nProbCog\r\nWMA\r\n..");
        	fw.close();
        }
	        BufferedReader br = new BufferedReader(new FileReader(dirName));
	        String line = br.readLine();
	        while (!line.equalsIgnoreCase("..")){
				data.add(line);
				line = br.readLine();
			}
	        br.close();
        }
        catch (IOException ex) {
        	ex.getMessage();
        }
    	// BRS
		// TRAVOS
		// iCLUB
		// Personalized
		// WMA
        //String[] listData = { "BRS", "TRAVOS", "iCLUB", "Personalized","ProbCog", "WMA"};
      //  String[] columnNames = { "French", "Spanish", "Italian" };
        
        list = new JList(data.toArray());
        
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if(super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                }
                else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });
        
        listSelectionModel = list.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                new SharedListSelectionHandler());
        JScrollPane listPane = new JScrollPane(list);

        /*JPanel controlPane = new JPanel();
        String[] modes = { "SINGLE_SELECTION",
                           "SINGLE_INTERVAL_SELECTION",
                           "MULTIPLE_INTERVAL_SELECTION" };

        final JComboBox comboBox = new JComboBox(modes);
        comboBox.setSelectedIndex(2);
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newMode = (String)comboBox.getSelectedItem();
                if (newMode.equals("SINGLE_SELECTION")) {
                    listSelectionModel.setSelectionMode(
                        ListSelectionModel.SINGLE_SELECTION);
                } else if (newMode.equals("SINGLE_INTERVAL_SELECTION")) {
                    listSelectionModel.setSelectionMode(
                        ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                } else {
                    listSelectionModel.setSelectionMode(
                        ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                }
                output.append("----------"
                              + "Mode: " + newMode
                              + "----------" + newline);
            }
        });
        controlPane.add(new JLabel("Selection mode:"));
        controlPane.add(comboBox);*/

        //Build output area.
        output = new JTextArea(1, 10);
        output.setEditable(false);
//        JScrollPane outputPane = new JScrollPane(output,
//                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
//                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //Do the layout.
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        contentPane.add(splitPane, BorderLayout.CENTER);

        JPanel topHalf = new JPanel();
        topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
        JPanel listContainer = new JPanel(new GridLayout(1,1));
        listContainer.setBorder(BorderFactory.createTitledBorder(
                                                "List"));
        listContainer.add(listPane);
        
        topHalf.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        topHalf.add(listContainer);
        //topHalf.add(tableContainer);

        topHalf.setMinimumSize(new Dimension(100, 50));
        topHalf.setPreferredSize(new Dimension(100, 110));
        splitPane.add(topHalf);

        JPanel bottomHalf = new JPanel(new FlowLayout());
        okButton.addActionListener(this);
		cancelButton.addActionListener(this);
        bottomHalf.add(okButton);
        bottomHalf.add(cancelButton);
        bottomHalf.setMinimumSize(new Dimension(200, 50));
        bottomHalf.setPreferredSize(new Dimension(300, 40));
        splitPane.add(bottomHalf);
        
        /*bottomHalf.add(controlPane, BorderLayout.PAGE_START);
        bottomHalf.add(outputPane, BorderLayout.CENTER);
        //XXX: next line needed if bottomHalf is a scroll pane:
        //bottomHalf.setMinimumSize(new Dimension(400, 50));
        bottomHalf.setPreferredSize(new Dimension(450, 135));
        splitPane.add(bottomHalf);*/             
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("DetectionListSelection");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //Create and set up the content pane.
        //DetectionListSelection demo = new DetectionListSelection();
        //demo.setOpaque(true);
        frame.setContentPane(contentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void initialise() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();              
                //DetectionModelsParameters DMP = new DetectionModelsParameters();
            }
        });
    }

    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            
            selected = list.getSelectedValuesList(); 
           
            String[] selectedItems = new String[selected.size()];

//            for(int i=0; i < selected.size();i++) {
//            	selectedItems[i] = selected.get(i).toString();
//            	output.append(selectedItems[i]);
//            }
            
            /*for (int i = 0; i < selectedItems.length; i++) {
            	System.out.println(selectedItems[i]);
            }*/

//            int firstIndex = e.getFirstIndex();
//            int lastIndex = e.getLastIndex();
//            boolean isAdjusting = e.getValueIsAdjusting(); 
//            output.append("Event for indexes "
//                          + firstIndex + " - " + lastIndex
//                          + "; isAdjusting is " + isAdjusting
//                          + "; selected indexes:");

//            if (lsm.isSelectionEmpty()) {
//                output.append(" <none>");
//            } else {
//                // Find out which indexes are selected.
//                int minIndex = lsm.getMinSelectionIndex();
//                int maxIndex = lsm.getMaxSelectionIndex();
//                for (int i = minIndex; i <= maxIndex; i++) {
//                    if (lsm.isSelectedIndex(i)) {
//                        output.append(" " + i);
//                    }
//                }
//            }
            output.append(newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
    }

	public static List getList() {
		// TODO Auto-generated method stub
		return selected;
	}
	
	public ArrayList getDataList() {
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			if (e.getSource() == okButton) {
				List selected = getList();
				try {
					DetectionModelsParameters dmp = new DetectionModelsParameters(selected, getDataList());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				frame.setVisible(false);
			}
			else {
				frame.setVisible(false);
			}
		}
		catch (NullPointerException ex)
		{
			JOptionPane.showMessageDialog(null, "Please select at least one model.");
		}
	}
}