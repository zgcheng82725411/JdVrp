package jingdong;

import java.util.ArrayList;
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
	
	//�ٵ�����
	int overTimeOrWeight= 200000;

	Random random = new Random();

	Vector<Integer> sellMatchCharger; // �±��Ӧsell��id�������� charger���±�
	
	Vector<Vehicle> vehicle1 = new Vector<Vehicle>(); // ���泵����Ϣ
	
	

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

	
////����Ⱥ�Ż��������Ŵ��㷨
private void inheritance(Vector<CarRoute> result,  Vector<Vector<Integer>> distance,
		Vector<Vector<Integer>> time,  Vector<Vehicle> vehicle,  Vector<Node> nodeList)
{
	vehicle1.clear();
	vehicle1.add(new Vehicle(1, "iveco", 12, 2.0, 100000, 0.5, 0.012, 200));
	vehicle1.add(new Vehicle(2, "truck", 16, 2.5, 120000, 0.5, 0.014, 300));
		
	Vector<Vector<CarRoute>> population = initPopulation(result); //��Ⱥ��ʼ��
	//������Ӧ��
	
	List<Fit> fitness = new ArrayList<Fit>();
	
	for(int i = 0; i < population.size();i++)
	{
		double fits = 1/calAllCost(population.get(i), vehicle);
		Fit theFit = new Fit();
		theFit.setRouteList(population.get(i));
		theFit.setFitness(fits);
		fitness.add(theFit);
	}
	
	//ѡ�����ŵ�һ�����������̷�ѡ��N-1��
	//Fit sel = selectBest(fitness);
	List<Fit> newFitness = selectFit(fitness);
		
	//���ѡ2�����������,���ŵĲ�������졣������Ⱥ
			
	}
	// population[0] ��һ������С�ģ��������compareֻ�Ƚ��ܾ���
	
private Fit selectBest(List<Fit> fitness) {
	
	return null;
}

private List<Fit> selectFit(List<Fit> fitness) {
	
	double sum = 0;
	Fit bestFit = fitness.get(0); 
	for(int i = 0; i < fitness.size();i++)
    {
	    sum += fitness.get(i).getFitness();
	    if(bestFit.getFitness() < fitness.get(i).getFitness())
	    {
	    	bestFit = fitness.get(i);
	    }
	}
	fitness.remove(bestFit);
	
	List<Fit> newfitness = new ArrayList<Fit>();
	newfitness.add(bestFit);
	double p[] = new double[fitness.size()];
	//����һ��ϵ�����ܺ�Ϊ1	 
	for(int i= 0; i < fitness.size(); i++)
    {
        p[i] = fitness.get(i).getFitness()/sum;
    }
	
	double sleectP = random.nextFloat();
    int selectCity = 0;
    double sum1 = 0;
    for (int i = 0; i < fitness.size(); i++) {
        sum1 += p[i];
        if (sum1 >= sleectP) {
            selectCity = i;
            newfitness.add(fitness.get(i));
            break;
        }
    }
     
    //���ŵı���
    return newfitness;	
}



//���������ķ���
	public static double calAllCost( Vector<CarRoute> result,  Vector<Vehicle> vehicle)
	{
		////������λС��
		//record.trans_cost = result[i].totalMileage * vehicle[result[i].cartype - 1].unit_trans_cost;

		//record.charge_cost = result[i].chargeNum * 50;
		////������λС��
		//record.wait_cost = result[i].waitTime * 0.4;

		//record.fixed_use_cost = vehicle[result[i].cartype - 1].vechile_cost;
		double all_cost = 0;
		for (int i = 0; i < result.size(); ++i)
		{
			all_cost += result.get(i).totalMileage * vehicle.get(result.get(i).cartype - 1).unit_trans_cost;
			all_cost += result.get(i).chargeNum * 50;
			all_cost += result.get(i).waitTime * 0.4;
			all_cost += vehicle.get(result.get(i).cartype - 1).vechile_cost;
		}
		return all_cost;
	}
	



//���в����ŵ�һ�飬�ڽ�����֤
public void allOperationCarRoute(CarRoute car1, CarRoute car2,  Vector<Vector<Integer>> distance,
		Vector<Vector<Integer>> time, Vector<Vehicle> vehicle, Vector<Node> nodeList)
{
	CarRoute temCar1 =car1;
	CarRoute temCar2 = car2;
	double minCost = calOneCost(car1, vehicle) + calOneCost(car2, vehicle);
	int cir = REPEAT_NUM;
	while (--cir > 0)
	{
		int num = random.nextInt() % CHANGE_NUM + 1;
		//����
		for (int i = 0; i < num; ++i)
		{
			//·��Ϊ�յ����
			if (temCar1.route.size() == 1 || temCar2.route.size() == 1)
				break;
			int d1 = random.nextInt() % (temCar1.route.size() - 1) + 1;
			int d2 = random.nextInt() % (temCar2.route.size() - 1) + 1;
					
			int temp = temCar1.route.get(d1);
			temCar1.route.set(d1, temCar2.route.get(d2));
			temCar2.route.set(d2, temp);
		}
	
		//����
		for (int i = 0; i < num; ++i)
		{
			//·��Ϊ�յ����
			if (temCar1.route.size() <= 2)
				break;
			int d1 = random.nextInt() % (temCar1.route.size() - 1) + 1;
			int d2 = random.nextInt() % (temCar1.route.size() - 1) + 1;
			if (d1 == d2)
				continue;
			//swap(temCar1.route[d1], temCar1.route[d2]);
			int temp = temCar1.route.get(d1);
			temCar1.route.set(d1, temCar1.route.get(d2));
			temCar1.route.set(d2, temp);
		}
		for (int i = 0; i < num; ++i)
		{
			//·��Ϊ�յ����
			if (temCar2.route.size() <= 2)
				break;
			int d1 = random.nextInt() % (temCar2.route.size() - 1) + 1;
			int d2 = random.nextInt() % (temCar2.route.size() - 1) + 1;
			if (d1 == d2)
				continue;
			//swap(temCar2.route[d1], temCar2.route[d2]);
			int temp = temCar2.route.get(d1);
			temCar2.route.set(d1, temCar2.route.get(d2));
			temCar2.route.set(d2, temp);
		}			
	}
}

//Ⱥ���ʼ��
public Vector<Vector<CarRoute>> initPopulation( Vector<CarRoute> result)
{
	Vector<Vector<CarRoute>> results = new Vector<Vector<CarRoute>>();
	for (int i = 0; i < POPULATION_NUM; ++i)
	{
		results.add(result);
	}
	return results;
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
