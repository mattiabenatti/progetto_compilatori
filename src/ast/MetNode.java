package ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sun.xml.internal.ws.api.ha.HaInfo;

import lib.FOOLlib;
import util.Environment;
import util.Environment.ClassMap;
import util.SemanticError;

public class MetNode implements DepNode {
	String id;
	String idClass;
	boolean overriden;
	Node type;
	int nNew;
	String label=null;
	ArrayList<Node> listParameters=new ArrayList<>();
	ArrayList<Node> listBody;
	ArrayList<Node> listDeclarations;
	
	
	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		String strPar="";
		String strDec="";
		String strBody="";
		if (listParameters!=null) {
			for (Node parameter:listParameters) {
				strPar+=parameter.toPrint(indent+ " ");
			}
		}
		if (listDeclarations!=null) {
			for (Node declaration:listDeclarations) {
				strDec+=declaration.toPrint(indent+" ");
			}
		}
		if (listBody!=null) {
			for(Node body:listBody) {
				strBody+=body.toPrint(indent+" ");
			}
		}
		return indent+ "Met: "+id+"\n"+type.toPrint(indent+" ")+strPar+strDec+strBody;
	}

	@Override
	public Node typeCheck() {
		// TODO Auto-generated method stub
		
		if (listDeclarations!=null) {
			for (Node declarationNode:listDeclarations) {
				declarationNode.typeCheck();
			}
		}
		if (listBody!=null) {
			for (Node bodyNode:listBody) {
				if(!FOOLlib.isSubtype(bodyNode.typeCheck(), type)) {
					System.out.println("Type of return value in function "+id+" is not the same");
					System.exit(0);
				}
			}
		}
		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		String strDec="";
		String strPopDec="";
		String strPopPar="";
		String strBody="";
		String funl="";
		if (listDeclarations!=null) {
			for (Node declarationNode:listDeclarations) {
				strDec+=declarationNode.codeGeneration();
			}
		}
		if (listDeclarations!=null) {
			for (Node declarationNode:listDeclarations) {
				strPopDec+="pop\n";
			}
			for (int i=0;i<nNew;i++) {
				strPopDec+="pop\n";
			}
		}
		for (Node parameterNode:listParameters) {
			strPopPar+="pop\n";
		}
		funl=ClassMap.getClass(idClass).getMet(id).getLabel();
		
		if (listBody!=null) {
			for(Node bodyNode:listBody) {
				strBody+=bodyNode.codeGeneration();
			}
		}
		
		FOOLlib.putCode(funl+ ":\n"+
						"cfp\n"+
						"lra\n"+
						strDec+
						strBody+"srv\n"+
						strPopDec+"sra\n"+
						"sfp\n"+
						"pop\n"+
						strPopPar+
						"lrv\n"+
						"lra\n"+"js\n");
		
		return "push "+funl+"\n";
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		ArrayList<SemanticError> res=new ArrayList<SemanticError>();
		ArrayList<Node> parameterTypesNode=new ArrayList<>();
		STentry entryS=env.getVarMapActual().getEntry(id);

		env.getVarMapActual().putHashMap();
		env.getVarMapActual().incrementNestingLevel();
		
		//controlla parametri metodo
		int parameterOffset=2;
		for(Node parameterNode:listParameters) {
			ParNode parameter=(ParNode)parameterNode;
			res.addAll(parameter.checkSemantics(env));
			//controlla se esiste la classe del parametro e nel caso la setta
			if(parameter.getType()instanceof ClassTypeNode) {
				ClassTypeNode classTypeNode=(ClassTypeNode)parameter.getType();
				String idClass=classTypeNode.getId();
				if(env.getClassVarMap().get(idClass)==null) {
					res.add(new SemanticError(idClass+" class not exists"));
				}
				else {
					classTypeNode=(ClassTypeNode)env.getVarMapActual().getEntry0(idClass).getType();
					parameter.setType(classTypeNode);
				}
			}
			parameterTypesNode.add(parameter.getType());
			if(env.getVarMapActual().putEntry(parameter.getId(), new STentry(env.getVarMapActual().getNestingLevel(), parameter.getType(),parameterOffset))!=null) {
				res.add(new SemanticError(parameter.getId()+" in method "+id+" already declared"));
			}
			
		}
		//setta la classe di ritorno
		if (type instanceof ClassTypeNode) {
			ClassTypeNode retClassTypeNode=(ClassTypeNode)type;
			if(env.getClassVarMap().get(retClassTypeNode.getId())!=null) {
				retClassTypeNode=(ClassTypeNode)env.getClassVarMap().get(retClassTypeNode.getId()).getEntry0(retClassTypeNode.getId()).getType();
				type=retClassTypeNode;
			}
			else {
				res.add(new SemanticError(retClassTypeNode.getId()+" class is not declared"));
			}
		}
		entryS.addType(new ArrowTypeNode(parameterTypesNode, type, true));
		//controllo con le dichiarazioni locali
		if(listDeclarations!=null) {
			if(listDeclarations.size()>0) {
				env.getVarMapActual().setOffset(-2);
				for (Node declarationNode:listDeclarations) {
					res.addAll(declarationNode.checkSemantics(env));
				}
			}
		}
		//controllo body
		for (Node bodyNode:listBody) {
			res.addAll(bodyNode.checkSemantics(env));
		}
		
		env.getVarMapActual().removeHashMap();
		env.getVarMapActual().decrementNestingLevel();
		
		
		return res;
	}
	public MetNode(String id, Node type) {
		this.id=id;
		this.type=type;
	}
	public MetNode(String id, String idClass, Node type, ArrayList<Node> listParameters, ArrayList<Node> listDeclarations, ArrayList<Node> listBody) {
		this.id=id;
		this.idClass=idClass;
		this.type=type;
		this.listParameters=listParameters;
		this.listDeclarations=listDeclarations;
		this.listBody=listBody;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdClass() {
		return idClass;
	}

	public void setIdClass(String idClass) {
		this.idClass = idClass;
	}

	public boolean isOverriden() {
		return overriden;
	}

	public void setOverriden(boolean overriden) {
		this.overriden = overriden;
	}

	public Node getType() {
		return type;
	}

	public void setType(Node type) {
		this.type = type;
	}

	public int getnNew() {
		return nNew;
	}

	public void setnNew(int nNew) {
		this.nNew = nNew;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void createLabel() {
		label=FOOLlib.freshFunLabel();
	}

	public ArrayList<Node> getListParameters() {
		return listParameters;
	}

	public void setListParameters(ArrayList<Node> listParameters) {
		this.listParameters = listParameters;
	}

	public ArrayList<Node> getListBody() {
		return listBody;
	}

	public void setListBody(ArrayList<Node> listBody) {
		this.listBody = listBody;
	}

	public ArrayList<Node> getListDeclarations() {
		return listDeclarations;
	}

	public void setListDeclarations(ArrayList<Node> listDeclarations) {
		this.listDeclarations = listDeclarations;
	}
	public void setListDeclarationsBody(ArrayList<Node> declarations,ArrayList<Node> body) {
		this.listDeclarations=declarations;
		this.listBody=body;
	}
	
	@Override
	public boolean equals(Object o) {
		MetNode oMet=(MetNode)o;
		if (o==null) {
			return false;
		}
		if (o.getClass()!=getClass()) {
			return false;
		}
		if (o==this) {
			return true;
		}
		if(id==null) {
			if (oMet.getId()!=null) {
				return false;
			}
			if (!(oMet.getId().equals(id))) {
				return false;
			}
		}
		return true;
		
	}

	@Override
	public HashSet<String> getAllDep() {
		HashSet<String> depSet = new HashSet<>();
		for (Node parameter:listParameters) {
			if (parameter instanceof DepNode) {
				depSet.addAll(((DepNode)parameter).getAllDep());
			}
		}
		for (Node declaration:listDeclarations) {
			depSet.addAll(((DepNode)declaration).getAllDep());
		}
		return depSet;
	}
	

}
