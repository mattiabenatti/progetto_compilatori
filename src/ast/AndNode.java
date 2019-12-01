package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class AndNode implements Node {

  private Node left;
  private Node right;
  
  public AndNode (Node l, Node r) {
    left=l;
    right=r;
  }
  
  @Override
 	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  //create the result
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();
	  
	  //check semantics in the left and in the right exp
	  
	  res.addAll(left.checkSemantics(env));
	  res.addAll(right.checkSemantics(env));
	  
 	  return res;
 	}
  
  public String toPrint(String s) {
    return s+"And\n" + left.toPrint(s+"  ")  
                      + right.toPrint(s+"  ") ; 
  }
  
  public Node typeCheck() {
    if (! ( FOOLlib.isSubtype(left.typeCheck(),new BoolTypeNode()) &&
            FOOLlib.isSubtype(right.typeCheck(),new BoolTypeNode()) ) ) {
      System.out.println("Non booleans in and");
      System.exit(0);
    }
    return new BoolTypeNode();
  }
  
  public String codeGeneration() {
	  String l1 = FOOLlib.freshLabel(); 
	  String l2 = FOOLlib.freshLabel();
		return left.codeGeneration()+
			   right.codeGeneration()+
			   "add\n"+
			   "push 2\n"+
			   "beq "+ l1 +"\n"+
			   "push 0\n"+
			   "b " + l2 + "\n" +
			   l1 + ":\n"+
			   "push 1\n" +
			   l2 + ":\n";
  }
  
} 