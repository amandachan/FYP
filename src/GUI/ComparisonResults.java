package GUI;

import java.util.ArrayList;

public class ComparisonResults {
	private ArrayList trustModelList = null;
	private ArrayList meanList = null;
	private ArrayList varList = null;

	public ComparisonResults() {
		this.trustModelList = new ArrayList();
		this.meanList = new ArrayList();
		this.varList = new ArrayList();
	}

	public ArrayList getTrustModelList() {
		return trustModelList;
	}

	public void setTrustModelList(ArrayList trustModelList) {
		this.trustModelList = trustModelList;
	}

	public ArrayList getMeanList() {
		return meanList;
	}

	public void setMeanList(ArrayList meanList) {
		this.meanList = meanList;
	}
	
	public ArrayList getVarList() {
		return varList;
	}

	public void setVarList(ArrayList varList) {
		this.varList = varList;
	}
}
