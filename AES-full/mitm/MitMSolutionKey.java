package mitmsearch.mitm;

public class MitMSolutionKey {
  public int[][][][][] RK;
  public int[][][][][] S;
  public int[][][][][] K;
  public int[][][][] DCK;
  public int[][][][] DCS;
  public int[][][] GuessS;

  public MitMSolutionKey() {}

  public MitMSolutionKey(final int[][][][][] RK, final int[][][][][] S, final int[][][][][] K, final int[][][][] DCK, final int[][][][] DCS, final int[][][] GuessS) {
    this.RK = RK;
    this.S = S;
    this.K = K;
    this.DCK = DCK;
    this.DCS = DCS;
    this.GuessS = GuessS;
  }
}
