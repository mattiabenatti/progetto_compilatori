package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class VoidNode implements Node {
  
  public VoidNode () {
   
  }
  
  public String toPrint(String s) {
    return s+"void\n"; 
  }
  
  public Node typeCheck() {
    return new VoidTypeNode();
  }    
  
  @Override
 	public ArrayList<SemanticError> checkSemantics(Environment env) {

 	  return new ArrayList<SemanticError>();
 	}
  
  public String codeGeneration() {
	  	return "";
	  }
         
}  