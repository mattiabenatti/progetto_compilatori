package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class FunNode implements Node {

  

  String id;
  Node type; 
  String label;
  ArrayList<Node> parlist = new ArrayList<Node>(); 
  ArrayList<Node> declist = new ArrayList<>(); 
  ArrayList<Node> bodylist=new ArrayList<>();
  int nNew=0;
  
  public FunNode (String i, Node t) {
    id=i;
    type=t;
  }
  public FunNode(String id, Node type, ArrayList<Node> parlist, ArrayList<Node> declist, ArrayList<Node> bodylist) {
		this.id = id;
		this.type = type;
		this.parlist = parlist;
		this.declist = declist;
		this.bodylist = bodylist;
	}
  public String getId() {
		return id;
	}

	public ArrayList<Node> getParlist() {
		return parlist;
	}

	public ArrayList<Node> getDeclist() {
		return declist;
	}
	public ArrayList<Node> getBodylist() {
		return bodylist;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setBodylist(ArrayList<Node> bodylist) {
		this.bodylist = bodylist;
	}
  public void addDecBody (ArrayList<Node> d, ArrayList<Node> b) {
    declist=d;
    bodylist=b;
  }
  public Node getType() {
	  return type;
  }
  public ArrayList<Node> getBody() {
	  return bodylist;
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  
	  //create result list
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();
	  
	  //env.offset = -2;
      STentry entry = new STentry(env.getVarMapActual().getNestingLevel(),env.getVarMapActual().getOffset());//separo introducendo "entry"
      env.getVarMapActual().decrementOffset();
      
      if ( env.getVarMapActual().putEntry(id, entry) != null )
        res.add(new SemanticError("Fun id "+id+" already declared"));
      else{
    	  //creare una nuova hashmap per la symTable
	      
	      env.getVarMapActual().putHashMap();
	      env.getVarMapActual().incrementNestingLevel();
	      
	      ArrayList<Node> parTypes = new ArrayList<Node>();
	      int paroffset=1;
	      
	      //check args
	      for(Node a : parlist){
	    	  ParNode arg = (ParNode) a;
	    	  arg.checkSemantics(env);
	    	  parTypes.add(arg.getType());
	    	  if ( env.getVarMapActual().putEntry(arg.getId(), new STentry(env.getVarMapActual().getNestingLevel(),arg.getType(),paroffset)) != null) {
	    		  System.out.println("Parameter id "+arg.getId()+" already declared");
	    	  }
	    	  paroffset++;
              
	      }
	      if (type instanceof ClassTypeNode) {
	    	  ClassTypeNode classType=(ClassTypeNode)type;
	    	  String idClass=classType.getId();
	    	  for (String string:env.getClassVarMap().keySet()) {
	    		  System.out.println(string);
	    	  }
	    	  if (env.getClassVarMap().get(idClass)!=null) {
	    		  classType=(ClassTypeNode)env.getClassVarMap().get(idClass).getEntry0(idClass).getType();
	    		  type=classType;
	    	  }
	    	  else {
	    		  res.add(new SemanticError("Class "+idClass+" is not declared"));
	    	  }
	      }
	      
	      //set func type
	      entry.addType( new ArrowTypeNode(parTypes, type,false) );
	      
	    //check semantics in the dec list
	      if(declist.size() > 0){
	    	  env.getVarMapActual().setOffset(-2);
	    	  //if there are children then check semantics for every child and save the results
	    	  for(Node n : declist)
	    		  res.addAll(n.checkSemantics(env));
	      }
	     
	      //check body
	      for (Node bodyNode:bodylist) {
	    	  res.addAll(bodyNode.checkSemantics(env));
	      }
	      
	      //res.addAll(body.checkSemantics(env));
	      
	      //close scope
	      env.getVarMapActual().removeHashMap();
	      env.getVarMapActual().decrementNestingLevel();
	      	      
      }
      
      return res;
	}
  
  public void addPar (Node p) {
    parlist.add(p);
  }  
  
  public String toPrint(String s) {
	String parlstr="";
	String bodystr="";
	for (Node par:parlist)
	  parlstr+=par.toPrint(s+"  ");
	String declstr="";
	if (declist!=null) 
	  for (Node dec:declist)
	    declstr+=dec.toPrint(s+"  ");
	if (bodylist!=null) 
		  for (Node b:bodylist)
		    bodystr+=b.toPrint(s+"  ");
    return s+"Fun:" + id +"\n"
		   +type.toPrint(s+"  ")
		   +parlstr
	   	   +declstr
           +bodystr; 
  }
  
  //valore di ritorno non utilizzato
  public Node typeCheck () {
	if (declist!=null) 
	  for (Node dec:declist)
		dec.typeCheck();
	for (Node bodyNode:bodylist) {
		if (!FOOLlib.isSubtype(bodyNode.typeCheck(), type)) {
			System.out.println("Wrong return type for function "+id);
		    System.exit(0);
		}
	}
    /*if ( !(FOOLlib.isSubtype(body.typeCheck(),type)) ){
      System.out.println("Wrong return type for function "+id);
      System.exit(0);
    } */ 
    return null;
  }
  
  public String codeGeneration() {
	  
	    String declCode="";
	    if (declist!=null) for (Node dec:declist)
		    declCode+=dec.codeGeneration();
	    
	    String popDecl="";
	    if (declist!=null) {
	    	for (Node dec:declist) {
	    		popDecl+="pop\n";
	    	}
	    	for (int i=0;i<nNew;i++) {
	    		popDecl+="pop\n";
	    	}
	    }
	    	
	    
	    String popParl="";
	    for (Node dec:parlist)
	    	popParl+="pop\n";
	    
	    String body="";
	    for (Node nodeBody:bodylist) {
	    	body+=nodeBody.codeGeneration();
	    }
	    
	    String funl=FOOLlib.freshFunLabel(); 
	    label=funl;
	    FOOLlib.putCode(funl+":\n"+
	            "cfp\n"+ //setta $fp a $sp				
				"lra\n"+ //inserimento return address
	    		declCode+ //inserimento dichiarazioni locali
	    		//body.codeGeneration()+
	    		body+"srv\n"+ //pop del return value
	    		popDecl+"sra\n"+ // pop del return address
	    		"pop\n"+ // pop di AL
	    		popParl+
	    		"sfp\n"+  // setto $fp a valore del CL
	    		"lrv\n"+ // risultato della funzione sullo stack
	    		"lra\n"+"js\n" // salta a $ra
	    		);
	    
		return "push "+ funl +"\n";
  }
  
}  