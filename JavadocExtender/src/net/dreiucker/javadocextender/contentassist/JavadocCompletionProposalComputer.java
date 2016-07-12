package net.dreiucker.javadocextender.contentassist;

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

import net.dreiucker.javadocextender.ElementRegistry;

public class JavadocCompletionProposalComputer implements IJavaCompletionProposalComputer {
	
	private static final boolean DEBUG = false;
	
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

		List<ICompletionProposal> result = null;
		String prefix = null;
		try {
			prefix = computeIdentifierPrefix(context);
		} catch (BadLocationException e) {
			// simply no proposal
			return result;
		}
		
		if (DEBUG) {
			System.out.println("computing completion proposals based on prefix " + prefix);
		}

		if (atBeforePrefix(prefix, context)) {
			// found @prefix, check for proposals based on that
			List<String> choices = registry.getAllTagsWithPrefix(prefix);
			result = computeProposals(context, prefix, choices);
		} else {
			// search for proposals inside a tag
			result = searchProposalsInsideTag(context);
		}
		
		// must not return null
		if (result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	/**
	 * Assume that we might be inside a tag when auto complete is triggered, this method
	 * identifies the tag in question and computes the according proposals.
	 * 
	 * @param context
	 * @param prefix
	 * @return
	 */
	private List<ICompletionProposal> searchProposalsInsideTag(ContentAssistInvocationContext context) {
		IDocument document = context.getDocument();
		if (document == null) return null;
		
		
		
		/*
		 * This scenario explains the variable names:
		 * 
		 * 
		 *              atSignOffset (+1 = tagname start)              user's cursor
		 *              v                                              v
		 * text:        @tagname This is some text that gets auto compl|
		 *                      ^                                      ^
		 *                      prefix start                           context.getInvocationOffset()
		 *              
		 * prefix text is "This is some text that gets auto compl", i.e. trim
		 * tag name and white spaces 
		 */
		
		// search backwards till the last @ sign 
		int offset = context.getInvocationOffset();
		// (but abort for performance reasons)
		int endOffset = Math.max(-1, offset - 200);
		int atSignOffset = -1;
		try {
			// search backwards till the last @ sign 
			while (offset > endOffset) {
				if (document.getChar(offset) == '@') {
					atSignOffset = offset;
					break;
				}
				offset--;
			}
			
			// determine the tag name
			if (atSignOffset >= 0) {
				String tagname = null;
				int tagNameStart = atSignOffset+ 1;
				int prefixStart = tagNameStart;
				while (prefixStart < context.getInvocationOffset()) {
					
					char char1 = document.getChar(prefixStart);
					if (!Character.isJavaIdentifierPart(char1) && !(char1 == '.')) {
						tagname = document.get(tagNameStart, prefixStart - tagNameStart);
						break;
					}
					prefixStart++;
				}
				
				if (tagname != null) {
					prefixStart++; //skip whitespace
					// we found the tag name.
					// find the prefix (after the tag name) next.
					String prefix = document.get(prefixStart, 
							context.getInvocationOffset() - prefixStart);
					return computeProposals(context, tagname, prefix, prefixStart);
				}
			}
		} catch (BadLocationException e) {
			return null;
		}

		return null;
	}

	/**
	 * Create proposals, assuming a concrete tagname and a prefix after that tag name is given 
	 * @param context
	 * @param tagname
	 * @param prefix
	 */
	private List<ICompletionProposal> computeProposals(ContentAssistInvocationContext context, String tagname, 
			String prefix, int prefixStart) {
		List<String> choices = registry.getAllKnownValues(tagname, prefix);
		List<ICompletionProposal> proposals = new ArrayList<>();
		if (choices != null) {
			for (String choice : choices) {
				CompletionProposal proposal = new CompletionProposal(choice, prefixStart,
						prefix.length(), prefixStart + choice.length(), null, 
						choice, null, "Inserts the text \"" + choice + "\".");
				proposals.add(proposal);
			}
		}
		
		return proposals;
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
	
	/**
	 * fixed version of ContentAssistInvocationContext.computeIdentifierPrefix()
	 * which works with fully qualified names 
	 *  
	 *  @see ContentAssistInvocationContext
	 *  
	 * @param context
	 * @return
	 * @throws BadLocationException
	 */
	private String computeIdentifierPrefix(ContentAssistInvocationContext context) throws BadLocationException {
		IDocument document= context.getDocument();
		if (document == null)
			return null;
		int end= context.getInvocationOffset();
		int start= end;
		while (--start >= 0) {
			char char1 = document.getChar(start);
			if (!Character.isJavaIdentifierPart(char1) && !(char1 == '.') )
				break;
		}
		start++;
		return document.get(start, end - start);
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
