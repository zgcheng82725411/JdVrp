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

	public List<Integer> getInterRoute()
	{
		return route;
	}
}
