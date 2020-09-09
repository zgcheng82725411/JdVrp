package jingdong;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Ga {

	// ��Ⱥ�и��������
	int POPULATION_NUM = 10;

	// ÿ��ִ�е�������
	int REPEAT_NUM = 20;

	// �Ŵ�����
	int ITERATION_NUM = 500000;

	// ��������
	int CIRCAL_TIME = 20000;

	// ִ�������ٴα仯������֤���е���Ч��
	int CHANGE_NUM = 5;

	// �����ֲ����Ŵ����
	double JUMP_P = 95;

	// �ɽ��ܱ��ķ�Χ
	double ACCEPT = 0.01;

	Random random = new Random();

	Vector<Integer> sellMatchCharger; // �±��Ӧsell��id�������� charger���±�

	// ����sellMatchCharger;�±��Ӧsell��id�������� charger���±�
	public void calSellChargerMatch(Vector<Integer> vec,
			Vector<Vector<Integer>> distance, Vector<Node> charger,
			Vector<Node> seller) {
		vec.add(0);
		for (Node sell : seller) {
			int chargerIndex = findCharge(sell, distance, charger);
			vec.add(chargerIndex);
		}
	}

	private int findCharge(Node sell, Vector<Vector<Integer>> distance,
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

	// �������壬���棬���죬���ҽ������Ŵ��㷨
	public void inheritance(Vector<CarRoute> result,
			Vector<Vector<Integer>> distance, Vector<Vector<Integer>> time,
			Vector<Vehicle> vehicle, Vector<Node> nodeList, Vector<Node> charger) {
		// �������壬���棬���죬���ҽ������Ŵ��㷨
		int c = CIRCAL_TIME;
		while (--c > 0) {

			for (int i = 0; i < result.size(); ++i) {
				for (int j = 0; j < result.size(); ++j)
					if (i != j)
						exchangeCarRoute2(result.get(i), result.get(j),
								distance, time, vehicle, nodeList, charger); // �������

			}
			for (int i = 0; i < result.size(); ++i) {
				for (int j = 0; j < result.size(); ++j)
					if (i != j)
						addOrDeleteCarRoute2(result.get(i), result.get(j),
								distance, time, vehicle, nodeList, charger); // �������

			}
			for (int i = 0; i < result.size(); ++i) {
				selfExchangeCarRoute(result.get(i), distance, time, vehicle,
						nodeList, charger); // ���ҽ���
			}
			for (int i = 0; i < result.size(); ++i) {
				selfAddOrDeleteCarRoute(result.get(i), distance, time, vehicle,
						nodeList, charger); // ���ҵ���
			}

			// ÿ���� 2 �� �� д���ļ�
			if ((CIRCAL_TIME - c) % 5 == 0) {
				// vector<CarRoute> tem = result;

				// changeVehicleType(result, distance, vehicle); //ת��������ͺ�
				changeVehicleType2(result, distance, time, vehicle, nodeList,
						charger);
				deleteEmptyCar(result);

				sort(result.begin(), result.end(), lessVehicleType);

				Vector<AnswerRecord> answer;
				answer = getAnswer(result, vehicle);

				whrit_answer(answer);
			}
		}
	}

	// ���Ž��棬���򲻱䣬���������
	public void exchangeCarRoute2(CarRoute car1, CarRoute car2,
			Vector<Vector<Integer>> distance, Vector<Vector<Integer>> time,
			Vector<Vehicle> vehicle, Vector<Node> nodeList, Vector<Node> charger) {
		// �����
		CarRoute temCar1 = car1;
		CarRoute temCar2 = car2;
		double minCost = calOneCost(car1, vehicle) + calOneCost(car2, vehicle);
		int cir = REPEAT_NUM;
		while (--cir > 0) {
			int num = random.nextInt() % CHANGE_NUM + 1;
			for (int i = 0; i < num; ++i) {
				// ·��Ϊ�յ����
				if (temCar1.route.size() == 1 || temCar2.route.size() == 1)
					continue;
				int d1 = random.nextInt() % (temCar1.route.size() - 1) + 1;
				int d2 = random.nextInt() % (temCar2.route.size() - 1) + 1;
				// swap(temCar1.route[d1], temCar2.route[d2]);
				int temp = temCar1.route.get(d1);
				temCar1.route.set(d1, temCar2.route.get(d2));
				temCar2.route.set(d2, temp);
			}

			if (isLegal(temCar1, distance, time, vehicle, nodeList, charger)
					&& isLegal(temCar2, distance, time, vehicle, nodeList,
							charger)) {
				double cost1 = calOneCost(temCar1, vehicle);
				double cost2 = calOneCost(temCar2, vehicle);
				if (cost1 + cost2 < minCost
						|| (random.nextInt() % 100 > JUMP_P && cost1 + cost2
								- minCost < ACCEPT * minCost)) // Ч����
				{
					car1 = temCar1;
					car2 = temCar2;
					minCost = cost1 + cost2;
					return;
				} else // ����õĽ������ԭtemCar��״̬
				{
					temCar1 = car1;
					temCar2 = car2;
				}

			} else {
				temCar1 = car1;
				temCar2 = car2;
			}
		}
	}

	//������ӻ�ɾ�������򲻱�
	public void addOrDeleteCarRoute2(CarRoute car1, CarRoute car2,  Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time,  Vector<Vehicle> vehicle,  Vector<Node> nodeList,  Vector<Node> charger)
	{
		CarRoute temCar1 = car1;
		CarRoute temCar2 =car2;
		double minCost = calOneCost(car1, vehicle) + calOneCost(car2, vehicle);
		//typedef vector<int>::iterator Iter;
		//Iterator Iter = temCar1.iterator();
		int cir = REPEAT_NUM;
		while (--cir > 0)
		{
			int num = rand() % CHANGE_NUM + 1;
			for (int  i = 0; i < num; ++i)
			{
				//��ֹ����·��Ϊ�յ����
				if (temCar1.route.size() == 2 && temCar2.route.size() == 2)
				{
					continue;
				}
				if (temCar1.route.size() == 2)
					swap(temCar1, temCar2);
				if (temCar1.route.size() == 1 || temCar2.route.size() == 1)
					continue;
				int d1 = rand() % (temCar1.route.size() - 1) + 1;
				int d2 = rand() % (temCar2.route.size() - 1) + 1;
				Iterator  iter1 = temCar1.route.begin() + d1;
				Iterator  iter2 = temCar2.route.begin() + d2;
				int deleteVal = *iter1;
				temCar1.route.erase(iter1);
				temCar2.route.insert(iter2, deleteVal);
			}

			if (isLegal(temCar1, distance, time, vehicle, nodeList, charger) 
				&& isLegal(temCar2, distance, time, vehicle, nodeList, charger))
			{
				double cost1 = calOneCost(temCar1, vehicle);
				double cost2 = calOneCost(temCar2, vehicle);
				if (cost1 + cost2 < minCost ||
					(rand() % 100 > JUMP_P && cost1 + cost2 - minCost < ACCEPT * minCost)) //Ч����
				{
					car1 = temCar1;
					car2 = temCar2;
					minCost = cost1 + cost2;
					return;
				}
				else  //����õĽ������ԭtemCar��״̬
				{
					temCar1 = car1;
					temCar2 = car2 ;
				}

			}
			else
			{
				temCar1 = car1;
				temCar2 = car2;
			}
		}
	}
	// ��������
	public double calOneCost(CarRoute car, Vector<Vehicle> vehicle) {
		// //������λС��
		// record.trans_cost = result[i].totalMileage *
		// vehicle[result[i].cartype - 1].unit_trans_cost;

		// record.charge_cost = result[i].chargeNum * 50;
		// //������λС��
		// record.wait_cost = result[i].waitTime * 0.4;

		// record.fixed_use_cost = vehicle[result[i].cartype - 1].vechile_cost;
		if (car.route.size() == 1)
			return 0;

		double all_cost = 0;

		all_cost += car.totalMileage
				* vehicle.get(car.cartype - 1).unit_trans_cost;
		all_cost += car.chargeNum * 50;
		all_cost += car.waitTime * 0.4;
		all_cost += vehicle.get(car.cartype - 1).vechile_cost;

		return all_cost;
	}

	// �Ϸ����޸� �����Ϸ��Ͳ���
	public boolean isLegal(CarRoute car, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle,
			Vector<Node> nodeList, Vector<Node> charger) {
		CarRoute tempCar = new CarRoute();
		tempCar.route.add(0);

		for (int i = 1; i < car.route.size(); ++i) {
			Node sell = nodeList.get(car.route.get(i));
			int lastIndex = tempCar.route.get(tempCar.route.size() - 1);
			if (car.route.get(i) < 1001) {

				if (canVisit(tempCar, vehicle, sell, distance, time)) {
					visitSeller(tempCar, sell, distance, time);
				} else if (bySellChargeCanVisit(tempCar, vehicle, sell,
						distance, time, charger)) // ���Կ��ǸĽ�
				{
					visitSeller(tempCar, sell, distance, time);
					int chargeIndex = sellMatchCharger.get(sell.id);
					visitCharge(tempCar, charger.get(chargeIndex), distance,
							time);
				} else
					return false;
			}
		}

		// �ı�����ͺ�
		isChangeVehicleType(tempCar, distance, time, vehicle, nodeList, charger);

		car = tempCar;
		return true;

	}

	//�������ͺ��Ƿ���Ը��ģ��������ֱ�Ӹ��ģ���������ԣ�����false
		public boolean isChangeVehicleType(CarRoute car,  Vector<Vector<Integer>> distance,
				Vector<Vector<Integer>> time,  Vector<Vehicle> vehicle,  Vector<Node> nodeList, Vector<Node> charger)
		{
			CarRoute tempCar = new CarRoute();
			if (car.cartype == 2)
				tempCar.cartype = 1;
			else
				tempCar.cartype = 2;

			tempCar.route.add(0);

			for (int i = 1; i < car.route.size(); ++i)
			{
				Node sell = nodeList.get(car.route.get(i));
				int lastIndex = tempCar.route.get(tempCar.route.size()-1);
				if (car.route.get(i) < 1001)
				{

					if (canVisit(tempCar, vehicle, sell, distance, time))
					{
						visitSeller(tempCar, sell, distance, time);
					}
					else if (bySellChargeCanVisit(tempCar, vehicle, sell, distance, time, charger))  //���Կ��ǸĽ�
					{
						visitSeller(tempCar, sell, distance, time);
						int chargeIndex = sellMatchCharger.get(sell.id);
						visitCharge(tempCar, charger.get(chargeIndex), distance, time);
					}
					else
						return false;
				}
			}

			//����car���ͺ�
			if(calOneCost(car, vehicle) > calOneCost(tempCar,vehicle))
				car = tempCar;
			return true;
		}
		
	private static void visitCharge(CarRoute curCar, Node charger,
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
	
	// ������bug
	public boolean bySellChargeCanVisit(CarRoute curCar,
			Vector<Vehicle> vehicle, Node sell,
			Vector<Vector<Integer>> distance, Vector<Vector<Integer>> time,
			List<Node> charger)

	{
		int closeChargerIndex = sellMatchCharger.get(sell.id);

		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size() - 1); // ·�������һ���̻��ڵ�
		// cout<<curCar.curMileage<<" "<<lastIndex<<" "<<sell.id<<" "<<closeChargerIndex<<endl;
		curCar.curMileage = curCar.curMileage
				+ distance.get(lastIndex).get(sell.id)
				+ distance.get(sell.id).get(charger.get(closeChargerIndex).id)
				- distance.get(lastIndex).get(0);
		// cout<<curCar.curMileage<<endl;
		// ʱ�䴰Լ����
		// ����ʱ��������̻�����ٽ���ʱ��֮ǰ
		int arriveTime = curCar.curTime - time.get(lastIndex).get(0)
				+ time.get(lastIndex).get(sell.id);
		if (arriveTime > sell.last_int_tm)
			return false;

		int waitT = sell.first_int_tm > arriveTime ? sell.first_int_tm
				- arriveTime : 0;

		curCar.curTime = arriveTime + waitT + 30
				+ time.get(sell.id).get(charger.get(closeChargerIndex).id)
				+ time.get(charger.get(closeChargerIndex).id).get(0);

		// cout<<curCar.curMileage<<" "<<curCar.curVolume<<" "<<curCar.curWeight<<" "<<curCar.curTime<<endl;

		return isConstraint(curCar, vehicle);

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

	// ����curCar ����������Ƿ����ֱ�ӷ���sell
	private static boolean canVisit(CarRoute curCar, Vector<Vehicle> vehicle,
			Node sell, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time) {
		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size() - 1); // ·�������һ���̻��ڵ�
		curCar.curMileage = curCar.curMileage
				+ distance.get(lastIndex).get(sell.id)
				+ distance.get(sell.id).get(0) - distance.get(lastIndex).get(0);
		// ʱ�䴰Լ����
		// ����ʱ��������̻�����ٽ���ʱ��֮ǰ
		int arriveTime = curCar.curTime - time.get(lastIndex).get(0)
				+ time.get(lastIndex).get(sell.id);
		if (arriveTime > sell.last_int_tm)
			return false;

		int waitT = sell.first_int_tm > arriveTime ? sell.first_int_tm
				- arriveTime : 0;

		curCar.curTime = arriveTime + waitT + 30 + time.get(sell.id).get(0);
		return isConstraint(curCar, vehicle);
	}

	public static boolean isConstraint(CarRoute curCar, Vector<Vehicle> vehicle) // Լ������
	{
		if (curCar.curVolume > vehicle.get(curCar.cartype - 1).volume) // ��������Լ��
		{
			return false;
		}
		if (curCar.curWeight > vehicle.get(curCar.cartype - 1).weight) // ��������Լ��
		{
			return false;
		}
		if (curCar.curMileage > vehicle.get(curCar.cartype - 1).driving_range)// ���Լ��
																				// ��ע�⣺�Ȳ�������;�������
		{
			return false;
		}
		if (curCar.curTime > 960) // ����ʱ��Լ��
		{
			return false;
		}
		return true;
	}
}
