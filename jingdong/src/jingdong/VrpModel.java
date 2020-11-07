package jingdong;

import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

public class VrpModel {

	Number data; 
	IloCplex model;
	public IloNumVar[][][] visit; 
	public IloNumVar[][] weight; 
	double cost;
	GetSolution solution;

	public VrpModel(Number data) {
		this.data = data;
	}

	public void build() throws IloException {

		model = new IloCplex();
		model.setOut(null);
		visit = new IloNumVar[data.pointNum][data.pointNum][data.carNum];

		weight = new IloNumVar[data.pointNum][data.carNum];
		
		for (int i = 0; i < data.pointNum; i++) {
			for (int k = 0; k < data.carNum; k++) {
				weight[i][k] = model.numVar(0, 1e15, IloNumVarType.Float, "w"
						+ i + "," + k);
			}
			for (int j = 0; j < data.pointNum; j++) {
				if (data.arcs[i][j] == 0) {
					visit[i][j] = null;
				} else {				
					for (int k = 0; k < data.carNum; k++) {
						visit[i][j][k] = model.numVar(0, 1, IloNumVarType.Int,
								"x" + i + "," + j + "," + k);
					}
				}
			}
		}
		//目标函数,公式1	
		IloNumExpr obj = model.numExpr();
		for (int i = 0; i < data.pointNum; i++) {
			for (int j = 0; j < data.pointNum; j++) {
				if (data.arcs[i][j] == 0) {
					continue;
				}
				for (int k = 0; k < data.carNum; k++) {
					obj = model.sum(obj,
							model.prod(data.distance[i][j], visit[i][j][k]));
				}
			}
		}
		model.addMinimize(obj);
		
		//公式(2)
		for (int i = 1; i < data.pointNum - 1; i++) {
			IloNumExpr expr1 = model.numExpr();
			for (int k = 0; k < data.carNum; k++) {
				for (int j = 1; j < data.pointNum; j++) {
					if (data.arcs[i][j] == 1) {
						expr1 = model.sum(expr1, visit[i][j][k]);
					}
				}
			}
			model.addEq(expr1, 1);
		}
		//公式(3)
		for (int k = 0; k < data.carNum; k++) {
			IloNumExpr expr2 = model.numExpr();
			for (int j = 1; j < data.pointNum; j++) {
				if (data.arcs[0][j] == 1) {
					expr2 = model.sum(expr2, visit[0][j][k]);
				}
			}
			model.addEq(expr2, 1);
		}
		//公式(4)
		for (int k = 0; k < data.carNum; k++) {
			for (int j = 1; j < data.pointNum - 1; j++) {
				IloNumExpr expr3 = model.numExpr();
				IloNumExpr subExpr1 = model.numExpr();
				IloNumExpr subExpr2 = model.numExpr();
				for (int i = 0; i < data.pointNum; i++) {
					if (data.arcs[i][j] == 1) {
						subExpr1 = model.sum(subExpr1, visit[i][j][k]);
					}
					if (data.arcs[j][i] == 1) {
						subExpr2 = model.sum(subExpr2, visit[j][i][k]);
					}
				}
				expr3 = model.sum(subExpr1, model.prod(-1, subExpr2));
				model.addEq(expr3, 0);
			}
		}
		//公式(5)
		for (int k = 0; k < data.carNum; k++) {
			IloNumExpr expr4 = model.numExpr();
			for (int i = 0; i < data.pointNum - 1; i++) {
				if (data.arcs[i][data.pointNum - 1] == 1) {
					expr4 = model.sum(expr4, visit[i][data.pointNum - 1][k]);
				}
			}
			model.addEq(expr4, 1);
		}
		//公式(6)
		double M = 1e5;
		for (int k = 0; k < data.carNum; k++) {
			for (int i = 0; i < data.pointNum; i++) {
				for (int j = 0; j < data.pointNum; j++) {
					if (data.arcs[i][j] == 1) {
						IloNumExpr expr5 = model.numExpr();
						IloNumExpr expr6 = model.numExpr();
						expr5 = model.sum(weight[i][k], data.seviceTime[i]
								+ data.distance[i][j]);
						expr5 = model.sum(expr5, model.prod(-1, weight[j][k]));
						expr6 = model.prod(M,
								model.sum(1, model.prod(-1, visit[i][j][k])));
						model.addLe(expr5, expr6);
					}
				}
			}
		}
		//公式(7)
		for (int k = 0; k < data.carNum; k++) {
			for (int i = 1; i < data.pointNum - 1; i++) {
				IloNumExpr expr7 = model.numExpr();
				for (int j = 0; j < data.pointNum; j++) {
					if (data.arcs[i][j] == 1) {
						expr7 = model.sum(expr7, visit[i][j][k]);
					}
				}
				model.addLe(model.prod(data.beginTime[i], expr7), weight[i][k]);
				model.addLe(weight[i][k], model.prod(data.endTime[i], expr7));
			}
		}
		//公式(8)
		for (int k = 0; k < data.carNum; k++) {
			model.addLe(data.earlyest, weight[0][k]);
			model.addLe(data.earlyest, weight[data.pointNum - 1][k]);
			model.addLe(weight[0][k], data.laterest);
			model.addLe(weight[data.pointNum - 1][k], data.laterest);
		}
		//公式(9)
		for (int k = 0; k < data.carNum; k++) {
			IloNumExpr expr8 = model.numExpr();
			for (int i = 1; i < data.pointNum - 1; i++) {
				IloNumExpr expr9 = model.numExpr();
				for (int j = 0; j < data.pointNum; j++) {
					if (data.arcs[i][j] == 1) {
						expr9 = model.sum(expr9, visit[i][j][k]);
					}
				}
				expr8 = model.sum(expr8, model.prod(data.demands[i], expr9));
			}
			model.addLe(expr8, data.capicity);
		}
	}

	// 函数功能：解模型，并生成车辆路径和得到目标值
	public void solve() throws IloException {
		ArrayList<ArrayList<Integer>> routes = new ArrayList<ArrayList<Integer>>(); // 定义车辆路径链表
		ArrayList<ArrayList<Double>> servetimes = new ArrayList<ArrayList<Double>>(); // 定义花费时间链表
		// 初始化车辆路径和花费时间链表，链表长度为车辆数k
		for (int k = 0; k < data.carNum; k++) {
			ArrayList<Integer> r = new ArrayList<Integer>(); // 定义一个对象为int型的链表
			ArrayList<Double> t = new ArrayList<Double>(); // 定义一个对象为double型的链表
			routes.add(r); // 将上述定义的链表加入到链表routes中
			servetimes.add(t); // 同上
		}
		// 判断建立的模型是否可解
		if (model.solve() == false) {
			// 模型不可解
			System.out.println("problem should not solve false!!!");
			return; // 若不可解，则直接跳出solve函数
		} else {
			// 模型可解，生成车辆路径
			for (int k = 0; k < data.carNum; k++) {
				boolean terminate = true;
				int i = 0;
				routes.get(k).add(0);
				servetimes.get(k).add(0.0);
				while (terminate) {
					for (int j = 0; j < data.pointNum; j++) {
						if (data.arcs[i][j] >= 0.5
								&& model.getValue(visit[i][j][k]) >= 0.5) {
							routes.get(k).add(j);
							servetimes.get(k).add(model.getValue(weight[j][k]));
							i = j;
							break;
						}
					}
					if (i == data.pointNum - 1) {
						terminate = false;
					}
				}
			}
		}
		solution = new GetSolution(data, routes, servetimes);
		cost = model.getObjValue();
		System.out.println("routes=" + solution.routes);
	}		
}

//类功能：解的可行性判断(可直接跳过此类)
class GetSolution{
	double epsilon = 0.0001;
	Number data = new Number();
	ArrayList<ArrayList<Integer>> routes = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Double>> servetimes = new ArrayList<ArrayList<Double>>();
	public GetSolution(Number data, ArrayList<ArrayList<Integer>> routes, ArrayList<ArrayList<Double>> servetimes) {
		super();
		this.data = data;
		this.routes = routes;
		this.servetimes = servetimes;
	}
	//函数功能：比较两个数的大小
	public int double_compare(double v1,double v2) {
		if (v1 < v2 - epsilon) {
			return -1;
		}
		if (v1 > v2 + epsilon) {
			return 1;
		}
		return 0;
	}
	//函数功能：解的可行性判断
	public void fesible() throws IloException {
		//车辆数量可行性判断
		if (routes.size() > data.carNum) {
			System.out.println("error: vecnum!!!");
			System.exit(0);
		}
		//车辆载荷可行性判断
		for (int k = 0; k < routes.size(); k++) {
			ArrayList<Integer> route = routes.get(k);
			double capasity = 0;
			for (int i = 0; i < route.size(); i++) {
				capasity += data.demands[route.get(i)];
			}
			if (capasity > data.capicity) {
				System.out.println("error: cap!!!");
				System.exit(0);
			}
		}
		//时间窗、车容量可行性判断
		for (int k = 0; k < routes.size(); k++) {
			ArrayList<Integer> route = routes.get(k);
			ArrayList<Double> servertime = servetimes.get(k);
			double capasity = 0;
			for (int i = 0; i < route.size()-1; i++) {
				int origin = route.get(i);
				int destination = route.get(i+1);
				double si = servertime.get(i);
				double sj = servertime.get(i+1);
				if (si < data.beginTime[origin] && si >  data.endTime[origin]) {
					System.out.println("error: servertime!");
					System.exit(0);
				}
				if (double_compare(si + data.distance[origin][destination],data.endTime[destination]) > 0) {
					System.out.println(origin + ": [" + data.beginTime[origin] + ","+data.endTime[origin]+"]"+ " "+ si);
					System.out.println(destination + ": [" + data.beginTime[destination] + ","+data.endTime[destination]+"]"+ " "+ sj);
					System.out.println(data.distance[origin][destination]);
					System.out.println(destination + ":" );
					System.out.println("error: forward servertime!");
					System.exit(0);
				}
				if (double_compare(sj - data.distance[origin][destination],data.beginTime[origin]) < 0) {
					System.out.println(origin + ": [" + data.beginTime[origin] + ","+data.endTime[origin]+"]"+ " "+ si);
					System.out.println(destination + ": [" + data.beginTime[destination] + ","+data.endTime[destination]+"]"+ " "+ sj);
					System.out.println(data.distance[origin][destination]);
					System.out.println(destination + ":" );
					System.out.println("error: backward servertime!");
					System.exit(0);
				}
			}
			if (capasity > data.capicity) {
				System.out.println("error: cap!!!");
				System.exit(0);
			}
		}
	}
}
