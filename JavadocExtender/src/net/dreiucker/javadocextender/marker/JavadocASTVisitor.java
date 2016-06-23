package net.dreiucker.javadocextender.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;

import net.dreiucker.javadocextender.ElementRegistry;

/**
 * Walks the AST and generates markers for unknown javadoc elements 
 * 
 * @author Mark
 *
 */
public class JavadocASTVisitor extends ASTVisitor {

	private final static boolean DEBUG = false;

	private String currentJavadocTag = null;
	private IResource resource;
	private CompilationUnit compilationUnit;

	/**
	 * 
	 * @param compilationUnit
	 * @param file
	 */
	public JavadocASTVisitor(CompilationUnit compilationUnit, IResource file) {
		this.resource = file;
		this.compilationUnit = compilationUnit;
		
		// delete old markers
		try {
			file.deleteMarkers(JavadocCompilationParticipant.JAVADOC_EXTENDER_UNKNOWN_REFERENCE_MARKER, true,
					IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			System.err.println("Internal error upon marker deletion on file '" + file.getFullPath() + "', message: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public boolean visit(Javadoc node) {
		return true;
	}

	@Override
	public boolean visit(TagElement node) {
		String tagName = node.getTagName().substring(1);
		if (ElementRegistry.getInstance().isKnownTag(tagName)) {
			currentJavadocTag = tagName;
			return true;
		}
		currentJavadocTag = null;
		return false;
	}

	@Override
	public boolean visit(Block node) {
		// minor optimization to speed up walkthrough
		return false;
	}

	@Override
	public boolean visit(TextElement node) {

		String text = node.getText();
		char[] charArray = text.toCharArray();
		// count whitespaces at the beginning of the text
		int whitespaceCount = 0;
		for (int i = 0; i < charArray.length; i++) {
			if (Character.isWhitespace(charArray[i])) {
				whitespaceCount++;
			} else {
				break;
			}
		}
		int startPosition = node.getStartPosition() + whitespaceCount;
		int endPosition = startPosition + node.getLength() - whitespaceCount;
		text = text.trim(); // also remove trailing whitespaces

		ElementRegistry registry = ElementRegistry.getInstance();
		if (!registry.isElementValid(currentJavadocTag, text)) {

			try {
				IMarker marker = resource
						.createMarker(JavadocCompilationParticipant.JAVADOC_EXTENDER_UNKNOWN_REFERENCE_MARKER);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
				marker.setAttribute(IMarker.MESSAGE,
						"The value \"" + text + "\" is not a valid after the tag " + currentJavadocTag);
				marker.setAttribute(IMarker.CHAR_START, startPosition);
				marker.setAttribute(IMarker.CHAR_END, endPosition);
				int line = compilationUnit.getLineNumber(startPosition);
				marker.setAttribute(IMarker.LINE_NUMBER, line);
				marker.setAttribute(IMarker.LOCATION, "line " + line);
			} catch (CoreException e) {
				System.err.println("Internal error upon marker creation on file '" + resource.getFullPath()
						+ "', message: " + e.getLocalizedMessage());
				e.printStackTrace();
			}

			if (DEBUG) {
				System.out.println("MDD Javadoc Tag " + currentJavadocTag + " and text \"" + text + "\" are not valid");
			}
		} else {
			if (DEBUG) {
				System.out.println("MDD Javadoc Tag " + currentJavadocTag + " and text \"" + text + "\" are valid");
			}
		}

		return false;
	}
}
