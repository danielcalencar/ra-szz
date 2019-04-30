package br.ufrn.raszz.miner.refdiff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.raszz.model.RefCaller;
import br.ufrn.raszz.model.RefElement;
import br.ufrn.raszz.util.FileUtil;
import refdiff.core.rm2.model.Multiset;
import refdiff.core.rm2.model.SDAttribute;
import refdiff.core.rm2.model.SDEntity;
import refdiff.core.rm2.model.SDMethod;
import refdiff.core.rm2.model.SDType;
import refdiff.core.rm2.model.refactoring.SDRefactoring;

public class RefactoringDataBuilder {
	
	public RefElement prepareElement(SDRefactoring refactoring, String project, String commitId) {

		RefElement refEl = new RefElement(commitId, project);
		refEl.setRefactoringtype(refactoring.getRefactoringType().toString());
		refEl.setSummary(refactoring.toString());
		refEl.setElementtype(refactoring.getEntityAfter().getElementType());
		refEl.setAftersimpleName(refactoring.getEntityAfter().simpleName());
		refEl.setBeforesimpleName(refactoring.getEntityBefore().simpleName());
		refEl.setAftercontent(refactoring.getEntityAfter().getContent());
		refEl.setBeforecontent(refactoring.getEntityAfter().getContent());

		// AFTER entity
		SDType afterContainer = null;		
		if (refactoring.getEntityAfter() instanceof SDType)
			afterContainer = (SDType) refactoring.getEntityAfter();
		else 
			afterContainer = (SDType) refactoring.getEntityAfter().container();		
		refEl.setAfternestingLevel(afterContainer.nestingLevel());
		while (afterContainer.nestingLevel() > 0) {
			afterContainer = (SDType) afterContainer.container();
		}
		refEl.setAfterpathfile(afterContainer.sourceFilePath());
		
		refEl.setEntityafter(refactoring.getEntityAfter().getVerboseFullLine());
		refEl.setAfterstartline(refactoring.getEntityAfter().getStartposition());
		refEl.setAfterendline(refactoring.getEntityAfter().getEndposition());
		refEl.setAfterstartscope(refactoring.getEntityAfter().getFirstpositionstatement());

		// BEFORE entity
		SDType beforeContainer = null;		
		if (refactoring.getEntityBefore() instanceof SDType)
			beforeContainer = (SDType) refactoring.getEntityBefore();
		else 
			beforeContainer = (SDType) refactoring.getEntityBefore().container();		
		refEl.setBeforenestingLevel(beforeContainer.nestingLevel());
		while (beforeContainer.nestingLevel() > 0) {
			beforeContainer = (SDType) beforeContainer.container();
		}
		refEl.setBeforepathfile(beforeContainer.sourceFilePath());
		
		refEl.setEntitybefore(refactoring.getEntityBefore().getVerboseFullLine());
		refEl.setBeforestartline(refactoring.getEntityBefore().getStartposition());
		refEl.setBeforeendline(refactoring.getEntityBefore().getEndposition());
		refEl.setBeforestarscope(refactoring.getEntityBefore().getFirstpositionstatement());

		// CALLERS of after/before entity
		refEl.getCallerList().addAll(extractCaller("after", refactoring.getEntityAfter(), refEl.getRefactoringtype(), refEl.getSummary(), project, commitId));
		refEl.getCallerList().addAll(extractCaller("before", refactoring.getEntityBefore(), refEl.getRefactoringtype(), refEl.getSummary(), project, commitId));

		if (refEl.getCallerList() != null)
			refEl.setCallers(refEl.getCallerList().size());
		return refEl;
	}
	
	private static List<RefCaller> extractCaller(String callertype, SDEntity entity, String refactoringtype, String summary, String project, String commitId) {
		Multiset<SDMethod> callers = new Multiset<>();
		Multiset<SDType> typedCallers = new Multiset<>();
		
		List<RefCaller> refCallers = new ArrayList<RefCaller>();
		
		if (entity instanceof SDMethod) {
			SDMethod method = (SDMethod) entity;
			callers = method.callers();
		} else if (entity instanceof SDAttribute) {
			SDAttribute attribute = (SDAttribute) entity;
			callers = attribute.referencedBy();
		} else if (entity instanceof SDType) {
			SDType type = (SDType) entity;
			typedCallers = type.referencedBy();
		}

		for (SDMethod caller : callers) {
			List<Long> callerlines = callers.getCallerLines(caller);
			for (Long line : callerlines) {
				RefCaller refCaller = new RefCaller(commitId, project, callertype);
				refCaller.setSummary(summary);
				refCaller.setCallermethod(caller.getVerboseFullLine());
				refCaller.setCallerline(line);
				refCaller.setSimplename(entity.simpleName());
				
				SDType callerContainer = (SDType) caller.container();	
				refCaller.setNestingLevel(callerContainer.nestingLevel());
				while (callerContainer.nestingLevel() > 0) {
					callerContainer = (SDType) callerContainer.container();
				}
				refCaller.setCallerpath(callerContainer.sourceFilePath());
				
				refCaller.setCallerstartline(caller.getStartposition());
				refCaller.setCallerendline(caller.getEndposition());

				refCaller.setRefactoringtype(refactoringtype);
				refCaller.setEntityafter(entity.getVerboseFullLine());	
				
				refCallers.add(refCaller);
			}
		}

		for (SDType caller : typedCallers) {
			List<Long> callerlines = typedCallers.getCallerLines(caller);
			for (Long line : callerlines) {
				RefCaller refCaller = new RefCaller(commitId, project, callertype);
				refCaller.setSummary(summary);
				refCaller.setCallermethod(caller.getVerboseFullLine());
				refCaller.setCallerline(line);
				refCaller.setSimplename(entity.simpleName());
				SDType callerContainer = caller;	
				refCaller.setNestingLevel(callerContainer.nestingLevel());
				while (callerContainer.nestingLevel() > 0) {
					callerContainer = (SDType) callerContainer.container();
				}
				refCaller.setCallerpath(callerContainer.sourceFilePath());				

				refCaller.setCallerstartline(caller.getStartposition());
				refCaller.setCallerendline(caller.getEndposition());

				refCaller.setRefactoringtype(refactoringtype);
				refCaller.setEntityafter(entity.getVerboseFullLine());

				refCallers.add(refCaller);
			}
		}
		return refCallers;
	}

	private RefElement extractData(SDRefactoring refactoring, String project, String folder, String commitId)
			throws IOException {

		RefElement refEl = new RefElement(commitId, project);
		refEl.setRefactoringtype(refactoring.getRefactoringType().toString());
		refEl.setSummary(refactoring.toString());

		String afterpathfile = "";
		if (refactoring.getEntityAfter().container() instanceof SDType)
			afterpathfile = ((SDType) refactoring.getEntityAfter().container()).sourceFilePath();
		else
			afterpathfile = refactoring.getEntityAfter().pathfile();
		// System.out.println("A");
		/*
		 * if (afterpathfile.equals(
		 * "/activemq/camel/trunk/components/camel-jetty/src/main/java/org/apache/camel/component/jetty/JettyHttpComponent.java"
		 * )) System.out.println("DEBUG POINT");
		 */
		String content = refactoring.getEntityAfter().getVerboseFullLine();

		String afterFolder = folder + "v1\\";
		String beforeFolder = folder + "v0\\";

		String elementType = null;
		Multiset<SDMethod> callers = new Multiset<>();
		elementType = refactoring.getEntityAfter().getElementType();
		refEl.setElementtype(elementType);

		if (refactoring.getEntityAfter() instanceof SDMethod) {
			SDMethod method = (SDMethod) refactoring.getEntityAfter();
			callers = method.callers();
		} else if (refactoring.getEntityAfter() instanceof SDAttribute) {
			SDAttribute attribute = (SDAttribute) refactoring.getEntityAfter();
			callers = attribute.referencedBy();
		}
		// System.out.println("B");

		long[] interval = new long[2];

		// AFTER entity
		refEl.setEntityafter(content);
		refEl.setAfterpathfile(afterpathfile);

		if (elementType.equals("Method"))
			interval = FileUtil.matchCodeIntervalNumber(afterFolder, commitId, afterpathfile, content);
		else {
			interval[0] = FileUtil.matchLinenumber(afterFolder, commitId, afterpathfile, content);
			interval[1] = interval[0];
		}
		// System.out.println("C");
		refEl.setAfterstartline(interval[0]);
		refEl.setAfterendline(interval[1]);

		// BEFORE entity
		String beforepathfile = "";

		if (refactoring.getEntityBefore().container() instanceof SDType)
			beforepathfile = ((SDType) refactoring.getEntityBefore().container()).sourceFilePath();
		else
			beforepathfile = refactoring.getEntityBefore().pathfile();

		content = refactoring.getEntityBefore().getVerboseFullLine();
		// System.out.println("D");
		refEl.setEntitybefore(content);
		refEl.setBeforepathfile(beforepathfile);

		if (elementType.equals("Method"))
			interval = FileUtil.matchCodeIntervalNumber(beforeFolder, commitId, beforepathfile, content);
		else {
			interval[0] = FileUtil.matchLinenumber(beforeFolder, commitId, beforepathfile, content);
			interval[1] = interval[0];
		}
		// System.out.println("E");
		refEl.setBeforestartline(interval[0]);
		refEl.setBeforeendline(interval[1]);

		// CALLERS of after entity
		refEl.setCallers(callers.size());
		for (SDMethod caller : callers) {
			RefCaller refCaller = new RefCaller(commitId, project, null);
			String callerpathfile = "";

			if (caller.container() instanceof SDType)
				callerpathfile = ((SDType) caller.container()).sourceFilePath();
			else
				callerpathfile = caller.pathfile();

			content = caller.getVerboseFullLine();
			interval = FileUtil.matchCodeIntervalNumber(afterFolder, commitId, callerpathfile, content);

			refCaller.setSummary(refactoring.toString());
			refCaller.setCallermethod(content);
			refCaller.setCallerstartline(interval[0]);
			refCaller.setCallerendline(interval[1]);
			refCaller.setCallerpath(callerpathfile);
			refCaller.setRefactoringtype(refEl.getRefactoringtype());
			refEl.getCallerList().add(refCaller);
		}
		// System.out.println("F");
		return refEl;
	}

	private static void printdebug(String folder, RefElement refEl) {
		String afterFolder = folder + "v1\\";
		String beforeFolder = folder + "v0\\";

		System.out.print(refEl.getRefactoringtype() + " | after: " + refEl.getEntityafter() + " | " + afterFolder
				+ refEl.getRevision() + refEl.getAfterpathfile());

		System.out.print(" | [" + refEl.getAfterstartline() + "," + refEl.getAfterendline() + "("
				+ refEl.getAfterstartscope() + ")]");

		// System.out.println(refEl.getFirtstatement());
		System.out.print(" | before: " + refEl.getEntitybefore() + " | " + beforeFolder + refEl.getRevision()
				+ refEl.getBeforepathfile());

		System.out.print(" | [" + refEl.getBeforestartline() + "," + refEl.getBeforeendline() + "]");

		System.out.println(" | Callers (" + refEl.getCallers() + ")");

		
		for (RefCaller refCaller : refEl.getCallerList()) {
			System.out.println(" Caller: " + refCaller.getCallermethod() + " | " + refCaller.getCallerpath() + " | ["
					+ refCaller.getCallerstartline() + "," + refCaller.getCallerendline() + " ("
					+ refCaller.getCallerline() + ")]");
		}

	}

}
