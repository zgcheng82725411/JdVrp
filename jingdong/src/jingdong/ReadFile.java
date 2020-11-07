package jingdong;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

import com.sun.javafx.collections.MappingChange.Map;


public class ReadFile {
	private HashMap<Integer, Node> nodesMap = new HashMap<Integer, Node>();
	public void read_node(Vector<Node> vec) throws IOException {
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
				
			} else if (lineNum > 1 && lineNum < 1002) {
				if(lineNum == 1000)
				{
					int u =0;
					u++;
				}
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
			nodesMap.put(tempNode.id, tempNode);
			strbuff = data.readLine();
			lineNum++;
		}
	}
	
	public HashMap<Integer, Node> getNode()
	{
		return nodesMap;
	}
	
	public  int changeTime(String str) // 把时间换成int
	{
		if (str == "00:00")
			return 960;
		else {			
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
	
	public void read_distance(Vector<Vector<Integer>> dis,
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
}
