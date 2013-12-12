package environment;
//import Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import main.*;
import weka.core.converters.*; //for arffsaver

import java.io.*;

import main.Transaction;
import agent.Buyer;
import agent.Seller;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

import weka.core.*;

import java.lang.*;

import distributions.PseudoRandom;

public abstract class Environment {
	public  HashMap<String, Integer> stringToBuyer = new HashMap<String, Integer>();
	public  HashMap<String, Integer> stringToSeller = new HashMap<String, Integer>();

	public  ArrayList<Double> finalRatings = new ArrayList();

	public HashMap<String, Integer> getStringToBuyer() {
		return stringToBuyer;
	}



	public void setStringToBuyer(HashMap<String, Integer> stringToBuyer) {
		this.stringToBuyer = stringToBuyer;
	}



	public HashMap<String, Integer> getStringToSeller() {
		return stringToSeller;
	}



	public void setStringToSeller(HashMap<String, Integer> stringToSeller) {
		this.stringToSeller = stringToSeller;
	}



	public ArrayList<Double> getFinalRatings() {
		return finalRatings;
	}



	public void setFinalRatings(ArrayList<Double> finalRatings) {
		this.finalRatings = finalRatings;
	}

	public FastVector attInfo;
	public FastVector attInfo2;
	public FastVector attBuyer;
	public FastVector attbuyerHonest;
	public FastVector attSeller;
	public FastVector attProduct;
	public FastVector attSellerHonest;
	public Instances data;
	public Instances data2;
	public  ArrayList<String> productID = new ArrayList();
	public  ArrayList<String> userID = new ArrayList();
	public  ArrayList<String> profilename = new ArrayList();
	public  ArrayList<Double> score = new ArrayList();
	public  ArrayList<Integer> date = new ArrayList();
	protected ArrayList<Buyer> buyerList;
	protected ArrayList<Seller> sellerList;
	protected ArrayList<Transaction> transactionList;
	protected ArrayList<Product> productList;

	protected int m_Numdays;                                //days of transactions;
	protected int m_NumBuyers;
	protected int m_NumSellers;
	protected int m_NumRating;                        //binary, multinominal, real
	protected HashMap<Integer, Integer> sameRatingH;
	protected HashMap<Integer, Integer> sameRatingDH;
	protected Instances m_Transactions = null;
	protected Instances balances = null;
	//attack name and defense name
	protected String m_AttackName = null;
	protected String m_DefenseName = null;
	protected HashMap<Seller,Double> m_SellersTrueRating; //added by neel

	protected HashMap<Seller,Double> m_SellersTrueRep;
	protected HashMap<Seller, Double> sellerBankBalance;
	protected HashMap<Buyer, Double> buyerBankBalance;


	protected HashMap<Seller, Double> positiveRatings;
	protected HashMap<Seller, Double> negativeRatings;

	protected ArrayList<ArrayList<Integer>> m_DailySales = new ArrayList<ArrayList<Integer>>();
	protected ArrayList<Integer> m_DailySales_s = new ArrayList<Integer>();

	protected int Day;

	//protected HashMap<Integer, Double> dailyMCCHonestSeller = new HashMap<Integer, Double>();
	protected HashMap<Integer, Double> dailyMCCDishonestSeller = new HashMap<Integer, Double>();
	//previously - m_DailyRep[day][dishonest/honest] = reputation
	protected HashMap<Integer, Double> dailyRepHonestSeller;
	protected HashMap<Integer, Double> dailyRepDishonestSeller;
	protected HashMap<Integer, Double> dailyRepDiffHonestSeller ;
	protected HashMap<Integer, Double> dailyRepDiffDishonestSeller;
	protected 		ArrayList<Double> mccValues;
	protected ArrayList<Double> trustOfAdvisors;
	protected MCC mcc = new MCC();



	public HashMap<Seller, Double> getPositiveRatings() {
		return positiveRatings;
	}



	public void setPositiveRatings(HashMap<Seller, Double> positiveRatings) {
		this.positiveRatings = positiveRatings;
	}



	public HashMap<Seller, Double> getNegativeRatings() {
		return negativeRatings;
	}



	public void setNegativeRatings(HashMap<Seller, Double> negativeRatings) {
		this.negativeRatings = negativeRatings;
	}



	public ArrayList<String> getProductID() {
		return productID;
	}



	public void setProductID(ArrayList<String> productID) {
		this.productID = productID;
	}



	public ArrayList<String> getUserID() {
		return userID;
	}



	public void setUserID(ArrayList<String> userID) {
		this.userID = userID;
	}



	public ArrayList<Double> getScore() {
		return score;
	}



	public void setScore(ArrayList<Double> score) {
		this.score = score;
	}



	public ArrayList<Integer> getDate() {
		return date;
	}



	public void setDate(ArrayList<Integer> date) {
		this.date = date;
	}



	public MCC getMcc() {
		return mcc;
	}



	public void setMcc(MCC mcc) {
		this.mcc = mcc; 
	}



	protected Environment(){           // default constructor

	}



	public ArrayList<Product> getProductList() {
		return productList;
	}



	public void setProductList(ArrayList<Product> productList) {
		this.productList = productList;
	}



	public Instances getBalances() {
		return balances;
	}



	public void setBalances(Instances balances) {
		this.balances = balances;
	}



	public HashMap<Seller, Double> getM_SellersTrueRating() {
		return m_SellersTrueRating;
	}


	public void setM_SellersTrueRating(HashMap<Seller, Double> m_SellersTrueRating) {
		this.m_SellersTrueRating = m_SellersTrueRating;
	}


	Environment(ArrayList<Buyer> buyerList,ArrayList<Seller> sellerList, ArrayList<Transaction> transactionList){
		this.buyerList = buyerList;
		this.sellerList = sellerList;               // maybe from the setUpEnvironment() under CentralAuthority
		this.transactionList = transactionList;     //or else we can invoke each of the set methods below to initialize
	}

	public void setDay(int day){


		if (Day < Parameter.NO_OF_DAYS){
			double val0 = dailyRepDiffDishonestSeller.get(Day);
			double val1 = dailyRepDiffHonestSeller.get(Day);
			dailyRepDiffDishonestSeller.put(Day+1, val0);
			dailyRepDiffHonestSeller.put(Day+1, val1);
		}
		this.Day = day+1;
	}
	public int getDay(){
		return this.Day;
	}

	protected void parameterSetting(String attackName, String defenseName){
		//set the number of dishonest/honest buyers
		int db = Parameter.NO_OF_DISHONEST_BUYERS;
		int hb = Parameter.NO_OF_HONEST_BUYERS;
		Parameter.NO_OF_DISHONEST_BUYERS = (db < hb) ? db : hb;
		Parameter.NO_OF_HONEST_BUYERS = (db > hb) ? db : hb;
		//setting the attack model
		if(Parameter.includeSybil(attackName)){
			Parameter.NO_OF_DISHONEST_BUYERS = (db > hb) ? db : hb;
			Parameter.NO_OF_HONEST_BUYERS = (db < hb) ? db : hb;
		}

		//get the setting from the public file <Para.java>
		m_Numdays = Parameter.NO_OF_DAYS;
		m_NumBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
		m_NumSellers = Parameter.NO_OF_DISHONEST_SELLERS + Parameter.NO_OF_HONEST_SELLERS;

		//    m_SellersTrueRating = new double[m_NumSellers];
		//   m_SellersTrueRep = new double[m_NumSellers];

		//     m_SellersTrueRating = new HashMap();
		//     m_SellersTrueRep = new HashMap();

		//statistic of results
		Day = 0;
		//    m_DailySales = new int[m_Numdays + 1][m_NumSellers];

		//     m_DailyRep = new double[m_Numdays + 1][2];         NO NEED TO INTIALIZE SINCE WE ARE
		//     m_DailyRepDiff = new double[m_Numdays + 1][2];      USING HASHMAPS...WHICH CAN BE DYNAMICALLY
		//     m_DailyMCC = new double[m_Numdays + 1][2];          FILLED!

		m_AttackName = new String(attackName);
		m_DefenseName = new String(defenseName);

		//  System.out.println("entered environment");
	} // parameterSetting()

	public Instances initialInstancesHeader(){

		attInfo = new FastVector();

		//attribute include: [day, p_id, buyer_id, buyer_is_honest, seller_id, seller_is_honest, rating]
		//for day information
		attInfo.addElement(new Attribute(Parameter.dayString));         //for time information, real

		//for buyer/advisor information, string, because the whitewashing problem
		attBuyer = new FastVector();
		/*if(Parameter.includeWhitewashing()){
			//more buyer in database
			m_NumBuyers = m_NumBuyers + Parameter.NO_OF_DAYS * Parameter.NO_OF_DISHONEST_BUYERS;
			for(int i = 0; i < m_NumBuyers; i++){
				String str = "b" + Integer.toString(i);
				attBuyer.addElement(str);
			}
		}else{*/
		for(int i = 0; i < m_NumBuyers; i++){
			String str = "b" + Integer.toString(i);
			attBuyer.addElement(str);
		}

		attInfo.addElement(new Attribute(Parameter.buyerIdString, attBuyer));

		//for buyer/advisor dishonest/honest
		attbuyerHonest = new FastVector();
		attbuyerHonest.addElement(Parameter.agent_dishonest);                               //[dishonest, honest]
		attbuyerHonest.addElement(Parameter.agent_honest);
		attInfo.addElement(new Attribute(Parameter.buyerHonestyString, attbuyerHonest));

		//for sellers id, nominal
		attSeller = new FastVector();                //for seller information, nominal
		for(int i = 0; i < m_NumSellers; i++){
			String str = "s" + Integer.toString(i);
			attSeller.addElement(str);
		}
		attInfo.addElement(new Attribute(Parameter.sellerIdString, attSeller));

		//for seller dishonest/honest
		//      FastVector attSellerHonest = new FastVector();
		//      attSellerHonest.addElement(Parameter.agent_dishonest);
		//      attSellerHonest.addElement(Parameter.agent_honest);
		//      attInfo.addElement(new Attribute(Parameter.m_sHonestStr, attSellerHonest));
		//for seller dishonest/honest = true rating, more general type is real according to rating type
		attInfo.addElement(new Attribute(Parameter.sellerHonestyString));

		//for rating, nominal
		//      if(Parameter.RATING_TYPE.compareTo("binary") == 0){
		//          FastVector attRating = new FastVector();                                       //for rating information, nominal
		//          for(int i = 0; i < Parameter.RATING_BINARY.length; i++){
		//              attRating.addElement(Integer.toString(Parameter.RATING_BINARY[i]));
		//          }
		//          attInfo.addElement(new Attribute(Parameter.m_ratingStr, attRating));
		//      }
		//for rating nominal, real
		attInfo.addElement(new Attribute(Parameter.ratingString));
		//	attInfo.addElement(new Attribute(Parameter.ra))

		String instsName = new String("eCommerce.arff");
		Instances header = new Instances(instsName, attInfo, m_Numdays * (m_NumBuyers));
		//set the class index

		//      header.setSellerIndex(header.numAttributes() - 1);

		data=new Instances("eCommerce.arff",attInfo,0);

		return header;
	}

	//FOR BUYER / SELLER BALANCE INFO
	public Instances initialInstancesHeader2(){
		//System.out.println("product size" + productList.size());

		System.out.println("enters instance header 2");
		attInfo2 = new FastVector();
		//attribute include: [day, p_id, buyer_id, buyer_is_honest, seller_id, seller_is_honest, rating]
		//for day information
		attInfo2.addElement(new Attribute(Parameter.dayString));         //for time information, real

		//for buyer/advisor information, string, because the whitewashing problem

		attBuyer = new FastVector();
		for(int i = 0; i < m_NumBuyers; i++){
			String str = "b" + Integer.toString(i);
			attBuyer.addElement(str);
		}

		attInfo2.addElement(new Attribute(Parameter.buyerIdString, attBuyer));


		attInfo2.addElement(new Attribute(Parameter.buyerBalString));

		attProduct = new FastVector();               
		for(int i = 0; i < m_NumSellers; i++){
			String str = "p" + Integer.toString(i);
			attProduct.addElement(str);
		}
		attInfo2.addElement(new Attribute(Parameter.productString, attProduct));

		attInfo2.addElement(new Attribute(Parameter.salePriceString));


		attSeller = new FastVector();
		for(int i = 0; i < m_NumSellers; i++){
			String str = "s" + Integer.toString(i);
			attSeller.addElement(str);
		}
		attInfo2.addElement(new Attribute(Parameter.sellerIdString, attSeller));

		//for seller dishonest/honest = true rating, more general type is real according to rating type
		attSellerHonest = new FastVector(); 				
		attSellerHonest.addElement(Parameter.agent_dishonest);                               //[dishonest, honest]
		attSellerHonest.addElement(Parameter.agent_honest);

		attInfo2.addElement(new Attribute(Parameter.sellerHonestyString, attSellerHonest));

		attInfo2.addElement(new Attribute(Parameter.sellerBalString));

		//	attInfo2.addElement(new Attribute(Parameter.sellerBalString));

		String instsName = new String("eCommerce2.arff");
		Instances header = new Instances(instsName, attInfo2, m_Numdays * (m_NumBuyers+m_NumSellers)*2);
		//set the class index
		//      header.setSellerIndex(header.numAttributes() - 1);

		data2=new Instances("eCommerce2.arff",attInfo2,0);

		return header;
	}

	public void assignTruth(){


		//  System.out.println("enters assign truth");
		System.out.println("is seller list empty?  "+this.sellerList.size()+ this.buyerList.size());
		//assign the true rating for sellers
		if(Parameter.RATING_TYPE.compareTo("binary") == 0){
			for(int i = 0; i < Parameter.NO_OF_DISHONEST_SELLERS; i++){
				m_SellersTrueRating.put(sellerList.get(i),-1.0);
				m_SellersTrueRep.put(sellerList.get(i), 0.0);
				//	System.out.println("the true reputation "+m_SellersTrueRep.get(sellerList.get(i)));
			}

			/* for(int i = 0; i < Parameter.NO_OF_DISHONEST_SELLERS; i++){    COMMENTED BY NEEL
                //dishonest seller, rating = -1
                m_SellersTrueRating[i] = Parameter.RATING_BINARY[0];
                m_SellersTrueRep[i] = 0.0;
            }*/

			for(int i = Parameter.NO_OF_DISHONEST_SELLERS; i < m_NumSellers; i++){
				m_SellersTrueRating.put(this.sellerList.get(i),1.0);
				m_SellersTrueRep.put(this.sellerList.get(i),1.0);
			}
			/*  for(int i = Parameter.NO_OF_DISHONEST_SELLERS; i < m_NumSellers; i++){   COMMENTED BY NEEL
                //honest seller, rating = 1
                m_SellersTrueRating[i] = Parameter.RATING_BINARY[2];
                m_SellersTrueRep[i] = 1.0;
            }*/

		}
		else if(Parameter.RATING_TYPE.equalsIgnoreCase("multinomial")){

			int interval = m_NumSellers / (Parameter.RATING_MULTINOMINAL.length - 1);
			double[] trueRep = new double[]{0, 0.25, 0.5, 0.75, 1.0};
			int halfPos = Parameter.RATING_MULTINOMINAL.length / 2;
			for(int i = 0; i < m_NumSellers; i++){
				int ratingIdx = i / interval;
				if(ratingIdx >= halfPos)ratingIdx++;
				//[1..1, 2...,2, 4...4, 5...5]

				m_SellersTrueRating.put(this.sellerList.get(i),(double)Parameter.RATING_MULTINOMINAL[ratingIdx]); //need to check the casting...not sure!!
				m_SellersTrueRep.put(this.sellerList.get(i),trueRep[ratingIdx]);

				// m_SellersTrueRating[i] = Parameter.RATING_MULTINOMINAL[ratingIdx];
				// m_SellersTrueRep[i] = trueRep[ratingIdx];
			} //for loop


			/*  else if(Parameter.RATING_TYPE.compareTo("multinominal") == 0){
            int interval = m_NumSellers / (Parameter.RATING_MULTINOMINAL.length - 1);
            double[] trueRep = new double[]{0, 0.25, 0.5, 0.75, 1.0};
            int halfPos = Parameter.RATING_MULTINOMINAL.length / 2;
            for(int i = 0; i < m_NumSellers; i++){
                int ratingIdx = i / interval;
                if(ratingIdx >= halfPos)ratingIdx++;
                //[1..1, 2...,2, 4...4, 5...5]
                m_SellersTrueRating[i] = Parameter.RATING_MULTINOMINAL[ratingIdx];
                m_SellersTrueRep[i] = trueRep[ratingIdx];
            } */
		}

		else if(Parameter.RATING_TYPE.equalsIgnoreCase("real")){

			for(int i = 0; i < m_NumSellers; i++){
				if(i < m_NumSellers/2){
					double interval = (Parameter.m_omega[0] - Parameter.RATING_REAL[0]) / (m_NumSellers/2 - 1);
					m_SellersTrueRating.put(this.sellerList.get(i),(i*interval));
					//m_SellersTrueRating[i] = i * interval;
					m_SellersTrueRep.put(this.sellerList.get(i),(i*interval));
					//m_SellersTrueRep[i] = i * interval;
				} else{
					double interval = (Parameter.RATING_REAL[1] - Parameter.m_omega[1]) / (m_NumSellers/2 - 1);
					m_SellersTrueRating.put(this.sellerList.get(i),(Parameter.m_omega[1] + (i - m_NumSellers/2) * interval));
					//m_SellersTrueRating[i] = Parameter.m_omega[1] + (i - m_NumSellers/2) * interval;
					m_SellersTrueRep.put(this.sellerList.get(i),(Parameter.m_omega[1] + (i - m_NumSellers/2) * interval));
					// m_SellersTrueRep[i] = Parameter.m_omega[1] + (i - m_NumSellers/2) * interval;
				}
			}
		}

		/*else if(Parameter.RATING_TYPE.compareTo("real") == 0){
            for(int i = 0; i < m_NumSellers; i++){
                if(i < m_NumSellers/2){
                    double interval = (Parameter.m_omega[0] - Parameter.RATING_REAL[0]) / (m_NumSellers/2 - 1);
                    m_SellersTrueRating[i] = i * interval;
                    m_SellersTrueRep[i] = i * interval;
                } else{
                    double interval = (Parameter.RATING_REAL[1] - Parameter.m_omega[1]) / (m_NumSellers/2 - 1);
                    m_SellersTrueRating[i] = Parameter.m_omega[1] + (i - m_NumSellers/2) * interval;
                    m_SellersTrueRep[i] = Parameter.m_omega[1] + (i - m_NumSellers/2) * interval;
                }

            }
        }*/

		else{
			System.out.println("no such type of rating existent");
		}
	} // assignTruth()

	public void agentSetting(String attackName, String defenseName) throws ClassNotFoundException, NoSuchMethodException, SecurityException{

		sellerBankBalance = new HashMap<Seller, Double>();
		buyerBankBalance = new HashMap<Buyer, Double>();


		positiveRatings = new HashMap<Seller, Double>();
		negativeRatings = new HashMap<Seller, Double>();

		dailyMCCDishonestSeller = new HashMap<Integer, Double>();
		dailyRepHonestSeller = new HashMap<Integer, Double>();
		dailyRepDishonestSeller = new HashMap<Integer, Double>();
		dailyRepDiffHonestSeller = new HashMap<Integer, Double>();
		dailyRepDiffDishonestSeller = new HashMap<Integer, Double>();
		mccValues = new ArrayList<Double>();
		trustOfAdvisors = new ArrayList<Double>();
		mcc = new MCC();
		sameRatingH = new HashMap<Integer, Integer>();
		sameRatingDH = new HashMap<Integer, Integer>();

		buyerList = new ArrayList<Buyer>();
		sellerList = new ArrayList<Seller>();
		transactionList = new ArrayList<Transaction>();
		productList = new ArrayList<Product>();

		m_SellersTrueRating = new HashMap<Seller, Double>();
		m_SellersTrueRep = new HashMap<Seller, Double>();
		Day =0;
		for(int i=0; i<Parameter.NO_OF_DAYS; i++){
			dailyRepDishonestSeller.put(i, 0.0);
			dailyRepHonestSeller.put(i,0.0);
			dailyRepDiffDishonestSeller.put(i,0.0);
			dailyRepDiffHonestSeller.put(i,0.0);

		}

		//	System.out.println("enters agent Setting");

		int numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;
		int numSellers = Parameter.NO_OF_DISHONEST_SELLERS+Parameter.NO_OF_HONEST_SELLERS;
		for(int k = 0; k < numSellers; k++){
			int sid = k;
			if(sid < Parameter.NO_OF_DISHONEST_SELLERS){

				Seller s = new Seller();
				s.setId(sid);s.setIshonest(false);
				s.setEcommerce(this);

				sellerList.add(s);

			} else{

				Seller s = new Seller();
				s.setId(sid);s.setIshonest(true);
				s.setEcommerce(this);

				sellerList.add(s);

			}
		}
		int bid=0;
		for(int p = 0; p < numBuyers; p++){
			//System.out.println(numBuyers);
			bid = p; 
			if(bid < Parameter.NO_OF_DISHONEST_BUYERS){
				//	System.out.println(Parameter.NO_OF_DISHONEST_BUYERS);

				//int id, boolean ishonest, String attackName, String defenseName, Environment ecommerce
				Buyer b = new Buyer();
				//	System.out.println(Parameter.NO_OF_DISHONEST_BUYERS);

				b.setId(bid);				
				b.setAttackName(attackName);
				b.attackSetting(attackName);
				b.setIshonest(false); 
				b.setEcommerce(this);
				buyerList.add(b);

			}
			else{
				Buyer b = new Buyer();
				b.setId(bid);
				b.setIshonest(true);
				b.setDefenseName(defenseName);
				b.defenseSetting(defenseName);
				//System.out.println("TEST");

				b.setEcommerce(this);
				buyerList.add(b);
			}
		}

	
		for(int i=0; i<numBuyers; i++){
			buyerList.get(i).setListOfBuyers(buyerList);
			buyerList.get(i).setListOfSellers(sellerList);
		}
		for(int i=0; i<numSellers; i++){
			sellerList.get(i).setListOfBuyers(buyerList);
			sellerList.get(i).setListOfSellers(sellerList);
		}

		for(int i=0; i<sellerList.size(); i++){
			Product p = new Product();
			p.setId(i);
			p.setPrice(i);
			p.setS(sellerList.get(i));
			productList.add(p); 
			sellerList.get(i).addProductToList(p);
		}
		//System.out.println("HELLO PRODCUT LIST " + productList.size());
		for(int i=0; i<productList.size(); i++){
			productList.get(i).setListOfProducts(productList);
		}
		//System.out.println(buyerList.size() + sellerList.size());
	} //agentSetting

	//Instances data=new Instances("eCommerce.arff",attInfo,0);
	public void createData(int dVal,String bVal,String bHonestVal,String sVal,double sHonestVal,double rVal){
		//  Instances data=new Instances("eCommerce.arff",attInfo,0);
		//  System.out.println("enters create data");

		double vals[] = new double[data.numAttributes()];
		//     System.out.println("enters create data"+vals.length);

		vals[0]=dVal;
		vals[1]=attBuyer.indexOf("b"+bVal);
		vals[2]=attbuyerHonest.indexOf(bHonestVal);
		vals[3]=attSeller.indexOf("s"+sVal);
		vals[4]=sHonestVal;
		vals[5]=rVal;
		//vals[6]=currentRating;
		data.add(new Instance(1.0,vals));

		//System.out.println(data);
	}


	public void createData2(int dVal,String bVal,double buyerBal,String product,double saleprice, String sVal, String ishonest, double sellerBal){

		double vals[] = new double[data2.numAttributes()];
		vals[0]=dVal;
		vals[1]=attBuyer.indexOf("b"+bVal);
		vals[2]=buyerBal;
		vals[3]=attProduct.indexOf("p"+product);
		vals[4]=saleprice;
		vals[5]=attSeller.indexOf("s"+sVal);
		vals[6]=attSellerHonest.indexOf(ishonest);
		vals[7]=sellerBal;
		//System.out.println(ishonest);
		//vals[6]=currentRating;
		data2.add(new Instance(1.0,vals));
		//	System.out.println(data2.numInstances());
		//System.out.println(data2);

		//System.out.println(data);
	}

	public void createARFFfile()throws Exception{
		ArffSaver  saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File("data/FYP.arff"));

		//       saver.setFile(new File("/Users/chanamanda/FYP.arff"));
		//	saver.setDestination(new File("c:/Users/NEEL/Desktop/FYP.arff"));
		saver.writeBatch();
	}

	public void createBalanceArff()throws Exception{


		ArffSaver saver2 = new ArffSaver();
		saver2.setInstances(data2);
		saver2.setFile(new File("data/Balance.arff"));
		//       saver.setFile(new File("/Users/chanamanda/FYP.arff"));
		//	saver.setDestination(new File("c:/Users/NEEL/Desktop/FYP.arff"));
		saver2.writeBatch();
	}

	public String getDefenseName(){
		return m_DefenseName;
	}

	public Environment getEnvironment(){
		return this;
	}


	public void AddBuyerToList(Buyer buyer){   //dynamic way of adding a buyer to the list
		buyerList.add(buyer);
	}
	public void AddSellerToList(Seller seller){  //dynamic way of adding seller to the list
		sellerList.add(seller);
	}
	public void AddTransactionToList(Transaction transaction){
		transactionList.add(transaction);
	}
	public int getBuyerListSize(){
		return buyerList.size();
	}
	public int getSellerListSize(){
		return sellerList.size();
	}
	public int getTransactionListSize(){
		return transactionList.size();
	}
	public void setBuyerList(ArrayList<Buyer> buyerList){
		this.buyerList = buyerList;
	}
	public void setSellerList(ArrayList<Seller> sellerList){
		this.sellerList = sellerList;
	}
	public void setTransactionList(ArrayList<Transaction> transactionList){
		this.transactionList = transactionList;
	}
	public ArrayList<Buyer> getBuyerList(){
		return buyerList;
	}
	public ArrayList<Seller> getSellerList(){
		return sellerList;
	}
	public ArrayList<Transaction> getTransactionList(){
		return transactionList;
	}

	public Instances getM_Transactions() {
		return m_Transactions;
	}

	public void setM_Transactions(Instances m_Transactions) {
		this.m_Transactions = m_Transactions;
	}
	public abstract void eCommerceSetting(String attackName,String defenseName);
	abstract Instances generateInstances();

	abstract void importConfigSettings();
	abstract void createEnvironment();
	abstract void saveWekaInstances();


	//--- added by Amanda, needed for buyer class -------------
	//to get true rating of seller from hashmap
	public double getSellersTrueRating(int s1){
		return m_SellersTrueRating.get( sellerList.get(s1));

	}
	//for MAE calculation
	public double getSellersTrueRep(int s1){

		//   System.out.println( m_SellersTrueRep.size());
		return m_SellersTrueRep.get( sellerList.get(s1));

	}

	public void updateTransactionList(Transaction t){
		transactionList.add(t);
	}
	public HashMap getSellersTrueRepMap(){
		return m_SellersTrueRep;
	}

	public void updateDailyReputationDiff(ArrayList<Double> trustVals){
		int sidHonest = Parameter.TARGET_HONEST_SELLER;
		int sidDishonest = Parameter.TARGET_DISHONEST_SELLER;
		if (m_DefenseName.matches("eBay")){
			double truehrep = positiveRatings.get(sellerList.get(sidHonest)) + negativeRatings.get(sellerList.get(sidHonest));
			double truedhrep = positiveRatings.get(sellerList.get(sidDishonest)) + negativeRatings.get(sellerList.get(sidDishonest));
			double diff0 = Math.abs(truedhrep - trustVals.get(0));
			double diff1 = Math.abs(truehrep - trustVals.get(1));
			dailyRepDiffDishonestSeller.put(Day, diff0);
			dailyRepDiffHonestSeller.put(Day, diff1);
		}
		else {
			//TODO ERROR HERE
			double new0 = dailyRepDishonestSeller.get(Day) + trustVals.get(0);
			dailyRepDishonestSeller.put(Day, new0);
			double new1 = dailyRepHonestSeller.get(Day) + trustVals.get(1);
			dailyRepHonestSeller.put(Day,  new1);
			double diff0 = dailyRepDiffDishonestSeller.get(Day) + Math.abs(m_SellersTrueRep.get(sellerList.get(sidDishonest)) - trustVals.get(0));
			dailyRepDiffDishonestSeller.put(Day, diff0);
			double diff1 = dailyRepDiffHonestSeller.get(Day) + Math.abs(m_SellersTrueRep.get(sellerList.get(sidHonest)) - trustVals.get(1));
			dailyRepDiffHonestSeller.put(Day, diff1);
		}
	}



	public ArrayList<Double> getDailyReputation(int day){
		ArrayList<Double> dr = new ArrayList<Double>();

		double val0 = dailyRepDishonestSeller.get(day) / (Parameter.NO_OF_HONEST_BUYERS);
		double val1 = dailyRepHonestSeller.get(day) / (Parameter.NO_OF_HONEST_BUYERS);
		dr.add(0, val0);
		dr.add(1, val1);
		return dr;
	}

	public ArrayList<Double> getDailyRepDiff(){
		ArrayList<Double> drd = new ArrayList<Double>();

		double val0 = dailyRepDiffDishonestSeller.get(Day) / ((Day+1) * Parameter.NO_OF_HONEST_BUYERS);
		double val1 = dailyRepDiffHonestSeller.get(Day) / ((Day+1) * Parameter.NO_OF_HONEST_BUYERS);
		drd.add(0, val0);
		drd.add(1,val1);

		return drd;
	}



	public ArrayList<Double> getMccValues() {
		return mccValues;
	}



	public void setMccValues(ArrayList<Double> mccValues) {
		this.mccValues = mccValues;
	}



	public ArrayList<Double> getTrustOfAdvisors() {
		return trustOfAdvisors;
	}



	public void setTrustOfAdvisors(ArrayList<Double> trustOfAdvisors) {
		this.trustOfAdvisors = trustOfAdvisors;
	}

	public void updateRatings(Seller s, double rating){
		if (rating ==-1.0)
			negativeRatings.put(s, negativeRatings.get(s)+1);
		else if(rating==1.0)
			positiveRatings.put(s, positiveRatings.get(s)+1);

	}

	//add by Shi Qing
	public void updateDailySales(int sid){		

		int curr_DailySales=m_DailySales.get(this.Day).get(sid);
		m_DailySales.get(this.Day).set(sid,curr_DailySales+1);
		//m_DailySales[m_Day][sid]++;
	}

	public ArrayList<ArrayList<Integer>> getDailySales(){		

		return m_DailySales;
	}

	public void updateArray(Instance inst) {
		// when insert one instance, the array is change

		int bVal = (int)inst.value(Parameter.m_bidIdx);;
		int sVal = (int)inst.value(Parameter.m_sidIdx);
		//translate the ratings to binary;
		int rVal = translate2BinaryRating(inst.value(Parameter.m_ratingIdx));		



		/***personalized approach: update information***/
		if(Parameter.includePersonalized(m_DefenseName) ){	
			int dayInterval = (int)(Parameter.NO_OF_DAYS / Parameter.m_timewindows);
			int dVal_B = (int) inst.value(Parameter.m_dayIdx);   
			int bVal_B = bVal;
			int sVal_B = sVal;
			int rVal_B = rVal;
			if (dayInterval ==0) dayInterval = 1;
			int tw_B = dVal_B / dayInterval;
			if (rVal_B == Parameter.RATING_BINARY[1]) {
				return;
			}
			//only use the recently ratings
			ArrayList<Boolean> advisorscanned = new ArrayList<Boolean>();
			for(int i=0; i<m_NumBuyers; i++){
				advisorscanned.add(false);
			}
			//sort the instances by data, ascending;
			//m_Transactions.sort(Para.m_dayIdx);
			for (int j = m_Transactions.numInstances() - 1; j >=0 ; j--) {
				Instance inst_A = m_Transactions.instance(j);				
				int dVal_A = (int) inst_A.value(Parameter.m_dayIdx);
				int bVal_A = (int) inst_A.value(Parameter.m_bidIdx);
				int sVal_A = (int) inst_A.value(Parameter.m_sidIdx);
				//translate the ratings to binary;
				int rVal_A = translate2BinaryRating(inst_A.value(Parameter.m_ratingIdx));						
				int tw_A = dVal_A / dayInterval;
				if(rVal_A == Parameter.RATING_BINARY[1] || advisorscanned.get(bVal_A))continue;
				//same seller, same time window, prior to buyer day 
				if (sVal_B != sVal_A || tw_B != tw_A  || dVal_A > dVal_B)continue;				
				if (rVal_B != rVal_A) { 
					buyerList.get(bVal_B).getSameRatingDH().set(bVal_A, buyerList.get(bVal_B).getSameRatingDH().get(bVal_A)+1);
					buyerList.get(bVal_A).getSameRatingDH().set(bVal_B, buyerList.get(bVal_A).getSameRatingDH().get(bVal_B)+1);

				} else {
					buyerList.get(bVal_B).getSameRatingH().set(bVal_A, buyerList.get(bVal_B).getSameRatingH().get(bVal_A)+1);
					buyerList.get(bVal_A).getSameRatingH().set(bVal_B, buyerList.get(bVal_A).getSameRatingH().get(bVal_B)+1);

				}
				advisorscanned.set(bVal_A,  true);
			}
		}	
		/***personalized approach: update information***/


	}
	public int translate2BinaryRating(double originalRating){

		int rVal = (int)originalRating;
		if(Parameter.RATING_TYPE.compareTo("multinominal") == 0){
			rVal = Parameter.rating_multinominal2binary(originalRating);
		}else if(Parameter.RATING_TYPE.compareTo("real") == 0){
			rVal = Parameter.rating_real2binary(originalRating);			
		}

		return rVal;
	}

	private int translate2MultinominalRating(double originalRating){

		int rVal = (int)originalRating;
		if(Parameter.RATING_TYPE.compareTo("binary") == 0){			
			rVal = Parameter.rating_binary2multinominal(originalRating);
		} else if(Parameter.RATING_TYPE.compareTo("real") == 0){
			rVal = Parameter.rating_real2multinominal(originalRating);			
		}

		return rVal;
	}
	/*
	private int m_mnrLen = Parameter.RATING_MULTINOMINAL.length;
	private ArrayList<Double> m_trustA;
	//private int m_NumBuyers;
	//private int m_NumSellers;
	private ArrayList<Integer> neg_SR;
	private ArrayList<ArrayList<Integer>> neg_BSR;
	private ArrayList<Integer> pos_SR;
	private ArrayList<ArrayList<Integer>> pos_BSR;
	private ArrayList<Double> m_Rtimes;
	private ArrayList<ArrayList<Integer>> BS_currentRating;
	private ArrayList<Integer> S_currentRating;
	public void updateArrayList(Instance inst) {
		// when insert one instance, the array is change

		if (Parameter.RATING_TYPE.compareTo("binary") == 0) {
			int bVal = (int)inst.value(Parameter.m_bidIdx);;
			int sVal = (int)inst.value(Parameter.m_sidIdx);
			//translate the ratings to binary;
			int rVal = translate2BinaryRating(inst.value(Parameter.m_ratingIdx));		
			if (rVal == Parameter.RATING_BINARY[0]) {
				m_BSR[bVal][sVal][0]++;
				m_SR[sVal][0]++;	
				m_DailySales[m_Day][sVal]++;
			} else if (rVal == Parameter.RATING_BINARY[1]) {			
				// nothing need to do;
			} else if (rVal == Parameter.RATING_BINARY[2]) {
				m_BSR[bVal][sVal][1]++;
				m_SR[sVal][1]++;
				m_DailySales[m_Day][sVal]++;
			}	
	 */

	/***TRAVOS: update information for travos***/
	/*
			if(Parameter.includeTRAVOS(m_DefenseName)){
				m_BS_currRating[bVal][sVal] = rVal;
			}
		}

		int bVal = (int)inst.value(Parameter.m_bidIdx);;
		int sVal = (int)inst.value(Parameter.m_sidIdx);
		//translate the ratings to multinominal;
		int rVal = translate2MultinominalRating(inst.value(Parameter.m_ratingIdx));	
		if(rVal != Parameter.RATING_MULTINOMINAL[m_mnrLen/2]){
			m_BSR[bVal][sVal][rVal - 1]++;	
			m_DailySales[m_Day][sVal]++;	
		}

		//get the number of dishonest/honest buyers;
				//	findSEparas(insts.relationName());
				Instances transactions = ecommerce.getM_Transactions();
				m_NumBuyers = transactions.attribute(Parameter.m_bidIdx).numValues();
				//System.out.println("=====m_NumBuyers====="+m_NumBuyers);
				m_NumSellers = transactions.attribute(Parameter.m_sidIdx).numValues();	
				m_NumInstances = transactions.numInstances();
				//m_trustA = new double[m_NumBuyers];   //the initial trust values is 0;
				m_trustA = new ArrayList<Double>();
				neg_SR = new ArrayList<Integer>();
				neg_BSR = new ArrayList<ArrayList<Integer>>();
				pos_SR = new ArrayList<Integer>();
				pos_BSR = new ArrayList<ArrayList<Integer>>();

				//m_BSR = new int[m_NumBuyers][m_NumSellers][2];
				//m_BS_currRating = new int[m_NumBuyers][m_NumSellers];
				BS_currentRating = new ArrayList<ArrayList<Integer>>();
				S_currentRating = new ArrayList<Integer>();
				//for statistic features
				//m_Rtimes = new double[Parameter.NO_OF_DAYS];
				m_Rtimes = new ArrayList<Double>();

				for(int b=0; b < m_NumBuyers; b++){
					for (int s=0; s < m_NumSellers; s++){
						neg_SR.add(0);
						pos_SR.add(0);
						S_currentRating.add(0);
					}
					neg_BSR.add(neg_SR);
					pos_BSR.add(pos_SR);
					BS_currentRating.add(S_currentRating);
				}

				for(int d=0; d< Parameter.NO_OF_DAYS; d++ ){
					m_Rtimes.add(0.0);
				}

				//read the instances
				for(int i = 0; i < m_NumInstances; i++){
					Instance inst = transactions.instance(i);
					int bVal = (int)(inst.value(Parameter.m_bidIdx));
					int sVal = (int)(inst.value(Parameter.m_sidIdx));
					double rVal = inst.value(Parameter.m_ratingIdx);	
					if (rVal == Parameter.RATING_BINARY[0]) {//-1
						//m_BSR[bVal][sVal][0]++;
						neg_BSR.get(bVal).set(sVal, neg_BSR.get(bVal).get(sVal)+1);
					} else if (rVal == Parameter.RATING_BINARY[1]) {			
						// nothing need to do;
					} else if (rVal == Parameter.RATING_BINARY[2]) {//1
						//m_BSR[bVal][sVal][1]++;
						pos_BSR.get(bVal).set(sVal, pos_BSR.get(bVal).get(sVal)+1);
					}	
					//m_BS_currRating[bVal][sVal] = (int) rVal;
					BS_currentRating.get(bVal).set(sVal,(int)rVal);
					int dVal = (int)(inst.value(Parameter.m_dayIdx))-1;
					//m_Rtimes[dVal]++;
					m_Rtimes.set(dVal, m_Rtimes.get(dVal)+1);
				}
	}*/

}//class

