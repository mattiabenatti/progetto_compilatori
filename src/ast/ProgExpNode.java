package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class ProgExpNode implements Node {

  Node exp;
  ArrayList<Node> listDeclarations;
  
  public ProgExpNode (ArrayList<Node> listDeclarations, Node exp) {
    this.exp=exp;
    this.listDeclarations=listDeclarations;
  }
  
  public Node getExp() {
	return exp;
  }

  public void setExp(Node exp) {
	this.exp = exp;
  }

  public ArrayList<Node> getListDeclarations() {
	return listDeclarations;
  }

  public void setListDeclarations(ArrayList<Node> listDeclarations) {
	this.listDeclarations = listDeclarations;
  }

  public String toPrint(String s) {
    
	String strDec="";
	if (listDeclarations!=null) {
		for (Node declaration:listDeclarations) {
			strDec+=declaration.toPrint(s+" ");
		}
	}
    return "ProgExp\n" + strDec+ exp.toPrint("  ") ;
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
	  	ArrayList<SemanticError> res=new ArrayList<SemanticError>();
	  	
	  	env.getVarMapActual().putHashMap();
	  	env.getVarMapActual().incrementNestingLevel();
	  	
	  	if (listDeclarations!=null) {
	  		if(listDeclarations.size()>0) {
	  			env.getVarMapActual().setOffset(-2);
	  			for (Node declarationNode:listDeclarations) {
	  				res.addAll(declarationNode.checkSemantics(env));
	  			}
	  		}
	  	}
	  	res.addAll(exp.checkSemantics(env));
	  	try {
		  	env.getVarMapActual().removeHashMap();
		  	env.getVarMapActual().decrementNestingLevel();
	  	}
	  	catch(Exception e) {
	  		e.printStackTrace();
	  	}
	  	
		return res;
	}
  
  public Node typeCheck() {
	for (Node declarationNode:listDeclarations) {
		declarationNode.typeCheck();
	}
    return exp.typeCheck();
  }  
  
  public String codeGeneration() {
	  String decCode="";
	 
	  for (Node declarationNode:listDeclarations) {
			  decCode+=declarationNode.codeGeneration();
	  }
	  
		return "push 0\n"+
				decCode+exp.codeGeneration()+
				"halt\n"+FOOLlib.getCode();
  }  
  
}  