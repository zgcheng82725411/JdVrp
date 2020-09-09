package jingdong;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class jingdong {

	static final double MAX_DOUBLE = 100000000;
	static final double A = 0.4;
	static final double B = 1.2;
	static final double C = 1.1;
	static final int probability = 80;
	static final double GOBACK = 0.99;
	static final int SEED = 1;
	static final int INIT_CAR_NUM = 170;
	private static final int Integer = 0;

	static Vector<Integer> sellMatchCharger;

	public static void main(String[] args) throws IOException {
		Vector<Vehicle> vehicle = new Vector<Vehicle>(); // ���泵����Ϣ
		vehicle.add(new Vehicle(1, "iveco", 12, 2.0, 100000, 0.5, 0.012, 200));
		vehicle.add(new Vehicle(2, "truck", 16, 2.5, 120000, 0.5, 0.014, 300));

		// ����input_node����
		Vector<Node> nodeList = new Vector<Node>();

		// ��ȡinput_node.txt����
		read_node(nodeList);

		// 1-1000�̻���Ϣ
		List<Node> seller = nodeList.subList(1, 1001);

		// ���׮��Ϣ
		List<Node> charger = nodeList.subList(1001, nodeList.size());
		
		sellMatchCharger = new Vector<Integer>();
		Vector<Vector<Integer>> distance = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> time = new Vector<Vector<Integer>>();

		read_distance(distance, time);

		// ����sellMatchCharger;
		// �±��Ӧsell��id�������� charger���±�
		calSellChargerMatch(sellMatchCharger, distance, charger, seller);

		Vector<CarRoute> result = new Vector<CarRoute>();
		//result = execute(seller, charger, distance, time, vehicle);

		result = synchroSearch(seller, charger, distance, time, vehicle);

		calAllCost(result, vehicle);
		// �Ŵ��㷨
		// inheritance(result, distance, time, vehicle, nodeList,charger);

		// showResult(result);

		// changeVehicleType(result,distance,vehicle); //ת��������ͺ�
		// changeVehicleType2(result, distance, time, vehicle, nodeList,
		// charger);

		// sort(result.begin(), result.end(), lessVehicleType);

		// vector<AnswerRecord> answer;
		// answer = getAnswer(result, vehicle);

		// whrit_answer(answer);
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
	
	private static void calSellChargerMatch(Vector<java.lang.Integer> vec,
			Vector<Vector<java.lang.Integer>> distance, List<Node> charger,
			List<Node> seller) {
		vec.add(0);
		for (Node sell : seller) {
			int chargerIndex = findCharge(sell, distance, charger);
			vec.add(chargerIndex);
		}
	}

	// ����sellMatchCharger;�±��Ӧsell��id�������� charger���±�
	private static void calSellChargerMatch(Vector<Integer> vec,
			Vector<Vector<Integer>> distance, Vector<Node> charger,
			Vector<Node> seller) {
		vec.add(0);
		for (Node sell : seller) {
			int chargerIndex = findCharge(sell, distance, charger);
			vec.add(chargerIndex);
		}
	}

	private static void read_node(Vector<Node> vec) throws IOException {

		String strbuff;
		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream("D:\\soft\\code\\gitworkspace\\"
						+ "JdVrp\\jingdong\\src\\jingdong\\input_node.txt")));

		strbuff = data.readLine();
		int lineNum = 0;
		while (strbuff!= null && strbuff != "") {
			// ��ȡһ�����ݣ����ݸ�ʽ1 6734 1453

			Node tempNode = new Node();
			// �ַ��ָ�				
			String[] strcol = strbuff.split("\t");
			if(lineNum == 0)
			{
				strbuff = data.readLine();
				lineNum++;
				continue;
			}
			else if (lineNum == 1) {
				tempNode.weight = tempNode.volume = 0;
				tempNode.first_int_tm = 0;
				tempNode.last_int_tm = 960;
				tempNode.isServer = false;
				
			} else if (lineNum > 1 && lineNum < 1001) {
				double k = Double.valueOf(strcol[0]);
				tempNode.id = (int) (k);
				k = Double.valueOf(strcol[1]);
				tempNode.type = (int) (k);
				tempNode.lng = Double.valueOf(strcol[2]);
				tempNode.lat = Double.valueOf(strcol[3]);
				tempNode.weight = Double.valueOf(strcol[4]);
				tempNode.volume = Double.valueOf(strcol[5]);
				tempNode.first_int_tm = changeTime(strcol[6]);
				tempNode.last_int_tm = changeTime(strcol[7]);
			} else {
				double k = Double.valueOf(strcol[0]);
				tempNode.id = (int) (k);
				k = Double.valueOf(strcol[1]);
				tempNode.type = (int) (k);
				tempNode.lng = Double.valueOf(strcol[2]);
				tempNode.lat = Double.valueOf(strcol[3]);

				tempNode.weight = tempNode.volume = 0;
				tempNode.first_receive_tm = tempNode.last_receive_tm = "";
				tempNode.first_int_tm = -1;
				tempNode.last_int_tm = -1;
				tempNode.isServer = false;
			}
			vec.add(tempNode);
			strbuff = data.readLine();
			lineNum++;
		}
	}

	private static void read_distance(Vector<Vector<Integer>> dis,
			Vector<Vector<Integer>> time) throws IOException {
		String strbuff;
		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream("D:\\soft\\code\\gitworkspace\\"
						+ "JdVrp\\jingdong\\src\\jingdong\\input_distance_time.txt")));
		strbuff = data.readLine();
		int lineNum = 0;
		for (int i = 0; i <= 1100; ++i) {
			Vector<Integer> di = new Vector<Integer>();
			Vector<Integer> tm = new Vector<Integer>();
			for (int j = 0; j <= 1100; ++j) {
				if (i == j) {
					di.add(0);
					tm.add(0);
				} else {
					strbuff = data.readLine();
					double a, d, t;
					char c;
					String[] strcol = strbuff.split(",");					
					d = Double.valueOf(strcol[3]);
					t = Double.valueOf(strcol[4]);
					di.add(new Double(d).intValue());
					tm.add(new Double(t).intValue());
				}
			}
			dis.add(di);
			time.add(tm);
		}
	}

	private static int findCharge(Node sell, Vector<Vector<Integer>> distance,
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

	public static int changeTime(String str) // ��ʱ�任��int
	{
		if (str == "00:00")
			return 960;
		else {
			// istringstream stream(str);
			int h = 0, m = 0;
			char c;
			String[] strcol = str.split(":");
			
			double tempH = (Double.valueOf(strcol[0]));
			double tempM = (Double.valueOf(strcol[1]));
			h = (int) (tempH);
			m = (int) (tempM); 
			return (h - 8) * 60 + m;
		}
	}

	// �����ڵ� �����������·��
	public static Vector<CarRoute> execute(List<Node> seller,
			List<Node> charger, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle) {
		Vector<CarRoute> result = new Vector<CarRoute>();
		while (!isAllserver(seller)) {
			CarRoute curCar = new CarRoute();
			curCar.route.add(0); // ����������Ϊ���������
			while (isConstraint(curCar, vehicle)) // ѡ��ڵ�
			{
				int indexMin;
				indexMin = nextVisitIndex(curCar, seller, distance, time,
						vehicle); // ��ͨ�����վ��ֱ�ӿ��Է��ʵ�����̻�
				// if (rand() % 100 < probability)
				// indexMin = nextVisitIndex(curCar, seller, distance, time,
				// vehicle); //��ͨ�����վ��ֱ�ӿ��Է��ʵ�����̻�
				// else
				// indexMin = nextRandomVisitIndex(curCar, seller, distance,
				// time, vehicle);

				if (indexMin != -1) // �ҵ�����һ�����ʵ��̻�
				{
					visitSeller(curCar, seller.get(indexMin), distance, time);
				} else {
					if (!isGoback(curCar, vehicle)) {
						int sellIndex = nextSellCharger(curCar, seller,
								charger, distance, time, vehicle); // �ȵ����̻���ȥ���

						if (sellIndex != -1) // �ҵ����� �ȵ����̻���ȥ��������̻�
						{
							visitSeller(curCar, seller.get(sellIndex),
									distance, time); // �����̻�

							// ������վ����
							int closeChargerIndex = sellMatchCharger.get(seller
									.get(sellIndex).id); 
							visitCharge(curCar, charger.get(closeChargerIndex),
									distance, time);
						} else {
							// cout << "���ֽ�����";
							// for (size_t i = 0; i < curCar.route.size(); ++i)
							// cout << curCar.route[i] << " ";
							// cout << endl;
							break;
						}

					} else {
						// cout << "�����ӽ����أ����ֽ���:";
						// for (size_t i = 0; i < curCar.route.size(); ++i)
						// cout << curCar.route[i] << " ";
						// cout << endl;
						break;
					}
				}
			}
			result.add(curCar);
		}
		// changeVehicleType(result, distance, vehicle); //ת��������ͺ�
		return result;

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

	
	
	private static int nextSellCharger(CarRoute curCar, List<Node> seller,
			List<Node> charger, Vector<Vector<java.lang.Integer>> distance,
			Vector<Vector<java.lang.Integer>> time, Vector<Vehicle> vehicle) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static boolean isGoback(CarRoute curCar, Vector<Vehicle> vehicle) {
		if (curCar.curVolume / vehicle.get(curCar.cartype - 1).volume > GOBACK) //��ǰ�����Ѿ�ʹ��80%ֱ�ӷ���
		{
			//cout << "��������" << curCar.curVolume << " " << vehicle[curCar.cartype - 1].volume << endl;
			return true;
		}
		if (curCar.curWeight / vehicle.get(curCar.cartype - 1).weight > GOBACK)
		{
			//cout << "���ؼ���" << curCar.curWeight << " " << vehicle[curCar.cartype - 1].weight << endl;
			return true;
		}
		return false;
	}

	private static void visitSeller(CarRoute curCar, Node sell,
			Vector<Vector<java.lang.Integer>> distance,
			Vector<Vector<java.lang.Integer>> time) {
		//����sell�̻�
		sell.isServer = true;
		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size()-1);
		curCar.curMileage = curCar.curMileage + distance.get(lastIndex).get(sell.id)
			+ distance.get(sell.id).get(0) - distance.get(lastIndex).get(0);

		int arriveTime = curCar.curTime - time.get(lastIndex).get(0) + 
				time.get(lastIndex).get(sell.id);
		int waitT = sell.first_int_tm > arriveTime ?
			sell.first_int_tm - arriveTime : 0;
		//���³���ʱ��,����ʱ��Ϊ �����ڵ�һ�ε����̻��ĵȴ�ʱ��
		if (curCar.leaveTime == -1)
		{
			curCar.leaveTime = waitT;
		}
		else {
			curCar.waitTime += waitT;
		}

		curCar.curTime = arriveTime + waitT + 30 + time.get(sell.id).get(0);
		//����ʱ��+�ȴ�+����+����

		curCar.route.add(sell.id);
		curCar.totalMileage = curCar.totalMileage + distance.get(lastIndex).get(sell.id)
			+ distance.get(sell.id).get(0) - distance.get(lastIndex).get(0);

		curCar.backTime = curCar.curTime;
		//curCar.waitTime += waitT;

	}

	public static int nextVisitIndex(CarRoute curCar, List<Node> seller,
			Vector<Vector<Integer>> distance, Vector<Vector<Integer>> time,
			Vector<Vehicle> vehicle) {
		double minWeight = Double.MAX_VALUE;
		int indexMin = -1; // ������һ�����ʵ��̻�
		for (int i = 0; i < seller.size(); ++i) // �����̻���Ѱ����һ���ڵ�
		{
			if (!seller.get(i).isServer
					&& canVisit(curCar, vehicle, seller.get(i), distance, time)) // sell[i]δ�����ʣ��ҿ��Է���
			{ // ���ҿ��Է���
				double weight = calWeight(curCar, vehicle, seller.get(i),
						distance, time);
				if (weight < minWeight) {
					minWeight = weight;
					indexMin = i;
				}

			}
		}
		return indexMin;
	}

	public static double calWeight(CarRoute curCar, Vector<Vehicle> vehicle,
			Node sell, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time) {
		int lastIndex = curCar.route.get(curCar.route.size() - 1);
		double dis = distance.get(lastIndex).get(sell.id);
		double arriveTime = curCar.curTime - time.get(lastIndex).get(0)
				+ time.get(lastIndex).get(sell.id);
		// ʱ�䴰Լ��
		if (arriveTime > sell.last_int_tm)
			return Double.MAX_VALUE;

		double waitTime = sell.first_int_tm > arriveTime ? sell.first_int_tm
				- arriveTime : 0;
		double canDelayTime = Math.min(sell.last_int_tm - sell.first_int_tm,
				sell.last_int_tm - arriveTime);

		// return A * time[lastIndex][sell.id]+ B * waitTime + C * canDelayTime
		// ; //��ʱ��ΪȨ��

		return A * (dis * vehicle.get(curCar.cartype - 1).unit_trans_cost) // �Է���
				+ B * (waitTime * 0.40) + C * (canDelayTime * 0.40);
		// return A * (dis * vehicle[curCar.cartype - 1].unit_trans_cost)
		// + C * (canDelayTime * 0.4);
	}

	public static boolean isAllserver(List<Node> seller) {
		for (int i = 0; i < seller.size(); ++i) {
			if (seller.get(i).isServer == false)
				return false;
		}
		return true;
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

	// ��ʼ��vector<CarRoute> result
	public static Vector<CarRoute> initResult() {
		Vector<CarRoute> result = new Vector<CarRoute>();
		CarRoute car = new CarRoute();
		car.route.add(0);
		for (int i = 0; i < INIT_CAR_NUM; ++i) {
			car.cartype = 1;
			result.add(car);
		}
		return result;
	}

	// ͬ������
	public static Vector<CarRoute> synchroSearch(List<Node> seller,
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

		for (Node sell : seller) {
			int carIndex = whichCarVisitThisSell(result, sell, charger,
					distance, time, vehicle);
			if (carIndex != -1) {
				if (canVisit(result.get(carIndex), vehicle, sell, distance,
						time)) {
					visitSeller(result.get(carIndex), sell, distance, time);
				} else if (bySellChargeCanVisit(result.get(carIndex), vehicle,
						sell, distance, time, charger)) {
					visitSeller(result.get(carIndex), sell, distance, time); // �����̻�

					int closeChargerIndex = findCharge(sell, distance, charger); // ������վ����
					visitCharge(result.get(carIndex),
							charger.get(closeChargerIndex), distance, time);
				}
			} else {
				CarRoute tem = new CarRoute();
				tem.route.add(0);
				result.add(tem);
				visitSeller(result.get(result.size() - 1), sell, distance, time);
			}
		}
		deleteEmptyCar(result);
		return result;
	}

	// ɾ���յĻ���
	public static void deleteEmptyCar(Vector<CarRoute> result) {
		Vector<CarRoute> ret = new Vector<CarRoute>();
		for (CarRoute car : result) {
			if (car.route.size() > 1)
				ret.add(car);
		}
		result = ret;
	}

	// ������ȥ�����������
	public static int whichCarVisitThisSell(Vector<CarRoute> result, Node sell,
			List<Node> charger, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle) {
		int carIndex = -1;
		double minvisitCost = MAX_DOUBLE;

		for (int i = 0; i < result.size(); ++i) {
			CarRoute curCar = result.get(i);
			// ����curCar�Ƿ���Բ�������� ֱ�ӷ�������
			if (canVisit(curCar, vehicle, sell, distance, time)) {
				double curCost = 0;
				// ·������
				curCost += distance.get(
						curCar.route.get(curCar.route.size() - 1)).get(sell.id)
						* vehicle.get(curCar.cartype - 1).unit_trans_cost;
				// //�ȴ�����,�״γ���û�еȴ�����
				// if (curCar.route.back()!=0)
				// {
				// int arriveTime = curCar.curTime -
				// time[curCar.route.back()][0] +
				// time[curCar.route.back()][sell.id];
				// int waitT = sell.first_int_tm > arriveTime ?
				// sell.first_int_tm - arriveTime : 0;
				// curCost += 0.4 * waitT;

				// }

				if (curCost < minvisitCost) {
					carIndex = i;
					minvisitCost = curCost;
				}
			}
			// ����curCar�������վ�����ܷ�������
			else if (bySellChargeCanVisit(curCar, vehicle, sell, distance,
					time, charger)) {
				double curCost = 0;

				// ·������
				curCost += distance.get(
						curCar.route.get(curCar.route.size() - 1)).get(sell.id)
						* vehicle.get(curCar.cartype - 1).unit_trans_cost;
				// int closeChargerIndex = findCharge(curCar, sell, distance,
				// time, charger);
				// curCost += (distance[curCar.route.back()][sell.id] +
				// distance[sell.id][charger[closeChargerIndex].id])
				// * vehicle[curCar.cartype - 1].unit_trans_cost;

				// //��绨��
				// curCost += 50;

				// //�ȴ�����
				// if (curCar.route.back() != 0)
				// {
				// int arriveTime = curCar.curTime -
				// time[curCar.route.back()][0] +
				// time[curCar.route.back()][sell.id];
				// int waitT = sell.first_int_tm > arriveTime ?
				// sell.first_int_tm - arriveTime : 0;
				// curCost += 0.4 * waitT;

				// }

				if (curCost < minvisitCost) {
					carIndex = i;
					minvisitCost = curCost;
				}
			}
		}
		return carIndex;
	}

	// ������bug
	public static boolean bySellChargeCanVisit(CarRoute curCar,
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

	// seller����ıȽϺ���
	public static boolean timeCompare(Node node1, Node node2) {
		return node1.last_int_tm < node2.last_int_tm;
	}
}
