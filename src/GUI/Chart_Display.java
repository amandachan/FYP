package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.CentralAuthority;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class Chart_Display extends JPanel implements ActionListener {

	private CategoryDataset dataset;
	private JFreeChart chart;
	private ChartPanel chartPanel;
	// Label and dropbox
	private String title;
	private JLabel chartLabel;
	private Chart_Table chartTable;
	private Container contentChartPane;
	private JComboBox chartComboBox;
	private Container contentlabelDropBoxPane;
	private String[] chartTitles;
	private ChartAnalyzer_Main chart_Main;
	private String[] attackList;
	private String attackName;
	private JButton okButton;

	public Chart_Display(Chart_Table chartTable, ChartAnalyzer_Main chart_Main,
			String[] attackList, String attackName, ArrayList chartList) {
		// this.title = chartTitles.get(index);
		this.chart_Main = chart_Main;
		this.setLayout(new BorderLayout(10, 10));
		this.chartTable = chartTable;
		this.attackList = attackList;
		this.attackName = attackName;
		initLabelAndDropBox(attackList);
		initChartData(attackName, chartList);
		initOKButton();
	}

	public void setSelectedIndex(int index) {
		this.chartComboBox.setSelectedIndex(index);
	}

	// To create label and drop box for the chart
	public void initLabelAndDropBox(String[] attackList) {
		// Container to store label and dropbox
		contentlabelDropBoxPane = new Container();
		contentlabelDropBoxPane.setLayout(new FlowLayout());
		chartLabel = new JLabel("Chart Type: ");
		chartComboBox = new JComboBox(attackList);
		chartComboBox.setSelectedIndex(0); // Default selected drop box value
		chartComboBox.addActionListener(this);
		// Adding container to the panel
		contentlabelDropBoxPane.add(chartLabel);
		contentlabelDropBoxPane.add(chartComboBox);
		contentlabelDropBoxPane.setSize(this.getWidth(), 20);
		this.add(contentlabelDropBoxPane, BorderLayout.NORTH);
	}

	// To create chart diagram
	public void initChartData(String attackName, ArrayList chartList) { // Std. var
		// Container to store the chart diagram
		contentChartPane = new Container();
		contentChartPane.setLayout(new FlowLayout());
		// Creating of chart
		// dataset = createDataset();
		dataset = accessHashMap(attackName);
		chart = createChart(dataset);
		chartList.add(chart);
		chartPanel = new ChartPanel(chart);
		// this.add(chartPanel, BorderLayout.CENTER);
		// Adding container to the panel
		contentChartPane.add(chartPanel);
		this.add(chartPanel, BorderLayout.CENTER);
	}
	
	// To create OKButton at the end (Return to main menu)
	private void initOKButton() {
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		contentChartPane.add(okButton);		
	}

	// To create a method to read from a file and display the chart content
	private CategoryDataset createDataset(ArrayList meanList, ArrayList varList,
			ArrayList trustModelList) {

		// Vector<String> series = new Vector<String>();
		String series = "Mean";
		String series1 = "Variance";
		// Vector<String> xAxis = new Vector<String>();
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// char x = 65;
		// for(int i = 0;i < chartTable.getRow().size();i++)
		// {
		// x += i;
		// series.addElement(""+x);
		// }

		// for(Object col : chartTable.getCol())
		// {
		// xAxis.addElement((String)col);
		// }
		//
		// int col = 0;
		// int rowIndex = 0;
		// for(Vector<Object> row : chartTable.getRow())
		// {
		// for(Object value: row)
		// dataset.addValue(6.0, series1, category1);
		// dataset.addValue(Math.abs(Double.parseDouble((String)value)),series.get(rowIndex),xAxis.get(col++));
		// rowIndex++;col = 0;
		// }

		// ComparisonResults cr = new ComparisonResults();
		// ArrayList trustModelList = new ArrayList();
		// ArrayList meanList = new ArrayList();
		// for (int i = 0; i < attackList.length; i++) {
		// //dataset.addValue(1.0, series1, (String)defenseList[i]);
		// cr = (ComparisonResults) TestAll.outputResult.get(attackList[i]);
		// trustModelList = cr.getTrustModelList();
		// meanList = cr.getMeanList();
		// for (int j = 0; j < meanList.size(); j++) {
		// dataset.addValue(Double.parseDouble((String) meanList.get(j)),
		// series, (String) trustModelList.get(j));
		// }
		//
		// //dataset.addValue(3.0, series1, category3);
		// }
		for (int j = 0; j < meanList.size(); j++) {
			dataset.addValue(Double.parseDouble((String) meanList.get(j)),
					series, (String) trustModelList.get(j));
		}
		
		for (int k = 0; k < varList.size(); k++) {
			dataset.addValue(Double.parseDouble((String) varList.get(k)),
					series1, (String) trustModelList.get(k));
		}

		return dataset;
	}

	private JFreeChart createChart(final CategoryDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createBarChart(attackName.toUpperCase()+" against: ", // chart
																			// title
				"Defense Models", // domain axis label
				"Value", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);

		// set up gradient paints for series...
		final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
				0.0f, 0.0f, Color.lightGray);
		final GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
				0.0f, 0.0f, Color.lightGray);
		final GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
				0.0f, 0.0f, Color.lightGray);
		renderer.setSeriesPaint(0, gp0);
		renderer.setSeriesPaint(1, gp1);
		renderer.setSeriesPaint(2, gp2);

		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}

	// For action listener when clicking the drop box
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.chartComboBox)) {
			chart_Main.initPanel(this.chartComboBox.getSelectedIndex());
			// initChartData(attackName);
		}
		else if (e.getSource().equals(okButton)) {
			contentChartPane.setVisible(false);
		}
	}

	public CategoryDataset accessHashMap(String attackName) {
		//TODO not used?
		Object[] keySet = CentralAuthority.outputResult.keySet().toArray();
		ComparisonResults cr = new ComparisonResults();
		//TODO getStatistics
		cr = (ComparisonResults) CentralAuthority.outputResult.get(attackName);

		return dataset = createDataset(cr.getMeanList(), cr.getVarList(), cr.getTrustModelList());
	}
}
