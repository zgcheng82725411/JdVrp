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

	public CarRoute(CarRoute carRoute) {
		curTime = carRoute.curTime;  //时间约束
		curMileage = carRoute.curMileage;     //里程约束
		curVolume = carRoute.curVolume;   //容量约束
		curWeight= carRoute.curWeight;   //载重约束

		cartype = carRoute.cartype;
		route = new Vector<Integer>(carRoute.route);
		leaveTime = carRoute.leaveTime;
		backTime = carRoute.backTime;
		waitTime= carRoute.waitTime;
		chargeNum = carRoute.chargeNum;
		totalMileage = carRoute.totalMileage;
		
	}
	
    public CarRoute() {
		
	}

	public List<Integer> getInterRoute()
	{
		return route;
	}
	
	
}
