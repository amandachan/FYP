package main;

import GUI.SimulationAnalyzer_Main;
import GUI.ComparisonResults;
import GUI.Display;
import agent.*;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import java.lang.*;

import defenses.eBay;
import distributions.*;
import environment.*;
import main.*;

public class CentralAuthority {

	private Environment env;
	private String m_defenseName;
	private String m_attackName;
	private ArrayList<Buyer> m_buyers = new ArrayList<Buyer>();
	private ArrayList<Seller> m_sellers = new ArrayList<Seller>();
	private BankBalance bankbalance ;
	ArrayList<Double> defenseTime_day= new ArrayList<Double>();
	ArrayList<Double> honest_avgWt;
	ArrayList<Double> dishonest_avgWt;
	ArrayList transList;
	private String realdata_filename;

	public static HashMap outputResult = new HashMap(); 
	SimulationAnalyzer_Main analysis = new SimulationAnalyzer_Main();



	public CentralAuthority(){   
		honest_avgWt = new ArrayList();
		dishonest_avgWt = new ArrayList();
		env = new EnvironmentS();
		for(int i=0; i<Parameter.NO_OF_DAYS; i++){
			defenseTime_day.add(i,0.0);
			dishonest_avgWt.add(i,0.0);
			honest_avgWt.add(i,0.0);

		}
		bankbalance = new BankBalance();

	}



	public void setUpEnvironment(String attackName, String defenseName)throws Exception{
		env.eCommerceSetting(attackName, defenseName);
	}

	public ArrayList simulateEnvironment(int noOfRuns, String attackName, String defenseName, boolean dailyPrint) throws ClassNotFoundException, NoSuchMethodException, SecurityException,Exception{


		System.out.println("enters simulateEnvironment "+attackName+"  "+defenseName);

		m_attackName = new String(attackName);
		m_defenseName = new String(defenseName);
		setUpEnvironment(attackName, defenseName);   

		transList = new ArrayList();
		m_buyers=env.getBuyerList();
		m_sellers=env.getSellerList();
		
		System.out.println(m_buyers.size());
		for (int i=0; i<m_sellers.size(); i++){
			env.getPositiveRatings().put(m_sellers.get(i), 0.0);
			env.getNegativeRatings().put(m_sellers.get(i), 0.0);
		}
		for (int day = 0; day < Parameter.NO_OF_DAYS; day++){                                                   
			//step 2: Attack model (dishonest buyers)               
			attack(day);            

			//step 3: Defense model (honest buyers)
			long defensetimeStart = new Date().getTime();
			defense(day);   
			long defensetimeEnd = new Date().getTime();

			defenseTime_day.set(day,(-defensetimeStart + defensetimeEnd) / 1000.0 );

			env.setDay(day); //update to next day
			avgerWeights(day);
			bankbalance.updateDailyBankBalance(day, m_buyers);
			bankbalance.printDailyBalance(day);
			if (day%5==0){
				try {


					File file = new File(m_defenseName + m_attackName + "Trans.txt");

					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}

					int honesttrans = env.getSellerList().get(Parameter.TARGET_HONEST_SELLER).getSales().get(day);
					int dishonestrans = env.getSellerList().get(Parameter.TARGET_DISHONEST_SELLER).getSales().get(day);


					FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(day + " | " + dishonestrans + " | " + honesttrans + "\n");
					bw.close();


				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {

				String file1name = m_defenseName + m_attackName + noOfRuns + "MAE";
				String file2name = m_defenseName + m_attackName + noOfRuns + "MCC";
				String file3name = m_defenseName + m_attackName + noOfRuns + "FNR";
				String file4name = m_defenseName + m_attackName + noOfRuns + "FPR";
				String file5name = m_defenseName + m_attackName + noOfRuns + "TPR";
				String file6name = m_defenseName + m_attackName + noOfRuns + "Accuracy";
				String file7name = m_defenseName + m_attackName + noOfRuns + "Precision";
				String file8name = m_defenseName + m_attackName + noOfRuns + "F-Measure";

				File file1 = new File(file1name + ".txt");
				File file2 = new File(file2name + ".txt");
				File file3 = new File(file3name + ".txt");
				File file4 = new File(file4name + ".txt");
				File file5 = new File(file5name + ".txt");
				File file6 = new File(file6name + ".txt");
				File file7 = new File(file7name + ".txt");
				File file8 = new File(file8name + ".txt");


				// if file doesnt exists, then create it
				if (!file1.exists()) {
					file1.createNewFile();
				}
				if (!file2.exists()) {
					file2.createNewFile();
				}
				if (!file3.exists()) {
					file3.createNewFile();
				}
				if (!file4.exists()) {
					file4.createNewFile();
				}
				if (!file5.exists()) {
					file5.createNewFile();
				}
				if (!file6.exists()) {
					file6.createNewFile();
				}
				if (!file7.exists()) {
					file7.createNewFile();
				}
				if (!file8.exists()) {
					file8.createNewFile();
				}

				FileWriter fw1 = new FileWriter(file1.getAbsoluteFile(),true);
				FileWriter fw2 = new FileWriter(file2.getAbsoluteFile(),true);
				FileWriter fw3 = new FileWriter(file3.getAbsoluteFile(),true);
				FileWriter fw4 = new FileWriter(file4.getAbsoluteFile(),true);
				FileWriter fw5 = new FileWriter(file5.getAbsoluteFile(),true);
				FileWriter fw6 = new FileWriter(file6.getAbsoluteFile(),true);
				FileWriter fw7 = new FileWriter(file7.getAbsoluteFile(),true);
				FileWriter fw8 = new FileWriter(file8.getAbsoluteFile(),true);

				BufferedWriter bw1 = new BufferedWriter(fw1);
				bw1.write(env.getDailyRepDiff().get(0) + " " + " " + env.getDailyRepDiff().get(1) + "\n");
				bw1.close();

				BufferedWriter bw2 = new BufferedWriter(fw2);
				bw2.write(env.getMcc().getDailyMCC(day).get(0) + " " + " " + env.getMcc().getDailyMCC(day).get(1) + "\n");
				bw2.close();

				BufferedWriter bw3 = new BufferedWriter(fw3);
				bw3.write(env.getMcc().getDailyFNR(day).get(0) + " " + " " + env.getMcc().getDailyFNR(day).get(1) + "\n");
				bw3.close();

				BufferedWriter bw4 = new BufferedWriter(fw4);
				bw4.write(env.getMcc().getDailyFPR(day).get(0) + " " + " " + env.getMcc().getDailyFPR(day).get(1) + "\n");
				bw4.close();

				BufferedWriter bw5 = new BufferedWriter(fw5);
				bw5.write(env.getMcc().getDailyTPR(day).get(0) + " " + " " +  env.getMcc().getDailyTPR(day).get(1) + "\n");
				bw5.close();

				BufferedWriter bw6 = new BufferedWriter(fw6);
				bw6.write(env.getMcc().getDailyAcc(day).get(0) + " " + " " + env.getMcc().getDailyAcc(day).get(1) + "\n");
				bw6.close();

				BufferedWriter bw7 = new BufferedWriter(fw7);
				bw7.write(env.getMcc().getDailyPrec(day).get(0) + " " + " " + env.getMcc().getDailyPrec(day).get(1) + "\n");
				bw7.close();

				BufferedWriter bw8 = new BufferedWriter(fw8);
				bw8.write(env.getMcc().getDailyF(day).get(0) + " " + " " + env.getMcc().getDailyF(day).get(1) + "\n");
				bw8.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return transList;
	}

	private String formatResults(double result){
		if(result>=0){
			DecimalFormat df = new DecimalFormat("0.00000000000000000");			
			return df.format(result);
		}
		else
		{
			DecimalFormat df = new DecimalFormat("0.0000000000000000");			
			return df.format(result);
		}
	}


	private void attack(int day){

		int numBuyers = Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS;

		m_buyers = env.getBuyerList();
		//System.out.println(m_buyers.get(index))
		for(int i = 0; i < numBuyers; i++){
			int bid = i;
			if(m_buyers.get(bid).isIshonest() == false){
				if(m_attackName.equalsIgnoreCase("Whitewashing") || m_attackName.equalsIgnoreCase("Sybil_Whitewashing")){
					if (day > 0) {
						//System.out.println(bid);

						bid = (Parameter.NO_OF_DISHONEST_BUYERS + Parameter.NO_OF_HONEST_BUYERS) + (day - 1) * Parameter.NO_OF_DISHONEST_BUYERS + bid;			
						bid %=100;
						//System.out.println(bid);
					}
				}
				m_buyers.get(bid).addTransaction(day);
			}
		}
	}

	private void defense(int day){

		int numBuyers = env.getBuyerList().size();


		for(int i = 0; i < numBuyers; i++){
			int bid = i;
			if(m_buyers.get(bid).isIshonest() == true){
				m_buyers.get(bid).addTransaction(day);
			}
		}


		//		else if (env instanceof EnvironmentR){
		//			int count=0;
		//			while(count != env.getUserID().size() && count != env.getProductID().size() && count != env.getScore().size()){
		//
		//				if(env.getDate().get(count) == env.getDay()-1){
		//					Buyer b = env.getBuyerList().get(env.getStringToBuyer().get(env.getUserID().get(count)));
		//					Seller s = env.getSellerList().get(env.getStringToSeller().get(env.getProductID().get(count)));
		//					b.addTransaction(day, s);
		//				}
		//				count++;
		//			}
		//		}

	}

	private void avgerWeights(int day){

		int db = Parameter.NO_OF_DISHONEST_BUYERS;
		int hb = Parameter.NO_OF_HONEST_BUYERS; 


		//these code for trust models: trustworthiness for local/partial advisors;
		if(Parameter.includeWMA(m_defenseName) || Parameter.includeEA(m_defenseName)){
			int numDA = 0; //number of dishonest advisors;
			int numHA = 0; //number of honest advisors
			honest_avgWt.set(day,(double)0);
			honest_avgWt.set(day, (double)0);


			for(int i = Parameter.NO_OF_DISHONEST_BUYERS; i < Parameter.NO_OF_DISHONEST_BUYERS + hb; i++){
				int bid = i;

				ArrayList<Double> weights_BA = new ArrayList<Double>();
				// weights_BA.add(m_buyers.get(bid).getTrusts());
				for (int j=0; j<m_buyers.get(bid).getTrusts().size(); j++){
					weights_BA.add(m_buyers.get(bid).getTrusts().get(j));
				}
				weights_BA.add(m_buyers.get(bid).getTrusts().get(i));
				ArrayList<Buyer> advisors = new ArrayList<Buyer>();
				// advisors.add(m_buyers.get(bid).getAdvisors());
				for (int j=0;j<m_buyers.get(bid).getAdvisors().size();j++){


					advisors.add(m_buyers.get(bid).getAdvisor(j));

				}
				for(int j = 0; j < advisors.size(); j++){                    
					int aid = advisors.get(j).getId(); double oldWt;
					if(aid == bid)continue;
					if(aid < Parameter.NO_OF_DISHONEST_BUYERS || aid >= Parameter.NO_OF_DISHONEST_BUYERS + hb){
						// m_dailyAvgWeights[day][0] += weights_BA.get(aid);

						oldWt = dishonest_avgWt.get(day);
						oldWt +=weights_BA.get(aid);
						dishonest_avgWt.set(day, oldWt);
						numDA++;

					} else{
						oldWt = honest_avgWt.get(day);
						oldWt +=weights_BA.get(aid);
						honest_avgWt.set(day, oldWt);
						numHA++;
					}
				}

			}
			if (numDA != 0) {
				//  double wt =  (dishonest_avgWt.get(day))/ numDA;
				dishonest_avgWt.set(day,(dishonest_avgWt.get(day)/ numDA));
				// m_dailyAvgWeights[day][0] /= (numDA);
			}           
			if (numHA != 0) {
				// m_dailyAvgWeights[day][1] /= (numHA);
				honest_avgWt.set(day,(honest_avgWt.get(day)/numHA));
			}
		}
		else{
			//these code for trust models: trustworthiness for all advisors;
			int numDA = 0;
			int numHA = 0;
			for(int i = Parameter.NO_OF_DISHONEST_BUYERS; i < Parameter.NO_OF_DISHONEST_BUYERS + hb; i++){
				int bid = i;                
				for (int k = 0; k < 2; k++) {                
					int sid = Parameter.TARGET_DISHONEST_SELLER;
					if (k == 1)sid = Parameter.TARGET_HONEST_SELLER;
					ArrayList <Double> SaverTA = new ArrayList<Double>();
					//  SaverTA.add(m_buyers.get(bid).getAverageTrusts(sid));

					double array1 [] =new double[m_buyers.get(bid).getAverageTrusts(sid).length]; //check
					array1 = m_buyers.get(bid).getAverageTrusts(sid); //check
					for(int j=0;j<array1.length;j++)  //check
						SaverTA.add(array1[j]);  //check

					if(SaverTA.get(0) >= 0){//dishonest advisors
						//System.out.println(dishonest_avgWt.get(day));
						dishonest_avgWt.set(day,dishonest_avgWt.get(day) +SaverTA.get(0));
						//m_dailyAvgWeights[day][0] += SaverTA[0];
						numDA++;
					}
					if(SaverTA.get(1) >= 0){//dishonest advisors
						honest_avgWt.set(day, honest_avgWt.get(day)+SaverTA.get(1));
						// m_dailyAvgWeights[day][1] += SaverTA[1];
						numHA++;
					}
				}   
			}       

			if (numDA != 0) {
				dishonest_avgWt.set(day,(dishonest_avgWt.get(day)/ numDA));
				//m_dailyAvgWeights[day][0] /= (numDA);
			}           
			if (numHA != 0) {
				honest_avgWt.set(day,(honest_avgWt.get(day)/ numDA));
				// m_dailyAvgWeights[day][1] /= (numHA);
			}   
		}       

	}

	public void evaluateDefenses(ArrayList<String> defenseNames, ArrayList<String> attackNames, String evaluateName, String realdata) throws Exception,ClassNotFoundException, NoSuchMethodException, SecurityException{
		double averageMAEdh=0, averageMCCdh=0, averageFNRdh=0, averageFPRdh=0, averageTPRdh=0, averageAccdh=0, averagePrecdh = 0,averageFdh=0;
		double averageMAEh=0, averageMCCh=0, averageFNRh=0, averageFPRh=0, averageTPRh=0, averageAcch=0, averagePrech =0, averageFh=0;
		double averageRbosutness=0;
		
		// TO READ FILE FOR REAL ENVIRONMENT
		if (realdata != null){
			realdata_filename = realdata;
		}
		
		int runtimes = Parameter.NO_OF_RUNTIMES;                    //runtimes =  50
		transList = new ArrayList();
		//output the result: [|transactions|, time]
		//      double[][][][] results = new double[runtimes][defenseNames.length][attackNames.length][2];
		ArrayList<ArrayList<Double>> result_attack = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<ArrayList<Double>>> result_defense_attack = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> result_runtime_defense_attack = new ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>();

		for(int i = 0; i < runtimes; i++){
			result_attack = new ArrayList<ArrayList<Double>>();
			result_defense_attack = new ArrayList<ArrayList<ArrayList<Double>>>();
			for(int j = 0; j < defenseNames.size(); j++){            
				for(int k = 0; k < attackNames.size(); k++){                 
					//            System.err.print("  runtimes = " + i + ",   defense = " + defenseNames[j] + ",   attack = " + attackNames[k]);


					long start = new Date().getTime();
					// [true / false] means print/not daily result     
					transList = simulateEnvironment(i, attackNames.get(k), defenseNames.get(j), false);
					long end = new Date().getTime();
					System.err.println("   time =  " + (end - start)/1000.0 + " s");


					averageMAEdh += env.getDailyRepDiff().get(0);
					averageMAEh += env.getDailyRepDiff().get(1);
					averageMCCdh += env.getMcc().getDailyMCC(env.getDay()-1).get(0); 
					averageMCCh += env.getMcc().getDailyMCC(env.getDay()-1).get(1);
					averageFNRdh += env.getMcc().getDailyFNR(env.getDay()-1).get(0);
					averageFNRh += env.getMcc().getDailyFNR(env.getDay()-1).get(1);
					averageFPRdh += env.getMcc().getDailyFPR(env.getDay()-1).get(0); 
					averageFPRh += env.getMcc().getDailyFPR(env.getDay()-1).get(1);
					averageTPRdh += env.getMcc().getDailyTPR(env.getDay()-1).get(0);
					averageTPRh += env.getMcc().getDailyTPR(env.getDay()-1).get(1);
					averageAccdh += env.getMcc().getDailyAcc(env.getDay()-1).get(0);
					averageAcch += env.getMcc().getDailyAcc(env.getDay()-1).get(1);
					averagePrecdh += env.getMcc().getDailyPrec(env.getDay()-1).get(0); 
					averagePrech += env.getMcc().getDailyPrec(env.getDay()-1).get(1);
					averageFdh += env.getMcc().getDailyF(env.getDay()-1).get(0) ;
					averageFh += env.getMcc().getDailyF(env.getDay()-1).get(1);

					//results[i][j][k] = generate_report(i); //report include the run count
					result_attack.add(generate_report(i));					
				}
				result_defense_attack.add(result_attack);
			}

			result_runtime_defense_attack.add(result_defense_attack);
		}

		try {

			String file1name = m_defenseName + m_attackName  + "Overall";

			File file1 = new File(file1name + ".txt");


			// if file doesnt exists, then create it
			if (!file1.exists()) {
				file1.createNewFile();
			}

			FileWriter fw = new FileWriter(file1.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Defense Name: " + m_defenseName + " " + " " + "Attack Name: " + m_attackName + "\n");
			bw.write("Average Results of Evaluation Metrics for 100 days \n\n");
			bw.write(" _______________________________________________________________________________________________________\n");
			bw.write("|     Evaluation Metrics      " + "|          Dishonest Seller          " + "|           Honest Seller            |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|            MAE              " + "|         " + formatResults(averageMAEdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averageMAEh/Parameter.NO_OF_RUNTIMES) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|            MCC              " + "|         " + formatResults(averageMCCdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averageMCCh/Parameter.NO_OF_RUNTIMES) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|            FNR              " + "|         " + formatResults(averageFNRdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averageFNRh/Parameter.NO_OF_RUNTIMES) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|            FNR              " + "|         " + formatResults(averageFPRdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averageFPRh/Parameter.NO_OF_RUNTIMES) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			//bw.write("|           TPR              " + "|         " + formatResults(averageTPRdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averageTPRh/Parameter.NO_OF_RUNTIMES) +"        |\n");
			//bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|          Accuracy           " + "|         " + formatResults(averageAccdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averageAcch/Parameter.NO_OF_RUNTIMES) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|         Precision           " + "|         " + formatResults(averagePrecdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averagePrech/Parameter.NO_OF_RUNTIMES) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n");
			bw.write("|         F-Measure           " + "|         " + formatResults(averageFdh/Parameter.NO_OF_RUNTIMES)  + "        |         " + formatResults(averageFh/Parameter.NO_OF_RUNTIMES) +"        |\n");
			bw.write("|_____________________________|____________________________________|____________________________________|\n\n");

			//			bw.write("_____________________________|____________________________________\n");
			//			bw.write("|         Robustness          " + "|         " + formatResults(averageRbosutness/runtimes)  + "        |\n");
			//			bw.write("|_____________________________|____________________________________|\n");


			bw.close();



		} catch (IOException e) {
			e.printStackTrace();
		}

		//getStatistic(results, defenseNames, attackNames, evaluateName);
		getStatistic(result_runtime_defense_attack, defenseNames, attackNames, evaluateName);

	}

	public ArrayList<Double> generate_report(int runCount){

		try{
			PrintWriter pw;

			String pathName = "output";
			if(Parameter.RATING_TYPE.equalsIgnoreCase("binary")){
				pathName = "outputB";
			}else if(Parameter.RATING_TYPE.equalsIgnoreCase("multinominal")){
				pathName = "outputM";
			}else if(Parameter.RATING_TYPE.equalsIgnoreCase("real")){
				pathName = "outputR";
			}
			String outputPath = pathName + "/" + m_defenseName ;
			File experimentDirectory = new File(outputPath);
			if (!experimentDirectory.exists()) {
				boolean result = new File(outputPath).mkdirs();
				//				System.out.println("Creating " + outputPath);
			}
			String outputName = experimentDirectory + "/" + m_defenseName + "2" + m_attackName + "Head.txt";			
			pw = new PrintWriter(new BufferedWriter(new FileWriter(outputName)));

			//int no_of_tran_of_target_honest_seller = m_dailySales[Parameter.NO_OF_DAYS][Parameter.TARGET_HONEST_SELLER];
			//int no_of_tran_of_target_dishonest_seller = m_dailySales[Parameter.NO_OF_DAYS][Parameter.TARGET_DISHONEST_SELLER];			
			int no_of_tran_of_target_honest_seller = env.getSellerList().get(Parameter.TARGET_HONEST_SELLER).getSales().get(Parameter.NO_OF_DAYS-1);
			int no_of_tran_of_target_dishonest_seller = env.getSellerList().get(Parameter.TARGET_DISHONEST_SELLER).getSales().get(Parameter.NO_OF_DAYS-1);
			pw.println("Attack: " + m_attackName + " Defense: " + m_defenseName);
			pw.println();
			pw.println("=======================PERFORMANCE=========================");		
			pw.println("target honest sales: " + no_of_tran_of_target_honest_seller);
			pw.println("target dishonest sales: " + no_of_tran_of_target_dishonest_seller);
			pw.println("target dishonest:honest sales: " + no_of_tran_of_target_dishonest_seller + ":" + no_of_tran_of_target_honest_seller);
			pw.println("target dishonest-honest sales: " + (no_of_tran_of_target_dishonest_seller - no_of_tran_of_target_honest_seller));			
			pw.println();	
			pw.println("======================PARAMETERS==========================");				
			pw.println("NO_OF_DISHONEST_BUYERS             : " + Parameter.NO_OF_DISHONEST_BUYERS);
			pw.println("NO_OF_HONEST_BUYERS                : " + Parameter.NO_OF_HONEST_BUYERS);			
			pw.println("NO_OF_DISHONEST_SELLERS            : " + Parameter.NO_OF_DISHONEST_SELLERS);
			pw.println("NO_OF_HONEST_SELLERS               : " + Parameter.NO_OF_HONEST_SELLERS);
			pw.println("NO_OF_DAYS                         : " + Parameter.NO_OF_DAYS);						
			pw.println("TARGET_DISHONEST_SELLER            : " + Parameter.TARGET_DISHONEST_SELLER);
			pw.println("TARGET_HONEST_SELLER               : " + Parameter.TARGET_HONEST_SELLER);
			pw.println("honestBuyerOntargetSellerRatio     : " + Parameter.m_honestBuyerOntargetSellerRatio);
			pw.println("dishonestBuyerOntargetSellerRatio  : " + Parameter.m_dishonestBuyerOntargetSellerRatio);
			pw.println();
			pw.println("=======================DAILY INFO=========================");	
			pw.println("sellers' sale");	
			pw.println("==========================================================");
			pw.close();					

			//out the statistical information, |trans|, MAE-DS rep, HS rep, DS repDiff, HS repdiff, time
			ArrayList<Double> results = new ArrayList<Double>();
			results.add((double) (no_of_tran_of_target_dishonest_seller - no_of_tran_of_target_honest_seller));		
			results.add(env.getDailyRepDiff().get(0)); 
			//System.out.println(env.getDailyRepDiff().get(1));
			//= m_dailyRepDiff[Parameter.NO_OF_DAYS][0];
			results.add(env.getDailyRepDiff().get(1));
			//results[2] = m_dailyRepDiff[Parameter.NO_OF_DAYS][1];
			//results[3] = m_dailyMCC[Parameter.NO_OF_DAYS][0];
			//results[4] = m_dailyMCC[Parameter.NO_OF_DAYS][1];
			//TODO check -1?
			results.add(env.getMcc().getDailyMCC(Parameter.NO_OF_DAYS-1).get(0));
			results.add(env.getMcc().getDailyMCC(Parameter.NO_OF_DAYS-1).get(1));

			outputName = pathName + "/" + m_defenseName + "2" + m_attackName + ".txt";
			pw = new PrintWriter(new BufferedWriter(new FileWriter(outputName, true)));			
			pw.println(results.get(0) + "  " + results.get(1) + "   " + results.get(2)+ "  " + results.get(3)+ "  " + results.get(4));			
			pw.close();

			//output the detail sellers' sale
			outputName = pathName + "/" + m_defenseName + "/" + m_defenseName;			
			outputName += "2" + m_attackName + runCount;		
			outputName += "Detail";
			//Detail 1: |transactions|
			pw = new PrintWriter(new BufferedWriter(new FileWriter(outputName + "_trans.txt")));
			for(int j = 0; j < Parameter.NO_OF_DAYS; j++){
				for (int i = 0; i < Parameter.NO_OF_HONEST_SELLERS	+ Parameter.NO_OF_DISHONEST_SELLERS; i++) {											
					pw.print(env.getSellerList().get(i).getSales().get(j) + " ");
				}
				pw.println();
			}			
			pw.close();
			//Detail 2: MAE
			pw = new PrintWriter(new BufferedWriter(new FileWriter(outputName + "_MAE.txt")));
			//TODO check for(int j = 0; j <= Parameter.NO_OF_DAYS; j++){
			for(int j = 0; j < Parameter.NO_OF_DAYS; j++){	
				for (int i = 0; i < 2; i++) {											
					pw.print(env.getDailyReputation(j).get(i) + "   ");
				}
				for (int i = 0; i < 2; i++) {											
					pw.print(env.getDailyReputation(j).get(i) + "   ");
				}
				//for (int i = 0; i < 2; i++) {											
				//pw.print(m_dailyAvgWeights[j][i] + "   ");
				//}	
				pw.print( dishonest_avgWt.get(j) + "   ");
				pw.print( honest_avgWt.get(j)+ "   ");
				pw.println();
			}			
			pw.close();	

			return results;
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}

		return null;
	}

	private void getStatistic(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> result_runtime_defense_attack, ArrayList<String> defenseNames, ArrayList<String> attackNames, String evaluateName){

		//		results : [run times][defense][attack] [|transactions|, time]
		int runtimes = Parameter.NO_OF_RUNTIMES;
		int noIndicators = 5;
		//double[][][] means = new double[defenseNames.length][attackNames.length][noIndicators];
		ArrayList<ArrayList<ArrayList<Double>>> mean_defense_attack_indi = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> mean_attack_indi = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> mean_indi = new ArrayList<Double>();

		//double[][][] stds = new double[defenseNames.length][attackNames.length][noIndicators];
		ArrayList<ArrayList<ArrayList<Double>>> std_defense_attack_indi = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> std_attack_indi = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> std_indi = new ArrayList<Double>();
		for(int l = 0; l < noIndicators; l++){
			mean_indi.add(l, 0.0);
		}
		//get the mean and standard deviation	
		System.out.println(runtimes + " " + result_runtime_defense_attack.size());

		for(int i = 0; i < runtimes; i++){
			for(int j = 0; j < defenseNames.size(); j++){	
				for(int k = 0; k < attackNames.size(); k++){


					for(int l = 0; l < noIndicators; l++){
						if(l == 0){// |transactions|
							double theoricialBound1 = (Parameter.NO_OF_DAYS * Parameter.NO_OF_HONEST_BUYERS * Parameter.m_honestBuyerOntargetSellerRatio);
							double theoricialBound2 = (Parameter.NO_OF_DAYS * Parameter.NO_OF_DISHONEST_BUYERS * Parameter.m_honestBuyerOntargetSellerRatio);
							double theoricialBound = theoricialBound1 > theoricialBound2? theoricialBound1: theoricialBound2;
							if(Parameter.includeSybil(attackNames.get(k))){
								theoricialBound = theoricialBound1 < theoricialBound2? theoricialBound1: theoricialBound2;
							} 
							//results[i][j][k][l] /= -theoricialBound;
							//System.out.println(result_runtime_defense_attack.get(i).get(j).get(k).get(l) + " " + (-theoricialBound));
							double inst_var= result_runtime_defense_attack.get(i).get(j).get(k).get(l)/(-theoricialBound);
							result_runtime_defense_attack.get(i).get(j).get(k).set(l, inst_var);
						}
						//means[j][k][l] += results[i][j][k][l];


						mean_indi.set(l,mean_indi.get(l)+result_runtime_defense_attack.get(i).get(j).get(k).get(l));
					}
					mean_attack_indi.add(k, mean_indi);
				}
				mean_defense_attack_indi.add(j,mean_attack_indi);
			}
		}	

		for(int l = 0; l < noIndicators; l++){
			std_indi.add(l,0.0);
		}

		for(int i = 0; i < runtimes; i++){

			for(int j = 0; j < defenseNames.size(); j++){			
				for(int k = 0; k < attackNames.size(); k++){


					for(int l = 0; l < noIndicators; l++){
						if (i==0){
							double mean = mean_defense_attack_indi.get(j).get(k).get(l)/runtimes;
							mean_defense_attack_indi.get(j).get(k).set(l, mean);
						}
						double inst_std=(result_runtime_defense_attack.get(i).get(j).get(k).get(l)-mean_defense_attack_indi.get(j).get(k).get(l))*(result_runtime_defense_attack.get(i).get(j).get(k).get(l)-mean_defense_attack_indi.get(j).get(k).get(l));
						std_indi.set(l,std_indi.get(l)+inst_std);
					}
					std_attack_indi.add(k,std_indi);	
				}
				std_defense_attack_indi.add(j,std_attack_indi);	
			}
		}



		//print out the result; noIndicators = [robustness, time]	
		//		noIndicators = 1;

		String hmKey = "";
		ComparisonResults cr = null;
		for (int a = 0; a < attackNames.size(); a++) {
			hmKey = attackNames.get(a);
			cr = new ComparisonResults();
			for (int b = 0; b < defenseNames.size(); b++) {
				cr.getTrustModelList().add(defenseNames.get(b));					
			}
			outputResult.put(hmKey, cr);
		}

		Object[] possibilities = {
				"Robustness [-1,1]",
				"MAE-DS repDiff(reputation difference of target dishonest seller ([0, 1])",
				"MAE-HS repDiff(reputation difference of target honest seller ([0, 1])", 
				"MCC-DS (Classification of target dishonest seller ([-1,1])",
				"MCC-HS (Classification of target honest seller ([-1,1])"			
		};
		int l = -1;
		if(evaluateName.equalsIgnoreCase(possibilities[0].toString())){
			System.out.println("------- Metric 1: Robustness ([-1,1]) ---------------------------------------------------------------");
			l = 0;
		}  else if(evaluateName.equalsIgnoreCase(possibilities[1].toString())){
			System.out.println("------- Metric 2: MAE-DS repDiff(reputation difference of target dishonest seller [0, 1]) ---------");
			l = 1;
		} else if(evaluateName.equalsIgnoreCase(possibilities[2].toString())){
			System.out.println("------- Metric 3: MAE-HS repDIff(reputation difference of target honest seller ([0, 1]) --------");
			l = 2;
		}
		else if(evaluateName.equalsIgnoreCase(possibilities[3].toString())){
			System.out.println("------- Metric 4: MCC-DS (Classification of target dishonest seller ([-1,1]) --------");
			l = 3;
		}
		else if(evaluateName.equalsIgnoreCase(possibilities[4].toString())){
			System.out.println("------- Metric 5: MCC-HS (Classification of target dishonest seller ([-1,1]) --------");
			l = 4;
		}


		System.out.printf(String.format("%-16s	", "          "));
		for (int k = 0; k < attackNames.size(); k++) {
			System.out.print(String.format("%-2s	", attackNames.get(k)));
		}
		System.out.println();
		for (int j = 0; j < defenseNames.size(); j++) {
			//using the "Tab" key to get spaces, it is easy to translate in word
			System.out.print(String.format("%-16s	", defenseNames.get(j)));   
			for (int k = 0; k < attackNames.size(); k++) {	
				DecimalFormat roundoff = new DecimalFormat("0.000");
				//means[j][k][l] = Double.parseDouble(roundoff.format(means[j][k][l]));
				mean_defense_attack_indi.get(j).get(k).set(l, Double.parseDouble(roundoff.format(mean_defense_attack_indi.get(j).get(k).get(l))));
				if(runtimes > 0){
					//stds[j][k][l] = Math.sqrt(stds[j][k][l] / (runtimes));
					double inst_std_var=Math.sqrt(std_defense_attack_indi.get(j).get(k).get(l) / (runtimes));
					std_defense_attack_indi.get(j).get(k).set(l,inst_std_var);
				}
				//stds[j][k][l] = Double.parseDouble(roundoff.format(stds[j][k][l]));
				std_defense_attack_indi.get(j).get(k).set(l,Double.parseDouble(roundoff.format(std_defense_attack_indi.get(j).get(k).get(l))));
				//using the "Tab" key to get spaces, it is easy to translate in word
				System.out.printf("%2.2f+%2.2f	", mean_defense_attack_indi.get(j).get(k).get(l), std_defense_attack_indi.get(j).get(k).get(l));
				analysis.setResult(true);
				analysis.setText("-- Trust Model Used: " + defenseNames.get(j) + " --");
				analysis.setText("-- Attack Model Used: " + attackNames.get(k) + " --");
				analysis.setText("*** Total Rating ***");
				analysis.setText("Mean: " + String.valueOf(mean_defense_attack_indi.get(j).get(k).get(l)) + ", Std. variance: "
						+ String.valueOf(std_defense_attack_indi.get(j).get(k).get(l)));
				cr = (ComparisonResults) outputResult.get(attackNames.get(k));
				for (int m = 0; m < cr.getTrustModelList().size(); m++) {
					if (defenseNames.get(j).equalsIgnoreCase((String) cr.getTrustModelList().get(m))) {
						cr.getMeanList().add(m, String.format("%-5.6s",String.valueOf(mean_defense_attack_indi.get(j).get(k).get(l))));
						cr.getVarList().add(m, String.format("%-5.6s", String.valueOf(std_defense_attack_indi.get(j).get(k).get(l))));
					}
				}
				//analysis.setText("*** Trust Difference ***");
				analysis.setText("*** # Success Transactions ***");
				analysis.setText("*** Simulation has finished! ***");
				analysis.setText("");
				analysis.setChartTableData(j, k, mean_defense_attack_indi.get(j).get(k).get(l), std_defense_attack_indi.get(j).get(k).get(l));
				//analysis.readLogFile();

				//My own added code//


				//						System.out.println();
				//						new Display("Display", analysis);
			}
			System.out.println();
			//analysis.readLogFile();
		}	
		analysis.readTrans(env.getTransactionList(), m_buyers, env);
		// Printing output of hashmap
		System.out.println();
		System.out.println("=====Come to Display=====");
		new Display("Display", analysis);
	}





	public void showOutput(){

	}

	public void exportToDB(){

	}

	public void importFromDB(){

	}

	public void getCentralReputation(){

	}

	public void displaySuggestions(){

	}


	public Environment getEnv() {
		return env;
	}


	public void setEnv(Environment env) {
		this.env = env;
	}



	public String getRealdata_filename() {
		return realdata_filename;
	}



	public void setRealdata_filename(String realdata_filename) {
		this.realdata_filename = realdata_filename;
	}
	
	


}