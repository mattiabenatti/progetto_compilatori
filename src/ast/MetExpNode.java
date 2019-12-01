package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class MetExpNode implements Node {

	String idMet;
	ArrayList<Node>listParameters;
	int nestingLevel;
	STentry funEntry;
	STentry objEntry;
	boolean call=false;
	String obj;
	
	public MetExpNode(String idMet, ArrayList<Node> listParameters) {
		this.idMet=idMet;
		this.listParameters=listParameters;
		this.obj="this";
	}
	public MetExpNode(String obj,String idMet, ArrayList<Node> listParameters) {
		this.idMet=idMet;
		this.listParameters=listParameters;
		this.obj=obj;
	}
	@Override
	public String toPrint(String indent) {
		String strPar="";
		String strObj="";
		if (listParameters!=null) {
			for (Node parameter : listParameters) {
				strPar+=parameter.toPrint(indent+" ");
			}
		}
		if (objEntry==null) {
			strObj=indent+"entry obj: \n"+indent+"this \n";
		}
		else {
			strObj="entry obj: \n"+objEntry.toPrint(indent+" ")+indent;
		}
		return indent+"Met Call: "+idMet+" by "+obj+" (nesting level: "+nestingLevel+")\n"+indent+strObj+"entry fun:\n "+funEntry.toPrint(indent+" ")+indent+"parameters:\n "+strPar;
	}

	@Override
	public Node typeCheck() {
		// TODO Auto-generated method stub
		ArrowTypeNode met;
		if(!(funEntry.getType() instanceof ArrowTypeNode)) {
			System.out.println(idMet+" is not a method");
			System.exit(0);
		}
		met=(ArrowTypeNode)funEntry.getType();
		if(!met.isMethodImplementation()) {
			System.out.println(idMet+" is not a method");
			System.exit(0);
		}
		if(met.getParList().size()!=listParameters.size()) {
			System.out.println("Method "+idMet+" has not the right number of parameters");
			System.exit(0);
		}
		for (int i=0;i<listParameters.size();i++) {
			if (!FOOLlib.isSubtype(listParameters.get(i).typeCheck(), met.getParList().get(i))) {
				System.out.println("Parameter "+(i+1)+" in method "+idMet+" has not the right type");
				System.exit(0);
			}
		}
		return met.getRet();
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		
		String strPar="";
		String strClass="";
		String strMet="";
		String getAR="";
		int offset;
		int nLevel;
		int offsetM;
		
		for (int i=listParameters.size()-1;i>=0;i--) {
			strPar+=listParameters.get(i).codeGeneration();
		}
		if (objEntry!=null) {
			offset=objEntry.getOffset();
			nLevel=objEntry.getNestinglevel();
		}
		else {
			offset=1;
			nLevel=nestingLevel;
		}
		offsetM=(-1*funEntry.getOffset())-1;
		
		for (int i=0;i<nestingLevel-nLevel;i++) {
			getAR+="lw\n";
		}
		if(call) {
			int offsetC=-1*offset;
			strClass+="push 1\n"+
					"lfp\n"+
					"add\n"+
					"lw\n"+
					"push "+(offsetC-1)+"\n"+
					"add\n"+
					"lw\n";
			strMet+=strClass+"push "+offsetM+"\n"+
					"add\n"+
					"lw\n"+"js\n";
		}
		else {
			strClass+="push "+offset+"\n"+
					"lfp\n"+getAR+
					"add\n"+
					"lw\n";
			strMet+="push "+offset+"\n"+
					"lfp\n"+
					"add\n"+"lw\n"+
					"push "+offsetM+"\n"+
					"add\n"+
					"lw\n"+"js\n";
		}
		
		return strPar+strClass+"lfp\n"+strMet;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		ArrayList<SemanticError> res=new ArrayList<>();
		STentry entryTmp=null;
		STentry entryTmpCurrent=null;
		STentry entryTmpSuper=null;
		String idCurrent=null;
		String idSuper=null;

		if(!obj.equals("this")) {
			System.out.println("no obj this");
			entryTmp=env.getVarMapActual().getEntry(obj);
			//setta se metodo invocato su campo classe o su una variabile normale
			if (env.getActualVarMap()!=null) {
				if (entryTmp.getNestinglevel()==1) {
					call=true;
				}
			}
			
			if (!(entryTmp.getType() instanceof ClassTypeNode)) {
				res.add(new SemanticError(obj+" is not a class"));
			}
			else {
				objEntry=entryTmp;
				if (((ClassTypeNode)objEntry.getType()).getIdType()!=null) {
					idCurrent=((ClassTypeNode)objEntry.getType()).getIdType();
					idSuper=((ClassTypeNode)objEntry.getType()).getId();
				}
				else {
					idCurrent=((ClassTypeNode)objEntry.getType()).getId();
				}
				if (env.getClassVarMap().get(idCurrent)==null) {
					res.add(new SemanticError(idCurrent+" class not declared"));
				}
				if(idSuper!=null) {
					entryTmpSuper=env.getClassVarMap().get(idSuper).getEntryX(idMet, 1);
					if (entryTmpSuper==null) {
						res.add(new SemanticError(idMet+" method not declared in superclass "+idSuper));
					}
				}
				entryTmpCurrent=env.getClassVarMap().get(idCurrent).getEntryX(idMet, 1);
				if (entryTmpCurrent==null) {
					res.add(new SemanticError(idMet+" method not declared"));
				}
				else {
					funEntry=entryTmpCurrent;
					nestingLevel=env.getVarMapActual().getNestingLevel();
					for (Node paramater:listParameters) {
						res.addAll(paramater.checkSemantics(env));
					}
					
				}
			}
		}
		else {
			System.out.println("obj this");
			idCurrent=env.getActualVarMap();
			if (idCurrent==null) {
				res.add(new SemanticError("Word 'this' not in a class"));
			}
			else {
				entryTmpCurrent=env.getClassVarMap().get(idCurrent).getEntryX(idMet, 1);
				if (entryTmpCurrent==null) {
					res.add(new SemanticError(idMet+" method not declared"));
				}
				else {
					funEntry=entryTmpCurrent;
					nestingLevel=env.getVarMapActual().getNestingLevel();
					for (Node paramater:listParameters) {
						res.addAll(paramater.checkSemantics(env));
					}
				}
			}
		}

		
		return res;
	}

}
