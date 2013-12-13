package GUI;

import java.awt.GridLayout;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.MenuListener;

import main.Parameter;
import main.Transaction;
import environment.*;
import weka.core.Instance;
import weka.core.Instances;
import agent.Buyer;



public class SimulationAnalyzer_Main extends JPanel implements EventListener {
	
	private JTextArea simLog = new JTextArea();
	//For transaction details
	private Marketplace_Table transTable;
	private String columnData[] = {"Buyer","Seller","Product No","Product Price"};
	//For balance details
	private Marketplace_Table transBalTable;
	private String columnBalData[] = {"Buyer", "Balance"};
	//For rating details
	private Marketplace_Table transRatingTable;
	private ChartAnalyzer_Main chartMain;
	
	public ChartAnalyzer_Main getChartMain() {
		return chartMain;
	}

	private String columnRatingData[] = {"Seller", "Rating"};
	private ArrayList<Vector<Vector<Object>>> charts = new ArrayList<Vector<Vector<Object>>>(); 
	private ArrayList<Vector<Object>> chartCols = new ArrayList<Vector<Object>>();
	private String [] chartTitles;
    private Vector<Object> col = new Vector<Object>();
    private Vector<Vector <Object>> data = new Vector<Vector <Object>>();
    private boolean isResult = false;
    
	public boolean isResult() {
		return isResult;
	}

	public void setResult(boolean isResult) {
		this.isResult = isResult;
	}

	public SimulationAnalyzer_Main()
	{
		initTable();
		initPanel();
	}
	
	public void initTable()
	{
		transTable = new Marketplace_Table();
		transTable.setInitTable(columnData.length, this.columnData);
		transBalTable = new Marketplace_Table();
		transBalTable.setInitTable(this.columnBalData.length, this.columnBalData);
		transRatingTable = new Marketplace_Table();
		transRatingTable.setInitTable(columnRatingData.length, this.columnRatingData);
	}
	
	public void initPanel()
	{
		this.setLayout(new GridLayout(3,1,10,10));
		simLog.setSize(10,10);
		simLog.setEditable(false);
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(1,0));
		textPanel.add(new JScrollPane(simLog));
		textPanel.setBorder(BorderFactory.createTitledBorder("Log Details"));
		add(textPanel);
		
		textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(1,0));
		textPanel.setBorder(BorderFactory.createTitledBorder("Rating Details"));
		textPanel.add(transTable);
		add(textPanel);
		
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new GridLayout(1,2));
		
		textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(1,0));
		textPanel.setBorder(BorderFactory.createTitledBorder("Buyer Details"));
		textPanel.add(transBalTable);
		tablePanel.add(textPanel);
		
		textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(1,0));
		textPanel.setBorder(BorderFactory.createTitledBorder("Seller Details"));
		textPanel.add(transRatingTable);
		tablePanel.add(textPanel);
		
		add(tablePanel);
	}
	
	public void setChartAnalyzer(ChartAnalyzer_Main chartMain)
	{
		this.chartMain = chartMain;
	}
	
	public void readLogFile()
	{
		try
		{
			Scanner readLog = new Scanner(simLog.getText());
			
			//Attributes to read
			String nextLine = "",transaction = "";
			Vector<String> transData = new Vector<String>(); //Use for storing transaction info
			Vector<String> balData = new Vector<String>(); //Use for storing of balance info
			Vector<String> ratingData = new Vector<String>(); //Use for storing of rating info
			int i = 0;
			while(readLog.hasNextLine())
			{
				nextLine = readLog.nextLine();
				
				if(isResult){
					processResults(nextLine);
				}
				else
				{
					if(nextLine.startsWith("Rating"))
					{
						ratingData = new Vector<String>();
						ratingData = splitLine(nextLine);
						ratingData.removeElementAt(0);
						ratingData.removeElementAt(0);
						setRowData(ratingData,'C');
						System.out.println(ratingData.toString());
					}
					else if(nextLine.startsWith("Balance"))
					{
						balData = new Vector<String>();
						balData = splitLine(nextLine);
						balData.removeElementAt(0);
						setRowData(balData,'B');
					}
					else if(nextLine.contains("Simulation result"))
					{
						isResult = true;
					}
					else
					{
						transaction = nextLine.split(" - ")[nextLine.split(" - ").length-1];
						if(transaction.contains("(OK)"))
						{
							transData = new Vector<String>();
							transData.addElement(transaction.split(" <-")[0]);
							transData.addElement(transaction.split("-> ")[1].substring(0,transaction.split("-> ")[1].indexOf("(OK")));
							transData.addElement(transaction.split(" <-")[1].split("-> ")[0].substring(0,transaction.split(" <-")[1].split("-> ")[0].indexOf("(x")-1));
							transData.addElement(transaction.split(" <-")[1].split("-> ")[0].substring(transaction.split(" <-")[1].split("-> ")[0].indexOf("(x")+2,
							transaction.split(" <-")[1].split("-> ")[0].length()-1));
							setRowData(transData,'A');
						}
					}
				}
				
			}
			System.out.println("Reading Finished!");
			readLog.close();
			if(!charts.isEmpty())
				chartMain = new ChartAnalyzer_Main(charts, chartCols, chartTitles);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void processResults(String nextLine)
	{
		if(nextLine.contains("*** #"))
		{
			charts.add(data);
			data = new Vector<Vector <Object>>();
			col = new Vector<Object>();
			isResult = false;
			return;
		}
		
		if(nextLine.startsWith("*** "))
		{
//			chartTitles.add(nextLine.substring(nextLine.indexOf("***")+3,nextLine.lastIndexOf("***")).trim());
//			if(!data.isEmpty())
//			{
//				charts.add(data);
//				data = new Vector<Vector <Object>>();
//				col = new Vector<Object>();
//			}
			return;
		}
		else if (nextLine.startsWith("-- "))
		{
			//chartTitles.add(nextLine.substring(nextLine.indexOf("Used: ")+6,nextLine.lastIndexOf(" --")).trim());
			if(!data.isEmpty())
			{
				charts.add(data);
				data = new Vector<Vector <Object>>();
				col = new Vector<Object>();
			}
		}
		else
		{//get mean and variance title names
			if(col.isEmpty())
			{
				col.addAll(processChartColumns(nextLine));
				chartCols.add(processChartColumns(nextLine));
			}
			//get mean and variance values
			data.add(processChartData(nextLine));
		}
	}
	
	public void readTrans(ArrayList transList, ArrayList<Buyer> buyers, Environment commerce){
		Instances instList = commerce.getM_Transactions();
		Instance inst = null;
		for (int a =0; a<transList.size(); a++){
			Vector<String> transData = new Vector<String>();
			transData.addElement("b"+((Transaction)transList.get(a)).getBuyer().getId());
			transData.addElement("s"+((Transaction)transList.get(a)).getSeller().getId());
			//getProductNo to getQuantity
			transData.addElement(String.valueOf(((Transaction)transList.get(a)).getProduct().getId()));
			transData.addElement(String.valueOf(((Transaction)transList.get(a)).getPrice()));
			setRowData(transData,'A');
		}
		for (int b =0; b<buyers.size(); b++)
		{
			DecimalFormat roundoff = new DecimalFormat("0.00");
			Vector<String> buyersData = new Vector<String>();
			buyersData.addElement("b"+String.valueOf(buyers.get(b).getId()));
			buyersData.addElement(String.valueOf(roundoff.format(buyers.get(b).getAccountDetails().getBalance())));
			setRowData(buyersData,'B');
		}
		double[] sellerRating = new double[Parameter.TOTAL_NO_OF_SELLERS];
		double[] sellerRatingCount = new double[Parameter.TOTAL_NO_OF_SELLERS];
		for (int e = 0; e<sellerRating.length; e++)
		{
			sellerRating[e] = 0;
			sellerRatingCount[e] = 0;
		}
		for (int c = 0; c<instList.numInstances(); c++)
		{
			inst = instList.instance(c);
			int sellerID = (int) inst.value(Parameter.m_sidIdx);
			sellerRating[sellerID] += inst.value(Parameter.m_ratingIdx);
			sellerRatingCount[sellerID]++;
			//System.out.println("Debug: sellerRating of " +sellerID+"is "+sellerRating[sellerID]);
			//System.out.println("Debug: sellerRatingCount:"+sellerRatingCount[sellerID]);
		}
		for (int f = 0; f<sellerRating.length; f++)
		{
			DecimalFormat roundoff = new DecimalFormat("0.000");
			Vector<String> sellerData = new Vector<String>();
			if (sellerRating[f] == 0.0 && sellerRatingCount[f] ==0)
			{
				sellerRating[f] = commerce.getSellersTrueRating(f);
			} else {
				sellerRating[f] /= sellerRatingCount[f];
			}
			sellerData.addElement("s"+f);
			sellerData.addElement(String.valueOf(roundoff.format(sellerRating[f])));
			setRowData(sellerData, 'C');			
		}
//		if(!charts.isEmpty())
//		   chartMain = new ChartAnalyzer_Main(charts, chartCols,chartTitles);
	}
	
	public void setChartTableData(int j, int k, double mean, double std)
	{
		Vector set = null;
			set = new Vector();
				set.add(MainGUI.selectedAttack.get(k).toString());
				set.add(MainGUI.selectedDetect.get(j).toString());
				set.add(mean);
				set.add(std);
				data.add(set);
				charts.add(data);			

		set = new Vector();
		set.add("Atk Model");
		set.add("Detection Model");
		set.add("Mean");
		set.add("Std. Variance");
		chartCols.add(set);
		if(!charts.isEmpty())
			   chartMain = new ChartAnalyzer_Main(charts, chartCols, chartTitles);
	}
	
	public Vector<Object> processChartData(String line)
	{
		if(line.split("[,]").length < 1)
			return null;
		
		String[] columns = line.split(",");
		Vector<Object> row = new Vector<Object>();
		
		for(String column : columns)
			row.add(column.split(":")[1].trim());
		
		return row;
	}
	
	public Vector<Object> processChartColumns(String line)
	{
		if(line.split("[,]").length < 1)
			return null;
		
		String[] columns = line.split(",");
		Vector<Object> col = new Vector<Object>();
		
		for(String column : columns)
			col.add(column.split(":")[0].trim());
		
		return col;
	}
	
	public Vector<String> splitLine(String line)
	{
		Vector<String> listofWords = new Vector<String>();
		String word = "";

		for(char curChar : line.toCharArray())
		{
			
			if(curChar!=' ' && curChar!=':')
				word += curChar;
			else if(curChar == ' ')
			{
				if(!word.isEmpty())
					listofWords.addElement(word);
				word = "";
			}
		}
		if(!word.isEmpty())
			listofWords.addElement(word);
		return listofWords;

	}
	
	public void setText(String text)
	{
		simLog.append(text + "\n");
		simLog.setCaretPosition(simLog.getDocument().getLength());
	}
	
	public void setDouble(Double text)
	{
		simLog.append(text + "\n");
		simLog.setCaretPosition(simLog.getDocument().getLength());
	}
	
	public ArrayList<Vector<Vector<Object>>> getCharts()
	{
		return charts;
	}
	
	public ArrayList<Vector<Object>> getChartColumns()
	{
		return chartCols;
	}
	
	public void setRowData(Vector<String> transData, char type)
	{
		if(type=='A')
			transTable.addRowData(transData);
		else if(type=='B')
			transBalTable.addRowData(transData);
		else
			transRatingTable.addRowData(transData);
	}

	public void onRecvMyEvent(MyEvent event) {
		simLog.append(event.text + "\n");
		simLog.setCaretPosition(simLog.getDocument().getLength());
	}

}
