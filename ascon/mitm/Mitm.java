package mitmsearch.mitm;

import gurobi.*;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
public class Mitm {
  private final int Rounds;
  private final GRBModel model;
  private FileWriter logfile ;
  private final MitmFactory factory;
  private final GRBVar[][] Cond;
  private final GRBVar[][][][] DA;
  private final GRBVar[][][][] DB;
  private final GRBVar[][][][] DC;
  private final GRBLinExpr   objective;
  private final GRBVar[] obj;
  private final GRBVar[] dom;

  /**
   * @param env the Gurobi environment
   */
  public Mitm(final GRBEnv env, final int Rounds) throws GRBException {
    model = new GRBModel(env);
    this.Rounds = Rounds;

    factory = new MitmFactory(model);
    Cond = new GRBVar[32][2];
    DA = new GRBVar[Rounds+1][32][5][3];
    DB = new GRBVar[Rounds][32][5][3];
    DC = new GRBVar[Rounds][32][5][2];
    // Initialization

    for (int k = 0; k < 32; k++)
      for (int l = 0; l < 2; l++) {
        Cond[k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Cond_"+k+"_"+l);
      }

    for (int round = 0; round < Rounds+1; round++)
      for (int i = 0; i < 32; i++)
        for (int j = 0; j < 5; j++)
          for (int l = 0; l < 3; l++) {
            DA[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_"+round+"_"+i+"_"+j+"_"+l);
          }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 32; i++)
        for (int j = 0; j < 5; j++)
          for (int l = 0; l < 3; l++) {
            DB[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_"+round+"_"+i+"_"+j+"_"+l);
          }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 32; i++)
        for (int j = 0; j < 5; j++)
          for (int l = 0; l < 2; l++){
            DC[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC_"+round+"_"+i+"_"+j+"_"+l);

          }


    dom = new GRBVar[32];
    for (int i = 0; i < 32; i++) {
      dom[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "dom_"+i);
    }

    GRBVar[][] DC_match = new GRBVar[32][2];
    for (int i = 0; i < 32; i++)
      for (int l = 0; l < 2; l++){
        DC_match[i][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC_match_"+"_"+i+"_"+l);
      }

    //fixed input
    ArticleTrail_3r();
    factory.addfixed_in_new(DA);

    // Constraints
    factory.addSbox_cond_new(DA, DB ,Cond);
    factory.addxor_new(DA, DB, DC);
    factory.addSbox_new(DA, DB);



    //factory.addDoM_y2(DA, dom);
    factory.addDoM_new(DA, dom,DC_match);



    objective = new GRBLinExpr();
    GRBLinExpr DoF_red = new GRBLinExpr();
    GRBLinExpr DoF_blue = new GRBLinExpr();
    GRBLinExpr DoM = new GRBLinExpr();

    GRBLinExpr DoF_red_linear = new GRBLinExpr();
    GRBLinExpr Cond_count = new GRBLinExpr();

    GRBVar Obj = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj");

    obj = new GRBVar[3];
    obj[0] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj1");
    obj[1] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj2");
    obj[2] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj3");


    for (int k = 0; k < 32; k++) {
      DoF_red.addTerm(1.0, DA[0][k][0][2]);
      DoF_blue.addTerm(1.0, DA[0][k][0][0]);
    }


    for (int round = 0; round < Rounds; round ++)
      for (int i = 0; i < 32; i++)
        for (int k = 0; k < 5; k++) {
          DoF_red.addTerm(-1.0, DC[round][i][k][1]);
        }

    for (int i = 0; i < 32; i++) {
      DoF_red.addTerm(-1.0, DC_match[i][1]);
    }

    for (int round = 0; round < Rounds; round ++)
      for (int i = 0; i < 32; i++)
        for (int k = 0; k < 5; k++) {
          DoF_red_linear.addTerm(1.0, DC[round][i][k][1]);
          DoF_red_linear.addTerm(-1.0, DC[round][i][k][0]);
        }
    for (int i = 0; i < 32; i++) {
      DoF_red_linear.addTerm(1.0, DC_match[i][1]);
      DoF_red_linear.addTerm(-1.0, DC_match[i][0]);
    }

    for (int i = 0; i < 32; i++) {
      Cond_count.addTerm(1.0, DA[0][i][0][0]);
      Cond_count.addTerm(-1.0, DB[0][i][0][0]);

      Cond_count.addTerm(1.0, DA[0][i][0][2]);
      Cond_count.addTerm(-1.0, DB[0][i][0][2]);

      Cond_count.addTerm(1.0, DA[0][i][0][0]);
      Cond_count.addTerm(-1.0, DB[0][i][1][0]);

      Cond_count.addTerm(1.0, DA[0][i][0][2]);
      Cond_count.addTerm(-1.0, DB[0][i][1][2]);

      Cond_count.addTerm(1.0, DA[0][i][0][0]);
      Cond_count.addTerm(-1.0, DB[0][i][3][0]);

      Cond_count.addTerm(1.0, DA[0][i][0][2]);
      Cond_count.addTerm(-1.0, DB[0][i][3][2]);

      Cond_count.addTerm(1.0, DA[0][i][0][0]);
      Cond_count.addTerm(-1.0, DB[0][i][4][0]);

      Cond_count.addTerm(1.0, DA[0][i][0][2]);
      Cond_count.addTerm(-1.0, DB[0][i][4][2]);
    }
    model.addConstr(Cond_count, GRB.LESS_EQUAL, 25, "");





    for (int i = 0; i < 32; i++) {
      DoM.addTerm(1.0, dom[i]);
    }



    objective.addTerm(1.0, Obj);

    model.addConstr(DoF_red, GRB.GREATER_EQUAL, 2, "");
    model.addConstr(DoF_blue, GRB.GREATER_EQUAL, 2, "");
    model.addConstr(DoM, GRB.GREATER_EQUAL, 2, "");

    model.addConstr(DoF_red, GRB.EQUAL, obj[0], "");
    model.addConstr(DoF_blue, GRB.EQUAL, obj[1], "");
    model.addConstr(DoM, GRB.EQUAL, obj[2], "");

    model.addConstr(objective, GRB.LESS_EQUAL, DoF_blue, "");
    model.addConstr(objective, GRB.LESS_EQUAL, DoF_red, "");
    model.addConstr(objective, GRB.LESS_EQUAL, DoM, "");
    //model.addConstr(objective, GRB.LESS_EQUAL, 20, "");
    //model.setObjective(objective, GRB.MAXIMIZE);
    model.setObjective(DoF_red_linear, GRB.MAXIMIZE);
  }

  public List<MitmSolution> solve(final int nSolutions, final boolean nonOptimalSolutions, final int minObjValue, final int Threads) throws GRBException {
    model.read("tune1.prm");
    model.write("model.lp");
    model.set(GRB.IntParam.Threads, Threads);
    if (minObjValue != -1)
      model.addConstr(objective, GRB.EQUAL, minObjValue, "objectiveFix");
    model.set(GRB.IntParam.DualReductions, 0);

    try {
      FileWriter logfile = null;
      logfile = new FileWriter("cb.log");
    }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }

    GRBVar[] vars = model.getVars();
    System.out.println (vars.length);
    System.out.println (vars[0].get(GRB.StringAttr.VarName));
    Callback cb = new Callback(vars,logfile);
    model.setCallback(cb);

    model.optimize();
    model.write("output.sol");
    //model.computeIIS();
    //model.write("model1.ilp");
    return getAllFoundSolutions();
  }

  public void dispose() throws GRBException {
    model.dispose();
  }

  public List<MitmSolution> getAllFoundSolutions() throws GRBException {
    return IntStream.range(0, model.get(GRB.IntAttr.SolCount)).boxed()
            .map(solNb -> getSolution(solNb))
            .collect(Collectors.toList());
  }

  private MitmSolution getSolution(final int solutionNumber) {
    try {
      model.set(GRB.IntParam.SolutionNumber, solutionNumber);
      int[][] CondValue     = new int[32][2];
      int[][][][] DAValue     = new int[Rounds+1][32][5][3];
      int[][][][] DBValue  = new int[Rounds][32][5][3];
      int[][][][] DCValue = new int[Rounds][32][5][2];
      int[] domValue  = new int[32];
      int[] objValue  = new int[3];

      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 32; i++)
          for (int j = 0; j < 5; j++) {
            for (int l = 0; l < 2; l++)
              DCValue[round][i][j][l]  = (int) Math.round(DC[round][i][j][l].get(GRB.DoubleAttr.Xn));
            for (int l = 0; l < 3; l++)  {
              DAValue[round][i][j][l]  = (int) Math.round(DA[round][i][j][l].get(GRB.DoubleAttr.Xn));
              DBValue[round][i][j][l]  = (int) Math.round(DB[round][i][j][l].get(GRB.DoubleAttr.Xn));
            }
          }
      for (int i = 0; i < 32; i++)
        for (int j = 0; j < 5; j++)
          for (int l = 0; l < 3; l++)  {
            DAValue[Rounds][i][j][l]  = (int) Math.round(DA[Rounds][i][j][l].get(GRB.DoubleAttr.Xn));
          }

      for (int j = 0; j < 32; j++)
        for (int k = 0; k < 2; k++)  {
          CondValue[j][k]  = (int) Math.round(Cond[j][k].get(GRB.DoubleAttr.Xn));
        }

      for (int i = 0; i < 32; i++)
        domValue[i]  = (int) Math.round(dom[i].get(GRB.DoubleAttr.Xn));

      for (int i = 0; i < 3; i++)
        objValue[i]  = (int) Math.round(obj[i].get(GRB.DoubleAttr.Xn));

      return new MitmSolution(Rounds, (int) Math.round(model.get(GRB.DoubleAttr.PoolObjVal)), CondValue, DAValue, DBValue, DCValue, domValue, objValue);
    } catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
      return null; // Can't access
    }
  }


  private void ArticleTrail_3r() throws GRBException {
    int[] red = {0,5,6,7,15,18,22,25,26,27,28,29}; //12
    int[] blue = {1,4,11,13,14,17,23,}; //7
    int[] gray = {2,3,8,9,10,12,16,19,20,21,24,30,31}; //13

    for (int k = 0; k < 32; k++) {
      for (int i : red) {
        if (k == i) {
          model.addConstr(DA[0][k][0][0], GRB.EQUAL, 0, "");
          model.addConstr(DA[0][k][0][1], GRB.EQUAL, 1, "");
          model.addConstr(DA[0][k][0][2], GRB.EQUAL, 1, "");
        }
      }
      for (int i : gray) {
        if (k == i) {
          model.addConstr(DA[0][k][0][0], GRB.EQUAL, 0, "");
          model.addConstr(DA[0][k][0][1], GRB.EQUAL, 1, "");
          model.addConstr(DA[0][k][0][2], GRB.EQUAL, 0, "");
        }
      }
      for (int i : blue) {
        if (k == i) {
          model.addConstr(DA[0][k][0][0], GRB.EQUAL, 1, "");
          model.addConstr(DA[0][k][0][1], GRB.EQUAL, 1, "");
          model.addConstr(DA[0][k][0][2], GRB.EQUAL, 0, "");
        }
      }
    }
/*
    int[] Dom = {2,3,6,12,16,17,26};
    for (int k = 0; k < 32; k++) {
      if (k == 2 | k == 3 | k == 6 | k == 12 | k == 16 | k == 17 | k == 26)
        model.addConstr(dom[k], GRB.EQUAL, 1, "");
      else
        model.addConstr(dom[k], GRB.EQUAL, 0, "");
    }

 */
  }

  private void ArticleTrail_4r() throws GRBException {
    int[] red = {0,2,4,5,6,7,9,12,14,16,19,22,24,25,26,28,29}; //17
    int[] blue = {10,20}; //2
    int[] gray = {1,3,8,11,13,15,17,18,21,23,27,30,31}; //13

    for (int k = 0; k < 32; k++) {
      for (int i : red) {
        if (k == i) {
          model.addConstr(DA[0][k][0][0], GRB.EQUAL, 0, "");
          model.addConstr(DA[0][k][0][1], GRB.EQUAL, 1, "");
          model.addConstr(DA[0][k][0][2], GRB.EQUAL, 1, "");
        }
      }
      for (int i : gray) {
        if (k == i) {
          model.addConstr(DA[0][k][0][0], GRB.EQUAL, 0, "");
          model.addConstr(DA[0][k][0][1], GRB.EQUAL, 1, "");
          model.addConstr(DA[0][k][0][2], GRB.EQUAL, 0, "");
        }
      }
      for (int i : blue) {
        if (k == i) {
          model.addConstr(DA[0][k][0][0], GRB.EQUAL, 1, "");
          model.addConstr(DA[0][k][0][1], GRB.EQUAL, 1, "");
          model.addConstr(DA[0][k][0][2], GRB.EQUAL, 0, "");
        }
      }
    }
/*
    int[] Dom = {18,28};
    for (int k = 0; k < 32; k++) {
      if (k == 18 | k == 28)
        model.addConstr(dom[k], GRB.EQUAL, 1, "");
      else
        model.addConstr(dom[k], GRB.EQUAL, 0, "");
    }

 */

  }

}
