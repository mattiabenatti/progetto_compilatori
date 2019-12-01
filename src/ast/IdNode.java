package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class IdNode implements Node {

  private String id;
  private STentry entry;
  private int nestinglevel;
  
  public IdNode (String i) {
    id=i;
  }
  
  public String toPrint(String s) {
	return s+"Id:" +id+ " at nestlev " + nestinglevel +"\n" + entry.toPrint(s+"  ") ;  
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  
	  //create result list
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();
	  STentry tmp=env.getVarMapActual().getEntry(id); 
	  if (tmp==null) {  
		  res.add(new SemanticError("Id "+id+" not declared"));  
	  }
	  else {
		  entry=tmp;
		  nestinglevel=env.getVarMapActual().getNestingLevel();
	  }
	  
	  /*while (j>=0 && tmp==null)
		  tmp=(env.symTable.get(j--)).get(id);
      if (tmp==null)
          res.add(new SemanticError("Id "+id+" not declared"));
      
      else{
    	  entry = tmp;
    	  nestinglevel = env.getNestingLevel();
      }*/
	  
	  return res;
	}
  
  public Node typeCheck () {
	if (entry.getType() instanceof ArrowTypeNode) { //
	  System.out.println("Wrong usage of function identifier");
      System.exit(0);
    }/*
	if (pref!=null) {
		if (pref.contentEquals("-")&& !FOOLlib.isSubtype(entry.getType(), new IntTypeNode())) {
		      System.out.println("Cannot use - operator for non-integer ids");
		      System.exit(0);
		}
		else if (pref.contentEquals("!")&& !FOOLlib.isSubtype(entry.getType(), new BoolTypeNode())) {
		      System.out.println("Cannot use ! operator for non-boolean ids");
		      System.exit(0);
		}
	}
    return entry.getType();*/
	  
	  return entry.getType();
  }
  
  public String codeGeneration() {
	  
	  //String l1 = FOOLlib.freshLabel(); 
	  //String l2 = FOOLlib.freshLabel();
	  
      String getAR="";
      String ret="";
      /*
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
	  for (int i=0; i<nestinglevel-entry.getNestinglevel(); i++) 
	    	 getAR+="lw\n";
	    return "push "+entry.getOffset()+"\n"+ //metto offset sullo stack
		       "lfp\n"+getAR+ //risalgo la catena statica
			   "add\n"+ 
               "lw\n"+ //carico sullo stack il valore all'indirizzo ottenuto
	    	   end;
	    	   */
      
      int offset;
	  if (entry.isInClass()) {
		  for (int i=1;i<nestinglevel-entry.getNestinglevel();i++) {
			  getAR+="push 1\n"+"add\n"+"lw\n";
		  }
		  offset=-1*entry.getOffset();
		  ret="push 1\n"+
				  "lfp\n"+
				  "add\n"+
				  "lw\n"+
				  getAR+
				  "push "+(offset-1)+"\n"+
				  "add\n"+
				  "lw\n";
	  }
	  else {
		  for (int i=0;i<nestinglevel-entry.getNestinglevel();i++) {
			  getAR+="lw\n";
		  }
		  offset=entry.getOffset();
		  ret= "push "+offset+"\n"+
				  "lfp\n"+getAR+
				  "add\n"+ 
				  "lw\n";
				  
				  
	  }
				  
	  return ret;
  }
}  