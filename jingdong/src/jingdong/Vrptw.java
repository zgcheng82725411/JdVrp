package jingdong;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
/**
 * @author�� huangnan
 * @School: HuaZhong University of science and technology
 * @����˵�������벻ͬ���ļ�ǰҪ�ֶ��޸�vetexnum����������ֵΪ���е����,������������0��n+1��
 * ����������ȡ��Solomon��������
 *
 */
//�������
class Data{
	int vetexnum;					//���е㼯��n�������������ĺͿͻ��㣬��β��0��n��Ϊ�������ģ�
	double E;	      				//��������ʱ�䴰��ʼʱ��
	double	L;	     			 	//��������ʱ�䴰����ʱ��
	int vecnum;    					//������
	double cap;     				//�����غ�
	int[][] vertexs;				//���е������x,y
	int[] demands;					//������
	int[] vehicles;					//�������
	double[] a;						//ʱ�䴰��ʼʱ�䡾a[i],b[i]��
	double[] b;						//ʱ�䴰����ʱ�䡾a[i],b[i]��
	double[] s;						//�ͻ���ķ���ʱ��
	int[][] arcs;					//arcs[i][j]��ʾi��j��Ļ�
	double[][] dist;				//��������������ǹ�ϵ,���þ����ʾ���� C[i][j]=dist[i][j]
	//�ض�С��3.26434-->3.2
	public double double_truncate(double v){
		int iv = (int) v;
		if(iv+1 - v <= 0.000000000001)
			return iv+1;
		double dv = (v - iv) * 10;
		int idv = (int) dv;
		double rv = iv + idv / 10.0;
		return rv;
	}	
}
//�๦�ܣ���Ŀ������ж�(��ֱ����������)
class Solution{
	double epsilon = 0.0001;
	Data data = new Data();
	ArrayList<ArrayList<Integer>> routes = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Double>> servetimes = new ArrayList<ArrayList<Double>>();
	public Solution(Data data, ArrayList<ArrayList<Integer>> routes, ArrayList<ArrayList<Double>> servetimes) {
		super();
		this.data = data;
		this.routes = routes;
		this.servetimes = servetimes;
	}
	//�������ܣ��Ƚ��������Ĵ�С
	public int double_compare(double v1,double v2) {
		if (v1 < v2 - epsilon) {
			return -1;
		}
		if (v1 > v2 + epsilon) {
			return 1;
		}
		return 0;
	}
	//�������ܣ���Ŀ������ж�
	public void fesible() throws IloException {
		//���������������ж�
		if (routes.size() > data.vecnum) {
			System.out.println("error: vecnum!!!");
			System.exit(0);
		}
		//�����غɿ������ж�
		for (int k = 0; k < routes.size(); k++) {
			ArrayList<Integer> route = routes.get(k);
			double capasity = 0;
			for (int i = 0; i < route.size(); i++) {
				capasity += data.demands[route.get(i)];
			}
			if (capasity > data.cap) {
				System.out.println("error: cap!!!");
				System.exit(0);
			}
		}
		//ʱ�䴰���������������ж�
		for (int k = 0; k < routes.size(); k++) {
			ArrayList<Integer> route = routes.get(k);
			ArrayList<Double> servertime = servetimes.get(k);
			double capasity = 0;
			for (int i = 0; i < route.size()-1; i++) {
				int origin = route.get(i);
				int destination = route.get(i+1);
				double si = servertime.get(i);
				double sj = 0/*servertime.get(i+1)*/;
				if (si < data.a[origin] && si >  data.b[origin]) {
					System.out.println("error: servertime!");
					System.exit(0);
				}
				if (double_compare(si + data.dist[origin][destination],data.b[destination]) > 0) {
					System.out.println(origin + ": [" + data.a[origin] + ","+data.b[origin]+"]"+ " "+ si);
					System.out.println(destination + ": [" + data.a[destination] + ","+data.b[destination]+"]"+ " "+ sj);
					System.out.println(data.dist[origin][destination]);
					System.out.println(destination + ":" );
					System.out.println("error: forward servertime!");
					System.exit(0);
				}
				if (double_compare(sj - data.dist[origin][destination],data.a[origin]) < 0) {
					System.out.println(origin + ": [" + data.a[origin] + ","+data.b[origin]+"]"+ " "+ si);
					System.out.println(destination + ": [" + data.a[destination] + ","+data.b[destination]+"]"+ " "+ sj);
					System.out.println(data.dist[origin][destination]);
					System.out.println(destination + ":" );
					System.out.println("error: backward servertime!");
					System.exit(0);
				}
			}
			if (capasity > data.cap) {
				System.out.println("error: cap!!!");
				System.exit(0);
			}
		}
	}
}
//�๦�ܣ�����ģ�Ͳ����
public class Vrptw {
	Data data;					//������Data�Ķ���
	IloCplex model;				//����cplex�ڲ���Ķ���		
	public IloNumVar[][][] x;	//x[i][j][k]��ʾ��arcs[i][j]������k����
	public IloNumVar[][] w;		//�����������е��ʱ�����
	double cost;				//Ŀ��ֵobject
	Solution solution;			
	public Vrptw(Data data) {
		this.data = data;
	}
	//�������ܣ���ģ�ͣ������ɳ���·���͵õ�Ŀ��ֵ
	public void solve() throws IloException {
		ArrayList<ArrayList<Integer>> routes = new ArrayList<ArrayList<Integer>>();		//���峵��·������
		ArrayList<ArrayList<Double>> servetimes = new ArrayList<ArrayList<Double>>();	//���廨��ʱ������
		//��ʼ������·���ͻ���ʱ������������Ϊ������k
		for (int k = 0; k < data.vecnum; k++) {
			ArrayList<Integer> r = new ArrayList<Integer>();	//����һ������Ϊint�͵�����
			ArrayList<Double> t = new ArrayList<Double>();	//����һ������Ϊdouble�͵�����
			routes.add(r);								//�����������������뵽����routes��
			servetimes.add(t);							//ͬ��
		}
		//�жϽ�����ģ���Ƿ�ɽ�
		if(model.solve() == false){
			//ģ�Ͳ��ɽ�
			System.out.println("problem should not solve false!!!");
			return;										//�����ɽ⣬��ֱ������solve����
		}
		else{
			//ģ�Ϳɽ⣬���ɳ���·��
			for(int k = 0; k < data.vecnum; k++){
				boolean terminate = true;
				int i = 0;
				routes.get(k).add(0);		
				servetimes.get(k).add(0.0);
				while(terminate){
					for (int j = 0; j < data.vetexnum; j++) {
						if (data.arcs[i][j]>=0.5 && model.getValue(x[i][j][k])>=0.5) {
							routes.get(k).add(j);
							//servetimes.get(k).add(model.getValue(w[j][k]));
							i = j;
							break;
						}
					}
					if (i == data.vetexnum-1) {
						terminate = false;
					}
				}
			}
		}
		solution = new Solution(data,routes,servetimes);
		cost = model.getObjValue();
		System.out.println("routes="+solution.routes);
	}
	//�������ܣ�����VRPTW��ѧģ�ͽ���VRPTW��cplexģ��
	private void build_model() throws IloException {
		//model
		model = new IloCplex();
		model.setOut(null);
		//variables
		x = new IloNumVar[data.vetexnum][data.vetexnum][data.vecnum];
		w = new IloNumVar[data.vetexnum][data.vecnum];				//�������ʵ��ʱ��
		//����cplex����x��w���������ͼ�ȡֵ��Χ
		for (int i = 0; i < data.vetexnum; i++) {
			for (int k = 0; k < data.vecnum; k++) {
				w[i][k] = model.numVar(0, 1e15, IloNumVarType.Float, "w" + i + "," + k);
			}
			for (int j = 0; j < data.vetexnum; j++) {
				if (data.arcs[i][j]==0) {
					x[i][j] = null;
				}
				else{
					//Xijk,��ʽ(10)-(11)
					for (int k = 0; k < data.vecnum; k++) {
						x[i][j][k] = model.numVar(0, 1, IloNumVarType.Int, "x" + i + "," + j + "," + k);
					}
				}
			}
		}
		//����Ŀ�꺯��
		//��ʽ(1)
		IloNumExpr obj = model.numExpr();
		for(int i = 0; i < data.vetexnum; i++){
			for(int j = 0; j < data.vetexnum; j++){
				if (data.arcs[i][j]==0) {
					continue;
				}
				for(int k = 0; k < data.vecnum; k++){
					obj = model.sum(obj, model.prod(data.dist[i][j], x[i][j][k]));
				}
			}
		}
		model.addMinimize(obj);
		//����Լ��
		//��ʽ(2)
		for(int i= 1; i < data.vetexnum-1;i++){
			IloNumExpr expr1 = model.numExpr();
			for (int k = 0; k < data.vecnum; k++) {
				for (int j = 1; j < data.vetexnum; j++) {
					if (data.arcs[i][j]==1) {
						expr1 = model.sum(expr1, x[i][j][k]);
					}
				}
			}
			model.addEq(expr1, 1);
		}
		//��ʽ(3)
		for (int k = 0; k < data.vecnum; k++) {
			IloNumExpr expr2 = model.numExpr();
			for (int j = 1; j < data.vetexnum; j++) {
				if (data.arcs[0][j]==1) {
					expr2 = model.sum(expr2, x[0][j][k]);
				}
			}
			model.addEq(expr2, 1);
		}
		//��ʽ(4)
		for (int k = 0; k < data.vecnum; k++) {
			for (int j = 1; j < data.vetexnum-1; j++) {
				IloNumExpr expr3 = model.numExpr();
				IloNumExpr subExpr1 = model.numExpr();
				IloNumExpr subExpr2 = model.numExpr();
				for (int i = 0; i < data.vetexnum; i++) {
					if (data.arcs[i][j]==1) {
						subExpr1 = model.sum(subExpr1,x[i][j][k]);
					}
					if (data.arcs[j][i]==1) {
						subExpr2 = model.sum(subExpr2,x[j][i][k]);
					}
				}
				expr3 = model.sum(subExpr1,model.prod(-1, subExpr2));
				model.addEq(expr3, 0);
			}
		}
		//��ʽ(5)
		for (int k = 0; k < data.vecnum; k++) {
			IloNumExpr expr4 = model.numExpr();
			for (int i = 0; i < data.vetexnum-1; i++) {
				if (data.arcs[i][data.vetexnum-1]==1) {
					expr4 = model.sum(expr4,x[i][data.vetexnum-1][k]);
				}
			}
			model.addEq(expr4, 1);
		}
		//��ʽ(6)
		/*double M = 1e5;
		for (int k = 0; k < data.vecnum; k++) {
			for (int i = 0; i < data.vetexnum; i++) {
				for (int j = 0; j < data.vetexnum; j++) {
					if (data.arcs[i][j] == 1) {
						IloNumExpr expr5 = model.numExpr();
						IloNumExpr expr6 = model.numExpr();
						expr5 = model.sum(w[i][k], data.s[i]+data.dist[i][j]);
						expr5 = model.sum(expr5,model.prod(-1, w[j][k]));
						expr6 = model.prod(M,model.sum(1,model.prod(-1, x[i][j][k])));
						model.addLe(expr5, expr6);
					}
				}
			}
		}
		//��ʽ(7)
		for (int k = 0; k < data.vecnum; k++) {
			for (int i = 1; i < data.vetexnum-1; i++) {
				IloNumExpr expr7 = model.numExpr();
				for (int j = 0; j < data.vetexnum; j++) {
					if (data.arcs[i][j] == 1) {
						expr7 = model.sum(expr7,x[i][j][k]);
					}
				}
				model.addLe(model.prod(data.a[i], expr7), w[i][k]);
				model.addLe(w[i][k], model.prod(data.b[i], expr7));
			}
		}
		//��ʽ(8)
		for (int k = 0; k < data.vecnum; k++) {
			model.addLe(data.E, w[0][k]);
			model.addLe(data.E, w[data.vetexnum-1][k]);
			model.addLe(w[0][k], data.L);
			model.addLe(w[data.vetexnum-1][k], data.L);
		}
		*/
		//��ʽ(9)
		for (int k = 0; k < data.vecnum; k++) {
			IloNumExpr expr8 = model.numExpr();
			for (int i = 1; i < data.vetexnum-1; i++) {
				IloNumExpr expr9 = model.numExpr();
				for (int j = 0; j < data.vetexnum; j++) {
					if (data.arcs[i][j] == 1) {
						expr9=model.sum(expr9,x[i][j][k]);
					}
				}
				expr8 = model.sum(expr8,model.prod(data.demands[i],expr9));
			}
			model.addLe(expr8, data.cap);
		}
	}
	//�������ܣ���txt�ļ��ж�ȡ���ݲ���ʼ������
	public static void process_solomon(String path,Data data,int vetexnum) throws Exception{
		String line = null;
		String[] substr = null;
		Scanner cin = new Scanner(new BufferedReader(new FileReader(path)));  //��ȡ�ļ�
		for(int i =0; i < 4;i++){
			line = cin.nextLine();  //��ȡһ��
		}
		line = cin.nextLine();
		line.trim(); //���ص����ַ��������һ��������ɾ����ʼ�ͽ�β�Ŀո�
		substr = line.split(("\\s+")); //�Կո�Ϊ��־���ַ������
		//��ʼ������
		data.vetexnum = vetexnum;
		data.vecnum = Integer.parseInt(substr[1]); 
		data.cap = Integer.parseInt(substr[2]);
		data.vertexs =new int[data.vetexnum][2];				//���е������x,y
		data.demands = new int[data.vetexnum];					//������
		data.vehicles = new int[data.vecnum];					//�������
		data.a = new double[data.vetexnum];						//ʱ�䴰��ʼʱ��
		data.b = new double[data.vetexnum];						//ʱ�䴰����ʱ��
		data.s = new double[data.vetexnum];						//����ʱ��
		data.arcs = new int[data.vetexnum][data.vetexnum];
		data.dist = new double[data.vetexnum][data.vetexnum];	//��������������ǹ�ϵ,�þ����ʾcost
		for(int i =0; i < 4;i++){
			line = cin.nextLine();
		}
		//��ȡvetexnum-1������
		for (int i = 0; i < data.vetexnum - 1; i++) {
			line = cin.nextLine();
			line.trim();
			substr = line.split("\\s+");
			data.vertexs[i][0] = Integer.parseInt(substr[2]);
			data.vertexs[i][1] = Integer.parseInt(substr[3]);
			data.demands[i] = Integer.parseInt(substr[4]);
			data.a[i] = Integer.parseInt(substr[5]);
			data.b[i] = Integer.parseInt(substr[6]);
			data.s[i] = Integer.parseInt(substr[7]);
		}
		cin.close();//�ر���
		//��ʼ���������Ĳ���
		data.vertexs[data.vetexnum-1] = data.vertexs[0];
		data.demands[data.vetexnum-1] = 0;
		data.a[data.vetexnum-1] = data.a[0];
		data.b[data.vetexnum-1] = data.b[0];
		data.E = data.a[0];
		data.L = data.b[0];
		data.s[data.vetexnum-1] = 0;		
		double min1 = 1e15;
		double min2 = 1e15;
		//��������ʼ��
		for (int i = 0; i < data.vetexnum; i++) {
			for (int j = 0; j < data.vetexnum; j++) {
				if (i == j) {
					data.dist[i][j] = 0;
					continue;
				}
				data.dist[i][j] = Math.sqrt((data.vertexs[i][0]-data.vertexs[j][0])*(data.vertexs[i][0]-data.vertexs[j][0])+
						(data.vertexs[i][1]-data.vertexs[j][1])*(data.vertexs[i][1]-data.vertexs[j][1]));
				data.dist[i][j]=data.double_truncate(data.dist[i][j]);
			}
		}
		data.dist[0][data.vetexnum-1] = 0;
		data.dist[data.vetexnum-1][0] = 0;
		//��������������ǹ�ϵ
		for (int  k = 0; k < data.vetexnum; k++) {
			for (int i = 0; i < data.vetexnum; i++) {
				for (int j = 0; j < data.vetexnum; j++) {
					if (data.dist[i][j] > data.dist[i][k] + data.dist[k][j]) {
						data.dist[i][j] = data.dist[i][k] + data.dist[k][j];
					}
				}
			}
		}
		//��ʼ��Ϊ��ȫͼ
		for (int i = 0; i < data.vetexnum; i++) {
			for (int j = 0; j < data.vetexnum; j++) {
				if (i != j) {
					data.arcs[i][j] = 1;
				}
				else {
					data.arcs[i][j] = 0;
				}
			}
		}
		//��ȥ������ʱ�䴰������Լ���ı�
		for (int i = 0; i < data.vetexnum; i++) {
			for (int j = 0; j < data.vetexnum; j++) {
				if (i == j) {
					continue;
				}
				if (data.a[i]+data.s[i]+data.dist[i][j]>data.b[j] || data.demands[i]+data.demands[j]>data.cap) {
					data.arcs[i][j] = 0;
				}
				if (data.a[0]+data.s[i]+data.dist[0][i]+data.dist[i][data.vetexnum-1]>data.b[data.vetexnum-1]) {
					System.out.println("the calculating example is false");
					
				}
			}
		}
		for (int i = 1; i < data.vetexnum-1; i++) {
			if (data.b[i] - data.dist[0][i] < min1) {
				min1 = data.b[i] - data.dist[0][i];
			}
			if (data.a[i] + data.s[i] + data.dist[i][data.vetexnum-1] < min2) {
				min2 = data.a[i] + data.s[i] + data.dist[i][data.vetexnum-1];
			}
		}
		if (data.E > min1 || data.L < min2) {
			System.out.println("Duration false!");
			System.exit(0);//��ֹ����
		}
		//��ʼ����������0��n+1����Ĳ���
		data.arcs[data.vetexnum-1][0] = 0;
		data.arcs[0][data.vetexnum-1] = 1;
		for (int i = 1; i < data.vetexnum-1; i++) {
			data.arcs[data.vetexnum-1][i] = 0;
		}
		for (int i = 1; i < data.vetexnum-1; i++) {
			data.arcs[i][0] = 0;
		}
	}
	public static void main(String[] args) throws Exception {
		Data data = new Data();
		int vetexnum = 102;//���е����������0��n+1�����������ĵ�
		//���벻ͬ���ļ�ǰҪ�ֶ��޸�vetexnum����������ֵ�������е����,������������
//		String path = "C:\\Users\\Administrator\\Desktop\\c101.txt";//������ַ
		String path = "D:/soft/code/Gurobi���Ž̳�/VRPTW/data/c101.txt";//������ַ
		process_solomon(path,data,vetexnum);
		System.out.println("input succesfully");
		System.out.println("cplex procedure###########################");
		Vrptw cplex = new Vrptw(data);
		cplex.build_model();
		double cplex_time1 = System.nanoTime();
		cplex.solve();
		cplex.solution.fesible();
		double cplex_time2 = System.nanoTime();
		double cplex_time = (cplex_time2 - cplex_time1) / 1e9;//���ʱ�䣬��λs
		System.out.println("cplex_time " + cplex_time + " bestcost " + cplex.cost);
	}
}

