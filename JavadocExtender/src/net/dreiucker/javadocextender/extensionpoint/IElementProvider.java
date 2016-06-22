package net.dreiucker.javadocextender.extensionpoint;

import java.util.Set;

/**
 * An ElementProvider provides information about one javadoc and valid
 * elements after this tag.
 * 
 * @author Mark
 *
 */
public interface IElementProvider {

	/**
	 * Report the one javadoc tag which is supported by this element provider.
	 * 
	 * @return The valid tag, not including the @ sign.
	 */
	String getTag();

	/**
	 * Reports all known elements behind the supported tag.
	 * 
	 * Known elements will be used for example for offering auto completion and
	 * checking of consistency
	 * 
	 * @return All known valid strings after this javadoc tag
	 */
	Set<String> getKnownElements();

	/**
	 * Are strings which are not in the set of known elements allowed after the
	 * supported tag?
	 * 
	 * @return <code>true</code> if unknown elements are allowed,
	 *         <code>false</code> if the should be treated as an error
	 */
	boolean unknownElementsAllowed();
	
	void addElementsChangedListener(IElementChangeListener listener);
	
	void removeElementsChangedListener(IElementChangeListener listener);

	void openEditor(String text);
}
