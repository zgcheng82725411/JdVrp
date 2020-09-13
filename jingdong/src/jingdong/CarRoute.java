package jingdong;

import java.util.List;
import java.util.Vector;

public class CarRoute {
	
	int curTime =0;        //时间约束
	int curMileage =0;     //里程约束
	double curVolume =0;   //容量约束
	double curWeight= 0;   //载重约束

	int cartype =2;
	Vector<Integer> route = new Vector<Integer>();
	int leaveTime =-1;
	int backTime =0;
	int waitTime=0;
	int chargeNum =0;
	int totalMileage =0;

	public List<Integer> getInterRoute()
	{
		return route;
	}
}
