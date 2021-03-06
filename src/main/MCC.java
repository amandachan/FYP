package main;

import java.util.ArrayList;
import java.util.HashMap;

public class MCC extends EvaluationMetrics{

	private HashMap<Integer, Double> dailyMCChonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyMCCdishonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyFNRhonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyFNRdishonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyAcchonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyAccdishonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyFPRhonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyFPRdishonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyPrechonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyPrecdishonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyFhonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyFdishonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyTPRhonest = new HashMap<Integer,Double>();
	private HashMap<Integer, Double> dailyTPRdishonest = new HashMap<Integer,Double>();

	public MCC(){
		for (int i=0; i<Parameter.NO_OF_DAYS; i++){
			dailyMCChonest.put(i, 0.0);
			dailyMCCdishonest.put(i,0.0);
			dailyFNRhonest.put(i, 0.0);
			dailyFNRdishonest.put(i,0.0);
			dailyAcchonest.put(i, 0.0);
			dailyAccdishonest.put(i,0.0);
			dailyFPRhonest.put(i, 0.0);
			dailyFPRdishonest.put(i,0.0);
			dailyPrechonest.put(i, 0.0);
			dailyPrecdishonest.put(i,0.0);
			dailyFhonest.put(i, 0.0);
			dailyFdishonest.put(i,0.0);
			dailyTPRhonest.put(i, 0.0);
			dailyTPRdishonest.put(i,0.0);
		}
	}
	
	

	public HashMap<Integer, Double> getDailyTPRhonest() {
		return dailyTPRhonest;
	}



	public void setDailyTPRhonest(HashMap<Integer, Double> dailyTPRhonest) {
		this.dailyTPRhonest = dailyTPRhonest;
	}



	public HashMap<Integer, Double> getDailyFhonest() {
		return dailyFhonest;
	}



	public void setDailyFhonest(HashMap<Integer, Double> dailyFhonest) {
		this.dailyFhonest = dailyFhonest;
	}



	public HashMap<Integer, Double> getDailyFdishonest() {
		return dailyFdishonest;
	}



	public void setDailyFdishonest(HashMap<Integer, Double> dailyFdishonest) {
		this.dailyFdishonest = dailyFdishonest;
	}



	public HashMap<Integer, Double> getDailyPrechonest() {
		return dailyPrechonest;
	}



	public void setDailyPrechonest(HashMap<Integer, Double> dailyPrechonest) {
		this.dailyPrechonest = dailyPrechonest;
	}



	public HashMap<Integer, Double> getDailyPrecdishonest() {
		return dailyPrecdishonest;
	}



	public void setDailyPrecdishonest(HashMap<Integer, Double> dailyPrecdishonest) {
		this.dailyPrecdishonest = dailyPrecdishonest;
	}



	public HashMap<Integer, Double> getDailyMCChonest() {
		return dailyMCChonest;
	}

	public void setDailyMCChonest(HashMap<Integer, Double> dailyMCChonest) {
		this.dailyMCChonest = dailyMCChonest;
	}

	public HashMap<Integer, Double> getDailyMCCdishonest() {
		return dailyMCCdishonest;
	}

	public void setDailyMCCdishonest(HashMap<Integer, Double> dailyMCCdishonest) {
		this.dailyMCCdishonest = dailyMCCdishonest;
	}


	public HashMap<Integer, Double> getDailyFNRhonest() {
		return dailyFNRhonest;
	}

	public void setDailyFNRhonest(HashMap<Integer, Double> dailyFNRhonest) {
		this.dailyFNRhonest = dailyFNRhonest;
	}

	public HashMap<Integer, Double> getDailyFNRdishonest() {
		return dailyFNRdishonest;
	}

	public void setDailyFNRdishonest(HashMap<Integer, Double> dailyFNRdishonest) {
		this.dailyFNRdishonest = dailyFNRdishonest;
	}


	public HashMap<Integer, Double> getDailyAcchonest() {
		return dailyAcchonest;
	}

	public void setDailyAcchonest(HashMap<Integer, Double> dailyAcchonest) {
		this.dailyAcchonest = dailyAcchonest;
	}

	public HashMap<Integer, Double> getDailyAccdishonest() {
		return dailyAccdishonest;
	}

	public void setDailyAccdishonest(HashMap<Integer, Double> dailyAccdishonest) {
		this.dailyAccdishonest = dailyAccdishonest;
	}



	public HashMap<Integer, Double> getDailyFPRhonest() {
		return dailyFPRhonest;
	}

	public void setDailyFPRhonest(HashMap<Integer, Double> dailyFPRhonest) {
		this.dailyFPRhonest = dailyFPRhonest;
	}

	public HashMap<Integer, Double> getDailyFPRdishonest() {
		return dailyFPRdishonest;
	}

	public void setDailyFPRdishonest(HashMap<Integer, Double> dailyFPRdishonest) {
		this.dailyFPRdishonest = dailyFPRdishonest;
	}

	private ArrayList<Integer> cofusionMatrix(ArrayList<Double>trustOfAdvisors) {
		// true positive, false negative, false positive, true negative,
		ArrayList<Integer> cmvals = new ArrayList<Integer>();
		for(int i=0; i<4; i++){
			cmvals.add(0);
		}
		for (int k = 0; k < (Parameter.NO_OF_DISHONEST_BUYERS+Parameter.NO_OF_HONEST_BUYERS); k++) {
			int aid = k;
			if (aid >= Parameter.NO_OF_DISHONEST_BUYERS && aid < (Parameter.NO_OF_DISHONEST_BUYERS+Parameter.NO_OF_HONEST_BUYERS)) { // ground truth: honest advisors
				if (trustOfAdvisors.get(aid) > 0.5) // true positive
				{
					cmvals.set(0, cmvals.get(0)+1);	
				}
				else if (trustOfAdvisors.get(aid) < 0.5) // false negative
				{
					cmvals.set(1, cmvals.get(1)+1);					
				}
			} else { // ground truth: dishonest advisors
				if (trustOfAdvisors.get(aid)> 0.5) // false positive
				{
					cmvals.set(2, cmvals.get(2)+1);					
				}
				else if (trustOfAdvisors.get(aid) < 0.5) // true negative
				{
					cmvals.set(3, cmvals.get(3)+1);						
				}
			}
		}
		return cmvals;
	}

	public double calculateMCC(int sid, ArrayList<Double> trustOfAdvisors) {

		double MCC = 0.0;
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;

		for (int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++) {
			if(j!=sid)continue;
			ArrayList<Integer> cvals= cofusionMatrix(trustOfAdvisors);	
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
		} 
		MCC = (tp * tn - fp * fn)
				/ Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
		if (Double.isNaN(MCC)) {
			MCC = -1.0;
		}
		//.out.println("MCC: "+MCC);
		return MCC;
	}

	public double calculateFNR(int sid, ArrayList<Double> trustOfAdvisors){
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;
		for (int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++) {
			if(j!=sid)continue;
			ArrayList<Integer> cvals= cofusionMatrix(trustOfAdvisors);	
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
		} 
		//tp / (fn + tp)
		//System.out.println(tp + " / ( " + fn + " " + tp  + " )");

		double fnr = fn / (fn + tp);
		if (Double.isNaN(fnr)) {
			fnr = 0.0;
		}		
		return fnr;
	}

	public double calculateFPR(int sid, ArrayList<Double> trustOfAdvisors){
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;
		for (int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++) {
			if(j!=sid)continue;
			ArrayList<Integer> cvals= cofusionMatrix(trustOfAdvisors);	
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
		} 
		//tp / (fn + tp)
		double fpr = fp / (tn + fp);
		if (Double.isNaN(fpr)) {
			fpr = 0.0;
		}	
		return fpr;
	}

	public double calculateAccuracy(int sid, ArrayList<Double> trustOfAdvisors){
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;
		for (int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++) {
			if(j!=sid)continue;
			ArrayList<Integer> cvals= cofusionMatrix(trustOfAdvisors);	
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
		} 
		//tp / (fn + tp)
		double acc = (tn + tp) / (tn + fp + fn + tp);
		if (Double.isNaN(acc)) {
			acc = 0.0;
		}	
		return acc;
	}

	public double calculatePrecision(int sid, ArrayList<Double> trustOfAdvisors){
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;
		for (int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++) {
			if(j!=sid)continue;
			ArrayList<Integer> cvals= cofusionMatrix(trustOfAdvisors);	
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
		} 
		
		double prec = tp/ (fp + tp);
		if (Double.isNaN(prec)) {
			prec = 0.0;
		}	
		return prec;
	}
	
	public double calculateF(int sid, ArrayList<Double> trustOfAdvisors){
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;
		for (int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++) {
			if(j!=sid)continue;
			ArrayList<Integer> cvals= cofusionMatrix(trustOfAdvisors);	
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
		} 
		
		double prec = tp/ (fp + tp);
		double recall = tp / (tp+fn);
		double f = 2 * ( (prec*recall) / (prec + recall));
		if (Double.isNaN(f)) {
			f = 0.0;
		}	
		return f;
	}
	
	public double calculateTPR(int sid, ArrayList<Double> trustOfAdvisors){
		double tp, fn, fp, tn;
		tp = fn = fp = tn = 0;
		for (int j = 0; j < (Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS); j++) {
			if(j!=sid)continue;
			ArrayList<Integer> cvals= cofusionMatrix(trustOfAdvisors);	
			tp += cvals.get(0);
			fn += cvals.get(1);
			fp += cvals.get(2);
			tn += cvals.get(3);
		} 

		double tpr =tp / (fn+tp);
		if (Double.isNaN(tpr) ){
			tpr = 0.0;
		}	
		return tpr;
	}
	
	public ArrayList<Double> getDailyMCC(int day){
		ArrayList<Double> dailymcc = new ArrayList<Double>();
		dailymcc.add(dailyMCCdishonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		dailymcc.add(dailyMCChonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		return dailymcc;
	}

	public ArrayList<Double> getDailyFNR(int day){
		ArrayList<Double> dailyfnr = new ArrayList<Double>();
		dailyfnr.add(dailyFNRdishonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		dailyfnr.add(dailyFNRhonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		return dailyfnr;
	}

	public ArrayList<Double> getDailyFPR(int day){
		ArrayList<Double> dailyfpr = new ArrayList<Double>();
		dailyfpr.add(dailyFPRdishonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		dailyfpr.add(dailyFPRhonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		return dailyfpr;
	}

	public ArrayList<Double> getDailyAcc(int day){
		ArrayList<Double> dailyacc = new ArrayList<Double>();
		dailyacc.add(dailyAccdishonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		dailyacc.add(dailyAcchonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		return dailyacc;
	}
	
	public ArrayList<Double> getDailyPrec(int day){
		ArrayList<Double> dailyprec = new ArrayList<Double>();
		dailyprec.add(dailyPrecdishonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		dailyprec.add(dailyPrechonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		return dailyprec;
	}
	
	public ArrayList<Double> getDailyF(int day){
		ArrayList<Double> dailyF = new ArrayList<Double>();
		dailyF.add(dailyFdishonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		dailyF.add(dailyFhonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		return dailyF;
	}
	
	public ArrayList<Double> getDailyTPR(int day){
		ArrayList<Double> dailyTPR = new ArrayList<Double>();
		dailyTPR.add(dailyTPRdishonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		dailyTPR.add(dailyTPRhonest.get(day) / (Parameter.NO_OF_HONEST_BUYERS));
		return dailyTPR;
	}

	public void updateDailyMCC(ArrayList<Double> mccVals, int day){
		dailyMCCdishonest.put(day, dailyMCCdishonest.get(day)+mccVals.get(0));
		dailyMCChonest.put(day,  dailyMCChonest.get(day)+mccVals.get(1));
	}

	public void updateDailyFNR(ArrayList<Double> fnrVals, int day){
		dailyFNRdishonest.put(day, dailyFNRdishonest.get(day)+fnrVals.get(0));
		dailyFNRhonest.put(day,  dailyFNRhonest.get(day)+fnrVals.get(1));
	}

	public void updateDailyFPR(ArrayList<Double> fprVals, int day){
		dailyFPRdishonest.put(day, dailyFPRdishonest.get(day)+fprVals.get(0));
		dailyFPRhonest.put(day,  dailyFPRhonest.get(day)+fprVals.get(1));
	}

	public void updateDailyAcc(ArrayList<Double> accVals, int day){
		dailyAccdishonest.put(day, dailyAccdishonest.get(day)+accVals.get(0));
		dailyAcchonest.put(day,  dailyAcchonest.get(day)+accVals.get(1));
	}
	
	public void updateDailyPrec(ArrayList<Double> accVals, int day){
		dailyPrecdishonest.put(day, dailyPrecdishonest.get(day)+accVals.get(0));
		dailyPrechonest.put(day,  dailyPrechonest.get(day)+accVals.get(1));
	}
	
	public void updateDailyF(ArrayList<Double> accVals, int day){
		dailyFdishonest.put(day, dailyFdishonest.get(day)+accVals.get(0));
		dailyFhonest.put(day,  dailyFhonest.get(day)+accVals.get(1));
	}
	
	public void updateDailyTPR(ArrayList<Double> accVals, int day){
		dailyTPRdishonest.put(day, dailyTPRdishonest.get(day)+accVals.get(0));
		dailyTPRhonest.put(day,  dailyTPRhonest.get(day)+accVals.get(1));
	}
}
