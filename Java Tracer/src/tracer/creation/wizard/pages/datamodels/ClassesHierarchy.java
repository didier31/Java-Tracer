package tracer.creation.wizard.pages.datamodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import com.sun.jdi.ReferenceType;

import tracer.creation.wizard.pages.datamodels.containers.StaticTree;

public class ClassesHierarchy extends StaticTree {

	public ClassesHierarchy(Collection<ReferenceType> list)
	{
		@SuppressWarnings("serial")
		class ArrayTree extends Vector<Object>
		{
		    public ArrayTree(int initialCapacity) {
				super(initialCapacity);
				super.add(new Integer(0));
				cellsCountIdx.push(new Integer(0));
			}

			Stack<Integer> cellsCountIdx = new Stack<Integer>();
		    
	    	public synchronized void incCellsCount()
	    	{
	    		for(Integer idx : cellsCountIdx)
	    			{
	    			set(idx, size() - idx);
	    			};
	    	}

			public synchronized boolean addSibling(Object e) {
				boolean success = super.add(e);
				if (success)
				{
					incCellsCount();
				}
				return success;
			}

			public synchronized boolean addChild(Object e) {
				boolean success = super.add(new Integer(0));
				if (success)
				{
					super.add(e);
					if (success)
					{
						cellsCountIdx.push(size()-2); // push idx of super.add(new Integer(0));
						incCellsCount();
					}
				}
			return success;
			}
			
			public boolean addSubsequent(ReferenceType referenceType, String[] nameSpace, int fromIdx)
			{
			// Add the first name at the same level
			boolean success = addSibling(nameSpace[fromIdx]);
			// and create floor in the tree for each name following.
			for (int i = fromIdx + 1; i < nameSpace.length; i++)
			{
				success |= addChild(nameSpace[i]);
			}
			// Add the classifier as leaf
			success |= addChild(referenceType);
			return success;
		}
			
			public synchronized void closeSibling()
			{
				cellsCountIdx.pop();
			}
		}
		
		ArrayTree workingTree = new ArrayTree(list.size() << 1);		
		
		List<ReferenceType> sortedlist = new ArrayList<ReferenceType>(list);
		Collections.sort(sortedlist);
		Iterator<ReferenceType> classIt = sortedlist.iterator();
		
		String[] actualNamespace = new String[0];
		
		while (classIt.hasNext())
		{
			ReferenceType referenceType = classIt.next();
			
			String typeQualifiedNameStr = referenceType.name();		
			
			String[] qualifiedName = typeQualifiedNameStr.split("\\.");
			String[] nameSpace = Arrays.copyOf(qualifiedName, qualifiedName.length-1);
			
			int idiff = commonNamespace(actualNamespace, nameSpace);
			
			if (idiff == actualNamespace.length)
			{
				// this means namespace is a sub-namespace or sibling
				
				if (nameSpace.length == actualNamespace.length)
				{
				// This means, it's sibling (not a clone because sortedList is populated with unique items)
					workingTree.addSibling(referenceType);
				}
				else
				{   // This means it's sub-namespace
					workingTree.addSubsequent(referenceType, nameSpace, idiff);									
					actualNamespace = nameSpace;
				}
			}
			else
			{   // This means idiff < actualNamespace.length <=> must go up to the floor in common
				for (int i = 0; i < actualNamespace.length - idiff; i++)
				{
					workingTree.closeSibling();
				}
				workingTree.addSubsequent(referenceType, nameSpace, idiff);				
				actualNamespace = nameSpace;
			}
		}
	setTree(workingTree.toArray());
	}
	
	// Return the index of first difference
	// Invariant : returned value <= min(s1.length, s2.length)
	private int commonNamespace(String[] s1, String[] s2)
	{
		int i = 0;
		while (i < s1.length && i < s2.length & s1[i].compareTo(s2[i]) == 0)
		{
			i++;
		}
		return i;
	}	
}
