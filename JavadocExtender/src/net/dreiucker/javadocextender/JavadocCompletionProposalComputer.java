package net.dreiucker.javadocextender;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class JavadocCompletionProposalComputer implements IJavaCompletionProposalComputer {

	ElementRegistry registry;

	public JavadocCompletionProposalComputer() {
		registry = ElementRegistry.getInstance();
	}

	@Override
	public void sessionStarted() {
	}

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {

		String prefix = null;
		try {
			prefix = context.computeIdentifierPrefix().toString();
		} catch (BadLocationException e) {
		}
		System.out.println("computing completion proposals based on prefix " + prefix);
		ArrayList<ICompletionProposal> list = new ArrayList<>();

		list.add(new CompletionProposal("MDDs New String", 0, 0, 0, 
				null, "Display String", null, "Additional Proposal Info"));
		return list;
	}

	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		System.out.println("Computing context information");
		return new ArrayList<>();
	}

	@Override
	public String getErrorMessage() {
		// called with every proposal call, null indicates no error.
		// Error are displayed in the status bar of eclipse
		return null;
	}

	@Override
	public void sessionEnded() {
	}

}
