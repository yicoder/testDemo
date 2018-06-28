package com.yicoder.commonutils;

import java.util.Random;
import java.util.Vector;

/**
 * @Package:        com.yicoder.commonutils
 * @ClassName:      Ant
 * @Description:    蚂蚁对象，选择路径，计算距离长度
 * @Author:         ziping
 * @CreateDate:     2018/6/21 15:52
 * @UpdateUser:     ziping
 * @UpdateDate:     2018/6/21 15:52
 * @UpdateRemark:   The modified content
 * @Version:        1.0
 * Copyright: Copyright (c) 2018/6/21
 */
public class Ant implements Cloneable {

  private Vector<Integer> tabu; //禁忌表
  private Vector<Integer> allowedCities; //允许搜索的城市
  private float[][] delta; //信息数变化矩阵，信息素增量
  private int[][] distance; //距离矩阵
  private float[][] distanceBeta; //距离矩阵

  private float alpha;
  private float beta;

  private int tourLength; //路径长度
  private int cityNum; //城市数量

  private int firstCity; //起始城市
  private int currentCity; //当前城市

  public Ant(){
    cityNum = 30;
    tourLength = 0;

  }

  /**
   * Constructor of Ant
   * @param num 蚂蚁数量
   */
  public Ant(int num){
    cityNum = num;
    tourLength = 0;

  }

  /**
   * 初始化蚂蚁
   * @param distance 距离矩阵
   * @param distanceBeta 距离矩阵指数
   * @param a alpha
   * @param b beta
   */
  public void init(int[][] distance, float[][] distanceBeta, float a, float b){
    alpha = a;
    beta = b;
    allowedCities = new Vector<Integer>();
    tabu = new Vector<Integer>();
    this.distance = distance;
    this.distanceBeta = distanceBeta;
    delta = new float[cityNum][cityNum];
    for (int i = 0; i < cityNum; i++) {
      Integer integer = new Integer(i);
      allowedCities.add(integer);
      for (int j = 0; j < cityNum; j++) {
        delta[i][j] = 0.f;
      }
    }

    // 用第一个货道货位作为起止点
    firstCity = 0;
    for (Integer i:allowedCities) {
      if (i.intValue() == firstCity) {
        allowedCities.remove(i);
        break;
      }
    }

    tabu.add(Integer.valueOf(firstCity));
    currentCity = firstCity;
  }

  /**
   * 选择下一个城市
   * @param pheromone 信息素矩阵
   */
  public void selectNextCity(float[][] pheromone){
    float[] p = new float[cityNum];
    float dbTotal=0.f;
    //计算概率矩阵
    for (int i = 0; i < cityNum; i++) {
      boolean flag = false;
      for (Integer j:allowedCities) {
        if (i == j.intValue()) {
          p[i] = (float) Math.pow(pheromone[currentCity][j],alpha)*distanceBeta[currentCity][j];
          dbTotal += p[i];
          flag = true;
          break;
        }
      }

      if (flag == false) {
        p[i] = 0.f;
      }
    }

    //轮盘选择
    int selectCity = 0;
    float dbTemp=0.0f;
    Random random = new Random();
    if (dbTotal > 0.0) {//总的信息素值大于0
      dbTemp = (float) (dbTotal * new Random().nextDouble()); //取一个随机数
      for (int i=0;i<cityNum;i++) {
        if (allowedCities.contains(Integer.valueOf(i))) {//城市没去过
          dbTemp=dbTemp-p[i]; //这个操作相当于转动轮盘，如果对轮盘选择不熟悉，仔细考虑一下
          if (dbTemp < 0.0) { //轮盘停止转动，记下城市编号，直接跳出循环
            selectCity=i;
            break;
          }
        }
      }
    }
    //=================================================================
    //如果城市间的信息素非常小 ( 小到比float能够表示的最小的数字还要小 )
    //那么由于浮点运算的误差原因，上面计算的概率总和可能为0
    //会出现经过上述操作，没有城市被选择出来
    //出现这种情况，就把第一个没去过的城市作为返回结果
    if (selectCity == 0) {
      for (Integer i:allowedCities) {
        //城市没去过,设置为下一个选择要去的城市
        selectCity = i;
        break;
      }
    }

    //从允许选择的城市中去除select city
    for (Integer i:allowedCities) {
      if (i.intValue() == selectCity) {
        allowedCities.remove(i);
        break;
      }
    }
    //在禁忌表中添加select city
    tabu.add(Integer.valueOf(selectCity));
    //将当前城市改为选择的城市
    currentCity = selectCity;

  }

  /**
   * 计算路径长度
   * @return 路径长度
   */
  public int calculateTourLength(){

    if(this.tabu == null) {
      return this.tourLength;
    }

    int len = 0;
    for (int i = 0; i < cityNum; i++) {
      len += distance[this.tabu.get(i).intValue()][this.tabu.get(i+1).intValue()];
    }
    tourLength = len;
    return len;
  }

  public Vector<Integer> getAllowedCities() {
    return allowedCities;
  }

  public void setAllowedCities(Vector<Integer> allowedCities) {
    this.allowedCities = allowedCities;
  }

  public int getTourLength() {
    return tourLength;
  }
  public void setTourLength(int tourLength) {
    this.tourLength = tourLength;
  }
  public int getCityNum() {
    return cityNum;
  }
  public void setCityNum(int cityNum) {
    this.cityNum = cityNum;
  }

  public Vector<Integer> getTabu() {
    return tabu;
  }

  public void setTabu(Vector<Integer> tabu) {
    this.tabu = tabu;
  }

  public float[][] getDelta() {
    return delta;
  }

  public void setDelta(float[][] delta) {
    this.delta = delta;
  }

  public int getFirstCity() {
    return firstCity;
  }

  public void setFirstCity(int firstCity) {
    this.firstCity = firstCity;
  }

  @Override
  public Object clone() {
    Ant ant = null;
    try {
      ant = (Ant) super.clone();   //浅复制
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    ant.tabu = (Vector<Integer>) this.tabu.clone();   //深度复制
    return ant;
  }

}