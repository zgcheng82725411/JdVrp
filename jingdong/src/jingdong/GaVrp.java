package jingdong;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.sun.javafx.geom.Crossings;

public class GaVrp {

	// 种群中个体的数量
	int POPULATION_NUM = 10;

	// 每代执行的最多次数
	int REPEAT_NUM = 20;

	// 遗传代数
	int ITERATION_NUM = 500000;

	// 迭代次数
	int CIRCAL_TIME = 20000;

	Random random = new Random();

	// 迟到次数
	int overTimeOrWeight = 200000;
	
	Vector<CarRoute> bestCarRoute = new Vector<CarRoute>();
	
	double maxFits = 0;	

	public GaVrp(HashMap<Integer, Integer> sellMatchCharger,
			HashMap<Integer, Node> node) {
		
	}

	public Vector<Vector<CarRoute>> init(Vector<CarRoute> result) {
		// 得到RUSULT的大小
		int sum = 0;
		for (CarRoute temp : result) {
			sum += temp.getInterRoute().size();
		}
		int route[][] = new int[POPULATION_NUM][sum];
		Vector<Vector<CarRoute>> resultList = new Vector<Vector<CarRoute>>();
		for (int i = 0; i < POPULATION_NUM; i++) {
			Vector<CarRoute> newResult = new Vector<CarRoute>(result);
			int from = random.nextInt(100) % 10;
			int to = random.nextInt(100) % 10;
			newResult.set(from, result.get(to));
			newResult.set(to, result.get(from));
			resultList.add(newResult);
		}

		List<List<Integer>> allRoutes = new ArrayList<List<Integer>>();
		for (int i = 0; i < POPULATION_NUM; i++) {
			List<Integer> routeIndex = new ArrayList<Integer>();
			Vector<CarRoute> newResult = resultList.get(i);
			for (int j = 0; j < newResult.size(); j++) {
				routeIndex.addAll(newResult.get(j).getInterRoute());
			}
			allRoutes.add(routeIndex);
		}

		// 用1500表示类型2的车
		for (int i = 0; i < allRoutes.size(); i++) {
			List<Integer> routeIndex = allRoutes.get(i);
			for (int k = 0; k < routeIndex.size(); k++) {
				route[i][k] = routeIndex.get(k);
			}
		}
		return resultList;
	}

	public void ga(Vector<Vector<CarRoute>> resultList, Vector<Vehicle> vehicle) {
		// 首先计算适应度
		int count =0;
		while(count++ < 100)
		{
			List<Fit> fitness = new ArrayList<Fit>();
			
			for (int i = 0; i < resultList.size(); i++) {
				
				List<Integer> test = getRouteId(resultList.get(i));
				System.out.print(test);
				double fits = 1 / calAllCost(resultList.get(i), vehicle);
				if(fits > maxFits)
				{
					maxFits = fits;
					bestCarRoute = resultList.get(i);
				}
				Fit theFit = new Fit();
				theFit.setRouteList(resultList.get(i));
				theFit.setFitness(fits);
				fitness.add(theFit);
			}
			Vector<Vector<CarRoute>> newResult = select(fitness, resultList);
			// 转成TSP模型，0表示1号卡车，1200表示2号卡车
			
			// 选取2个,变异交叉

			// 轮盘法选择

			// 交叉和变异
			resultList =  cross(newResult);			
			// 选择一个最优的，进行下一次迭代	
			
		}
	}
	
	private Vector<Vector<CarRoute>> cross(Vector<Vector<CarRoute>> newResult) {
		Vector<Vector<CarRoute>> changeResult = new Vector<Vector<CarRoute>>();
		while(changeResult.size() < newResult.size()) {
			// 随机选取2个不一样的，进行交叉
			int from = random.nextInt(newResult.size());
			int to = random.nextInt(newResult.size()) ;
			if (from == to) {
				continue;
			}
			
			Vector<CarRoute> fromCar = newResult.get(from);
			Vector<CarRoute> toCar = newResult.get(to);
			List<Integer> fromIds = getRouteId(fromCar);
			List<Integer> toIds = getRouteId(toCar);

			//fromIds = Arrays.asList(1,2,3,4,5,7,6);
			//toIds = Arrays.asList(3,1,4,2,7,5,6);
			List<List<Integer>> twoResult = crossbegin(toIds.size(), fromIds, toIds);			
			//变成车序，然后算成本
			Vector<Vector<CarRoute>> carRoute = changeToCarVector(twoResult);
			if(changeResult.size() + 1  == newResult.size())
			{
				changeResult.add(carRoute.get(0));
			}
			else {
				changeResult.addAll(carRoute);
			}
			
			
		}
		return changeResult;
	}

	private Vector<Vector<CarRoute>> changeToCarVector(List<List<Integer>> twoResult) {	
		Vector<Vector<CarRoute>> carRoute= new Vector<Vector<CarRoute>>();							
		for(int i =0;i < twoResult.size();i++)
		{
			Vector<CarRoute> tempCar = new Vector<CarRoute>();
			Vector<Integer> indexsFrom = new Vector<Integer>() ;
			List<Integer> indexs = new ArrayList<Integer>();
			boolean flag = false;
			CarRoute car = null ;
			List<Integer> tempList = twoResult.get(i);
			for(int index=0;index < tempList.size();index++)
			{		
				
				if(tempList.get(index) >= 1100)
				{								
					car = new CarRoute();														
					car.route.add(0);
					car.cartype = 1;
					tempCar.add(car);
				}
				else if(tempList.get(index) >= 1200)
				{			
					car = new CarRoute();													
					car.route.add(0);
					car.cartype = 2;
					tempCar.add(car);
				}
				else {
					
					car.route.add(tempList.get(index));
				}									
			}
			carRoute.add(tempCar);								
		}
		return carRoute;
	}

	private List<List<Integer>> crossbegin(int cityNum, List<Integer> fromIds,
			List<Integer> toIds) {		
		List<List<Integer>> twoResult = new ArrayList<List<Integer>>();
		// 交叉操作(OX1)	
		if (random.nextDouble() < 0.8) {
			// 定义两个cut点
			int cutPoint1 = -1;
			int cutPoint2 = -1;
			int r1 = random.nextInt(cityNum);
			if (r1 > 0 && r1 < cityNum - 1) {
				cutPoint1 = r1;
			
				int r2 = random.nextInt(cityNum - r1);
				if (r2 == 0) {
					cutPoint2 = r1 + 1;
				} else if (r2 > 0) {
					cutPoint2 = r1 + r2;
				}

			}
			List<Integer> allIndexOne = new ArrayList<Integer>();
			List<Integer> allIndexTwo = new ArrayList<Integer>();
			for (int i = 0; i < cityNum; i++) {
				allIndexOne.add(-1);
				allIndexTwo.add(-1);
			}
			// 先弄父路径
			if (cutPoint1 > 0 && cutPoint2 > 0) {
				List<Integer> subCutOne = fromIds.subList(cutPoint1,
						cutPoint2 + 1);
				List<Integer> subCutTwo = toIds.subList(cutPoint1,
						cutPoint2 + 1);

				// 获取
				List<Integer> oldOne = new ArrayList<Integer>();
				List<Integer> oldTwo = new ArrayList<Integer>();

				for (int i = cutPoint2; i < cityNum; i++) {
					oldOne.add(fromIds.get(i));
					oldTwo.add(toIds.get(i));
				}

				for (int i = 0; i < cutPoint1; i++) {
					oldOne.add(fromIds.get(i));
					oldTwo.add(toIds.get(i));
				}

				for (int i = cutPoint1; i < cutPoint2 + 1; i++) {
					allIndexOne.set(i, subCutOne.get(i-cutPoint1));
					allIndexTwo.set(i, subCutTwo.get(i-cutPoint1));
					oldOne.add(subCutOne.get(i-cutPoint1));
					oldTwo.add(subCutTwo.get(i-cutPoint1));
				}

				oldTwo.removeAll(subCutOne);
				oldOne.removeAll(subCutTwo);

				int index = 0;
				for (int i = cutPoint2+1; i < cityNum; i++) {
					allIndexOne.set(i, oldTwo.get(index++));
				}
				for (int i = 0; i < cutPoint1; i++) {
					allIndexOne.set(i, oldTwo.get(index++));
				}

				index = 0;
				for (int i = cutPoint2+1; i < cityNum; i++) {
					allIndexTwo.set(i, oldOne.get(index++));
				}
				for (int i = 0; i < cutPoint1; i++) {
					allIndexTwo.set(i, oldOne.get(index++));
				}
			}

			// 变异
			float p = random.nextFloat();
			if (p > 0.1) {
				cutPoint1 = -1;
				cutPoint2 = -1;
				r1 = random.nextInt(cityNum);
				if (r1 > 0 && r1 < cityNum - 1) {
					cutPoint1 = r1;

					int r2 = random.nextInt(cityNum - r1);
					if (r2 == 0) {
						cutPoint2 = r1 + 1;
					} else if (r2 > 0) {
						cutPoint2 = r1 + r2;
					}

					int one = allIndexOne.get(cutPoint1);
					int two = allIndexOne.get(cutPoint2);

					allIndexOne.set(cutPoint1, two);
					allIndexOne.set(cutPoint2, one);
				}

				p = random.nextFloat();
				if (p > 0.1) {
					cutPoint1 = -1;
					cutPoint2 = -1;
					r1 = random.nextInt(cityNum);
					if (r1 > 0 && r1 < cityNum - 1) {
						cutPoint1 = r1;

						int r2 = random.nextInt(cityNum - r1);
						if (r2 == 0) {
							cutPoint2 = r1 + 1;
						} else if (r2 > 0) {
							cutPoint2 = r1 + r2;
						}

						int one = allIndexTwo.get(cutPoint1);
						int two = allIndexTwo.get(cutPoint2);

						allIndexTwo.set(cutPoint1, two);
						allIndexTwo.set(cutPoint2, one);
					}
				}
			}
			
			twoResult.add(allIndexOne);
			twoResult.add(allIndexTwo);
			
		}
		else {
			twoResult.add(fromIds);
			twoResult.add(toIds);
		}
		return twoResult;
	}

	private List<Integer> getRouteId(Vector<CarRoute> fromCar) {
		List<Integer> allIndexs = new ArrayList<Integer>();
		int car1fromIndex = 1100;
		int car2fromIndex = 1200;
		//各50个车
		int car1Sum = 0;
		int car2Sum = 0;
		for (int i = 0; i < fromCar.size(); i++) {
			List<Integer> indexs = fromCar.get(i).getInterRoute();
			if (fromCar.get(i).cartype == 1) {
				indexs.set(0, car1fromIndex++);
				car1Sum++;
				
			} else if (fromCar.get(i).cartype == 2) {
				indexs.set(0, car2fromIndex++);
				car2Sum++;
			}
			allIndexs.addAll(indexs);
		}
		
		for (int i = car1Sum; i < 100; i++) {
			allIndexs.add(1100+ i++);
		}
		for (int i = car2Sum; i < 100; i++) {
			allIndexs.add(1200+ i++);
		}

		return allIndexs;
	}

	public Vector<Vector<CarRoute>> select(List<Fit> fitness,
			Vector<Vector<CarRoute>> resultListOld) {
		double sum = 0;
		for (int i = 0; i < fitness.size(); i++) {
			sum += fitness.get(i).getFitness();
		}

		// 分子系数
		// 计算概率矩阵
		double p[] = new double[fitness.size()];
		for (int i = 0; i < fitness.size(); i++) {
			p[i] = fitness.get(i).getFitness() / sum;
		}
		double sleectP = random.nextFloat();
		int selectCity = 0;
		double sum1 = 0;
		Vector<Vector<CarRoute>> resultList = new Vector<Vector<CarRoute>>();
		while (resultList.size() <= fitness.size()) {
			for (int i = 0; i < fitness.size(); i++) {
				sum1 += p[i];
				if (sum1 >= sleectP) {
					selectCity = i;
					break;
				}
			}
			resultList.add(resultListOld.get(selectCity));
		}
		return resultList;
	}

	public double calAllCost(Vector<CarRoute> result, Vector<Vehicle> vehicle) {
		double all_cost = 0;
		for (int i = 0; i < result.size(); ++i) {
			all_cost += result.get(i).totalMileage
					* vehicle.get(result.get(i).cartype - 1).unit_trans_cost;
			all_cost += result.get(i).chargeNum * 50;
			all_cost += result.get(i).waitTime * 0.4;
			all_cost += vehicle.get(result.get(i).cartype - 1).vechile_cost;
		}
		System.out.println(all_cost);
		return all_cost;
	}
	
	public double calAllCost(List<Integer> ids)
	{
		return 1;
    }

}
