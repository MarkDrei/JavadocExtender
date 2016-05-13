package net.dreiucker.javadocextender.extensionpoint;

import java.util.Set;

/**
 * An ElementProvider provides information about new javadoc tags and valid
 * elements after those tags.
 * 
 * @author Mark
 *
 */
public interface ElementProvider {

	/**
	 * Report all javadoc tags which are supported by this element provider.
	 * 
	 * @return The valid tags, not including the @ sign.
	 */
	Set<String> getValidTags();

	/**
	 * Reports all known elements behind a certain tag.
	 * 
	 * Known elements will be used for example for offering auto completion and
	 * checking of consistency
	 * 
	 * @param tag
	 *            The javadoc tag without the @ sign
	 * @return All known valid strings after this javadoc tag
	 */
	Set<String> getKnownElements(String tag);

	/**
	 * Are strings which are not in the set of known elements allowed after the
	 * given tag?
	 * 
	 * @param tag
	 *            The javadoc tag without the @ sign
	 * @return <code>true</code> if unknown elements are allowed,
	 *         <code>false</code> if the should be treated as an error
	 */
	boolean unknownElementsAllowed(String tag);
}
