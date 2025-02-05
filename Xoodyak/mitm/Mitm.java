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
  private final GRBVar[][][][] DP;
  private final GRBVar[][][][] DC1;
  private final GRBVar[][][][] DP2;
  private final GRBVar[][][][] DC12;
  private final GRBVar[][][][][] DB; 
  private final GRBVar[][][][][] DC2;
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
    DA = new GRBVar[Rounds][4][3][32][3];
    DP = new GRBVar[Rounds][4][32][3];
    DP2 = new GRBVar[Rounds][4][32][3];
    DC1 = new GRBVar[Rounds][4][32][2];
    DC12 = new GRBVar[Rounds][4][32][2];
    DB = new GRBVar[Rounds][4][3][32][3];
    DC2 = new GRBVar[Rounds][4][3][32][2];
    // Initialization
    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 4; i++) 
        for (int j = 0; j < 3; j++) 
	  for (int k = 0; k < 32; k++) 
            for (int l = 0; l < 3; l++) {     
              DA[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_"+round+"_"+i+"_"+j+"_"+k+"_"+l);            
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 4; i++) 
        for (int j = 0; j < 3; j++) 
	  for (int k = 0; k < 32; k++)           
            for (int l = 0; l < 3; l++) {     
              DB[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_"+round+"_"+i+"_"+j+"_"+k+"_"+l);    	          
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 4; i++) 
        for (int j = 0; j < 3; j++) 
	  for (int k = 0; k < 32; k++)
        for (int l = 0; l < 2; l++){
            DC2[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC2_"+round+"_"+i+"_"+j+"_"+k+"_"+l);
    	             
    }
    

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 4; i++) 
	for (int k = 0; k < 32; k++) 
	  for (int l = 0; l < 3; l++)  {         
            DP[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP_"+round+"_"+i+"_"+k+"_"+l);               
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 4; i++) 
	for (int k = 0; k < 32; k++) 
	  for (int l = 0; l < 3; l++)  {
	    DP2[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP2_"+round+"_"+i+"_"+k+"_"+l);              
    }
    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 4; i++) 
	for (int k = 0; k < 32; k++)
      for (int l = 0; l < 2; l++){
          DC1[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC1_"+round+"_"+i+"_"+k+"_"+l);
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 4; i++) 
	for (int k = 0; k < 32; k++)
      for (int l = 0; l < 2; l++){
	  DC12[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC12_"+round+"_"+i+"_"+k+"_"+l);
    }

    dom = new GRBVar[4][32];
    for (int i = 0; i < 4; i++) 
      for (int k = 0; k < 32; k++){
        dom[i][k]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "dom_"+i+"_"+k);
    }

    //fixed input and output
    articleTrail_r68();
    factory.addfixed_in(DA);

    // Constraints

    factory.addthreexor_new(DA, DP, DC1);
    factory.addtwoxor_new(DP2, DP, DC12);
    factory.addTheta_new(DA, DP2, DB, DC2);
    // todo
    factory.addnonlinearcond_new(DB, DA);
    // todo
    factory.addnonlinear_new(DB, DA);
    // todo
    factory.addDoM_new(DB, dom);

    /*
    GRBVar[][] beta = new GRBVar[4][32];
    for (int i = 0; i < 4; i++) 
      for (int j = 0; j < 32; j++) {
        beta[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "beta_"+i+"_"+j); 
    }
    factory.betaConstraints(DA, beta);

     */
   
    objective = new GRBLinExpr();  
    GRBLinExpr DoF_red = new GRBLinExpr();
    GRBLinExpr DoF_blue = new GRBLinExpr();
    GRBLinExpr DoM = new GRBLinExpr();
    
    GRBVar Obj = model.addVar(0.0, 256.0, 0.0, GRB.INTEGER, "Obj");
    obj = new GRBVar[3];
    obj[0] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj1"); 
    obj[1] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj2"); 
    obj[2] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj3");
                  
      
    for (int i = 0; i < 4; i++) 
        for (int j = 0; j < 32; j++) {
        DoF_red.addTerm(1.0, DA[0][i][2][j][2]);
        DoF_blue.addTerm(1.0, DA[0][i][2][j][0]);
    }
    for (int round = 0; round < Rounds; round ++) 
        for (int i = 0; i < 4; i++) 
            for (int j = 0; j < 32; j++) {
                DoF_red.addTerm(-1.0, DC1[round][i][j][1]);
                DoF_red.addTerm(-1.0, DC12[round][i][j][1]);
                for (int k = 0; k < 3; k++) {
                    DoF_red.addTerm(-1.0, DC2[round][i][k][j][1]);
                }
    }


    for (int i = 0; i < 4; i++) 
        for (int j = 0; j < 32; j++) {
            DoM.addTerm(1.0, dom[i][j]);
    }


    GRBLinExpr Linear_Red = new GRBLinExpr();

    for (int round = 0; round < Rounds; round ++)
      for (int i = 0; i < 4; i++)
        for (int k = 0; k < 32; k++) {
          Linear_Red.addTerm(-1.0, DC1[round][i][k][0]);
          Linear_Red.addTerm(1.0, DC1[round][i][k][1]);

          Linear_Red.addTerm(-1.0, DC12[round][i][k][0]);
          Linear_Red.addTerm(1.0, DC12[round][i][k][1]);
        }


    for (int round = 0; round < Rounds; round ++)
      for (int i = 0; i < 4; i++)
        for (int j = 0; j < 3; j ++) {
          for (int k = 0; k < 32; k++) {
            Linear_Red.addTerm(-1.0, DC2[round][i][j][k][0]);
            Linear_Red.addTerm(1.0, DC2[round][i][j][k][1]);
          }
        }
    model.addConstr(Linear_Red, GRB.GREATER_EQUAL, 8.0, "");


    
    objective.addTerm(1.0, Obj);

    //model.addConstr(DoF_red, GRB.GREATER_EQUAL, 1, "");
    //model.addConstr(DoF_blue, GRB.EQUAL, 7, "");
    //model.addConstr(DoM, GRB.GREATER_EQUAL, 1, "");

    model.addConstr(DoF_red, GRB.EQUAL, obj[0], "");
    model.addConstr(DoF_blue, GRB.EQUAL, obj[1], "");
    model.addConstr(DoM, GRB.EQUAL, obj[2], ""); 

    model.addConstr(objective, GRB.LESS_EQUAL, DoF_blue, "");
    model.addConstr(objective, GRB.LESS_EQUAL, DoF_red, "");
    model.addConstr(objective, GRB.LESS_EQUAL, DoM, "");
    model.addConstr(objective, GRB.LESS_EQUAL, 20, "");
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
      int[][][][][] DAValue     = new int[Rounds][4][3][32][3];
      int[][][][] DPValue  = new int[Rounds][4][32][3];
      int[][][][] DP2Value  = new int[Rounds][4][32][3];
      int[][][][] DC1Value = new int[Rounds][4][32][2];
      int[][][][] DC12Value = new int[Rounds][4][32][2];
      int[][][][][] DBValue  = new int[Rounds][4][3][32][3];
      int[][][][][] DC2Value  = new int[Rounds][4][3][32][2];
      int[][] domValue  = new int[4][32];
      int[] objValue  = new int[3];
      
      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 4; i++) 
          for (int j = 0; j < 3; j++) 
	    for (int k = 0; k < 32; k++) {
          for (int l = 0; l < 2; l++){
              DC2Value[round][i][j][k][l]  = (int) Math.round(DC2[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
          }
              for (int l = 0; l < 3; l++)  { 
                DAValue[round][i][j][k][l]  = (int) Math.round(DA[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                DBValue[round][i][j][k][l]  = (int) Math.round(DB[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
              }
                
      }
      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 4; i++) 
	  for (int k = 0; k < 32; k++) {
        for (int l = 0; l < 2; l++) {
          DC1Value[round][i][k][l] = (int) Math.round(DC1[round][i][k][l].get(GRB.DoubleAttr.Xn));
          DC12Value[round][i][k][l] = (int) Math.round(DC12[round][i][k][l].get(GRB.DoubleAttr.Xn));
        }
            for (int l = 0; l < 3; l++)  { 
              DPValue[round][i][k][l]  = (int) Math.round(DP[round][i][k][l].get(GRB.DoubleAttr.Xn));
              DP2Value[round][i][k][l]  = (int) Math.round(DP2[round][i][k][l].get(GRB.DoubleAttr.Xn));
            }
      }
      for (int i = 0; i < 4; i++) 
	for (int k = 0; k < 32; k++) 
          domValue[i][k]  = (int) Math.round(dom[i][k].get(GRB.DoubleAttr.Xn));

      for (int i = 0; i < 3; i++) 
        objValue[i]  = (int) Math.round(obj[i].get(GRB.DoubleAttr.Xn));

      return new MitmSolution(Rounds, (int) Math.round(model.get(GRB.DoubleAttr.PoolObjVal)), DAValue,DBValue, DC2Value, DPValue, DP2Value, DC1Value, DC12Value, domValue, objValue);
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

  private void articleTrail_r67() throws GRBException{
    int[][] redz = {
            {0, 2, 3, 5, 6, 7, 8, 9, 11, 12, 14, 15, 17, 20, 23, 26, 29, 31},  //18
            {0, 2, 3, 5, 7, 8, 9, 11, 12, 14, 16, 17, 20, 21, 23, 25, 26}, //17
            {0, 3, 5, 7, 8, 9, 12, 14, 17, 18, 21, 23, 26, 28, 30, 31}, //16
            {0, 1, 3, 5, 8, 9, 10, 12, 14, 17, 18, 19, 21, 23, 26, 28} //16
    };
    int [][] bluez = {
            {},  //0
            {},  //0
            {4, 13, 22, 27}, //4
            {4, 13, 27} //3
    };
    int [][] grayz = {
            {1, 4, 10, 13, 16, 18, 19, 21, 22, 24, 25, 27, 28, 30}, //14
            {1, 4, 6, 10, 13, 15, 18, 19, 22, 24, 27, 28, 29, 30, 31}, //15
            {1, 2, 6, 10, 11, 15, 16, 19, 20, 24, 25, 29}, //12
            {2, 6, 7, 11, 15, 16, 20, 22, 24, 25, 29, 30, 31} //13
    };



    for (int i = 0; i < redz.length; i++)
      for (int j = 0; j < redz[i].length; j++){
        model.addConstr(DA[0][i][2][redz[i][j]][0], GRB.EQUAL, 0, "");
        model.addConstr(DA[0][i][2][redz[i][j]][2], GRB.EQUAL, 1, "");
      }

    for (int i = 0; i < bluez.length; i++)
      for (int j = 0; j < bluez[i].length; j++){
        model.addConstr(DA[0][i][2][bluez[i][j]][0], GRB.EQUAL, 1, "");
        model.addConstr(DA[0][i][2][bluez[i][j]][2], GRB.EQUAL, 0, "");
      }
    for (int i = 0; i < grayz.length; i++)
      for (int j = 0; j < grayz[i].length; j++){
        model.addConstr(DA[0][i][2][grayz[i][j]][0], GRB.EQUAL, 0, "");
        model.addConstr(DA[0][i][2][grayz[i][j]][2], GRB.EQUAL, 0, "");
      }


    for (int i = 0; i < 4; i++)
      for (int k = 0; k < 32; k++){
        if ((i==1 & k==8) | (i==1 & k==10) | (i==2 & k==10) | (i==1 & k==17) | (i==1 & k==26) | (i==1 & k==31) | (i==2 & k==31)){
          model.addConstr(dom[i][k], GRB.EQUAL, 1, "");
        }
        else{
          model.addConstr(dom[i][k], GRB.EQUAL, 0, "");
        }

      }
  }


  private void articleTrail_r68() throws GRBException{
    int[][] redz = {
            {0, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 20, 23, 25, 26, 29, 31}, // 18
            {0, 2, 3, 5, 7, 8, 9, 11, 12, 14, 16, 17, 20, 23, 25, 26, 30}, // 17
            {0, 3, 5, 8, 9, 12, 14, 17, 18, 21, 23, 26, 28, 30, 31}, // 15
            {0, 1, 2, 3, 5, 8, 9, 10, 11, 12, 14, 17, 18, 19, 20, 23, 26, 28}, // 18
    };
    int [][] bluez = {
            {},  //0
            {},  //0
            {4, 13, 22, 27}, //4
            {4, 13, 27} //3
    };
    int [][] grayz = {
            {1, 4, 7, 10, 13, 16, 18, 19, 21, 22, 24, 27, 28, 30}, // 14
            {1, 4, 6, 10, 13, 15, 18, 19, 21, 22, 24, 27, 28, 29, 31}, // 15
            {1, 2, 6, 7, 10, 11, 15, 16, 19, 20, 24, 25, 29}, // 13
            {6, 7, 15, 16, 21, 22, 24, 25, 29, 30, 31}, // 11
    };



    for (int i = 0; i < redz.length; i++)
      for (int j = 0; j < redz[i].length; j++){
        model.addConstr(DA[0][i][2][redz[i][j]][0], GRB.EQUAL, 0, "");
        model.addConstr(DA[0][i][2][redz[i][j]][2], GRB.EQUAL, 1, "");
      }

    for (int i = 0; i < bluez.length; i++)
      for (int j = 0; j < bluez[i].length; j++){
        model.addConstr(DA[0][i][2][bluez[i][j]][0], GRB.EQUAL, 1, "");
        model.addConstr(DA[0][i][2][bluez[i][j]][2], GRB.EQUAL, 0, "");
      }
    for (int i = 0; i < grayz.length; i++)
      for (int j = 0; j < grayz[i].length; j++){
        model.addConstr(DA[0][i][2][grayz[i][j]][0], GRB.EQUAL, 0, "");
        model.addConstr(DA[0][i][2][grayz[i][j]][2], GRB.EQUAL, 0, "");
      }


    for (int i = 0; i < 4; i++)
      for (int k = 0; k < 32; k++){
        if ((i==1 & k==8) | (i==1 & k==10) | (i==2 & k==10) | (i==1 & k==17) | (i==1 & k==26) | (i==1 & k==31) | (i==2 & k==31)){
          model.addConstr(dom[i][k], GRB.EQUAL, 1, "");
        }
        else{
          model.addConstr(dom[i][k], GRB.EQUAL, 0, "");
        }

      }
  }
}




