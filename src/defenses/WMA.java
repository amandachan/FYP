package defenses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import main.Parameter;
import agent.Buyer;
import agent.Seller;
import weka.core.Instance;
import weka.core.Instances;
import distributions.PseudoRandom;
import environment.*;

public class WMA extends Defense{

	//epsilon means the |transaction (buyer, seller)|	
	private Buyer b;
	private trustNet tn = null;

	private int depthlimit = Parameter.depthLimit;
	private int neighboursize = Parameter.neighbourLimit;
	//private double[][][] m_BSnumBDU = null;
	//each buyer has it first level of neighbors;
	//private int[][] m_neighborsFirstLevel = null;
	private HashMap<Integer, ArrayList<Integer>> firstlevel = null;


	public void readInstances(){

		//get the number of dishonest/honest buyers;
		//findSEparas(insts.relationName());
		Instances transactions = ecommerce.getM_Transactions();
		totalBuyers = transactions.attribute(Parameter.m_bidIdx).numValues();
		totalSellers = transactions.attribute(Parameter.m_sidIdx).numValues();	
		m_NumInstances = transactions.numInstances();
		trustOfAdvisors = new ArrayList<Double>();
		for(int i=0; i<totalBuyers; i++){
			trustOfAdvisors.add(0.0);
		}

		rtimes = new ArrayList<Double>();
		for(int i=0; i<Parameter.NO_OF_DAYS; i++){
			rtimes.add(0.0);
		}

		totalBuyers = transactions.attribute(Parameter.m_bidIdx).numValues();		
		if(neighboursize > totalBuyers){
			neighboursize = totalBuyers;
		}
		totalSellers = transactions.attribute(Parameter.m_sidIdx).numValues();
		m_NumInstances = transactions.numInstances();
		//initialize the double array;
		//	m_BSR = new int[m_NumBuyers][m_NumSellers][1]; //store the number of rating (B, S)
		//m_BSnumBDU = new double[m_NumBuyers][m_NumSellers][3];		
		for(int i = 0; i < totalBuyers; i++){			
			for(int j = 0; j < totalSellers; j++){				
				m_BSnumBDU[i][j][0] = m_BSnumBDU[i][j][1] = m_BSnumBDU[i][j][2] = 0;
			}
		}
		trustOfAdvisors = new ArrayList<Double>();
		for(int i=0; i<totalBuyers; i++){
			trustOfAdvisors.add(0.0);
		}
		//initial the first level of neighbors
		ArrayList<Integer> neighbour = new ArrayList<Integer>();
		firstlevel = new HashMap<Integer, ArrayList<Integer>>();

		for(int i=0; i<neighboursize; i++){
			neighbour.add(0);
		}
		for(int i=0; i<totalBuyers; i++){
			firstlevel.put(i, neighbour);
		}

		for(int i=0; i<totalBuyers; i++){
			int bid = i;
			for(int k=0; k<neighboursize; k++){
				int aid = PseudoRandom.randInt(0, totalBuyers-1);
				firstlevel.get(bid).set(k, aid);
			}
		}

		//for statistic features
		rtimes = new ArrayList<Double>();
		for(int i=0; i<Parameter.NO_OF_DAYS; i++){
			rtimes.add(0.0);
		}

		//read the instances
		for(int i = 0; i < m_NumInstances; i++){
			Instance inst = transactions.instance(i);
			int bVal = (int)inst.value(Parameter.m_bidIdx);;
			int sVal = (int)inst.value(Parameter.m_sidIdx);
			//translate the ratings to real;
			double rVal = inst.value(Parameter.m_ratingIdx);

			//not null rating;	

			m_BSR[bVal][sVal][0]++;		
			if (rVal >= Parameter.m_omega[1]) {
				m_BSnumBDU[bVal][sVal][0]++;
			} else if (rVal < Parameter.m_omega[0]) {
				m_BSnumBDU[bVal][sVal][1]++;
			} else {
				m_BSnumBDU[bVal][sVal][2]++;
			}
			int dVal = (int)(inst.value(Parameter.m_dayIdx))-1;
			rtimes.set(dVal,  rtimes.get(dVal)+1);
		}
	}


	@Override
	public void calculateReputation1(Buyer buyer1, Seller sid,
			ArrayList<Boolean> trustAdvisors) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Boolean> calculateReputation2(Buyer buyer, Seller sid,
			ArrayList<Boolean> trustAdvisors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calculateTrust(Seller seller, Buyer honestBuyer) {
		readInstances();
		int sid = seller.getId();
		int bid = honestBuyer.getId();		
		double rep_aBS = 0.5;	

		for(int k = 0; k < totalBuyers; k++){
			//initial trust value
			int aid = k;
			trustOfAdvisors.set(aid, 1.0);
			if(m_BSnumBDU[aid][sid][0] + m_BSnumBDU[aid][sid][1] + m_BSnumBDU[aid][sid][2] != 0){
				trustOfAdvisors.set(aid, 0.5);
			}
		}

		tn.update(honestBuyer, seller);       //each buyer know advisors in its trust network

		int depth = 1;
		Vector<Integer> witnesses = new Vector<Integer>();			
		buildTrustNet(honestBuyer, depth, witnesses);
		ArrayList<Double> bdu = new ArrayList<Double>();
		bdu.add(0.0);
		bdu.add(0.0);
		bdu.add(1.0);
		for (int k = 0; k < witnesses.size(); k++) {// initial trust value
			int aid = witnesses.get(k);
			if (aid == bid)continue;//is itself
			if(m_BSR[aid][sid][0] == 0)continue; //no transaction
			if(m_BSnumBDU[aid][sid][0] + m_BSnumBDU[aid][sid][1] + m_BSnumBDU[aid][sid][2] != 0){				
				double[] bdu_AS = m_BSnumBDU[aid][sid];
				double[] wbdu_AS = m_tn.weightBDU(m_trustA[aid], bdu_AS);
				bdu = tn.DStheory(bdu, wbdu_AS);
			}			
		}
		rep_aBS = (bdu.get(0) + bdu.get(2)) / (1.0 + bdu.get(2)));	

		//change to predict reputation with real ratings, code by siweiJiang, 2012-10-08
		double sum_ratings = 0;
		double sum_trust = 0;
		for (int k = 0; k < witnesses.size(); k++) {// initial trust value
			int aid = witnesses.get(k);
			if (aid == bid)continue;
			if(m_BSR[aid][sid][0] == 0)continue;
			sum_ratings += trustOfAdvisors.get(aid)* getRatings(ecommerce.getBuyerList().get(aid), seller);
			sum_trust += trustOfAdvisors.get(aid);
		}
		if (sum_trust == 0.0) {
			rep_aBS = 0.5;
		} else {
			rep_aBS = sum_ratings / sum_trust;
		}

		return rep_aBS;
	}

	@Override
	public Seller chooseSeller(Buyer b, Environment ec) {
		this.ecommerce = ec;
		this.b = b;
		if(day == 0){
			tn = new trustNet(depthlimit);
		}
		int bid = b.getId();
		//System.out.println("in chooseseller method");
		//calculate the trust values on target seller		
		ArrayList<Double> trustValues = new ArrayList<Double>();
		ArrayList<Double> mccValues = new ArrayList<Double>();
		ArrayList<Double> FNRValues = new ArrayList<Double>();
		ArrayList<Double> FPRValues = new ArrayList<Double>();
		ArrayList<Double> accValues = new ArrayList<Double>();
		ArrayList<Double> precValues = new ArrayList<Double>();
		ArrayList<Double> fValues = new ArrayList<Double>();
		ArrayList<Double> TPRValues = new ArrayList<Double>();


		if (trustValues.size()==0){
			for(int i=0; i<2; i++){
				trustValues.add(0.0);
				mccValues.add(0.0);
				FNRValues.add(0.0);
				accValues.add(0.0);
				FPRValues.add(0.0);
				precValues.add(0.0);
				fValues.add(0.0);
				TPRValues.add(0.0);

			}
		}
		Seller s = new Seller();
		for (int k = 0; k < 2; k++) {				
			int sid = Parameter.TARGET_DISHONEST_SELLER;
			if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;

			trustValues.set(k,calculateTrust(honestBuyer.getSeller(sid),honestBuyer));
			mccValues.set(k, ecommerce.getMcc().calculateMCC(sid, trustOfAdvisors));
			FNRValues.set(k, ecommerce.getMcc().calculateFNR(sid, trustOfAdvisors));
			accValues.set(k, ecommerce.getMcc().calculateAccuracy(sid, trustOfAdvisors));
			FPRValues.set(k, ecommerce.getMcc().calculateFPR(sid, trustOfAdvisors));
			precValues.set(k, ecommerce.getMcc().calculatePrecision(sid, trustOfAdvisors));
			fValues.set(k, ecommerce.getMcc().calculateF(sid, trustOfAdvisors));
			TPRValues.set(k, ecommerce.getMcc().calculateTPR(sid, trustOfAdvisors));



		}
		//update the daily reputation difference
		ecommerce.updateDailyReputationDiff(trustValues);
		ecommerce.getMcc().updateDailyMCC(mccValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyFNR(FNRValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyAcc(accValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyFPR(FPRValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyPrec(precValues,ecommerce.getDay());
		ecommerce.getMcc().updateDailyF(fValues,ecommerce.getDay());

		//select seller with the maximum trust values from the two target sellers
		int sellerid = Parameter.TARGET_DISHONEST_SELLER;
		if(trustValues.get(0) < trustValues.get(1)){
			sellerid = Parameter.TARGET_HONEST_SELLER;
		} else if (trustValues.get(0) == trustValues.get(1)){
			sellerid = (PseudoRandom.randDouble() < 0.5)?Parameter.TARGET_DISHONEST_SELLER:Parameter.TARGET_HONEST_SELLER;
		}
		if(PseudoRandom.randDouble() > Parameter.m_honestBuyerOntargetSellerRatio){ 
			sellerid = 1 + (int)(PseudoRandom.randDouble() * (Parameter.TARGET_DISHONEST_SELLER + Parameter.TARGET_HONEST_SELLER - 2));
		}		
		return b.getSeller(sellerid);
	}	

	private class trustNet{

		private int neighboursize;
		private int depthlimit;
		private ArrayList<Double> weights;

		public trustNet(int dl){			

			neighboursize = b.getTrustNetwork().size();
			depthlimit = dl;
			int numBuyers = ecommerce.getBuyerList().size();
			weights = new ArrayList<Double>();
			for(int i=0; i<numBuyers; i++){
				weights.add(1.0);
			}
			b.setTrusts(weights);
		}



		private ArrayList<Double> weightBDU(double w, ArrayList<Double> bdu){

			double sum = bdu.get(0)+ bdu.get(1) + bdu.get(2);
			if(sum != 0){
				bdu.set(0,  bdu.get(0) / sum);
				bdu.set(1, bdu.get(1) / sum);
				bdu.set(2, bdu.get(2) / sum);
			}

			ArrayList<Double> weightbdu = new ArrayList<Double>();
			weightbdu.add(w * bdu.get(0));
			weightbdu.add(w * bdu.get(1));
			weightbdu.add(1.0 - weightbdu.get(0) - weightbdu.get(1));

			return weightbdu;
		}

		private ArrayList<Double> DStheory(ArrayList<Double> bdu_BS, ArrayList<Double> bdu_AS){
			ArrayList<Double> bdu_com = new ArrayList<Double>(); //belief, disbelief, uncertain
			double null_intersection = bdu_BS.get(0) * bdu_AS.get(1) + bdu_BS.get(1)* bdu_AS.get(0); 	//intersection is null
			if(null_intersection == 1.0){
				bdu_com.add(0.0);
				bdu_com.add(0.0);
				bdu_com.add(1.0);
			} else{
				bdu_com.add(bdu_BS.get(0) * bdu_AS.get(0) + bdu_BS.get(0) * bdu_AS.get(2) + bdu_BS.get(2) * bdu_AS.get(0)); //belief combination
				bdu_com.add(bdu_BS.get(1) * bdu_AS.get(1) + bdu_BS.get(1) * bdu_AS.get(2) + bdu_BS.get(2) * bdu_AS.get(1)); //disbelief combination
				bdu_com.add(bdu_BS.get(2) * bdu_AS.get(2)); 												//uncertain combination				
				for(int i = 0; i < 3; i++){
					bdu_com.set(i,  bdu_com.get(i) / (1.0-null_intersection));
				}
			}

			return bdu_com;
		}

		//get the trustworthiness of seller. based on the trust net
		private void buildTrustNet(Buyer buyer, int depth, Vector<Integer> witnesses){ 

			if(depth >= depthlimit)return;			
			ArrayList<Integer> sn = buyer.getTrustNetwork();
			for(int i = 0; i < sn.size(); i++){
				int aid = sn.get(i);				
				if(witnesses.contains(aid) == false){
					witnesses.add(aid);
					int db = Parameter.NO_OF_DISHONEST_BUYERS;
					int hb = Parameter.NO_OF_HONEST_BUYERS;
					if(aid >= db + hb){
						aid = (aid - (db + hb)) % db;
					}					
					Buyer advisor = buyer.getAdvisor(aid);
					buildTrustNet(advisor, depth + 1, witnesses);
				}
			}	
		}

		ArrayList<Integer> maxFastSort(ArrayList<Double> x, int m) {

			int len = x.size();
			ArrayList<Integer> idx = new ArrayList<Integer>();
			for (int j = 0; j < len; j++) {
				idx.add(j);
			}
			for (int i = 0; i < m; i++) {
				for (int j = i + 1; j < len; j++) {

					if(x.get(idx.get(i)) < x.get(idx.get(j))){
						int id = idx.get(i);
						idx.set(i, idx.get(j));
						idx.set(j, id);
					}
				}
			} // for
			return idx;
		}

		//update only advisors in its trust networks
		private void updateWeight(Buyer b, Seller s, Vector<Integer> witnesses){
			int bid = b.getId();
			int sid = s.getId();
			double rho = 0.5;
			double[] bdu_BS = m_BSnumBDU[bid][sid];
			double rep_BS = (bdu_BS.get(0) + bdu_BS.get(2)) / (1.0 + bdu_BS.get(2));
			if(rep_BS >= Parameter.m_omega[1]){
				rho = 1.0;
			} else if(rep_BS < Parameter.m_omega[0]){
				rho = 0.0;
			} else{
				rho = 0.5;
			}

			for(int k = 0; k < witnesses.size(); k++){
				int aid = witnesses.get(k);
				if(aid == bid)continue;
				double[] bdu_AS = m_BSnumBDU[aid][sid];
				double pi_AS = (bdu_AS.get(0) + bdu_AS.get(2)) / (1.0 + bdu_AS.get(2));
				double theta = 1.0 - Math.abs(pi_AS - rho) / 2.0;
				trustOfAdvisors.set(aid, trustOfAdvisors.get(aid) * theta);
			}
		}

		private void selectReliableNeighbor(Buyer b){
			int bid = b.getId();
			//select neighbor with high weight from the witness
			ArrayList<Integer> idx = maxFastSort(trustOfAdvisors, neighboursize);
			for(int k = 0; k < neighboursize; k++){
				firstlevel[bid][k] = idx.get(k);
			}
		}

		public void update(Buyer honestBuyer, Seller s){			

			int depth = 1;
			Vector<Integer> witnesses = new Vector<Integer>();			
			buildTrustNet(honestBuyer, depth, witnesses);
			updateWeight(honestBuyer, s, witnesses);
			tn.selectReliableNeighbor(honestBuyer);
		}

		public ArrayList<Double> getBDU(Buyer b, Seller s){			
			int bid = b.getId();
			int sid = s.getId();
			//get the witness			
			int depth = 1;			
			Vector<Integer> witnesses = new Vector<Integer>();			
			if(day > 0){
				buildTrustNet(b, depth, witnesses);
			}
			//			m_buyer.setAdvisors(witnesses);

			//calculate the bdu values;	
			ArrayList<Double> bdu = new ArrayList<Double>();
			bdu.add(0.0);
			bdu.add(0.0);
			bdu.add(1.0);

			if(witnesses.size() > 0){
				int[][] BSnumR = ((eCommerceR)m_eCommerce).getBuyerSellersNumberRatings();				
				for(int k = 0; k < witnesses.size(); k++){					
					int aid = witnesses.get(k);
					if(BSnumR[aid][sid] >0){
						double[] bdu_AS = ((eCommerceR)m_eCommerce).getBDUBuyerSeller(aid, sid);
						double[] wbdu_AS = weightBDU(m_weights[aid], bdu_AS);
						bdu = DStheory(bdu, wbdu_AS);
					}		
				}	
			}			

			return bdu;
		}

	}

	//get the trustworthiness of seller. based on the trust net
	private void buildTrustNet(Buyer b, int depth, Vector<Integer> witnesses){ 

		if(depth >= depthlimit)return;			

		int[] nfl = m_neighborsFirstLevel[bid];
		for(int k = 0; k < nfl.length; k++){
			int aid = nfl[k];				
			if(witnesses.contains(aid) == false){
				witnesses.add(aid);
				//aid has rating with sid
				if(aid >= totalBuyers){
					aid = (aid - totalBuyers) % dhBuyer;
				} 
				buildTrustNet(ecommerce.getBuyerList().get(aid), depth + 1, witnesses);
			}
		}	
	}




	public double getRatings(Buyer b, Seller s){//play a trick, only for static seller behavior
		int bid = b.getId();
		int sid = s.getId();
		double rep = 0.0;
		double x = sid * 1.0 / (totalSellers - 1);
		//choice 1: uniform distribuiton
		rep = x;

		//choice 2: more sellers get reputation close to 0 and 1;		
		if(x < 0.5){
			rep = 2 * x * x;
		}else{
			rep = 1 - 2 * (x - 1) * (x - 1);
		}

		//choice 3: stepwise line
		double k = 0.5;   //k <= 1
		double porportion = 0.5; //how many are low reputation
		if(x < porportion){
			rep = k * x;
		}else{
			rep = k * (x - 1.0) + 1.0;
		}

		double rVal = rep;

		return rVal;
	}


}

