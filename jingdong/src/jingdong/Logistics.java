package jingdong;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.function.IntUnaryOperator;

import com.sun.javafx.collections.MappingChange.Map;
import com.sun.org.apache.bcel.internal.generic.I2F;
import com.sun.org.apache.regexp.internal.recompile;

public class Logistics {

	final int INIT_CAR_NUM = 200;

	final double MAX_DOUBLE = 100000000;

	HashMap<Integer, Integer> sellMatchCharger = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> calSellChargerMatch(Vector<Vector<Integer>> distance, List<Node> charger,
			List<Node> seller) {
		HashMap<Integer, Integer> nearCharge = new HashMap<Integer, Integer>();
		for (Node sell : seller) {
			int chargerIndex = findCharge(sell, distance, charger);		
			nearCharge.put(sell.id,charger.get(chargerIndex).id);
		}
		
		sellMatchCharger = nearCharge;
		return nearCharge;
	}

	public int findCharge(Node sell, Vector<Vector<Integer>> distance,
			List<Node> charger) {
		int chargerIndex = -1;
		double minDis = Double.MAX_VALUE;
		for (int i = 0; i < charger.size(); ++i) {
			if (distance.get((int) sell.id).get((int) charger.get(i).id) < minDis) {
				minDis = distance.get((int) sell.id).get(
						(int) charger.get(i).id);
				chargerIndex = i;
			}
		}
		return chargerIndex;
	}

	// ͬ������
	public Vector<CarRoute> synchroSearch(List<Node> seller,
			List<Node> charger, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle) {
		Vector<CarRoute> result = initResult();
			
		for (Node sell : seller) {
			System.out.println(sell.id);
		}

		//�������еĵ�ͳ���ÿ�ζ��������
		int total = 0;
		for (Node sell : seller) {
			//�ҵ�����ĵ�,�����Ϊ�����򷵻�-2����������-1
			
			if(sell.id == 862)
			{
				int u  =0;
				u++;
			}
			int carIndex = whichCarVisitThisSell(result, sell, charger,
					distance, time, vehicle);
			if(carIndex == -1)
			{
				total++;
				continue;
			}
			else
			{
				System.out.println(sell.id);
			}
			visitSeller(result.get(carIndex), sell, distance, time, vehicle);		
		}
		System.out.println(total);
		
		result = deleteEmptyCar(result);
		
		//���ϻ�����
		addLastPoint(result, distance, vehicle);
		calAllCost(result, vehicle);
		
		for(int i = 0;i < result.size();i++)
		{
			System.out.println(result.get(i).getInterRoute());
		}
		return result;
	}
	
	private void addLastPoint(Vector<CarRoute> result, Vector<Vector<Integer>> distance, Vector<Vehicle> vehicle) {
		
		for (CarRoute curCar : result) {
			int lastIndex = curCar.route.get(curCar.route.size() - 1);
							
			curCar.curMileage += distance.get(lastIndex).get(0);	
			if(curCar.curMileage > vehicle.get(curCar.cartype-1).driving_range)
			{
				curCar.chargeNum++;
				
				curCar.totalMileage = curCar.totalMileage
						+ distance.get(lastIndex).get(sellMatchCharger.get(lastIndex))
						+ distance.get(sellMatchCharger.get(lastIndex)).get(0);
				curCar.curMileage = distance.get(sellMatchCharger.get(lastIndex)).get(0);
			}
			else {
				curCar.totalMileage = curCar.totalMileage
						+ distance.get(lastIndex).get(0);	
			}
		}	
		
	}

	// ɾ���յĻ���
	public Vector<CarRoute> deleteEmptyCar(Vector<CarRoute> result) {
		Vector<CarRoute> ret = new Vector<CarRoute>();
		for (CarRoute car : result) {
			if (car.route.size() > 1)
				ret.add(car);
		}
		return ret;
	}

	private void visitSeller(CarRoute curCar, Node sell,
			Vector<Vector<java.lang.Integer>> distance,
			Vector<Vector<java.lang.Integer>> time, Vector<Vehicle> vehicle) {
		// ����sell�̻�
		sell.isServer = true;
		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size() - 1);
		
		int mileage = curCar.curMileage
				+ distance.get(lastIndex).get(sell.id);

		//�ж��Ƿ�����
		int arriveTime;
		if(mileage > vehicle.get(curCar.cartype - 1).driving_range)
		{
			curCar.chargeNum++;
			curCar.totalMileage = curCar.totalMileage
					+ distance.get(lastIndex).get(sellMatchCharger.get(sell.id))
					+ distance.get(sellMatchCharger.get(sell.id)).get(sell.id);
			curCar.curMileage = distance.get(sellMatchCharger.get(sell.id)).get(sell.id);
			
			arriveTime = curCar.curTime	+ time.get(lastIndex).get(sellMatchCharger.get(sell.id))
					+ time.get(sellMatchCharger.get(sell.id)).get(sell.id);
		}
		else {
			curCar.totalMileage = curCar.totalMileage
					+ distance.get(lastIndex).get(sell.id);
			curCar.curMileage += distance.get(lastIndex).get(sell.id);
			arriveTime = curCar.curTime	+ time.get(lastIndex).get(sell.id);
		}
					
		int waitT = sell.first_int_tm > arriveTime ? sell.first_int_tm
				- arriveTime : 0;
		// ���³���ʱ��,����ʱ��Ϊ �����ڵ�һ�ε����̻��ĵȴ�ʱ��
		
		curCar.waitTime += waitT;
		
		curCar.curTime = arriveTime + waitT + 30;
		// ����ʱ��+�ȴ�+����+����
		curCar.route.add(sell.id);				
	}
	
	public double calAllCost( Vector<CarRoute> result,  Vector<Vehicle> vehicle)
	{		
		double all_cost = 0;
		for (int i = 0; i < result.size(); ++i)
		{
			all_cost += result.get(i).totalMileage * vehicle.get(result.get(i).cartype - 1).unit_trans_cost;
			all_cost += result.get(i).chargeNum * 50;
			all_cost += result.get(i).waitTime * 0.4;
			all_cost += vehicle.get(result.get(i).cartype - 1).vechile_cost;
		}
		System.out.println(all_cost);
		return all_cost;
	}

	// ������ȥ�����������
	public int whichCarVisitThisSell(Vector<CarRoute> result, Node sell,
			List<Node> charger, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle) {
		int carIndex = -1;
		double minvisitCost = MAX_DOUBLE;
		for (int i = 0; i < result.size(); ++i) {
			CarRoute curCar = new CarRoute(result.get(i));
			// ����curCar�Ƿ���Բ�������� ֱ�ӷ�������
			if (canVisit(curCar, vehicle, sell, distance, time)== 1) {
				double curCost = 0;
				// ·������
				curCost += distance.get(
						curCar.route.get(curCar.route.size() - 1)).get(sell.id)
						* vehicle.get(curCar.cartype - 1).unit_trans_cost;
				if (curCost < minvisitCost) {
					carIndex = i;
					minvisitCost = curCost;
				}
			}			
		}
		return carIndex;
	}

	//����curCar ����������Ƿ����ֱ�ӷ���sell
	private int canVisit(CarRoute curCar, Vector<Vehicle> vehicle,
			Node sell, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time) {
		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size() - 1); // ·�������һ���̻��ڵ�
		curCar.curMileage = curCar.curMileage
				+ distance.get(lastIndex).get(sell.id);			;
		// ʱ�䴰Լ����
		// ����ʱ��������̻�����ٽ���ʱ��֮ǰ
		int arriveTime = curCar.curTime	+ time.get(lastIndex).get(sell.id);
		if (arriveTime > sell.last_int_tm)
			return -1;

		int waitT = sell.first_int_tm > arriveTime ? sell.first_int_tm
				- arriveTime : 0;

		curCar.curTime = arriveTime + waitT + 30;
		int flag = isConstraint(curCar, vehicle);
		if(flag == 1)
		{
			return 1;
		}
		else if(flag == -1){
			return -2;
		}
		else {
			//���һ���ڵ��磬Ȼ���ж���̺�ʱ��,��������1
			int chargeindex = sellMatchCharger.get(sell.id);
			CarRoute tempCar = new CarRoute(curCar);
			arriveTime = tempCar.curTime + time.get(lastIndex).get(chargeindex)
					+ time.get(chargeindex).get(sell.id);
			if (arriveTime > sell.last_int_tm)
				return -1;
			else {		
				return 1;
			}			
		}		
	}

	// ��ʼ��vector<CarRoute> result
	public Vector<CarRoute> initResult() {
		Vector<CarRoute> result = new Vector<CarRoute>();
		
		for (int i = 0; i < INIT_CAR_NUM/2; ++i) {
			CarRoute car = new CarRoute();
			car.route.add(0);
			car.cartype = 1;
			result.add(car);
		}
		for (int i = 0; i < INIT_CAR_NUM/2; ++i) {
			CarRoute car = new CarRoute();
			car.route.add(0);
			car.cartype = 2;
			result.add(car);
		}
		return result;
	}

	public int isConstraint(CarRoute curCar, Vector<Vehicle> vehicle) // Լ������
	{
		if (curCar.curVolume > vehicle.get(curCar.cartype - 1).volume) // ��������Լ��
		{
			return -1;
		}
		if (curCar.curWeight > vehicle.get(curCar.cartype - 1).weight) // ��������Լ��
		{
			return -1;
		}
		if (curCar.curMileage > vehicle.get(curCar.cartype - 1).driving_range)// ���Լ��
																				// ��ע�⣺�Ȳ�������;�������
		{
			return -2;
		}
		if (curCar.curTime > 960) // ����ʱ��Լ��
		{
			return -1;
		}
		return 1;
	}

}
