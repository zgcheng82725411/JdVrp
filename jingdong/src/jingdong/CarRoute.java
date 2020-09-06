package jingdong;

import java.util.Vector;

public class CarRoute {
	
	int curTime;        //时间约束
	int curMileage;     //里程约束
	double curVolume;   //容量约束
	double curWeight;   //载重约束

	int cartype;
	Vector<Integer> route;
	int leaveTime;
	int backTime;
	int waitTime;
	int chargeNum;
	int totalMileage;

}
