package jingdong;
 
import java.util.Random;

//vrp
public class VehicleRoutingProblem {
	
	public int []bestGhArrValueCode;
	public int [] bestCode;
	public double bestResultValue = 10000;
    static int max = 101;
    static int maxqvehicle = 1024;
    static int maxdvehicle = 1024;
    Random ra = new Random();
    int K;// 最多使用车数目
    int KK;// 实际使用车数
    int clientNum;// 客户数目,染色体长度
    double punishWeight;// W1, W2, W3;//惩罚权重
    double crossRate, mutationRate;// 交叉概率和变异概率
    int populationScale;// 种群规模
    int T;// 进化代数
    int t;// 当前代数
    int[] bestGhArr;// 所有代数中最好的染色体
    double[] timeGhArr;// 所有代数中最好的染色体
    double bestFitness;// 所有代数中最好的染色体的适应度
    int bestGenerationNum;// 最好的染色体出现的代数
    double decodedEvaluation;// 解码后所有车辆所走路程总和、
    double[][] vehicleInfoMatrix;// K下标从1开始到K，0列表示车的最大载重量，1列表示车行驶的最大距离，2列表示速度
    int[] decodedArr;// 染色体解码后表达的每辆车的服务的客户的顺序
    double[][] distanceMatrix;// 客户距离
    double[] weightArr;// 客户需求量
    int[][] oldMatrix;// 初始种群，父代种群，行数表示种群规模，一行代表一个个体，即染色体，列表示染色体基因片段
    int[][] newMatrix;// 新的种群，子代种群
    double[] fitnessArr;// 种群适应度，表示种群中各个个体的适应度
    double[] probabilityArr;// 种群中各个个体的累计概率
    double[] x1;
    double[] y1;
 
    // 初始化函数
    void initData() {
        int i, j;
        decodedEvaluation = 0;// 解码后所有车辆所走路程总和
        punishWeight = 300;// 车辆超额惩罚权重
        clientNum = 32;// 客户数目,染色体长度
        K = 4;// 最大车数目
        populationScale = 200;// 种群规模
        crossRate = 0.9;// 交叉概率
        mutationRate = 0.07;// 变异概率，实际为(1-Pc)*0.9=0.09
        T = 3000;// 进化代数
        bestFitness = 0;// 所有代数中最好的染色体的适应度
        vehicleInfoMatrix = new double[K
                + 2][3];// K下标从1开始到K，0列表示车的最大载重量，1列表示车行驶的最大距离，2列表示速度
        bestGhArr = new int[clientNum];// 所有代数中最好的染色体
        timeGhArr = new double[clientNum];// 所有代数中最好的染色体
        decodedArr = new int[clientNum];// 染色体解码后表达的每辆车的服务的客户的顺序
        distanceMatrix = new double[clientNum + 1][clientNum + 1];// 客户距离
        weightArr = new double[clientNum + 1];// 客户需求量
        oldMatrix = new int[populationScale][clientNum];// 初始种群，父代种群，行数表示种群规模，一行代表一个个体，即染色体，列表示染色体基因片段
        newMatrix = new int[populationScale][clientNum];// 新的种群，子代种群
        fitnessArr = new double[populationScale];// 种群适应度，表示种群中各个个体的适应度
        probabilityArr = new double[populationScale];// 种群中各个个体的累计概率
        x1 = new double[clientNum + 1];
        y1 = new double[clientNum + 1];
        // 车辆最大载重和最大行驶
        bestCode = new int[clientNum];
        
        bestGhArrValueCode = new int[clientNum];
        vehicleInfoMatrix[1][0] = 8.0;
        vehicleInfoMatrix[1][1] = 5000.0;
        vehicleInfoMatrix[2][0] = 8.0;
        vehicleInfoMatrix[2][1] = 5000.0;
        vehicleInfoMatrix[3][0] = 8.0;
        vehicleInfoMatrix[3][1] = 5000.0;
        vehicleInfoMatrix[4][0] = 8.0;
        vehicleInfoMatrix[4][1] = 5000.0;

        vehicleInfoMatrix[5][0] = maxqvehicle;// 限制最大
        vehicleInfoMatrix[5][1] = maxdvehicle;
 
        // 客户坐标
        x1[0] = 14.5; y1[0] = 13.0; weightArr[0] = 0.0;
        x1[1] = 12.8; y1[1] = 8.5; weightArr[1] = 0.0;
        x1[2] = 18.4; y1[2] = 3.4; weightArr[2] = 0;
        x1[3] = 15.4; y1[3] = 16.6; weightArr[3] = 0;
        x1[4] = 18.9; y1[4] = 15.2; weightArr[4] =0;
        x1[5] = 15.5; y1[5] = 11.6; weightArr[5] = 0;
        x1[6] = 3.9; y1[6] = 10.6; weightArr[6] = 0;
        x1[7] = 10.6; y1[7] = 7.6; weightArr[7] = 0;
        x1[8] = 8.6; y1[8] = 8.4; weightArr[8] = 0;
        x1[9] = 12.5; y1[9] = 2.1; weightArr[9] = 0;
        x1[10] = 13.8; y1[10] = 15.2; weightArr[10] = 0;
        x1[11] = 6.7; y1[11] = 16.9; weightArr[11] = 0;
        x1[12] = 14.8; y1[12] = 7.6; weightArr[12] = 0;
        x1[13] = 1.8; y1[13] = 8.7; weightArr[13] = 0;
        x1[14] = 17.1; y1[14] = 11.0; weightArr[14] = 0;
        x1[15] = 7.4; y1[15] = 1.0; weightArr[15] = 0;
        x1[16] = 0.2; y1[16] = 2.8; weightArr[16] = 0;
        x1[17] = 11.9; y1[17] = 19.8; weightArr[17] = 0;
        x1[18] = 5.2; y1[18] = 15.1; weightArr[18] = 0;
        x1[19] = 11.4; y1[19] = 5.6; weightArr[19] = 0;
        x1[20] = 17.6; y1[20] = 14.8; weightArr[20] = 0;
        x1[21] = 4.7; y1[21] = 6.9; weightArr[21] = 0;
        x1[22] = 19.8; y1[22] = 17.6; weightArr[22] = 0;
        x1[23] = 3.8; y1[23] = 12.7; weightArr[23] = 0;
        x1[24] = 12.1; y1[24] = 15.0; weightArr[24] = 0;
        x1[25] = 2.4; y1[25] = 10.0; weightArr[25] = 0;
        x1[26] = 5.2; y1[26] = 21.8; weightArr[26] = 0;
        x1[27] = 10.9; y1[27] = 19.8; weightArr[27] = 0;
        x1[28] = 9.2; y1[28] = 5.1; weightArr[28] = 0;
        x1[29] = 1.4; y1[29] = 15.6; weightArr[29] =0;
        x1[30] = 8.6; y1[30] = 4.8; weightArr[30] = 0;
 
        x1[31] = 1.7; y1[31] = 18.6; weightArr[31] =0;
        x1[32] = 11.6; y1[32] = 15.8; weightArr[32] = 0;
        
        double x = 0, y = 0;
        // 客户之间距离
        int endIndex = clientNum + 1;
        for (i = 0; i < endIndex; i++) {
            for (j = 0; j < endIndex; j++) {
                x = x1[i] - x1[j];
                y = y1[i] - y1[j];
                distanceMatrix[i][j] = Math.sqrt(x * x + y * y);
            }
        }
    }
 
    // 染色体评价函数，输入一个染色体，得到该染色体评价值
    double caculateFitness(int[] Gh) {
        // 染色体从下标0开始到L-1；
        int i, j;// i车的编号，j客户编号
        int flag;// 超额使用的车数
        double cur_d, cur_q, evaluation;// 当前车辆行驶距离，载重量，评价值，即各车行驶总里程
 
        cur_d = distanceMatrix[0][Gh[0]];// Gh[0]表示第一个客户，
        cur_q = weightArr[Gh[0]];
 
        i = 1;// 从1号车开始，默认第一辆车能满足第一个客户的需求
        evaluation = 0;// 评价值初始为0
        flag = 0;// 表示车辆数未超额
 
        for (j = 1; j < clientNum; j++) {
            cur_q = cur_q + weightArr[Gh[j]];
            cur_d = cur_d + distanceMatrix[Gh[j]][Gh[j - 1]];
 
            // 如果当前客户需求大于车的最大载重，或者距离大于车行驶最大距离，调用下一辆车
            if (cur_q > vehicleInfoMatrix[i][0]
                    || cur_d + distanceMatrix[Gh[j]][0]
                    > vehicleInfoMatrix[i][1])// 还得加上返回配送中心距离
            {
                i = i + 1;// 使用下一辆车
                evaluation =
                        evaluation + cur_d - distanceMatrix[Gh[j]][Gh[j - 1]]
                                + distanceMatrix[Gh[j - 1]][0];
                cur_d = distanceMatrix[0][Gh[j]];// 从配送中心到当前客户j距离
                cur_q = weightArr[Gh[j]];
            }
        }
        evaluation = evaluation + cur_d + distanceMatrix[Gh[clientNum
                - 1]][0];// 加上最后一辆车走的距离
        flag = i - K;// 看车辆使用数目是否大于规定车数，最多只超一辆车
        if (flag < 0)
            flag = 0;// 不大于则不惩罚
 
        evaluation = evaluation + flag * punishWeight;// 超额车数乘以惩罚权重
        return 10 / evaluation;// 压缩评价值
    }
 
    // 染色体解码函数，输入一个染色体，得到该染色体表达的每辆车的服务的客户的顺序
    void decoding(int[] Gh) {
        // 染色体从下标0开始到L-1；
        int i, j;// i车的编号，j客户编号
        double cur_d, cur_q, evaluation;// 当前车辆行驶距离，载重量，评价值，即各车行驶总里程
        cur_d = distanceMatrix[0][Gh[0]];// Gh[0]表示第一个客户，
        cur_q = weightArr[Gh[0]];
        i = 1;// 从1号车开始，默认第一辆车能满足第一个客户的需求
        decodedArr[i] = 1;
        evaluation = 0;
        for (j = 1; j < clientNum; j++) {
            cur_q = cur_q + weightArr[Gh[j]];
            cur_d = cur_d + distanceMatrix[Gh[j]][Gh[j - 1]];
            // 如果当前客户需求大于车的最大载重，或者距离大于车行驶最大距离，调用下一辆车
            if (cur_q > vehicleInfoMatrix[i][0]
                    || cur_d + distanceMatrix[Gh[j]][0]
                    > vehicleInfoMatrix[i][1]) {
                i = i + 1;// 使用下一辆车
                decodedArr[i] = decodedArr[i - 1] + 1;//
                evaluation =
                        evaluation + cur_d - distanceMatrix[Gh[j]][Gh[j - 1]]
                                + distanceMatrix[Gh[j - 1]][0];
                cur_d = distanceMatrix[0][Gh[j]];// 从配送中心到当前客户j距离
                cur_q = weightArr[Gh[j]];
            } else {
                decodedArr[i] = decodedArr[i] + 1;//
            }
        }
        decodedEvaluation = evaluation + cur_d + distanceMatrix[Gh[clientNum
                - 1]][0];// 加上最后一辆车走的距离
        KK = i;
 
    }
 
    // 初始化种群
    void initGroup() {
        int i, k;
        int randomNum = 0;
        for (k = 0; k < populationScale; k++)// 种群数
        {
            for (i = 0; i < clientNum; i++)
                oldMatrix[k][i] = i + 1;
            for (i = 0; i < clientNum; i++) {
                randomNum = ra.nextInt(clientNum);
                swap(oldMatrix[k], i, randomNum);
            }
        }  
    }
 
    public void swap(int arr[], int index1, int index2) {
        int temp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = temp;
    }
 
    // 计算种群中各个个体的累积概率，前提是已经计算出各个个体的适应度Fitness[max]，作为赌轮选择策略一部分，Pi[max]
    void countRate() {
        int k;
        double sumFitness = 0;// 适应度总和
 
        for (k = 0; k < populationScale; k++) {
            sumFitness += fitnessArr[k];
        }
 
        // 计算各个个体累计概率
        probabilityArr[0] = fitnessArr[0] / sumFitness;
        for (k = 1; k < populationScale; k++) {
            probabilityArr[k] =
                    fitnessArr[k] / sumFitness + probabilityArr[k - 1];
        }
    }
 
    //复制染色体，k表示新染色体在种群中的位置，kk表示旧的染色体在种群中的位置
    void copyChrosome(int k, int kk) {
        System.arraycopy(oldMatrix[kk], 0, newMatrix[k], 0, clientNum);
    }
 
    // 挑选某代种群中适应度最高的个体，直接复制到子代中，前提是已经计算出各个个体的适应度Fitness[max]
    void selectBestChrosome() {
        int k, maxid;
        double maxevaluation;
        maxid = 0;
        maxevaluation = fitnessArr[0];
        for (k = 1; k < populationScale; k++) {
            if (maxevaluation < fitnessArr[k]) {
                maxevaluation = fitnessArr[k];
                maxid = k;               
            }
        }
 
        if (bestFitness < maxevaluation) {
            bestFitness = maxevaluation;
            bestGenerationNum = t;// 最好的染色体出现的代数;
            System.arraycopy(oldMatrix[maxid], 0, bestCode, 0, clientNum);
            
        }
        // 复制染色体，k表示新染色体在种群中的位置，kk表示旧的染色体在种群中的位置
        copyChrosome(0, maxid);// 将当代种群中适应度最高的染色体k复制到新种群中，排在第一位0
    }
 
    // 产生随机数
 
    int select() {
        int k;
        double ran1;
        ran1 = Math.abs(ra.nextDouble());
        for (k = 0; k < populationScale; k++) {
            if (ran1 <= probabilityArr[k]) {
                break;
            }
        }
        return k;
    }
 
    // 类OX交叉算子,交叉算子不够优秀
    void oxCrossover(int k1, int k2) {
        int i, j, k, flag;
        int ran1, ran2, temp;
        int[] Gh1 = new int[clientNum];
        int[] Gh2 = new int[clientNum];
        ran1 = ra.nextInt(clientNum);
        ran2 = ra.nextInt(clientNum);
        while (ran1 == ran2)
            ran2 = ra.nextInt(clientNum);
        if (ran1 > ran2)
        {
            temp = ran1;
            ran1 = ran2;
            ran2 = temp;
        }
        flag = ran2 - ran1 + 1;// 删除重复基因前染色体长度
 
        //中间片段复制
        for (i = 0, j = ran1; i < flag; i++, j++) {
            Gh1[i] = newMatrix[k2][j];
            Gh2[i] = newMatrix[k1][j];
        }
        // 已近赋值i=ran2-ran1个基因
        for (k = 0, j = flag; j < clientNum; j++)// 染色体长度
        {
            i = 0;
            while (i != flag) {
                Gh1[j] = newMatrix[k1][k++];
                i = 0;
                while (i < flag && Gh1[i] != Gh1[j])
                    i++;
            }
        }
 
        for (k = 0, j = flag; j < clientNum; j++)// 染色体长度
        {
            i = 0;
            while (i != flag) {
                Gh2[j] = newMatrix[k2][k++];
                i = 0;
                while (i < flag && Gh2[i] != Gh2[j])
                    i++;
            }
        }
        System.arraycopy(Gh1, 0, newMatrix[k1], 0, clientNum);
        System.arraycopy(Gh2, 0, newMatrix[k2], 0, clientNum);
    }
 
    // 对种群中的第k个染色体进行变异
    void mutation(int k) {
        int ran1, ran2;
        ran1 = ra.nextInt(clientNum);
        ran2 = ra.nextInt(clientNum);
        while (ran1 == ran2)
            ran2 = ra.nextInt(clientNum);
        swap(newMatrix[k], ran1, ran2);
    }
 
    // 进化函数，保留最优
    void evolution() {
        int k, selectId;
        double r;// 大于0小于1的随机数
        // 挑选某代种群中适应度最高的个体
        selectBestChrosome();
        // 赌轮选择策略挑选scale-1个下一代个体
        for (k = 1; k < populationScale; k++) {
            selectId = select();
            copyChrosome(k, selectId);
        }
        for (k = 1; k + 1 < populationScale / 2; k = k + 2) {
            r = Math.abs(ra.nextDouble());
            // crossover
            if (r < crossRate) {
                oxCrossover(k, k + 1);// 进行交叉
            } else {
                r = Math.abs(ra.nextDouble());
                if (r < mutationRate) {
                    mutation(k);
                }
                r = Math.abs(ra.nextDouble());
                if (r < mutationRate) {
                    mutation(k + 1);
                }
            }
        }
        if (k == populationScale / 2 - 1)// 剩最后一个染色体没有交叉L-1
        {
            r = Math.abs(ra.nextDouble());
            if (r < mutationRate) {
                mutation(k);
            }
        }
 
    }
 
    public int[] bestCode()
    {
    	return bestCode;
    }
    
    public int[] getbestGhArrValueCode()
    {
    	return bestGhArrValueCode;
    }
    
    public BestResult solveVrp() {
        int i, j, k;
        BestResult bestResult = new BestResult();
        // 初始化数据，不同问题初始化数据不一样
        initData();
 
        // 初始化种群
        initGroup();
        int[] tempGA = new int[clientNum];
 
        // 计算初始化种群适应度，Fitness[max]
        for (k = 0; k < populationScale; k++) {
            for (i = 0; i < clientNum; i++) {
                tempGA[i] = oldMatrix[k][i];
            }
 
            fitnessArr[k] = caculateFitness(tempGA);
        }
 
        // 计算初始化种群中各个个体的累积概率，Pi[max]
        countRate();
        for (t = 0; t < T; t++) {
            evolution();// 进化函数，保留最优
            // 将新种群newMatrix复制到旧种群oldMatrix中，准备下一代进化
            for (k = 0; k < populationScale; k++)
                System.arraycopy(newMatrix[k], 0, oldMatrix[k], 0, clientNum);
            // 计算种群适应度，Fitness[max]
            for (k = 0; k < populationScale; k++) {
                System.arraycopy(oldMatrix[k], 0, tempGA, 0, clientNum);
                fitnessArr[k] = caculateFitness(tempGA);
            }
            // 计算种群中各个个体的累积概率，Pi[max]
            countRate();
            // 进度条
            
            int[] bestGhArrValue = new int[clientNum];
            if(bestFitness < bestResultValue)
            {
            	bestResultValue = bestFitness;
            	System.arraycopy(bestCode, 0, bestGhArrValue, 0, clientNum);    
            	System.arraycopy(bestCode, 0, bestGhArrValueCode, 0, clientNum);
            	bestResult.setbestCode(bestCode);
            }         
        }
                   
        decoding(bestGhArr);
      
        String tefa = "";
        int tek;
        int[] templ = new int[max];
 
        for (i = 1; i <= KK; i++) {
 
            templ[1] = 0;
            tefa = "0-";
            tek = decodedArr[i - 1];
            for (j = tek, k = 2; j < decodedArr[i]; j++, k++) {
                tefa = tefa + bestGhArr[j] + "-";
                templ[k] = bestGhArr[j];
            }
            templ[k] = 0;
            templ[0] = k;
            tefa = k + "-" + tefa + "0";
          
        }
        bestResult.setBestFitness(10 / bestFitness);
        bestResult.setBestGenerationNum(bestGenerationNum);
        
        bestResult.setbestCode(bestCode);
        return bestResult;
    }
 
    public static void main(String[] args) {
        VehicleRoutingProblem vehicleRoutingProblem = new VehicleRoutingProblem();
        int count = 500;
        double generationNum = 0;
        double totalFitness = 0;
    
        double bestResultValue = 10000;
        int bestCode[] = null;
        for (int i = 0; i < count; i++) {  
        	BestResult bestResult = new BestResult();
            bestResult = vehicleRoutingProblem.solveVrp();
            totalFitness += bestResult.getBestFitness();
            generationNum += bestResult.getBestGenerationNum();
            if(bestResultValue > bestResult.getBestFitness())
            {
            	bestResultValue = bestResult.getBestFitness();     
            	bestCode = bestResult.getbestCode();
            }      
       }
       System.out.println(bestResultValue);
       for(int i = 0;i < bestCode.length;i++)
       {
    	   System.out.print(bestCode[i]);
    	   System.out.print("-");
       }
    
    }
}
 
class BestResult {
    private double bestFitness;
    private int bestGenerationNum;
 
    int[] bestCode;
    public int getBestGenerationNum() {
        return bestGenerationNum;
    }
 
    public void setBestGenerationNum(int bestGenerationNum) {
        this.bestGenerationNum = bestGenerationNum;
    }
 
    public double getBestFitness() {
        return bestFitness;
    }
 
    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }
    
    public int[] getbestCode()
    {  	
		return bestCode;
    }
    
    public void setbestCode(int[] bestCode)
    {
    	this.bestCode = bestCode;
    }
    
}
