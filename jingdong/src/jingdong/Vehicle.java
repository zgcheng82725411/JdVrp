package jingdong;

import com.sun.beans.WeakCache;

public class Vehicle {

	
	public int id;
	public	String name;
	public	double volume;
	public	double weight;
	public  double driving_range;
	public	double charge_tm;
	public	double unit_trans_cost;
	public	int vechile_cost;
	public Vehicle(int i, String string, int j, double d, int k, double e,
			double f, int l) {	
		
		id = i;
		name = string;
		volume = j;
		weight = d;
		driving_range = k;
		charge_tm = e;
		unit_trans_cost = f;
		vechile_cost = l;
		
				
	}		
}
