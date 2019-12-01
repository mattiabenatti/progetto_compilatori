package ast;
import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class ArrowTypeNode implements Node {

  private ArrayList<Node> parlist; 
  private Node ret;
  private boolean methodImplementation;

  
  public ArrowTypeNode (ArrayList<Node> p, Node r, boolean methodImplementation) {
    parlist=p;
    ret=r;
    this.methodImplementation=methodImplementation;
  }
    
  public String toPrint(String s) { //
	String parlstr="";
    for (Node par:parlist)
      parlstr+=par.toPrint(s+"  ");
	return s+"ArrowType\n" + parlstr + ret.toPrint(s+"  ->") ; 
  }
  
  public Node getRet () { //
    return ret;
  }
  
  public ArrayList<Node> getParList () { //
    return parlist;
  }
  

  public boolean isMethodImplementation() {
	return methodImplementation;
}

public void setMethodImplementation(boolean methodImplementation) {
	this.methodImplementation = methodImplementation;
}

@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		return new ArrayList<SemanticError>();
	}
  
  //non utilizzato
  public Node typeCheck () {
    return null;
  }

  //non utilizzato
  public String codeGeneration() {
		return "";
  }

}  