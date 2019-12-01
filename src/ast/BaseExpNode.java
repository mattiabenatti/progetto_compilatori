package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class BaseExpNode implements Node {

  private String pref;
  private Node exp;
  
  public BaseExpNode (String p, Node e) {
    pref=p;
    exp=e;
  }
  
  public String toPrint(String s) {
    return s+pref+"\n" + exp.toPrint(s+" "); 
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  //create the result
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();
	  
	  //check semantics in the left and in the right exp
	  
	  res.addAll(exp.checkSemantics(env));
	  
	  return res;
	}
  
  public Node typeCheck() {
    Node e = exp.typeCheck();
    if (pref!=null) {
		if (pref.contentEquals("-")&& !FOOLlib.isSubtype(e, new IntTypeNode())) {
		      System.out.println("Cannot use - operator for non-integer exps");
		      System.exit(0);
		}
		else if (pref.contentEquals("!")&& !FOOLlib.isSubtype(e, new BoolTypeNode())) {
		      System.out.println("Cannot use ! operator for non-boolean exps");
		      System.exit(0);
		}
    }
    return e;
  }  
  
  public String codeGeneration() {
	  String l1 = FOOLlib.freshLabel(); 
	  String l2 = FOOLlib.freshLabel();
	  String end = "";
	  if (pref!=null) {
		  if (pref.contentEquals("-")) {
    		  end="push -1\n"+
    			  "mult\n";
    	  }
    	  else {
    		  end= 	   "push 0\n"+
    				   "beq "+ l1 +"\n"+
    				   "push 0\n"+
    				   "b " + l2 + "\n" +
    				   l1 + ":\n"+
    				   "push 1\n" +
    				   l2 + ":\n";
    			       
    		  
    	  }
	  }
	  return exp.codeGeneration()+
			   end;
		       
  }
  
}  