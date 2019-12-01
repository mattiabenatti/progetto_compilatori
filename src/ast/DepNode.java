package ast;

import java.util.HashSet;
import java.util.Set;

public interface DepNode extends Node {
	public HashSet<String> getAllDep();
}
