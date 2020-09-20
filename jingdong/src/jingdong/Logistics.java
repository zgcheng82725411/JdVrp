package jingdong;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class Logistics {

	final int INIT_CAR_NUM = 170;

	final double MAX_DOUBLE = 100000000;

	Vector<Integer> sellMatchCharger = new Vector<Integer>();
	public Vector<Integer> calSellChargerMatch(Vector<Integer> vec,
			Vector<Vector<Integer>> distance, List<Node> charger,
			List<Node> seller) {
		for (Node sell : seller) {
			int chargerIndex = findCharge(sell, distance, charger);
			vec.add(chargerIndex);
		}
		sellMatchCharger = vec;
		return vec;
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
		// ����������ʱ������������
		Collections.sort(seller, new Comparator<Node>() {
			public int compare(Node node1, Node node2) {
				if (node1.last_int_tm < node2.last_int_tm) {
					return 1;
				}
				return 0;
			}
		});

		//�������еĵ�ͳ���ÿ�ζ��������
		for (Node sell : seller) {
			//�ҵ�����ĵ�,�����Ϊ�����򷵻�-2����������-1
			int carIndex = whichCarVisitThisSell(result, sell, charger,
					distance, time, vehicle);
			//���û�У����Ƿ���ͨ����������
			result.get(carIndex).getInterRoute().add(sell.id);
		}		
		return result;
	}


	private void visitCharge(CarRoute curCar, Node charger,
			Vector<Vector<Integer>> distance, Vector<Vector<Integer>> time) {
		// ���ʳ��վ
		++curCar.chargeNum; // ������+1
		int lastIndex = curCar.route.get(curCar.route.size() - 1);

		curCar.totalMileage = curCar.totalMileage
				- distance.get(lastIndex).get(0)
				+ distance.get(lastIndex).get(charger.id)
				+ distance.get(charger.id).get(0);

		curCar.curMileage = distance.get(charger.id).get(0);

		int arriveTime = curCar.curTime - time.get(lastIndex).get(0)
				+ time.get(lastIndex).get(charger.id);

		curCar.curTime = arriveTime + 30 + time.get(charger.id).get(0);
		// ����ʱ��+���+����

		curCar.route.add(charger.id);
		curCar.backTime = curCar.curTime;
	}

	// ɾ���յĻ���
	public void deleteEmptyCar(Vector<CarRoute> result) {
		Vector<CarRoute> ret = new Vector<CarRoute>();
		for (CarRoute car : result) {
			if (car.route.size() > 1)
				ret.add(car);
		}
		result = ret;
	}

	private void visitSeller(CarRoute curCar, Node sell,
			Vector<Vector<java.lang.Integer>> distance,
			Vector<Vector<java.lang.Integer>> time) {
		// ����sell�̻�
		sell.isServer = true;
		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size() - 1);
		curCar.curMileage = curCar.curMileage
				+ distance.get(lastIndex).get(sell.id)
				+ distance.get(sell.id).get(0) - distance.get(lastIndex).get(0);

		int arriveTime = curCar.curTime - time.get(lastIndex).get(0)
				+ time.get(lastIndex).get(sell.id);
		int waitT = sell.first_int_tm > arriveTime ? sell.first_int_tm
				- arriveTime : 0;
		// ���³���ʱ��,����ʱ��Ϊ �����ڵ�һ�ε����̻��ĵȴ�ʱ��
		if (curCar.leaveTime == -1) {
			curCar.leaveTime = waitT;
		} else {
			curCar.waitTime += waitT;
		}

		curCar.curTime = arriveTime + waitT + 30 + time.get(sell.id).get(0);
		// ����ʱ��+�ȴ�+����+����

		curCar.route.add(sell.id);
		curCar.totalMileage = curCar.totalMileage
				+ distance.get(lastIndex).get(sell.id)
				+ distance.get(sell.id).get(0) - distance.get(lastIndex).get(0);

		curCar.backTime = curCar.curTime;
		// curCar.waitTime += waitT;

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
				//��������1
				return 1;
			}			
		}		
	}

	// ��ʼ��vector<CarRoute> result
	public Vector<CarRoute> initResult() {
		Vector<CarRoute> result = new Vector<CarRoute>();
		CarRoute car = new CarRoute();
		car.route.add(0);
		for (int i = 0; i < INIT_CAR_NUM; ++i) {
			car.cartype = 1;
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
