package jingdong;
 
/**
 * @author mnmlist@163.com
 * @date 2015/09/26
 * @time 22:24
 */
 
import java.util.Arrays;
import java.util.Random;

import org.omg.CORBA.INTERNAL;
 
public class VehicleRoutingProblem {
	public double bestResultValue = 10000;
    static int max = 101;
    static int maxqvehicle = 1024;
    static int maxdvehicle = 1024;
    Random ra = new Random();
    int K;// ���ʹ�ó���Ŀ
    int KK;// ʵ��ʹ�ó���
    int clientNum;// �ͻ���Ŀ,Ⱦɫ�峤��
    double punishWeight;// W1, W2, W3;//�ͷ�Ȩ��
    double crossRate, mutationRate;// ������ʺͱ������
    int populationScale;// ��Ⱥ��ģ
    int T;// ��������
    int t;// ��ǰ����
    int[] bestGhArr;// ���д�������õ�Ⱦɫ��
    double[] timeGhArr;// ���д�������õ�Ⱦɫ��
    double bestFitness;// ���д�������õ�Ⱦɫ�����Ӧ��
    int bestGenerationNum;// ��õ�Ⱦɫ����ֵĴ���
    double decodedEvaluation;// ��������г�������·���ܺ͡�
    double[][] vehicleInfoMatrix;// K�±��1��ʼ��K��0�б�ʾ���������������1�б�ʾ����ʻ�������룬2�б�ʾ�ٶ�
    int[] decodedArr;// Ⱦɫ���������ÿ�����ķ���Ŀͻ���˳��
    double[][] distanceMatrix;// �ͻ�����
    double[] weightArr;// �ͻ�������
    int[][] oldMatrix;// ��ʼ��Ⱥ��������Ⱥ��������ʾ��Ⱥ��ģ��һ�д���һ�����壬��Ⱦɫ�壬�б�ʾȾɫ�����Ƭ��
    int[][] newMatrix;// �µ���Ⱥ���Ӵ���Ⱥ
    double[] fitnessArr;// ��Ⱥ��Ӧ�ȣ���ʾ��Ⱥ�и����������Ӧ��
    double[] probabilityArr;// ��Ⱥ�и���������ۼƸ���
    double[] x1;
    double[] y1;
 
    // ��ʼ������
    void initData() {
        int i, j;
        decodedEvaluation = 0;// ��������г�������·���ܺ�
        punishWeight = 300;// ��������ͷ�Ȩ��
        clientNum = 20;// �ͻ���Ŀ,Ⱦɫ�峤��
        K = 5;// �����Ŀ
        populationScale = 200;// ��Ⱥ��ģ
        crossRate = 0.9;// �������
        mutationRate = 0.1;// ������ʣ�ʵ��Ϊ(1-Pc)*0.9=0.09
        T = 3000;// ��������
        bestFitness = 0;// ���д�������õ�Ⱦɫ�����Ӧ��
        vehicleInfoMatrix = new double[K
                + 2][3];// K�±��1��ʼ��K��0�б�ʾ���������������1�б�ʾ����ʻ�������룬2�б�ʾ�ٶ�
        bestGhArr = new int[clientNum];// ���д�������õ�Ⱦɫ��
        timeGhArr = new double[clientNum];// ���д�������õ�Ⱦɫ��
        decodedArr = new int[clientNum];// Ⱦɫ���������ÿ�����ķ���Ŀͻ���˳��
        distanceMatrix = new double[clientNum + 1][clientNum + 1];// �ͻ�����
        weightArr = new double[clientNum + 1];// �ͻ�������
        oldMatrix = new int[populationScale][clientNum];// ��ʼ��Ⱥ��������Ⱥ��������ʾ��Ⱥ��ģ��һ�д���һ�����壬��Ⱦɫ�壬�б�ʾȾɫ�����Ƭ��
        newMatrix = new int[populationScale][clientNum];// �µ���Ⱥ���Ӵ���Ⱥ
        fitnessArr = new double[populationScale];// ��Ⱥ��Ӧ�ȣ���ʾ��Ⱥ�и����������Ӧ��
        probabilityArr = new double[populationScale];// ��Ⱥ�и���������ۼƸ���
        x1 = new double[clientNum + 1];
        y1 = new double[clientNum + 1];
        // ����������غ������ʻ
        vehicleInfoMatrix[1][0] = 8.0;
        vehicleInfoMatrix[1][1] = 50.0;
        vehicleInfoMatrix[2][0] = 8.0;
        vehicleInfoMatrix[2][1] = 50.0;
        vehicleInfoMatrix[3][0] = 8.0;
        vehicleInfoMatrix[3][1] = 50.0;
        vehicleInfoMatrix[4][0] = 8.0;
        vehicleInfoMatrix[4][1] = 50.0;
        vehicleInfoMatrix[5][0] = 8.0;
        vehicleInfoMatrix[5][1] = 50.0;
        vehicleInfoMatrix[6][0] = maxqvehicle;// �������
        vehicleInfoMatrix[6][1] = maxdvehicle;
 
        // �ͻ�����
        x1[0] = 14.5;
        y1[0] = 13.0;
        weightArr[0] = 0.0;
        x1[1] = 12.8;
        y1[1] = 8.5;
        weightArr[1] = 0.1;
        x1[2] = 18.4;
        y1[2] = 3.4;
        weightArr[2] = 0.4;
        x1[3] = 15.4;
        y1[3] = 16.6;
        weightArr[3] = 1.2;
        x1[4] = 18.9;
        y1[4] = 15.2;
        weightArr[4] = 1.5;
        x1[5] = 15.5;
        y1[5] = 11.6;
        weightArr[5] = 0.8;
        x1[6] = 3.9;
        y1[6] = 10.6;
        weightArr[6] = 1.3;
        x1[7] = 10.6;
        y1[7] = 7.6;
        weightArr[7] = 1.7;
        x1[8] = 8.6;
        y1[8] = 8.4;
        weightArr[8] = 0.6;
        x1[9] = 12.5;
        y1[9] = 2.1;
        weightArr[9] = 1.2;
        x1[10] = 13.8;
        y1[10] = 15.2;
        weightArr[10] = 0.4;
        x1[11] = 6.7;
        y1[11] = 16.9;
        weightArr[11] = 0.9;
        x1[12] = 14.8;
        y1[12] = 7.6;
        weightArr[12] = 1.3;
        x1[13] = 1.8;
        y1[13] = 8.7;
        weightArr[13] = 1.3;
        x1[14] = 17.1;
        y1[14] = 11.0;
        weightArr[14] = 1.9;
        x1[15] = 7.4;
        y1[15] = 1.0;
        weightArr[15] = 1.7;
        x1[16] = 0.2;
        y1[16] = 2.8;
        weightArr[16] = 1.1;
        x1[17] = 11.9;
        y1[17] = 19.8;
        weightArr[17] = 1.5;
        x1[18] = 13.2;
        y1[18] = 15.1;
        weightArr[18] = 1.6;
        x1[19] = 6.4;
        y1[19] = 5.6;
        weightArr[19] = 1.7;
        x1[20] = 9.6;
        y1[20] = 14.8;
        weightArr[20] = 1.5;
 
        double x = 0, y = 0;
        // �ͻ�֮�����
        int endIndex = clientNum + 1;
        for (i = 0; i < endIndex; i++) {
            for (j = 0; j < endIndex; j++) {
                x = x1[i] - x1[j];
                y = y1[i] - y1[j];
                distanceMatrix[i][j] = Math.sqrt(x * x + y * y);
            }
        }
    }
 
    // Ⱦɫ�����ۺ���������һ��Ⱦɫ�壬�õ���Ⱦɫ������ֵ
    double caculateFitness(int[] Gh) {
        // Ⱦɫ����±�0��ʼ��L-1��
        int i, j;// i���ı�ţ�j�ͻ����
        int flag;// ����ʹ�õĳ���
        double cur_d, cur_q, evaluation;// ��ǰ������ʻ���룬������������ֵ����������ʻ�����
 
        cur_d = distanceMatrix[0][Gh[0]];// Gh[0]��ʾ��һ���ͻ���
        cur_q = weightArr[Gh[0]];
 
        i = 1;// ��1�ų���ʼ��Ĭ�ϵ�һ�����������һ���ͻ�������
        evaluation = 0;// ����ֵ��ʼΪ0
        flag = 0;// ��ʾ������δ����
 
        for (j = 1; j < clientNum; j++) {
            cur_q = cur_q + weightArr[Gh[j]];
            cur_d = cur_d + distanceMatrix[Gh[j]][Gh[j - 1]];
 
            // �����ǰ�ͻ�������ڳ���������أ����߾�����ڳ���ʻ�����룬������һ����
            if (cur_q > vehicleInfoMatrix[i][0]
                    || cur_d + distanceMatrix[Gh[j]][0]
                    > vehicleInfoMatrix[i][1])// ���ü��Ϸ����������ľ���
            {
                i = i + 1;// ʹ����һ����
                evaluation =
                        evaluation + cur_d - distanceMatrix[Gh[j]][Gh[j - 1]]
                                + distanceMatrix[Gh[j - 1]][0];
                cur_d = distanceMatrix[0][Gh[j]];// ���������ĵ���ǰ�ͻ�j����
                cur_q = weightArr[Gh[j]];
            }
        }
        evaluation = evaluation + cur_d + distanceMatrix[Gh[clientNum
                - 1]][0];// �������һ�����ߵľ���
        flag = i - K;// ������ʹ����Ŀ�Ƿ���ڹ涨���������ֻ��һ����
        if (flag < 0)
            flag = 0;// �������򲻳ͷ�
 
        evaluation = evaluation + flag * punishWeight;// ��������Գͷ�Ȩ��
        return 10 / evaluation;// ѹ������ֵ
    }
 
    // Ⱦɫ����뺯��������һ��Ⱦɫ�壬�õ���Ⱦɫ�����ÿ�����ķ���Ŀͻ���˳��
    void decoding(int[] Gh) {
        // Ⱦɫ����±�0��ʼ��L-1��
        int i, j;// i���ı�ţ�j�ͻ����
        double cur_d, cur_q, evaluation;// ��ǰ������ʻ���룬������������ֵ����������ʻ�����
        cur_d = distanceMatrix[0][Gh[0]];// Gh[0]��ʾ��һ���ͻ���
        cur_q = weightArr[Gh[0]];
        i = 1;// ��1�ų���ʼ��Ĭ�ϵ�һ�����������һ���ͻ�������
        decodedArr[i] = 1;
        evaluation = 0;
        for (j = 1; j < clientNum; j++) {
            cur_q = cur_q + weightArr[Gh[j]];
            cur_d = cur_d + distanceMatrix[Gh[j]][Gh[j - 1]];
            // �����ǰ�ͻ�������ڳ���������أ����߾�����ڳ���ʻ�����룬������һ����
            if (cur_q > vehicleInfoMatrix[i][0]
                    || cur_d + distanceMatrix[Gh[j]][0]
                    > vehicleInfoMatrix[i][1]) {
                i = i + 1;// ʹ����һ����
                decodedArr[i] = decodedArr[i - 1] + 1;//
                evaluation =
                        evaluation + cur_d - distanceMatrix[Gh[j]][Gh[j - 1]]
                                + distanceMatrix[Gh[j - 1]][0];
                cur_d = distanceMatrix[0][Gh[j]];// ���������ĵ���ǰ�ͻ�j����
                cur_q = weightArr[Gh[j]];
            } else {
                decodedArr[i] = decodedArr[i] + 1;//
            }
        }
        decodedEvaluation = evaluation + cur_d + distanceMatrix[Gh[clientNum
                - 1]][0];// �������һ�����ߵľ���
        KK = i;
 
    }
 
    // ��ʼ����Ⱥ
    void initGroup() {
        int i, k;
        int randomNum = 0;
        for (k = 0; k < populationScale; k++)// ��Ⱥ��
        {
            for (i = 0; i < clientNum; i++)
                oldMatrix[k][i] = i + 1;
            for (i = 0; i < clientNum; i++) {
                randomNum = ra.nextInt(clientNum);
                swap(oldMatrix[k], i, randomNum);
            }
        }
 
        // ��ʾ��ʼ����Ⱥ
        //		System.out.println("///��ʾ��ʼ��Ⱥ��ʼ(In initGroup method)");
        //		for (k = 0; k < populationScale; k++)
        //			System.out.println(Arrays.toString(oldMatrix[k]));
        //		System.out.println("///��ʾ��ʼ��Ⱥ����");
    }
 
    public void swap(int arr[], int index1, int index2) {
        int temp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = temp;
    }
 
    // ������Ⱥ�и���������ۻ����ʣ�ǰ�����Ѿ�����������������Ӧ��Fitness[max]����Ϊ����ѡ�����һ���֣�Pi[max]
    void countRate() {
        int k;
        double sumFitness = 0;// ��Ӧ���ܺ�
 
        for (k = 0; k < populationScale; k++) {
            sumFitness += fitnessArr[k];
        }
 
        // ������������ۼƸ���
        probabilityArr[0] = fitnessArr[0] / sumFitness;
        for (k = 1; k < populationScale; k++) {
            probabilityArr[k] =
                    fitnessArr[k] / sumFitness + probabilityArr[k - 1];
        }
    }
 
    // ����Ⱦɫ�壬k��ʾ��Ⱦɫ������Ⱥ�е�λ�ã�kk��ʾ�ɵ�Ⱦɫ������Ⱥ�е�λ��
    void copyChrosome(int k, int kk) {
        System.arraycopy(oldMatrix[kk], 0, newMatrix[k], 0, clientNum);
    }
 
    // ��ѡĳ����Ⱥ����Ӧ����ߵĸ��壬ֱ�Ӹ��Ƶ��Ӵ��У�ǰ�����Ѿ�����������������Ӧ��Fitness[max]
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
            bestGenerationNum = t;// ��õ�Ⱦɫ����ֵĴ���;
            System.arraycopy(oldMatrix[maxid], 0, bestGhArr, 0, clientNum);
        }
        // ����Ⱦɫ�壬k��ʾ��Ⱦɫ������Ⱥ�е�λ�ã�kk��ʾ�ɵ�Ⱦɫ������Ⱥ�е�λ��
        copyChrosome(0, maxid);// ��������Ⱥ����Ӧ����ߵ�Ⱦɫ��k���Ƶ�����Ⱥ�У����ڵ�һλ0
    }
 
    // ���������
 
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
 
    // ��OX��������,�������Ӳ�������
    void oxCrossover(int k1, int k2) {
        int i, j, k, flag;
        int ran1, ran2, temp;
        int[] Gh1 = new int[clientNum];
        int[] Gh2 = new int[clientNum];
        ran1 = ra.nextInt(clientNum);
        ran2 = ra.nextInt(clientNum);
        while (ran1 == ran2)
            ran2 = ra.nextInt(clientNum);
        if (ran1 > ran2)// ȷ��ran1<ran2
        {
            temp = ran1;
            ran1 = ran2;
            ran2 = temp;
        }
        flag = ran2 - ran1 + 1;// ɾ���ظ�����ǰȾɫ�峤��
 
        for (i = 0, j = ran1; i < flag; i++, j++) {
            Gh1[i] = newMatrix[k2][j];
            Gh2[i] = newMatrix[k1][j];
        }
        // �ѽ���ֵi=ran2-ran1������
        for (k = 0, j = flag; j < clientNum; j++)// Ⱦɫ�峤��
        {
            i = 0;
            while (i != flag) {
                Gh1[j] = newMatrix[k1][k++];
                i = 0;
                while (i < flag && Gh1[i] != Gh1[j])
                    i++;
            }
        }
 
        for (k = 0, j = flag; j < clientNum; j++)// Ⱦɫ�峤��
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
 
    // ����Ⱥ�еĵ�k��Ⱦɫ����б���
    void mutation(int k) {
        int ran1, ran2;
        ran1 = ra.nextInt(clientNum);
        ran2 = ra.nextInt(clientNum);
        while (ran1 == ran2)
            ran2 = ra.nextInt(clientNum);
        swap(newMatrix[k], ran1, ran2);
 
    }
 
    // ������������������
    void evolution() {
        int k, selectId;
        double r;// ����0С��1�������
        // ��ѡĳ����Ⱥ����Ӧ����ߵĸ���
        selectBestChrosome();
        // ����ѡ�������ѡscale-1����һ������
        for (k = 1; k < populationScale; k++) {
            selectId = select();
            copyChrosome(k, selectId);
        }
        for (k = 1; k + 1 < populationScale / 2; k = k + 2) {
            r = Math.abs(ra.nextDouble());
            // crossover
            if (r < crossRate) {
                oxCrossover(k, k + 1);// ���н���
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
        if (k == populationScale / 2 - 1)// ʣ���һ��Ⱦɫ��û�н���L-1
        {
            r = Math.abs(ra.nextDouble());
            if (r < mutationRate) {
                mutation(k);
            }
        }
 
    }
 
    public BestResult solveVrp() {
        int i, j, k;
        BestResult bestResult = new BestResult();
        // ��ʼ�����ݣ���ͬ�����ʼ�����ݲ�һ��
        initData();
 
        // ��ʼ����Ⱥ
        initGroup();
        int[] tempGA = new int[clientNum];
 
        // �����ʼ����Ⱥ��Ӧ�ȣ�Fitness[max]
        for (k = 0; k < populationScale; k++) {
            for (i = 0; i < clientNum; i++) {
                tempGA[i] = oldMatrix[k][i];
            }
 
            fitnessArr[k] = caculateFitness(tempGA);
        }
 
        // �����ʼ����Ⱥ�и���������ۻ����ʣ�Pi[max]
        countRate();
        for (t = 0; t < T; t++) {
            evolution();// ������������������
            // ������ȺnewMatrix���Ƶ�����ȺoldMatrix�У�׼����һ������
            for (k = 0; k < populationScale; k++)
                System.arraycopy(newMatrix[k], 0, oldMatrix[k], 0, clientNum);
            // ������Ⱥ��Ӧ�ȣ�Fitness[max]
            for (k = 0; k < populationScale; k++) {
                System.arraycopy(oldMatrix[k], 0, tempGA, 0, clientNum);
                fitnessArr[k] = caculateFitness(tempGA);
            }
            // ������Ⱥ�и���������ۻ����ʣ�Pi[max]
            countRate();
            // ������
            
            if(bestFitness < bestResultValue)
            {
            	bestResultValue = bestFitness;
            	
            }         
        }
        // �����Ⱥ
        //		System.out.println("//");
        //		for (k = 0; k < populationScale; k++)
        //			System.out.println(Arrays.toString(oldMatrix[k]));
        //		System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\t");
        // ���ִ���
       // System.out.println("��õĴ��������ڣ�" + bestGenerationNum + "��");
        // Ⱦɫ������ֵ
       
       // System.out.println("��õĽ��Ϊ��" + (10 / bestFitness) + "��" + bestFitness);
        // ��õ�Ⱦɫ��
      //  System.out.println(Arrays.toString(bestGhArr));
        // ��õ�Ⱦɫ�����
        decoding(bestGhArr);
        // ʹ�ó���
       // System.out.println("ʹ�ó�����" + KK);
        // ����
       // System.out.println("�������룺" + Arrays.toString(decodedArr));
      //  System.out.println("������ʻ������룺" + decodedEvaluation);
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
           // System.out.println(tefa);
        }
        bestResult.setBestFitness(10 / bestFitness);
        bestResult.setBestGenerationNum(bestGenerationNum);
        return bestResult;
    }
 
    public static void main(String[] args) {
        VehicleRoutingProblem vehicleRoutingProblem = new VehicleRoutingProblem();
        int count = 50;
        double generationNum = 0;
        double totalFitness = 0;
        BestResult bestResult = null;
        double bestResultValue = 10000;
        for (int i = 0; i < count; i++) {
          //  System.out.println(
          //          "/the " + (i + 1) + "iteration start...");
            bestResult = vehicleRoutingProblem.solveVrp();
            totalFitness += bestResult.getBestFitness();
            generationNum += bestResult.getBestGenerationNum();
            if(bestResultValue > bestResult.getBestFitness())
            {
            	bestResultValue = bestResult.getBestFitness();
            }
          //  System.out.println(
         //           "/the " + (i + 1) + "iteration end...");
         //   System.out.println();
        }
       System.out.println(bestResultValue);
       // System.out.println("ƽ���ڵ�" + (generationNum / count) + "���ҵ����н⡣");
       // System.out.println("ƽ����·��Ϊ��" + (totalFitness / count));
 
    }
}
 
class BestResult {
    private double bestFitness;
    private int bestGenerationNum;
 
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
 
}
