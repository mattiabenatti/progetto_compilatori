package ast;

import java.util.ArrayList;
import java.util.HashSet;

import lib.FOOLlib;
import util.Environment;
import util.Environment.ClassMap;
import util.SemanticError;

public class ClassNode implements DepNode {

 

  private String id;
  ArrayList<ParNode> listParameters=new ArrayList<>();
  ArrayList<MetNode> listMethod=new ArrayList<>();
  String parentId=null;
  ClassNode parentNode=null;
  ClassTypeNode type=null;
  boolean gen=false;
  HashSet<String>allDep=new HashSet<>();
  public ClassNode (String i) {
	id=i;
   
  }
  public ClassNode(String id, String parentId) {
	  this.id=id;
	  this.parentId=parentId;
  }
  
  public String toPrint(String s) {
     return s+"Class:"+id;  
  }
  
  public Node typeCheck() {
	  
	for (Node parameterNode:listParameters) {
		parameterNode.typeCheck();
	}
	for (Node metNode:listMethod) {
		metNode.typeCheck();
	}
	if (parentNode!=null) {
		for(int i=0;i<parentNode.getListParameters().size();i++) {
			if(!FOOLlib.isSubtype(((ParNode)(listParameters.get(i))).getType(), ((ParNode)(parentNode.getListParameters().get(i))).getType())) {
				System.out.println("Cannot override field "+(i+1)+" in class "+id+": it is not a subtype");
				System.exit(0);
			}
		}
		for (int i=0;i<parentNode.getListMethod().size();i++) {
			MetNode f=(MetNode)(listMethod.get(i));
			MetNode p=(MetNode)(parentNode.getListMethod().get(i));
			if (f.getListParameters().size()!=
					p.getListParameters().size()) {
				System.out.println("Cannot override method "+(i+1)+" in class "+id+": it is not the same number of parameters");
				System.exit(0);
			}
			if(!FOOLlib.isSubtype(f.getType(), p.getType())) {
				System.out.println("Cannot override method "+(i+1)+" in class "+id+": return value is not a subtype");
				System.exit(0);
			}
			for (int j=0;j<p.getListParameters().size();j++) {
				ParNode ff=(ParNode)(f.getListParameters().get(j));
				ParNode pp=(ParNode)(p.getListParameters().get(j));
				if (!FOOLlib.isSubtype(ff,pp)) {
					System.out.println("Cannot override field "+(j+1)+" in method "+(i+1)+" in class "+id+": it is not a subtype");
					System.exit(0);
				}
			}
		}
	}
	
	
    return type;
  }    
  
  @Override
 	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  ArrayList<SemanticError> res=new ArrayList<SemanticError>();
	  STentry entry=env.getClassVarMap().get(id).getEntry0(id);
	  ClassTypeNode classTypeNode;
	  ArrayList<Node> parTypeNodes=new ArrayList<>();
	  ArrayList<Node> metTypeNodes=new ArrayList<>();
	  if (parentNode!=null) {
		  classTypeNode=new ClassTypeNode(id, null, null, parentNode.getType());
	  }
	  else {
		  classTypeNode=new ClassTypeNode(id,null,null);
	  }
	  type=classTypeNode;
	  entry.addType(classTypeNode);
	  for (Node parameterNode:listParameters) {
		  ParNode par=(ParNode)parameterNode;
		  res.addAll(par.checkSemantics(env));
		  parTypeNodes.add(par.getType());
		  STentry entryTmp=env.getVarMapActual().getEntry(par.getId());
		  entryTmp.addType(par.getType());
	  }
	  classTypeNode.setListTypeParameters(parTypeNodes);
	  
	  for (Node metNode:listMethod) {
		  res.addAll(metNode.checkSemantics(env));
		  metTypeNodes.add(((MetNode)metNode).getType());
	  }
	  classTypeNode.setListTypeMet(metTypeNodes);
	  
 	  return res;
 	}
  
  public String codeGeneration() {
	  String strMet="";
	  for (Node met:listMethod) {
		  strMet+=met.codeGeneration();
		  strMet+="lhp\n"+
				  "sw\n"+
				  "push 1\n"+
				  "lhp\n"+
				  "add\n"+
				  "shp\n";
	  }
	  ClassMap.getClass(id).setGen(true);

	  return strMet;
  }
	@Override
	public HashSet<String> getAllDep() {
		// TODO Auto-generated method stub
		if(parentId!=null) {
			allDep.add(parentId);
		}
		for (Node paramater:listParameters) {
			if(paramater instanceof DepNode) {
				allDep.addAll(((DepNode)paramater).getAllDep());
			}
		}
		for (Node metNode:listMethod) {
			if(metNode instanceof DepNode) {
				allDep.addAll(((DepNode)metNode).getAllDep());
			}
		}
		
		return allDep;
	}
	 public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public ArrayList<ParNode> getListParameters() {
			return listParameters;
		}

		public void setListParameters(ArrayList<ParNode> listParameters) {
			this.listParameters = listParameters;
		}
		public void addParameterNode(ParNode parNode) {
			listParameters.add(parNode);
		}

		public ArrayList<MetNode> getListMethod() {
			return listMethod;
		}

		public void setListMethod(ArrayList<MetNode> listMethod) {
			this.listMethod = listMethod;
		}
		public void addMetNode(Node metNode) {
			FunNode f=(FunNode) metNode;
			listMethod.add(new MetNode(f.getId(), this.id,f.getType(),f.getParlist(),f.getDeclist(),f.getBody()));
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public ClassNode getParentNode() {
			return parentNode;
		}

		public void setParentNode(ClassNode parentNode) {
			this.parentNode = parentNode;
		}

		public ClassTypeNode getType() {
			return type;
		}
		public void setType(ClassTypeNode type) {
			this.type = type;
		}
		
		public boolean isGen() {
			return gen;
		}

		public void setGen(boolean gen) {
			this.gen = gen;
		}

		public void setAllDep(HashSet<String> allDep) {
			this.allDep = allDep;
		}
		public MetNode getMet(String id) {
			for (Node metNode:listMethod) {
				if (((MetNode)metNode).getId().equals(id)) {
					return (MetNode)metNode;
				}
			}
			return null;
		}
		public void createMethodLabel() {
			for (MetNode method:listMethod) {
				method.createLabel();
			}
		}
}  