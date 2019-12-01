package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class ProgStmNode implements Node {

  ArrayList<Node> listDeclarations;
  
  ArrayList<Node> listStms;
  
  public ProgStmNode (ArrayList<Node> listDeclarations, ArrayList<Node> listStms) {
	  
	this.listDeclarations=listDeclarations;
    this.listStms=listStms;
  }
  
  public ArrayList<Node> getListDeclarations() {
		return listDeclarations;
	}

	public void setListDeclarations(ArrayList<Node> listDeclarations) {
		this.listDeclarations = listDeclarations;
	}

	public ArrayList<Node> getListStms() {
		return listStms;
	}

	public void setListStms(ArrayList<Node> listStms) {
		this.listStms = listStms;
	}

  
  public String toPrint(String s) {
	  String strdec = "";
	  String strstm="";
		for (Node dec : listDeclarations)
			strdec += dec.toPrint(s + "  ");
		
		for (Node stm : listStms)
			strstm += stm.toPrint(s + "  ");
		
		return s + "ProgStm\n" + strdec + strstm;
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  /*
	  env.nestingLevel++;
      HashMap<String,STentry> hm = new HashMap<String,STentry> ();
      env.symTable.add(hm);
      
      //declare resulting list
      ArrayList<SemanticError> res = new ArrayList<SemanticError>();
      if(clas.size() > 0){
    	  env.offset = -2;
    	  //if there are children then check semantics for every child and save the results
    	  for(Node c : clas)
    		  res.addAll(c.checkSemantics(env));
      }
      //check semantics in the dec list
      if(listDeclarations.size() > 0){
    	  if (clas==null)
    		  env.offset = -2;
    	  //if there are children then check semantics for every child and save the results
    	  for(Node n : listDeclarations)
    		  res.addAll(n.checkSemantics(env));
      }
      
      //check semantics in the exp body
      res.addAll(exp.checkSemantics(env));
      if (listStms!=null && listStms.size()>0) {
    	  for (Node n:listStms)
    		  res.addAll(n.checkSemantics(env));
      }
      
      //clean the scope, we are leaving a let scope
      env.symTable.remove(env.nestingLevel--);
      
      //return the result
      return res;
      */
	  
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
	  	for (Node stmNode:listStms) {
	  		res.addAll(stmNode.checkSemantics(env));
	  	}
	  	
	  	env.getVarMapActual().removeHashMap();
	  	env.getVarMapActual().decrementNestingLevel();
	  	
	  	return res;
	}
  
  public Node typeCheck () {
    for (Node dec:listDeclarations)
      dec.typeCheck();
    if (listStms!=null)
    	for (Node n:listStms)
    		n.typeCheck();
    return new VoidTypeNode();
  }
  
  public String codeGeneration() {
	  /*
	  String clasCode="";
	  String declCode="";
	  String stmCode="";
	  if (clas!=null)
		  for (Node c:clas)
			  clasCode+=c.codeGeneration();
	  for (Node dec:listDeclarations)
		    declCode+=dec.codeGeneration();
	  if (listStms!=null)
		  for (Node n:listStms) 
			  stmCode+=n.codeGeneration();
	  return  "push 0\n"+
			  clasCode+
			  declCode+
			  stmCode+
			  exp.codeGeneration()+"halt\n"+
			  FOOLlib.getCode();
	  */
	  String strDec="";
		String strStm="";
		  for (Node declarationNode:listDeclarations) {
			  if (declarationNode instanceof FunNode) {
				  strDec+=declarationNode.codeGeneration();
			  }
		  }
		  for (Node declarationNode:listDeclarations) {
			  if (! (declarationNode instanceof FunNode)) {
				  strDec+=declarationNode.codeGeneration();
			  }
		  }
		  if (listStms!=null) {
			  for (Node stmNode:listStms) {
				  strStm+=stmNode.codeGeneration();
			  }
		  }
			return "push 0\n"+
					strDec+
					strStm+
					"halt\n"+FOOLlib.getCode();
  } 
  
  
    
}  