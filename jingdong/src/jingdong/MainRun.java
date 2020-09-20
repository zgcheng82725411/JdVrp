package jingdong;

import java.io.IOException;
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
		
		Vector<Vector<Integer>> distance = new Vector<Vector<Integer>>();
		Vector<Vector<Integer>> time = new Vector<Vector<Integer>>();

		readFile.read_distance(distance, time);
		
		Vector<Integer> sellMatchCharger = new Vector<Integer>();
		
		Logistics logic = new Logistics();
		sellMatchCharger = logic.calSellChargerMatch(sellMatchCharger, 
				distance, charger, seller);
		
		Vector<CarRoute> result = new Vector<CarRoute>();
		result = logic.synchroSearch(seller, charger, distance, time, vehicle);
		
		
	}
	
	
}
