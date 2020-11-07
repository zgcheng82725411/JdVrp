package jingdong;

import java.util.Vector;

public class Fit {

	Vector<CarRoute> routeList = new Vector<CarRoute>();
	Vector<Vector<Integer>> route = new Vector<Vector<Integer>>();
	Vector<Integer> type = new Vector<Integer>();
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
	
	public void setRouteIndex(Vector<Vector<Integer>> route)
	{
		this.route = route;
	}
	
	public Vector<Vector<Integer>> getRouteIndex()
	{
		return route;
	}
	
	public Vector<Integer> getCarTypeIndex()
	{
		return type;
	}
	
		
}
