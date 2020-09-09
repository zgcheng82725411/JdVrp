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
		Vector<Vehicle> vehicle = new Vector<Vehicle>(); // 保存车辆信息
		vehicle.add(new Vehicle(1, "iveco", 12, 2.0, 100000, 0.5, 0.012, 200));
		vehicle.add(new Vehicle(2, "truck", 16, 2.5, 120000, 0.5, 0.014, 300));

		// 保存input_node内容
		Vector<Node> nodeList = new Vector<Node>();

		// 读取input_node.txt操作
		read_node(nodeList);

		// 1-1000商户信息
		List<Node> seller = nodeList.subList(1, 1001);

		// 充电桩信息
		List<Node> charger = nodeList.subList(1001, nodeList.size());
		
		sellMatchCharger = new Vector<Integer>();
		Vector<Vector<Integer>> distance = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> time = new Vector<Vector<Integer>>();

		read_distance(distance, time);

		// 计算sellMatchCharger;
		// 下标对应sell的id，内容是 charger的下标
		calSellChargerMatch(sellMatchCharger, distance, charger, seller);

		Vector<CarRoute> result = new Vector<CarRoute>();
		//result = execute(seller, charger, distance, time, vehicle);

		result = synchroSearch(seller, charger, distance, time, vehicle);

		calAllCost(result, vehicle);
		// 遗传算法
		// inheritance(result, distance, time, vehicle, nodeList,charger);

		// showResult(result);

		// changeVehicleType(result,distance,vehicle); //转变货车的型号
		// changeVehicleType2(result, distance, time, vehicle, nodeList,
		// charger);

		// sort(result.begin(), result.end(), lessVehicleType);

		// vector<AnswerRecord> answer;
		// answer = getAnswer(result, vehicle);

		// whrit_answer(answer);
	}


	//单个方案的费用
	public static double calAllCost( Vector<CarRoute> result,  Vector<Vehicle> vehicle)
	{
		////保留两位小数
		//record.trans_cost = result[i].totalMileage * vehicle[result[i].cartype - 1].unit_trans_cost;

		//record.charge_cost = result[i].chargeNum * 50;
		////保留两位小数
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

	// 计算sellMatchCharger;下标对应sell的id，内容是 charger的下标
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
			// 读取一行数据，数据格式1 6734 1453

			Node tempNode = new Node();
			// 字符分割				
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

	public static int changeTime(String str) // 把时间换成int
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

	// 给定节点 获得汽车分配路线
	public static Vector<CarRoute> execute(List<Node> seller,
			List<Node> charger, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle) {
		Vector<CarRoute> result = new Vector<CarRoute>();
		while (!isAllserver(seller)) {
			CarRoute curCar = new CarRoute();
			curCar.route.add(0); // 配送中心作为货车的起点
			while (isConstraint(curCar, vehicle)) // 选择节点
			{
				int indexMin;
				indexMin = nextVisitIndex(curCar, seller, distance, time,
						vehicle); // 不通过充电站，直接可以访问的最佳商户
				// if (rand() % 100 < probability)
				// indexMin = nextVisitIndex(curCar, seller, distance, time,
				// vehicle); //不通过充电站，直接可以访问的最佳商户
				// else
				// indexMin = nextRandomVisitIndex(curCar, seller, distance,
				// time, vehicle);

				if (indexMin != -1) // 找到了下一个访问的商户
				{
					visitSeller(curCar, seller.get(indexMin), distance, time);
				} else {
					if (!isGoback(curCar, vehicle)) {
						int sellIndex = nextSellCharger(curCar, seller,
								charger, distance, time, vehicle); // 先到达商户再去充电

						if (sellIndex != -1) // 找到可以 先到达商户再去充电的最佳商户
						{
							visitSeller(curCar, seller.get(sellIndex),
									distance, time); // 到达商户

							// 最近充电站坐标
							int closeChargerIndex = sellMatchCharger.get(seller
									.get(sellIndex).id); 
							visitCharge(curCar, charger.get(closeChargerIndex),
									distance, time);
						} else {
							// cout << "此轮结束：";
							// for (size_t i = 0; i < curCar.route.size(); ++i)
							// cout << curCar.route[i] << " ";
							// cout << endl;
							break;
						}

					} else {
						// cout << "货车接近满载，此轮结束:";
						// for (size_t i = 0; i < curCar.route.size(); ++i)
						// cout << curCar.route[i] << " ";
						// cout << endl;
						break;
					}
				}
			}
			result.add(curCar);
		}
		// changeVehicleType(result, distance, vehicle); //转变货车的型号
		return result;

	}

	// 货车curCar 不经过充电是否可以直接访问sell
	private static boolean canVisit(CarRoute curCar, Vector<Vehicle> vehicle,
			Node sell, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time) {
		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size() - 1); // 路径的最后一个商户节点
		curCar.curMileage = curCar.curMileage
				+ distance.get(lastIndex).get(sell.id)
				+ distance.get(sell.id).get(0) - distance.get(lastIndex).get(0);
		// 时间窗约束，
		// 到达时间必须在商户的最迟接收时间之前
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
		// 访问充电站
		++curCar.chargeNum; // 充电次数+1
		int lastIndex = curCar.route.get(curCar.route.size() - 1);

		curCar.totalMileage = curCar.totalMileage
				- distance.get(lastIndex).get(0)
				+ distance.get(lastIndex).get(charger.id)
				+ distance.get(charger.id).get(0);

		curCar.curMileage = distance.get(charger.id).get(0);

		int arriveTime = curCar.curTime - time.get(lastIndex).get(0)
				+ time.get(lastIndex).get(charger.id);

		curCar.curTime = arriveTime + 30 + time.get(charger.id).get(0);
		// 到达时间+充电+返回

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
		if (curCar.curVolume / vehicle.get(curCar.cartype - 1).volume > GOBACK) //当前容量已经使用80%直接返回
		{
			//cout << "容量即满" << curCar.curVolume << " " << vehicle[curCar.cartype - 1].volume << endl;
			return true;
		}
		if (curCar.curWeight / vehicle.get(curCar.cartype - 1).weight > GOBACK)
		{
			//cout << "载重即满" << curCar.curWeight << " " << vehicle[curCar.cartype - 1].weight << endl;
			return true;
		}
		return false;
	}

	private static void visitSeller(CarRoute curCar, Node sell,
			Vector<Vector<java.lang.Integer>> distance,
			Vector<Vector<java.lang.Integer>> time) {
		//访问sell商户
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
		//更新出发时间,出发时间为 货车在第一次到达商户的等待时间
		if (curCar.leaveTime == -1)
		{
			curCar.leaveTime = waitT;
		}
		else {
			curCar.waitTime += waitT;
		}

		curCar.curTime = arriveTime + waitT + 30 + time.get(sell.id).get(0);
		//到达时间+等待+服务+返回

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
		int indexMin = -1; // 保存下一个访问的商户
		for (int i = 0; i < seller.size(); ++i) // 遍历商户，寻找下一个节点
		{
			if (!seller.get(i).isServer
					&& canVisit(curCar, vehicle, seller.get(i), distance, time)) // sell[i]未被访问，且可以访问
			{ // ，且可以访问
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
		// 时间窗约束
		if (arriveTime > sell.last_int_tm)
			return Double.MAX_VALUE;

		double waitTime = sell.first_int_tm > arriveTime ? sell.first_int_tm
				- arriveTime : 0;
		double canDelayTime = Math.min(sell.last_int_tm - sell.first_int_tm,
				sell.last_int_tm - arriveTime);

		// return A * time[lastIndex][sell.id]+ B * waitTime + C * canDelayTime
		// ; //以时间为权重

		return A * (dis * vehicle.get(curCar.cartype - 1).unit_trans_cost) // 以费用
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

	public static boolean isConstraint(CarRoute curCar, Vector<Vehicle> vehicle) // 约束条件
	{
		if (curCar.curVolume > vehicle.get(curCar.cartype - 1).volume) // 货车容量约束
		{
			return false;
		}
		if (curCar.curWeight > vehicle.get(curCar.cartype - 1).weight) // 货车载重约束
		{
			return false;
		}
		if (curCar.curMileage > vehicle.get(curCar.cartype - 1).driving_range)// 里程约束
																				// 。注意：先不考虑中途充电的情况
		{
			return false;
		}
		if (curCar.curTime > 960) // 返程时间约束
		{
			return false;
		}
		return true;
	}

	// 初始化vector<CarRoute> result
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

	// 同步搜索
	public static Vector<CarRoute> synchroSearch(List<Node> seller,
			List<Node> charger, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle) {
		Vector<CarRoute> result = initResult();
		// 按照最晚到达时间最商铺排序

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
					visitSeller(result.get(carIndex), sell, distance, time); // 到达商户

					int closeChargerIndex = findCharge(sell, distance, charger); // 最近充电站坐标
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

	// 删除空的货车
	public static void deleteEmptyCar(Vector<CarRoute> result) {
		Vector<CarRoute> ret = new Vector<CarRoute>();
		for (CarRoute car : result) {
			if (car.route.size() > 1)
				ret.add(car);
		}
		result = ret;
	}

	// 哪辆车去服务这家商铺
	public static int whichCarVisitThisSell(Vector<CarRoute> result, Node sell,
			List<Node> charger, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle) {
		int carIndex = -1;
		double minvisitCost = MAX_DOUBLE;

		for (int i = 0; i < result.size(); ++i) {
			CarRoute curCar = result.get(i);
			// 货车curCar是否可以不经过充电 直接服务商铺
			if (canVisit(curCar, vehicle, sell, distance, time)) {
				double curCost = 0;
				// 路径花费
				curCost += distance.get(
						curCar.route.get(curCar.route.size() - 1)).get(sell.id)
						* vehicle.get(curCar.cartype - 1).unit_trans_cost;
				// //等待花费,首次出发没有等待花费
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
			// 货车curCar经过充电站充电才能服务商铺
			else if (bySellChargeCanVisit(curCar, vehicle, sell, distance,
					time, charger)) {
				double curCost = 0;

				// 路径花费
				curCost += distance.get(
						curCar.route.get(curCar.route.size() - 1)).get(sell.id)
						* vehicle.get(curCar.cartype - 1).unit_trans_cost;
				// int closeChargerIndex = findCharge(curCar, sell, distance,
				// time, charger);
				// curCost += (distance[curCar.route.back()][sell.id] +
				// distance[sell.id][charger[closeChargerIndex].id])
				// * vehicle[curCar.cartype - 1].unit_trans_cost;

				// //充电花费
				// curCost += 50;

				// //等待花费
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

	// 可能有bug
	public static boolean bySellChargeCanVisit(CarRoute curCar,
			Vector<Vehicle> vehicle, Node sell,
			Vector<Vector<Integer>> distance, Vector<Vector<Integer>> time,
			List<Node> charger)

	{
		int closeChargerIndex = sellMatchCharger.get(sell.id);

		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size() - 1); // 路径的最后一个商户节点
		// cout<<curCar.curMileage<<" "<<lastIndex<<" "<<sell.id<<" "<<closeChargerIndex<<endl;
		curCar.curMileage = curCar.curMileage
				+ distance.get(lastIndex).get(sell.id)
				+ distance.get(sell.id).get(charger.get(closeChargerIndex).id)
				- distance.get(lastIndex).get(0);
		// cout<<curCar.curMileage<<endl;
		// 时间窗约束，
		// 到达时间必须在商户的最迟接收时间之前
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

	// seller排序的比较函数
	public static boolean timeCompare(Node node1, Node node2) {
		return node1.last_int_tm < node2.last_int_tm;
	}
}
