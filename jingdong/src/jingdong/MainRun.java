package jingdong;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class MainRun {

	
	public static void main(String[] args) throws IOException {
		
		Vector<Vehicle> vehicle = new Vector<Vehicle>(); // 保存车辆信息
		vehicle.add(new Vehicle(1, "iveco", 12, 2.0, 100000, 0.5, 0.012, 200));
		vehicle.add(new Vehicle(2, "truck", 16, 2.5, 120000, 0.5, 0.014, 300));
		
		// 保存input_node内容
		Vector<Node> nodeList = new Vector<Node>();
		ReadFile readFile = new ReadFile();
		readFile.read_node(nodeList);
		
		// 1-1000商户信息
		List<Node> seller = nodeList.subList(1, 1001);

		// 充电桩信息
		List<Node> charger = nodeList.subList(1001, nodeList.size());
		
		// 按照最晚到达时间最商铺排序
		Collections.sort(seller, new Comparator<Node>() {
			public int compare(Node node1, Node node2) {
				if (node1.last_int_tm > node2.last_int_tm) {
					return 1;
				}
				else if (node1.last_int_tm < node2.last_int_tm){
					return -1;
				}
				else {
					return 0;
				}
						
			}
		});
		
		Vector<Vector<Integer>> distance = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> time = new Vector<Vector<Integer>>();

		readFile.read_distance(distance, time);
		
		HashMap<Integer, Integer> sellMatchCharger = new HashMap<Integer, Integer>();
		
		Logistics logic = new Logistics();
		sellMatchCharger = logic.calSellChargerMatch(
				distance, charger, seller);
		
		Vector<CarRoute> result = new Vector<CarRoute>();
		result = logic.synchroSearch(seller, charger, distance, time, vehicle);	
		GaVrp vrp = new GaVrp(sellMatchCharger, readFile.getNode());
		Vector<Vector<CarRoute>> resultList = vrp.init(result);
		vrp.ga(resultList, vehicle);
		
	}
	
	
}
