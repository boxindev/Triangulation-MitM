package mitmsearch.mitm;

import gurobi.*;

public class MitMKey {
  private final GRBModel model;
  private final int Rounds;
  private final int keyRounds;
  private final int Startr;
  private final int regime;
  public  final GRBVar[][][][][] RK;
  public  final GRBVar[][][][][] S;
  public  final GRBVar[][][][][] K;
  public  final GRBVar[][][][] DCK;
  public  final GRBVar[][][][] DCS;
  public  final GRBVar[][][] GuessS;


  public MitMKey(final GRBModel model, final int Rounds, final int keyRounds, final int regime, final int Startr) throws GRBException {
    this.model = model;
    this.Rounds = Rounds;
    this.keyRounds = keyRounds;
    this.Startr = Startr;
    this.regime = regime;
    S = new GRBVar[keyRounds][4+regime*2][4][2][2];
    K = new GRBVar[keyRounds][4+regime*2][4][2][2];
    RK = new GRBVar[Rounds+1][4+regime*2][4][2][2];
    DCK = new GRBVar[keyRounds][4+regime*2][4][2];
    DCS = new GRBVar[keyRounds][4+regime*2][4][2];
    GuessS = new GRBVar[keyRounds][4+regime*2][4];
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++) {
              S[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "S"+round+i+j+k+l);
              K[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "K"+round+i+j+k+l);
            }
    for (int round = 0; round < Rounds+1; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++)  {
              RK[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "RK"+round+i+j+k+l);
            }
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)  {
            DCK[round][i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DCK"+round+i+j+k);
          }
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)  {
            DCS[round][i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DCS"+round+i+j+k);
          }
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++) {
          GuessS[round][i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "GS"+round+i+j);
        }


    if (regime == 0)
      //create128();
      create128_t();
    else if (regime == 1)
      //create192();
      create192_t();
    else
      //create256();
      create256_t();
  }
  public void create128_t() throws GRBException {
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++) {
              model.addConstr(S[round][i][j][k][l], GRB.EQUAL, 1, "");
              model.addConstr(K[round][i][j][k][l], GRB.EQUAL, 1, "");
            }

    for (int round = 0; round < Rounds+1; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++)  {
              model.addConstr(RK[round][i][j][k][l], GRB.EQUAL, 1, "");
            }
  }
  public void create192_t() throws GRBException {
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++) {
              model.addConstr(S[round][i][j][k][l], GRB.EQUAL, 1, "");
              model.addConstr(K[round][i][j][k][l], GRB.EQUAL, 1, "");
            }

    for (int round = 0; round < Rounds+1; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++)  {
              model.addConstr(RK[round][i][j][k][l], GRB.EQUAL, 1, "");
            }
  }
  public void create256_t() throws GRBException {
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++) {
              model.addConstr(S[round][i][j][k][l], GRB.EQUAL, 1, "");
              model.addConstr(K[round][i][j][k][l], GRB.EQUAL, 1, "");
            }

    for (int round = 0; round < Rounds+1; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++)  {
              model.addConstr(RK[round][i][j][k][l], GRB.EQUAL, 1, "");
            }
  }
  public void create128() throws GRBException {
    addStoRK128(S, K, RK, DCK);

    final GRBVar[][][][] Sboxout = new GRBVar[Rounds+1][4][2][2];
    for (int round = 0; round < Rounds+1; round++)
      for (int i = 0; i < 4; i++)
        for (int k = 0; k < 2; k++)
          for (int l = 0; l < 2; l++) {
            Sboxout[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Sboxout"+round+i+k+l);
          }
    //S=S'
    for (int round = 0; round < Startr; round++) {
      for (int k = 0; k < 2; k++) {
        for (int l = 0; l < 2; l++) {
          model.addConstr(S[round][0][0][k][l], GRB.EQUAL, S[round+1][1][3][k][l], "");
          model.addConstr(S[round][0][2][k][l], GRB.EQUAL, S[round+1][1][1][k][l], "");
          model.addConstr(S[round][0][3][k][l], GRB.EQUAL, S[round+1][1][2][k][l], "");
          model.addConstr(S[round][1][0][k][l], GRB.EQUAL, S[round+1][2][3][k][l], "");
          model.addConstr(S[round][1][2][k][l], GRB.EQUAL, S[round+1][2][1][k][l], "");
          model.addConstr(S[round][1][3][k][l], GRB.EQUAL, S[round+1][2][2][k][l], "");
          model.addConstr(S[round][2][0][k][l], GRB.EQUAL, S[round+1][3][3][k][l], "");
          model.addConstr(S[round][2][2][k][l], GRB.EQUAL, S[round+1][3][1][k][l], "");
          model.addConstr(S[round][2][3][k][l], GRB.EQUAL, S[round+1][3][2][k][l], "");
          model.addConstr(S[round][3][0][k][l], GRB.EQUAL, S[round+1][0][3][k][l], "");
          model.addConstr(S[round][3][2][k][l], GRB.EQUAL, S[round+1][0][1][k][l], "");
          model.addConstr(S[round][3][3][k][l], GRB.EQUAL, S[round+1][0][2][k][l], "");
        }
        //DCS
        for (int i = 0; i < 4; i++) {
          model.addConstr(DCS[round][i][0][k], GRB.EQUAL, 0, "");
          model.addConstr(DCS[round][i][2][k], GRB.EQUAL, 0, "");
          model.addConstr(DCS[round][i][3][k], GRB.EQUAL, 0, "");
        }
      }
      //xor S()
      addSbox(S[round+1][1][3], Sboxout[round][0], GuessS[round+1][1][3]);
      addtwoXor(S[round][0][1], DCS[round][0][1], S[round+1][1][0], Sboxout[round][0]);
      addSbox(S[round+1][2][3], Sboxout[round][1], GuessS[round+1][2][3]);
      addtwoXor(S[round][1][1], DCS[round][1][1], S[round+1][2][0], Sboxout[round][1]);
      addSbox(S[round+1][3][3], Sboxout[round][2], GuessS[round+1][3][3]);
      addtwoXor(S[round][2][1], DCS[round][2][1], S[round+1][3][0], Sboxout[round][2]);
      addSbox(S[round+1][0][3], Sboxout[round][3], GuessS[round+1][0][3]);
      addtwoXor(S[round][3][1], DCS[round][3][1], S[round+1][0][0], Sboxout[round][3]);
      //GuessS
      /*for (int i = 0; i < 4; i++)
        for (int j = 0; j < 3; j++) {
        model.addConstr(GuessS[round+1][i][j], GRB.EQUAL, 0, "");
      }*/
    }
    //S'=S
    for (int round = Startr+1; round < Rounds+1; round++) {
      for (int k = 0; k < 2; k++) {
        for (int l = 0; l < 2; l++) {
          model.addConstr(S[round][0][1][k][l], GRB.EQUAL, S[round-1][3][2][k][l], "");
          model.addConstr(S[round][0][2][k][l], GRB.EQUAL, S[round-1][3][3][k][l], "");
          model.addConstr(S[round][0][3][k][l], GRB.EQUAL, S[round-1][3][0][k][l], "");
          model.addConstr(S[round][1][1][k][l], GRB.EQUAL, S[round-1][0][2][k][l], "");
          model.addConstr(S[round][1][2][k][l], GRB.EQUAL, S[round-1][0][3][k][l], "");
          model.addConstr(S[round][1][3][k][l], GRB.EQUAL, S[round-1][0][0][k][l], "");
          model.addConstr(S[round][2][1][k][l], GRB.EQUAL, S[round-1][1][2][k][l], "");
          model.addConstr(S[round][2][2][k][l], GRB.EQUAL, S[round-1][1][3][k][l], "");
          model.addConstr(S[round][2][3][k][l], GRB.EQUAL, S[round-1][1][0][k][l], "");
          model.addConstr(S[round][3][1][k][l], GRB.EQUAL, S[round-1][2][2][k][l], "");
          model.addConstr(S[round][3][2][k][l], GRB.EQUAL, S[round-1][2][3][k][l], "");
          model.addConstr(S[round][3][3][k][l], GRB.EQUAL, S[round-1][2][0][k][l], "");
        }
        //DCS
        for (int i = 0; i < 4; i++)
          for (int j = 1; j < 4; j++)  {
            model.addConstr(DCS[round][i][j][k], GRB.EQUAL, 0, "");
          }
      }
      //xor S'()
      addSbox(S[round-1][3][0], Sboxout[round][0], GuessS[round-1][3][0]);
      addtwoXor(S[round][0][0], DCS[round][0][0], S[round-1][3][1], Sboxout[round][0]);
      addSbox(S[round-1][0][0], Sboxout[round][1], GuessS[round-1][0][0]);
      addtwoXor(S[round][1][0], DCS[round][1][0], S[round-1][0][1], Sboxout[round][1]);
      addSbox(S[round-1][1][0], Sboxout[round][2], GuessS[round-1][1][0]);
      addtwoXor(S[round][2][0], DCS[round][2][0], S[round-1][1][1], Sboxout[round][2]);
      addSbox(S[round-1][2][0], Sboxout[round][3], GuessS[round-1][2][0]);
      addtwoXor(S[round][3][0], DCS[round][3][0], S[round-1][2][1], Sboxout[round][3]);
      //GuessS
      /*for (int i = 0; i < 4; i++)
        for (int j = 1; j < 4; j++) {
        model.addConstr(GuessS[round-1][i][j], GRB.EQUAL, 0, "");
      }*/
    }
  }

  public void addStoRK128(GRBVar[][][][][] S, GRBVar[][][][][] K, GRBVar[][][][][] RK, GRBVar[][][][] DCK) throws GRBException {
    for (int r = 0; r < Rounds+1; r++) {
      addfourXor(RK[r][0][0], DCK[r][0][0], S[r][0][3], S[r][1][2], S[r][2][1], S[r][3][0]);
      addfourXor(RK[r][0][1], DCK[r][0][1], S[r][0][2], S[r][1][1], S[r][2][0], S[r][3][3]);
      addfourXor(RK[r][0][2], DCK[r][0][2], S[r][0][1], S[r][1][0], S[r][2][3], S[r][3][2]);
      addfourXor(RK[r][0][3], DCK[r][0][3], S[r][0][0], S[r][1][3], S[r][2][2], S[r][3][1]);
      addtwoXor(RK[r][1][0], DCK[r][1][0], S[r][1][2], S[r][3][0]);
      addtwoXor(RK[r][1][1], DCK[r][1][1], S[r][0][2], S[r][2][0]);
      addtwoXor(RK[r][1][2], DCK[r][1][2], S[r][1][0], S[r][3][2]);
      addtwoXor(RK[r][1][3], DCK[r][1][3], S[r][0][0], S[r][2][2]);
      addtwoXor(RK[r][2][0], DCK[r][2][0], S[r][0][3], S[r][3][0]);
      addtwoXor(RK[r][2][1], DCK[r][2][1], S[r][2][0], S[r][3][3]);
      addtwoXor(RK[r][2][2], DCK[r][2][2], S[r][1][0], S[r][2][3]);
      addtwoXor(RK[r][2][3], DCK[r][2][3], S[r][0][0], S[r][1][3]);
      for (int k = 0; k < 2; k++) {
        for (int l = 0; l < 2; l++) {
          model.addConstr(RK[r][3][0][k][l], GRB.EQUAL, S[r][3][0][k][l], "");
          model.addConstr(RK[r][3][1][k][l], GRB.EQUAL, S[r][2][0][k][l], "");
          model.addConstr(RK[r][3][2][k][l], GRB.EQUAL, S[r][1][0][k][l], "");
          model.addConstr(RK[r][3][3][k][l], GRB.EQUAL, S[r][0][0][k][l], "");
        }
        for (int j = 0; j < 4; j++) {
          model.addConstr(DCK[r][3][j][k], GRB.EQUAL, 0, "");
        }
      }
    }
  }

  public void create192() throws GRBException {
    addStoRK192(S, K, RK, DCK);

    final GRBVar[][][][] Sboxout = new GRBVar[keyRounds][4][2][2];
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4; i++)
        for (int k = 0; k < 2; k++)
          for (int l = 0; l < 2; l++) {
            Sboxout[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Sboxout"+round+i+k+l);
          }
    //S=S'
    for (int round = 0; round < Startr; round++) {
      for (int k = 0; k < 2; k++) {
        for (int l = 0; l < 2; l++) {
          model.addConstr(S[round][0][0][k][l], GRB.EQUAL, S[round+1][3][0][k][l], "");
          model.addConstr(S[round][0][1][k][l], GRB.EQUAL, S[round+1][3][1][k][l], "");
          model.addConstr(S[round][0][2][k][l], GRB.EQUAL, S[round+1][3][2][k][l], "");
          //6-8
          model.addConstr(S[round][1][2][k][l], GRB.EQUAL, S[round+1][4][2][k][l], "");
          model.addConstr(S[round][1][3][k][l], GRB.EQUAL, S[round+1][4][3][k][l], "");
          model.addConstr(S[round][2][0][k][l], GRB.EQUAL, S[round+1][5][0][k][l], "");
          //15-17
          model.addConstr(S[round][3][3][k][l], GRB.EQUAL, S[round+1][0][3][k][l], "");
          model.addConstr(S[round][4][0][k][l], GRB.EQUAL, S[round+1][1][0][k][l], "");
          model.addConstr(S[round][4][1][k][l], GRB.EQUAL, S[round+1][1][1][k][l], "");
          //21-23
          model.addConstr(S[round][5][1][k][l], GRB.EQUAL, S[round+1][2][1][k][l], "");
          model.addConstr(S[round][5][2][k][l], GRB.EQUAL, S[round+1][2][2][k][l], "");
          model.addConstr(S[round][5][3][k][l], GRB.EQUAL, S[round+1][2][3][k][l], "");
        }
        //DCS
        model.addConstr(DCS[round][0][0][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][0][1][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][0][2][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][1][2][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][1][3][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][2][0][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][3][3][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][4][0][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][4][1][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][5][1][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][5][2][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][5][3][k], GRB.EQUAL, 0, "");
      }
      //3-5
      addtwoXor(S[round][0][3], DCS[round][0][3], S[round+1][3][3], S[round+1][4][0]);
      addtwoXor(S[round][1][0], DCS[round][1][0], S[round+1][4][0], S[round+1][4][1]);
      addSbox(S[round+1][4][2], Sboxout[round][0], GuessS[round+1][4][2]);
      addtwoXor(S[round][1][1], DCS[round][1][1], S[round+1][4][1], Sboxout[round][0]);
      //9-11
      addtwoXor(S[round][2][1], DCS[round][2][1], S[round+1][5][1], S[round+1][5][2]);
      addtwoXor(S[round][2][2], DCS[round][2][2], S[round+1][5][2], S[round+1][5][3]);
      addSbox(S[round+1][3][0], Sboxout[round][1], GuessS[round+1][3][0]);
      addtwoXor(S[round][2][3], DCS[round][2][3], S[round+1][5][3], Sboxout[round][1]);
      //12-14
      addtwoXor(S[round][3][0], DCS[round][3][0], S[round+1][0][0], S[round+1][0][1]);
      addtwoXor(S[round][3][1], DCS[round][3][1], S[round+1][0][1], S[round+1][0][2]);
      addSbox(S[round+1][0][3], Sboxout[round][2], GuessS[round+1][0][3]);
      addtwoXor(S[round][3][2], DCS[round][3][2], S[round+1][0][2], Sboxout[round][2]);
      //18-20
      addtwoXor(S[round][4][2], DCS[round][4][2], S[round+1][1][2], S[round+1][1][3]);
      addtwoXor(S[round][4][3], DCS[round][4][3], S[round+1][1][3], S[round+1][2][0]);
      addSbox(S[round+1][2][1], Sboxout[round][3], GuessS[round+1][2][1]);
      addtwoXor(S[round][5][0], DCS[round][5][0], S[round+1][2][0], Sboxout[round][3]);
      //GuessS
      /*for (int j = 0; j < 4; j++) {
        model.addConstr(GuessS[round+1][1][j], GRB.EQUAL, 0, "");
        model.addConstr(GuessS[round+1][5][j], GRB.EQUAL, 0, "");
        if (j != 2)
          model.addConstr(GuessS[round+1][4][j], GRB.EQUAL, 0, "");
        if (j != 0)
          model.addConstr(GuessS[round+1][3][j], GRB.EQUAL, 0, "");
        if (j != 3)
          model.addConstr(GuessS[round+1][0][j], GRB.EQUAL, 0, "");
        if (j != 1)
          model.addConstr(GuessS[round+1][2][j], GRB.EQUAL, 0, "");

      }*/
    }
    //S'=S
    for (int round = Startr+1; round < keyRounds; round++) {
      for (int k = 0; k < 2; k++) {
        for (int l = 0; l < 2; l++) {
          //3-5
          model.addConstr(S[round][0][3][k][l], GRB.EQUAL, S[round-1][3][3][k][l], "");
          model.addConstr(S[round][1][0][k][l], GRB.EQUAL, S[round-1][4][0][k][l], "");
          model.addConstr(S[round][1][1][k][l], GRB.EQUAL, S[round-1][4][1][k][l], "");
          //9-11
          model.addConstr(S[round][2][1][k][l], GRB.EQUAL, S[round-1][5][1][k][l], "");
          model.addConstr(S[round][2][2][k][l], GRB.EQUAL, S[round-1][5][2][k][l], "");
          model.addConstr(S[round][2][3][k][l], GRB.EQUAL, S[round-1][5][3][k][l], "");
          //12-14
          model.addConstr(S[round][3][0][k][l], GRB.EQUAL, S[round-1][0][0][k][l], "");
          model.addConstr(S[round][3][1][k][l], GRB.EQUAL, S[round-1][0][1][k][l], "");
          model.addConstr(S[round][3][2][k][l], GRB.EQUAL, S[round-1][0][2][k][l], "");
          //18-20
          model.addConstr(S[round][4][2][k][l], GRB.EQUAL, S[round-1][1][2][k][l], "");
          model.addConstr(S[round][4][3][k][l], GRB.EQUAL, S[round-1][1][3][k][l], "");
          model.addConstr(S[round][5][0][k][l], GRB.EQUAL, S[round-1][2][0][k][l], "");
        }
        //DCS
        model.addConstr(DCS[round][0][3][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][1][0][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][1][1][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][2][1][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][2][2][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][2][3][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][3][0][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][3][1][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][3][2][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][4][2][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][4][3][k], GRB.EQUAL, 0, "");
        model.addConstr(DCS[round][5][0][k], GRB.EQUAL, 0, "");
      }
      //0-2
      addSbox(S[round-1][3][3], Sboxout[round][0], GuessS[round-1][3][3]);
      addtwoXor(S[round][0][0], DCS[round][0][0], S[round-1][3][0], S[round][0][1]);
      addtwoXor(S[round][0][1], DCS[round][0][1], S[round-1][3][1], S[round][0][2]);
      addtwoXor(S[round][0][2], DCS[round][0][2], S[round-1][3][2], Sboxout[round][0]);
      //6-8
      addSbox(S[round-1][5][1], Sboxout[round][1], GuessS[round-1][5][1]);
      addtwoXor(S[round][1][2], DCS[round][1][2], S[round-1][4][2], S[round][1][3]);
      addtwoXor(S[round][1][3], DCS[round][1][3], S[round-1][4][3], S[round][2][0]);
      addtwoXor(S[round][2][0], DCS[round][2][0], S[round-1][5][0], Sboxout[round][1]);
      //15-17
      addSbox(S[round-1][1][2], Sboxout[round][2], GuessS[round-1][1][2]);
      addtwoXor(S[round][3][3], DCS[round][3][3], S[round-1][0][3], S[round][4][0]);
      addtwoXor(S[round][4][0], DCS[round][4][0], S[round-1][1][0], S[round][4][1]);
      addtwoXor(S[round][4][1], DCS[round][4][1], S[round-1][1][1], Sboxout[round][2]);
      //21-23
      addSbox(S[round-1][0][0], Sboxout[round][3], GuessS[round-1][0][0]);
      addtwoXor(S[round][5][1], DCS[round][5][1], S[round-1][2][1], S[round][5][2]);
      addtwoXor(S[round][5][2], DCS[round][5][2], S[round-1][2][2], S[round][5][3]);
      addtwoXor(S[round][5][3], DCS[round][5][3], S[round-1][2][3], Sboxout[round][3]);
      //GuessS
      /*for (int j = 0; j < 4; j++) {
        model.addConstr(GuessS[round-1][2][j], GRB.EQUAL, 0, "");
        model.addConstr(GuessS[round-1][4][j], GRB.EQUAL, 0, "");
        if (j != 3)
          model.addConstr(GuessS[round-1][3][j], GRB.EQUAL, 0, "");
        if (j != 1)
          model.addConstr(GuessS[round-1][5][j], GRB.EQUAL, 0, "");
        if (j != 2)
          model.addConstr(GuessS[round-1][1][j], GRB.EQUAL, 0, "");
        if (j != 0)
          model.addConstr(GuessS[round-1][0][j], GRB.EQUAL, 0, "");

      }*/
    }
  }

  public void addStoRK192(GRBVar[][][][][] S, GRBVar[][][][][] K, GRBVar[][][][][] RK, GRBVar[][][][] DCK) throws GRBException {
    for (int r = 0; r < keyRounds; r++) {
      addtwoXor(K[r][0][0], DCK[r][0][0], S[r][0][2], S[r][3][2]);
      addtwoXor(K[r][0][1], DCK[r][0][1], S[r][1][1], S[r][4][1]);
      addtwoXor(K[r][0][2], DCK[r][0][2], S[r][2][0], S[r][5][0]);
      addtwoXor(K[r][0][3], DCK[r][0][3], S[r][2][3], S[r][5][3]);

      addtwoXor(K[r][2][0], DCK[r][2][0], S[r][0][1], S[r][3][1]);
      addtwoXor(K[r][2][1], DCK[r][2][1], S[r][1][0], S[r][4][0]);
      addtwoXor(K[r][2][2], DCK[r][2][2], S[r][1][3], S[r][4][3]);
      addtwoXor(K[r][2][3], DCK[r][2][3], S[r][2][2], S[r][5][2]);

      addtwoXor(K[r][4][0], DCK[r][4][0], S[r][0][0], S[r][3][0]);
      addtwoXor(K[r][4][1], DCK[r][4][1], S[r][0][3], S[r][3][3]);
      addtwoXor(K[r][4][2], DCK[r][4][2], S[r][1][2], S[r][4][2]);
      addtwoXor(K[r][4][3], DCK[r][4][3], S[r][2][1], S[r][5][1]);

      for (int k = 0; k < 2; k++) {
        for (int l = 0; l < 2; l++) {
          model.addConstr(K[r][1][0][k][l], GRB.EQUAL, S[r][0][2][k][l], "");
          model.addConstr(K[r][1][1][k][l], GRB.EQUAL, S[r][4][1][k][l], "");
          model.addConstr(K[r][1][2][k][l], GRB.EQUAL, S[r][2][0][k][l], "");
          model.addConstr(K[r][1][3][k][l], GRB.EQUAL, S[r][5][3][k][l], "");

          model.addConstr(K[r][3][0][k][l], GRB.EQUAL, S[r][0][1][k][l], "");
          model.addConstr(K[r][3][1][k][l], GRB.EQUAL, S[r][4][0][k][l], "");
          model.addConstr(K[r][3][2][k][l], GRB.EQUAL, S[r][1][3][k][l], "");
          model.addConstr(K[r][3][3][k][l], GRB.EQUAL, S[r][5][2][k][l], "");

          model.addConstr(K[r][5][0][k][l], GRB.EQUAL, S[r][0][0][k][l], "");
          model.addConstr(K[r][5][1][k][l], GRB.EQUAL, S[r][3][3][k][l], "");
          model.addConstr(K[r][5][2][k][l], GRB.EQUAL, S[r][1][2][k][l], "");
          model.addConstr(K[r][5][3][k][l], GRB.EQUAL, S[r][5][1][k][l], "");
        }

        for (int j = 0; j < 4; j++) {
          model.addConstr(DCK[r][1][j][k], GRB.EQUAL, 0, "");
          model.addConstr(DCK[r][3][j][k], GRB.EQUAL, 0, "");
          model.addConstr(DCK[r][5][j][k], GRB.EQUAL, 0, "");
        }
      }
    }
    for (int r = 0; r < Rounds+1; r++)
      for (int i = 0; i < 4; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++)  {
              model.addConstr(RK[r][i][j][k][l], GRB.EQUAL, K[(4*r+i)/6][(4*r+i)%6][j][k][l], "");
            }
  }

  public void create256() throws GRBException {
    addStoRK256(S, K, RK, DCK);

    final GRBVar[][][][] Sboxout = new GRBVar[keyRounds][8][2][2];
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 8; i++)
        for (int k = 0; k < 2; k++)
          for (int l = 0; l < 2; l++) {
            Sboxout[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Sboxout"+round+i+k+l);
          }
    //S=S'
    for (int round = 0; round < Startr; round++) {
      for (int k = 0; k < 2; k++)
        for (int l = 0; l < 2; l++) {
          //0-7
          model.addConstr(S[round][0][0][k][l], GRB.EQUAL, S[round+1][3][2][k][l], "");
          model.addConstr(S[round][0][1][k][l], GRB.EQUAL, S[round+1][3][3][k][l], "");
          model.addConstr(S[round][1][0][k][l], GRB.EQUAL, S[round+1][2][2][k][l], "");
          model.addConstr(S[round][1][1][k][l], GRB.EQUAL, S[round+1][2][3][k][l], "");
          model.addConstr(S[round][1][2][k][l], GRB.EQUAL, S[round+1][3][0][k][l], "");
          model.addConstr(S[round][1][3][k][l], GRB.EQUAL, S[round+1][3][1][k][l], "");
          //8-15
          model.addConstr(S[round][2][0][k][l], GRB.EQUAL, S[round+1][5][2][k][l], "");
          model.addConstr(S[round][2][1][k][l], GRB.EQUAL, S[round+1][5][3][k][l], "");
          model.addConstr(S[round][3][0][k][l], GRB.EQUAL, S[round+1][4][2][k][l], "");
          model.addConstr(S[round][3][1][k][l], GRB.EQUAL, S[round+1][4][3][k][l], "");
          model.addConstr(S[round][3][2][k][l], GRB.EQUAL, S[round+1][5][0][k][l], "");
          model.addConstr(S[round][3][3][k][l], GRB.EQUAL, S[round+1][5][1][k][l], "");
          //16-23
          model.addConstr(S[round][4][0][k][l], GRB.EQUAL, S[round+1][7][2][k][l], "");
          model.addConstr(S[round][4][1][k][l], GRB.EQUAL, S[round+1][7][3][k][l], "");
          model.addConstr(S[round][5][0][k][l], GRB.EQUAL, S[round+1][6][2][k][l], "");
          model.addConstr(S[round][5][1][k][l], GRB.EQUAL, S[round+1][6][3][k][l], "");
          model.addConstr(S[round][5][2][k][l], GRB.EQUAL, S[round+1][7][0][k][l], "");
          model.addConstr(S[round][5][3][k][l], GRB.EQUAL, S[round+1][7][1][k][l], "");
          //24-31
          model.addConstr(S[round][6][0][k][l], GRB.EQUAL, S[round+1][1][2][k][l], "");
          model.addConstr(S[round][6][1][k][l], GRB.EQUAL, S[round+1][1][3][k][l], "");
          model.addConstr(S[round][7][0][k][l], GRB.EQUAL, S[round+1][0][2][k][l], "");
          model.addConstr(S[round][7][1][k][l], GRB.EQUAL, S[round+1][0][3][k][l], "");
          model.addConstr(S[round][7][2][k][l], GRB.EQUAL, S[round+1][1][0][k][l], "");
          model.addConstr(S[round][7][3][k][l], GRB.EQUAL, S[round+1][1][1][k][l], "");
        }
      //2-3
      addSbox(S[round+1][3][3], Sboxout[round][0], GuessS[round+1][3][3]);
      addtwoXor(S[round][0][2], DCS[round][0][2], S[round+1][2][0], Sboxout[round][0]);
      addSbox(S[round+1][2][0], Sboxout[round][1], GuessS[round+1][2][0]);
      addtwoXor(S[round][0][3], DCS[round][0][3], S[round+1][2][1], Sboxout[round][1]);
      //10-11
      addSbox(S[round+1][5][3], Sboxout[round][2], GuessS[round+1][5][3]);
      addtwoXor(S[round][2][2], DCS[round][2][2], S[round+1][4][0], Sboxout[round][2]);
      addSbox(S[round+1][4][0], Sboxout[round][3], GuessS[round+1][4][0]);
      addtwoXor(S[round][2][3], DCS[round][2][3], S[round+1][4][1], Sboxout[round][3]);
      //18-19
      addSbox(S[round+1][7][3], Sboxout[round][4], GuessS[round+1][7][3]);
      addtwoXor(S[round][4][2], DCS[round][4][2], S[round+1][6][0], Sboxout[round][4]);
      addSbox(S[round+1][6][0], Sboxout[round][5], GuessS[round+1][6][0]);
      addtwoXor(S[round][4][3], DCS[round][4][3], S[round+1][6][1], Sboxout[round][5]);
      //26-27
      addSbox(S[round+1][1][3], Sboxout[round][6], GuessS[round+1][1][3]);
      addtwoXor(S[round][6][2], DCS[round][6][2], S[round+1][0][0], Sboxout[round][6]);
      addSbox(S[round+1][0][0], Sboxout[round][7], GuessS[round+1][0][0]);
      addtwoXor(S[round][6][3], DCS[round][6][3], S[round+1][0][1], Sboxout[round][7]);
    }
    //S'=S
    for (int round = Startr+1; round < keyRounds; round++) {
      for (int k = 0; k < 2; k++)
        for (int l = 0; l < 2; l++) {
          //2-7
          model.addConstr(S[round][0][2][k][l], GRB.EQUAL, S[round-1][7][0][k][l], "");
          model.addConstr(S[round][0][3][k][l], GRB.EQUAL, S[round-1][7][1][k][l], "");
          model.addConstr(S[round][1][0][k][l], GRB.EQUAL, S[round-1][7][2][k][l], "");
          model.addConstr(S[round][1][1][k][l], GRB.EQUAL, S[round-1][7][3][k][l], "");
          model.addConstr(S[round][1][2][k][l], GRB.EQUAL, S[round-1][6][0][k][l], "");
          model.addConstr(S[round][1][3][k][l], GRB.EQUAL, S[round-1][6][1][k][l], "");
          //10-15
          model.addConstr(S[round][2][2][k][l], GRB.EQUAL, S[round-1][1][0][k][l], "");
          model.addConstr(S[round][2][3][k][l], GRB.EQUAL, S[round-1][1][1][k][l], "");
          model.addConstr(S[round][3][0][k][l], GRB.EQUAL, S[round-1][1][2][k][l], "");
          model.addConstr(S[round][3][1][k][l], GRB.EQUAL, S[round-1][1][3][k][l], "");
          model.addConstr(S[round][3][2][k][l], GRB.EQUAL, S[round-1][0][0][k][l], "");
          model.addConstr(S[round][3][3][k][l], GRB.EQUAL, S[round-1][0][1][k][l], "");
          //10-15
          model.addConstr(S[round][4][2][k][l], GRB.EQUAL, S[round-1][3][0][k][l], "");
          model.addConstr(S[round][4][3][k][l], GRB.EQUAL, S[round-1][3][1][k][l], "");
          model.addConstr(S[round][5][0][k][l], GRB.EQUAL, S[round-1][3][2][k][l], "");
          model.addConstr(S[round][5][1][k][l], GRB.EQUAL, S[round-1][3][3][k][l], "");
          model.addConstr(S[round][5][2][k][l], GRB.EQUAL, S[round-1][2][0][k][l], "");
          model.addConstr(S[round][5][3][k][l], GRB.EQUAL, S[round-1][2][1][k][l], "");
          //26-31
          model.addConstr(S[round][6][2][k][l], GRB.EQUAL, S[round-1][5][0][k][l], "");
          model.addConstr(S[round][6][3][k][l], GRB.EQUAL, S[round-1][5][1][k][l], "");
          model.addConstr(S[round][7][0][k][l], GRB.EQUAL, S[round-1][5][2][k][l], "");
          model.addConstr(S[round][7][1][k][l], GRB.EQUAL, S[round-1][5][3][k][l], "");
          model.addConstr(S[round][7][2][k][l], GRB.EQUAL, S[round-1][4][0][k][l], "");
          model.addConstr(S[round][7][3][k][l], GRB.EQUAL, S[round-1][4][1][k][l], "");
        }
      //0-1
      addSbox(S[round-1][6][1], Sboxout[round][0], GuessS[round-1][6][1]);
      addtwoXor(S[round][0][0], DCS[round][0][0], S[round-1][6][2], Sboxout[round][0]);
      addSbox(S[round][0][0], Sboxout[round][1], GuessS[round-1][6][2]);
      addtwoXor(S[round][0][1], DCS[round][0][1], S[round-1][6][3], Sboxout[round][1]);
      //8-9
      addSbox(S[round-1][0][1], Sboxout[round][2], GuessS[round-1][0][1]);
      addtwoXor(S[round][2][0], DCS[round][2][0], S[round-1][0][2], Sboxout[round][2]);
      addSbox(S[round][2][0], Sboxout[round][3], GuessS[round-1][0][2]);
      addtwoXor(S[round][2][1], DCS[round][2][1], S[round-1][0][3], Sboxout[round][3]);
      //16-17
      addSbox(S[round-1][2][1], Sboxout[round][4], GuessS[round-1][2][1]);
      addtwoXor(S[round][4][0], DCS[round][4][0], S[round-1][2][2], Sboxout[round][4]);
      addSbox(S[round][4][0], Sboxout[round][5], GuessS[round-1][2][2]);
      addtwoXor(S[round][4][1], DCS[round][4][1], S[round-1][2][3], Sboxout[round][5]);
      //24-25
      addSbox(S[round-1][4][1], Sboxout[round][6], GuessS[round-1][4][1]);
      addtwoXor(S[round][6][0], DCS[round][6][0], S[round-1][4][2], Sboxout[round][6]);
      addSbox(S[round][6][0], Sboxout[round][7], GuessS[round-1][4][2]);
      addtwoXor(S[round][6][1], DCS[round][6][1], S[round-1][4][3], Sboxout[round][7]);
    }
  }

  public void addStoRK256(GRBVar[][][][][] S, GRBVar[][][][][] K, GRBVar[][][][][] RK, GRBVar[][][][] DCK) throws GRBException {
    for (int r = 0; r < keyRounds; r++) {
      //0-3
      //addfourXor(K[r][0][0], DCK[r][0][0], S[r][1][2], S[r][3][0], S[r][4][2], S[r][6][0]);
      //addfourXor(K[r][0][1], DCK[r][0][1], S[r][1][0], S[r][2][2], S[r][4][0], S[r][7][2]);
      //addfourXor(K[r][0][2], DCK[r][0][2], S[r][0][2], S[r][2][0], S[r][5][2], S[r][7][0]);
      //addfourXor(K[r][0][3], DCK[r][0][3], S[r][0][0], S[r][3][2], S[r][5][0], S[r][6][2]);
      
      addthreeXor(K[r][0][0], DCK[r][0][0], K[r][2][0], S[r][3][0], S[r][4][2]);
      addthreeXor(K[r][0][1], DCK[r][0][1], S[r][1][0], S[r][2][2], K[r][2][1]);
      addthreeXor(K[r][0][2], DCK[r][0][2], S[r][0][2], K[r][2][2], S[r][7][0]);
      addthreeXor(K[r][0][3], DCK[r][0][3], K[r][2][3], S[r][5][0], S[r][6][2]);
      
      //4-7
      addtwoXor(K[r][1][0], DCK[r][1][0], S[r][3][0], S[r][6][0]);
      addtwoXor(K[r][1][1], DCK[r][1][1], S[r][1][0], S[r][4][0]);
      addtwoXor(K[r][1][2], DCK[r][1][2], S[r][2][0], S[r][7][0]);
      addtwoXor(K[r][1][3], DCK[r][1][3], S[r][0][0], S[r][5][0]);
      //8-11
      addtwoXor(K[r][2][0], DCK[r][2][0], S[r][1][2], S[r][6][0]);
      addtwoXor(K[r][2][1], DCK[r][2][1], S[r][4][0], S[r][7][2]);
      addtwoXor(K[r][2][2], DCK[r][2][2], S[r][2][0], S[r][5][2]);
      addtwoXor(K[r][2][3], DCK[r][2][3], S[r][0][0], S[r][3][2]);
      //16-19
      //addfourXor(K[r][4][0], DCK[r][4][0], S[r][1][3], S[r][3][1], S[r][4][3], S[r][6][1]);
      //addfourXor(K[r][4][1], DCK[r][4][1], S[r][1][1], S[r][2][3], S[r][4][1], S[r][7][3]);
      //addfourXor(K[r][4][2], DCK[r][4][2], S[r][0][3], S[r][2][1], S[r][5][3], S[r][7][1]);
      //addfourXor(K[r][4][3], DCK[r][4][3], S[r][0][1], S[r][3][3], S[r][5][1], S[r][6][3]);
      
      addthreeXor(K[r][4][0], DCK[r][4][0], K[r][6][0], S[r][3][1], S[r][4][3]);
      addthreeXor(K[r][4][1], DCK[r][4][1], S[r][1][1], S[r][2][3], K[r][6][1]);
      addthreeXor(K[r][4][2], DCK[r][4][2], S[r][0][3], K[r][6][2], S[r][7][1]);
      addthreeXor(K[r][4][3], DCK[r][4][3], K[r][6][3], S[r][5][1], S[r][6][3]);
      //20-23
      addtwoXor(K[r][5][0], DCK[r][5][0], S[r][3][1], S[r][6][1]);
      addtwoXor(K[r][5][1], DCK[r][5][1], S[r][1][1], S[r][4][1]);
      addtwoXor(K[r][5][2], DCK[r][5][2], S[r][2][1], S[r][7][1]);
      addtwoXor(K[r][5][3], DCK[r][5][3], S[r][0][1], S[r][5][1]);
      //24-27
      addtwoXor(K[r][6][0], DCK[r][6][0], S[r][1][3], S[r][6][1]);
      addtwoXor(K[r][6][1], DCK[r][6][1], S[r][4][1], S[r][7][3]);
      addtwoXor(K[r][6][2], DCK[r][6][2], S[r][2][1], S[r][5][3]);
      addtwoXor(K[r][6][3], DCK[r][6][3], S[r][0][1], S[r][3][3]);

      for (int k = 0; k < 2; k++)
        for (int l = 0; l < 2; l++) {
          //12-15
          model.addConstr(K[r][3][0][k][l], GRB.EQUAL, S[r][6][0][k][l], "");
          model.addConstr(K[r][3][1][k][l], GRB.EQUAL, S[r][4][0][k][l], "");
          model.addConstr(K[r][3][2][k][l], GRB.EQUAL, S[r][2][0][k][l], "");
          model.addConstr(K[r][3][3][k][l], GRB.EQUAL, S[r][0][0][k][l], "");
          //28-31
          model.addConstr(K[r][7][0][k][l], GRB.EQUAL, S[r][6][1][k][l], "");
          model.addConstr(K[r][7][1][k][l], GRB.EQUAL, S[r][4][1][k][l], "");
          model.addConstr(K[r][7][2][k][l], GRB.EQUAL, S[r][2][1][k][l], "");
          model.addConstr(K[r][7][3][k][l], GRB.EQUAL, S[r][0][1][k][l], "");
        }
    }
    for (int r = 0; r < Rounds+1; r++)
      for (int i = 0; i < 4; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++)  {
              model.addConstr(RK[r][i][j][k][l], GRB.EQUAL, K[r/2][i+(r%2)*4][j][k][l], "");
            }
  }

  public void addfourXor(GRBVar[][] mainvar, GRBVar[] CK, GRBVar[][] ... vars) throws GRBException {

    GRBVar[] Allone = new GRBVar[3];
    for (int l = 0; l < 3; l++) {
      Allone[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "");
      //Allone[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Allone"+l);
    }
    Determine_AllOne(Allone[0], vars[0][0][0], vars[1][0][0], vars[2][0][0], vars[3][0][0]);
    Determine_AllOne(Allone[1], vars[0][0][1], vars[1][0][1], vars[2][0][1], vars[3][0][1]);
    Determine_AllOne(Allone[2], vars[0][1][1], vars[1][1][1], vars[2][1][1], vars[3][1][1]);

    GRBVar[] Twozero = new GRBVar[2];
    for (int l = 0; l < 2; l++) {
      Twozero[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "");
      //Twozero[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Twozero"+l);
    }
    Determine_twoinfour(Twozero[0], vars[0][0][0], vars[1][0][0], vars[2][0][0], vars[3][0][0]);
    Determine_twoinfour(Twozero[1], vars[0][1][1], vars[1][1][1], vars[2][1][1], vars[3][1][1]);

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

  public void addthreeXor(GRBVar[][] mainvar, GRBVar[] CK, GRBVar[][] ... vars) throws GRBException {

    GRBVar[] Allone = new GRBVar[3];
    for (int l = 0; l < 3; l++) {
      Allone[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "");
      //Allone[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Allone"+l);
    }
    Determine_AllOne(Allone[0], vars[0][0][0], vars[1][0][0], vars[2][0][0]);
    Determine_AllOne(Allone[1], vars[0][0][1], vars[1][0][1], vars[2][0][1]);
    Determine_AllOne(Allone[2], vars[0][1][1], vars[1][1][1], vars[2][1][1]);

    GRBVar[] Twozero = new GRBVar[2];
    for (int l = 0; l < 2; l++) {
      Twozero[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "");
      //Twozero[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Twozero"+l);
    }
    Determine_twointhree(Twozero[0], vars[0][0][0], vars[1][0][0], vars[2][0][0]);
    Determine_twointhree(Twozero[1], vars[0][1][1], vars[1][1][1], vars[2][1][1]);

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

  public void addtwoXor(GRBVar[][] mainvar, GRBVar[] CK, GRBVar[][] ... vars) throws GRBException {
    GRBVar[] Allone = new GRBVar[3];
    for (int l = 0; l < 3; l++) {
      Allone[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "");
      //Allone[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Allone"+l);
    }
    Determine_AllOne(Allone[0], vars[0][0][0], vars[1][0][0]);
    Determine_AllOne(Allone[1], vars[0][0][1], vars[1][0][1]);
    Determine_AllOne(Allone[2], vars[0][1][1], vars[1][1][1]);

    GRBVar[] Twozero = new GRBVar[2];
    for (int l = 0; l < 2; l++) {
      Twozero[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "");
      //Twozero[l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Twozero"+l);
    }
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

  public void addSbox(GRBVar[][] Sin, GRBVar[][] Sout, GRBVar Guess) throws GRBException {
    double[][] t = {{0, 0, 1, 1, -1, 0, 1},
            {0, 0, -1, 0, 1, 0, -1},
            {0, 0, 0, -1, 1, 0, -1},
            {0, 1, 0, 0, -1, 0, 0}};
    double[] c = {0, 0, 0, 0};
    for (int q = 0; q < 4; q++) {
      model.addConstr(linExprOf(t[q], Sin[0][0], Sin[0][1], Sin[1][1], Sout[0][0], Sout[0][1], Sout[1][1], Guess), GRB.GREATER_EQUAL, c[q], "");
    }
    model.addConstr(Sin[0][0], GRB.EQUAL, Sout[0][0], "");
    model.addConstr(Sin[1][1], GRB.EQUAL, Sout[1][1], "");
    model.addConstr(Sout[0][1], GRB.EQUAL, Sout[1][0], "");
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

  public void Determine_twointhree(GRBVar mainVar, GRBVar ... vars) throws GRBException {
    GRBLinExpr expr = new GRBLinExpr();
    expr.addTerm(2.0, mainVar);
    for (int i = 0; i < vars.length; i++) {
      expr.addTerm(1.0, vars[i]);
    }
    model.addConstr(expr, GRB.GREATER_EQUAL, 2, "");
    model.addConstr(expr, GRB.LESS_EQUAL, 3, "");
  }

  public void Determine_twoinfour(GRBVar mainVar, GRBVar ... vars) throws GRBException {
    GRBLinExpr expr1 = new GRBLinExpr();
    GRBLinExpr expr2 = new GRBLinExpr();
    expr1.addTerm(3.0, mainVar);
    expr2.addTerm(-2.0, mainVar);
    for (int i = 0; i < vars.length; i++) {
      expr1.addTerm(1.0, vars[i]);
      expr2.addTerm(-1.0, vars[i]);
    }
    model.addConstr(expr1, GRB.GREATER_EQUAL, 3, "");
    model.addConstr(expr2, GRB.GREATER_EQUAL, -4, "");
  }

  public GRBLinExpr linExprOf(double[] coeffs, GRBVar ... vars) throws GRBException {
    GRBLinExpr ofVars = new GRBLinExpr();
    ofVars.addTerms(coeffs, vars);
    return ofVars;
  }

  public GRBLinExpr linExprOf(GRBVar ... vars) {
    GRBLinExpr expr = new GRBLinExpr();
    for (GRBVar var : vars)
      expr.addTerm(1.0, var);
    return expr;
  }
  public MitMSolutionKey getValue() throws GRBException {
    int[][][][][] RKValue = new int[Rounds+1][4+2*regime][4][2][2];
    int[][][][][] SValue   = new int[keyRounds][4+2*regime][4][2][2];
    int[][][][][] KValue   = new int[keyRounds][4+2*regime][4][2][2];
    int[][][][] DCKValue   = new int[keyRounds][4+2*regime][4][2];
    int[][][][] DCSValue   = new int[keyRounds][4+2*regime][4][2];
    int[][][] GuessSValue   = new int[keyRounds][4+2*regime][4];
    for (int round = 0; round <= Rounds; round++)
      for (int i = 0; i < 4+2*regime; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++) {
              RKValue[round][i][j][k][l] = (int) Math.round(RK[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
            }
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++){
            DCKValue[round][i][j][k] = (int) Math.round(DCK[round][i][j][k].get(GRB.DoubleAttr.Xn));
          }
    for (int r = 0; r < keyRounds; r++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++)
            for (int l = 0; l < 2; l++) {
              SValue[r][i][j][k][l] = (int) Math.round(S[r][i][j][k][l].get(GRB.DoubleAttr.Xn));
              KValue[r][i][j][k][l] = (int) Math.round(K[r][i][j][k][l].get(GRB.DoubleAttr.Xn));
            }
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++)
          for (int k = 0; k < 2; k++){
            DCSValue[round][i][j][k] = (int) Math.round(DCS[round][i][j][k].get(GRB.DoubleAttr.Xn));
          }
    for (int round = 0; round < keyRounds; round++)
      for (int i = 0; i < 4+regime*2; i++)
        for (int j = 0; j < 4; j++) {
          GuessSValue[round][i][j] = (int) Math.round(GuessS[round][i][j].get(GRB.DoubleAttr.Xn));
        }

    return new MitMSolutionKey(RKValue, SValue, KValue, DCKValue, DCSValue, GuessSValue);
  }
}
