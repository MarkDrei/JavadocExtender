package net.dreiucker.javadocextender;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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

		List<ICompletionProposal> result = new ArrayList<>();
		String prefix = null;
		try {
			prefix = context.computeIdentifierPrefix().toString();
		} catch (BadLocationException e) {
			// simply no proposal
			return result;
		}
		System.out.println("computing completion proposals based on prefix " + prefix);

		if (atBeforePrefix(prefix, context)) {
			// found @prefix, check for proposals based on that
			List<String> choices = registry.getAllTagsWithPrefix(prefix);
			result = computeProposals(context, prefix, choices);
		}
		return result;
	}

	/**
	 * Computes the proposals, assuming that the prefix can be replaced by the
	 * given strings
	 * 
	 * @param context
	 *            The context for the proposal
	 * @param prefix
	 *            the computed prefix
	 * @param choices
	 *            the valid choices for the completion
	 * @return the valid proposals
	 */
	private List<ICompletionProposal> computeProposals(ContentAssistInvocationContext context, String prefix,
			List<String> choices) {
		List<ICompletionProposal> result = new ArrayList<>();
		
		for(String choice : choices) {
			int prefixStart = context.getInvocationOffset() - prefix.length();
			CompletionProposal proposal = new CompletionProposal(choice, prefixStart,
					prefix.length(), prefixStart + choice.length(), null, 
					"@" + choice, null, "Inserts the tag @" + choice + ".");
			result.add(proposal);
		}
		
		return result;
	}

	/**
	 * Checks whether there is an @ sign before the given prefix
	 * 
	 * @param prefix
	 *            The calculated prefix
	 * @param context
	 *            The context of the proposal request
	 * @return <code>true</code> if there is an @ sign before the prefix
	 */
	private boolean atBeforePrefix(String prefix, ContentAssistInvocationContext context) {
		// position the offset to one before the prefex
		int offset = context.getInvocationOffset();
		offset -= prefix.length();
		offset--;
		
		IDocument document = context.getDocument();
		if (document != null && offset >= 0) {
			try {
				char character = document.getChar(offset);
				return character == '@';
			} catch (BadLocationException e) {
				return false;
			}
		}
		return false;
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
