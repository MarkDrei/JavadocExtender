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
	
	/**
	 * Adds a listener who needs to be informed when the list of known elements
	 * changes 
	 * 
	 * @param The listener to add
	 */
	void addElementsChangedListener(IElementChangeListener listener);
	
	/**
	 * Remove a formerly registered listener.
	 * If the listener was never registered, then this is a NOP.
	 * 
	 * @param listener The listener to remove
	 */
	void removeElementsChangedListener(IElementChangeListener listener);
	
	/**
	 * Navigate to the given element.
	 * This may imply opening the an editor or a similar action.
	 * 
	 * Can be implemented as a NOP if this action is not supported, though this leaves
	 * a awkward user interface...
	 *  
	 * @param element The text/name of the element whose editor is to be opened
	 */
	void openEditor(String element);
	
	/**
	 * Get a helpful text which is displayed for the user when picking an element via the
	 * auto-complete feature.
	 * The text should explain the element to the user.
	 * 
	 *  <code>null</code> can be returned in case no explanation is available, in this
	 *  case a default text is added.
	 * 
	 * @param element The element whose description is requested
	 * @return A descriptive text or <code>null</code>, if no description is available
	 */
	String getElementDescription(String element);
}
