package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class VarNode implements Node {

  private String id;
  private Node type;
  private Node exp;
  
  public VarNode (String i, Node t, Node v) {
    id=i;
    type=t;
    exp=v;
  }
  
  	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
  	//create result list
  		
  	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();

  	  if (type instanceof ClassTypeNode) {
  		  String classId=((ClassTypeNode)type).getId();
  		  if(env.getClassVarMap().get(classId)!=null) {
  			  type=env.getClassVarMap().get(classId).getEntry0(classId).getType();
  		  }
  		  else {
  			  res.add(new SemanticError("Class "+classId+" not defined"));
  		  }
  	  }
  	  STentry entry=new STentry(env.getVarMapActual().getNestingLevel(), type, env.getVarMapActual().getOffset());
  	  if (env.getVarMapActual().putEntry(id, entry)!=null) {
  		  res.add(new SemanticError("Var "+id+" already declared"));
  	  }
  	  if (exp instanceof NewNode && type instanceof ClassTypeNode) {
  		  if (!((NewNode)exp).getId().equals(((ClassTypeNode)type).getId())){
  	  		  System.out.println("A2");

  			  ((ClassTypeNode)type).setIdType(((NewNode)exp).getId());
  		  }
  	  }
  	  res.addAll(exp.checkSemantics(env));
  	  env.getVarMapActual().decrementOffset();
  	  /*HashMap<String,STentry> hm = env.getSymTable().get(env.getNestingLevel());
        STentry entry = new STentry(env.getNestingLevel(),type, env.getOffset()); //separo introducendo "entry"
        env.decrementOffset();
        if ( hm.put(id,entry) != null )
          res.add(new SemanticError("Var id "+id+" already declared"));
        
        res.addAll(exp.checkSemantics(env));*/
        
        return res;
	}
  public Node getType() {
	  return type;
  }
  public String getId() {
	  return id;
  }
  public String toPrint(String s) {
	return s+"Var:" + id +"\n"
	  	   +type.toPrint(s+"  ")  
           +exp.toPrint(s+"  "); 
  }
  
  //valore di ritorno non utilizzato
  public Node typeCheck () {
    if (! (FOOLlib.isSubtype(exp.typeCheck(),type)) ){      
      System.out.println("incompatible value for variable "+id);
      System.exit(0);
    }     
    return null;
  }
  
  public String codeGeneration() {
		return exp.codeGeneration();
  }  
    
}  