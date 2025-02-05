package mitmsearch.mitm;

import gurobi.*;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.io.FileWriter;
public class Mitm {
    private final int Rounds;
    private final int keyRounds;
    private final int regime;
    private final int fRounds;
    private final int bRounds;
    private final int KeyStartr;
    private final int StateStartr;
    private final GRBModel model;
    private FileWriter logfile;
    private final MitmFactory factory;
    private final MitMKey Key;
    private final GRBVar[][][][][] Af;
    private final GRBVar[][][][][] SBf;
    private final GRBVar[][][][][] MCf;
    private final GRBVar[][][][] DCf;
    private final GRBVar[][][][] DCfAK;
    private final GRBVar[][][] Guesslf;
    private final GRBVar[][][][][] Ab;
    private final GRBVar[][][][][] SBb;
    private final GRBVar[][][][][] MCb;
    private final GRBVar[][][][] DCb;
    private final GRBVar[][][][] DCbAK;
    private final GRBVar[][][] Guesslb;
    private final GRBLinExpr objective;
    private final GRBVar[] obj;
    private final GRBVar[][] dom;

    /**
     * @param env the Gurobi environment
     */
    public Mitm(final GRBEnv env, final int Rounds, final int keyRounds, final int regime, final int fRounds, final int KeyStartr, final int StateStartr) throws GRBException {
        model = new GRBModel(env);
        this.Rounds = Rounds;
        this.keyRounds = keyRounds;
        this.regime = regime;
        this.fRounds = fRounds;
        this.KeyStartr = KeyStartr;
        this.StateStartr = StateStartr;
        bRounds = Rounds - fRounds;
        factory = new MitmFactory(model);
        Key = new MitMKey(model, Rounds, keyRounds, regime, KeyStartr);
        Af = new GRBVar[fRounds][4+regime*2][4][2][2];
        SBf = new GRBVar[fRounds][4+regime*2][4][2][2];
        MCf = new GRBVar[fRounds][4+regime*2][4][2][2];
        DCf = new GRBVar[fRounds][4+regime*2][4][2];
        DCfAK = new GRBVar[fRounds][4+regime*2][4][2];
        Ab = new GRBVar[bRounds][4+regime*2][4][2][2];
        SBb = new GRBVar[bRounds][4+regime*2][4][2][2];
        MCb = new GRBVar[bRounds][4+regime*2][4][2][2];
        DCb = new GRBVar[bRounds][4+regime*2][4][2];
        DCbAK = new GRBVar[bRounds][4+regime*2][4][2];
        Guesslf = new GRBVar[fRounds][4+regime*2][4];
        Guesslb = new GRBVar[bRounds][4+regime*2][4];
        // Initialization
        for (int round = 0; round < fRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 2; k++)
                        for (int l = 0; l < 2; l++) {
                            Af[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Af_" + round + "_" + i + "_" + j + "_" + k + "_" + l);
                        }
        for (int round = 0; round < fRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 2; k++)
                        for (int l = 0; l < 2; l++) {
                            SBf[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "SBf_" + round + "_" + i + "_" + j + "_" + k + "_" + l);
                        }
        for (int round = 0; round < fRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 2; k++)
                        for (int l = 0; l < 2; l++) {
                            MCf[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "MCf_" + round + "_" + i + "_" + j + "_" + k + "_" + l);
                        }
        for (int round = 0; round < fRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int l = 0; l < 2; l++) {
                        DCf[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DCf_" + round + "_" + i + "_" + j + "_" + l);
                    }
        for (int round = 0; round < fRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int l = 0; l < 2; l++) {
                        DCfAK[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DCfAK_" + round + "_" + i + "_" + j + "_" + l);
                    }
        for (int round = 0; round < fRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++) {
                    Guesslf[round][i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Guf_" + round + "_" + i + "_" + j);
                }

        for (int round = 0; round < bRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 2; k++)
                        for (int l = 0; l < 2; l++) {
                            Ab[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Ab_" + round + "_" + i + "_" + j + "_" + k + "_" + l);
                        }
        for (int round = 0; round < bRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 2; k++)
                        for (int l = 0; l < 2; l++) {
                            SBb[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "SBb_" + round + "_" + i + "_" + j + "_" + k + "_" + l);
                        }
        for (int round = 0; round < bRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 2; k++)
                        for (int l = 0; l < 2; l++) {
                            MCb[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "MCb_" + round + "_" + i + "_" + j + "_" + k + "_" + l);
                        }
        for (int round = 0; round < bRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int l = 0; l < 2; l++) {
                        DCb[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DCb_" + round + "_" + i + "_" + j + "_" + l);
                    }
        for (int round = 0; round < bRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++)
                    for (int l = 0; l < 2; l++) {
                        DCbAK[round][i][j][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DCbAK_" + round + "_" + i + "_" + j + "_" + l);
                    }
        for (int round = 0; round < bRounds; round++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++) {
                    Guesslb[round][i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Gub_" + round + "_" + i + "_" + j);
                }

        dom = new GRBVar[4+regime*2][4];
        for (int i = 0; i < 4+regime*2; i++)
            for (int k = 0; k < 4; k++) {
                dom[i][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "dom_" + i + "_" + k);
            }

        //fixed input
        //ArticleTrail();
        //if (regime == 1){
            //ArticleTrail_192();
        //}
        //if (regime == 2){
            //ArticleTrail_256();
        //}
        factory.addfixed_in(Af, Key.S, KeyStartr,regime);

        // Constraints
        for (int r = 0; r < fRounds; r++) {
            factory.addforward(Af, SBf, MCf, Key.RK, DCf, DCfAK, Guesslf, r, StateStartr, Rounds,regime);
        }


        for (int r = 0; r < bRounds; r++) {
            factory.addbackward(Ab, Af, SBb, MCb, Key.RK, DCb, DCbAK, Guesslb, r, bRounds, StateStartr, Rounds,regime);
        }


        factory.addMatch(SBf, MCf, Ab, Key.RK, DCfAK, dom, fRounds, StateStartr, Rounds,regime);



        objective = new GRBLinExpr();
        GRBLinExpr obj0expr = new GRBLinExpr();
        GRBLinExpr obj1expr = new GRBLinExpr();
        GRBLinExpr obj2expr = new GRBLinExpr();

        GRBLinExpr redgap = new GRBLinExpr();
        GRBLinExpr matchgap = new GRBLinExpr();

        GRBVar Obj = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj");

        obj = new GRBVar[5];
        obj[0] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj0"); //DoF of red
        obj[1] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj1"); //DoF of blue
        obj[2] = model.addVar(0.0, 1600.0, 0.0, GRB.INTEGER, "Obj2"); //DoM


        GRBLinExpr Consumedred = new GRBLinExpr();
        GRBLinExpr Consumedblue = new GRBLinExpr();
        GRBLinExpr Guess = new GRBLinExpr();
        for (int i = 0; i < 4+regime*2; i++)
            for (int j = 0; j < 4; j++) {
                obj0expr.addTerm(-2.0, Key.S[KeyStartr][i][j][0][0]);
                obj0expr.addConstant(2.0);

                obj1expr.addTerm(-2.0, Key.S[KeyStartr][i][j][1][1]);
                obj1expr.addConstant(2.0);
            }
            


        for (int i = 0; i < 4+regime*2; i++)
            for (int j = 0; j < 4; j++) {
                obj0expr.addTerm(-2.0, Af[0][i][j][0][0]);
                obj0expr.addConstant(2.0);

                obj1expr.addTerm(-2.0, Af[0][i][j][1][1]);
                obj1expr.addConstant(2.0);
            }

        for (int r = 0; r < fRounds; r++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++) {
                    obj0expr.addTerm(-2.0, DCf[r][i][j][0]);
                    obj1expr.addTerm(-2.0, DCf[r][i][j][1]);

                    obj0expr.addTerm(-2.0, DCfAK[r][i][j][0]);
                    obj1expr.addTerm(-2.0, DCfAK[r][i][j][1]);

                    obj0expr.addTerm(-1.0, Guesslf[r][i][j]);
                    obj1expr.addTerm(-1.0, Guesslf[r][i][j]);
                    obj2expr.addTerm(-1.0, Guesslf[r][i][j]);

                    Consumedblue.addTerm(1.0, DCf[r][i][j][1]);
                    Consumedblue.addTerm(1.0, DCfAK[r][i][j][1]);

                    Guess.addTerm(1.0, Guesslf[r][i][j]);
                }
        for (int r = 0; r < bRounds; r++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++) {
                    obj0expr.addTerm(-2.0, DCb[r][i][j][0]);
                    obj1expr.addTerm(-2.0, DCb[r][i][j][1]);

                    obj0expr.addTerm(-2.0, DCbAK[r][i][j][0]);
                    obj1expr.addTerm(-2.0, DCbAK[r][i][j][1]);

                    obj0expr.addTerm(-1.0, Guesslb[r][i][j]);
                    obj1expr.addTerm(-1.0, Guesslb[r][i][j]);
                    obj2expr.addTerm(-1.0, Guesslb[r][i][j]);

                    Consumedred.addTerm(1.0, DCb[r][i][j][0]);
                    Consumedred.addTerm(1.0, DCbAK[r][i][j][0]);

                    Guess.addTerm(1.0, Guesslb[r][i][j]);
                }
        for (int r = 0; r < keyRounds; r ++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++) {
                    obj0expr.addTerm(-2.0, Key.DCK[r][i][j][0]);
                    obj1expr.addTerm(-2.0, Key.DCK[r][i][j][1]);
                    if (r<KeyStartr){
                        Consumedred.addTerm(1.0, Key.DCK[r][i][j][0]);
                    }
                    if (r>KeyStartr){
                        Consumedblue.addTerm(1.0, Key.DCK[r][i][j][1]);
                    }
                }
        for (int r = 0; r < keyRounds; r ++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++) {
                    obj0expr.addTerm(-2.0, Key.DCS[r][i][j][0]);
                    obj1expr.addTerm(-2.0, Key.DCS[r][i][j][1]);
                    if (r<KeyStartr){
                        Consumedred.addTerm(1.0, Key.DCS[r][i][j][0]);
                    }
                    if (r>KeyStartr){
                        Consumedblue.addTerm(1.0, Key.DCS[r][i][j][1]);
                    }
            }
        for (int r = 0; r < keyRounds; r ++)
            for (int i = 0; i < 4+regime*2; i++)
                for (int j = 0; j < 4; j++) {
                    obj0expr.addTerm(-1.0, Key.GuessS[r][i][j]);
                    obj1expr.addTerm(-1.0, Key.GuessS[r][i][j]);
                    obj2expr.addTerm(-1.0, Key.GuessS[r][i][j]);
                }

        for (int i = 0; i < 4+regime*2; i++)
            for (int j = 0; j < 4; j++) {
                obj2expr.addTerm(2.0, dom[i][j]);

            }
        objective.addTerm(1.0, Obj);

        model.addConstr(Consumedred, GRB.EQUAL, 0, "");
        model.addConstr(Consumedblue, GRB.EQUAL, 0, "");
        model.addConstr(Guess, GRB.EQUAL, 0, "");
        model.addConstr(obj0expr, GRB.EQUAL, obj[0], "");
        model.addConstr(obj1expr, GRB.EQUAL, obj[1], "");
        model.addConstr(obj2expr, GRB.EQUAL, obj[2], "");

        //add gap between red/match and blue
        redgap.addTerm(1.0,obj[0]);
        redgap.addTerm(-1.0,obj[1]);
        model.addConstr(redgap, GRB.GREATER_EQUAL, 0, "");
        matchgap.addTerm(1.0,obj[2]);
        matchgap.addTerm(-1.0,obj[1]);
        model.addConstr(matchgap, GRB.GREATER_EQUAL, 0, "");

        //model.addConstr(obj[0], GRB.GREATER_EQUAL, 1, "");
        //model.addConstr(obj[1], GRB.GREATER_EQUAL, 1, "");
        //model.addConstr(obj[2], GRB.GREATER_EQUAL, 1, "");

        model.addConstr(objective, GRB.LESS_EQUAL, obj0expr, "");
        model.addConstr(objective, GRB.LESS_EQUAL, obj1expr, "");
        model.addConstr(objective, GRB.LESS_EQUAL, obj2expr, "");
        

        model.setObjective(objective, GRB.MAXIMIZE);
    }

    public List<MitmSolution> solve(final int nSolutions, final boolean nonOptimalSolutions, final int minObjValue, final int Threads) throws GRBException {
        model.read("tune1.prm");
        model.write("model.lp");
        model.set(GRB.IntParam.Threads, Threads);
        //if (minObjValue != -1)
        //  model.addConstr(objective, GRB.EQUAL, minObjValue, "objectiveFix");
        //model.set(GRB.IntParam.DualReductions, 0);
    /* try {
      FileWriter logfile = null;
      logfile = new FileWriter("cb.log");
    }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
    
    GRBVar[] vars = model.getVars();
    System.out.println (vars.length); 
    System.out.println (vars[0].get(GRB.StringAttr.VarName)); 
    Callback cb = new Callback(vars,logfile,Rounds);
    model.setCallback(cb);*/

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

            int[][][][][] AfValue = new int[fRounds][4+regime*2][4][2][2];
            int[][][][][] SBfValue = new int[fRounds][4+regime*2][4][2][2];
            int[][][][][] MCfValue = new int[fRounds][4+regime*2][4][2][2];
            int[][][][] DCfValue = new int[fRounds][4+regime*2][4][2];
            int[][][][] DCfAKValue = new int[fRounds][4+regime*2][4][2];
            int[][][][][] AbValue = new int[bRounds][4+regime*2][4][2][2];
            int[][][][][] SBbValue = new int[bRounds][4+regime*2][4][2][2];
            int[][][][][] MCbValue = new int[bRounds][4+regime*2][4][2][2];
            int[][][][] DCbValue = new int[bRounds][4+regime*2][4][2];
            int[][][][] DCbAKValue = new int[bRounds][4+regime*2][4][2];
            int[][] domValue = new int[4+regime*2][4];
            int[] objValue = new int[3];

            for (int round = 0; round < fRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int k = 0; k < 2; k++)
                            for (int l = 0; l < 2; l++) {
                                AfValue[round][i][j][k][l] = (int) Math.round(Af[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                            }
            for (int round = 0; round < bRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int k = 0; k < 2; k++)
                            for (int l = 0; l < 2; l++) {
                                AbValue[round][i][j][k][l] = (int) Math.round(Ab[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                            }
            for (int round = 0; round < fRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int k = 0; k < 2; k++)
                            for (int l = 0; l < 2; l++) {
                                SBfValue[round][i][j][k][l] = (int) Math.round(SBf[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                            }
            for (int round = 0; round < bRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int k = 0; k < 2; k++)
                            for (int l = 0; l < 2; l++) {
                                SBbValue[round][i][j][k][l] = (int) Math.round(SBb[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                            }
            for (int round = 0; round < fRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int k = 0; k < 2; k++)
                            for (int l = 0; l < 2; l++) {
                                MCfValue[round][i][j][k][l] = (int) Math.round(MCf[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                            }
            for (int round = 0; round < bRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int k = 0; k < 2; k++)
                            for (int l = 0; l < 2; l++) {
                                MCbValue[round][i][j][k][l] = (int) Math.round(MCb[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                            }
            for (int round = 0; round < fRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int l = 0; l < 2; l++) {
                            DCfValue[round][i][j][l] = (int) Math.round(DCf[round][i][j][l].get(GRB.DoubleAttr.Xn));
                        }
            for (int round = 0; round < bRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int l = 0; l < 2; l++) {
                            DCbValue[round][i][j][l] = (int) Math.round(DCb[round][i][j][l].get(GRB.DoubleAttr.Xn));
                        }
            for (int round = 0; round < fRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int l = 0; l < 2; l++) {
                            DCfAKValue[round][i][j][l] = (int) Math.round(DCfAK[round][i][j][l].get(GRB.DoubleAttr.Xn));
                        }
            for (int round = 0; round < bRounds; round++)
                for (int i = 0; i < 4+regime*2; i++)
                    for (int j = 0; j < 4; j++)
                        for (int l = 0; l < 2; l++) {
                            DCbAKValue[round][i][j][l] = (int) Math.round(DCbAK[round][i][j][l].get(GRB.DoubleAttr.Xn));
                        }

            for (int i = 0; i < 4+regime*2; i++)
                for (int k = 0; k < 4; k++)
                    domValue[i][k] = (int) Math.round(dom[i][k].get(GRB.DoubleAttr.Xn));

            for (int i = 0; i < 3; i++)
                objValue[i] = (int) Math.round(obj[i].get(GRB.DoubleAttr.Xn));

            return new MitmSolution(Rounds, keyRounds, regime, fRounds, bRounds, KeyStartr, StateStartr, (int) Math.round(model.get(GRB.DoubleAttr.PoolObjVal)), AfValue, SBfValue, MCfValue, DCfValue, DCfAKValue, AbValue, SBbValue, MCbValue, DCbValue, DCbAKValue, Key.getValue(), domValue, objValue);
        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            return null; // Can't access
        }
    }

    private void ArticleTrail() throws GRBException {
        if ((StateStartr == 4) & (fRounds == 6) & (KeyStartr == 5)) {
            System.out.println("Verify (StateStartr == 4) & (fRounds == 6) & (KeyStartr == 5)");
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 0 & j == 0) | (i == 1 & j == 1) | (i == 2 & j == 2) | (i == 3 & j == 3)) {
                        model.addConstr(Af[0][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][1][1], GRB.EQUAL, 0, "");
                    } else {
                        model.addConstr(Af[0][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Af[0][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][1][1], GRB.EQUAL, 1, "");
                    }

                }

            for (int round = 0; round < Rounds+1; round++){
                if (round == 5){
                    for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 4; j++) {
                            if ((i == 1 & j == 1) | (i == 2 & j == 2) | (i == 3 & j == 3)) {
                                model.addConstr(Key.RK[round][i][j][0][0], GRB.EQUAL, 1, "");
                                model.addConstr(Key.RK[round][i][j][0][1], GRB.EQUAL, 1, "");
                                model.addConstr(Key.RK[round][i][j][1][0], GRB.EQUAL, 1, "");
                                model.addConstr(Key.RK[round][i][j][1][1], GRB.EQUAL, 1, "");
                            } else {
                                model.addConstr(Key.RK[round][i][j][0][0], GRB.EQUAL, 1, "");
                                model.addConstr(Key.RK[round][i][j][0][1], GRB.EQUAL, 1, "");
                                model.addConstr(Key.RK[round][i][j][1][0], GRB.EQUAL, 1, "");
                                model.addConstr(Key.RK[round][i][j][1][1], GRB.EQUAL, 0, "");
                            }
                        }
                }
                else {
                    for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 4; j++) {
                            model.addConstr(Key.RK[round][i][j][0][0], GRB.EQUAL, 1, "");
                            model.addConstr(Key.RK[round][i][j][0][1], GRB.EQUAL, 1, "");
                            model.addConstr(Key.RK[round][i][j][1][0], GRB.EQUAL, 1, "");
                            model.addConstr(Key.RK[round][i][j][1][1], GRB.EQUAL, 0, "");

                        }
                }
            }

        }
    }

    private void ArticleTrail_192() throws GRBException {
        if ((StateStartr == 2) & (fRounds == 7) & (KeyStartr == 2)) {
            System.out.println("Verify (StateStartr == 2) & (fRounds == 7) & (KeyStartr == 2)");
            
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    model.addConstr(Af[0][i][j][0][0], GRB.EQUAL, 0, "");
                    model.addConstr(Af[0][i][j][0][1], GRB.EQUAL, 1, "");
                    model.addConstr(Af[0][i][j][1][0], GRB.EQUAL, 1, "");
                    model.addConstr(Af[0][i][j][1][1], GRB.EQUAL, 1, "");
                }
	/*
		    model.addConstr(Af[2][1][2][0][0], GRB.EQUAL, 1, "");
                    model.addConstr(Af[2][1][2][0][1], GRB.EQUAL, 1, "");
                    model.addConstr(Af[2][1][2][1][0], GRB.EQUAL, 1, "");
                    model.addConstr(Af[2][1][2][1][1], GRB.EQUAL, 1, "");
                    
                    model.addConstr(Af[3][2][2][0][0], GRB.EQUAL, 1, "");
                    model.addConstr(Af[3][2][2][0][1], GRB.EQUAL, 1, "");
                    model.addConstr(Af[3][2][2][1][0], GRB.EQUAL, 1, "");
                    model.addConstr(Af[3][2][2][1][1], GRB.EQUAL, 1, "");
	     for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 4; j++) {
                            if ((i == 0 & j == 2) | (i == 1 & j == 2) | (i == 1 & j == 3) | (i == 2 & j == 0) | (i == 3 & j == 1)) {
                                model.addConstr(MCf[1][i][j][0][0], GRB.EQUAL, 0, "");
                                model.addConstr(MCf[1][i][j][0][1], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[1][i][j][1][0], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[1][i][j][1][1], GRB.EQUAL, 1, "");
                            } else {
                                model.addConstr(MCf[1][i][j][0][0], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[1][i][j][0][1], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[1][i][j][1][0], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[1][i][j][1][1], GRB.EQUAL, 1, "");
                            }
                       }
                       
		for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 4; j++) {
                            if ((i == 2 & j == 0) | (i == 2 & j == 1) | (i == 2 & j == 3)) {
                                model.addConstr(MCf[2][i][j][0][0], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[2][i][j][0][1], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[2][i][j][1][0], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[2][i][j][1][1], GRB.EQUAL, 1, "");
                            } 
                            else if ((i == 2 & j == 2)) {
                                model.addConstr(MCf[2][i][j][0][0], GRB.EQUAL, 0, "");
                                model.addConstr(MCf[2][i][j][0][1], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[2][i][j][1][0], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[2][i][j][1][1], GRB.EQUAL, 1, "");
                            } 
                            else {
                                model.addConstr(MCf[2][i][j][0][0], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[2][i][j][0][1], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[2][i][j][1][0], GRB.EQUAL, 1, "");
                                model.addConstr(MCf[2][i][j][1][1], GRB.EQUAL, 0, "");
                            }
                       }
                       
           for (int i = 0; i < 6; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 0 & j == 0) | (i == 0 & j == 3) | (i == 2 & j == 1) | (i == 3 & j == 0))  {
                        model.addConstr(Key.S[2][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[2][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[2][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[2][i][j][1][1], GRB.EQUAL, 0, "");
                    } 
                    else if ((i == 1 & j == 1) | (i == 1 & j == 2))  {
                        model.addConstr(Key.S[2][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.S[2][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[2][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[2][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                    else {
                        model.addConstr(Key.S[2][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[2][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[2][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[2][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                }*/
                
            /*           
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 0 & j == 1) | (i == 1 & j == 1) | (i == 2 & j == 1))  {
                        //model.addConstr(Key.RK[0][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[0][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[0][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[0][i][j][1][1], GRB.EQUAL, 1, "");
                    } 
                    else if ((i == 0 ) | (i == 1))  {
                        //model.addConstr(Key.RK[0][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[0][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[0][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[0][i][j][1][1], GRB.EQUAL, 0, "");
                    }
                    else {
                        //model.addConstr(Key.RK[0][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[0][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[0][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[0][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                }


            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 0 & j == 2) | (i == 1 & j == 2) | (i == 2 & j == 1) | (i == 3 & j == 1))  {
                        //model.addConstr(Key.RK[1][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[1][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[1][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[1][i][j][1][1], GRB.EQUAL, 1, "");
                    } 
                    else if ((i == 0 & j == 0) | (i == 0 & j == 1) | (i == 0 & j == 3) | (i == 1 & j == 0) | (i == 2 & j == 0) | (i == 2 & j == 2) | (i == 2 & j == 3))  {
                        //model.addConstr(Key.RK[1][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[1][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[1][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[1][i][j][1][1], GRB.EQUAL, 0, "");
                    }
                    else {
                        //model.addConstr(Key.RK[1][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[1][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[1][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[1][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                }
                
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 2 & j == 2) | (i == 3 & j == 2))  {
                        //model.addConstr(Key.RK[2][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[2][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[2][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[2][i][j][1][1], GRB.EQUAL, 1, "");
                    } 
                    else if ((i == 2) | (i == 3))  {
                        //model.addConstr(Key.RK[2][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[2][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[2][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[2][i][j][1][1], GRB.EQUAL, 0, "");
                    }
                    else {
                        //model.addConstr(Key.RK[2][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[2][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[2][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[2][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                }

*/
/*
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if (i == 0 & j == 1)  {
                        model.addConstr(Key.RK[3][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[3][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[3][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[3][i][j][1][1], GRB.EQUAL, 1, "");
                    } else {
                        model.addConstr(Key.RK[3][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[3][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[3][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[3][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                }
                

            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 0 & j == 2) | (i == 1 & j == 2))  {
                        model.addConstr(Key.RK[4][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[4][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[4][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[4][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                    else if ((i == 0 & j == 0) | (i == 0 & j == 1) | (i == 0 & j == 3) | (i == 1 & j == 0) | (i == 2 & j == 3) | (i == 3 & j == 3))  {
                        model.addConstr(Key.RK[4][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[4][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[4][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[4][i][j][1][1], GRB.EQUAL, 0, "");
                    } 
                    else {
                        model.addConstr(Key.RK[4][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[4][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[4][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[4][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                }*/
               /* 
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 2 & j == 2))  {
                        //model.addConstr(Key.RK[5][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[5][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[5][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[5][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                    else if ((i == 0 & j == 3) | (i == 1 & j == 3) | (i == 2 & j == 0) | (i == 2 & j == 1) | (i == 2 & j == 3) | (i == 3 & j == 0) | (i == 3 & j == 1) | (i == 3 & j == 3))  {
                        //model.addConstr(Key.RK[5][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[5][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[5][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[5][i][j][1][1], GRB.EQUAL, 0, "");
                    } 
                    else {
                        //model.addConstr(Key.RK[5][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[5][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[5][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[5][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                }
		
	for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if (j == 1)  {
                        //model.addConstr(Key.RK[6][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[6][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[6][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[6][i][j][1][1], GRB.EQUAL, 1, "");
                    } 
                    else {
                        //model.addConstr(Key.RK[6][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[6][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[6][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[6][i][j][1][1], GRB.EQUAL, 0, "");
                    }
                }
                
         for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 0 & j == 2) | (i == 1 & j == 2) | (i == 2 & j == 1) | (i == 3 & j == 1))  {
                        //model.addConstr(Key.RK[7][i][j][0][0], GRB.EQUAL, 0, "");
                        //model.addConstr(Key.RK[7][i][j][0][1], GRB.EQUAL, 0, "");
                        //model.addConstr(Key.RK[7][i][j][1][0], GRB.EQUAL, 0, "");
                        //model.addConstr(Key.RK[7][i][j][1][1], GRB.EQUAL, 0, "");
                    } 
                    else {
                        //model.addConstr(Key.RK[7][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[7][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[7][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[7][i][j][1][1], GRB.EQUAL, 0, "");
                    }
                }
          for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((j == 1) | (i == 2 & j == 2) | (i == 3 & j == 2))  {
                        //model.addConstr(Key.RK[8][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[8][i][j][0][1], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[8][i][j][1][0], GRB.EQUAL, 0, "");
                        //model.addConstr(Key.RK[8][i][j][1][1], GRB.EQUAL, 0, "");
                    } 
                    else {
                        //model.addConstr(Key.RK[8][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[8][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[8][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[8][i][j][1][1], GRB.EQUAL, 0, "");
                    }
                }
                
          for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((j == 0) |(j == 1))  {
                        //model.addConstr(Key.RK[9][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[9][i][j][0][1], GRB.EQUAL, 0, "");
                        model.addConstr(Key.RK[9][i][j][1][0], GRB.EQUAL, 0, "");
                        //model.addConstr(Key.RK[9][i][j][1][1], GRB.EQUAL, 0, "");
                    } 
                    else {
                        //model.addConstr(Key.RK[9][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[9][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.RK[9][i][j][1][0], GRB.EQUAL, 1, "");
                        //model.addConstr(Key.RK[9][i][j][1][1], GRB.EQUAL, 0, "");
                    }
                }*/

        }
        }
        
        
    private void ArticleTrail_256() throws GRBException {
        if ((StateStartr == 4) & (fRounds == 5) & (KeyStartr == 3)) {
            System.out.println("Verify (StateStartr == 4) & (fRounds == 5) & (KeyStartr == 3)");
            
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if ((i == 0 & j == 1) | (i == 0 & j == 2) | (i == 1 & j == 2) | (i == 1 & j == 3) | (i == 2 & j == 0) | (i == 2 & j == 3) | (i == 3 & j == 0) | (i == 3 & j == 1)) {
                        model.addConstr(Af[0][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][1][1], GRB.EQUAL, 0, "");
                    } else {
                        model.addConstr(Af[0][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Af[0][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Af[0][i][j][1][1], GRB.EQUAL, 1, "");
                    }

                }
                
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if (i == 0 & j == 3)  {
                        model.addConstr(Key.S[3][i][j][0][0], GRB.EQUAL, 0, "");
                        model.addConstr(Key.S[3][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[3][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[3][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                    else if ((i == 1 & j == 1) | (i == 2 & j == 3) | (i == 3 & j == 1) | (i == 3 & j == 2) | (i == 3 & j == 3) | (i == 4 & j == 3) | (i == 5 & j == 1) | (i == 5 & j == 2) | (i == 5 & j == 3) | (i == 6 & j == 3) | (i == 7 & j == 0)| (i == 7 & j == 1))  {
                        model.addConstr(Key.S[3][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[3][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[3][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[3][i][j][1][1], GRB.EQUAL, 1, "");
                    }
                    else {
                        model.addConstr(Key.S[3][i][j][0][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[3][i][j][0][1], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[3][i][j][1][0], GRB.EQUAL, 1, "");
                        model.addConstr(Key.S[3][i][j][1][1], GRB.EQUAL, 0, "");
                    }
                }
              
   

        }        
        
    }
}
