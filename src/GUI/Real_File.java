package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import main.Parameter;

public class Real_File extends JPanel implements ActionListener, WindowListener{

	SpringLayout layout	= new SpringLayout();
	String name = "";
	String importFileDirectory = "";
	JButton import_file;
	JButton reset;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");

	Real_Env main;

	public Real_File(Real_Env main) {
		this.main = main;
		this.setLayout(layout);
		Border blackline = BorderFactory.createLineBorder(Color.BLACK);


		TitledBorder simTitle = BorderFactory
				.createTitledBorder(blackline, "Load Transaction File");

		import_file = new JButton("Import");
		import_file.addActionListener(this);

		reset = new JButton("Reset");
		reset.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);

		this.add(import_file);
		this.add(reset);
		this.add(ok);
		this.add(cancel);

		layout.putConstraint(SpringLayout.WEST, reset, 10, SpringLayout.EAST, import_file);
		layout.putConstraint(SpringLayout.NORTH, reset, 0, SpringLayout.NORTH, import_file);


		layout.putConstraint(SpringLayout.WEST, ok, 10, SpringLayout.EAST, reset);
		layout.putConstraint(SpringLayout.NORTH, ok, 0, SpringLayout.NORTH, import_file);
		layout.putConstraint(SpringLayout.SOUTH, ok, 0, SpringLayout.SOUTH, import_file);

		layout.putConstraint(SpringLayout.WEST, cancel, 10, SpringLayout.EAST, ok);
		layout.putConstraint(SpringLayout.NORTH, cancel, 0, SpringLayout.NORTH, import_file);
		layout.putConstraint(SpringLayout.SOUTH, cancel, 0, SpringLayout.SOUTH, import_file);

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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(import_file)) {
			importFileDirectory = selectFile();
			
			if (importFileDirectory.length() > 0) {
				if (importFileDirectory.substring(importFileDirectory.lastIndexOf(".") + 1).equals("txt")) {
				}
				else {
					JOptionPane.showMessageDialog(this, "Please make sure that a .txt file is imported!", 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}			
		}

		else if (e.getSource().equals(ok)) {
			//NewFeature newFeature = new NewFeature(modelSelectionBox.getSelectedItem().toString(), labelArray, textFieldArray);
			boolean flagA = fileCopy(importFileDirectory, name);

			if (flagA ){
				JOptionPane.showMessageDialog(null, "File has been imported successfully");
				main.dispose();
			}
		}
		else if (e.getSource().equals(cancel)) {
			main.dispose();
		}
	}

	// Open file chooser dialog and return the filename chosen
	public String selectFile() {
		String filename = "";
		JFileChooser fc = new JFileChooser();
		int value = fc.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION){

			filename = fc.getSelectedFile().getAbsolutePath();
			name = fc.getSelectedFile().getName();
		}
		return filename;
	}

	//FileCopy to destination
	public boolean fileCopy(String srcFileName, String filename) {
		File srcFile = new File(srcFileName);
		String desFileName = "";


		desFileName = "realdata/" + name;
		System.out.println(desFileName);

		try {
			InputStream in = new FileInputStream(srcFile);
			OutputStream out = new FileOutputStream(new File(desFileName));

			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
			System.out.println("File has been copied.");
			return true;
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "File is not found!", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
