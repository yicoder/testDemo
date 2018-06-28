package com.yicoder.commonutils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * @Package:        com.yicoder.commonutils
 * @ClassName:      ACO
 * @Description: 蚁群算法（ACO）。
 *      因为tsp是旅行商问题或者称为中国邮递员问题，本身以城市为节点，所以代码和注释很多地方
 *      出现城市字眼，可以把城市当作货位看待。
 * @Author:         ziping
 * @CreateDate:     2018/6/19 09:18
 * @UpdateUser:     ziping
 * @UpdateDate:     2018/6/19 09:18
 * @UpdateRemark:   The modified content
 * @Version:        1.0
 * Copyright: Copyright (c) 2018/6/19
 */
public class ACO {
    private Ant[] ants; //蚂蚁
    private int antNum; //蚂蚁数量
    private int cityNum; //城市数量
    private int MAX_GEN; //运行代数
    private float[][] pheromone; //信息素矩阵
    private int[][] distance; //距离矩阵
    private float[][] distanceBeta; //距离指數
    private int bestLength; //最佳长度
    private int[] bestTour; //最佳路径

    private int colnum = 11; // 列数

    //三个参数
    private float alpha;
    private float beta;
    private float rho;

    private Ant globalBestAnt; //保存全局最优结果
    private Ant iterationBestAnt; //保存迭代最优结果
    private Ant tempAnt; //临时

    private double Pbest = 0.05; //蚂蚁一次搜索找到最优路径的概率
    private float m_dbRate; //最大信息素和最小信息素的比值

    private float Qmax; //信息素上限
    private float Qmin;  //信息素下限

    public static boolean debugflag; // 是否打印结果

    public ACO() {
    }

    /**
     * constructor of ACO
     *
     * @param n 城市数量
     * @param m 蚂蚁数量
     * @param g 运行代数
     * @param a alpha 表征信息素重要程度的参数
     * @param b beta 表征启发式因子重要程度的参数
     * @param r rho 信息素蒸发系数
     ***/
    public ACO(int n, int m, int g, float a, float b, float r) {
        cityNum = n;
        antNum = m;
        ants = new Ant[antNum];
        MAX_GEN = g;
        alpha = a;
        beta = b;
        rho = r;
    }

    @SuppressWarnings("resource")
    /**
     * 初始化ACO算法类
     * @param cityDot 货位点所在过道坐标
     * @throws IOException
     */
    private void init(int[][] cityDot) throws IOException {

        // 货位所在位置坐标，下标从0开始，（0,0）点是起止点可进可出，奇数过道只能进不能出，偶数过道只能出不能进，
        // 数组第一列是过道下标，第二列是过道货位位置
        //
        //   过道
        //    10					    					0
        //    9					    					10	1
        //    8					    						2
        //    7		1			    4						3
        //    6					    	6					4
        //    5		2			    						5
        //    4					    5		7				6
        //    3			    3					8	9		7
        //    2						    					8
        //    1						    					9
        //  起止(0,0)				    					10  单位
        //    0↑↓	1↑	2↓	3↑	4↓	5↑	6↓	7↑	8↓	9↑	10↓	    过道
        //
        //  如：最优解路径：0 2 1 5 4 6 7 8 9 10 3 0

        // 生成货位所在位置两两间的距离矩阵
        distance = new int[cityNum][cityNum];
        distanceBeta = new float[cityNum][cityNum];
        distance[0][0] = 0; //对角线为0
        distanceBeta[0][0] = 0.f; //对角线为0
        // 先设置起点和其它节点间的距离
        for (int j = 1; j < cityNum; j++) {
            if (cityDot[j][0] % 2 == 0) {
                // 过道为偶数
                distance[0][j] = (colnum) + cityDot[j][0] + (colnum - cityDot[j][1]);
                distance[j][0] = cityDot[j][0] + (cityDot[j][1] + 1);
            } else {
                // 过道为奇数
                distance[0][j] = cityDot[j][0] + (cityDot[j][1] + 1);
                distance[j][0] = (colnum - cityDot[j][1]) + cityDot[j][0] + (colnum);
            }
            distanceBeta[0][j] = (float) Math.pow(1.0 / distance[0][j], beta);
            distanceBeta[j][0] = (float) Math.pow(1.0 / distance[j][0], beta);
        }
        // 设置货位点两两间的距离矩阵,以及距离指数矩阵
        for (int i = 1; i < cityNum - 1; i++) {
            distance[i][i] = 0;  //对角线为0
            for (int j = i + 1; j < cityNum; j++) {
                if (cityDot[i][0] % 2 == 0) {
                    if (cityDot[j][0] % 2 == 0) {
                        // i点在偶数列，j点在偶数列
                        if (cityDot[i][0] != cityDot[j][0]) {
                            // i和j点不同行
                            distance[i][j] = (cityDot[i][1] + 1) + Math.abs(cityDot[i][0] - cityDot[j][0]) + (colnum) + (colnum - cityDot[j][1]);
                            distance[j][i] = (cityDot[j][1] + 1) + Math.abs(cityDot[j][0] - cityDot[i][0]) + (colnum) + (colnum - cityDot[i][1]);
                        } else {
                            // i和j点在同一行
                            if (cityDot[i][1] > cityDot[j][1]) {
                                // i点比j点列大
                                distance[i][j] = cityDot[i][1] - cityDot[j][1];
                                // j到i走法无意义，设置为比较大的值
                                distance[j][i] = Short.MAX_VALUE;
                            } else if (cityDot[i][1] < cityDot[j][1]) {
                                // i到j走法无意义，设置为比较大的值
                                distance[i][j] = Short.MAX_VALUE;
                                distance[j][i] = cityDot[j][1] - cityDot[i][1];
                            } else {
                                distance[i][j] = 1;
                                distance[j][i] = 1;
                            }
                        }
                    } else {
                        // i点在偶数列，j点在奇数列
                        distance[i][j] = (cityDot[i][1] + 1) + Math.abs(cityDot[i][0] - cityDot[j][0]) + (cityDot[j][1] + 1);
                        distance[j][i] = (colnum - cityDot[j][1]) + Math.abs(cityDot[j][0] - cityDot[i][0]) + (colnum - cityDot[i][1]);
                    }

                } else if (cityDot[i][0] % 2 != 0 && cityDot[j][0] % 2 != 0) {
                    // i点在奇数列，j点在奇数列
                    if (cityDot[i][0] != cityDot[j][0]) {
                        // i和j点不同行
                        distance[i][j] = (colnum - cityDot[i][1]) + Math.abs(cityDot[i][0] - cityDot[j][0]) + (colnum) + (cityDot[j][1] + 1);
                        distance[j][i] = (colnum - cityDot[j][1]) + Math.abs(cityDot[j][0] - cityDot[i][0]) + (colnum) + (cityDot[i][1] + 1);
                    } else {
                        // i和j点在同一行
                        if (cityDot[i][1] > cityDot[j][1]) {
                            // i点比j点列大

                            // i到j走法无意义，设置为比较大的值
                            distance[i][j] = Short.MAX_VALUE;
                            distance[j][i] = cityDot[i][1] - cityDot[j][1];
                        } else if (cityDot[i][1] < cityDot[j][1]) {
                            distance[i][j] = cityDot[j][1] - cityDot[i][1];
                            // j到i走法无意义，设置为比较大的值
                            distance[j][i] = Short.MAX_VALUE;
                        } else {
                            // 同一位置默认为1，避免做分母计算时为0
                            distance[i][j] = 1;
                            distance[j][i] = 1;
                        }
                    }
                } else {
                    // i点在奇数列，j点在偶数列
                    distance[i][j] = (colnum - cityDot[i][1]) + Math.abs(cityDot[i][0] - cityDot[j][0]) + (colnum - cityDot[j][1]);
                    distance[j][i] = (cityDot[j][1] + 1) + Math.abs(cityDot[j][0] - cityDot[i][0]) + (cityDot[i][1] + 1);
                }
                distanceBeta[i][j] = (float) Math.pow(1.0 / distance[i][j], beta);
                distanceBeta[j][i] = (float) Math.pow(1.0 / distance[j][i], beta);
            }
        }

        // 输出距离矩阵
        if (debugflag) {
            for (int i = 0; i < distance.length; i++) {
                for (int j = 0; j < distance[0].length; j++) {
                    System.out.print(distance[i][j] + "\t");
                }
                System.out.println();
                debugflag = false;
            }
        }

        //初始化信息素矩阵
        pheromone = new float[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                //初始化为0.1
                pheromone[i][j] = 10000.f;
            }
        }
        //初始化最优路径
        bestLength = Integer.MAX_VALUE;
        bestTour = new int[cityNum + 1];
        //随机放置蚂蚁
        for (int i = 0; i < antNum; i++) {
            ants[i] = new Ant(cityNum);
            ants[i].init(distance, distanceBeta, alpha, beta);
        }

        //计算最大和最小信息素之间的比值
        //蚂蚁一次搜索找到最优路径的概率
        float dbTemp = 0.f;
        double dbN = cityNum;
        //对Pbest开N_CITY_COUNT次方
        dbTemp = (float) Math.exp(Math.log(Pbest) / dbN);
        m_dbRate = (float) ((2.0 / dbTemp - 2.0) / (dbN - 2.0));

        //因为第一次迭代时，还没有全局最优解，所有计算不出最大和最小值，先设置成0.0
        Qmax = 0.f;
        Qmin = 0.f;

        // 初始化迭代最优的那个蚂蚁
        iterationBestAnt = new Ant();
        iterationBestAnt.setTourLength(Integer.MAX_VALUE);
    }

    /**
     * 最优路径搜索
     */
    public void solve() {

        // 迭代MAX_GEN次
        for (int g = 0; g < MAX_GEN; g++) {

            iterationBestAnt.setTabu(null);
            iterationBestAnt.setTourLength(Integer.MAX_VALUE);

            // 移动所有蚂蚁
            for (int i = 0; i < antNum; i++) {
                // 移动蚂蚁一个来回
                for (int j = 1; j < cityNum; j++) {
                    ants[i].selectNextCity(pheromone);
                }
                // 添加回到的起始点
                ants[i].getTabu().add(ants[i].getFirstCity());
                // 计算此蚂蚁移动路径总长度
                ants[i].calculateTourLength();
                // 计算并记录最佳结果
                if (ants[i].getTourLength() < bestLength) {
                    bestLength = ants[i].getTourLength();
                    for (int k = 0; k < cityNum + 1; k++) {
                        bestTour[k] = ants[i].getTabu().get(k).intValue();
                    }
                    // 保存最优解
                    globalBestAnt = (Ant) ants[i].clone();
                }

                // 保存迭代最优解
                if (ants[i].getTourLength() < iterationBestAnt.getTourLength()) {
                    iterationBestAnt = (Ant) ants[i].clone();
                }

                //
                for (int j = 0; j < cityNum; j++) {
                    ants[i].getDelta()[ants[i].getTabu().get(j).intValue()][ants[i].getTabu().get(j + 1).intValue()] = (float) (1. / ants[i].getTourLength());
                }
            }


            // 更新环境信息素，使用全局最优和迭代最优交替更新的策略
            // 每过5次迭代使用一次全局最优蚂蚁更新信息素
            // 这样可以扩大搜索范围
            if ((g + 1) % 5 == 0) {
                updatePheromone(1);
            } else {
                updatePheromone(0);
            }


            //重新初始化蚂蚁
            for (int i = 0; i < antNum; i++) {
                ants[i].init(distance, distanceBeta, alpha, beta);
            }
        }

        //打印最佳结果
        printOptimal();
    }

    /**
     * 更新信息素, 只用当前最好蚂蚁去更新
     */
    private void updatePheromone(int flag) {

        //信息素挥发
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                pheromone[i][j] = pheromone[i][j] * rho;
            }
        }

        if (flag == 1) {
            tempAnt = globalBestAnt;
        } else {
            tempAnt = iterationBestAnt;
        }

        //信息素更新
        for (int i = 0; i < cityNum; i++) {
            pheromone[tempAnt.getTabu().get(i).intValue()][tempAnt.getTabu().get(i + 1).intValue()] += (float) (1. / tempAnt.getTourLength());
        }

        //=====================================================
        //检查环境信息素，如果在最小和最大值的外面，则将其重新调整
        Qmax = (float) (1.0 / (globalBestAnt.getTourLength() * (1.0 - rho)));
        Qmin = Qmax * m_dbRate;

        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                if (pheromone[i][j] < Qmin) {
                    pheromone[i][j] = Qmin;
                }

                if (pheromone[i][j] > Qmax) {
                    pheromone[i][j] = Qmax;
                }

            }
        }
    }

    /**
     * 打印结果
     */
    private void printOptimal() {
        System.out.println("The optimal length is: " + bestLength);
        System.out.println("The optimal tour is: ");
        for (int i = 0; i < cityNum + 1; i++) {
            System.out.print(bestTour[i] + " ");
        }
        System.out.println();
    }

    public Ant[] getAnts() {
        return ants;
    }

    public void setAnts(Ant[] ants) {
        this.ants = ants;
    }

    public int getAntNum() {
        return antNum;
    }

    public void setAntNum(int m) {
        this.antNum = m;
    }

    public int getCityNum() {
        return cityNum;
    }

    public void setCityNum(int cityNum) {
        this.cityNum = cityNum;
    }

    public int getMAX_GEN() {
        return MAX_GEN;
    }

    public void setMAX_GEN(int mAX_GEN) {
        MAX_GEN = mAX_GEN;
    }

    public float[][] getPheromone() {
        return pheromone;
    }

    public void setPheromone(float[][] pheromone) {
        this.pheromone = pheromone;
    }

    public int[][] getDistance() {
        return distance;
    }

    public void setDistance(int[][] distance) {
        this.distance = distance;
    }

    public int getBestLength() {
        return bestLength;
    }

    public void setBestLength(int bestLength) {
        this.bestLength = bestLength;
    }

    public int[] getBestTour() {
        return bestTour;
    }

    public void setBestTour(int[] bestTour) {
        this.bestTour = bestTour;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float getRho() {
        return rho;
    }

    public void setRho(float rho) {
        this.rho = rho;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        int num = 6;
        int[][] cityDot = getPos(num);

        /**
         * 标准蚁群算法参数优化：
         * 1、根据大量仿真实验，蚁群算法参数优化最好的经验结果为：
         *      α∈[1,4]、β∈[2,5]、r ≈ 0.5、q∈[10,1000]时算法综合性能较好。
         * 但是具体问题仍需具体分析，不通用。
         * 2、参数最优组合“三步走”策略
         * （1）确定蚂蚁数目，城市规模：蚂蚁数目 ≈ 1.5；
         * （2）参数粗调，即调整取值范围较大的α、β即 q；
         * （3）参数微调，即调整取值范围较小的ρ。
         *
         * 最大最小蚁群算法参数优化：（本系统采用次算法）
         * 下方参数值是通过城市数6,10,12,17,20,31,50,62,93，分别进行10次实验得到的优化解。
         */
        int m;
        if (cityDot.length < 2) {
            m = 1;
        } else {
            m = cityDot.length * 1 / 5;
        }
        int q = 1000;
        float a = 1.f;
        float b = 2.f;
        float r = 0.95f;

        ACO.debugflag = true;
        for (int i = 0; i < 10; i++) {
            long starttime = System.currentTimeMillis();
            System.out.println("starttime:" + starttime);

            ACO aco = new ACO(cityDot.length, m, q, a, b, r);
            aco.init(cityDot);
            aco.solve();

            long endtime = System.currentTimeMillis();
            System.out.println("endtime:" + endtime);
            System.out.println("take time:" + (endtime - starttime) + "ms");
        }


    }

    /**
     * 获取模拟数据
     * @param num 城市数
     * @return
     */
    private static int[][] getPos(int num) {
        switch (num) {
            case 6:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {2, 5},
                        {3, 3},
                        {5, 7},
                        {6, 6},
                        {8, 2}
                };
            case 10:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {1, 5},
                        {3, 3},
                        {5, 7},
                        {5, 4},
                        {6, 6},
                        {7, 4},
                        {8, 3},
                        {9, 3},
                        {10, 9}
                };
            case 12:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {1, 5},
                        {3, 3},
                        {5, 7},
                        {5, 4},
                        {6, 6},
                        {7, 4},
                        {8, 3},
                        {9, 3},
                        {10, 9},
                        {3, 8},
                        {4, 8}
                };
            case 17:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {1, 5},
                        {3, 3},
                        {5, 7},
                        {5, 4},
                        {6, 6},
                        {7, 4},
                        {8, 3},
                        {9, 3},
                        {10, 9},
                        {3, 8},
                        {4, 8},
                        {2, 6},
                        {2, 4},
                        {7, 8},
                        {8, 6},
                        {10, 6}
                };
            case 20:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {1, 5},
                        {3, 3},
                        {5, 7},
                        {5, 4},
                        {6, 6},
                        {7, 4},
                        {8, 3},
                        {9, 3},
                        {10, 9},
                        {3, 8},
                        {4, 8},
                        {2, 6},
                        {2, 4},
                        {7, 8},
                        {8, 6},
                        {10, 6},
                        {7, 7},
                        {2, 4},
                        {5, 2}
                };
            case 31:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {1, 5},
                        {3, 3},
                        {5, 7},
                        {5, 4},
                        {6, 6},
                        {7, 4},
                        {8, 3},
                        {9, 3},
                        {10, 9},
                        {3, 8},
                        {4, 8},
                        {2, 6},
                        {2, 4},
                        {7, 8},
                        {8, 6},
                        {10, 6},
                        {7, 7},
                        {2, 4},
                        {5, 2},
                        {8, 3},
                        {2, 1},
                        {7, 3},
                        {9, 1},
                        {3, 2},
                        {5, 4},
                        {5, 4},
                        {3, 4},
                        {6, 9},
                        {10, 1},
                        {1, 9}
                };
            case 50:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {1, 5},
                        {3, 3},
                        {5, 7},
                        {5, 4},
                        {6, 6},
                        {7, 4},
                        {8, 3},
                        {9, 3},
                        {10, 9},
                        {3, 8},
                        {4, 8},
                        {2, 6},
                        {2, 4},
                        {7, 8},
                        {8, 6},
                        {10, 6},
                        {7, 7},
                        {2, 4},
                        {5, 2},
                        {8, 3},
                        {2, 1},
                        {7, 3},
                        {9, 1},
                        {3, 2},
                        {5, 4},
                        {5, 4},
                        {3, 4},
                        {6, 9},
                        {10, 1},
                        {1, 9},

                        {2, 7},
                        {3, 5},
                        {4, 3},
                        {5, 7},
                        {6, 4},
                        {7, 6},
                        {8, 4},
                        {9, 3},
                        {10, 3},
                        {1, 9},
                        {2, 8},
                        {3, 8},
                        {4, 6},
                        {5, 4},
                        {6, 8},
                        {7, 6},
                        {8, 6},
                        {9, 7},
                        {2, 9}
                };
            case 62:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {1, 5},
                        {3, 3},
                        {5, 7},
                        {5, 4},
                        {6, 6},
                        {7, 4},
                        {8, 3},
                        {9, 3},
                        {10, 9},
                        {3, 8},
                        {4, 8},
                        {2, 6},
                        {2, 4},
                        {7, 8},
                        {8, 6},
                        {10, 6},
                        {7, 7},
                        {2, 4},
                        {5, 2},
                        {8, 3},
                        {2, 1},
                        {7, 3},
                        {9, 1},
                        {3, 2},
                        {5, 4},
                        {5, 4},
                        {3, 4},
                        {6, 9},
                        {10, 1},
                        {1, 9},

                        {2, 7},
                        {3, 5},
                        {4, 3},
                        {5, 7},
                        {6, 4},
                        {7, 6},
                        {8, 4},
                        {9, 3},
                        {10, 3},
                        {1, 9},
                        {2, 8},
                        {3, 8},
                        {4, 6},
                        {5, 4},
                        {6, 8},
                        {7, 6},
                        {8, 6},
                        {9, 7},
                        {10, 4},
                        {1, 2},
                        {2, 3},
                        {3, 1},
                        {4, 3},
                        {5, 1},
                        {6, 2},
                        {7, 4},
                        {8, 4},
                        {9, 4},
                        {10, 9},
                        {1, 1},
                        {2, 9}
                };
            case 93:
                return new int[][]{
                        {0, 0},
                        {1, 7},
                        {1, 5},
                        {3, 3},
                        {5, 7},
                        {5, 4},
                        {6, 6},
                        {7, 4},
                        {8, 3},
                        {9, 3},
                        {10, 9},
                        {3, 8},
                        {4, 8},
                        {2, 6},
                        {2, 4},
                        {7, 8},
                        {8, 6},
                        {10, 6},
                        {7, 7},
                        {2, 4},
                        {5, 2},
                        {8, 3},
                        {2, 1},
                        {7, 3},
                        {9, 1},
                        {3, 2},
                        {5, 4},
                        {5, 4},
                        {3, 4},
                        {6, 9},
                        {10, 1},
                        {1, 9},

                        {2, 7},
                        {3, 5},
                        {4, 3},
                        {5, 7},
                        {6, 4},
                        {7, 6},
                        {8, 4},
                        {9, 3},
                        {10, 3},
                        {1, 9},
                        {2, 8},
                        {3, 8},
                        {4, 6},
                        {5, 4},
                        {6, 8},
                        {7, 6},
                        {8, 6},
                        {9, 7},
                        {10, 4},
                        {1, 2},
                        {2, 3},
                        {3, 1},
                        {4, 3},
                        {5, 1},
                        {6, 2},
                        {7, 4},
                        {8, 4},
                        {9, 4},
                        {10, 9},
                        {1, 1},
                        {2, 9},

                        {2, 1},
                        {3, 2},
                        {4, 3},
                        {5, 4},
                        {6, 5},
                        {7, 6},
                        {8, 7},
                        {9, 8},
                        {10, 9},
                        {1, 10},
                        {2, 1},
                        {3, 2},
                        {4, 3},
                        {5, 4},
                        {6, 5},
                        {7, 6},
                        {8, 7},
                        {9, 8},
                        {10, 9},
                        {1, 10},
                        {2, 1},
                        {3, 2},
                        {4, 3},
                        {5, 4},
                        {6, 5},
                        {7, 6},
                        {8, 7},
                        {9, 8},
                        {10, 9},
                        {1, 10},
                        {2, 1}
                };
                default:
                    return null;
        }
    }

}