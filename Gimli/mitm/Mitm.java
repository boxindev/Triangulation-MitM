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
  private final GRBVar[][][][][] DA;
  private final GRBVar[][][][][] DP;
  private final GRBVar[][][][] DC1;
  private final GRBVar[][][][][] DB;
  private final GRBLinExpr   objective;
  private final GRBVar[] obj;
  private final GRBVar[][] dom;


  /**
   * @param env the Gurobi environment
   */
  public Mitm(final GRBEnv env, final int Rounds) throws GRBException {
    model = new GRBModel(env);
    this.Rounds = Rounds;
    //this.MatchRound = MatchRound;

    factory = new MitmFactory(model);
    DA = new GRBVar[Rounds+1][3][4][32][3];
    DP = new GRBVar[Rounds][3][4][32][3];
    DC1 = new GRBVar[Rounds][3][4][32];
    DB = new GRBVar[Rounds][3][4][32][3];
    // Initialization
    for (int round = 0; round < Rounds+1; round++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 4; j++)
	  for (int k = 0; k < 32; k++) 
            for (int l = 0; l < 3; l++) {     
              DA[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_"+round+"_"+i+"_"+j+"_"+k+"_"+l);            
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 4; j++)
	  for (int k = 0; k < 32; k++)           
            for (int l = 0; l < 3; l++) {     
              DB[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_"+round+"_"+i+"_"+j+"_"+k+"_"+l);    	          
    }


    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 32; k++)
            for (int l = 0; l < 3; l++) {
              DP[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP_"+round+"_"+i+"_"+j+"_"+k+"_"+l);
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 32; k++) {
              DC1[round][i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC1_"+round+"_"+i+"_"+j+"_"+k);
    }

    dom = new GRBVar[4][32];
    for (int i = 0; i < 4; i++) 
      for (int k = 0; k < 32; k++){
        dom[i][k]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "dom_"+i+"_"+k);
    }

    //fixed input and output
    //articleTrail();
    // todo
    factory.addfixed_in(DA);

    // Constraints

    factory.addnonlinear(DA, DP);
    factory.addthreexor(DA, DP, DB, DC1);
    factory.addswap(DB, DA);

    // todo
    factory.addDoM(DA, dom);
   
    objective = new GRBLinExpr();  
    GRBLinExpr DoF_red = new GRBLinExpr();
    GRBLinExpr DoF_blue = new GRBLinExpr();
    GRBLinExpr DoM = new GRBLinExpr();
    
    GRBVar Obj = model.addVar(0.0, 256.0, 0.0, GRB.INTEGER, "Obj");
    obj = new GRBVar[3];
    obj[0] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj1"); 
    obj[1] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj2"); 
    obj[2] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj3");
                  
      
    for (int j = 0; j < 4; j++)
        for (int k = 0; k < 32; k++) {
          DoF_red.addTerm(-1.0, DA[0][0][j][k][0]);
          DoF_red.addConstant(1.0);
          DoF_blue.addTerm(-1.0, DA[0][0][j][k][2]);
          DoF_blue.addConstant(1.0);
    }
    for (int round = 0; round < Rounds; round ++) 
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 32; k++) {
                    DoF_red.addTerm(-1.0, DC1[round][i][j][k]);
    }


    for (int i = 0; i < 4; i++) 
        for (int j = 0; j < 32; j++) {
            DoM.addTerm(1.0, dom[i][j]);
    }


    
    objective.addTerm(1.0, Obj);

    model.addConstr(DoF_red, GRB.GREATER_EQUAL, 1, "");
    model.addConstr(DoF_blue, GRB.GREATER_EQUAL, 1, "");
    //model.addConstr(DoM, GRB.GREATER_EQUAL, 1, "");

    model.addConstr(DoF_red, GRB.EQUAL, obj[0], "");
    model.addConstr(DoF_blue, GRB.EQUAL, obj[1], "");
    model.addConstr(DoM, GRB.EQUAL, obj[2], ""); 

    model.addConstr(objective, GRB.LESS_EQUAL, DoF_blue, "");
    model.addConstr(objective, GRB.LESS_EQUAL, DoF_red, "");
    model.addConstr(objective, GRB.LESS_EQUAL, DoM, "");
    //model.addConstr(objective, GRB.LESS_EQUAL, 20, "");
    model.setObjective(objective, GRB.MAXIMIZE);
  }

  public List<MitmSolution> solve(final int nSolutions, final boolean nonOptimalSolutions, final int minObjValue, final int Threads) throws GRBException {
    model.read("tune1.prm");
    model.write("model.lp");
    model.set(GRB.IntParam.Threads, Threads);
    if (minObjValue != -1)
      model.addConstr(objective, GRB.EQUAL, minObjValue, "objectiveFix");
    model.set(GRB.DoubleParam.PoolGap, (nonOptimalSolutions) ? 1.0 : 0.005);
    //model.set(GRB.IntParam.PoolSolutions, nbSolutions);
    //model.set(GRB.IntParam.PoolSearchMode, 2);
    //model.set(GRB.IntParam.DualReductions, 0);

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
      int[][][][][] DAValue     = new int[Rounds+1][3][4][32][3];
      int[][][][][] DPValue  = new int[Rounds][3][4][32][3];
      int[][][][] DC1Value = new int[Rounds][3][4][32];
      int[][][][][] DBValue  = new int[Rounds][3][4][32][3];
      int[][] domValue  = new int[4][32];
      int[] objValue  = new int[3];

      for (int round = 0; round < Rounds+1; round++)
        for (int i = 0; i < 3; i++)
          for (int j = 0; j < 4; j++)
            for (int k = 0; k < 32; k++) {
              for (int l = 0; l < 3; l++)  {
                DAValue[round][i][j][k][l]  = (int) Math.round(DA[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
              }
            }

      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 3; i++)
          for (int j = 0; j < 4; j++)
	    for (int k = 0; k < 32; k++) {
          DC1Value[round][i][j][k] = (int) Math.round(DC1[round][i][j][k].get(GRB.DoubleAttr.Xn));
              for (int l = 0; l < 3; l++)  {
                DPValue[round][i][j][k][l]  = (int) Math.round(DP[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                DBValue[round][i][j][k][l]  = (int) Math.round(DB[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
              }
                
      }
      for (int i = 0; i < 4; i++) 
	    for (int k = 0; k < 32; k++)
          domValue[i][k]  = (int) Math.round(dom[i][k].get(GRB.DoubleAttr.Xn));

      for (int i = 0; i < 3; i++) 
        objValue[i]  = (int) Math.round(obj[i].get(GRB.DoubleAttr.Xn));

      return new MitmSolution(Rounds, (int) Math.round(model.get(GRB.DoubleAttr.PoolObjVal)), DAValue,DBValue, DPValue, DC1Value, domValue, objValue);
    } catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
      return null; // Can't access
    }
  }

  private void articleTrail() throws GRBException{
    int[] bluei = {2,3,2,3,2,3,2,3};
    int[] bluej = {2,2,2,2,2,2,2,2};
    int[] bluek = {4,4,13,13,22,22,27,27};
    //Blue
    //for (int i = 0; i < bluei.length; i ++) {
      //model.addConstr(DA[0][bluei[i]][bluej[i]][bluek[i]][0], GRB.EQUAL, 1, ""); 
      //model.addConstr(DA[0][bluei[i]][bluej[i]][bluek[i]][2], GRB.EQUAL, 0, "");
    //}
    
    for (int i = 0; i < 4; i++) 
	for (int k = 0; k < 32; k++){

 		if ((i==2 & k==4) | (i==3 & k==4) | (i==2 & k==13) | (i==3 & k==13) | (i==2 & k==22) | (i==3 & k==22) | (i==2 & k==27) | (i==3 & k==27)){
		    model.addConstr(DA[0][i][2][k][0], GRB.EQUAL, 1, ""); 
      		    model.addConstr(DA[0][i][2][k][2], GRB.EQUAL, 0, "");			
		}
		else if ((i==2 & k==31) | (i==3 & k==31)){
		    model.addConstr(DA[0][i][2][k][0], GRB.EQUAL, 0, "");
      		    model.addConstr(DA[0][i][2][k][2], GRB.EQUAL, 0, "");
		}
		else{
		    model.addConstr(DA[0][i][2][k][0], GRB.EQUAL, 0, ""); 
      		    model.addConstr(DA[0][i][2][k][2], GRB.EQUAL, 1, "");	
		}
            
    }

    for (int i = 0; i < 4; i++)
      for (int k = 0; k < 32; k++){

        if ((i==1 & k==8) | (i==1 & k==10) | (i==2 & k==10) | (i==1 & k==17) | (i==1 & k==26) | (i==1 & k==31) | (i==2 & k==31)){
          model.addConstr(dom[i][k], GRB.EQUAL, 1, "");
        }
        //else{
          //model.addConstr(dom[i][k], GRB.EQUAL, 0, "");
        //}

      }

  }
}




