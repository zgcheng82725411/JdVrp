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
		String path = "D:/soft/code/Gurobi���Ž̳�/VRPTW/data/c101.txt";// ������ַ
		Number data = new Number();
		VrpData vrpdata = new VrpData();
		vrpdata.getData(path, data, pointNum);
		VrpModel vrpModel = new VrpModel(data);				
		vrpModel.build();
		vrpModel.solve();
	}		
}

// �������
class Number {
	int pointNum; // ���е㼯��n�������������ĺͿͻ��㣬��β��0��n��Ϊ�������ģ�
	double earlyest; // ��������ʱ�䴰��ʼʱ��
	double laterest; // ��������ʱ�䴰����ʱ��
	int carNum; // ������
	double capicity; // �����غ�
	int[][] vertexs; // ���е������x,y
	int[] demands; // ������
	int[] vehicles; // �������
	double[] beginTime; // ʱ�䴰��ʼʱ�䡾a[i],b[i]��
	double[] endTime; // ʱ�䴰����ʱ�䡾a[i],b[i]��
	double[] seviceTime; // �ͻ���ķ���ʱ��
	int[][] arcs; // arcs[i][j]��ʾi��j��Ļ�
	double[][] distance; // ��������������ǹ�ϵ,���þ����ʾ���� C[i][j]=dist[i][j]

	// �ض�С��3.2-->3
	public double double_truncate(double v) {
		int iv = (int) v;
		return iv;
	}
}
