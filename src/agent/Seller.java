package agent;

import java.util.ArrayList;

import main.Parameter;
import main.Product;
import main.Rating;



public class Seller extends Agent{

	private ArrayList<Rating> ratingsToBuyers;
	private ArrayList<Rating> ratingsFromBuyers;
	private ArrayList<Product> productsOnSale = new ArrayList<Product>();
	private ArrayList<Buyer> buyersRated;
	private ArrayList<Buyer> buyersRatedMe;
	private ArrayList<Integer> dailySales;

       
	public Seller(){
		dailySales = new ArrayList<Integer>();
		for(int i=0; i<Parameter.NO_OF_DAYS; i++){
			dailySales.add(0);
		}
	}
	public void addProductToList(Product p){
		productsOnSale.add(p);
	}
	public ArrayList<Product> getProductsOnSale() {
		return productsOnSale;
	}

	public void setProductsOnSale(ArrayList<Product> productsOnSale) {
		this.productsOnSale = productsOnSale;
	}

	public ArrayList<Rating> getRatingsToBuyers() {
		return ratingsToBuyers;
	}
	
	public void setRatingsToBuyers(ArrayList<Rating> ratingsToBuyers) {
		this.ratingsToBuyers = ratingsToBuyers;
	}
	
	public ArrayList<Rating> getRatingsFromBuyers() {
		return ratingsFromBuyers;
	}
	
	public void setRatingsFromBuyers(ArrayList<Rating> ratingsFromBuyers) {
		this.ratingsFromBuyers = ratingsFromBuyers;
	}

	public ArrayList<Buyer> getBuyersRatedMe() {
		return buyersRatedMe;
	}

	public void setBuyersRatedMe(ArrayList<Buyer> buyersRatedMe) {
		this.buyersRatedMe = buyersRatedMe;
	}

	public ArrayList<Buyer> getBuyersRated() {
		return buyersRated;
	}

	public void setBuyersRated(ArrayList<Buyer> buyersRated) {
		this.buyersRated = buyersRated;
	}
	public void addSales(int day){
		if(dailySales.get(day) ==0 && day>0)
			dailySales.set(day, dailySales.get(day-1));
		dailySales.set(day, dailySales.get(day)+1);
	}

	public ArrayList<Integer> getSales() {
		return dailySales;
	}
	
}
