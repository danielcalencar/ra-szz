package br.ufrn.raszz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.ufrn.raszz.miner.refdiff.RefDiffService;
import br.ufrn.raszz.model.RefElement;
import br.ufrn.razszz.connectoradapter.SzzRepository;

public abstract class RefacOperationsUtil {
	
	private static List<RefElement> getRefac_RENAME_METHOD (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("RENAME_METHOD")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number) 
			    		 || (!r.getElementtype().equals("CALLER") && (number >= r.getAfterstartline() && number < r.getAfterstartscope()))
			    		    )
			    		)
			     .collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_RENAME_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("RENAME_CLASS") 
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && (number == r.getAfterstartscope()  
			    		 || (number == r.getAfterstartscope()+1 && (c.contains("class") || c.contains("interface") || c.contains("enum")))))
			     .collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_EXTRACT_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_OPERATION")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getAfterstartline() && number <= r.getAfterendline()) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_EXTRACT_INTERFACE (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_INTERFACE")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (c.contains("class") && c.contains(r.getAftersimplename()))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_EXTRACT_SUPERCLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("EXTRACT_SUPERCLASS")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (c.contains("class") && c.contains(r.getAftersimplename()))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_INLINE_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().equals("INLINE_OPERATION")
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getAfterstartline() && number <= r.getAfterendline()) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_MOVE_ATTRIBUTE_OPERATION (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		return refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().equals("MOVE_ATTRIBUTE") || r.getRefactoringtype().equals("MOVE_OPERATION"))
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getAfterstartline() && number <= r.getAfterendline()) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_MOVE_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		return refacSet.stream()
				     .filter(r -> r.getRefactoringtype().equals("MOVE_CLASS") 
				    		 && r.getRevision().equals(rev)
				    		 && r.getAfterpathfile().equals(path)
				    		 && ((c.contains("class") && c.contains(r.getAftersimplename()))				    			 
				    				|| (c.contains("interface") && c.contains(r.getAftersimplename())) 
				    				|| (c.contains("enum") && c.contains(r.getAftersimplename())))				    						
				    		).collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_PULL_UP_DOWN (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		return refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("PULL_") //PULL_UP_ATTRIBUTE, PULL_UP_OPERATION, PULL_DOWN_ATTRIBUTE, PULL_DOWN_OPERATION
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && (number+adjindex) >= r.getAfterstartline() && number <= r.getAfterendline()) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
	}
	
	private static List<RefElement> getRefac_MOVE_RENAME_CLASS (List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		final String c = content;
		List<RefElement> result = refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("MOVE_RENAME_CLASS") 
			    		 && r.getRevision().equals(rev)
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && r.getAfternestingLevel() == 0 && number >= 1 && (number <= r.getAfterstartscope() || (number == (r.getAfterstartscope()+1) && (c.contains("class") || c.contains("interface") || c.contains("enum"))))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList());
		result.addAll(refacSet.stream()
			     .filter(r -> r.getRefactoringtype().contains("MOVE_RENAME_CLASS") 
			    		 && r.getRevision() == rev
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && r.getAfternestingLevel() >= 0 && number >= r.getAfterstartline() && (number <= r.getAfterstartscope() || (number == (r.getAfterstartscope()+1) && (c.contains("class") || c.contains("interface") || c.contains("enum"))))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList()));
		result.addAll(refacSet.stream()
			     .filter(r -> (r.getRefactoringtype().contains("MOVE_RENAME_CLASS") || r.getRefactoringtype().contains("RENAME_CLASS")) 
			    		 && r.getRevision() == rev
			    		 && r.getAfterpathfile().equals(path)
			    		 && ((!r.getElementtype().equals("CALLER") && ((c.contains("class") && c.contains(r.getAftersimplename()))     			 
				    				|| (c.contains("interface") && c.contains(r.getAftersimplename())) 
				    				|| (c.contains("enum") && c.contains(r.getAftersimplename())))) 
			    		 || (r.getElementtype().equals("CALLER") && r.getAfterstartscope() == number))
			    		)
			     .collect(Collectors.toList()));
		return result.stream().distinct().collect(Collectors.toList());
	}
	
	public static boolean isRefac(List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {	
		List<RefElement> result = filterRefacSet(refacSet, path, rev, number, adjindex, content);
		return (result.size() == 0)? false: true;
	}
	
	public static List<RefElement> filterRefacSet(List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {
		List<RefElement> result = new ArrayList<RefElement>();
		result.addAll(getRefac_RENAME_METHOD(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_RENAME_CLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_EXTRACT_OPERATION(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_EXTRACT_INTERFACE(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_EXTRACT_SUPERCLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_EXTRACT_SUPERCLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_INLINE_OPERATION(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_MOVE_ATTRIBUTE_OPERATION(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_MOVE_CLASS(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_PULL_UP_DOWN(refacSet, path, rev, number, adjindex, content));
		result.addAll(getRefac_MOVE_RENAME_CLASS(refacSet, path, rev, number, adjindex, content));
		return result.stream().distinct().collect(Collectors.toList());
	}
	
	public static String prevRefacContent(List<RefElement> refacSet, String content) {
		String beforeContent = content;
		String[] aux;
		String bef, aft;
		for(RefElement refac: refacSet) {
			switch (refac.getRefactoringtype()) {
			case "RENAME_METHOD":
				aux = refac.getEntitybefore().split("\\(")[0].split(" ");
				bef = aux[aux.length-1];
				aux = refac.getEntityafter().split("\\(")[0].split(" ");
				aft = aux[aux.length-1];	
				beforeContent = beforeContent.replaceFirst(aft, bef);
				break;
			case "RENAME_CLASS":
				bef = refac.getEntitybefore().split(" ")[1];
				aft = refac.getEntityafter().split(" ")[1];	
				beforeContent = beforeContent.replaceFirst(aft, bef);
				break;
			case "MOVE_RENAME_CLASS":
				aux = refac.getEntitybefore().split("\\(")[0].split(" ");
				bef = aux[aux.length-1];
				aux = refac.getEntityafter().split("\\(")[0].split(" ");
				aft = aux[aux.length-1];	
				beforeContent = beforeContent.replaceFirst(aft, bef);
				break;	
			}
		}
		return beforeContent;
	}
	
	public static long[] prevRefacLines(List<RefElement> refacSet) {
		long[] interval = new long[2];
		for(RefElement refac: refacSet) {
			if (refac.getRefactoringtype().equals("EXTRACT_OPERATION") ||
				refac.getRefactoringtype().equals("INLINE_OPERATION")
			){ //primeira linha do escopo do método até a última linha do escopo
				interval[0] = refac.getBeforestartline(); //refac.getBeforestarscope();
				interval[1] = refac.getBeforeendline();
			}
			if (refac.getRefactoringtype().equals("EXTRACT_INTERFACE") ||
				refac.getRefactoringtype().equals("EXTRACT_SUPERCLASS") ||
				refac.getRefactoringtype().equals("MOVE_CLASS") ||
				refac.getRefactoringtype().equals("MOVE_OPERATION") ||
				refac.getRefactoringtype().equals("PULL_UP_ATTRIBUTE") ||
				refac.getRefactoringtype().equals("PULL_UP_OPERATION") ||
				refac.getRefactoringtype().equals("PUSH_DOWN_ATTRIBUTE") ||
				refac.getRefactoringtype().equals("PUSH_DOWN_OPERATION")
			){ //primeira linha do javadoc até o final ou início do escopo
				interval[0] = refac.getBeforestartline();
				interval[1] = (refac.getBeforeendline() >0)? 
							refac.getBeforeendline(): refac.getBeforestarscope();
			} 
			if (refac.getRefactoringtype().equals("MOVE_ATTRIBUTE") ||
				refac.getRefactoringtype().equals("RENAME_METHOD") ||
				refac.getRefactoringtype().equals("RENAME_CLASS") ||
				refac.getRefactoringtype().equals("MOVE_RENAME_CLASS")
			){ //a linha exata onde inicial o atributo, método ou classe movida/renomeada
				interval[0] = refac.getBeforestartline(); //refac.getBeforestarscope();
				interval[1] = (refac.getBeforestarscope() >0)? 
						refac.getBeforestarscope(): refac.getBeforeendline();
			} 
		}
		return interval;
	}
	
	public static String prevRefacPath(List<RefElement> refacSet, String path, String rev, int number, int adjindex, String content) {		
		List<RefElement> result = refacSet.stream()
			     .filter(r -> !r.getElementtype().equals("CALLER"))
			     .collect(Collectors.toList());/**/
				
		/*List<RefElement>*/ result = filterRefacSet(refacSet, path, rev, number, adjindex, content);
				//getRefac_RENAME_CLASS(result, path, rev, number, adjindex, content);
		return (result.size() != 0)? result.get(0).getBeforepathfile() : null;
	}
	
	/*
	public String prevRefacContent(String path, long prevrev, int prevnumber, int adjindex, String content) {
		List<Object[]> refac = szzDAO.getRefacBic(path, prevrev, prevnumber, adjindex, content);
		//revision, refactoringtype, entitybefore, entityafter, [4]elementtype, [5]afterstartline, [6]afterendline, [7]afterstartscope, aftersimplename
		if (refac.size() > 0) {
			Object[] ref = refac.get(0);
			if (ref[1].toString().equals("RENAME_METHOD")) {
				String[] aux = ref[2].toString().split("\\(")[0].split(" ");
				String bef = aux[aux.length-1];					
				aux = ref[3].toString().split("\\(")[0].split(" ");
				String aft = aux[aux.length-1];					
				return content.replaceFirst(aft, bef);
			}
		}
		return null;
	}*/
	
	public static List<RefElement> checkUpdateRefactoringDatabase(SzzRepository repository, String commitId, String project){
		String revisionType="run";				
		RefDiffService refDiffService = new RefDiffService(repository);
		return refDiffService.executeRefDiff(project, commitId, revisionType);
	}

}
