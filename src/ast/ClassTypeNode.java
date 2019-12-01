package ast;

import java.util.ArrayList;
import java.util.HashSet;

import util.Environment;
import util.SemanticError;

public class ClassTypeNode implements DepNode {

	
	String id;
	String idType;
	ClassTypeNode parentTypeNode;
	ArrayList<Node>listTypeParameters;
	ArrayList<Node>listTypeMet;
	
	public ClassTypeNode(String id) {
		this.id = id;
		this.idType=null;
		this.parentTypeNode=null;
		this.listTypeParameters = null;
		this.listTypeMet = null;
	}
	
	public ClassTypeNode(String id, ArrayList<Node> listTypeParameters, ArrayList<Node> listTypeMet) {
		this.id = id;
		this.idType=null;
		this.parentTypeNode=null;
		this.listTypeParameters = listTypeParameters;
		this.listTypeMet = listTypeMet;
	}
	public ClassTypeNode(String id, ArrayList<Node> listTypeParameters, ArrayList<Node> listTypeMet,ClassTypeNode parentTypeNode) {
		this.id = id;
		this.idType=null;
		this.parentTypeNode=parentTypeNode;
		this.listTypeParameters = listTypeParameters;
		this.listTypeMet = listTypeMet;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public ClassTypeNode getParentTypeNode() {
		return parentTypeNode;
	}

	public void setParentTypeNode(ClassTypeNode parentTypeNode) {
		this.parentTypeNode = parentTypeNode;
	}

	public ArrayList<Node> getListTypeParameters() {
		return listTypeParameters;
	}

	public void setListTypeParameters(ArrayList<Node> listTypeParameters) {
		this.listTypeParameters = listTypeParameters;
	}

	public ArrayList<Node> getListTypeMet() {
		return listTypeMet;
	}

	public void setListTypeMet(ArrayList<Node> listTypeMet) {
		this.listTypeMet = listTypeMet;
	}
	
	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		return indent+this.id+"\n";
	}


	@Override
	public Node typeCheck() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		return new ArrayList<SemanticError>();
	}

	@Override
	public HashSet<String> getAllDep() {
		// TODO Auto-generated method stub
		HashSet<String> allDep = new HashSet<String>();
		allDep.add(this.id);
		if (this.idType != null) {
			allDep.add(this.idType);
		}
		return allDep;
	}

}
