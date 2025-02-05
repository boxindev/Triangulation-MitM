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
        output += "\\tikzset{base/.style = {draw=black, minimum width=0.02cm, minimum height=0.02cm, align=center, on chain},}\n";
        output += "\\begin{document}\n";
        output += "\\begin{tikzpicture}[scale = 0.45,every node/.style={scale=0.5}]\n";
        output += "\\makeatletter\n";

        for (int round = 0; round < mitmSolution.Rounds; round++) {

            output += " \\node[align=center] at ("+(-2)+","+(13*(mitmSolution.Rounds-round)+10.5)+") [scale=1.5]{\\textbf{\\huge $A^{("+round+")}$}};\n";
            //DA
            for (int k = 0; k < 32; k++)
            {
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 4; j++)
                    {
                        if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 0 & mitmSolution.DA[round][i][j][k][2] == 0)
                            output += "\\fill[color="+WhiteColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                        if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 1)
                            output += "\\fill[color="+RedColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                        if (mitmSolution.DA[round][i][j][k][0] == 1 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 0)
                            output += "\\fill[color="+BlueColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                        if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 0)
                            output += "\\fill[color="+PurpleColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                        if (mitmSolution.DA[round][i][j][k][0] == 1 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 1)
                            output += "\\fill[color="+GrayColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                    }
                output += "\\draw("+(5*k)+","+(13*(mitmSolution.Rounds-round)+9)+") grid ++(4,3);\n";
                output += " \\node[align=center] at ("+(5*k+2)+","+(13*(mitmSolution.Rounds-round)+8)+")[scale=2] {{\\Large z="+k+"}};\n";
            }

            if (round == 0) {
                int allred=0;
                int allblue=0;
                for (int k = 0; k < 32; k++)
                    for (int j = 0; j < 4; j++) {
                        if (mitmSolution.DA[round][0][j][k][0] == 0 & mitmSolution.DA[round][0][j][k][1] == 1 & mitmSolution.DA[round][0][j][k][2] == 1)
                            allred++;
                        if (mitmSolution.DA[round][0][j][k][0] == 1 & mitmSolution.DA[round][0][j][k][1] == 1 & mitmSolution.DA[round][0][j][k][2] == 0)
                            allblue++;
                    }
                output += "\\fill[color="+RedColor+"] ("+(5*31+4+2)+","+(13*(mitmSolution.Rounds-round)+11)+")  rectangle ++(1,1);\n";
                output += "\\draw("+(5*31+4+2)+","+(13*(mitmSolution.Rounds-round)+11)+")  grid ++(1,1);\n";
                output += " \\node[align=center] at ("+(5*31+4+4.2)+","+(13*(mitmSolution.Rounds-round)+11.4)+") {\\textbf{\\huge = "+(allred)+"}};\n";
                output += "\\fill[color="+BlueColor+"] ("+(5*31+4+2)+","+(13*(mitmSolution.Rounds-round)+9)+")  rectangle ++(1,1);\n";
                output += "\\draw("+(5*31+4+2)+","+(13*(mitmSolution.Rounds-round)+9)+") grid ++(1,1);\n";
                output += " \\node[align=center] at ("+(5*31+4+4.1)+","+(13*(mitmSolution.Rounds-round)+9.4)+") {\\textbf{\\huge = "+(allblue)+"}};\n";
            }


            output += " \\node[align=center] at ("+(-2)+","+(13*(mitmSolution.Rounds-round)+5.5)+") [scale=1.5]{\\textbf{\\huge $S^{("+round+")}$}};\n";
            //DP
            for (int k = 0; k < 32; k++)
            {
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 4; j++)
                    {
                        if (mitmSolution.DP[round][i][j][k][0] == 0 & mitmSolution.DP[round][i][j][k][1] == 0 & mitmSolution.DP[round][i][j][k][2] == 0)
                            output += "\\fill[color="+WhiteColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+4)+") rectangle ++(1,1);\n";
                        if (mitmSolution.DP[round][i][j][k][0] == 0 & mitmSolution.DP[round][i][j][k][1] == 1 & mitmSolution.DP[round][i][j][k][2] == 1)
                            output += "\\fill[color="+RedColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+4)+") rectangle ++(1,1);\n";
                        if (mitmSolution.DP[round][i][j][k][0] == 1 & mitmSolution.DP[round][i][j][k][1] == 1 & mitmSolution.DP[round][i][j][k][2] == 0)
                            output += "\\fill[color="+BlueColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+4)+") rectangle ++(1,1);\n";
                        if (mitmSolution.DP[round][i][j][k][0] == 0 & mitmSolution.DP[round][i][j][k][1] == 1 & mitmSolution.DP[round][i][j][k][2] == 0)
                            output += "\\fill[color="+PurpleColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+4)+") rectangle ++(1,1);\n";
                        if (mitmSolution.DP[round][i][j][k][0] == 1 & mitmSolution.DP[round][i][j][k][1] == 1 & mitmSolution.DP[round][i][j][k][2] == 1)
                            output += "\\fill[color="+GrayColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+4)+") rectangle ++(1,1);\n";
                    }
                output += "\\draw("+(5*k)+","+(13*(mitmSolution.Rounds-round)+4)+") grid ++(4,3);\n";
            }

            output += " \\node[align=center] at ("+(-2)+","+(13*(mitmSolution.Rounds-round)+1.5)+") [scale=1.5]{\\textbf{\\huge $C^{("+round+")}$}};\n";
            //DB
            for (int k = 0; k < 32; k++)
            {
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 4; j++)
                    {
                        if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 0 & mitmSolution.DB[round][i][j][k][2] == 0)
                            output += "\\fill[color="+WhiteColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i))+") rectangle ++(1,1);\n";
                        if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 1)
                            output += "\\fill[color="+RedColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i))+") rectangle ++(1,1);\n";
                        if (mitmSolution.DB[round][i][j][k][0] == 1 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 0)
                            output += "\\fill[color="+BlueColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i))+") rectangle ++(1,1);\n";
                        if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 0)
                            output += "\\fill[color="+PurpleColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i))+") rectangle ++(1,1);\n";
                        if (mitmSolution.DB[round][i][j][k][0] == 1 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 1)
                            output += "\\fill[color="+GrayColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i))+") rectangle ++(1,1);\n";
                    }
                output += "\\draw("+(5*k)+","+(13*(mitmSolution.Rounds-round))+") grid ++(4,3);\n";
            }

            int consumered3 = 0;
            for (int k = 0; k < 32; k++)
            {
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 4; j++)
                    {
                        //DC2
                        if (mitmSolution.DC1[round][i][j][k] == 1) {
                            consumered3 ++;
                            output += "\\draw[line width=2pt, color=red]("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i))+") grid ++(1,1);\n";
                        }
                    }
            }
            output += "\\fill[color="+RedColor+"] ("+(5*31+4+4)+","+(13*(mitmSolution.Rounds-round)+1)+")  rectangle ++(1,1);\n";
            output += "\\draw("+(5*31+4+4)+","+(13*(mitmSolution.Rounds-round)+1)+")  grid ++(1,1);\n";
            output += " \\node[align=center] at ("+(5*31+4+2.5)+","+(13*(mitmSolution.Rounds-round)+1.4)+") {\\textbf{\\huge - "+(consumered3)+"}};\n";


            output += "\n";

        }

        int round = mitmSolution.Rounds;
        output += " \\node[align=center] at ("+(-2)+","+(13*(mitmSolution.Rounds-round)+10.5)+") [scale=1.5]{\\textbf{\\huge $A^{("+round+")}$}};\n";
        //DA
        for (int k = 0; k < 32; k++)
        {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 4; j++)
                {
                    if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 0 & mitmSolution.DA[round][i][j][k][2] == 0)
                        output += "\\fill[color="+WhiteColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                    if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 1)
                        output += "\\fill[color="+RedColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                    if (mitmSolution.DA[round][i][j][k][0] == 1 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 0)
                        output += "\\fill[color="+BlueColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                    if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 0)
                        output += "\\fill[color="+PurpleColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                    if (mitmSolution.DA[round][i][j][k][0] == 1 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 1)
                        output += "\\fill[color="+GrayColor+"] ("+(5*k+j)+","+(13*(mitmSolution.Rounds-round)+(2-i)+9)+") rectangle ++(1,1);\n";
                }
            output += "\\draw("+(5*k)+","+(13*(mitmSolution.Rounds-round)+9)+") grid ++(4,3);\n";
        }



        // Footer
        output += "\\makeatother\n";
        output += "\\end{tikzpicture}\n";
        output += "\\end{document}\n";
        return output;
    }
}
