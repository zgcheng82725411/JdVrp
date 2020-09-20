package jingdong;

import java.util.List;
import java.util.Vector;

public class CarRoute {
	
	int curTime =0;        //ʱ��Լ��
	int curMileage =0;     //���Լ��
	double curVolume =0;   //����Լ��
	double curWeight= 0;   //����Լ��

	int cartype =2;
	Vector<Integer> route = new Vector<Integer>();
	int leaveTime =-1;
	int backTime =0;
	int waitTime=0;
	int chargeNum =0;
	int totalMileage =0;

	public CarRoute(CarRoute carRoute) {
		curTime = carRoute.curTime;  //ʱ��Լ��
		curMileage = carRoute.curMileage;     //���Լ��
		curVolume = carRoute.curVolume;   //����Լ��
		curWeight= carRoute.curWeight;   //����Լ��

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
