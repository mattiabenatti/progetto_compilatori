package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class GEqualNode implements Node {

  private Node left;
  private Node right;
  
  public GEqualNode (Node l, Node r) {
    left=l;
    right=r;
  }
  
  public String toPrint(String s) {
    return s+"GreaterOrEqual\n" + left.toPrint(s+"  ")   
                       + right.toPrint(s+"  ") ; 
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
  
  public Node typeCheck() {
    Node l = left.typeCheck();
    Node r = right.typeCheck();
    if (! ( FOOLlib.isSubtype(l,new IntTypeNode()) || FOOLlib.isSubtype(r,new IntTypeNode()) ) ) {
      System.out.println("Require integer types in GreaterOrEqual");
      System.exit(0);
    }
    return new BoolTypeNode();
  }  
  
  public String codeGeneration() {
	  String l1 = FOOLlib.freshLabel(); 
	  String l2 = FOOLlib.freshLabel();
	  
	  /*return left.codeGeneration()+
			   right.codeGeneration()+
			   "bleq "+ l1 +"\n"+
			   "push 0\n"+
			   "b " + l2 + "\n" +
			   l1 + ":\n"+
			   "push 1\n" +
			   l2 + ":\n";*/
	  return left.codeGeneration()+
	  "push -1\n"+
	  "mult\n"+
	   right.codeGeneration()+
	   "push -1\n"+
	   "mult\n"+
	   "bleq "+ l1 +"\n"+
	   "push 0\n"+
	   "b " + l2 + "\n" +
	   l1 + ":\n"+
	   "push 1\n" +
	   l2 + ":\n";
	   
		       
  }
  
}  