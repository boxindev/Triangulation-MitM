package mitmsearch.mitm;

import gurobi.*;

public class MitmFactory {
  private GRBModel model;
  private static final int[][] rho = new int[][]{{0,36,3,41,18},{1,44,10,45,2},{62,6,43,15,61},{28,55,25,21,56},{27,20,39,8,14}};


  public MitmFactory(final GRBModel model) {
    this.model = model;
  }

  public void addfixed_in(GRBVar[][][][] DA, GRBVar[][][][] DB, GRBVar[][] Cond) throws GRBException {

    for (int i = 0; i < 32; i++) {
      if (i < 30) {
        model.addConstr(DA[0][i][0][1], GRB.EQUAL, 1, "");
        model.addConstr(linExprOf(DA[0][i][0][0],DA[0][i][0][2]), GRB.GREATER_EQUAL, 1, "");
      }
      else {
        for (int l = 0; l < 3; l++)
          model.addConstr(DA[0][i][0][l], GRB.EQUAL, 1, "");
      }
    }
    for (int i = 0; i < 32; i++)
      for (int j = 1; j < 5; j++)
        for (int l = 0; l < 3; l++) {
          model.addConstr(DA[0][i][j][l], GRB.EQUAL, 1, "");
        }
    //DB
    for (int i = 0; i < 32; i ++) {
      //DB 0 and 4
      double[] t1 = {2, 2, -1, -1, -1, -1, 1};
      model.addConstr(linExprOf(t1, DA[0][i][0][0], DA[0][i][0][2], DB[0][i][0][0], DB[0][i][0][2], DB[0][i][4][0], DB[0][i][4][2], Cond[i][0]), GRB.EQUAL, 0, "");
      double[][] t2 = {{0, -1, 0, 1, 0, 0, 0},
              {2, 1, -1, -1, -1, 0, 1},
              {-1, 0, 1, 0, 0, 0, 0},
              {-1, 0, 0, 0, 1, 0, 0},
              {-1, 0, 1, 0, 1, 0, -1},
              {2, 1, -1, 0, -1, 0, 0}};
      double[] c2 = {0, 0, 0, 0, 0, 0};
      for (int q = 0; q < 6; q ++)
        model.addConstr(linExprOf(t2[q], DA[0][i][0][0], DA[0][i][0][2], DB[0][i][0][0], DB[0][i][0][2], DB[0][i][4][0], DB[0][i][4][2], Cond[i][0]), GRB.GREATER_EQUAL, c2[q], "");

      model.addConstr(DB[0][i][0][1], GRB.EQUAL, 1, "");
      model.addConstr(DB[0][i][4][1], GRB.EQUAL, 1, "");

      //DB 1 and 2
      for (int l = 0; l < 3; l++) {
        model.addConstr(DB[0][i][2][l], GRB.EQUAL, 1, "");
        model.addConstr(DB[0][i][1][l], GRB.EQUAL, DA[0][i][0][l], "");
      }
      //DB 3
      model.addConstr(DB[0][i][3][1], GRB.EQUAL, 1, "");
      double[] t3 = {1, 1, -1, -1, 1};
      model.addConstr(linExprOf(t3, DA[0][i][0][0], DA[0][i][0][2], DB[0][i][3][0], DB[0][i][3][2], Cond[i][1]), GRB.EQUAL, 0, "");
      double[][] t4 = {{0, 0, 1, 1, -1},
              {1, 0, -1, 0, 1},
              {-1, 0, 1, 0, 0}};
      double[] c4 = {1, 0, 0};
      for (int q = 0; q < 3; q ++)
        model.addConstr(linExprOf(t4[q], DA[0][i][0][0], DA[0][i][0][2], DB[0][i][3][0], DB[0][i][3][2], Cond[i][1]), GRB.GREATER_EQUAL, c4[q], "");
    }

    double[][] t = {{1, 1, -1}, {1, -1, 1}, {-1, 1, 1}, {-1, -1, -1}};
    double[] c = {0, 0, 0, -2};
    for (int i = 0; i < 32; i ++) {
      //model.addConstr(Cond[i][1], GRB.EQUAL, 1, "");
      for (int q = 0; q < 4; q++)
        model.addConstr(linExprOf(t[q], DA[0][i][0][0], DA[0][i][0][2], Cond[i][0]), GRB.GREATER_EQUAL, c[q], "");
      model.addConstr(Cond[i][1], GRB.EQUAL, Cond[i][0], "");
    }
  }

  public void addfixed_in_new(GRBVar[][][][] DA) throws GRBException {

    for (int i = 0; i < 32; i++) {
      if (i < 30) {
        model.addConstr(DA[0][i][0][1], GRB.EQUAL, 1, "");
        model.addConstr(linExprOf(DA[0][i][0][0],DA[0][i][0][2]), GRB.LESS_EQUAL, 1, "");
      }
      else {
        model.addConstr(DA[0][i][0][0], GRB.EQUAL, 0, "");
        model.addConstr(DA[0][i][0][1], GRB.EQUAL, 1, "");
        model.addConstr(DA[0][i][0][2], GRB.EQUAL, 0, "");
      }
    }
    for (int i = 0; i < 32; i++)
      for (int j = 1; j < 5; j++) {
        model.addConstr(DA[0][i][j][0], GRB.EQUAL, 0, "");
        model.addConstr(DA[0][i][j][1], GRB.EQUAL, 1, "");
        model.addConstr(DA[0][i][j][2], GRB.EQUAL, 0, "");
      }
  }

  public void addSbox_cond_new(GRBVar[][][][] DA, GRBVar[][][][] DB, GRBVar[][] Cond) throws GRBException {
    for (int i = 0; i < 32; i ++) {
      //DB 0 and 4
      model.addConstr(DB[0][i][0][1], GRB.EQUAL, 1, "");
      model.addConstr(DB[0][i][4][1], GRB.EQUAL, 1, "");
      for (int l = 0; l < 3; l++) {
        model.addConstr(DB[0][i][0][l], GRB.LESS_EQUAL, DA[0][i][0][l], "");
        model.addConstr(DB[0][i][4][l], GRB.LESS_EQUAL, DA[0][i][0][l], "");
      }
      double[] t1 = {1, 1, -1};
      model.addConstr(linExprOf(t1, DB[0][i][0][0], DB[0][i][4][0], DA[0][i][0][0]), GRB.GREATER_EQUAL, 0, "");
      model.addConstr(linExprOf(t1, DB[0][i][0][2], DB[0][i][4][2], DA[0][i][0][2]), GRB.GREATER_EQUAL, 0, "");

      //DB 1
      for (int l = 0; l < 3; l++) {
        model.addConstr(DB[0][i][1][l], GRB.EQUAL, DA[0][i][0][l], "");
      }

      //DB 2
      model.addConstr(DB[0][i][2][0], GRB.EQUAL, 0, "");
      model.addConstr(DB[0][i][2][1], GRB.EQUAL, 1, "");
      model.addConstr(DB[0][i][2][2], GRB.EQUAL, 0, "");

      //DB 3
      model.addConstr(DB[0][i][3][1], GRB.EQUAL, 1, "");
      for (int l = 0; l < 3; l++) {
        model.addConstr(DB[0][i][3][l], GRB.LESS_EQUAL, DA[0][i][0][l], "");
      }
    }


  }


  public void addxor(GRBVar[][][][] DA, GRBVar[][][][] DB, GRBVar[][][] DC) throws GRBException {
    GRBVar[][][][] DB_Allone = new GRBVar[DB.length][32][5][3];


    //GRBVar[][][] DB_Twored = new GRBVar[DB.length][32][5];
    double[] t1 = {1.0, 1.0, -1.0};

    for (int round = 0; round < DB.length; round ++)
      for (int i = 0; i < 32; i ++)
        for (int j = 0; j < 5; j ++) {
          for (int l = 0; l < 3; l ++) {
            DB_Allone[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_Allone_"+round+"_"+i+"_"+j+"_"+l);
            if (j == 0)
              Determine_AllOne(DB_Allone[round][i][j][l], DB[round][i][j][l], DB[round][(i-19+32)%32][j][l], DB[round][(i-28+32)%32][j][l]);
            if (j == 1)
              Determine_AllOne(DB_Allone[round][i][j][l], DB[round][i][j][l], DB[round][(i-61+64)%32][j][l], DB[round][(i-39+64)%32][j][l]);
            if (j == 2)
              Determine_AllOne(DB_Allone[round][i][j][l], DB[round][i][j][l], DB[round][(i-1+32)%32][j][l], DB[round][(i-6+32)%32][j][l]);
            if (j == 3)
              Determine_AllOne(DB_Allone[round][i][j][l], DB[round][i][j][l], DB[round][(i-10+32)%32][j][l], DB[round][(i-17+32)%32][j][l]);
            if (j == 4)
              Determine_AllOne(DB_Allone[round][i][j][l], DB[round][i][j][l], DB[round][(i-7+32)%32][j][l], DB[round][(i-41+64)%32][j][l]);
          }
	  /*DB_Twored[round][i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_Twored_"+round+"_"+i+"_"+j);
	  if (j == 0)
            Determine_twointhree(DB_Twored[round][i][j],DB[round][i][j][0],DB[round][(i-19+32)%32][j][0],DB[round][(i-28+32)%32][j][0]);
	  if (j == 1)
            Determine_twointhree(DB_Twored[round][i][j],DB[round][i][j][0],DB[round][(i-61+64)%32][j][0],DB[round][(i-39+64)%32][j][0]);
	  if (j == 2)
            Determine_twointhree(DB_Twored[round][i][j],DB[round][i][j][0],DB[round][(i-1+32)%32][j][0],DB[round][(i-6+32)%32][j][0]);
	  if (j == 3)
            Determine_twointhree(DB_Twored[round][i][j],DB[round][i][j][0],DB[round][(i-10+32)%32][j][0],DB[round][(i-17+32)%32][j][0]);
	  if (j == 4)
            Determine_twointhree(DB_Twored[round][i][j],DB[round][i][j][0],DB[round][(i-7+32)%32][j][0],DB[round][(i-41+64)%32][j][0]);*/


          model.addConstr(DB_Allone[round][i][j][1], GRB.EQUAL, DA[round+1][i][j][1], "");
          model.addConstr(DB_Allone[round][i][j][1], GRB.GREATER_EQUAL, DA[round+1][i][j][0], "");
          model.addConstr(DB_Allone[round][i][j][0], GRB.LESS_EQUAL, DA[round+1][i][j][0], "");
          model.addConstr(linExprOf(t1, DC[round][i][j], DB_Allone[round][i][j][0], DA[round+1][i][j][0]), GRB.EQUAL, 0, "");
          model.addConstr(DB_Allone[round][i][j][2], GRB.EQUAL, DA[round+1][i][j][2], "");


          //model.addConstr(DC[round][i][j], GRB.LESS_EQUAL, DB_Twored[round][i][j], "");

        }
  }


  public void addxor_new(GRBVar[][][][] DA, GRBVar[][][][] DB, GRBVar[][][][] DC) throws GRBException {
    GRBVar[][][] DB_Allone = new GRBVar[DB.length][32][5]; // v1
    GRBVar[][][][] DB_Existone = new GRBVar[DB.length][32][5][2]; // v0, v2
    GRBVar[][][] DB_Nowhite = new GRBVar[DB.length][32][5]; // v3
    GRBVar[][][] DB_White = new GRBVar[DB.length][32][5];

    double[] t0 = {1.0, 1.0, -1.0};
    double[] t1 = {1.0, 1.0, -1.0};  // nolinear
    double[][] t2 = {{1.0, -1.0, 0, 1.0},
            {1.0, 0, -1.0, 1.0},
            {-1.0, 1.0, 1.0, -1.0}};  //linear
    double[] c = {0, 0, 1};

    for (int round = 0; round < DB.length; round++){

      for (int i = 0; i < 32; i ++) {

        for (int j = 0; j < 5; j++) {
          DB_White[round][i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_White" + round + "_" + i + "_" + j);
          Determine_Allzero(DB_White[round][i][j], DB[round][i][j][0], DB[round][i][j][1], DB[round][i][j][2]);
        }
      }

      for (int i = 0; i < 32; i++) {

        for (int j = 0; j < 5; j++) {
          DB_Allone[round][i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_Allone_" + round + "_" + i + "_" + j);
          if (j == 0)
            Determine_AllOne(DB_Allone[round][i][j], DB[round][i][j][1], DB[round][(i - 19 + 32) % 32][j][1], DB[round][(i - 28 + 32) % 32][j][1]);
          if (j == 1)
            Determine_AllOne(DB_Allone[round][i][j], DB[round][i][j][1], DB[round][(i - 61 + 64) % 32][j][1], DB[round][(i - 39 + 64) % 32][j][1]);
          if (j == 2)
            Determine_AllOne(DB_Allone[round][i][j], DB[round][i][j][1], DB[round][(i - 1 + 32) % 32][j][1], DB[round][(i - 6 + 32) % 32][j][1]);
          if (j == 3)
            Determine_AllOne(DB_Allone[round][i][j], DB[round][i][j][1], DB[round][(i - 10 + 32) % 32][j][1], DB[round][(i - 17 + 32) % 32][j][1]);
          if (j == 4)
            Determine_AllOne(DB_Allone[round][i][j], DB[round][i][j][1], DB[round][(i - 7 + 32) % 32][j][1], DB[round][(i - 41 + 64) % 32][j][1]);

          for (int l = 0; l < 2; l++) {
            DB_Existone[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_Existone" + round + "_" + i + "_" + j + "_" + l);
          }
          if (j == 0) {
            Determine_ExistOne(DB_Existone[round][i][j][0], DB[round][i][j][0], DB[round][(i - 19 + 32) % 32][j][0], DB[round][(i - 28 + 32) % 32][j][0]);
            Determine_ExistOne(DB_Existone[round][i][j][1], DB[round][i][j][2], DB[round][(i - 19 + 32) % 32][j][2], DB[round][(i - 28 + 32) % 32][j][2]);
          }
          if (j == 1) {
            Determine_ExistOne(DB_Existone[round][i][j][0], DB[round][i][j][0], DB[round][(i - 61 + 64) % 32][j][0], DB[round][(i - 39 + 64) % 32][j][0]);
            Determine_ExistOne(DB_Existone[round][i][j][1], DB[round][i][j][2], DB[round][(i - 61 + 64) % 32][j][2], DB[round][(i - 39 + 64) % 32][j][2]);
          }
          if (j == 2) {
            Determine_ExistOne(DB_Existone[round][i][j][0], DB[round][i][j][0], DB[round][(i - 1 + 32) % 32][j][0], DB[round][(i - 6 + 32) % 32][j][0]);
            Determine_ExistOne(DB_Existone[round][i][j][1], DB[round][i][j][2], DB[round][(i - 1 + 32) % 32][j][2], DB[round][(i - 6 + 32) % 32][j][2]);
          }
          if (j == 3) {
            Determine_ExistOne(DB_Existone[round][i][j][0], DB[round][i][j][0], DB[round][(i - 10 + 32) % 32][j][0], DB[round][(i - 17 + 32) % 32][j][0]);
            Determine_ExistOne(DB_Existone[round][i][j][1], DB[round][i][j][2], DB[round][(i - 10 + 32) % 32][j][2], DB[round][(i - 17 + 32) % 32][j][2]);
          }
          if (j == 4) {
            Determine_ExistOne(DB_Existone[round][i][j][0], DB[round][i][j][0], DB[round][(i - 7 + 32) % 32][j][0], DB[round][(i - 41 + 64) % 32][j][0]);
            Determine_ExistOne(DB_Existone[round][i][j][1], DB[round][i][j][2], DB[round][(i - 7 + 32) % 32][j][2], DB[round][(i - 41 + 64) % 32][j][2]);
          }

          DB_Nowhite[round][i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_Nowhite" + round + "_" + i + "_" + j);
          if (j == 0)
            Determine_Allzero(DB_Nowhite[round][i][j], DB_White[round][i][j], DB_White[round][(i - 19 + 32) % 32][j], DB_White[round][(i - 28 + 32) % 32][j]);
          if (j == 1)
            Determine_Allzero(DB_Nowhite[round][i][j], DB_White[round][i][j], DB_White[round][(i - 61 + 64) % 32][j], DB_White[round][(i - 39 + 64) % 32][j]);
          if (j == 2)
            Determine_Allzero(DB_Nowhite[round][i][j], DB_White[round][i][j], DB_White[round][(i - 1 + 32) % 32][j], DB_White[round][(i - 6 + 32) % 32][j]);
          if (j == 3)
            Determine_Allzero(DB_Nowhite[round][i][j], DB_White[round][i][j], DB_White[round][(i - 10 + 32) % 32][j], DB_White[round][(i - 17 + 32) % 32][j]);
          if (j == 4)
            Determine_Allzero(DB_Nowhite[round][i][j], DB_White[round][i][j], DB_White[round][(i - 7 + 32) % 32][j], DB_White[round][(i - 41 + 64) % 32][j]);

          model.addConstr(DB_Nowhite[round][i][j], GRB.GREATER_EQUAL, DA[round + 1][i][j][0], "");
          model.addConstr(DB_Existone[round][i][j][0], GRB.GREATER_EQUAL, DA[round + 1][i][j][0], "");
          model.addConstr(linExprOf(t0, DB_Existone[round][i][j][0], DB_Nowhite[round][i][j], DA[round + 1][i][j][0]), GRB.LESS_EQUAL, 1, "");

          model.addConstr(DB_Nowhite[round][i][j], GRB.GREATER_EQUAL, DA[round + 1][i][j][1], "");
          model.addConstr(DB_Allone[round][i][j], GRB.LESS_EQUAL, DA[round + 1][i][j][1], "");
          model.addConstr(linExprOf(t1, DC[round][i][j][0], DB_Allone[round][i][j], DA[round + 1][i][j][1]), GRB.EQUAL, 0, "");

          for (int q = 0; q < 3; q++) {
            model.addConstr(linExprOf(t2[q], DC[round][i][j][1], DB_Existone[round][i][j][1], DB_Nowhite[round][i][j], DA[round + 1][i][j][2]), GRB.LESS_EQUAL, c[q], "");
          }

          model.addConstr(DC[round][i][j][1], GRB.GREATER_EQUAL, DC[round][i][j][0], "");
          double[] t3 = {1.0, 1.0, -1.0};
          model.addConstr(linExprOf(t3, DA[round + 1][i][j][1], DA[round + 1][i][j][2], DB_Nowhite[round][i][j]), GRB.GREATER_EQUAL, 0, "");

        }
      }
  }
  }

  public void addSboxnew(GRBVar[][][][] DA, GRBVar[][][][] DB) throws GRBException {
    GRBVar[][][] DA_AO01234 = new GRBVar[DA.length-1][32][3];
    GRBVar[][][] DA_AO1234 = new GRBVar[DA.length-1][32][3];
    GRBVar[][][] DA_AO0134 = new GRBVar[DA.length-1][32][3];
    //14 12 01 23 13 12 34 04 03
    GRBVar[][][] DA_NotMul = new GRBVar[DA.length-1][32][8];
    for (int round = 0; round < DA.length-1; round ++)
      for (int i = 0; i < 32; i ++)
        for (int l = 0; l < 3; l ++) {
          DA_AO01234[round][i][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_AO01234_"+round+"_"+i+"_"+l);
          Determine_AllOne(DA_AO01234[round][i][l], DA[round][i][0][l], DA[round][i][1][l], DA[round][i][2][l], DA[round][i][3][l], DA[round][i][4][l]);
          DA_AO1234[round][i][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_AO1234_"+round+"_"+i+"_"+l);
          Determine_AllOne(DA_AO1234[round][i][l], DA[round][i][1][l], DA[round][i][2][l], DA[round][i][3][l], DA[round][i][4][l]);
          DA_AO0134[round][i][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_AO0124_"+round+"_"+i+"_"+l);
          Determine_AllOne(DA_AO0134[round][i][l], DA[round][i][0][l], DA[round][i][1][l], DA[round][i][3][l], DA[round][i][4][l]);
        }
    for (int round = 0; round < DA.length-1; round ++)
      for (int i = 0; i < 32; i ++) {
        DA_NotMul[round][i][0] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_"+round+"_"+i+"_0");
        Determine_Notmul(DA[round][i][1][0], DA[round][i][1][2], DA[round][i][4][0], DA[round][i][4][2], DA_NotMul[round][i][0]);
        DA_NotMul[round][i][1] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_"+round+"_"+i+"_1");
        Determine_Notmul(DA[round][i][1][0], DA[round][i][1][2], DA[round][i][2][0], DA[round][i][2][2], DA_NotMul[round][i][1]);
        DA_NotMul[round][i][2] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_"+round+"_"+i+"_2");
        Determine_Notmul(DA[round][i][0][0], DA[round][i][0][2], DA[round][i][1][0], DA[round][i][1][2], DA_NotMul[round][i][2]);

        DA_NotMul[round][i][3] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_"+round+"_"+i+"_3");
        Determine_Notmul(DA[round][i][2][0], DA[round][i][2][2], DA[round][i][3][0], DA[round][i][3][2], DA_NotMul[round][i][3]);
        DA_NotMul[round][i][4] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_"+round+"_"+i+"_4");
        Determine_Notmul(DA[round][i][1][0], DA[round][i][1][2], DA[round][i][3][0], DA[round][i][3][2], DA_NotMul[round][i][4]);

        DA_NotMul[round][i][5] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_"+round+"_"+i+"_5");
        Determine_Notmul(DA[round][i][3][0], DA[round][i][3][2], DA[round][i][4][0], DA[round][i][4][2], DA_NotMul[round][i][5]);

        DA_NotMul[round][i][6] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_"+round+"_"+i+"_6");
        Determine_Notmul(DA[round][i][0][0], DA[round][i][0][2], DA[round][i][4][0], DA[round][i][4][2], DA_NotMul[round][i][6]);
        DA_NotMul[round][i][7] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_"+round+"_"+i+"_7");
        Determine_Notmul(DA[round][i][0][0], DA[round][i][0][2], DA[round][i][3][0], DA[round][i][3][2], DA_NotMul[round][i][7]);
      }

    for (int round = 1; round < DB.length; round ++)
      for (int i = 0; i < 32; i ++)  {
        //DB0
        model.addConstr(DB[round][i][0][0], GRB.EQUAL, DA_AO01234[round][i][0], "");
        model.addConstr(DB[round][i][0][2], GRB.EQUAL, DA_AO01234[round][i][2], "");
        Determine_AllOne(DB[round][i][0][1], DA_AO01234[round][i][1], DA_NotMul[round][i][0], DA_NotMul[round][i][1], DA_NotMul[round][i][2]);
        //DB1
        model.addConstr(DB[round][i][1][0], GRB.EQUAL, DA_AO01234[round][i][0], "");
        model.addConstr(DB[round][i][1][2], GRB.EQUAL, DA_AO01234[round][i][2], "");
        Determine_AllOne(DB[round][i][1][1], DA_AO01234[round][i][1], DA_NotMul[round][i][3], DA_NotMul[round][i][4], DA_NotMul[round][i][1]);
        //DB2
        model.addConstr(DB[round][i][2][0], GRB.EQUAL, DA_AO1234[round][i][0], "");
        model.addConstr(DB[round][i][2][2], GRB.EQUAL, DA_AO1234[round][i][2], "");
        Determine_AllOne(DB[round][i][2][1], DA_AO1234[round][i][1], DA_NotMul[round][i][5]);
        //DB3
        model.addConstr(DB[round][i][3][0], GRB.EQUAL, DA_AO01234[round][i][0], "");
        model.addConstr(DB[round][i][3][2], GRB.EQUAL, DA_AO01234[round][i][2], "");
        Determine_AllOne(DB[round][i][3][1], DA_AO01234[round][i][1], DA_NotMul[round][i][6], DA_NotMul[round][i][7]);
        //DB4
        model.addConstr(DB[round][i][4][0], GRB.EQUAL, DA_AO0134[round][i][0], "");
        model.addConstr(DB[round][i][4][2], GRB.EQUAL, DA_AO0134[round][i][2], "");
        Determine_AllOne(DB[round][i][4][1], DA_AO0134[round][i][1], DA_NotMul[round][i][0], DA_NotMul[round][i][2]);
      }


  }

  //7 colors
  public void addSbox_new(GRBVar[][][][] DA, GRBVar[][][][] DB) throws GRBException {
    double[][] t1 = {
            {-1, 1, 1, -3, 6, 3, -1, 1, 1, -4, 4, 4, -1, 1, 1, 7, -4, -10, 0},
            {0, 0, -1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, 1, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0},
            {-1, 1, 1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1},
            {0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, -4, -1, -1, 0},
            {0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {0, 0, 0, -1, 1, 1, 0, 0, -1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 1, 0, -1, 0},
            {-1, 0, 0, 1, -1, 1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 1, -3, 3, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 1, 0, -1, 0},
            {0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, -1, 2},
            {0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0},
            {0, 0, -1, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1},
            {1, 0, -1, 1, 0, -3, 1, 0, -1, 4, 0, -1, 1, 0, -1, -7, 0, 4, 6},
            {0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0},
            {0, -1, 0, 1, -1, 1, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 1, 0, 4},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, 0, 0, -1, 1, 2, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 1, -1, 1},
            {-1, 0, 0, 1, -1, 1, -1, 0, 0, -1, 0, 0, -1, 0, 0, 4, 0, -3, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0},
            {0, 0, -1, 1, -1, 1, 0, -1, 0, 0, 0, -1, 0, 0, -1, -2, 0, 3, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, -1, 0, 0, 0},
            {0, 0, -1, 1, -1, 1, 0, 0, -1, 0, -1, 0, 0, -1, 0, -1, 0, 2, 4},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0},
            {-1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, -1, 0, 1, -1, 1, 0, 0, -1, 0, -1, 0, 0, 0, -1, 0, 0, 1, 4},
            {0, -1, 0, 1, -1, 1, 0, 0, -1, 0, 0, -1, 0, -1, 0, 0, 0, 1, 4},
            {1, -1, 0, 1, 0, -2, 1, 0, -1, -1, 0, 0, 1, 0, -1, 1, -2, 0, 5},
            {6, -2, 0, 2, -1, -1, 6, 0, -2, 7, -1, -1, 6, 0, -2, -7, 1, 2, 8},
            {0, -1, 1, 0, -1, 1, 0, -1, 1, 0, 0, -1, 0, -1, 1, 0, 0, 1, 4},
            {0, 0, -1, 1, -1, 1, 0, -1, 0, 0, 0, -1, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, -1, 1, -1, 1, 0, -1, 0, 0, -1, 0, 0, 0, -1, -1, 0, 2, 4},
            {1, 0, -1, 1, 0, -1, 1, -1, 0, -1, 0, 0, 1, -1, 0, 1, -1, 0, 4},
            {0, -1, 0, 1, -1, 1, 0, -1, 0, 0, 0, -1, 0, 0, -1, 0, -1, 2, 4},
            {0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 1},
            {3, -1, -2, 3, 0, -3, 3, -1, -2, 7, -3, 0, 3, -3, 0, -7, 1, 3, 12},
            {0, 0, -1, 1, -1, 1, 0, 0, -1, 0, 0, -1, 0, 0, -1, -3, 0, 4, 4},
            {1, -1, 0, 1, 0, -1, 1, -1, 0, 0, 0, -1, 1, -1, 0, 0, -1, 2, 4},
            {0, -1, 1, 0, -1, 1, 0, -1, 1, 0, -2, 0, 0, -1, 1, 0, 2, -1, 5},
            {1, 0, -1, 1, 0, -1, 1, -1, 0, 0, -1, 0, 1, 0, -1, 0, -2, 2, 4},
            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 1, 0, -1, 1},
            {1, 0, -1, 1, 0, -1, 1, 0, -1, -1, 0, 0, 1, -1, 0, 1, -3, 2, 4},
            {0, -1, 0, 1, -1, 1, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, 1, 4},
            {0, 0, -1, 1, -1, 1, 0, 0, -1, 0, -1, 0, 0, 0, -1, -2, 0, 3, 4},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, -1, 0, 1, -1, 1, 0, 0, -1, 0, -1, 0, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, -1, 1, -1, 1, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, -1, 1, -1, 1, 0, 0, -1, 0, 0, -1, 0, -1, 0, 0, -2, 3, 4},
            {1, -1, 0, 1, 0, -1, 1, -1, 0, 0, 0, -1, 1, 0, -1, -1, -1, 2, 4},
            {1, -1, 1, 0, 0, -1, 1, -1, 1, 0, -1, 0, 1, -1, 1, 0, 0, 1, 4},
            {0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {1, -1, 0, 1, 0, -1, 1, 0, -1, 0, 0, -1, 1, -1, 0, 0, -2, 2, 4},
            {1, -1, 0, 1, 0, -1, 1, -1, 0, 0, -1, 0, 1, 0, -1, 0, -1, 1, 4},
            {1, 0, -1, 1, 0, -1, 1, -1, 0, 2, 0, -1, 1, -1, 0, -2, 0, 1, 4},
            {1, 0, -1, 1, 0, -3, 1, 0, -1, -1, 0, 0, 1, 0, -1, -2, -3, 3, 6},
            {0, -1, 0, 1, -1, 1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, -2, 3, 4},
            {1, -1, 1, -1, 0, 0, 1, -1, 1, 0, 0, -1, 1, -1, 1, 1, 0, 0, 4},
            {1, -1, 0, 1, 0, -1, 1, 0, -1, 0, -1, 0, 1, -1, 0, 0, -1, 1, 4},
            {0, 0, -1, 1, -1, 1, -1, 0, 0, 0, -1, 0, -1, 0, 0, 2, -2, 1, 4},
            {1, 0, -1, 1, 0, -2, 1, -1, 0, 0, 0, -1, 1, 0, -1, 0, -4, 3, 5},
            {1, 0, -1, 1, 0, -3, 1, 0, -1, 0, -1, 0, 1, 0, -1, -2, -3, 3, 6},
            {1, 0, -1, 1, 0, -2, 1, 0, -1, 3, 0, -1, 1, -1, 0, -5, 0, 3, 5},
            {1, 0, -1, 1, 0, -1, 1, -1, 0, 2, -1, 0, 1, -1, 0, -2, 0, 1, 4},
            {1, -1, 0, 1, 0, -2, 1, 0, -1, 1, -1, 0, 1, 0, -1, -2, -2, 2, 5},
            {1, -1, 0, 1, 0, -1, 1, 0, -1, 2, 0, -1, 1, 0, -1, -2, 0, 1, 4}
    };
    double[] c1 = {0, -2, 0, -1, 0, 0, -1, 0, -4, 0, -2, -2, 0, -1, -6, -2, 0, -4, 0, -1, -4, 0, 0, 0, 0, -4, 0, -4, 0, -2, 0, -4, -4, -5, -8, -4, -4, -4, -4, -4, -2, -1, -12, -4, -4, -5, -4, 0, -1, -4, -4, -4, 0, -4, -4, -4, -4, -4, -2, -4, -4, -4, -6, -4, -4, -4, -4, -5, -6, -5, -4, -5, -4};
    for (int round = 1; round < DB.length; round ++)
      for (int i = 0; i < 32; i ++)
        for (int q = 0; q < t1.length; q++) {
          model.addConstr(linExprOf(t1[q], DA[round][i][0][0], DA[round][i][0][1], DA[round][i][0][2], DA[round][i][1][0], DA[round][i][1][1], DA[round][i][1][2], DA[round][i][2][0], DA[round][i][2][1], DA[round][i][2][2],  DA[round][i][3][0], DA[round][i][3][1], DA[round][i][3][2], DA[round][i][4][0], DA[round][i][4][1], DA[round][i][4][2], DB[round][i][0][0], DB[round][i][0][1], DB[round][i][0][2]), GRB.GREATER_EQUAL, c1[q], "");
        }

    double[][] t2 = {
            {0, 0, 0, -1, 0, -2, -1, 0, -2, -1, 0, -2, 0, 0, 0, 1, -7, 0, 9},
            {-3, 3, 3, -1, 3, 1, -1, 1, 1, -1, 1, 1, -3, 3, 3, 7, -3, -9, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0},
            {0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, -4, -1, -1, 0},
            {0, 0, 0, 0, 0, -1, 0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, -1, 1},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, -1, 1},
            {0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {0, 0, -1, 1, 0, -1, 1, 0, -1, 1, 0, -1, 0, 0, -1, 0, -4, 3, 4},
            {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 1, 0, -1, 0},
            {0, 1, 1, 0, 0, -2, -2, 0, 0, -1, 0, -1, 0, 1, 1, 0, -1, -5, 6},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 1, -1, 0, 0},
            {0, 0, 0, -1, 1, 1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1},
            {0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, -1, 0, 0, 0, -1, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0},
            {0, -2, 0, 0, -1, 1, 0, -1, 1, 0, -1, 1, 0, -2, 0, 0, 2, -1, 6},
            {11, -2, -1, 3, -2, -1, 7, 0, -3, 7, 0, -3, 11, -2, -1, -11, 2, 3, 12},
            {0, 0, -1, 0, -1, 1, 0, -1, 1, 0, -1, 1, 0, 0, -1, 0, -1, 2, 4},
            {0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {0, 0, 0, 0, 0, -1, -1, 1, 1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, -1, 1},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 1},
            {0, -1, 0, 1, 0, -1, 1, -1, 0, 1, -1, 0, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, -1, 2},
            {0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, -1, 2},
            {0, -1, 0, 1, -1, 0, 1, 0, -1, 1, -1, 0, 0, 0, -1, 0, 0, 1, 4},
            {0, 0, -1, 1, -1, 0, 1, -1, 0, 1, 0, -1, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0},
            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, -1, 0, 1, -1, 1, 1, -1, 1, 0, 0, -1, 0, 0, -1, -1, 0, 2, 4},
            {0, 0, -1, 1, 0, -1, 1, -1, 0, 1, -1, 0, 0, 0, -1, 0, -2, 3, 4},
            {0, 0, -1, 1, -1, 1, 0, 0, -1, 1, -1, 1, 0, -1, 0, 0, -1, 2, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, -1, 0, 0, 0, -1, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, 1, -2, 5},
            {0, -1, 0, 1, 0, -1, 1, 0, -1, 1, 0, -1, 0, -1, 0, 0, -2, 1, 4},
            {0, -1, 0, 1, 0, -1, 1, -1, 0, 1, 0, -1, 0, 0, -1, -1, -1, 2, 4},
            {0, 0, -1, 1, 0, -1, 1, 0, -1, 1, -1, 0, 0, -1, 0, 0, -1, 1, 4},
            {0, -1, 0, 0, 0, -1, 1, -1, 1, 1, -1, 1, 0, 0, -1, 0, -1, 2, 4},
            {0, 0, -1, 1, -1, 1, 1, -1, 1, -1, 0, 0, 0, -1, 0, 1, 0, 0, 4},
            {0, 0, -1, -1, 0, 0, 1, -1, 1, 1, -1, 1, 0, -1, 0, 1, 0, 0, 4},
            {0, 0, -1, 1, -1, 0, 1, 0, -1, 1, 0, -1, 0, 0, -1, 0, -3, 3, 4},
            {0, -1, 0, 0, -1, 1, 0, -1, 1, 0, -1, 1, 0, 0, -1, 0, 0, 1, 4},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, -1, 0, 1, -1, 1, -1, 0, 0, 1, -1, 1, 0, 0, -1, 1, -1, 1, 4},
            {0, -1, 0, 1, -1, 0, 1, 0, -1, 1, 0, -1, 0, -1, 0, 0, -1, 1, 4},
            {0, 0, -1, 0, -1, 1, 0, -1, 1, 0, -1, 1, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 1, 0, -1, 1},
            {0, 0, -1, 1, 0, -1, 1, -1, 0, 1, 0, -1, 0, -1, 0, 0, -1, 1, 4},
            {0, 0, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 0, 0, -1, 0, 0, 2},
            {0, -1, 0, 1, 0, -1, 1, 0, -1, 1, -1, 0, 0, 0, -1, 0, -2, 2, 4},
            {0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {0, 0, -1, 1, -1, 1, 1, -1, 1, 0, 0, -1, 0, 0, -1, 0, 0, 1, 4},
            {0, -1, 0, 1, -1, 1, 1, -1, 1, 0, 0, -1, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, -1, 1, -1, 1, 0, 0, -1, 1, -1, 1, 0, 0, -1, -2, 0, 3, 4},
            {0, -1, 0, 1, -1, 1, 0, 0, -1, 1, -1, 1, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, -1, -1, 0, 0, 1, -1, 1, 1, -1, 1, 0, 0, -1, 1, 0, 0, 4},
            {0, 0, -1, 1, -1, 0, 1, 0, -1, 1, 0, -1, 0, -1, 0, 0, -1, 1, 4},
            {0, -1, 0, 1, -1, 0, 1, 0, -1, 1, 0, -1, 0, 0, -1, 0, -1, 1, 4},
            {0, 0, 0, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 0, 0, -3, 1, 3},
            {-1, 0, 0, 0, 0, -1, 1, -1, 1, 1, -1, 1, 0, -1, 0, 1, -1, 1, 4},
            {0, 0, -1, 1, 0, -1, 1, -1, 0, 1, 0, -1, 0, 0, -1, 0, -1, 1, 4},
            {0, -1, 0, 1, 0, -1, 1, 0, -1, 1, -1, 0, 0, -1, 0, 0, -1, 1, 4},
            {0, 0, -1, 1, 0, -1, 1, 0, -1, 1, -1, 0, 0, 0, -1, -2, -1, 3, 4},
            {0, -1, 0, 1, 0, -1, 1, -1, 0, 1, 0, -1, 0, -1, 0, 0, -1, 1, 4},
            {-1, 0, 0, 1, -1, 1, -1, 0, 0, 1, -1, 1, -1, 0, 0, 3, 0, -2, 4},
            {0, -1, 0, -1, 0, 0, 1, -1, 1, 1, -1, 1, -1, 0, 0, 2, 0, -1, 4},
            {7, -1, -1, 5, 0, -2, 5, 0, -2, 2, -2, 0, 7, -1, -1, -7, 1, 2, 8},
            {0, 0, -1, 1, 0, -1, 1, -1, 0, 1, -1, 0, 0, -1, 0, 0, 0, 1, 4},
            {0, 0, -1, 1, 0, -1, 1, 0, -1, 1, 0, -1, 0, -1, 0, 0, -2, 1, 4},
            {0, -1, 0, 1, 0, -1, 1, 0, -1, 1, 0, -1, 0, 0, -1, 0, -2, 1, 4},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {0, -1, 0, 0, 0, -1, 1, -1, 1, 1, -1, 1, 0, -1, 0, 0, 0, 1, 4},
            {0, -1, 2, -1, 0, 1, 0, -1, 1, -1, 0, 1, -3, 2, 2, 4, -3, -2, 4}
    };
    double[] c2 = {-9, 0, 0, 0, -1, -1, 0, -4, 0, 0, -6, 0, -1, -1, -2, 0, -6, -12, -4, -2, -1, 0, 0, -1, 0, -1, -4, -2, -2, -4, -4, 0, 0, 0, 0, -4, -4, -4, 0, -5, -4, -4, -4, -4, -4, -4, -4, -4, 0, 0, -4, -4, -4, -1, -4, -2, -4, -2, -4, -4, -4, -4, -4, -4, -4, -3, -4, -4, -4, -4, -4, -4, -4, -8, -4, -4, -4, 0, -4, -4};
    for (int round = 1; round < DB.length; round ++)
      for (int i = 0; i < 32; i ++)
        for (int q = 0; q < t2.length; q++) {
          model.addConstr(linExprOf(t2[q], DA[round][i][0][0], DA[round][i][0][1], DA[round][i][0][2], DA[round][i][1][0], DA[round][i][1][1], DA[round][i][1][2], DA[round][i][2][0], DA[round][i][2][1], DA[round][i][2][2], DA[round][i][3][0], DA[round][i][3][1], DA[round][i][3][2], DA[round][i][4][0], DA[round][i][4][1], DA[round][i][4][2], DB[round][i][1][0], DB[round][i][1][1], DB[round][i][1][2]), GRB.GREATER_EQUAL, c2[q], "");
        }


    double[][] t3 = {
            {0, 0, 0, -2, 2, 2, -2, 2, 2, -1, 2, 1, -1, 1, 1, 5, -2, -6, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, -1, 0, -2, 1, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0},
            {0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, -3, -1, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 1, 1, 0, 0, -1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, -1, 0, 0, -1, 1},
            {0, 0, 0, 0, 0, -1, 0, 0, -1, -1, 0, 1, -1, 0, 1, 2, -3, 2, 3},
            {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {0, 0, 0, 0, -2, 0, 0, -2, 0, 0, -1, 1, 0, -1, 1, 0, 2, -1, 5},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 1, 0, -1, 0},
            {0, 0, 0, 8, -2, -1, 8, -2, -1, 3, -2, -1, 7, 0, -3, -8, 2, 3, 9},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, -1, 2},
            {0, 0, 0, 0, 0, -1, 0, 0, -1, 1, -1, 1, 0, 0, -1, -2, 0, 3, 3},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 1, -1, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, -1, 2},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, 0, 0, -1, 0, 0, -1, 0, 0, 1, 0, -1, 1, 0, -1, 2, -1, -1, 3},
            {0, 0, 0, 0, 0, -1, 0, 0, -1, 0, 0, -1, 1, -1, 1, -2, 0, 3, 3},
            {0, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 1, -1, 1, 3, 0, -2, 3},
            {0, 0, 0, 0, -1, 0, 0, -1, 0, 1, -1, 1, 0, 0, -1, 0, 0, 1, 3},
            {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {0, 0, 0, 0, -1, 0, 0, -1, 0, 1, 0, -1, 1, -1, 0, 0, 0, 1, 3},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0},
            {0, 0, 0, 0, 0, -1, 0, 0, -1, 1, 0, -1, 1, 0, -1, 0, -3, 3, 3},
            {0, 0, 0, 0, 0, -1, 0, 0, -1, 0, -1, 1, 0, -1, 1, -1, 0, 2, 3},
            {0, 0, 0, -1, 0, 0, -1, 0, 0, 1, -1, 1, -1, 0, 0, 3, 0, -2, 3},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, -1, 0},
            {0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, -1, 1, -1, 1, 0, 0, 1, 3},
            {0, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, -1, 1, -1, 1, 0, -1, 2, 3},
            {0, 0, 0, 0, -1, 0, 0, 0, -1, 1, 0, -1, 1, 0, -1, 0, -1, 1, 3},
            {0, 0, 0, 0, 0, -1, 0, -1, 0, 1, 0, -1, 1, 0, -1, 0, -1, 1, 3},
            {0, 0, 0, 0, -1, 0, 0, 0, -1, 0, -1, 1, 0, -1, 1, 0, 0, 1, 3},
            {0, 0, 0, 0, 0, -1, 0, -1, 0, 1, -1, 1, 0, 0, -1, 0, -1, 2, 3},
            {0, 0, 0, 0, 0, -1, 0, -1, 0, 0, -1, 1, 0, -1, 1, 0, 0, 1, 3},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, -1, 0},
            {0, 0, 0, 0, -1, 0, 0, 0, -1, 1, -1, 1, 0, 0, -1, 0, 0, 1, 3},
            {0, 0, 0, 0, -1, 0, 0, -1, 0, 0, 0, -1, 1, -1, 1, 0, 0, 1, 3},
            {0, 0, 0, 0, -1, 0, 0, -1, 0, 1, 0, -1, 1, 0, -1, 0, -1, 1, 3},
            {0, 0, 0, 2, 0, -1, 2, 0, -1, 2, 0, -1, 1, -1, 0, -3, 0, 2, 3},
            {0, 0, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 1, -1, 1, 2, -1, 0, 3}};
    double[] c3 = {0, -2, 0, 0, -1, -1, -3, 0, 0, 0, -5, 0, -9, -2, -3, 0, 0, -2, 0, -3, -3, -3, -3, 0, 0, -3, 0, 0, -3, -3, -3, 0, -3, -3, -3, -3, -3, -3, -3, 0, -3, -3, -3, -3, -3,};
    for (int round = 1; round < DB.length; round ++)
      for (int i = 0; i < 32; i ++)
        for (int q = 0; q < t3.length; q++) {
          model.addConstr(linExprOf(t3[q], DA[round][i][0][0], DA[round][i][0][1], DA[round][i][0][2], DA[round][i][1][0], DA[round][i][1][1], DA[round][i][1][2], DA[round][i][2][0], DA[round][i][2][1], DA[round][i][2][2], DA[round][i][3][0], DA[round][i][3][1], DA[round][i][3][2], DA[round][i][4][0], DA[round][i][4][1], DA[round][i][4][2], DB[round][i][2][0], DB[round][i][2][1], DB[round][i][2][2]), GRB.GREATER_EQUAL, c3[q], "");
        }



    double[][] t4 = {{-2, 4, 2, -3, 3, 3, -3, 3, 3, -1, 1, 1, -1, 1, 1, 8, -3, -10, 0},
            {0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -2, 1, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0},
            {0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, -4, -1, -1, 0},
            {0, 0, -1, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, -1, 1},
            {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 1, 0, -1, 0},
            {1, -1, 1, 0, 0, -1, 0, 0, -1, -1, 0, 0, 0, 0, -1, 1, -3, 3, 4},
            {0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, -1, 2},
            {0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 2},
            {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 1, 0, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, -1, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {3, -2, -1, 11, -2, -1, 11, -2, -1, 10, 0, -3, 10, 0, -3, -11, 2, 3, 12},
            {0, -1, 1, 0, -2, 0, 0, -2, 0, 0, -1, 1, 0, -1, 1, 0, 2, -1, 6},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {0, 0, -1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1, 0, 0},
            {1, -1, 1, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 1, 0, 4},
            {1, -1, 1, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 4, 0, -3, 4},
            {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {1, 0, -1, 0, -1, 0, 0, -1, 0, 1, -1, 0, 1, 0, -1, 0, -1, 1, 4},
            {1, -1, 1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, -1, 0, 0, -2, 3, 4},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, -1, 2},
            {1, -1, 1, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, -1, -1, 0, 2, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, -1, 0, 0, 0},
            {1, 0, -1, -1, 0, 0, -1, 0, 0, 1, 0, -1, 1, -1, 0, 2, -1, -1, 4},
            {0, -1, 1, 0, 0, -1, 0, 0, -1, 0, -1, 1, 0, -1, 1, 0, -1, 2, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0},
            {1, -1, 1, 0, -1, 0, 0, 0, -1, 0, -1, 0, 0, 0, -1, -1, 0, 2, 4},
            {1, 0, -1, 0, 0, -1, 0, 0, -1, 1, -1, 0, 1, -1, 0, 0, -2, 3, 4},
            {1, -1, 1, 0, 0, -1, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, 1, 4},
            {1, 0, -1, 0, 0, -1, 0, 0, -1, 1, 0, -1, 1, 0, -1, -3, -1, 4, 4},
            {1, -1, 1, 0, -1, 0, 0, 0, -1, 0, 0, -1, 0, -1, 0, 0, -1, 2, 4},
            {1, -1, 1, 0, 0, -1, 0, -1, 0, 0, 0, -1, 0, -1, 0, -1, 0, 2, 4},
            {1, 0, -1, 0, -1, 0, 0, -1, 0, 1, 0, -1, 1, -1, 0, 0, -1, 1, 4},
            {0, 0, -1, 0, -1, 0, 0, -1, 0, 1, -1, 1, 1, -1, 1, 0, 0, 1, 4},
            {1, 0, -1, -1, 0, 0, -1, 0, 0, 1, -1, 0, 1, 0, -1, 2, -1, -1, 4},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {-1, 0, 0, -1, 0, 0, -1, 0, 0, 1, -1, 1, 1, -1, 1, 3, 0, -2, 4},
            {1, 0, -1, 0, 0, -1, 0, -1, 0, 1, 0, -1, 1, 0, -1, 0, -3, 3, 4},
            {1, 0, -1, 0, -1, 0, 0, 0, -1, 1, 0, -1, 1, 0, -1, 0, -3, 3, 4},
            {1, -1, 1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, -3, 4, 4},
            {0, -1, 1, 0, -1, 0, 0, 0, -1, 0, -1, 1, 0, -1, 1, 0, 0, 1, 4},
            {0, -1, 1, 0, 0, -1, 0, -1, 0, 0, -1, 1, 0, -1, 1, 0, 0, 1, 4},
            {1, 0, -1, 0, 0, -1, 0, -1, 0, 1, -1, 0, 1, -1, 0, 0, 0, 1, 4},
            {0, 0, -1, 0, -1, 0, 0, 0, -1, 1, -1, 1, 1, -1, 1, 0, 0, 1, 4},
            {1, 0, -1, 0, 0, -1, 0, 0, -1, 1, -1, 0, 1, 0, -1, 0, -3, 3, 4},
            {1, 0, -1, 0, -1, 0, 0, 0, -1, 1, 0, -1, 1, -1, 0, -1, -1, 2, 4},
            {1, -1, 1, 0, 0, -1, 0, 0, -1, 0, -1, 0, 0, 0, -1, 0, -2, 3, 4},
            {1, -1, 1, 0, -1, 0, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, -2, 3, 4},
            {1, -1, 1, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, 1, 4},
            {1, -1, 1, 0, 0, -1, 0, -1, 0, 0, 0, -1, 0, 0, -1, 0, 0, 1, 4},
            {1, -1, 1, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, -1, 0, 0, 0, 1, 4},
            {1, 0, -2, -1, 0, 0, -1, 0, 0, 1, 0, -1, 1, 0, -1, 2, -2, -1, 5},
            {1, 0, -1, 0, -1, 0, 0, 0, -1, 1, -1, 0, 1, 0, -1, -1, -1, 2, 4},
            {-1, 0, 0, 0, 0, -1, 0, 0, -1, 1, -1, 1, 1, -1, 1, 1, 0, 0, 4},
            {1, 0, -1, 0, 0, -1, 0, 0, -1, 1, 0, -1, 1, -1, 0, -2, -1, 3, 4},
            {0, -1, 1, -2, 1, 1, -2, 1, 1, 0, -1, 1, -1, 0, 1, 5, -4, -1, 4},
            {1, 0, -1, 0, 0, -1, 0, -1, 0, 1, -1, 0, 1, 0, -1, 0, -1, 1, 4},
            {0, 0, -1, 0, 0, -1, 0, -1, 0, 1, -1, 1, 1, -1, 1, 0, 0, 1, 4},
            {1, 0, -1, 0, 0, -1, 0, -1, 0, 1, 0, -1, 1, -1, 0, 0, -1, 1, 4},
            {1, 0, -1, 0, -1, 0, 0, -1, 0, 1, 0, -1, 1, 0, -1, -1, -1, 2, 4},
            {2, 0, -1, 2, 0, -1, 2, 0, -1, 1, -1, 0, 1, -1, 0, -3, 0, 2, 4},};
    double[] c4 = {0, -2, 0, 0, -1, -1, 0, -4, 0, 0, -2, -2, 0, 0, 0, 0, -12, -6, 0, 0, -2, 0, -4, -4, 0, 0, -4, -4, -2, -4, 0, -4, -4, 0, -4, -4, -4, -4, -4, -4, -4, -4, -4, 0, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -5, -4, -4, -4, -4, -4, -4, -4, -4, -4};
    for (int round = 1; round < DB.length; round ++)
      for (int i = 0; i < 32; i ++)
        for (int q = 0; q < t4.length; q++) {
          model.addConstr(linExprOf(t4[q], DA[round][i][0][0], DA[round][i][0][1], DA[round][i][0][2], DA[round][i][1][0], DA[round][i][1][1], DA[round][i][1][2], DA[round][i][2][0], DA[round][i][2][1], DA[round][i][2][2], DA[round][i][3][0], DA[round][i][3][1], DA[round][i][3][2], DA[round][i][4][0], DA[round][i][4][1], DA[round][i][4][2], DB[round][i][3][0], DB[round][i][3][1], DB[round][i][3][2]), GRB.GREATER_EQUAL, c4[q], "");
        }


    double[][] t5 = {
            {-1, 1, 1, -2, 4, 2, 0, 0, 0, -3, 3, 3, -1, 1, 1, 5, -3, -7, 0},
            {0, 0, -1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, 1, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0},
            {-1, 1, 1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 1, 0, -1, 0},
            {0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, -3, -1, -1, 0},
            {0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0},
            {0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 1, 0, 0, 0, 1, 0, -1, 0},
            {3, -2, -1, 6, 0, -3, 0, 0, 0, 8, -2, -1, 3, -2, -1, -8, 2, 3, 9},
            {0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 2},
            {0, 0, -1, 1, -1, 1, 0, 0, 0, 0, 0, -1, 0, 0, -1, 0, -2, 3, 3},
            {0, 0, 0, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0},
            {0, -1, 1, 0, -1, 1, 0, 0, 0, 0, -2, 0, 0, -1, 1, 0, 2, -1, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0},
            {-1, 0, 0, 1, -1, 1, 0, 0, 0, -1, 0, 0, -1, 0, 0, 3, 0, -2, 3},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1, 0, 0},
            {-1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 2},
            {0, -1, 0, 1, -1, 1, 0, 0, 0, 0, -1, 0, 0, -1, 0, 0, 1, 0, 3},
            {1, -1, 0, 1, 0, -1, 0, 0, 0, -1, 0, 0, 1, 0, -1, 1, -1, 0, 3},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, -1, 0},
            {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {0, -1, 1, -1, 0, 1, 0, 0, 0, 0, 0, -1, 0, -1, 1, 1, -1, 1, 3},
            {0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, -1, 2},
            {1, 0, -1, 1, 0, -1, 0, 0, 0, -1, 0, 0, 1, -1, 0, 1, -1, 0, 3},
            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
            {0, -1, 0, 1, -1, 1, 0, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 1, 3},
            {0, 0, -1, 1, -1, 1, 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 1, 3},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, -1, 0, 0, 0},
            {1, -1, 1, 0, 0, -1, 0, 0, 0, 0, 0, -1, 1, -1, 1, 0, 0, 1, 3},
            {0, 0, -1, 1, -1, 1, 0, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 1, 3},
            {0, -1, 0, 1, -1, 1, 0, 0, 0, 0, 0, -1, 0, 0, -1, 0, 0, 1, 3},
            {1, 0, -1, 1, 0, -1, 0, 0, 0, 0, -1, 0, 1, 0, -1, 0, -2, 2, 3},
            {2, 0, -1, 1, 0, -1, 0, 0, 0, 3, 0, -1, 2, 0, -1, -4, 0, 2, 3},
            {-1, 1, 1, -1, 1, 1, 0, 0, 0, -1, 1, 1, -1, 1, 1, 4, -4, -1, 0},
            {1, -1, 1, 0, 0, -1, 0, 0, 0, 0, -1, 0, 1, -1, 1, 0, 0, 1, 3},
            {0, 0, -1, 1, -1, 1, 0, 0, 0, 0, -1, 0, 0, -1, 0, 0, 0, 1, 3},
            {0, -1, 0, 1, -1, 1, 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 1, 3},
            {1, 0, -1, 1, 0, -1, 0, 0, 0, 1, 0, -1, 1, -1, 0, -1, -1, 1, 3},
            {1, -1, 0, 1, 0, -1, 0, 0, 0, 2, 0, -1, 1, 0, -1, -2, 0, 1, 3},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
            {1, 0, -1, 1, 0, -2, 0, 0, 0, -1, 0, 0, 1, 0, -1, 1, -2, 0, 4},
            {1, -1, 1, -2, 1, 1, 0, 0, 0, -2, 1, 1, 1, -1, 1, 4, -1, -3, 3},
            {1, -1, 0, 1, 0, -1, 0, 0, 0, 0, -1, 0, 1, 0, -1, 0, -1, 1, 3},
            {1, 0, -1, 1, 0, -1, 0, 0, 0, 0, -1, 0, 1, -1, 0, 0, -1, 1, 3},
            {0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -1, 0, 2},
    };
    double[] c5 = {0, -2, 0, -1, -1, 0, 0, 0, 0, -2, 0, -9, -2, -3, 0, -4, 0, -3, 0, -2, -3, -3, 0, 0, 0, -3, -2, -3, 0, -3, -3, 0, -3, -3, -3, -3, -3, 0, -3, -3, -3, -3, -3, 0, -4, -3, -3, -3, -2};
    for (int round = 1; round < DB.length; round ++)
      for (int i = 0; i < 32; i ++)
        for (int q = 0; q < t5.length; q++) {
          model.addConstr(linExprOf(t5[q], DA[round][i][0][0], DA[round][i][0][1], DA[round][i][0][2], DA[round][i][1][0], DA[round][i][1][1], DA[round][i][1][2], DA[round][i][2][0], DA[round][i][2][1], DA[round][i][2][2], DA[round][i][3][0], DA[round][i][3][1], DA[round][i][3][2], DA[round][i][4][0], DA[round][i][4][1], DA[round][i][4][2], DB[round][i][4][0], DB[round][i][4][1], DB[round][i][4][2]), GRB.GREATER_EQUAL, c5[q], "");
        }
  }



  public void addthreexor_match(GRBVar[][] Output, GRBVar[][][] Input, GRBVar[][] DC) throws GRBException {
    GRBVar[] Input_Allone = new GRBVar[32]; // v1
    GRBVar[][] Input_Existone = new GRBVar[32][2]; // v0, v2
    GRBVar[] Input_Nowhite = new GRBVar[32]; // v3
    GRBVar[][] Input_White = new GRBVar[32][5];

    double[] t0 = {1.0, 1.0, -1.0};
    double[] t1 = {1.0, 1.0, -1.0};  // nolinear
    double[][] t2 = {{1.0, -1.0, 0, 1.0},
            {1.0, 0, -1.0, 1.0},
            {-1.0,  1.0, 1.0, -1.0}};  //linear
    double[] c = {0, 0, 1};

    for (int i = 0; i < 32; i ++){

      for (int j = 0; j < 5; j++) {
        Input_White[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Input_White"+ "_" + i + "_" + j);
        Determine_Allzero(Input_White[i][j], Input[i][j][0], Input[i][j][1], Input[i][j][2]);
      }


      Input_Allone[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Input_Allone_" + "_" + i);
      Determine_AllOne(Input_Allone[i], Input[i][0][1], Input[i][2][1], Input[i][4][1]);


      for (int l = 0; l < 2; l++) {
        Input_Existone[i][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Input_Existone"+ "_" + i + "_" + l);
      }

      Determine_ExistOne(Input_Existone[i][0], Input[i][0][0], Input[i][2][0], Input[i][4][0]);
      Determine_ExistOne(Input_Existone[i][1], Input[i][0][2], Input[i][2][2], Input[i][4][2]);


      Input_Nowhite[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Input_Nowhite"+ "_" + i);
      Determine_Allzero(Input_Nowhite[i], Input_White[i][0],Input_White[i][2],Input_White[i][4]);


      model.addConstr(Input_Nowhite[i], GRB.GREATER_EQUAL, Output[i][0], "");
      model.addConstr(Input_Existone[i][0], GRB.GREATER_EQUAL, Output[i][0], "");
      model.addConstr(linExprOf(t0, Input_Existone[i][0], Input_Nowhite[i], Output[i][0]), GRB.LESS_EQUAL, 1, "");

      model.addConstr(Input_Nowhite[i], GRB.GREATER_EQUAL, Output[i][1], "");
      model.addConstr(Input_Allone[i], GRB.LESS_EQUAL, Output[i][1], "");
      model.addConstr(linExprOf(t1, DC[i][0], Input_Allone[i], Output[i][1]), GRB.EQUAL, 0, "");

      for (int q = 0; q < 3; q ++) {
        model.addConstr(linExprOf(t2[q], DC[i][1], Input_Existone[i][1], Input_Nowhite[i], Output[i][2]), GRB.LESS_EQUAL, c[q], "");
      }

      model.addConstr(DC[i][1], GRB.GREATER_EQUAL, DC[i][0], "");
      double[] t3 = {1.0, 1.0, -1.0};
      model.addConstr(linExprOf(t3, Output[i][1],Output[i][2],Input_Nowhite[i]), GRB.GREATER_EQUAL, 0, "");


    }
  }


  public void addDoM(GRBVar[][][][] DA, GRBVar[] dom) throws GRBException {
    GRBVar[] Nowhite = new GRBVar[32];
    GRBVar[] A0A1 = new GRBVar[32];
    GRBVar[] A1A2 = new GRBVar[32];
    GRBVar[] A1A4 = new GRBVar[32];
    for (int i = 0; i < 32; i++)  {
      Nowhite[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Nowhite_"+i);
      A0A1[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "A0A1_"+i);
      A1A2[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "A1A2_"+i);
      A1A4[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "A1A4_"+i);
    }


    int r = DA.length-1;
    for (int k = 0; k < 32; k ++) {
      Determine_AllOne(Nowhite[k],DA[r][k][0][1],DA[r][k][1][1],DA[r][k][2][1],DA[r][k][3][1],DA[r][k][4][1]);
      Determine_Notmul(DA[r][k][0][0], DA[r][k][0][2], DA[r][k][1][0], DA[r][k][1][2], A0A1[k]);
      Determine_Notmul(DA[r][k][1][0], DA[r][k][1][2], DA[r][k][2][0], DA[r][k][2][2], A1A2[k]);
      Determine_Notmul(DA[r][k][1][0], DA[r][k][1][2], DA[r][k][4][0], DA[r][k][4][2], A1A4[k]);


      Determine_AllOne(dom[k],Nowhite[k],A0A1[k],A1A2[k],A1A4[k]);
    }


  }

  public void addDoM_new(GRBVar[][][][] DA, GRBVar[] dom,GRBVar[][] DC_match) throws GRBException {
    GRBVar[][] out_match = new GRBVar[32][3];
    for (int i = 0; i < 32; i++)
      for (int l = 0; l < 3; l ++){
        out_match[i][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "out_match_"+"_"+i+"_"+l);
      }

    addthreexor_match(out_match,DA[DA.length-1],DC_match);

    GRBVar[][] White = new GRBVar[32][5];
    for (int i = 0; i < 32; i ++) {
      for (int j = 0; j < 5; j++) {
        White[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "White" + "_" + i + "_" + j);
        Determine_Allzero(White[i][j], DA[DA.length-1][i][j][0], DA[DA.length-1][i][j][1], DA[DA.length-1][i][j][2]);
      }
    }
    GRBVar[] Nowhite = new GRBVar[32];
    GRBVar[] A1_out = new GRBVar[32];
    for (int i = 0; i < 32; i++)  {
      Nowhite[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Nowhite_"+i);
      A1_out[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "A0_out"+i);

    }

    GRBVar[][] NotMulti = new GRBVar[32][2];
    for (int i = 0; i < 32; i ++) {
      for (int j = 0; j < 2; j++) {
        NotMulti[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "NotMulti" + "_" + i + "_" + j);
      }
    }
    for (int i = 0; i < 32; i ++) {
      Determine_Existzero(NotMulti[i][0], DA[DA.length-1][i][1][0], out_match[i][2]);
      Determine_Existzero(NotMulti[i][1], DA[DA.length-1][i][1][2], out_match[i][0]);
    }

    int r = DA.length-1;
    for (int k = 0; k < 32; k ++) {
      Determine_Allzero(Nowhite[k],White[k][0],White[k][1],White[k][2],White[k][3],White[k][4]);
      Determine_AllOne(A1_out[k], NotMulti[k][0], NotMulti[k][1]);

      Determine_AllOne(dom[k],Nowhite[k],A1_out[k]);
    }
  }

  public void addDoM_y2(GRBVar[][][][] DA, GRBVar[] dom) throws GRBException {
    GRBVar[] Nowhite = new GRBVar[32];
    GRBVar[] A3A4 = new GRBVar[32];
    for (int i = 0; i < 32; i++)  {
      Nowhite[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Nowhite_"+i);
      A3A4[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "A3A4_"+i);
    }


    int r = DA.length-1;
    for (int k = 0; k < 32; k ++) {
      Determine_AllOne(Nowhite[k],DA[r][k][1][1],DA[r][k][2][1],DA[r][k][3][1],DA[r][k][4][1]);
      Determine_Notmul(DA[r][k][3][0], DA[r][k][3][2], DA[r][k][4][0], DA[r][k][4][2], A3A4[k]);

      Determine_AllOne(dom[k],Nowhite[k],A3A4[k]);
    }


  }

  public void addDoM_new_y4(GRBVar[][][][] DA, GRBVar[] dom,GRBVar[] DC_match) throws GRBException {
    GRBVar[][] out_match = new GRBVar[32][3];
    for (int i = 0; i < 32; i++)
      for (int l = 0; l < 3; l ++){
        out_match[i][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "out_match_"+"_"+i+"_"+l);
      }

    addtwoxor_match(out_match,DA[DA.length-1],DC_match);

    GRBVar[] Nowhite = new GRBVar[32];
    GRBVar[] A1_out = new GRBVar[32];
    for (int i = 0; i < 32; i++)  {
      Nowhite[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Nowhite_"+i);
      A1_out[i]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "A0_out"+i);

    }


    int r = DA.length-1;
    for (int k = 0; k < 32; k ++) {
      Determine_AllOne(Nowhite[k],DA[r][k][0][1],DA[r][k][1][1],DA[r][k][3][1],DA[r][k][4][1]);
      Determine_Notmul(DA[r][k][1][0], DA[r][k][1][2], out_match[k][0],out_match[k][2], A1_out[k]);


      Determine_AllOne(dom[k],Nowhite[k],A1_out[k]);
    }
  }

  public void addtwoxor_match(GRBVar[][] Output, GRBVar[][][] Input, GRBVar[] DC) throws GRBException {
    GRBVar[][] DB_Allone = new GRBVar[32][3];

    double[] t1 = {1.0, 1.0, -1.0};


    for (int i = 0; i < 32; i ++) {
      for (int l = 0; l < 3; l ++) {
        DB_Allone[i][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_Allone_m4_"+"_"+i+"_"+l);
        Determine_AllOne(DB_Allone[i][l], Input[i][0][l], Input[i][4][l]);

      }

      model.addConstr(DB_Allone[i][1], GRB.EQUAL, Output[i][1], "");
      model.addConstr(DB_Allone[i][1], GRB.GREATER_EQUAL, Output[i][0], "");
      model.addConstr(DB_Allone[i][0], GRB.LESS_EQUAL, Output[i][0], "");
      model.addConstr(linExprOf(t1, DC[i], DB_Allone[i][0], Output[i][0]), GRB.EQUAL, 0, "");
      model.addConstr(DB_Allone[i][2], GRB.EQUAL, Output[i][2], "");

    }
  }


  public void Determine_Allzero(GRBVar mainVar, GRBVar ... vars) throws GRBException {
    GRBLinExpr expr = new GRBLinExpr();
    expr.addTerm(1.0, mainVar);
    for (int i = 0; i < vars.length; i++) {
      expr.addTerm(1.0, vars[i]);
      model.addConstr(linExprOf(mainVar,vars[i]), GRB.LESS_EQUAL, 1, "");
    }
    model.addConstr(expr, GRB.GREATER_EQUAL, 1, "");
  }

  public void Determine_twointhree(GRBVar mainVar, GRBVar ... vars) throws GRBException {
    GRBLinExpr expr = new GRBLinExpr();
    expr.addTerm(2.0, mainVar);
    for (int i = 0; i < vars.length; i++) {
      expr.addTerm(1.0, vars[i]);
    }
    model.addConstr(expr, GRB.GREATER_EQUAL, 2, "");
    model.addConstr(expr, GRB.LESS_EQUAL, 3, "");
  }


  public void Determine_AllOne(GRBVar mainVar, GRBVar ... vars) throws GRBException {
    GRBLinExpr exprm = new GRBLinExpr();
    exprm.addTerm(1.0, mainVar);
    GRBLinExpr exprp = new GRBLinExpr();
    exprp.addTerm(-1.0*vars.length, mainVar);
    for (int i = 0; i < vars.length; i++) {
      exprm.addTerm(-1.0, vars[i]);
      exprp.addTerm(1.0, vars[i]);
    }
    model.addConstr(exprm, GRB.GREATER_EQUAL, 1-vars.length, "");
    model.addConstr(exprp, GRB.GREATER_EQUAL, 0, "");
  }

  public void Determine_ExistOne(GRBVar mainVar, GRBVar ... vars) throws GRBException {
    GRBLinExpr expr1 = new GRBLinExpr();
    expr1.addTerm(1.0, mainVar);
    GRBLinExpr exprm = new GRBLinExpr();
    exprm.addTerm(vars.length, mainVar);
    for (int i = 0; i < vars.length; i++) {
      expr1.addTerm(-1.0, vars[i]);
      exprm.addTerm(-1.0, vars[i]);
    }
    model.addConstr(exprm, GRB.GREATER_EQUAL, 0, "");
    model.addConstr(expr1, GRB.LESS_EQUAL, 0, "");
  }

  public void Determine_Existzero(GRBVar mainVar, GRBVar ... vars) throws GRBException {
    GRBLinExpr expr = new GRBLinExpr();
    expr.addTerm(1.0, mainVar);
    for (int i = 0; i < vars.length; i++) {
      expr.addTerm(1.0, vars[i]);
      model.addConstr(linExprOf(mainVar,vars[i]), GRB.GREATER_EQUAL, 1, "");
    }
    model.addConstr(expr, GRB.LESS_EQUAL, vars.length, "");
  }

  public void Determine_Notmul(GRBVar Var10, GRBVar Var12, GRBVar Var20, GRBVar Var22, GRBVar mainVar) throws GRBException {
    double[][] t = {{0, -1, 0, -1, 1, 1},
            {0, 1, 1, 0, -1, 0},
            {-1, 0, -1, 0, 1, 1},
            {1, 0, 0, 1, -1, 0},
            {-1, -1, 0, 0, 1, 1},
            {0, 0, -1, -1, 1, 1}};
    double[] c = {-1, 0, -1, 0, -1, -1};
    for (int i = 0; i < 6; i++) {
      model.addConstr(linExprOf(t[i], Var10, Var12, Var20, Var22, mainVar), GRB.GREATER_EQUAL, c[i], "");
    }
  }


  public GRBLinExpr linExprOf(double[] coeffs, GRBVar ... vars) throws GRBException {
    GRBLinExpr ofVars = new GRBLinExpr();
    ofVars.addTerms(coeffs, vars);
    return ofVars;
  }

  public GRBLinExpr linExprOf(double constant, GRBVar ... vars) {
    GRBLinExpr ofVars = linExprOf(vars);
    ofVars.addConstant(constant);
    return ofVars;
  }

  public GRBLinExpr linExprOf(GRBVar ... vars) {
    GRBLinExpr expr = new GRBLinExpr();
    for (GRBVar var : vars)
      expr.addTerm(1.0, var);
    return expr;
  }

}
