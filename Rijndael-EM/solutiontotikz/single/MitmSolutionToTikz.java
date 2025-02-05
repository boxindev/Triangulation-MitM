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
  private static final int[][] rho = new int[][]{{0,36,3,41,18},{1,44,10,45,2},{62,6,43,15,61},{28,55,25,21,56},{27,20,39,8,14}};
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
    for (int round = 0; round < mitmSolution.Rounds; round++) {

        //output += " \\node[scale=1.2] () [xor] at ("+(-1)+","+(15*(mitmSolution.Rounds-round)+7)+") {$A^{("+(round)+")}$};\n";
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 4; j++) 
            {
              if (mitmSolution.A[round][i][j][0][0] == 0 & mitmSolution.A[round][i][j][0][1] == 0 )
                output += "\\fill[color="+WhiteColor+"] ("+(i)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.A[round][i][j][0][0] == 0 & mitmSolution.A[round][i][j][0][1] == 1 & mitmSolution.A[round][i][j][1][0] == 1 & mitmSolution.A[round][i][j][1][1] == 1 )
                output += "\\fill[color="+RedColor+"] ("+(i)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.A[round][i][j][1][0] == 1 & mitmSolution.A[round][i][j][1][1] == 0 & mitmSolution.A[round][i][j][0][0] == 1 & mitmSolution.A[round][i][j][0][1] == 1)
                output += "\\fill[color="+BlueColor+"] ("+(i)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.A[round][i][j][0][0] == 1 & mitmSolution.A[round][i][j][0][1] == 1 & mitmSolution.A[round][i][j][1][0] == 1 & mitmSolution.A[round][i][j][1][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(i)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.A[round][i][j][0][0] == 0 & mitmSolution.A[round][i][j][0][1] == 1 & mitmSolution.A[round][i][j][1][0] == 1 & mitmSolution.A[round][i][j][1][1] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(i)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
            }
        output += "\\draw("+(0)+","+(6*(mitmSolution.Rounds-round))+") grid ++(16,4);\n";


        
        //output += " \\node[scale=1.2] () [xor] at ("+(3)+","+(15*(mitmSolution.Rounds-round)+7)+") {$B^{("+(round)+")}$};\n";


        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 4; j++) 
            {
              if (mitmSolution.SB[round][i][j][0][0] == 0 & mitmSolution.SB[round][i][j][0][1] == 0 )
                output += "\\fill[color="+WhiteColor+"] ("+(i+18)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SB[round][i][j][0][0] == 0 & mitmSolution.SB[round][i][j][0][1] == 1 & mitmSolution.SB[round][i][j][1][0] == 1 & mitmSolution.SB[round][i][j][1][1] == 1 )
                output += "\\fill[color="+RedColor+"] ("+(i+18)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SB[round][i][j][1][0] == 1 & mitmSolution.SB[round][i][j][1][1] == 0 & mitmSolution.SB[round][i][j][0][0] == 1 & mitmSolution.SB[round][i][j][0][1] == 1)
                output += "\\fill[color="+BlueColor+"] ("+(i+18)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SB[round][i][j][0][0] == 1 & mitmSolution.SB[round][i][j][0][1] == 1 & mitmSolution.SB[round][i][j][1][0] == 1 & mitmSolution.SB[round][i][j][1][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(i+18)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.SB[round][i][j][0][0] == 0 & mitmSolution.SB[round][i][j][0][1] == 1 & mitmSolution.SB[round][i][j][1][0] == 1 & mitmSolution.SB[round][i][j][1][1] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(i+18)+","+(6*(mitmSolution.Rounds-round)+(3-j))+") rectangle ++(1,1);\n";
            }
        output += "\\draw("+(18)+","+(6*(mitmSolution.Rounds-round))+") grid ++(16,4);\n";


        


	


	//output += " \\node[scale=1.2] () [xor] at ("+(10)+","+(15*(mitmSolution.Rounds-round)+2.5)+") {${\\tt AK}$};\n";
	//output += " \\node[scale=2] () [xor] at ("+(23)+","+(15*(mitmSolution.Rounds-round)+2)+") {$\\bigoplus$};\n";



        //output += "\\draw[myarrows]("+(9)+","+(15*(mitmSolution.Rounds-round)+2)+") -- ++ (2,0);\n";

        //output += "\\draw[myarrows]("+(1)+","+(15*(mitmSolution.Rounds-round)+5)+") -- ++ (0,-7) -- ++ (4,-3) -- ++ (0,-1);\n";


    }

   
    for (int round = 0; round < mitmSolution.Rounds; round++) {

      for (int i = 0; i < 16; i++) 
        {       
          for (int j = 0; j < 4; j++) 
          {
              //DC
              if (mitmSolution.DC[round][i][j][0] == 1) {
                output += "\\draw[line width=2pt, color=red]("+(i)+","+(6*(mitmSolution.Rounds-round-1)+(3-j))+") grid ++(1,1);\n"; 
              }
	      if (mitmSolution.DC[round][i][j][1] == 1) {
                output += "\\draw[line width=2pt, color=blue]("+(i)+","+(6*(mitmSolution.Rounds-round-1)+(3-j))+") grid ++(1,1);\n"; 
              }
           }
        }

    }

    // Footer
    //output += "\\makeatother\n";
    output += "\\end{tikzpicture}\n";
    output += "\\end{document}\n";
    return output;
  }
}
