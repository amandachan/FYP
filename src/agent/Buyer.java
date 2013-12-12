package agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import weka.core.*;

import main.Product;
import main.Rating;
import main.Transaction;

import defenses.BRS;
import defenses.Defense;
import defenses.eBay;
import distributions.PseudoRandom;
import main.Parameter;

import attacks.AlwaysUnfair;
import attacks.Attack;
import attacks.Camouflage;

public class Buyer extends Agent{

	public HashMap<Integer,Double> MAEList =new HashMap();


	private boolean ishonest;
	private ArrayList<Rating> ratingsToProducts;
	private ArrayList<Product> productsPurchased = new ArrayList<Product>();
	public ArrayList<Seller> sellersRated = new ArrayList<Seller>();
	private ArrayList<Integer> sameRatingH = new ArrayList<Integer>();
	private ArrayList<Integer> sameRatingDH = new ArrayList<Integer>();
	//buyer's current rating (the most recent rating made by the buyer)
	private double currentRating;

	//for building buyer's trust network
	private Vector<Integer> advisors = new Vector<Integer>();
	private ArrayList<Double> trusts = new ArrayList<Double>();
	private double[][] m_SaverTA = new double[2][2];
	private int depthLimit = 4;
	private int neighborSize = 3;
	private ArrayList<Integer> trustNetwork = null;
	private double[] bounds = {0.0, 1.0};
	private double fitness;
	private int TNtype = 1; //0/1/2 means honest trust network/noise/collusive
	private boolean checkDay = false;

	public Buyer (int id){
		for(int i=0; i<ecommerce.getBuyerList().size(); i++){
			sameRatingH.add(0);
			sameRatingDH.add(0);
		}
		this.id = id;

	}
	public ArrayList<Integer> getSameRatingH() {
		return sameRatingH;
	}



	public void setSameRatingH(ArrayList<Integer> sameRatingH) {
		this.sameRatingH = sameRatingH;
	}
	public ArrayList<Integer> getSameRatingDH() {
		return sameRatingDH;
	}



	public void setSameRatingDH(ArrayList<Integer> sameRatingDH) {
		this.sameRatingDH = sameRatingDH;
	}

	public double getCurrentRating() {
		return currentRating;
	}

	public void setCurrentRating(double currentRating) {
		this.currentRating = currentRating;
	}
	

	public Buyer(){


	}

	public boolean isIshonest() {
		return ishonest;
	}

	public void setIshonest(boolean ishonest) {
		this.ishonest = ishonest;
	}

	public ArrayList<Rating> getRatingsToProducts() {
		return ratingsToProducts;
	}

	public void setRatingsToProducts(ArrayList<Rating> ratingsToProducts) {
		this.ratingsToProducts = ratingsToProducts;
	}

	public ArrayList<Product> getProductsPurchased() {
		return productsPurchased;
	}

	public void setProductsPurchased(ArrayList<Product> productsPurchased) {
		this.productsPurchased = productsPurchased;
	}

	public ArrayList<Transaction> getBuyerTransactions(){
		ArrayList<Transaction> buyerTransactions = null;
		return buyerTransactions;
	}

	public ArrayList<Product> getPurchasedProducts(){
		return productsPurchased;
	}

	//give rating
	public void rateSeller(int day){

		//System.out.println("buyer " + ishonest);
		if(this.day > 0){//scan all the history information,
			for (int i = 0; i < history.numInstances(); i++) {
				Instance inst = history.instance(i);
				int dVal = (int) (inst.value(Parameter.m_dayIdx));
				// only complete the current day transaction
				if (dVal != this.day)continue;				
				if (this.ishonest == false) {
					attackModel.setEcommerce(ecommerce);
					currentRating = attackModel.giveUnfairRating(inst);
				} else {
					defenseModel.setEcommerce(ecommerce);
					currentRating = defenseModel.giveFairRating(inst);
					//.println(currentRating);


				}				

				//update sellers' history
				int sVal = (int)(inst.value(Parameter.m_sidIdx));
				listOfSellers.get(sVal).addInstance(new Instance(inst));
			}
		}		
	}

	//set attack model
	public Attack attackSetting(String attackName){
		//Attack attackModel= new AlwaysUnfair();

		try{
			Class<?> class1 = Class.forName("attacks."+attackName.trim());
			Constructor<?> cons = class1.getDeclaredConstructor();
			cons.setAccessible(true);
			attackModel = (Attack) cons.newInstance();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (InstantiationException ex) {
			ex.printStackTrace();
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
		catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		//set the eCommerce
		attackModel.setEcommerce(ecommerce);
		return attackModel;
	}


	//set defense model
	public Defense defenseSetting(String defenseName) throws ClassNotFoundException, NoSuchMethodException, SecurityException{
		//defenseModel=  new eBay();
		try{
			Class class1 = Class.forName("defenses."+defenseName.trim());
			Constructor cons = class1.getDeclaredConstructor();
			cons.setAccessible(true);
			defenseModel = (Defense) cons.newInstance();

		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (InstantiationException ex) {
			ex.printStackTrace();
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
		catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		// set the eCommerce
		//System.out.println("TEST");

		defenseModel.setEcommerce(ecommerce);
		return defenseModel;
	}

	//choose whether to perform attack or defense depending on buyer's honesty
	public void chooseModel(String modelName) throws ClassNotFoundException, NoSuchMethodException, SecurityException{
		if(ishonest == false){
			//dishonest buyer
			attackName = new String(modelName);
			attackModel = attackSetting(modelName);
		} else{
			//honest buyer
			defenseName = new String(modelName);
			defenseModel = defenseSetting(modelName);	
		}
	}

	//randomly choose product that is sold by the chosen seller
	public int chooseProduct(int sellerid){
		Random randomGenerator = new Random();

		//get hashmap of seller id & product id
		HashMap<Integer, ArrayList<Integer>> prodList = new HashMap<Integer, ArrayList<Integer>>();


		//get list of products sold by the seller
		ArrayList<Integer> prodid = new ArrayList<Integer>();
		prodid = prodList.get(sellerid);

		//randomly select product from list of products
		int pid = randomGenerator.nextInt(prodid.size());
		return pid;

	}

	//retrieve product price based on chosen product
	public double buyProduct(int productid){

		//get hashmap of product id and sale price
		HashMap<Integer, Double> priceList = new HashMap<Integer, Double>();

		//get price of product
		double price = priceList.get(productid);
		return price;
	}

//	public Instance addTransaction(int day, Seller s){
//
//		this.day = ecommerce.getDay();
//		int dVal, bVal, sVal=0, productid; 
//		defenseModel.chooseSeller(this, ecommerce);
//		this.day = day;
//		Instances transactions = ecommerce.getM_Transactions();
//		history = transactions;
//		Instances balances = ecommerce.getBalances();
//		double rVal = Parameter.nullRating();
//
//		this.getAccount().credits(s);
//
//		double salePrice;
//		String bHonestVal = "honest";
//
//
//		Product p = s.getProductsOnSale().get(0);
//		Transaction t = new Transaction();
//		rateSeller(day);
//		ecommerce.updateRatings(s, currentRating);
//		t.create(this, s, p, 1, p.getPrice(), day, p.getPrice()*1, currentRating, 1);
//		ecommerce.getTransactionList().add(t);
//		this.account.editBalance(p.getPrice(), t);
//		s.getAccount().addToBalance(p.getPrice());
//		sellersRated.add(s);
//		productsPurchased.add(p);
//		trans.add(t);
//		ecommerce.updateTransactionList(t);
//		sVal = s.getId();
//		s.addSales(day);
//		
//		double sHonestVal = ecommerce.getSellersTrueRating(s.getId());
//		rVal = ecommerce.getM_SellersTrueRating().get(s); //added by neel
//		//add instance, update the array in e-commerce
//		dVal = day + 1;
//		bVal = this.getId();
//
//		//System.out.println(bVal);
//		Instance inst = new Instance(transactions.numAttributes());
//		Instance inst1 = new Instance(balances.numAttributes());
//
//		inst.setDataset(transactions);
//		inst.setValue(Parameter.m_dayIdx, dVal);
//		inst.setValue(Parameter.m_bidIdx, "b" + Integer.toString(bVal)); 
//		inst.setValue(Parameter.m_bHonestIdx, bHonestVal);
//		inst.setValue(Parameter.m_sidIdx, "s" + Integer.toString(sVal));
//		inst.setValue(Parameter.m_sHonestIdx, sHonestVal);			
//		inst.setValue(Parameter.m_ratingIdx, rVal);	
//		this.addInstance(new Instance(inst));
//
//		inst1.setDataset(balances);
//		inst1.setValue(Parameter.m_dayIdx, dVal);
//		inst1.setValue(Parameter.m_bidIdx, "b" + Integer.toString(bVal)); 
//		inst1.setValue(Parameter.m_bbalIdx, this.getAccount().getBalance());
//		inst1.setValue(Parameter.m_pIdx, "p" + Integer.toString(p.getId()));
//		inst1.setValue(Parameter.m_ppriceIdx, p.getPrice());			
//		inst1.setValue(Parameter.m_sidIdx2, sVal);	
//		inst1.setValue(Parameter.m_sHonestIdx2, sHonestVal);			
//		inst1.setValue(Parameter.m_sbalIdx, s.getAccount().getBalance());		
//
//		ecommerce.createData(dVal,Integer.toString(bVal),bHonestVal,Integer.toString(sVal),sHonestVal,currentRating);
//		String shonest="honest";
//		ecommerce.createData2(dVal,Integer.toString(bVal),this.getAccount().getBalance(),Integer.toString(p.getId()),p.getPrice(), Integer.toString(sVal),shonest,s.getAccount().getBalance());
//
//		try{
//			ecommerce.createARFFfile();
//			ecommerce.createBalanceArff();
//		}
//		catch(Exception e){}
//
//		return inst;
//	}
	//create transaction that includes buyer, seller and product
	public Instance addTransaction(int day){
		this.day = ecommerce.getDay();
		int dVal, bVal, sVal=0, productid; 
		Seller s1 = null;
		String bHonestVal = null;
		String shonest = null;

		if (ishonest==false){ //attack
			//select seller and product, then create transaction
			s1 = attackModel.chooseSeller(this);
			bHonestVal = Parameter.agent_dishonest;
		}
		else if (ishonest==true){//defense
			s1 = defenseModel.chooseSeller(this, ecommerce);
			bHonestVal = Parameter.agent_honest;
		}
		this.day = day;
		Instances transactions = ecommerce.getM_Transactions();
		Instances balances = ecommerce.getBalances();
		double rVal = Parameter.nullRating();

		this.getAccount().credits(s1);

		double salePrice;


		Product p = s1.getProductsOnSale().get(0);
		Transaction t = new Transaction();
		rateSeller(day);
		ecommerce.updateRatings(s1, currentRating);
		//Buyer buyer, Seller seller, Product product, int quantity, double price, int day, double amountPaid, double value, int cid
		t.create(this, s1, p, 1, p.getPrice(), day, p.getPrice()*1, currentRating, 1);
		ecommerce.getTransactionList().add(t);
		this.account.editBalance(p.getPrice(), t);
		s1.getAccount().addToBalance(p.getPrice());
		sellersRated.add(s1);
		productsPurchased.add(p);
		trans.add(t);
		ecommerce.updateTransactionList(t);
		sVal = s1.getId();
		if (s1.isIshonest()==true)
			shonest = Parameter.agent_honest;
		else
			shonest = Parameter.agent_dishonest;
		double sHonestVal = ecommerce.getSellersTrueRating(s1.getId());
		rVal = ecommerce.getM_SellersTrueRating().get(s1); //added by neel
		//add instance, update the array in e-commerce
		dVal = day + 1;
		bVal = this.getId();
		Instance inst = new Instance(transactions.numAttributes());
		Instance inst1 = new Instance(balances.numAttributes());

		inst.setDataset(transactions);
		inst.setValue(Parameter.m_dayIdx, dVal);
		inst.setValue(Parameter.m_bidIdx, "b" + Integer.toString(bVal)); 
		inst.setValue(Parameter.m_bHonestIdx, bHonestVal);
		inst.setValue(Parameter.m_sidIdx, "s" + Integer.toString(sVal));
		inst.setValue(Parameter.m_sHonestIdx, sHonestVal);			
		inst.setValue(Parameter.m_ratingIdx, rVal);	
		this.addInstance(new Instance(inst));
		s1.addSales(day);

		inst1.setDataset(balances);
		inst1.setValue(Parameter.m_dayIdx, dVal);
		inst1.setValue(Parameter.m_bidIdx, "b" + Integer.toString(bVal)); 
		inst1.setValue(Parameter.m_bbalIdx, this.getAccount().getBalance());
		inst1.setValue(Parameter.m_pIdx, "p" + Integer.toString(p.getId()));
		inst1.setValue(Parameter.m_ppriceIdx, p.getPrice());			
		inst1.setValue(Parameter.m_sidIdx2, sVal);	
		inst1.setValue(Parameter.m_sHonestIdx2, sHonestVal);			
		inst1.setValue(Parameter.m_sbalIdx, s1.getAccount().getBalance());		
		//this.addInstance(new Instance(inst1));


		ecommerce.createData(dVal,Integer.toString(bVal),bHonestVal,Integer.toString(sVal),sHonestVal,currentRating);
		ecommerce.createData2(dVal,Integer.toString(bVal),this.getAccount().getBalance(),Integer.toString(p.getId()),p.getPrice(), Integer.toString(sVal),shonest,s1.getAccount().getBalance());

		try{
			ecommerce.createARFFfile();
			ecommerce.createBalanceArff();
		}
		catch(Exception e){}
		//HashMap<Seller,Double> MAEList = new HashMap();

	//	System.out.println("Buyer: " + this.getId() + " Seller: " + s1.getId() + " Buyer Balance: " + this.getAccount().getBalance() + " Seller Balnce: " + s1.getAccount().getBalance() + " Balance: " + ecommerce.getBuyerBankBalance().get(this) + " Rating: " + currentRating);
		//   System.out.println("Seller ID: " + s1.getId() + " Buyer ID: " + this.getId() + " Rating: " + currentRating + " Day: " + day + " MAE " + mae);
		return inst;
	}

	public HashMap getMAEList(){
		return this.MAEList;
	}

	//----the below is used to build buyer's trust network of advisors-------------------------------

	public void calculateAverageTrusts(int sid){
		int index = 0; //only 0/1, means dishonest/honest duopoly seller
		if(sid == Parameter.TARGET_HONEST_SELLER){
			index = 1;
		}
		m_SaverTA[index][0] = 0.0;
		m_SaverTA[index][1] = 0.0;
		int numDA = 0; //number of dishonest advisors;
		int numHA = 0; //number of honest advisors;
		int hb = Parameter.NO_OF_HONEST_BUYERS;	
		for(int j = 0; j < advisors.size(); j++){					
			int aid = advisors.get(j);
			if(aid == this.id)continue;
			if(aid < Parameter.NO_OF_DISHONEST_BUYERS || aid >= Parameter.NO_OF_DISHONEST_BUYERS + hb){
				m_SaverTA[index][0] += trusts.get(aid);
				numDA++;
			} 
			else{
				m_SaverTA[index][1] += trusts.get(aid);
				numHA++;
			}
		}				
		if (numDA != 0) {
			m_SaverTA[index][0] /= (numDA);
		}
		if (numHA != 0) {
			m_SaverTA[index][1] /= (numHA);		
		}
	}

	private ArrayList<Integer> maxFastSort(ArrayList<Double> x, int m) {
		trusts.set(this.id, 0.0);
		int len = x.size();
		if(len > Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS){
			len = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS + (day - 1) * Parameter.NO_OF_DISHONEST_BUYERS;
		}
		ArrayList<Integer> idx = new ArrayList<Integer>();
		for (int j = 0; j < len; j++) {
			idx.set(j, j);
		}
		for (int i = 0; i < m; i++) {
			for (int j = i + 1; j < len; j++) {
				if (x.get(idx.get(i)) < x.get(idx.get(j))) {
					int id = idx.get(i);
					idx.set(i, idx.get(j));
					idx.set(j, id);
				} // if
			}
		} // for
		trusts.set(id, 1.0);		
		return idx;
	}

	private void selectReliableNeighbor(){
		//select neighbor with high weight from the witness	
		trusts.set(id, 0.0);		
		ArrayList<Integer> idx = maxFastSort(trusts, neighborSize);
		setTrustNetwork(idx);
		trusts.set(id, 1.0);
	}

	public void resetWitness(int day){
		if(day > 0){
			selectReliableNeighbor();
		}
		advisors.clear();
		int depth = 1;
		buildTrustNet(this, depth, advisors);
	}

	public void setAverageTrusts(int sid, double[] averTA){
		int index = 0; //only 0/1, means dishonest/hoenst duopoly seller
		if(sid == Parameter.TARGET_HONEST_SELLER){
			index = 1;
		}
		m_SaverTA[index][0] = averTA[0];
		m_SaverTA[index][1] = averTA[1];
	}

	public double[] getAverageTrusts(int sid){
		int index = 0; //only 0/1, means dishonest/hoenst duopoly seller
		if(sid == Parameter.TARGET_HONEST_SELLER){
			index = 1;
		}
		return m_SaverTA[index];
	}

	public Buyer getAdvisor(int aid){
		int db = Parameter.NO_OF_DISHONEST_BUYERS;
		int hb = Parameter.NO_OF_HONEST_BUYERS;
		if(aid >= db + hb){
			aid = (aid - (db + hb)) % db;
		}
		return listOfBuyers.get(aid);
	}

	public void setTrustNetwork(ArrayList<Integer> sn){
		for(int i = 0; i < neighborSize; i++){
			trustNetwork.set(i, sn.get(i));
		}
	}

	public ArrayList<Integer> getTrustNetwork(){
		return trustNetwork;
	}

	public void setTrustAdvisor(int aid, double trust){
		if(trusts.size()==0){
			for(int i=0; i<listOfBuyers.size(); i++){
				trusts.add(i, 0.0);
			}
		}
		trusts.set(aid, trust);
	}

	public void setTrusts(ArrayList<Double> ws){
		for(int i = 0; i < trusts.size(); i++){
			trusts.set(i,ws.get(i));
		}
	}

	public ArrayList<Double> getTrusts(){
		return trusts;
	}

	private void findNeighbors(int type){
		//two types = 0/1, from the limit/whole
		ArrayList<Integer> neighbor = new ArrayList<Integer>();
		int numBuyers = listOfBuyers.size(); 	
		if(type == 0){//for dishonest buyers; collusion
			ArrayList<Integer> limit = new ArrayList<Integer>();
			int db = Parameter.NO_OF_DISHONEST_BUYERS;
			int hb = Parameter.NO_OF_HONEST_BUYERS;	
			if(TNtype == 0){//honest trust network
				for (int i = 0; i < numBuyers; i++) {						
					if (i >= db && i < db + hb) {
						limit.add(i);
					}
				}
			} 
			else { //dishonest trust network
				for (int i = 0; i < numBuyers; i++) {
					if (i < db || i >= db + hb) {
						limit.add(i);
					}
				}
			}
			int numLimit = limit.size();	
			for (int i = 0; i < neighborSize; i++) {
				int addID = 0;
				do {
					int rnd = PseudoRandom.randInt(0, numLimit - 1);
					addID = limit.get(rnd);
				} while (neighbor.contains(addID));
				neighbor.add(addID);
				trustNetwork.set(i, addID);
			}
		} 
		else{//for honest buyers
			numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
			for (int i = 0; i < neighborSize; i++) {
				int addID = 0;
				do {
					addID = PseudoRandom.randInt(0, numBuyers- 1);
				} while (neighbor.contains(addID));
				neighbor.add(addID);
				trustNetwork.set(i, addID);
			}
		}		
	}

	private void buildTrustNet(Buyer buyer, int depth, Vector<Integer> advisors){ 
		if(depth > depthLimit)return;			
		ArrayList<Integer> sn = buyer.getTrustNetwork();
		for(int i = 0; i < sn.size(); i++){
			int aid = sn.get(i);				
			if(advisors.contains(aid) == false){
				advisors.add(aid);
				Buyer advisor = buyer.getAdvisor(aid);
				buildTrustNet(advisor, depth + 1, advisors);
			}
		}	
	}

	public void setDepthNeighborSize(int depth, int neighborSize){
		depthLimit = depth;
		this.neighborSize = neighborSize;
	}

	public void InitialTrustNetwork(int snType){
		if(Parameter.includeEA(ecommerce.getDefenseName()) || Parameter.includeWMA(ecommerce.getDefenseName())){
			TNtype = snType;
			//set the trust Network, trusts, and fitness
			trustNetwork = new ArrayList<Integer>();
			int numBuyers = listOfBuyers.size();
			trusts = new ArrayList<Double>();			
			if(this.ishonest == false){
				findNeighbors(0);			
				if (TNtype == 0) { // honest weight and fitness;
					int db = Parameter.NO_OF_DISHONEST_BUYERS;
					int hb = Parameter.NO_OF_HONEST_BUYERS;
					for (int i = 0; i < numBuyers; i++) {
						if (i < db || i >= db + hb) {
							trusts.set(i, 0.0);
						} 
						else {
							trusts.set(i,1.0);
						}					
					}
					trusts.set(id,1.0);
					fitness = 1.0;
				} 
				else if (TNtype == 1) { // noise weight and fitness;
					for (int i = 0; i < numBuyers; i++) {					
						trusts.set(i,PseudoRandom.randDouble(bounds[0], bounds[1]));						
					}
					trusts.add(id,1.0);
					fitness = PseudoRandom.randDouble(0, 1);
				} 
				else {//collusive	
					int db = Parameter.NO_OF_DISHONEST_BUYERS;
					int hb = Parameter.NO_OF_HONEST_BUYERS;
					for (int i = 0; i < numBuyers; i++) {								
						if (i < db || i >= db + hb) {
							trusts.set(i, 1.0);
						} 
						else {
							trusts.set(i, 0.0);
						}
					}
					fitness = 1.0;
				}
			} 
			else{
				findNeighbors(1);
				for (int i = 0; i < numBuyers; i++) {		
					trusts.set(i,PseudoRandom.randDouble(bounds[0], bounds[1]));
				}
				trusts.set(id,1.0);
				fitness = 0.0;
			}		
			//initial witnesses
			advisors = new Vector<Integer>();			
		}
	}

	public Vector<Integer> getAdvisors() {
		return advisors;
	}

	public void setAdvisors(Vector<Integer> advisors) {
		this.advisors = advisors;
	}

	
}
