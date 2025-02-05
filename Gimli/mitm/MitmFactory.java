package mitmsearch.mitm;

import gurobi.*;

public class MitmFactory {
  private GRBModel model;
  //private int MatchRound;

  public MitmFactory(final GRBModel model) {
    this.model = model;
    //this.MatchRound = MatchRound;
  }

    public void addfixed_in(GRBVar[][][][][] DA) throws GRBException {
        for (int i = 0; i < 3; i ++)
            for (int j = 0; j < 4; j ++)
                for (int k = 0; k < 32; k++) {
                    if (i == 0) {
                        model.addConstr(DA[0][i][j][k][1], GRB.EQUAL, 1, "");
                        model.addConstr(linExprOf(DA[0][i][j][k][0], DA[0][i][j][k][2]), GRB.GREATER_EQUAL, 1, "");
                    }
                    else {
                        for (int l = 0; l < 3; l++)
                            model.addConstr(DA[0][i][j][k][l], GRB.EQUAL, 1, "");
                    }

                    // padding
                    if ((i==0) && (j==3) && (k >= 24)) {
                        for (int l = 0; l < 3; l++)
                            model.addConstr(DA[0][i][j][k][l], GRB.EQUAL, 1, "");
                    }
                }
    }


    public void addthreexor(GRBVar[][][][][] DA, GRBVar[][][][][] DP, GRBVar[][][][][] DB, GRBVar[][][][] DC) throws GRBException {
        GRBVar[][][][][] DP_Allone = new GRBVar[DB.length][3][4][32][3];
        GRBVar[][][][] DP_Twored = new GRBVar[DB.length][3][4][32];
        double[] t1 = {1.0, 1.0, -1.0};

        for (int round = 0; round < DB.length; round ++)
            for (int i = 0; i < 3; i ++)
                for (int j = 0; j < 4; j ++)
                    for (int k = 0; k < 32; k++){
                        for (int l = 0; l < 3; l ++) {
                            DP_Allone[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_Allone_"+round+"_"+i+"_"+j+"_"+k+"_"+l);
                            if (i == 0)
                                Determine_AllOne(DP_Allone[round][i][j][k][l], DA[round][1][j][(k+9)%32][l], DA[round][2][j][k][l], DP[round][i][j][k][l]);
                            if (i == 1)
                                Determine_AllOne(DP_Allone[round][i][j][k][l], DA[round][0][j][(k+24)%32][l], DA[round][1][j][(k+9)%32][l], DP[round][i][j][k][l]);
                            if (i == 2) {
                                if (k < 31)
                                    Determine_AllOne(DP_Allone[round][i][j][k][l], DA[round][0][j][(k+24)%32][l], DA[round][2][j][k+1][l], DP[round][i][j][k][l]);
                                else
                                    Determine_AllOne(DP_Allone[round][i][j][k][l], DA[round][0][j][(k+24)%32][l], DP[round][i][j][k][l]);
                            }
                        }

                        DP_Twored[round][i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_Twored_"+round+"_"+i+"_"+j+"_"+k);
                        if (i == 0)
                            Determine_twointhree(DP_Twored[round][i][j][k], DA[round][1][j][(k+9)%32][0], DA[round][2][j][k][0], DP[round][i][j][k][0]);
                        if (i == 1)
                            Determine_twointhree(DP_Twored[round][i][j][k], DA[round][0][j][(k+24)%32][0], DA[round][1][j][(k+9)%32][0], DP[round][i][j][k][0]);
                        if (i == 2) {
                            if (k < 31)
                                Determine_twointhree(DP_Twored[round][i][j][k], DA[round][0][j][(k+24)%32][0], DA[round][2][j][k+1][0], DP[round][i][j][k][0]);
                            else
                                Determine_Allzero(DP_Twored[round][i][j][k], DA[round][0][j][(k+24)%32][0], DP[round][i][j][k][0]);
                        }


                        model.addConstr(DP_Allone[round][i][j][k][1], GRB.EQUAL, DB[round][i][j][k][1], "");
                        model.addConstr(DP_Allone[round][i][j][k][1], GRB.GREATER_EQUAL, DB[round][i][j][k][0], "");
                        model.addConstr(DP_Allone[round][i][j][k][0], GRB.LESS_EQUAL, DB[round][i][j][k][0], "");
                        model.addConstr(linExprOf(t1, DC[round][i][j][k], DP_Allone[round][i][j][k][0], DB[round][i][j][k][0]), GRB.EQUAL, 0, "");
                        model.addConstr(DP_Allone[round][i][j][k][2], GRB.EQUAL, DB[round][i][j][k][2], "");


                        model.addConstr(DC[round][i][j][k], GRB.LESS_EQUAL, DP_Twored[round][i][j][k], "");

                    }
    }

    public void addswap(GRBVar[][][][][] DB, GRBVar[][][][][] DA) throws GRBException {
        for (int round = 0; round < DB.length; round ++){
            //small swap
            if (round % 4 == 0){
                for (int i = 0; i < 3; i ++)
                    for (int j = 0; j < 4; j ++)
                        for (int k = 0; k < 32; k++){
                            for (int l = 0; l < 3; l ++) {
                                if (i==0){
                                    model.addConstr(DA[round+1][i][0][k][l], GRB.EQUAL, DB[round][i][1][k][l], "");
                                    model.addConstr(DA[round+1][i][1][k][l], GRB.EQUAL, DB[round][i][0][k][l], "");
                                    model.addConstr(DA[round+1][i][2][k][l], GRB.EQUAL, DB[round][i][3][k][l], "");
                                    model.addConstr(DA[round+1][i][3][k][l], GRB.EQUAL, DB[round][i][2][k][l], "");
                                }
                                else
                                    model.addConstr(DA[round+1][i][j][k][l], GRB.EQUAL, DB[round][i][j][k][l], "");
                            }
                        }
            }
            //small swap
            else if (round % 4 == 2){
                for (int i = 0; i < 3; i ++)
                    for (int j = 0; j < 4; j ++)
                        for (int k = 0; k < 32; k++){
                            for (int l = 0; l < 3; l ++) {
                                if (i==0){
                                    model.addConstr(DA[round+1][i][j][k][l], GRB.EQUAL, DB[round][i][(j+2)%4][k][l], "");
                                }
                                else
                                    model.addConstr(DA[round+1][i][j][k][l], GRB.EQUAL, DB[round][i][j][k][l], "");
                            }
                        }
            }
            else {
                for (int i = 0; i < 3; i ++)
                    for (int j = 0; j < 4; j ++)
                        for (int k = 0; k < 32; k++){
                            for (int l = 0; l < 3; l ++) {
                                model.addConstr(DA[round+1][i][j][k][l], GRB.EQUAL, DB[round][i][j][k][l], "");
                            }
                        }
            }
        }
    }

    public void addnonlinear(GRBVar[][][][][] DA, GRBVar[][][][][] DP) throws GRBException {
        GRBVar[][][][][] DA_AllOne = new GRBVar[DP.length][3][4][32][3];
        GRBVar[][][][] DA_NotMul = new GRBVar[DP.length][3][4][32];
        for (int round = 0; round < DP.length; round ++) {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 32; k++) {
                        DA_NotMul[round][i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_NotMul_" + round + "_" + i + "_" + j + "_" + k);
                        for (int l = 0; l < 3; l++)
                            DA_AllOne[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_AllOne_" + round + "_" + i + "_" + j + "_" + k+ "_" + l);
                    }

            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 32; k++) {
                    Determine_Notmul(DA[round][0][j][(k+24)%32][0], DA[round][0][j][(k+24)%32][2], DA[round][1][j][(k+9)%32][0], DA[round][1][j][(k+9)%32][2], DA_NotMul[round][0][j][k]);
                    Determine_Notmul(DA[round][0][j][(k+24)%32][0], DA[round][0][j][(k+24)%32][2], DA[round][2][j][k][0], DA[round][2][j][k][2], DA_NotMul[round][1][j][k]);
                    Determine_Notmul(DA[round][1][j][(k+9)%32][0], DA[round][1][j][(k+9)%32][2], DA[round][2][j][k][0], DA[round][2][j][k][2], DA_NotMul[round][2][j][k]);

                    for (int l = 0; l < 3; l++){
                        Determine_AllOne(DA_AllOne[round][0][j][k][l], DA[round][0][j][(k+24)%32][l], DA[round][1][j][(k+9)%32][l]);
                        Determine_AllOne(DA_AllOne[round][1][j][k][l], DA[round][0][j][(k+24)%32][l], DA[round][2][j][k][l]);
                        Determine_AllOne(DA_AllOne[round][2][j][k][l], DA[round][1][j][(k+9)%32][l], DA[round][2][j][k][l]);
                    }
                }

            for (int j = 0; j < 4; j++) {
                // DB 0
                if (round == 0) {
                    for (int k = 0; k < 32; k++)
                        for (int l = 0; l < 3; l++) {
                            model.addConstr(DP[round][0][j][k][l], GRB.EQUAL, 1, "");
                        }
                }
                else {
                    for (int k = 0; k < 29; k++) {
                        model.addConstr(DP[round][0][j][k][0], GRB.EQUAL, DA_AllOne[round][0][j][k + 3][0], "");
                        model.addConstr(DP[round][0][j][k][2], GRB.EQUAL, DA_AllOne[round][0][j][k + 3][2], "");
                        Determine_AllOne(DP[round][0][j][k][1], DA_AllOne[round][0][j][k + 3][1], DA_NotMul[round][0][j][k + 3]);
                    }
                    for (int k = 29; k < 32; k++)
                        for (int l = 0; l < 3; l++) {
                            model.addConstr(DP[round][0][j][k][l], GRB.EQUAL, 1, "");
                        }
                }

                // DB 1
                for (int k = 0; k < 31; k++) {
                    model.addConstr(DP[round][1][j][k][0], GRB.EQUAL, DA_AllOne[round][1][j][k + 1][0], "");
                    model.addConstr(DP[round][1][j][k][2], GRB.EQUAL, DA_AllOne[round][1][j][k + 1][2], "");
                    Determine_AllOne(DP[round][1][j][k][1], DA_AllOne[round][1][j][k + 1][1], DA_NotMul[round][1][j][k + 1]);
                }
                for (int k = 31; k < 32; k++)
                    for (int l = 0; l < 3; l++){
                        model.addConstr(DP[round][1][j][k][l], GRB.EQUAL, 1, "");
                    }

                // DB 2
                for (int k = 0; k < 30; k++) {
                    model.addConstr(DP[round][2][j][k][0], GRB.EQUAL, DA_AllOne[round][2][j][k + 2][0], "");
                    model.addConstr(DP[round][2][j][k][2], GRB.EQUAL, DA_AllOne[round][2][j][k + 2][2], "");
                    Determine_AllOne(DP[round][2][j][k][1], DA_AllOne[round][2][j][k + 2][1], DA_NotMul[round][2][j][k + 2]);
                }
                for (int k = 30; k < 32; k++)
                    for (int l = 0; l < 3; l++){
                        model.addConstr(DP[round][2][j][k][l], GRB.EQUAL, 1, "");
                    }

            }
        }
    }

    public void addDoM(GRBVar[][][][][] DA, GRBVar[][] dom) throws GRBException {
        GRBVar[][] DA_Nowhite_xor = new GRBVar[4][32];
        GRBVar[][] DA_Nowhite_mul = new GRBVar[4][32];
        GRBVar[][] DA_Notmul = new GRBVar[4][32];
        int r = DA.length-1;
        for (int j = 0; j < 4; j ++)
            for (int k = 0; k < 32; k ++) {
                DA_Nowhite_xor[j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_Nowhite_dom_xor_"+j+"_"+k);
                DA_Nowhite_mul[j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_Nowhite_dom_mul_"+j+"_"+k);
                DA_Notmul[j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_Notmul_dom_"+j+"_"+k);

                Determine_AllOne(DA_Nowhite_xor[j][k], DA[r][1][j][(k+9)%32][1], DA[r][2][j][k][1]);
                Determine_AllOne(DA_Nowhite_mul[j][k], DA[r][0][j][(k+24)%32][1], DA[r][1][j][(k+9)%32][1]);
                Determine_Notmul(DA[r][0][j][(k+24)%32][0], DA[r][0][j][(k+24)%32][2], DA[r][1][j][(k+9)%32][0], DA[r][1][j][(k+9)%32][2], DA_Notmul[j][k]);
            }
        for (int j = 0; j < 4; j ++) {
            for (int k = 0; k < 29; k++) {
                Determine_AllOne(dom[j][k], DA_Nowhite_xor[j][k], DA_Nowhite_mul[j][k+3], DA_Notmul[j][k+3]);
            }
            for (int k = 29; k < 32; k++) {
                model.addConstr(dom[j][k], GRB.EQUAL, DA_Nowhite_xor[j][k], "");
            }
        }
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

  public void Determine_Allzero(GRBVar mainVar, GRBVar ... vars) throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
	expr.addTerm(1.0, mainVar);
        for (int i = 0; i < vars.length; i++) {
          expr.addTerm(1.0, vars[i]);
	  model.addConstr(linExprOf(mainVar,vars[i]), GRB.LESS_EQUAL, 1, "");
        }
        model.addConstr(expr, GRB.GREATER_EQUAL, 1, "");
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

    public void Determine_two_oneinfive(GRBVar mainVar, GRBVar ... vars) throws GRBException {
        GRBLinExpr expr1 = new GRBLinExpr();
        GRBLinExpr expr2 = new GRBLinExpr();
        expr1.addTerm(4.0, mainVar);
        expr2.addTerm(-2.0, mainVar);
        for (int i = 0; i < vars.length; i++) {
            expr1.addTerm(-1.0, vars[i]);
            expr2.addTerm(1.0, vars[i]);
        }
        model.addConstr(expr1, GRB.GREATER_EQUAL, -1, "");
        model.addConstr(expr2, GRB.GREATER_EQUAL, 0, "");
    }

    public void Determine_two_oneinthree(GRBVar mainVar, GRBVar ... vars) throws GRBException {
        GRBLinExpr expr1 = new GRBLinExpr();
        GRBLinExpr expr2 = new GRBLinExpr();
        expr1.addTerm(2.0, mainVar);
        expr2.addTerm(-2.0, mainVar);
        for (int i = 0; i < vars.length; i++) {
            expr1.addTerm(-1.0, vars[i]);
            expr2.addTerm(1.0, vars[i]);
        }
        model.addConstr(expr1, GRB.GREATER_EQUAL, -1, "");
        model.addConstr(expr2, GRB.GREATER_EQUAL, 0, "");
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
