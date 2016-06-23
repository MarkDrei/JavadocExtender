package net.dreiucker.javadocextender.marker;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;

/**
 * <p>Upon compilation, checks whether any managed javadoc tags.</p>
 * <p> mention elements which are not known references. 
 * If so, markers are generated. </p>
 * 
 * @author Mark
 *
 */
public class JavadocCompilationParticipant extends CompilationParticipant {

	public static final String JAVADOC_EXTENDER_UNKNOWN_REFERENCE_MARKER = "JavadocExtender.unknownReferenceMarker";
	
	@Override
	public int aboutToBuild(IJavaProject project) {
		return READY_FOR_BUILD;
	}
	
	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}
	
	@Override
	public void reconcile(ReconcileContext context) {
		IJavaElementDelta delta = context.getDelta();
		if (delta != null) {
			IResource resource;
			try {
				resource = delta.getElement().getUnderlyingResource();
				context.getAST8().accept(new JavadocASTVisitor(context.getAST8(), resource));
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

}
