package jingdong;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class VrpData {
	// �������ܣ���txt�ļ��ж�ȡ���ݲ���ʼ������
	public static void getData(String path, Number data, int vetexnum)
			throws Exception {
		String line = null;
		String[] substr = null;
		Scanner cin = new Scanner(new BufferedReader(new FileReader(path))); // ��ȡ�ļ�
		for (int i = 0; i < 4; i++) {
			line = cin.nextLine(); // ��ȡһ��
		}
		line = cin.nextLine();
		line.trim(); // ���ص����ַ��������һ��������ɾ����ʼ�ͽ�β�Ŀո�
		substr = line.split(("\\s+")); // �Կո�Ϊ��־���ַ������
		// ��ʼ������
		data.pointNum = vetexnum;
		data.carNum = Integer.parseInt(substr[1]);
		data.capicity = Integer.parseInt(substr[2]);
		data.vertexs = new int[data.pointNum][2]; // ���е������x,y
		data.demands = new int[data.pointNum]; // ������
		data.vehicles = new int[data.carNum]; // �������
		data.beginTime = new double[data.pointNum]; // ʱ�䴰��ʼʱ��
		data.endTime = new double[data.pointNum]; // ʱ�䴰����ʱ��
		data.seviceTime = new double[data.pointNum]; // ����ʱ��
		data.arcs = new int[data.pointNum][data.pointNum];
		data.distance = new double[data.pointNum][data.pointNum]; // ��������������ǹ�ϵ,�þ����ʾcost
		for (int i = 0; i < 4; i++) {
			line = cin.nextLine();
		}
		
		//��ȡ����
		for (int i = 0; i < data.pointNum - 1; i++) {
			line = cin.nextLine();
			line.trim();
			substr = line.split("\\s+");
			data.vertexs[i][0] = Integer.parseInt(substr[2]);
			data.vertexs[i][1] = Integer.parseInt(substr[3]);
			data.demands[i] = Integer.parseInt(substr[4]);
			data.beginTime[i] = Integer.parseInt(substr[5]);
			data.endTime[i] = Integer.parseInt(substr[6]);
			data.seviceTime[i] = Integer.parseInt(substr[7]);
		}
		cin.close();// �ر���
		// ��ʼ���������Ĳ���
		data.vertexs[data.pointNum - 1] = data.vertexs[0];
		data.demands[data.pointNum - 1] = 0;
		data.beginTime[data.pointNum - 1] = data.beginTime[0];
		data.endTime[data.pointNum - 1] = data.endTime[0];
		data.earlyest = data.beginTime[0];
		data.laterest = data.endTime[0];
		data.seviceTime[data.pointNum - 1] = 0;
		double min1 = 1e15;
		double min2 = 1e15;
		// ��������ʼ��
		for (int i = 0; i < data.pointNum; i++) {
			for (int j = 0; j < data.pointNum; j++) {
				if (i == j) {
					data.distance[i][j] = 0;
					continue;
				}
				data.distance[i][j] = Math
						.sqrt((data.vertexs[i][0] - data.vertexs[j][0])
								* (data.vertexs[i][0] - data.vertexs[j][0])
								+ (data.vertexs[i][1] - data.vertexs[j][1])
								* (data.vertexs[i][1] - data.vertexs[j][1]));
				data.distance[i][j] = data.double_truncate(data.distance[i][j]);
			}
		}
		data.distance[0][data.pointNum - 1] = 0;
		data.distance[data.pointNum - 1][0] = 0;
		// ��������������ǹ�ϵ
		for (int k = 0; k < data.pointNum; k++) {
			for (int i = 0; i < data.pointNum; i++) {
				for (int j = 0; j < data.pointNum; j++) {
					if (data.distance[i][j] > data.distance[i][k]
							+ data.distance[k][j]) {
						data.distance[i][j] = data.distance[i][k]
								+ data.distance[k][j];
					}
				}
			}
		}
		// ��ʼ��Ϊ��ȫͼ
		for (int i = 0; i < data.pointNum; i++) {
			for (int j = 0; j < data.pointNum; j++) {
				if (i != j) {
					data.arcs[i][j] = 1;
				} else {
					data.arcs[i][j] = 0;
				}
			}
		}
		// ��ȥ������ʱ�䴰������Լ���ı�
		for (int i = 0; i < data.pointNum; i++) {
			for (int j = 0; j < data.pointNum; j++) {
				if (i == j) {
					continue;
				}
				if (data.beginTime[i] + data.seviceTime[i]
						+ data.distance[i][j] > data.endTime[j]
						|| data.demands[i] + data.demands[j] > data.capicity) {
					data.arcs[i][j] = 0;
				}
				if (data.beginTime[0] + data.seviceTime[i]
						+ data.distance[0][i]
						+ data.distance[i][data.pointNum - 1] > data.endTime[data.pointNum - 1]) {
					System.out.println("the calculating example is false");
				}
			}
		}
		for (int i = 1; i < data.pointNum - 1; i++) {
			if (data.endTime[i] - data.distance[0][i] < min1) {
				min1 = data.endTime[i] - data.distance[0][i];
			}
			if (data.beginTime[i] + data.seviceTime[i]
					+ data.distance[i][data.pointNum - 1] < min2) {
				min2 = data.beginTime[i] + data.seviceTime[i]
						+ data.distance[i][data.pointNum - 1];
			}
		}
		if (data.earlyest > min1 || data.laterest < min2) {
			System.out.println("Duration false!");
			System.exit(0);// ��ֹ����
		}
		// ��ʼ����������0��n+1����Ĳ���
		data.arcs[data.pointNum - 1][0] = 0;
		data.arcs[0][data.pointNum - 1] = 1;
		for (int i = 1; i < data.pointNum - 1; i++) {
			data.arcs[data.pointNum - 1][i] = 0;
		}
		for (int i = 1; i < data.pointNum - 1; i++) {
			data.arcs[i][0] = 0;
		}
	}
}
