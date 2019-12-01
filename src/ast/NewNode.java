package ast;

import java.awt.List;
import java.util.ArrayList;

import com.sun.media.jfxmedia.events.NewFrameEvent;

import lib.FOOLlib;
import util.Environment;
import util.Environment.ClassMap;
import util.SemanticError;

public class NewNode implements Node {

	

	String id;
	ArrayList<Node> listParameters=new ArrayList<>();
	int nestingLevel;
	STentry entry;
	
	public NewNode(String id, ArrayList<Node> listParameters) {
		this.id=id;
		this.listParameters=listParameters;
	}
	
	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		
		String strPar="";
		if (listParameters!=null) {
			for (Node parameter:listParameters) {
				strPar+=parameter.toPrint(indent+" ");
			}
		}
		return indent+"New: "+this.id+"(nesting level: "+nestingLevel+")\n"+entry.toPrint(indent+" ")+strPar;
	}

	@Override
	public Node typeCheck() {
		// TODO Auto-generated method stub
		
		ArrayList<Node> listTypePar=((ClassTypeNode)entry.getType()).getListTypeParameters();
		
		if(!(entry.getType() instanceof ClassTypeNode)) {
			System.out.println(id+" is not a class");
			System.exit(0);
		}
		if(listParameters.size()!=listTypePar.size()) {
			System.out.println(id+" has not the right number of parameters");
			System.exit(0);
		}
		for (int i=0;i<listParameters.size();i++) {
			if (!FOOLlib.isSubtype(listParameters.get(i).typeCheck(), listTypePar.get(i))) {
				System.out.println("Parameter "+(i+1)+" in class "+id+" has not the right type");
				System.exit(0);
			}
		}
		
		return entry.getType();
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		String strClass="";
		String strPar="";
		ArrayList<String> strPars=new ArrayList<>();
		ArrayList<Integer> offsets=new ArrayList<>();
		String strExtends="";
		String strExtendsPar="";
		int totalSize=0;
		int offset;
		ClassNode classNode=(ClassNode)ClassMap.getClass(((ClassTypeNode)entry.getType()).getId());
		for (Node node:classNode.getListMethod()) {
			MetNode metNode=(MetNode)node;
			strClass+="push "+metNode.getLabel()+"\n"+
					"lhp\n"+
					"sw\n"+
					"push 1\n"+
					"lhp\n"+
					"add\n"+
					"shp\n";
		}
		
		for(int i=0;i<listParameters.size();i++) {
			Node parameterNode=listParameters.get(i);
			if (parameterNode instanceof NewNode) {
				NewNode newNode=(NewNode) parameterNode;
				ClassNode cn=ClassMap.getClass(newNode.getId());
				offset=cn.getListParameters().size()+cn.getListMethod().size();
				offsets.add(totalSize);
				totalSize+=offset;
				String strExtClass="";
				String strExtPar="";
				ClassNode cn1=(ClassNode)ClassMap.getClass(((ClassTypeNode)newNode.getEntry().getType()).getId());
				for (Node mn:cn1.getListMethod()) {
					strExtClass+="push "+((MetNode)mn).getLabel()+"\n"+
							"lhp\n"+
							"sw\n"+
							"push 1\n"+
							"lhp\n"+
							"add\n"+
							"shp\n";
							;
				}
				for (int j=0;j<newNode.getListParameters().size();j++) {
					strExtPar+=newNode.getListParameters().get(j).codeGeneration()+
							"lhp\n"+
							"sw\n"+
							"push 1\n"+
							"lhp\n"+
							"add\n"+
							"shp\n";
							;
				}
				strExtendsPar+=strExtPar+strExtClass;
				strPars.add("");
			}
			else {
				strPars.add(parameterNode.codeGeneration());
				offsets.add(-1);
			}
		}
		for (int i=0;i<strPars.size();i++) {
			strPar+=strPars.get(i);
			if(offsets.get(i)!=-1) {
				strPar+="lhp\n"+
						"push "+(-1*(i+totalSize-offsets.get(i)))+"\n"+
						"add\n";
				strExtends+="lhp\n"+
							"push "+offsets.get(i)+"\n"+
							"add\n";
			}
			strPar+="lhp\n"+
					"sw\n"+
					"push 1\n"+
					"lhp\n"+
					"add\n"+
					"shp\n";
			
		}
				
				
		return strExtends+"lhp\n"+
						"push "+(totalSize)+"\n"+
						"add\n"+
						strExtendsPar+strPar+strClass;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		ArrayList<SemanticError> res=new ArrayList<>();

		if(env.getClassVarMap().get(id)==null) {
			res.add(new SemanticError(id+" class not declared"));
		}
		else {
			STentry tmpEntry=env.getClassVarMap().get(id).getEntry0(id);
			if (!(tmpEntry.getType()instanceof ClassTypeNode)) {
				res.add(new SemanticError(id+" is not a class"));
			}
			else {
				entry=tmpEntry;
				nestingLevel=env.getVarMapActual().getNestingLevel();
				for (Node parNode:listParameters) {
					if (parNode instanceof NewNode) {
						for (Node newNode:((NewNode)parNode).getListParameters()) {
							if (newNode instanceof NewNode) {
								res.add(new SemanticError("2 new used"));
							}
						}
					}
					res.addAll(parNode.checkSemantics(env));
				}
			}
		}
		
		return res;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<Node> getListParameters() {
		return listParameters;
	}

	public void setListParameters(ArrayList<Node> listParameters) {
		this.listParameters = listParameters;
	}

	public int getNestingLevel() {
		return nestingLevel;
	}

	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}

	public STentry getEntry() {
		return entry;
	}

	public void setEntry(STentry entry) {
		this.entry = entry;
	}

}
