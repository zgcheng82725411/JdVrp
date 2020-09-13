package jingdong;

import java.util.Vector;

public class Fit {

	Vector<CarRoute> routeList = new Vector<CarRoute>();
	double fit;
	
	public void setFitness(double fit)
	{
		this.fit = fit;
	}
	
	public double getFitness()
	{
		return fit;
	}	
	
	public Vector<CarRoute> getRouteList()
	{
		return routeList;
	}
	
	public void setRouteList(Vector<CarRoute> list)
	{
		routeList = list;
	}
		
}
