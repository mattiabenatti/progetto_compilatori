package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class IfStmNode implements Node {

  private Node cond;
  private ArrayList<Node> th;
  private ArrayList<Node> el;
  
  public IfStmNode (Node c, ArrayList<Node> t, ArrayList<Node> e) {
    cond=c;
    th=t;
    el=e;
  }
  
  public String toPrint(String s) {
	String thstr="";
	String elstr="";
	for (Node tnode:th) 
    	thstr+=tnode.toPrint(s+" ");
	for (Node enode:el) 
    	elstr+=enode.toPrint(s+" ");
    return s+"If\n" + cond.toPrint(s+"  ") 
                    + thstr   
                    + elstr; 
  }
  
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  //create the result
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();
	  
	  //check semantics in the condition
	  res.addAll(cond.checkSemantics(env));
	 	  
	  //check semantics in the then and in the else exp
	  for (Node tnode:th) 
		  res.addAll(tnode.checkSemantics(env));
	  if (el!=null) {
	      for (Node enode:el) 
	    	  res.addAll(enode.checkSemantics(env));
	  }
	  
	  return res;
	}
  
  
  public Node typeCheck() {
    if (!(FOOLlib.isSubtype(cond.typeCheck(),new BoolTypeNode()))) {
      System.out.println("non boolean condition in if");
      System.exit(0);
    }
	for (Node tnode:th) 
		tnode.typeCheck();
    for (Node enode:el) 
    	enode.typeCheck();

    return null;
  }
  
  public String codeGeneration() {
	  String l1 = FOOLlib.freshLabel(); 
	  String l2 = FOOLlib.freshLabel();
	  String thCode="";
	  String elCode="";
	  for (Node tnode:th)
		  thCode+=tnode.codeGeneration();
      for (Node enode:el) 
    	  elCode+=enode.codeGeneration();
      
	  return cond.codeGeneration()+
			 "push 1\n"+
			 "beq "+ l1 +"\n"+			  
			 elCode+
			 "b " + l2 + "\n" +
			 l1 + ":\n"+
			 thCode+
	         l2 + ":\n"; 
  }
  
}  