package lib;

import ast.*;

public class FOOLlib {
  
  private static int labCount=0; 
  
  private static int funLabCount=0; 
  
  private static int classLabCount=0;

  private static String funCode=""; 

  //valuta se il tipo "a" � <= al tipo "b", dove "a" e "b" sono tipi di base: int o bool
  public static boolean isSubtype (Node a, Node b) {
	  if(a instanceof ClassTypeNode) {
		  if(!(b instanceof ClassTypeNode)) {
			  return false;
		  }
		  else {
			  ClassTypeNode aCasted=(ClassTypeNode) a;
			  ClassTypeNode bCasted=(ClassTypeNode) b;
			  ClassTypeNode tmp=(ClassTypeNode)bCasted;
			  if (aCasted.getId().equals(tmp.getId())) {
				  return true;
			  }
			  if(aCasted.getListTypeParameters()==null) {
				  if (tmp.getListTypeParameters()!=null) {
					  return false;
				  }
			  }
			  else {
				  if (aCasted.getParentTypeNode()!=null) {
					  boolean check=false;
					  boolean loop=true;
					  ClassTypeNode tmpCheck=aCasted;
					  while(loop) {
						  if (tmp.getId().equals(tmpCheck.getId())) {
							  check=true;
							  loop=false;
						  }
						  else {
							  if(tmpCheck.getParentTypeNode()!=null) {
								  tmpCheck=tmpCheck.getParentTypeNode();
							  }
							  else {
								  loop=false;
							  }
						  }
					  }
					  if (!check) {
						  return false;
					  }
				  }
				  else if (!aCasted.getId().equals(tmp.getId())) {
					  return false;
				  }
				  
				  if (tmp.getListTypeParameters().size()>aCasted.getListTypeParameters().size()) {
					  return false;
				  }
				  for (int i=0;i<tmp.getListTypeParameters().size();i++) {
					  if(!isSubtype(tmp.getListTypeParameters().get(i), aCasted.getListTypeParameters().get(i))){
						  return false;
					  }
				  }
			  }
			  if (aCasted.getListTypeMet()==null) {
				  if(tmp.getListTypeMet()!=null) {
					  return false;
				  }
			  }
			  else {
				  if(tmp.getListTypeMet().size()>aCasted.getListTypeMet().size()) {
					  return false;
				  }
				  for (int i=0;i<tmp.getListTypeMet().size();i++) {
					  if(!isSubtype(aCasted.getListTypeMet().get(i), tmp.getListTypeMet().get(i))) {
						  return false;
					  }
				  }
			  }
		  }
	  }
	  
	  return a.getClass().equals(b.getClass());	  
  }

public static String freshLabel() { 
	return "label"+(labCount++);
  } 

  public static String freshFunLabel() { 
	return "function"+(funLabCount++);
  } 
  
  public static String freshClassLabel() {
	  return "class"+(classLabCount++);
  }
  
  public static void putCode(String c) { 
    funCode+="\n"+c; //aggiunge una linea vuota di separazione prima di funzione
  } 
  
  public static String getCode() { 
    return funCode;
  } 


}