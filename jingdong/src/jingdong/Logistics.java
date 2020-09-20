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

	// 同步搜索
	public Vector<CarRoute> synchroSearch(List<Node> seller,
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

		//遍历所有的点和车，每次都找最近的
		for (Node sell : seller) {
			//找到最近的点,如果因为距离则返回-2，其他返回-1
			int carIndex = whichCarVisitThisSell(result, sell, charger,
					distance, time, vehicle);
			//如果没有，看是否能通过充电来获得
			result.get(carIndex).getInterRoute().add(sell.id);
		}		
		return result;
	}


	private void visitCharge(CarRoute curCar, Node charger,
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

	// 删除空的货车
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
		// 访问sell商户
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
		// 更新出发时间,出发时间为 货车在第一次到达商户的等待时间
		if (curCar.leaveTime == -1) {
			curCar.leaveTime = waitT;
		} else {
			curCar.waitTime += waitT;
		}

		curCar.curTime = arriveTime + waitT + 30 + time.get(sell.id).get(0);
		// 到达时间+等待+服务+返回

		curCar.route.add(sell.id);
		curCar.totalMileage = curCar.totalMileage
				+ distance.get(lastIndex).get(sell.id)
				+ distance.get(sell.id).get(0) - distance.get(lastIndex).get(0);

		curCar.backTime = curCar.curTime;
		// curCar.waitTime += waitT;

	}

	// 哪辆车去服务这家商铺
	public int whichCarVisitThisSell(Vector<CarRoute> result, Node sell,
			List<Node> charger, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time, Vector<Vehicle> vehicle) {
		int carIndex = -1;
		double minvisitCost = MAX_DOUBLE;
		for (int i = 0; i < result.size(); ++i) {
			CarRoute curCar = new CarRoute(result.get(i));
			// 货车curCar是否可以不经过充电 直接服务商铺
			if (canVisit(curCar, vehicle, sell, distance, time)== 1) {
				double curCost = 0;
				// 路径花费
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

	//货车curCar 不经过充电是否可以直接访问sell
	private int canVisit(CarRoute curCar, Vector<Vehicle> vehicle,
			Node sell, Vector<Vector<Integer>> distance,
			Vector<Vector<Integer>> time) {
		curCar.curVolume += sell.volume;
		curCar.curWeight += sell.weight;

		int lastIndex = curCar.route.get(curCar.route.size() - 1); // 路径的最后一个商户节点
		curCar.curMileage = curCar.curMileage
				+ distance.get(lastIndex).get(sell.id);			;
		// 时间窗约束，
		// 到达时间必须在商户的最迟接收时间之前
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
			//最后一个节点充电，然后判断里程和时间,充电次数加1
			int chargeindex = sellMatchCharger.get(sell.id);
			CarRoute tempCar = new CarRoute(curCar);
			arriveTime = tempCar.curTime + time.get(lastIndex).get(chargeindex)
					+ time.get(chargeindex).get(sell.id);
			if (arriveTime > sell.last_int_tm)
				return -1;
			else {
				//充电次数加1
				return 1;
			}			
		}		
	}

	// 初始化vector<CarRoute> result
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

	public int isConstraint(CarRoute curCar, Vector<Vehicle> vehicle) // 约束条件
	{
		if (curCar.curVolume > vehicle.get(curCar.cartype - 1).volume) // 货车容量约束
		{
			return -1;
		}
		if (curCar.curWeight > vehicle.get(curCar.cartype - 1).weight) // 货车载重约束
		{
			return -1;
		}
		if (curCar.curMileage > vehicle.get(curCar.cartype - 1).driving_range)// 里程约束
																				// 。注意：先不考虑中途充电的情况
		{
			return -2;
		}
		if (curCar.curTime > 960) // 返程时间约束
		{
			return -1;
		}
		return 1;
	}

}
