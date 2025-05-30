package mitmsearch.mitm;

import gurobi.*;
import java.io.FileWriter ;
import java.io.IOException ;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class Callback extends GRBCallback {
  private double lastiter ;
  private double lastnode ;
  private GRBVar [] vars ;
  private FileWriter logfile ;
  public Callback ( GRBVar[] xvars, FileWriter xlogfile ) {
    lastiter = lastnode = - GRB . INFINITY ;
    vars = xvars ;
    logfile = xlogfile ;
  }
  /**
   * 
   */
  protected void callback () {
    try {
      if ( where == GRB.CB_POLLING ) {
        // Ignore polling callback
      } else if ( where == GRB.CB_PRESOLVE ) {
        // Presolve callback
        /*int cdels = getIntInfo (GRB.CB_PRE_COLDEL);
        int rdels = getIntInfo (GRB.CB_PRE_ROWDEL );
        if ( cdels != 0 || rdels != 0) {
          System.out.println ( cdels + " columns and " + rdels + " rows are removed " );
        }*/
      } else if ( where == GRB.CB_SIMPLEX ) {
        // Simplex callback
        /*double itcnt = getDoubleInfo ( GRB . CB_SPX_ITRCNT );
        if ( itcnt - lastiter >= 100) {
          lastiter = itcnt ;
          double obj = getDoubleInfo ( GRB . CB_SPX_OBJVAL );
          int ispert = getIntInfo ( GRB . CB_SPX_ISPERT );
          double pinf = getDoubleInfo ( GRB . CB_SPX_PRIMINF );
          double dinf = getDoubleInfo ( GRB . CB_SPX_DUALINF );
          char ch ;
          if ( ispert == 0) ch = ' ';
          else if ( ispert == 1) ch = 'S';
          else ch = 'P';
          System . out . println ( itcnt + " " + obj + ch + " " + pinf + " " + dinf );
        }*/
      } else if ( where == GRB.CB_MIP ) {
        // General MIP callback
        /*double nodecnt = getDoubleInfo ( GRB.CB_MIP_NODCNT );
        double objbst = getDoubleInfo ( GRB.CB_MIP_OBJBST );
        double objbnd = getDoubleInfo ( GRB.CB_MIP_OBJBND );
        int solcnt = getIntInfo ( GRB.CB_MIP_SOLCNT );
        if ( nodecnt - lastnode >= 100) {
          lastnode = nodecnt ;
          int actnodes = ( int ) getDoubleInfo ( GRB . CB_MIP_NODLFT );
          int itcnt = ( int ) getDoubleInfo ( GRB . CB_MIP_ITRCNT );
          int cutcnt = getIntInfo ( GRB . CB_MIP_CUTCNT );
          System . out . println ( nodecnt + " " + actnodes + " " + itcnt + " " + objbst + " " + objbnd + " " + solcnt + " " + cutcnt );
        }
        if ( Math . abs ( objbst - objbnd ) < 0.1 * (1.0 + Math . abs ( objbst ))) {
          System . out . println ( " Stop early - 10% gap achieved " );
          abort ();
        }
        if ( nodecnt >= 10000 && solcnt > 0) {
          System . out . println ( " Stop early - 10000 nodes explored " );
          abort ();
        }*/
      } else if ( where == GRB . CB_MIPSOL ) {
        // MIP solution callback
        int nodecnt = ( int ) getDoubleInfo ( GRB.CB_MIPSOL_NODCNT );
        double obj = getDoubleInfo ( GRB.CB_MIPSOL_OBJ );
        int solcnt = getIntInfo ( GRB.CB_MIPSOL_SOLCNT );
        double[] x = getSolution ( vars );
        System.out.println ( " **** New solution at node " + nodecnt + " , obj " + obj + " , sol " + solcnt + " **** " );
        String output = "outputimm/result"+solcnt+".json";
        MitmSolution Solution;
        Solution = getSolution(solcnt, obj, x);
        List<MitmSolution> ListSolution = new ArrayList<>();
        ListSolution.add(Solution);
        MitmSolution.toFile(output,ListSolution);
        
        
      } else if ( where == GRB . CB_MIPNODE ) {
        // MIP node callback
        /*System . out . println ( " **** New node **** " );
        if ( getIntInfo ( GRB . CB_MIPNODE_STATUS ) == GRB . OPTIMAL ) {
          double [] x = getNodeRel ( vars );
          setSolution ( vars , x );
        }*/
      } else if ( where == GRB . CB_BARRIER ) {
          // Barrier callback
          /*int itcnt = getIntInfo ( GRB . CB_BARRIER_ITRCNT );
          double primobj = getDoubleInfo ( GRB . CB_BARRIER_PRIMOBJ );
          double dualobj = getDoubleInfo ( GRB . CB_BARRIER_DUALOBJ );
          double priminf = getDoubleInfo ( GRB . CB_BARRIER_PRIMINF );
          double dualinf = getDoubleInfo ( GRB . CB_BARRIER_DUALINF );
          double cmpl = getDoubleInfo ( GRB . CB_BARRIER_COMPL );
          System . out . println ( itcnt + " " + primobj + " " + dualobj + " "+ priminf + " " + dualinf + " " + cmpl );*/
      } else if ( where == GRB.CB_MESSAGE ) {
        // Message callback
        //String msg = getStringInfo(GRB.CB_MSG_STRING );
        //if (msg != null) logfile.write (msg);
      }
    } catch ( GRBException e ) {
      System.out . println ( " Error code : " + e . getErrorCode ());
      System.out . println ( e . getMessage ());
      e.printStackTrace ();
    } catch ( Exception e ) {
    System.out.println ( " Error during callback " );
      e.printStackTrace ();
    }
  }

  private MitmSolution getSolution(int solutionNumber, double obj, double[] x) {
    //try {
      //model.set(GRB.IntParam.SolutionNumber, solutionNumber);
      int Rounds = 2;
      int[][][][] Pi_initValue     = new int[5][5][64][3];
      int[][][] CondValue     = new int[5][64][2];
      int[][][][][] DAValue     = new int[Rounds+1][5][5][64][3];
      int[][][][] DPValue  = new int[Rounds][5][64][3];
      int[][][][] DP2Value  = new int[Rounds][5][64][3];
      int[][][][] DC1Value = new int[Rounds][5][64][2];
      int[][][][] DC12Value = new int[Rounds][5][64][2];
      int[][][][][] DBValue  = new int[Rounds][5][5][64][3];
      int[][][][][] DC2Value  = new int[Rounds][5][5][64][2];
      int[][] DomValue  = new int[5][64];
      int[] objValue  = new int[3];

      for (int i = 0; i < 5; i++) 
        for (int j = 0; j < 5; j++) 
	  for (int k = 0; k < 64; k++) 
	    for (int l = 0; l < 3; l++)  { 
              Pi_initValue[i][j][k][l]  = (int) Math.round(x[3*(64*(5*i+j)+k)+l]);
            }
     for (int i = 0; i < 5; i++) 
	for (int k = 0; k < 64; k++)  
	  for (int l = 0; l < 2; l++)  { 
            CondValue[i][k][l]  = (int) Math.round(x[4800+2*(64*i+k)+l]);
	  }

      for (int round = 0; round < Rounds+1; round++)
        for (int i = 0; i < 5; i++) 
          for (int j = 0; j < 5; j++) 
	    for (int k = 0; k < 64; k++) 
	      for (int l = 0; l < 3; l++)  { 
                DAValue[round][i][j][k][l]  = (int) Math.round(x[5440+3*(64*(5*(5*round+i)+j)+k)+l]);
              }
      

      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 5; i++) 
          for (int j = 0; j < 5; j++) 
	    for (int k = 0; k < 64; k++) 
	      for (int l = 0; l < 3; l++)  { 
                DBValue[round][i][j][k][l]  = (int) Math.round(x[4800*Rounds+10240+3*(64*(5*(5*round+i)+j)+k)+l]);
              }
      

      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 5; i++) 
          for (int j = 0; j < 5; j++) 
            for (int k = 0; k < 64; k++)
                for (int l = 0; l < 2; l++){
                  DC2Value[round][i][j][k][l]  = (int) Math.round(x[9600*Rounds+10240+2*(64*(5*(5*round+i)+j)+k)+l]);
                }


      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 5; i++) 
	  for (int k = 0; k < 64; k++) 
	    for (int l = 0; l < 3; l++)  { 
              DPValue[round][i][k][l]  = (int) Math.round(x[12800*Rounds+10240+3*(64*(5*round+i)+k)+l]);
	    }
      

      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 5; i++) 
	  for (int k = 0; k < 64; k++) 
	    for (int l = 0; l < 3; l++)  { 
              DP2Value[round][i][k][l]  = (int) Math.round(x[13760*Rounds+10240+3*(64*(5*round+i)+k)+l]);
	    }
      

      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 5; i++) 
              for (int k = 0; k < 64; k++)
                  for (int l = 0; l < 2; l++){
                      DC1Value[round][i][k][l]  = (int) Math.round(x[14720*Rounds+10240+2*(64*(5*round+i)+k)+l]);
              }

      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 5; i++) 
          for (int k = 0; k < 64; k++)
              for (int l = 0; l < 2; l++){
                DC12Value[round][i][k][l]  = (int) Math.round(x[15360*Rounds+10240+2*(64*(5*round+i)+k)+l]);
          }

      for (int i = 0; i < 5; i++) 
	for (int k = 0; k < 64; k++) {
          DomValue[i][k]  = (int) Math.round(x[16000*Rounds+10240+64*i+k]);
      }
      for (int i = 0; i < 3; i++) {
        objValue[i]  = (int) Math.round(x[x.length-1-(2-i)]);
      }
      
      return new MitmSolution(Rounds, (int) obj, Pi_initValue, CondValue, DAValue,DBValue, DC2Value, DPValue, DP2Value, DC1Value, DC12Value, DomValue,objValue);
    /*} catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
      return null; // Can't access
    }*/
  }
}
