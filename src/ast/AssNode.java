package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import parser.FOOLParser.AsmContext;
import util.Environment;
import util.SemanticError;

public class AssNode implements Node {

	String id;
	Node exp;
	int nestinglevel;
	STentry stentry;
	AsmContext ctx;
	
	public AssNode(String id, Node exp, AsmContext ctx) {
		this.id=id;
		this.exp=exp;
		this.ctx=ctx;
	}
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Node getExp() {
		return exp;
	}


	public void setExp(Node exp) {
		this.exp = exp;
	}


	public int getNestinglevel() {
		return nestinglevel;
	}


	public void setNestinglevel(int nestinglevel) {
		this.nestinglevel = nestinglevel;
	}


	public STentry getStentry() {
		return stentry;
	}


	public void setStentry(STentry stentry) {
		this.stentry = stentry;
	}


	public AsmContext getCtx() {
		return ctx;
	}


	public void setCtx(AsmContext ctx) {
		this.ctx = ctx;
	}


	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		return indent+"Assignement: "+this.id+"\n"+this.exp.toPrint(indent+" ");
	}

	@Override
	public Node typeCheck() {
		// TODO Auto-generated method stub
		
		if (!FOOLlib.isSubtype(exp.typeCheck(),stentry.getType())) {
			System.out.println("Incompatible value for var: " + id + " \n");
			System.exit(0);
		}
		return new VoidTypeNode();
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		String getAR="";
		for (int i=0;i<nestinglevel-stentry.getNestinglevel();i++) {
			getAR+="lw\n";
		}
		return exp.codeGeneration()+
				"push "+stentry.getOffset()+"\n"+
				"lfp\n"+
				getAR+"add\n"+
				"sw\n";
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		
		ArrayList<SemanticError> res=new ArrayList<SemanticError>();
		
		res.addAll(exp.checkSemantics(env));
		STentry entryTmp=env.getVarMapActual().getEntry(id);
		if(entryTmp==null) {
			res.add(new SemanticError(id+" used for assignment not exist\n"));
		}
		this.stentry=entryTmp;
		nestinglevel=env.getVarMapActual().getNestingLevel();
		
		return res;
	}
	

}
