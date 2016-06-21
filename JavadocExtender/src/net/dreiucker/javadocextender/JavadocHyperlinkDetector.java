package net.dreiucker.javadocextender;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import net.dreiucker.javadocextender.extensionpoint.IElementProvider;

public class JavadocHyperlinkDetector extends AbstractHyperlinkDetector {
	
	private final static boolean DEBUG = false;

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		
		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();
		
		StringBuilder linkText = new StringBuilder();
		StringBuilder tagName = new StringBuilder();
		int linkRegionStart = -1, linkRegionEnd = -1;
		try {
			// idea: from the given region offset, search backwards till either
			//  the end of the line or a whitespace. This is the start of our identifier
			IRegion lineRegion = document.getLineInformationOfOffset(offset);
			int endSearch = lineRegion.getOffset();
			int currentOffset;
			for (
					// -1 since we search the start, and don't want to recognize the first
					//  whitespace as the start of the link region
					currentOffset = region.getOffset() - 1;  
					currentOffset >= endSearch;
					currentOffset--) 
			{
				if (!Character.isJavaIdentifierPart(document.getChar(currentOffset))) {
					linkRegionStart = currentOffset + 1;
					break;
				}
			}
			if (linkRegionStart < 0) {
				return null;
			}
			
			// look for an '@' sign before that
			int atSignStartOffset = -1;
			for (; currentOffset >= endSearch; currentOffset--) {
				if (document.getChar(currentOffset) == '@') {
					atSignStartOffset = currentOffset;
					break;
				}
			}
			if (atSignStartOffset < 0) {
				return null;
			}
			
			// from the '@' sign, go to the right till the end of the identifier
			int tagNameEndOffset = -1;
			for (currentOffset = atSignStartOffset + 1; currentOffset < linkRegionStart; currentOffset++) {
				char c = document.getChar(currentOffset);
				if (!Character.isJavaIdentifierPart(c)) {
					tagNameEndOffset = currentOffset;
					break;
				}
				tagName.append(c);
			}
			if (tagNameEndOffset < 0) {
				return null;
			}
			
			// now check: between end of tag name and start of link text, only whitespace is allowed
			for (currentOffset = tagNameEndOffset; currentOffset < linkRegionStart; currentOffset++) {
				if (!Character.isWhitespace(document.getChar(currentOffset))) {
					return null;
				}
			}
			
			// finish search for link text: go forward until we hit the end of the identifier
			endSearch = lineRegion.getOffset() + lineRegion.getLength();
			for (currentOffset = linkRegionStart; currentOffset <= endSearch; currentOffset++)
			{
				char c = document.getChar(currentOffset);
				if (!Character.isJavaIdentifierPart(c)) {
					linkRegionEnd = currentOffset - 1;
					break;
				}
				linkText.append(c);
			}
		} catch (BadLocationException e) {
			// TODO probably best to just ignore the error?
			System.err.println(" MDD Javadoc Hyperlink detector: BadLocationException");
			return null;
		}
		
		if (linkRegionEnd >= 0) {
			//TODO remove
			if (DEBUG) {
				System.out.println(" MDD tag name text: \"" + tagName + "\"");
				System.out.println(" MDD hyperling detection text: \"" + linkText + "\"");
			}
			
			IElementProvider provider = ElementRegistry.getInstance().getProviderForTag(
					tagName.toString(), linkText.toString());
			if (provider != null) {
				IRegion hyperlingRegion = new Region(linkRegionStart, linkRegionEnd - linkRegionStart + 1); 
				return new IHyperlink[] {new JavadocHyperlink(hyperlingRegion, linkText.toString())};
			}
		}
		
		return null;
	}

}
