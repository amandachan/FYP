package GUI;

import main.Parameter;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AttackListSelection extends JFrame implements ActionListener {
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
 
    public AttackListSelection() throws IOException {
    	
    	ArrayList<String> data = new ArrayList<String>();
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        try {
        	String dirName = "SavedConfiguration\\atkModels.txt";
            File directoryName = new File ("SavedConfiguration");
            if (!directoryName.exists() || !directoryName.isDirectory())
            {
            	directoryName.mkdirs();
            }
            File atkFile = new File(dirName);
            if (!atkFile.exists() || !atkFile.isFile())
            {
            	atkFile.createNewFile();
            	FileWriter fw = new FileWriter(atkFile);
            	fw.write("Sybil\r\nAlwaysUnfair\r\nCamouflage\r\nWhitewashing\r\nSybil_Camouflage\r\nSybil_Whitewashing\r\n..");
            	fw.close();
            }
	        BufferedReader br = new BufferedReader(new FileReader("SavedConfiguration\\atkModels.txt"));
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
        
        list = new JList<Object>(data.toArray());
        
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

        //Build output area.
        output = new JTextArea(1, 10);
        output.setEditable(false);
        JScrollPane outputPane = new JScrollPane(output,
                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //Do the layout.
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        contentPane.add(splitPane, BorderLayout.CENTER);

        JPanel topHalf = new JPanel();
        topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
        JPanel listContainer = new JPanel(new GridLayout(1,1));
        listContainer.setBorder(BorderFactory.createTitledBorder(
                                                "Choose Attack List"));
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
            
    }
    
	public JPanel getPanel(){
		return contentPane;
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
            }
        });
    }

    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            
            selected = list.getSelectedValuesList(); 
           
            String[] selectedItems = new String[selected.size()];


            output.append(newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
    }

	public static List getList() {
		// TODO Auto-generated method stub
		return selected;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			if (e.getSource() == okButton) {
				List selected = getList();
				for (int i = 0; i < selected.size(); i++) {
					System.out.print(selected.get(i) + " ");
				}
				Parameter.ATK_EMPTY = false;
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