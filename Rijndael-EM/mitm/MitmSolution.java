package mitmsearch.mitm;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.io.File;
import java.io.IOException;

public class MitmSolution {
  public int Rounds;
  public int keyRounds;
  public int regime;
  public int fRounds;
  public int bRounds;
  public int KeyStartr;
  public int StateStartr;
  public int objective;
  public int[][][][][] Af;
  public int[][][][][] SBf;
  public int[][][][][] MCf;
  public int[][][][] DCf;
  public int[][][][] DCfAK;
  public int[][][][][] Ab;
  public int[][][][][] SBb;
  public int[][][][][] MCb;
  public int[][][][] DCb;
  public int[][][][] DCbAK;
  public int[][] dom;
  public int[] obj;
  public MitMSolutionKey Key;

  public MitmSolution() {}

  public MitmSolution(int Rounds, int keyRounds, int regime, int fRounds, int bRounds, int KeyStartr, int StateStartr, int objective, int[][][][][] Af, int[][][][][] SBf, int[][][][][] MCf, int[][][][] DCf, int[][][][] DCfAK, int[][][][][] Ab, int[][][][][] SBb, int[][][][][] MCb, int[][][][] DCb, int[][][][] DCbAK, MitMSolutionKey Key, int[][] dom, int[] obj) {
    this.Rounds = Rounds;
    this.keyRounds = keyRounds;
    this.regime = regime;
    this.fRounds = fRounds;
    this.bRounds = bRounds;
    this.KeyStartr = KeyStartr;
    this.StateStartr = StateStartr;
    this.objective = objective;
 
    this.Af = Af;
    this.SBf = SBf;
    this.MCf = MCf;
    this.DCf = DCf;
    this.DCfAK = DCfAK;
    this.Ab = Ab;
    this.SBb = SBb;
    this.MCb = MCb;
    this.DCb = DCb;
    this.DCbAK = DCbAK;
    this.dom = dom;
    this.obj = obj;
    this.Key = Key;
  }

  public void toFile(String fileName) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(fileName), this);
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
  }

  public static void toFile(String fileName, List<MitmSolution> solutions) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(fileName), solutions);
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
  }

  public static void toFile(String fileName, MitmSolution solutions) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(fileName), solutions);
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
  }

  public static void toFile(File file, List<MitmSolution> solutions) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(file, solutions);
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
  }

  public static List<MitmSolution> fromFile(String fileName) {
    return fromFile(new File(fileName));
  }

  public static List<MitmSolution> fromFile(File file) {
    try {
      return new ObjectMapper().readValue(file, new TypeReference<List<MitmSolution>>(){});
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
    return null; // Can't reach
  }
}
