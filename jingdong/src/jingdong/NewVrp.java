package jingdong;

import ilog.cplex.IloCplex;

public class NewVrp {
	
	Data data;					
	IloCplex model;						
	double cost;				
	Solution solution;			
	public NewVrp(Data data) {
		this.data = data;
	}

	public static void main(String[] args) throws Exception {
		int pointNum = 102;
		String path = "D:/soft/code/Gurobi入门教程/VRPTW/data/c101.txt";// 算例地址
		Number data = new Number();
		VrpData vrpdata = new VrpData();
		vrpdata.getData(path, data, pointNum);
		VrpModel vrpModel = new VrpModel(data);				
		vrpModel.build();
		vrpModel.solve();
	}		
}

// 定义参数
class Number {
	int pointNum; // 所有点集合n（包括配送中心和客户点，首尾（0和n）为配送中心）
	double earlyest; // 配送中心时间窗开始时间
	double laterest; // 配送中心时间窗结束时间
	int carNum; // 车辆数
	double capicity; // 车辆载荷
	int[][] vertexs; // 所有点的坐标x,y
	int[] demands; // 需求量
	int[] vehicles; // 车辆编号
	double[] beginTime; // 时间窗开始时间【a[i],b[i]】
	double[] endTime; // 时间窗结束时间【a[i],b[i]】
	double[] seviceTime; // 客户点的服务时间
	int[][] arcs; // arcs[i][j]表示i到j点的弧
	double[][] distance; // 距离矩阵，满足三角关系,暂用距离表示花费 C[i][j]=dist[i][j]

	// 截断小数3.2-->3
	public double double_truncate(double v) {
		int iv = (int) v;
		return iv;
	}
}
