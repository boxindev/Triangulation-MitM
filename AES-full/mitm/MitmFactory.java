package mitmsearch.mitm;

import gurobi.*;

public class MitmFactory {
    private GRBModel model;

    public MitmFactory(final GRBModel model) {
    this.model = model;
    }

    public void addfixed_in(GRBVar[][][][][] Af, GRBVar[][][][][] MCb, GRBVar[][][][][] S, GRBVar[][][][][] RK, int Startr, int regime) throws GRBException {
      for (int i = 0; i < 4+regime*2; i++)
	for (int j = 0; j < 4; j++){
          model.addConstr(S[Startr][i][j][0][1], GRB.EQUAL, 1, "");//red or gray
          model.addConstr(S[Startr][i][j][1][0], GRB.EQUAL, 1, "");//blue or gray
          model.addConstr(linExprOf(S[Startr][i][j][0][0],S[Startr][i][j][1][1]), GRB.GREATER_EQUAL, 1, "");
	}
      for (int i = 0; i < 4; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++) 
            for (int l = 0; l < 2; l++) {
	      model.addConstr(Af[0][i][j][k][l], GRB.EQUAL, RK[0][i][j][k][l], "");
	      model.addConstr(MCb[MCb.length-1][i][j][k][l], GRB.EQUAL, RK[RK.length-1][i][j][k][l], "");
        }
    }

    public void addforward(GRBVar[][][][][] A, GRBVar[][][][][] SB, GRBVar[][][][][] MC, GRBVar[][][][][] RK, GRBVar[][][][] DC, GRBVar[][][][] DCAK, GRBVar[][][] Guessl, int round) throws GRBException {
      //SB
      addAtoSB_linear(A, SB, Guessl, round);
      if (round != A.length-1) {
        //MC	
        GRBVar[][][] AllOne = new GRBVar[4][2][2];
        for (int i = 0; i < 4; i++)
          for (int k = 0; k < 2; k++) 
            for (int l = 0; l < 2; l++) {
              AllOne[i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "AllOnef_"+round+"_"+i+"_"+k+"_"+l);
              Determine_AllOne(AllOne[i][k][l], SB[round][i][0][k][l], SB[round][i][1][k][l], SB[round][i][2][k][l], SB[round][i][3][k][l]);
              }
        
        GRBVar[][] Allgray = new GRBVar[4][2];
        GRBVar[] Existwhite = new GRBVar[4];
        GRBVar[][] ifconsume = new GRBVar[4][2];
	    for (int i = 0; i < 4; i++) {
          for (int j = 0; j < 2; j++) {
             Allgray[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Allgrayf_"+round+"_"+i+"_"+j);
             ifconsume[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "ifconsumef_"+round+"_"+i+"_"+j);
          }
            Determine_AllOne(Allgray[i][0], SB[round][i][0][0][0], SB[round][i][0][0][1], SB[round][i][1][0][0], SB[round][i][1][0][1], SB[round][i][2][0][0], SB[round][i][2][0][1], SB[round][i][3][0][0], SB[round][i][3][0][1]);
            Determine_AllOne(Allgray[i][1], SB[round][i][0][1][0], SB[round][i][0][1][1], SB[round][i][1][1][0], SB[round][i][1][1][1], SB[round][i][2][1][0], SB[round][i][2][1][1], SB[round][i][3][1][0], SB[round][i][3][1][1]);

            Existwhite[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Existwhitef_"+round+"_"+i);
            model.addConstr(linExprOf(Existwhite[i], AllOne[i][0][1]), GRB.EQUAL, 1, "");

            Determine_ExistOne(ifconsume[i][0], Allgray[i][0], Existwhite[i]);
            Determine_ExistOne(ifconsume[i][1], Allgray[i][1], Existwhite[i]);
        }


	    double[] t1 = {1, -1, 1};
        double[] t2 = {1, 1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1};
	    for (int i = 0; i < 4; i++) {
          for (int j = 0; j < 4; j++){
            //red
            model.addConstr(MC[round][i][j][0][0], GRB.LESS_EQUAL, AllOne[i][0][1], "");
            model.addConstr(MC[round][i][j][0][1], GRB.EQUAL, AllOne[i][0][1], "");
            model.addConstr(MC[round][i][j][0][0], GRB.GREATER_EQUAL, AllOne[i][0][0], "");
            model.addConstr(linExprOf(t1, DC[round][i][j][0], MC[round][i][j][0][0], AllOne[i][0][0]), GRB.EQUAL, 0, "");

            //blue
            model.addConstr(MC[round][i][j][1][1], GRB.LESS_EQUAL, AllOne[i][1][0], "");
            model.addConstr(MC[round][i][j][1][0], GRB.EQUAL, AllOne[i][1][0], "");
            model.addConstr(MC[round][i][j][1][1], GRB.GREATER_EQUAL, AllOne[i][1][1], "");
            model.addConstr(linExprOf(t1, DC[round][i][j][1], MC[round][i][j][1][1], AllOne[i][1][1]), GRB.EQUAL, 0, "");
          }
          //DC-numred-ifconsume<=-1
          model.addConstr(linExprOf(t2, DC[round][i][0][0], DC[round][i][1][0], DC[round][i][2][0], DC[round][i][3][0], SB[round][i][0][0][0], SB[round][i][1][0][0], SB[round][i][2][0][0], SB[round][i][3][0][0], SB[round][i][0][0][1], SB[round][i][1][0][1], SB[round][i][2][0][1], SB[round][i][3][0][1], ifconsume[i][0]), GRB.LESS_EQUAL, -1, "");
          model.addConstr(linExprOf(t2, DC[round][i][0][1], DC[round][i][1][1], DC[round][i][2][1], DC[round][i][3][1], SB[round][i][0][1][1], SB[round][i][1][1][1], SB[round][i][2][1][1], SB[round][i][3][1][1], SB[round][i][0][1][0], SB[round][i][1][1][0], SB[round][i][2][1][0], SB[round][i][3][1][0], ifconsume[i][1]), GRB.LESS_EQUAL, -1, "");
        }

        //AK
        for (int i = 0; i < 4; i++) 
          for (int j = 0; j < 4; j++){
            addtwoXor(A[round+1][i][j], DCAK[round][i][j], MC[round][i][j], RK[round+1][i][j]);
        }
      }
    }


     public void addbackward(GRBVar[][][][][] A, GRBVar[][][][][] SB, GRBVar[][][][][] MC, GRBVar[][][][][] RK, GRBVar[][][][] DC, GRBVar[][][][] DCAK, GRBVar[][][] Guessl, int round, int fRounds) throws GRBException {
      //SB-1
      addSBtoA_linear(A, SB, Guessl, round);
      if (round > 0) {
        //AK-1
        for (int i = 0; i < 4; i++) 
          for (int j = 0; j < 4; j++){
            addtwoXor(MC[round-1][i][j], DCAK[round][i][j], A[round][i][j], RK[round+fRounds][i][j]);
        }
      }
      //if (round != A.length-1) {
        //MC-1	
        GRBVar[][][] AllOne = new GRBVar[4][2][2];
        for (int i = 0; i < 4; i++)
          for (int k = 0; k < 2; k++) 
            for (int l = 0; l < 2; l++) {
              AllOne[i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "AllOneb_"+round+"_"+i+"_"+k+"_"+l);
	      Determine_AllOne(AllOne[i][k][l], MC[round][i][0][k][l], MC[round][i][1][k][l], MC[round][i][2][k][l], MC[round][i][3][k][l]);
            }
        
        GRBVar[][] Allgray = new GRBVar[4][2];
        GRBVar[] Existwhite = new GRBVar[4];
        GRBVar[][] ifconsume = new GRBVar[4][2];
        for (int i = 0; i < 4; i++) {
              for (int j = 0; j < 2; j++) {
                 Allgray[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Allgrayb_"+round+"_"+i+"_"+j);
                 ifconsume[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "ifconsumeb_"+round+"_"+i+"_"+j);
              }
          Determine_AllOne(Allgray[i][0], MC[round][i][0][0][0], MC[round][i][0][0][1], MC[round][i][1][0][0], MC[round][i][1][0][1], MC[round][i][2][0][0], MC[round][i][2][0][1], MC[round][i][3][0][0], MC[round][i][3][0][1]);
          Determine_AllOne(Allgray[i][1], MC[round][i][0][1][0], MC[round][i][0][1][1], MC[round][i][1][1][0], MC[round][i][1][1][1], MC[round][i][2][1][0], MC[round][i][2][1][1], MC[round][i][3][1][0], MC[round][i][3][1][1]);

              Existwhite[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Existwhitef_"+round+"_"+i);
          model.addConstr(linExprOf(Existwhite[i], AllOne[i][0][1]), GRB.EQUAL, 1, "");

          Determine_ExistOne(ifconsume[i][0], Allgray[i][0], Existwhite[i]);
          Determine_ExistOne(ifconsume[i][1], Allgray[i][1], Existwhite[i]);
        }
        double[] t1 = {1, -1, 1};
        double[] t2 = {1, 1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1};
        for (int i = 0; i < 4; i++) {
              for (int j = 0; j < 4; j++){
            //red
            model.addConstr(SB[round][i][j][0][0], GRB.LESS_EQUAL, AllOne[i][0][1], "");
            model.addConstr(SB[round][i][j][0][1], GRB.EQUAL, AllOne[i][0][1], "");
            model.addConstr(SB[round][i][j][0][0], GRB.GREATER_EQUAL, AllOne[i][0][0], "");
            model.addConstr(linExprOf(t1, DC[round][i][j][0], SB[round][i][j][0][0], AllOne[i][0][0]), GRB.EQUAL, 0, "");

            //blue
            model.addConstr(SB[round][i][j][1][1], GRB.LESS_EQUAL, AllOne[i][1][0], "");
            model.addConstr(SB[round][i][j][1][0], GRB.EQUAL, AllOne[i][1][0], "");
            model.addConstr(SB[round][i][j][1][1], GRB.GREATER_EQUAL, AllOne[i][1][1], "");
            model.addConstr(linExprOf(t1, DC[round][i][j][1], SB[round][i][j][1][1], AllOne[i][1][1]), GRB.EQUAL, 0, "");
              }
              //DC-numred-allgray<=-1
              model.addConstr(linExprOf(t2, DC[round][i][0][0], DC[round][i][1][0], DC[round][i][2][0], DC[round][i][3][0], MC[round][i][0][0][0], MC[round][i][1][0][0], MC[round][i][2][0][0], MC[round][i][3][0][0], MC[round][i][0][0][1], A[round][i][1][0][1], MC[round][i][2][0][1], MC[round][i][3][0][1], ifconsume[i][0]), GRB.LESS_EQUAL, -1, "");
              model.addConstr(linExprOf(t2, DC[round][i][0][1], DC[round][i][1][1], DC[round][i][2][1], DC[round][i][3][1], MC[round][i][0][1][1], MC[round][i][1][1][1], MC[round][i][2][1][1], MC[round][i][3][1][1], MC[round][i][0][1][0], MC[round][i][1][1][0], MC[round][i][2][1][0], MC[round][i][3][1][0], ifconsume[i][1]), GRB.LESS_EQUAL, -1, "");
        }
      //}
    }

   
    public void addMatch(GRBVar[][][][][] SBf, GRBVar[][][][][] MCf, GRBVar[][][][][] Ab, GRBVar[][][][][] RK, GRBVar[][][][] DCAK, GRBVar[][] dom, int fRounds) throws GRBException {  
        for (int i = 0; i < 4; i++) 
          for (int j = 0; j < 4; j++){
            addtwoXor(MCf[MCf.length-1][i][j], DCAK[fRounds-1][i][j], Ab[0][i][j], RK[fRounds][i][j]);
        }
       
        GRBVar[][] Notwhite = new GRBVar[4][8];
	    for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                Notwhite[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Notwhite_"+i+"_"+j);
                Notwhite[i][j+4] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Notwhite_"+i+"_"+(j+4));
                Determine_ExistOne(Notwhite[i][j], SBf[SBf.length-1][i][j][0][0], SBf[SBf.length-1][i][j][0][1], SBf[SBf.length-1][i][j][1][1]);
                Determine_ExistOne(Notwhite[i][j+4], MCf[MCf.length-1][i][j][0][0], MCf[MCf.length-1][i][j][0][1], MCf[MCf.length-1][i][j][1][1]);
            }
	    double[][] t = {{1, 1, 1, 1, 1, 1, 1, 1, -5, -1, -1, -1},
			{0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1},
			{-1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1}};
	    double[] c = {0, 0, 0, 0, -4};
	    for (int i = 0; i < 4; i++) {
	        for (int q = 0; q < 5; q++) {
		        model.addConstr(linExprOf(t[q], Notwhite[i][0], Notwhite[i][1], Notwhite[i][2],Notwhite[i][3], Notwhite[i][4], Notwhite[i][5], Notwhite[i][6], Notwhite[i][7], dom[i][0], dom[i][1], dom[i][2], dom[i][3]), GRB.GREATER_EQUAL, c[q], "");
	        }
        }
    }

    public void addMatch_bigSbox(GRBVar[][][][][] Af, GRBVar[][][][][] SBb, GRBVar[][][][][] RK, GRBVar[][] dom, int fRounds) throws GRBException {  
        /*for (int i = 0; i < 4; i++) 
          for (int j = 0; j < 4; j++){
            addtwoXor(MCf[MCf.length-1][i][j], DCAK[fRounds-1][i][j], Ab[0][i][j], RK[fRounds][i][j]);
        }*/
       
        GRBVar[][] Notwhite = new GRBVar[4][8];
        GRBVar[][] Green = new GRBVar[4][8];
	    for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                Notwhite[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Notwhite_"+i+"_"+j);
                Notwhite[i][j+4] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Notwhite_"+i+"_"+(j+4));
                Green[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Green_"+i+"_"+j);
                Green[i][j+4] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Green_"+i+"_"+(j+4));
                Determine_ExistOne(Notwhite[i][j], Af[Af.length-1][(i+j)%4][j][0][0], Af[Af.length-1][(i+j)%4][j][0][1], Af[Af.length-1][(i+j)%4][j][1][1]);
                Determine_ExistOne(Notwhite[i][j+4], SBb[0][(i-j+4)%4][j][0][0], SBb[0][(i-j+4)%4][j][0][1], SBb[0][(i-j+4)%4][j][1][1]);
                Determine_Green(Green[i][j], Af[Af.length-1][(i+j)%4][j][0][0], Af[Af.length-1][(i+j)%4][j][0][1], Af[Af.length-1][(i+j)%4][j][1][1]);
                Determine_Green(Green[i][j+4], SBb[0][(i-j+4)%4][j][0][0], SBb[0][(i-j+4)%4][j][0][1], SBb[0][(i-j+4)%4][j][1][1]);
            }
	    double[][] t = {{1, 1, 1, 1, 1, 1, 1, 1, -5, -1, -1, -1},
			{0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1},
			{-1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1}};
        double[] c = {0, 0, 0, 0, -4};
        for (int i = 0; i < 4; i++) {
            for (int q = 0; q < 5; q++) {
            model.addConstr(linExprOf(t[q], Notwhite[i][0], Notwhite[i][1], Notwhite[i][2],Notwhite[i][3], Notwhite[i][4], Notwhite[i][5], Notwhite[i][6], Notwhite[i][7], dom[i][0], dom[i][1], dom[i][2], dom[i][3]), GRB.GREATER_EQUAL, c[q], "");
            }
        }
        for (int i = 0; i < 4; i++) {
                GRBLinExpr Greennum = new GRBLinExpr();
                GRBLinExpr GreenBounds = new GRBLinExpr();
                GreenBounds.addTerm(-2.0, dom[i][0]);
                GreenBounds.addConstant(4.0);
            for (int j = 0; j < 8; j++) {
                    Greennum.addTerm(1.0, Green[i][j]);
                }
            //model.addConstr(Greennum, GRB.LESS_EQUAL, GreenBounds, " ");
        }
    }

    public void addAtoSB(GRBVar[][][][][] A, GRBVar[][][][][] SB, int r) throws GRBException {
	//SB
	/*double[][] t = {{0, 0, 0, 0, -1, 1, 0},
			{0, 1, 0, 0, 0, -1, 0},
			{0, 0, 0, 0, 0, 1, -1},
			{0, 0, 1, 0, 0, -1, 0},
			{-1, -1, -1, 0, 1, 0, 0},
			{1, 0, 0, 0, -1, 0, 0},
			{0, -1, -1, -1, 0, 0, 1},
			{0, 0, 0, 1, 0, 0, -1},
			{0, 0, 0, 0, 1, -1, 1}};
	double[] c = {0, 0, 0, 0, -2, 0, -2, 0, 0};*/
        double[][] t = {{0, 0, 0, 1, -1, 1},
			{0, 0, 0, 0, 1, -1},
			{0, 0, 0, -1, 1, 0},
			{0, 1, 0, 0, -1, 0}};
	double[] c = {0, 0, 0, 0};

	for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
		for (int q = 0; q < 4; q++) {
	            model.addConstr(linExprOf(t[q], A[r][(i+j)%4][j][0][0], A[r][(i+j)%4][j][0][1], A[r][(i+j)%4][j][1][1], SB[r][i][j][0][0], SB[r][i][j][0][1], SB[r][i][j][1][1]), GRB.GREATER_EQUAL, c[q], "");	
		}
		model.addConstr(A[r][(i+j)%4][j][0][0], GRB.EQUAL, SB[r][i][j][0][0], "");   	
		model.addConstr(A[r][(i+j)%4][j][1][1], GRB.EQUAL, SB[r][i][j][1][1], "");
		model.addConstr(SB[r][i][j][0][1], GRB.EQUAL, SB[r][i][j][1][0], "");	
	}
    }

    public void addSBtoA(GRBVar[][][][][] A, GRBVar[][][][][] SB, int r) throws GRBException {	
        double[][] t = {{0, 0, 0, 1, -1, 1},
			{0, 0, 0, 0, 1, -1},
			{0, 0, 0, -1, 1, 0},
			{0, 1, 0, 0, -1, 0}};
	double[] c = {0, 0, 0, 0};

	for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
		for (int q = 0; q < 4; q++) {
		    model.addConstr(linExprOf(t[q], SB[r][i][j][0][0], SB[r][i][j][0][1], SB[r][i][j][1][1], A[r][(i+j)%4][j][0][0], A[r][(i+j)%4][j][0][1], A[r][(i+j)%4][j][1][1]), GRB.GREATER_EQUAL, c[q], "");	
		}
		model.addConstr(A[r][(i+j)%4][j][0][0], GRB.EQUAL, SB[r][i][j][0][0], "");   	
		model.addConstr(A[r][(i+j)%4][j][1][1], GRB.EQUAL, SB[r][i][j][1][1], "");	
		model.addConstr(A[r][(i+j)%4][j][0][1], GRB.EQUAL, A[r][(i+j)%4][j][1][0], "");	
	}
    }


    public void addAtoSB_linear(GRBVar[][][][][] A, GRBVar[][][][][] SB, GRBVar[][][] Guessl, int r) throws GRBException {
        double[][] t = {{0, 0, 1, 1, -1, 0, 1},
			{0, 0, -1, 0, 1, 0, -1},
			{0, 0, 0, -1, 1, 0, -1},
			{0, 1, 0, 0, -1, 0, 0}};
	    double[] c = {0, 0, 0, 0};

	    for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                for (int q = 0; q < 4; q++) {
                    model.addConstr(linExprOf(t[q], A[r][(i+j)%4][j][0][0], A[r][(i+j)%4][j][0][1], A[r][(i+j)%4][j][1][1], SB[r][i][j][0][0], SB[r][i][j][0][1], SB[r][i][j][1][1], Guessl[r][(i+j)%4][j]), GRB.GREATER_EQUAL, c[q], "");
                }
                model.addConstr(A[r][(i+j)%4][j][0][0], GRB.EQUAL, SB[r][i][j][0][0], "");
                model.addConstr(A[r][(i+j)%4][j][1][1], GRB.EQUAL, SB[r][i][j][1][1], "");
                model.addConstr(SB[r][i][j][0][1], GRB.EQUAL, SB[r][i][j][1][0], "");
	        }
    }

    public void addSBtoA_linear(GRBVar[][][][][] A, GRBVar[][][][][] SB, GRBVar[][][] Guessl, int r) throws GRBException {	
        double[][] t = {{0, 0, 1, 1, -1, 0, 1},
			{0, 0, -1, 0, 1, 0, -1},
			{0, 0, 0, -1, 1, 0, -1},
			{0, 1, 0, 0, -1, 0, 0}};
	    double[] c = {0, 0, 0, 0};

	    for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                for (int q = 0; q < 4; q++) {
                    model.addConstr(linExprOf(t[q], SB[r][i][j][0][0], SB[r][i][j][0][1], SB[r][i][j][1][1], A[r][(i+j)%4][j][0][0], A[r][(i+j)%4][j][0][1], A[r][(i+j)%4][j][1][1], Guessl[r][i][j]), GRB.GREATER_EQUAL, c[q], "");
                }
                model.addConstr(A[r][(i+j)%4][j][0][0], GRB.EQUAL, SB[r][i][j][0][0], "");
                model.addConstr(A[r][(i+j)%4][j][1][1], GRB.EQUAL, SB[r][i][j][1][1], "");
                model.addConstr(A[r][(i+j)%4][j][0][1], GRB.EQUAL, A[r][(i+j)%4][j][1][0], "");
	}
    }

  public void addtwoXor(GRBVar[][] mainvar, GRBVar[] CK, GRBVar[][] ... vars) throws GRBException {
    GRBVar[] Allone = new GRBVar[3];
    for (int l = 0; l < 3; l++)
      Allone[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Allone"+l); 
    Determine_AllOne(Allone[0], vars[0][0][0], vars[1][0][0]); 
    Determine_AllOne(Allone[1], vars[0][0][1], vars[1][0][1]); 
    Determine_AllOne(Allone[2], vars[0][1][1], vars[1][1][1]); 
    
    GRBVar[] Twozero = new GRBVar[2];
    for (int l = 0; l < 2; l++)
      Twozero[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Twozero"+l);
    Determine_Allzero(Twozero[0], vars[0][0][0], vars[1][0][0]);
    Determine_Allzero(Twozero[1], vars[0][1][1], vars[1][1][1]);

    double[] t1 = {1.0, 1.0, -1.0};
    model.addConstr(Allone[1], GRB.EQUAL, mainvar[0][1], "");
    model.addConstr(Allone[1], GRB.EQUAL, mainvar[1][0], "");

    model.addConstr(Allone[1], GRB.GREATER_EQUAL, mainvar[0][0], "");
    model.addConstr(Allone[0], GRB.LESS_EQUAL, mainvar[0][0], "");
    model.addConstr(linExprOf(t1, CK[0], Allone[0], mainvar[0][0]), GRB.EQUAL, 0, ""); 
    model.addConstr(CK[0], GRB.LESS_EQUAL, Twozero[0], "");

    model.addConstr(Allone[1], GRB.GREATER_EQUAL, mainvar[1][1], "");
    model.addConstr(Allone[2], GRB.LESS_EQUAL, mainvar[1][1], "");
    model.addConstr(linExprOf(t1, CK[1], Allone[2], mainvar[1][1]), GRB.EQUAL, 0, ""); 
    model.addConstr(CK[1], GRB.LESS_EQUAL, Twozero[1], "");
  }

    public void Determine_consume(GRBVar AllOne, GRBVar Out, GRBVar DC) throws GRBException {
        model.addConstr(DC, GRB.LESS_EQUAL, Out, "");
        double[][] t = {{1.0, -1.0, 1.0},{1.0, 1.0, 1.0}};
        model.addConstr(linExprOf(t[0], DC, Out, AllOne), GRB.GREATER_EQUAL, 0, "");
        model.addConstr(linExprOf(t[1], DC, Out, AllOne), GRB.LESS_EQUAL, 2, "");
    }

    public void XOR_red(GRBVar mainVar, GRBVar ... vars) throws GRBException {
      GRBLinExpr expr = new GRBLinExpr();
      expr.addTerm(1.0, mainVar);
        for (int i = 0; i < vars.length; i++) {
          expr.addTerm(1.0, vars[i]);
	        model.addConstr(linExprOf(mainVar,vars[i]), GRB.LESS_EQUAL, 1, "");
        }
        model.addConstr(expr, GRB.GREATER_EQUAL, 1, "");
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

    public void Determine_Green(GRBVar mainVar, GRBVar ... vars) throws GRBException {
      double[][] t = {{0, 0, -1, -1},
		      {-1, 0, 0, -1},
		      {0, 1, 0, -1},
		      {1, -1, 1, 1}};
      double[] c = {-1, -1, 0, 0};
      for (int q = 0; q < 4; q++) {
	model.addConstr(linExprOf(t[q], vars[0], vars[1], vars[2], mainVar), GRB.GREATER_EQUAL, c[q], "");	
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
