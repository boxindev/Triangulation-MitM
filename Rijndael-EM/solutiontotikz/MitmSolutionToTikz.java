package mitmsearch.solutiontotikz;

import mitmsearch.mitm.MitmSolution;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.lang.Math;

public class MitmSolutionToTikz {
  private final MitmSolution mitmSolution;
  private static final String WhiteColor  = "white";
  private static final String GrayColor  = "lightgray";
  private static final String BlueColor = "blue";
  private static final String RedColor = "red"; 
  private static final String PurpleColor  = "green!60";
  private static final int[][] shiftRow = new int[][]{{0,1,2,3},{0,1,2,3},{0,1,3,4}};
  public MitmSolutionToTikz(final String filename, final int solutionNumber) {
    this(MitmSolution.fromFile(filename).get(solutionNumber));
  }

  public MitmSolutionToTikz(final MitmSolution mitmSolution) {
    this.mitmSolution = mitmSolution;
  }


  public String generate() {
    String output = "";
    // Header
    output += "\\documentclass{standalone}\n";
    output += "\\usepackage{tikz}\n";
    output += "\\usepackage{calc}\n";
    output += "\\usepackage{pgffor}\n";
    output += "\\usetikzlibrary{patterns}\n";
    output += "\\usetikzlibrary{arrows, decorations.pathmorphing,backgrounds,positioning,fit,petri}\n";
    output += "\\usetikzlibrary{arrows.meta,automata,positioning,shadows}\n";
    output += "\\tikzset{base/.style = {draw=black, minimum width=0.02cm, minimum height=0.02cm,align=center, on chain}, myarrows/.style = {-{Stealth[length=3mm]}}, myline/.style = {thick}, input/.style = {rectangle, text width=2em, text centered, minimum height=2em, base, fill=purple!30}, shift_bit/.style = {circle, text width=1.5em, minimum height=.1em,  base, fill=white!30}, and /.style = {circle, text width=0.4em, text centered, minimum height=0.2em, base, fill=red!30}, xor/.style = {},}\n";
    output += "\\begin{document}\n";
    output += "\\begin{tikzpicture}[scale = 0.5][decoration=brace]\n";
    //output += "\\makeatletter\n";

    //P
    output += " \\node[scale=1.2] () [xor] at ("+(-5- mitmSolution.regime)+","+(8*(mitmSolution.fRounds)+7)+") {$\\textsf{P}$};\n";
    for (int i = 0; i < 4+mitmSolution.regime*2; i++)
      for (int j = 0; j < 4; j++) 
      { 
        output += "\\fill[color="+GrayColor+"] ("+(i-4- mitmSolution.regime)+","+(8*(mitmSolution.fRounds)+5+(3-j))+") rectangle ++(1,1);\n";
      }  
    output += "\\draw("+(-4- mitmSolution.regime)+","+(8*(mitmSolution.fRounds)+5)+") grid ++(" + (4+mitmSolution.regime*2) + ",4);\n";
    output += "\\draw[myarrows]("+(-2)+","+(8*(mitmSolution.fRounds)+5)+") -- ++ (0,-2.6);\n";

    /*
    //Key
    if (mitmSolution.regime == 0) {
        for (int round = 0; round <= mitmSolution.Rounds; round++) {
            //RK
            output += " \\node[scale=1.2] () [xor] at (" + (-6) + "," + (8 * (mitmSolution.fRounds - round) + 5) + ") {$\\textsf{RK}^{(" + (round) + ")}$};\n";
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if (mitmSolution.Key.RK[round][i][j][0][0] == 0 & mitmSolution.Key.RK[round][i][j][0][1] == 0)
                        output += "\\fill[color=" + WhiteColor + "] (" + (i - 8) + "," + (8 * (mitmSolution.fRounds - round) + (3 - j)) + ") rectangle ++(1,1);\n";
                    if (mitmSolution.Key.RK[round][i][j][0][0] == 0 & mitmSolution.Key.RK[round][i][j][0][1] == 1 & mitmSolution.Key.RK[round][i][j][1][0] == 1 & mitmSolution.Key.RK[round][i][j][1][1] == 1)
                        output += "\\fill[color=" + RedColor + "] (" + (i - 8) + "," + (8 * (mitmSolution.fRounds - round) + (3 - j)) + ") rectangle ++(1,1);\n";
                    if (mitmSolution.Key.RK[round][i][j][1][0] == 1 & mitmSolution.Key.RK[round][i][j][1][1] == 0 & mitmSolution.Key.RK[round][i][j][0][0] == 1 & mitmSolution.Key.RK[round][i][j][0][1] == 1)
                        output += "\\fill[color=" + BlueColor + "] (" + (i - 8) + "," + (8 * (mitmSolution.fRounds - round) + (3 - j)) + ") rectangle ++(1,1);\n";
                    if (mitmSolution.Key.RK[round][i][j][0][0] == 1 & mitmSolution.Key.RK[round][i][j][0][1] == 1 & mitmSolution.Key.RK[round][i][j][1][0] == 1 & mitmSolution.Key.RK[round][i][j][1][1] == 1)
                        output += "\\fill[color=" + GrayColor + "] (" + (i - 8) + "," + (8 * (mitmSolution.fRounds - round) + (3 - j)) + ") rectangle ++(1,1);\n";
                    if (mitmSolution.Key.RK[round][i][j][0][0] == 0 & mitmSolution.Key.RK[round][i][j][0][1] == 1 & mitmSolution.Key.RK[round][i][j][1][0] == 1 & mitmSolution.Key.RK[round][i][j][1][1] == 0)
                        output += "\\fill[color=" + PurpleColor + "] (" + (i - 8) + "," + (8 * (mitmSolution.fRounds - round) + (3 - j)) + ") rectangle ++(1,1);\n";
                }
            output += "\\draw(" + (-8) + "," + (8 * (mitmSolution.fRounds - round)) + ") grid ++(4,4);\n";


            //output += "\\draw[myarrows](" + (-10) + "," + (8 * (mitmSolution.fRounds - round) + 2) + ") -- ++ (2,0);\n";
            //output += " \\node[scale=1.2] () [xor] at (" + (-9) + "," + (8 * (mitmSolution.fRounds - round) + 2.5) + ") {${\\tt C}_{0}$};\n";
            output += "\\draw[myarrows](" + (-4) + "," + (8 * (mitmSolution.fRounds - round) + 2) + ") -- ++ (1.6,0);\n";
        }
    }
      if (mitmSolution.regime > 0) {
          for (int round = 0; round <= mitmSolution.Rounds; round++) {
              //RK
              output += " \\node[scale=1.2] () [xor] at ("+(-5-2*mitmSolution.regime)+","+(8*(mitmSolution.fRounds-round)+5)+") {$\\textsf{RK}^{("+(round)+")}$};\n";
              for (int i = 0; i < 4+mitmSolution.regime*2; i++)
                  for (int j = 0; j < 4; j++)
                  {
                      if (mitmSolution.Key.RK[round][i][j][0][0] == 0 & mitmSolution.Key.RK[round][i][j][0][1] == 0 )
                          output += "\\fill[color="+WhiteColor+"] ("+(i-8-2*mitmSolution.regime)+","+(8*(mitmSolution.fRounds-round)+(3-j))+") rectangle ++(1,1);\n";
                      if (mitmSolution.Key.RK[round][i][j][0][0] == 0 & mitmSolution.Key.RK[round][i][j][0][1] == 1 & mitmSolution.Key.RK[round][i][j][1][0] == 1 & mitmSolution.Key.RK[round][i][j][1][1] == 1 )
                          output += "\\fill[color="+RedColor+"] ("+(i-8-2*mitmSolution.regime)+","+(8*(mitmSolution.fRounds-round)+(3-j))+") rectangle ++(1,1);\n";
                      if (mitmSolution.Key.RK[round][i][j][1][0] == 1 & mitmSolution.Key.RK[round][i][j][1][1] == 0 & mitmSolution.Key.RK[round][i][j][0][0] == 1 & mitmSolution.Key.RK[round][i][j][0][1] == 1)
                          output += "\\fill[color="+BlueColor+"] ("+(i-8-2*mitmSolution.regime)+","+(8*(mitmSolution.fRounds-round)+(3-j))+") rectangle ++(1,1);\n";
                      if (mitmSolution.Key.RK[round][i][j][0][0] == 1 & mitmSolution.Key.RK[round][i][j][0][1] == 1 & mitmSolution.Key.RK[round][i][j][1][0] == 1 & mitmSolution.Key.RK[round][i][j][1][1] == 1)
                          output += "\\fill[color="+GrayColor+"] ("+(i-8-2*mitmSolution.regime)+","+(8*(mitmSolution.fRounds-round)+(3-j))+") rectangle ++(1,1);\n";
                      if (mitmSolution.Key.RK[round][i][j][0][0] == 0 & mitmSolution.Key.RK[round][i][j][0][1] == 1 & mitmSolution.Key.RK[round][i][j][1][0] == 1 & mitmSolution.Key.RK[round][i][j][1][1] == 0)
                          output += "\\fill[color="+PurpleColor+"] ("+(i-8-2*mitmSolution.regime)+","+(8*(mitmSolution.fRounds-round)+(3-j))+") rectangle ++(1,1);\n";
                  }
              output += "\\draw("+(-8-2*mitmSolution.regime)+","+(8*(mitmSolution.fRounds-round))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";
              output += "\\draw[myarrows]("+(-4)+","+(8*(mitmSolution.fRounds-round)+2)+") -- ++ (1.6,0);\n";
          }
      }

     */
    //forward
    for (int round = 0; round < mitmSolution.fRounds; round++) {
        int stateStartr = mitmSolution.StateStartr;
        int realRound = (stateStartr+round)%mitmSolution.Rounds;

        output += " \\node[scale=1.2] () [xor] at ("+(2+mitmSolution.regime)+","+(8*(mitmSolution.fRounds-realRound)+5)+") {$\\textsf{A}^{("+(realRound)+")}$};\n";
        for (int i = 0; i < 4+mitmSolution.regime*2; i++)
            for (int j = 0; j < 4; j++) 
            {
              if (mitmSolution.Af[round][i][j][0][0] == 0 & mitmSolution.Af[round][i][j][0][1] == 0 )
                output += "\\fill[color="+WhiteColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Af[round][i][j][0][0] == 0 & mitmSolution.Af[round][i][j][0][1] == 1 & mitmSolution.Af[round][i][j][1][0] == 1 & mitmSolution.Af[round][i][j][1][1] == 1 )
                output += "\\fill[color="+RedColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Af[round][i][j][1][0] == 1 & mitmSolution.Af[round][i][j][1][1] == 0 & mitmSolution.Af[round][i][j][0][0] == 1 & mitmSolution.Af[round][i][j][0][1] == 1)
                output += "\\fill[color="+BlueColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Af[round][i][j][0][0] == 1 & mitmSolution.Af[round][i][j][0][1] == 1 & mitmSolution.Af[round][i][j][1][0] == 1 & mitmSolution.Af[round][i][j][1][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Af[round][i][j][0][0] == 0 & mitmSolution.Af[round][i][j][0][1] == 1 & mitmSolution.Af[round][i][j][1][0] == 1 & mitmSolution.Af[round][i][j][1][1] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
            }
        output += "\\draw("+(0)+","+(8*(mitmSolution.fRounds-realRound))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";



        output += " \\node[scale=1.2] () [xor] at ("+(8+mitmSolution.regime*3)+","+(8*(mitmSolution.fRounds-realRound)+5)+") {$ \\textsf{SB}^{("+(realRound)+")}$};\n";
        for (int i = 0; i < 4+mitmSolution.regime*2; i++)
            for (int j = 0; j < 4; j++)
            {
                if (mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 0 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 0 )
                    output += "\\fill[color="+WhiteColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
                if (mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 0 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][0] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][1] == 1 )
                    output += "\\fill[color="+RedColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
                if (mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][0] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][1] == 0 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 1)
                    output += "\\fill[color="+BlueColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
                if (mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][0] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][1] == 1)
                    output += "\\fill[color="+GrayColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
                if (mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 0 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][0] == 1 & mitmSolution.SBf[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][1] == 0)
                    output += "\\fill[color="+PurpleColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
            }
        output += "\\draw("+(1*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";


        
        output += " \\node[scale=1.2] () [xor] at ("+(14+mitmSolution.regime*5)+","+(8*(mitmSolution.fRounds-realRound)+5)+") {$\\textsf{SR}^{("+(realRound)+")}$};\n";

        
        for (int i = 0; i < 4+mitmSolution.regime*2; i++)
            for (int j = 0; j < 4; j++) 
            {
              if (mitmSolution.SBf[round][i][j][0][0] == 0 & mitmSolution.SBf[round][i][j][0][1] == 0 )
                output += "\\fill[color="+WhiteColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SBf[round][i][j][0][0] == 0 & mitmSolution.SBf[round][i][j][0][1] == 1 & mitmSolution.SBf[round][i][j][1][0] == 1 & mitmSolution.SBf[round][i][j][1][1] == 1 )
                output += "\\fill[color="+RedColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SBf[round][i][j][1][0] == 1 & mitmSolution.SBf[round][i][j][1][1] == 0 & mitmSolution.SBf[round][i][j][0][0] == 1 & mitmSolution.SBf[round][i][j][0][1] == 1)
                output += "\\fill[color="+BlueColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SBf[round][i][j][0][0] == 1 & mitmSolution.SBf[round][i][j][0][1] == 1 & mitmSolution.SBf[round][i][j][1][0] == 1 & mitmSolution.SBf[round][i][j][1][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SBf[round][i][j][0][0] == 0 & mitmSolution.SBf[round][i][j][0][1] == 1 & mitmSolution.SBf[round][i][j][1][0] == 1 & mitmSolution.SBf[round][i][j][1][1] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
            }
        output += "\\draw("+(2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";

        
        
        
        if ( realRound != mitmSolution.Rounds -1) {
          output += " \\node[scale=1.2] () [xor] at ("+(20+mitmSolution.regime*7)+","+(8*(mitmSolution.fRounds-realRound)+5)+") {$\\textsf{MC}^{("+(realRound)+")}$};\n";
          for (int i = 0; i < 4+mitmSolution.regime*2; i++)
            for (int j = 0; j < 4; j++) 
            {
              if (mitmSolution.MCf[round][i][j][0][0] == 0 & mitmSolution.MCf[round][i][j][0][1] == 0 )
                output += "\\fill[color="+WhiteColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.MCf[round][i][j][0][0] == 0 & mitmSolution.MCf[round][i][j][0][1] == 1 & mitmSolution.MCf[round][i][j][1][0] == 1 & mitmSolution.MCf[round][i][j][1][1] == 1 )
                output += "\\fill[color="+RedColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.MCf[round][i][j][1][0] == 1 & mitmSolution.MCf[round][i][j][1][1] == 0 & mitmSolution.MCf[round][i][j][0][0] == 1 & mitmSolution.MCf[round][i][j][0][1] == 1)
                output += "\\fill[color="+BlueColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.MCf[round][i][j][0][0] == 1 & mitmSolution.MCf[round][i][j][0][1] == 1 & mitmSolution.MCf[round][i][j][1][0] == 1 & mitmSolution.MCf[round][i][j][1][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.MCf[round][i][j][0][0] == 0 & mitmSolution.MCf[round][i][j][0][1] == 1 & mitmSolution.MCf[round][i][j][1][0] == 1 & mitmSolution.MCf[round][i][j][1][1] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
	      if (mitmSolution.DCf[round][i][j][0] == 1) {
                 output += "\\draw[line width=2pt, color=red]("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") grid ++(1,1);\n";
              }
	      if (mitmSolution.DCf[round][i][j][1] == 1) {
                 output += "\\draw[line width=2pt, color=blue]("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") grid ++(1,1);\n";
              }

            if (mitmSolution.DCfAK[round][i][j][0] == 1) {
                output += "\\draw[line width=2pt, color=red]("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound-1)+(3-j))+") grid ++(1,1);\n";
            }
            if (mitmSolution.DCfAK[round][i][j][1] == 1) {
                output += "\\draw[line width=2pt, color=blue]("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound-1)+(3-j))+") grid ++(1,1);\n";
            }
            }
          output += "\\draw("+(3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";
	}
	


	//output += " \\node[scale=1.2] () [xor] at ("+(-2)+","+(8*(mitmSolution.fRounds-realRound)+1)+") {${\\tt AK}$};\n";
	output += " \\node[scale=1.2] () [xor] at ("+(5+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+2.5)+") {${\\tt SB}$};\n";
	output += " \\node[scale=1.2] () [xor] at ("+(11+mitmSolution.regime*4)+","+(8*(mitmSolution.fRounds-realRound)+2.5)+") {${\\tt SR}$};\n";
    if (realRound != mitmSolution.Rounds-1) {
        output += " \\node[scale=1.2] () [xor] at (" + (17 + mitmSolution.regime * 6) + "," + (8 * (mitmSolution.fRounds - realRound) + 2.5) + ") {${\\tt MC}$};\n";
    }
	//output += " \\node[scale=1.2] () [xor] at ("+(-2)+","+(8*(mitmSolution.fRounds-realRound)+2)+") {$\\bigoplus$};\n";



        output += "\\draw[myarrows]("+(2*0+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (2,0);\n";
        output += "\\draw[myarrows]("+(2*1+2*(4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (2,0);\n";
        if (realRound != mitmSolution.Rounds-1) {
            output += "\\draw[myarrows](" + (2 * 2 + 3 * (4 + mitmSolution.regime * 2)) + "," + (8 * (mitmSolution.fRounds - realRound) + 2) + ") -- ++ (2,0);\n";
        }
        //output += "\\draw[myarrows]("+(-1.6)+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (1.6,0);\n";
        /*if (round != 0) {
	  output += " \\node[scale=1.2] () [xor] at ("+(-1)+","+(8*(mitmSolution.Rounds-round)+2)+") {$\\bigoplus$};\n";
          output += " \\node[scale=1.2] () [xor] at ("+(-1)+","+(8*(mitmSolution.Rounds-round)+1)+") {${\\tt AC}$};\n";
          
        }*/
       if (round != mitmSolution.fRounds-1 & realRound != mitmSolution.Rounds-1) {
	  output += "\\draw[myarrows]("+(2*3+4*(4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (2,0) -- ++ (0,-4) -- ++ (" + (-10-4*(4+mitmSolution.regime*2)) +",0) -- ++ (0,-4)-- ++ (2,0);\n";
          //output += "\\draw[myarrows]("+(22)+","+(8*(mitmSolution.fRounds-round)+2)+") -- ++ (2,0) -- ++ (0,-4) -- ++ (-26,0) -- ++ (0,-4) -- ++ (2,0);\n";
       }

        if (realRound == mitmSolution.Rounds-1) {
            output += "\\draw[myarrows]("+(2*2+3*(4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (2,0) -- ++ (0,-4) -- ++ (" + (-8-3*(4+mitmSolution.regime*2)) +",0) -- ++ (0,-3.6);\n";
            //output += "\\draw[myarrows]("+(22)+","+(8*(mitmSolution.fRounds-round)+2)+") -- ++ (2,0) -- ++ (0,-4) -- ++ (-26,0) -- ++ (0,-4) -- ++ (2,0);\n";
        }
       if(round == 0 & realRound != 0){
           output += "\\draw[myarrows]("+(0)+","+(8*(mitmSolution.fRounds-realRound)+2+0.4)+") -- ++ (-2,0)  -- ++ (0,4) -- ++ (" + (10+4*(4+mitmSolution.regime*2)) +",0) -- ++ (0,4) -- ++ (-2,0);\n";
       }

    }
    

    for (int round = 0; round < mitmSolution.bRounds; round++) {
        int stateStartr = mitmSolution.StateStartr;
        int realRound = (stateStartr-mitmSolution.bRounds+round+mitmSolution.Rounds)%mitmSolution.Rounds;

        output += " \\node[scale=1.2] () [xor] at ("+(2+mitmSolution.regime)+","+(8*(mitmSolution.fRounds-realRound)+5)+") {$\\textsf{A}^{("+(realRound)+")}$};\n";
        for (int i = 0; i < 4+mitmSolution.regime*2; i++)
            for (int j = 0; j < 4; j++) 
            {
              if (mitmSolution.Ab[round][i][j][0][0] == 0 & mitmSolution.Ab[round][i][j][0][1] == 0 )
                output += "\\fill[color="+WhiteColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Ab[round][i][j][0][0] == 0 & mitmSolution.Ab[round][i][j][0][1] == 1 & mitmSolution.Ab[round][i][j][1][0] == 1 & mitmSolution.Ab[round][i][j][1][1] == 1 )
                output += "\\fill[color="+RedColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Ab[round][i][j][1][0] == 1 & mitmSolution.Ab[round][i][j][1][1] == 0 & mitmSolution.Ab[round][i][j][0][0] == 1 & mitmSolution.Ab[round][i][j][0][1] == 1)
                output += "\\fill[color="+BlueColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Ab[round][i][j][0][0] == 1 & mitmSolution.Ab[round][i][j][0][1] == 1 & mitmSolution.Ab[round][i][j][1][0] == 1 & mitmSolution.Ab[round][i][j][1][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Ab[round][i][j][0][0] == 0 & mitmSolution.Ab[round][i][j][0][1] == 1 & mitmSolution.Ab[round][i][j][1][0] == 1 & mitmSolution.Ab[round][i][j][1][1] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(i)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
            }
        output += "\\draw("+(0)+","+(8*(mitmSolution.fRounds-realRound))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";



        output += " \\node[scale=1.2] () [xor] at ("+(8+mitmSolution.regime*3)+","+(8*(mitmSolution.fRounds-realRound)+5)+") {$ \\textsf{SB}^{("+(realRound)+")}$};\n";
        for (int i = 0; i < 4+mitmSolution.regime*2; i++)
            for (int j = 0; j < 4; j++)
            {
                if (mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 0 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 0 )
                    output += "\\fill[color="+WhiteColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
                if (mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 0 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][0] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][1] == 1 )
                    output += "\\fill[color="+RedColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
                if (mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][0] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][1] == 0 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 1)
                    output += "\\fill[color="+BlueColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
                if (mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][0] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][1] == 1)
                    output += "\\fill[color="+GrayColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
                if (mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][0] == 0 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][0][1] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][0] == 1 & mitmSolution.SBb[round][(i-shiftRow[mitmSolution.regime][j]+4+mitmSolution.regime*2)%(4+mitmSolution.regime*2)][j][1][1] == 0)
                    output += "\\fill[color="+PurpleColor+"] ("+(i+2+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
            }
        output += "\\draw("+(1*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";


        
        output += " \\node[scale=1.2] () [xor] at ("+(14+mitmSolution.regime*5)+","+(8*(mitmSolution.fRounds-realRound)+5)+") {$\\textsf{SR}^{("+(realRound)+")}$};\n";

        
        for (int i = 0; i < 4+mitmSolution.regime*2; i++)
            for (int j = 0; j < 4; j++) 
            {
              if (mitmSolution.SBb[round][i][j][0][0] == 0 & mitmSolution.SBb[round][i][j][0][1] == 0 )
                output += "\\fill[color="+WhiteColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SBb[round][i][j][0][0] == 0 & mitmSolution.SBb[round][i][j][0][1] == 1 & mitmSolution.SBb[round][i][j][1][0] == 1 & mitmSolution.SBb[round][i][j][1][1] == 1 )
                output += "\\fill[color="+RedColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SBb[round][i][j][1][0] == 1 & mitmSolution.SBb[round][i][j][1][1] == 0 & mitmSolution.SBb[round][i][j][0][0] == 1 & mitmSolution.SBb[round][i][j][0][1] == 1)
                output += "\\fill[color="+BlueColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SBb[round][i][j][0][0] == 1 & mitmSolution.SBb[round][i][j][0][1] == 1 & mitmSolution.SBb[round][i][j][1][0] == 1 & mitmSolution.SBb[round][i][j][1][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SBb[round][i][j][0][0] == 0 & mitmSolution.SBb[round][i][j][0][1] == 1 & mitmSolution.SBb[round][i][j][1][0] == 1 & mitmSolution.SBb[round][i][j][1][1] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DCb[round][i][j][0] == 1) {
                 output += "\\draw[line width=2pt, color=red]("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") grid ++(1,1);\n";
              }
	      if (mitmSolution.DCb[round][i][j][1] == 1) {
                 output += "\\draw[line width=2pt, color=blue]("+(i+2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") grid ++(1,1);\n";
              }
            }
        output += "\\draw("+(2*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";

        
        
        
        if ( realRound != mitmSolution.Rounds -1) {
          output += " \\node[scale=1.2] () [xor] at ("+(20+mitmSolution.regime*7)+","+(8*(mitmSolution.fRounds-realRound)+5)+") {$\\textsf{MC}^{("+(realRound)+")}$};\n";
          for (int i = 0; i < 4+mitmSolution.regime*2; i++)
            for (int j = 0; j < 4; j++) 
            {
              if (mitmSolution.MCb[round][i][j][0][0] == 0 & mitmSolution.MCb[round][i][j][0][1] == 0 )
                output += "\\fill[color="+WhiteColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.MCb[round][i][j][0][0] == 0 & mitmSolution.MCb[round][i][j][0][1] == 1 & mitmSolution.MCb[round][i][j][1][0] == 1 & mitmSolution.MCb[round][i][j][1][1] == 1 )
                output += "\\fill[color="+RedColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.MCb[round][i][j][1][0] == 1 & mitmSolution.MCb[round][i][j][1][1] == 0 & mitmSolution.MCb[round][i][j][0][0] == 1 & mitmSolution.MCb[round][i][j][0][1] == 1)
                output += "\\fill[color="+BlueColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.MCb[round][i][j][0][0] == 1 & mitmSolution.MCb[round][i][j][0][1] == 1 & mitmSolution.MCb[round][i][j][1][0] == 1 & mitmSolution.MCb[round][i][j][1][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.MCb[round][i][j][0][0] == 0 & mitmSolution.MCb[round][i][j][0][1] == 1 & mitmSolution.MCb[round][i][j][1][0] == 1 & mitmSolution.MCb[round][i][j][1][1] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(i+3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+(3-j))+") rectangle ++(1,1);\n";
	      
            }
          output += "\\draw("+(3*(2+4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";
	}
	


	//output += " \\node[scale=1.2] () [xor] at ("+(-2)+","+(8*(mitmSolution.fRounds-realRound)+1)+") {${\\tt AK}$};\n";
	output += " \\node[scale=1.2] () [xor] at ("+(5+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+2.5)+") {${\\tt SB}$};\n";
	output += " \\node[scale=1.2] () [xor] at ("+(11+mitmSolution.regime*4)+","+(8*(mitmSolution.fRounds-realRound)+2.5)+") {${\\tt SR}$};\n";
        if ( realRound != mitmSolution.Rounds -1)
	  output += " \\node[scale=1.2] () [xor] at ("+(17+mitmSolution.regime*6)+","+(8*(mitmSolution.fRounds-realRound)+2.5)+") {${\\tt MC}$};\n";
	//output += " \\node[scale=1.2] () [xor] at ("+(-2)+","+(8*(mitmSolution.fRounds-realRound)+2)+") {$\\bigoplus$};\n";



        output += "\\draw[myarrows]("+(2+2*0+4+mitmSolution.regime*2)+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (-2,0);\n";
        output += "\\draw[myarrows]("+(2+2*1+2*(4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (-2,0);\n";
        //output += "\\draw[myarrows]("+(0)+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (-1.6,0);\n";
        if ( realRound != mitmSolution.Rounds -1)
          output += "\\draw[myarrows]("+(2+2*2+3*(4+mitmSolution.regime*2))+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (-2,0);\n";
        //if (round != 0) {
	  //output += " \\node[scale=1.2] () [xor] at ("+(-1)+","+(8*(mitmSolution.Rounds-round)+2)+") {$\\bigoplus$};\n";
          //output += " \\node[scale=1.2] () [xor] at ("+(-1)+","+(8*(mitmSolution.Rounds-round)+1)+") {${\\tt AC}$};\n";
          
        //}
        if (realRound != 0) {
          //output += "\\draw[myarrows]("+(0)+","+(8*(-round)-6)+") -- ++ (-2,0) -- ++ (0,4) -- ++ (26,0) -- ++ (0,4) -- ++ (-2,0);\n";
          output += "\\draw[myarrows]("+(0)+","+(8*(mitmSolution.fRounds-realRound)+2)+")  -- ++ (-2,0) -- ++ (0,4) -- ++ (" + (10+4*(4+mitmSolution.regime*2)) +",0) -- ++ (0,4) -- ++ (-2,0);\n";
        }
        if (realRound == 0) {
            output += " \\node[scale=1.2] () [xor] at ("+(-2)+","+(8*(mitmSolution.fRounds-realRound)+2)+") {$\\bigoplus$};\n";
            output += "\\draw[myarrows]("+(0)+","+(8*(mitmSolution.fRounds-realRound)+2)+") -- ++ (-1.6,0);\n";
            output += "\\draw[myarrows]("+(-2.4)+","+(8*(mitmSolution.fRounds-realRound)+2)+")  -- ++ (-2,0) -- ++ (0," + (-8*mitmSolution.Rounds) +")  -- ++ (2,0);\n";
        }
        if (realRound == mitmSolution.Rounds-1){
            output += "\\draw[myarrows]("+(-2)+","+(8*(-mitmSolution.bRounds)+2+0.4)+") -- ++ (0,3.6) -- ++ (" + (8+3*(4+mitmSolution.regime*2)) +",0) -- ++ (0,4) -- ++ (-2,0);\n";
        }

    }
    //T  
    output += " \\node[scale=1.2] () [xor] at ("+(4+ mitmSolution.regime*2 +1)+","+(8*(-mitmSolution.bRounds)+2)+") {$\\textsf{C}$};\n";
    for (int i = 0; i < 4+mitmSolution.regime*2; i++)
      for (int j = 0; j < 4; j++) 
      { 
        output += "\\fill[color="+GrayColor+"] ("+(i)+","+(8*(-mitmSolution.bRounds)+(3-j))+") rectangle ++(1,1);\n";
      }  
    output += "\\draw("+(0)+","+(8*(-mitmSolution.bRounds))+") grid ++("+(4+2*mitmSolution.regime)+",4);\n";
    output += "\\draw[myarrows]("+(0)+","+(8*(-mitmSolution.bRounds)+2)+") -- ++ (-1.6,0);\n";
    output += " \\node[scale=1.2] () [xor] at ("+(-2)+","+(8*(-mitmSolution.bRounds)+2)+") {$\\bigoplus$};\n";
    //output += "\\draw[myarrows]("+(-2)+","+(8*(-mitmSolution.bRounds)+2+0.4)+") -- ++ (0,3.6) -- ++ (20,0) -- ++ (0,4) -- ++ (-2,0);\n";

    /*for (int i = 0; i < 16; i++) 
      output += "\\node[align=center] at ("+(i+0.5)+","+(31.5)+") {\\textbf{\\large $"+i+"$}};\n";
    for (int j = 0; j < 4; j++) 
      output += "\\node[align=center] at ("+(-0.5)+","+(35.5-j)+") {\\textbf{\\large $"+j+"$}};\n";

    output += "\\draw("+(61)+","+(22.5)+") grid ++(0.8,0.8);\n";
    output += "\\fill[color="+RedColor+"] ("+(61)+","+(22.5)+")  rectangle ++(0.8,0.8);\n";   
    output += " \\node[align=center] at ("+(60)+","+(22.8)+") {\\textbf{(~\\large - "+(8)+"}};\n";
    
    output += "\\draw("+(64)+","+(22.5)+") grid ++(0.8,0.8);\n";
    output += "\\fill[color="+BlueColor+"] ("+(64)+","+(22.5)+")  rectangle ++(0.8,0.8);\n";   
    output += " \\node[align=center] at ("+(63.5)+","+(22.8)+") {\\textbf{~,~\\large - "+(8)+"~~~~)}};\n";

    output += "\\draw("+(61)+","+(14.5)+") grid ++(0.8,0.8);\n";
    output += "\\fill[color="+RedColor+"] ("+(61)+","+(14.5)+")  rectangle ++(0.8,0.8);\n";   
    output += " \\node[align=center] at ("+(60)+","+(14.8)+") {\\textbf{(~\\large - "+(2)+"}};\n";
    
    output += "\\draw("+(64)+","+(14.5)+") grid ++(0.8,0.8);\n";
    output += "\\fill[color="+BlueColor+"] ("+(64)+","+(14.5)+")  rectangle ++(0.8,0.8);\n";   
    output += " \\node[align=center] at ("+(63.5)+","+(14.8)+") {\\textbf{~,~\\large - "+(2)+"~~~~)}};\n";


    output += "\\draw("+(43)+","+(-5.5)+") grid ++(0.8,0.8);\n";
    output += "\\fill[color="+RedColor+"] ("+(43)+","+(-5.5)+")  rectangle ++(0.8,0.8);\n";   
    output += " \\node[align=center] at ("+(42)+","+(-5.2)+") {\\textbf{(~\\large - "+(2)+"}};\n";
    
    output += "\\draw("+(46)+","+(-5.5)+") grid ++(0.8,0.8);\n";
    output += "\\fill[color="+BlueColor+"] ("+(46)+","+(-5.5)+")  rectangle ++(0.8,0.8);\n";   
    output += " \\node[align=center] at ("+(45.5)+","+(-5.2)+") {\\textbf{~,~\\large - "+(3)+"~~~~)}};\n";


    output += "\\draw("+(43)+","+(-13.5)+") grid ++(0.8,0.8);\n";
    output += "\\fill[color="+RedColor+"] ("+(43)+","+(-13.5)+")  rectangle ++(0.8,0.8);\n";   
    output += " \\node[align=center] at ("+(42)+","+(-13.2)+") {\\textbf{(~\\large - "+(4)+"}};\n";
    
    output += "\\draw("+(46)+","+(-13.5)+") grid ++(0.8,0.8);\n";
    output += "\\fill[color="+BlueColor+"] ("+(46)+","+(-13.5)+")  rectangle ++(0.8,0.8);\n";   
    output += " \\node[align=center] at ("+(45.5)+","+(-13.2)+") {\\textbf{~,~\\large - "+(6)+"~~~~)}};\n";*/
      
    // Footer
    //output += "\\makeatother\n";
    output += "\\end{tikzpicture}\n";
    output += "\\end{document}\n";
    return output;
  }
}
