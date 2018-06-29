package com.yicoder.commonutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: testDemo
 * @Package: com.yicoder.commonutils
 * @ClassName: ${TYPE_NAME}
 * @Description: java类作用描述
 * @Author: 作者姓名
 * @CreateDate: 2018/6/19 15:36
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/6/19 15:36
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class MOE {

    private static int bestvalue = Integer.MAX_VALUE;
    private static String bestfour = "";
    private static int[][] distance;
//    private static Map<String, Integer> map = new HashMap<String, Integer>() {
//        {
//            put("A", 1);
//            put("B", 2);
//            put("C", 3);
//            put("D", 4);
//            put("E", 5);
//            put("F", 6);
//        }
//    };

//    private static Map<String, Integer> map = new HashMap<String, Integer>() {
//        {
//            put("A", 1);
//            put("B", 2);
//            put("C", 3);
//            put("D", 4);
//            put("E", 5);
//            put("F", 6);
//            put("G", 7);
//            put("H", 8);
//            put("I", 9);
//            put("J", 10);
//        }
//    };
    private static Map<String, Integer> map = new HashMap<String, Integer>() {
        {
            put("A", 1);
            put("B", 2);
            put("C", 3);
            put("D", 4);
            put("E", 5);
            put("F", 6);
            put("G", 7);
            put("H", 8);
            put("I", 9);
            put("J", 10);
            put("K", 11);
            put("L", 12);
        }
    };

    // 使用穷举法结算
//    private static Map<String, Integer> map = new HashMap<String, Integer>() {
//        {
//            put("A", 1);
//            put("B", 2);
//            put("C", 3);
//            put("D", 4);
//            put("E", 5);
//            put("F", 6);
//            put("G", 7);
//            put("H", 8);
//            put("I", 9);
//            put("J", 10);
//            put("K", 11);
//            put("L", 12);
//            put("M", 13);
//            put("N", 14);
//            put("O", 15);
//            put("P", 16);
//            put("Q", 17);
//        }
//    };

    // 使用穷举法结算
    public static void main(String[] args) {
        long starttime = System.currentTimeMillis();
        System.out.println("蚁群算法开始：");
        System.out.println("蚁群算法开始：");
        System.out.println("蚁群算法开始：");
        System.out.println("starttime:" + starttime);

//        int[][] cityDot = new int[][] {
//                {0, 0},
//                {1, 7},
//                {2, 5},
//                {3, 3},
//                {5, 7},
//                {6, 6},
//                {8, 2}
//        };

//        int[][] cityDot = new int[][] {
//                {0, 0},
//                {1, 7},
//                {1, 5},
//                {3, 3},
//                {5, 7},
//                {5, 4},
//                {6, 6},
//                {7, 4},
//                {8, 3},
//                {9, 3},
//                {10, 9}
//        };

        int[][] cityDot = new int[][] {
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

//        int[][] cityDot = new int[][] {
//                {0, 0},
//                {1, 7},
//                {1, 5},
//                {3, 3},
//                {5, 7},
//                {5, 4},
//                {6, 6},
//                {7, 4},
//                {8, 3},
//                {9, 3},
//                {10, 9},
//                {3, 8},
//                {4, 8},
//                {2, 6},
//                {2, 4},
//                {7, 8},
//                {8, 6},
//                {10, 6}
//        };

        int cityNum = cityDot.length;
        // 列数
        int colnum = 11;

        // 生成货位所在位置两两间的距离矩阵
        distance = new int[cityNum][cityNum];
        distance[0][0] = 0; //对角线为0
        // 先设置起点和其它节点间的距离
        for(int j = 1; j < cityNum; j++) {
            if(cityDot[j][0] % 2 == 0) {
                // 过道为偶数
                distance[0][j] = (colnum) + cityDot[j][0] + (colnum - cityDot[j][1]);
                distance[j][0] = cityDot[j][0] + (cityDot[j][1] + 1);
            } else {
                // 过道为奇数
                distance[0][j] = cityDot[j][0] + (cityDot[j][1] + 1);
                distance[j][0] = (colnum - cityDot[j][1]) + cityDot[j][0] + (colnum);
            }
        }
        // 设置货位点两两间的距离矩阵
        for (int i = 1; i < cityNum - 1; i++) {
            distance[i][i] = 0;  //对角线为0
            for (int j = i + 1; j < cityNum; j++) {
                if (cityDot[i][0] % 2 == 0) {
                    if(cityDot[j][0] % 2 == 0) {
                        // i点在偶数列，j点在偶数列
                        if(cityDot[i][0] != cityDot[j][0]) {
                            // i和j点不同行
                            distance[i][j] = (cityDot[i][1] + 1) + Math.abs(cityDot[i][0] - cityDot[j][0]) + (colnum) + (colnum - cityDot[j][1]);
                            distance[j][i] = (cityDot[j][1] + 1) + Math.abs(cityDot[j][0] - cityDot[i][0]) + (colnum) + (colnum - cityDot[i][1]);
                        } else {
                            // i和j点在同一行
                            if(cityDot[i][1] > cityDot[j][1]) {
                                // i点比j点列大
                                distance[i][j] = cityDot[i][1] - cityDot[j][1];
                                distance[j][i] = Integer.MAX_VALUE; // j到i走法无意义，设置为无穷大
                            } else {
                                distance[i][j] = Integer.MAX_VALUE; // i到j走法无意义，设置为无穷大
                                distance[j][i] = cityDot[j][1] - cityDot[i][1];
                            }
                        }
                    } else {
                        // i点在偶数列，j点在奇数列
                        distance[i][j] = (cityDot[i][1] + 1) + Math.abs(cityDot[i][0]-cityDot[j][0]) + (cityDot[j][1] + 1);
                        distance[j][i] = (colnum - cityDot[j][1]) + Math.abs(cityDot[j][0]-cityDot[i][0]) + (colnum - cityDot[i][1]);
                    }

                } else if(cityDot[i][0] % 2 != 0 && cityDot[j][0] % 2 != 0) {
                    // i点在奇数列，j点在奇数列
                    if(cityDot[i][0] != cityDot[j][0]) {
                        // i和j点不同行
                        distance[i][j] = (colnum - cityDot[i][1]) + Math.abs(cityDot[i][0] - cityDot[j][0]) + (colnum) + (cityDot[j][1] + 1);
                        distance[j][i] = (colnum - cityDot[j][1]) + Math.abs(cityDot[j][0] - cityDot[i][0]) + (colnum) + (cityDot[i][1] + 1);
                    } else {
                        // i和j点在同一行
                        if(cityDot[i][1] > cityDot[j][1]) {
                            // i点比j点列大
                            distance[i][j] = Integer.MAX_VALUE; // i到j走法无意义，设置为无穷大
                            distance[j][i] = cityDot[i][1] - cityDot[j][1];
                        } else {
                            distance[i][j] = cityDot[j][1] - cityDot[i][1];
                            distance[j][i] = Integer.MAX_VALUE; // j到i走法无意义，设置为无穷大
                        }
                    }
                } else {
                    // i点在奇数列，j点在偶数列
                    distance[i][j] = (colnum - cityDot[i][1]) + Math.abs(cityDot[i][0]-cityDot[j][0]) + (colnum - cityDot[j][1]);
                    distance[j][i] = (cityDot[j][1] + 1) + Math.abs(cityDot[j][0]-cityDot[i][0]) + (cityDot[i][1] + 1);
                }
            }
        }

        // 输出距离矩阵
        for(int i = 0; i < distance.length; i++) {
            for(int j =0; j < distance[0].length; j++) {
                System.out.print(distance[i][j] + "\t");
            }
            System.out.println();
        }

        String[] chs = new String[] {
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "I",
                "J",
                "K",
                "L"
        };

//        String[] chs = new String[] {
//                "A",
//                "B",
//                "C",
//                "D",
//                "E",
//                "F",
//                "G",
//                "H",
//                "I",
//                "J",
//                "K",
//                "L",
//                "M",
//                "N",
//                "O",
//                "P",
//                "Q"
//        };

//        String[] chs = new String[] {
//                "A",
//                "B",
//                "C",
//                "D",
//                "E",
//                "F"
//        };

        arrange(chs, 0, chs.length);

        System.out.println("The optimal length is:" + bestvalue);
        System.out.println("The optimal tour is: " + bestfour);

        long endtime = System.currentTimeMillis();
        System.out.println("starttime:" + endtime);
        System.out.println("take time:" + (endtime-starttime) + "ms");

    }

    private static void arrange(String[] chs, int start, int len){

        if(start == len-1){
            String append="";
            for(int i=0; i<chs.length; ++i) {
                append += chs[i];
            }

            int sum = 0;
            char[] chars = append.toCharArray();
            sum += distance[0][map.get(chars[0]+"").intValue()];
            for (int j = 0; j < chars.length - 1; j++) {
                sum += distance[map.get(chars[j]+"").intValue()][map.get(chars[j + 1]+"").intValue()];
            }
            sum += distance[map.get(chars[chars.length-1]+"").intValue()][0];
            if(sum > 0 && sum < bestvalue) {
                bestvalue = sum;
                bestfour = append;
            }
            return;
        }
        for(int i=start; i<len; i++){
            String temp = chs[start];
            chs[start] = chs[i];
            chs[i] = temp;
            arrange(chs, start+1, len);
            temp = chs[start];
            chs[start] = chs[i];
            chs[i] = temp;
        }
    }
}

