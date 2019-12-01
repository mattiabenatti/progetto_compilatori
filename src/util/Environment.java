package util;

import java.util.ArrayList;
import java.util.HashMap;

import ast.ClassNode;
import ast.STentry;

public class Environment {
	public class VarMap{
		public ArrayList<HashMap<String,STentry>> symTable = new ArrayList<HashMap<String,STentry>>();
		private HashMap<String,Integer> disTable=new HashMap<String,Integer>();
		private int disOffset=0;
		private int nestingLevel = -1;
		private int offset = 0;
		public ArrayList<HashMap<String, STentry>> getSymTable() {
			return symTable;
		}
		public void setSymTable(ArrayList<HashMap<String, STentry>> symTable) {
			this.symTable = symTable;
		}
		public HashMap<String, Integer> getDisTable() {
			return disTable;
		}
		public void setDisTable(HashMap<String, Integer> disTable) {
			this.disTable = disTable;
		}
		public int getDisOffset() {
			return disOffset;
		}
		public void setDisOffset(int disOffset) {
			this.disOffset = disOffset;
		}
		public int getNestingLevel() {
			return nestingLevel;
		}
		public void setNestingLevel(int nestingLevel) {
			this.nestingLevel = nestingLevel;
		}
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}
		public STentry getEntry0(String id) {
			return symTable.get(0).get(id);
		}
		public STentry getEntryX(String id, int level) {
			return symTable.get(level).get(id);
		}
		public STentry getEntry(String id) {
			STentry res=null;
			int level=nestingLevel;
			while(level>=0) {
				res=symTable.get(level).get(id);
				level-=1;
				if (res!=null) {
					break;
				}
				
			}
			return res;
		}
		public void incrementNestingLevel() {
			nestingLevel++;
		}
		public void decrementNestingLevel() {
			nestingLevel--;
		}
		public void incrementOffset() {
			offset++;
		}
		public void decrementOffset() {
			offset--;
		}
		public STentry putEntry(String id, STentry entry) {
			return symTable.get(nestingLevel).put(id, entry);
		}
		public void putHashMap() {
			symTable.add(new HashMap<>());
		}
		public void removeHashMap() {
			symTable.remove(nestingLevel);
		}
	}
	
	public static class ClassMap{
		private static HashMap<String,ClassNode> classMap=new HashMap<>();
		public static void putClass(String id, ClassNode classNode) {
			classMap.put(id, classNode);
		}
		
		public static ClassNode getClass(String id) {
			return classMap.get(id);
		}
		
		public static ArrayList<String> getInstances(String id){
			ArrayList<String> listIstances=new ArrayList<>();
			
			for(String idClass:classMap.keySet()) {
				ClassNode classNode=classMap.get(idClass);
				while(classNode.getParentId()!=null) {
					ClassNode parent=classNode.getParentNode();
					if(id.equals(parent.getId())) {
						listIstances.add(classNode.getId());
					}
					classNode=parent;
				}
			}
			
			return listIstances;
		}
	}
	
	private VarMap envVarMap=new VarMap();
	private HashMap<String,VarMap>classVarMap=new HashMap<>();
	String actualVarMap=null;
	public VarMap getEnvVarMap() {
		return envVarMap;
	}
	public void setEnvVarMap(VarMap envVarMap) {
		this.envVarMap = envVarMap;
	}
	public HashMap<String, VarMap> getClassVarMap() {
		return classVarMap;
	}
	public void setClassVarMap(HashMap<String, VarMap> classVarMap) {
		this.classVarMap = classVarMap;
	}
	public String getActualVarMap() {
		return actualVarMap;
	}
	public void setActualVarMap(String actualVarMap) {
		this.actualVarMap = actualVarMap;
	}
	
	public void addClassVarMap(String idClass) {
		classVarMap.put(idClass, new VarMap());
	}
	
	public VarMap getVarMapActual() {
		if (actualVarMap==null) {
			return envVarMap;
		}
		else {
			return classVarMap.get(actualVarMap);
		}
	}
	
	
	
	
}
