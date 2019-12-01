package ast;

import java.util.ArrayList;
import java.util.HashSet;

import lib.FOOLlib;
import util.Environment;
import util.Environment.ClassMap;
import util.SemanticError;

public class ProgExpClassNode implements DepNode {
	
	Node expNode;
	ArrayList<Node> listDeclarations=new ArrayList<>();
	ArrayList<ClassNode> listClass=new ArrayList<>();
	
	public ProgExpClassNode(Node expNode) {
		// TODO Auto-generated constructor stub
		this.expNode=expNode;
	}
	public ProgExpClassNode(ArrayList<Node> listDeclarations, Node expNode) {
		// TODO Auto-generated constructor stub
		this.expNode=expNode;
		this.listDeclarations=listDeclarations;
	}
	
	@Override
	public String toPrint(String indent) {
		String strDec="";
		String strClass="";
		if (listDeclarations!=null) {
			for (Node declaration:listDeclarations) {
				strDec+=declaration.toPrint(indent+" ");
			}
		}
		if (listClass!=null) {
			for (Node cl:listClass) {
				strClass+=cl.toPrint(indent+" ");
			}
		}
		return "ProgClass: \n"+strClass+strDec+expNode.toPrint(indent+" ");
		
	}

	@Override
	public Node typeCheck() {
		// TODO Auto-generated method stub
		
		for (Node classNode:listClass) {
			classNode.typeCheck();
		}
		if (listDeclarations!=null) {
			for(Node declarationNode:listDeclarations) {
				declarationNode.typeCheck();
			}
		}
		return expNode.typeCheck();
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		String strDec="";
		for (Node classNode:listClass) {
			((ClassNode)classNode).createMethodLabel();
		}
		for (Node classNode:listClass) {
			classNode.codeGeneration();
		}
		if (listDeclarations!=null) {
			for (Node decNode:listDeclarations) {
				strDec+=decNode.codeGeneration();
			}
		}
		return "push 0\n"
				+strDec+expNode.codeGeneration()+"halt\n"+FOOLlib.getCode();
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		ArrayList<SemanticError> res=new ArrayList<SemanticError>();
		ArrayList<ClassNode>listClassNew=new ArrayList<>();
		ArrayList<String>listClassName=new ArrayList<>();
		ArrayList<Boolean>listCheck=new ArrayList<>();
		getAllDep();
	//controllo dipendenze classi
		if (listClass.size()>0) {
			for (Node classNode:listClass) {
				listClassName.add(((ClassNode)classNode).getId());
				listCheck.add(false);
			}

			//controllo dipendenze incrociate
			boolean checkClassAdded=true;
			while(listCheck.contains(false) && res.size()==0 && checkClassAdded) {
				checkClassAdded=false;
				for(int i=0; i<listClass.size();i++) {
					if(!listCheck.get(i)) {
						ClassNode cn=(ClassNode)listClass.get(i);
						boolean redo=true;
						for (String dependence:cn.getAllDep()) {
							if(listClassName.indexOf(dependence)==-1) {
								res.add(new SemanticError(dependence+" class not declared"));
							}
							else if(listClassName.indexOf(dependence)==i) {
								if(cn.getParentId()==listClassName.get(i)) {
									res.add(new SemanticError(cn.getId()+" class and it dependence "+dependence+" are the same"));
								}
							}
							else {
								if(!listCheck.get(listClassName.indexOf(dependence))) {
									redo=false;
								}
							}
						}
						if (cn.getAllDep().size()==0||redo) {
							checkClassAdded=true;
							listCheck.set(i, true);
							listClassNew.add(cn);
						}
					}
				}
			}
			if (!checkClassAdded) {
				res.add(new SemanticError("Detected cross dependencies"));
			}
			if (res.size()==0) {
				listClass=listClassNew;
				for (Node cn1:listClass) {
					ClassNode cn1Casted=(ClassNode)cn1;
					if (cn1Casted.getParentId()!=null) {
						for (Node cn2:listClass) {
							if(((ClassNode)cn2).getId().equals(cn1Casted.getParentId())) {
								System.out.println("FAASGASG");
								cn1Casted.setParentNode((ClassNode)cn2);
							}
						}
					}
					env.setActualVarMap(cn1Casted.getId());
					if (env.getClassVarMap().get(cn1Casted.getId())!=null) {
						res.add(new SemanticError(cn1Casted.getId()+" class already declared"));
					}
					else {
						env.addClassVarMap(cn1Casted.getId());
						
						env.getVarMapActual().putHashMap();
						env.getVarMapActual().incrementNestingLevel();
						
						STentry entry=new STentry(env.getVarMapActual().getNestingLevel(), new ClassTypeNode(cn1Casted.getId()),env.getVarMapActual().getOffset());
						env.getVarMapActual().decrementOffset();
						env.getVarMapActual().putEntry(cn1Casted.getId(), entry);
						
					}
				}
				//Controllo ereditarietà
				for (Node classNode:listClass) {
					ClassNode classNodeCast=(ClassNode)classNode;
					env.setActualVarMap(classNodeCast.getId());
					if (env.getClassVarMap().get(classNodeCast.getId())==null) {
						env.addClassVarMap(classNodeCast.getId());
					}
					env.getVarMapActual().putHashMap();
					env.getVarMapActual().incrementNestingLevel();
					
					if (classNodeCast.getParentId()!=null) {
						if(classNodeCast.getParentId().equals(classNodeCast.getId())) {
							res.add(new SemanticError(classNodeCast.getId()+" ineriths from himself"));
							
						}
						else if(classNodeCast.getParentNode()==null) {
							res.add(new SemanticError(classNodeCast.getParentId()+" class of parent not declared"));
						}
						else {
							ArrayList<ParNode> listTmpPar=new ArrayList<>();
							for(ParNode parTmpNode:classNodeCast.getParentNode().getListParameters()) {
								listTmpPar.add(new ParNode(parTmpNode.getId(), parTmpNode.getType()));
							}
							classNodeCast.setListParameters(listTmpPar);
							ArrayList<MetNode> listTmpMet=new ArrayList<>();
							for(MetNode metTmpNode:classNodeCast.getParentNode().getListMethod()) {
								listTmpMet.add(new MetNode(metTmpNode.getId(),classNodeCast.getId(), metTmpNode.getType(),metTmpNode.getListParameters(),metTmpNode.getListDeclarations(),metTmpNode.getListBody()));
							}
							//cerca metodi da ereditare
							for (MetNode mn:classNodeCast.getListMethod()) { //da correggere
								boolean override=false;
								for(int i=0;i<listTmpMet.size();i++) {
									if(listTmpMet.get(i).getId().equals(mn.getId())) {
										((MetNode)mn).setOverriden(true);
										listTmpMet.set(i, mn);
										override=true;
									}
								}
								if(!override) {
									listTmpMet.add(mn);
								}

							}
							classNodeCast.setListMethod(listTmpMet);
						}
					}
					for (Node np:classNodeCast.getListParameters()) {
						STentry tmpEntry=new STentry(env.getVarMapActual().getNestingLevel(), env.getVarMapActual().getOffset());
						tmpEntry.setInClass(true);
						env.getVarMapActual().decrementOffset();
						if(env.getVarMapActual().putEntry(((ParNode)np).getId(), tmpEntry)!=null) {
							res.add(new SemanticError(((ParNode)np).getId()+" parameter already declared"));
						}
					}
					if(classNodeCast.getListMethod().size()>0) {
						for (Node mn:classNodeCast.getListMethod()) {
							STentry tmpEntry=new STentry(env.getVarMapActual().getNestingLevel(), env.getVarMapActual().getOffset());
							env.getVarMapActual().decrementOffset();
							if(env.getVarMapActual().putEntry(((MetNode)mn).getId(), tmpEntry)!=null) {
								res.add(new SemanticError(((MetNode)mn).getId()+" method already declared"));
							}
						}
					}
					ClassMap.putClass(classNodeCast.getId(), classNodeCast);
				}
					
			}
			
		}
		if(res.size()>0) {
			return res;
		}
		for (Node classNode:listClass) {
			ClassNode classNodeCasted=(ClassNode)classNode;
			env.setActualVarMap(classNodeCasted.getId());
			if (env.getClassVarMap().get(classNodeCasted.getId())==null) {
				env.addClassVarMap(classNodeCasted.getId());
			}
			res.addAll(((ClassNode)classNode).checkSemantics(env));
		}

		//torno ad utilizzare l'ambiente principale
		
		env.setActualVarMap(null);
		env.getVarMapActual().putHashMap();
		env.getVarMapActual().incrementNestingLevel();
		if (listDeclarations.size()>0) {
			env.getVarMapActual().setOffset(-2);
			for (Node declarationNode:listDeclarations) {
				res.addAll(declarationNode.checkSemantics(env));
			}
		}
		res.addAll(expNode.checkSemantics(env));
		env.getVarMapActual().removeHashMap();
		env.getVarMapActual().decrementNestingLevel();

		return res;
		
	}

	@Override
	public HashSet<String> getAllDep() {
		// TODO Auto-generated method stub
		for (ClassNode classNode:listClass) {
			classNode.getAllDep();
		}
		return null;
	}
	public Node getExpNode() {
		return expNode;
	}
	public void setExpNode(Node expNode) {
		this.expNode = expNode;
	}
	public ArrayList<Node> getListDeclarations() {
		return listDeclarations;
	}
	public void setListDeclarations(ArrayList<Node> listDeclarations) {
		this.listDeclarations = listDeclarations;
	}
	public ArrayList<ClassNode> getListClass() {
		return listClass;
	}
	public void setListClass(ArrayList<ClassNode> listClass) {
		this.listClass = listClass;
	}
	public void addClassNode(Node classNode) {
		listClass.add((ClassNode)classNode);
	}

}

