package net.dreiucker.javadocextender.provider;

import java.util.HashSet;
import java.util.Set;

import net.dreiucker.javadocextender.extensionpoint.IElementProvider;

/**
 * Provides dummy elements for the javadoc.
 * This class is for testing and experimenting purposes.
 * 
 * @author Mark
 *
 */
public class DummyElementProvider implements IElementProvider {
	
	private final Set<String> VALID_TAGS = new HashSet<>();

	public DummyElementProvider() {
		VALID_TAGS.add("dummy.Tag");
		VALID_TAGS.add("prefixOneTag");
		VALID_TAGS.add("prefixAnotherTag");
	}

	@Override
	public Set<String> getValidTags() {
		return VALID_TAGS;
	}

	@Override
	public Set<String> getKnownElements(String tag) {
		HashSet<String> set = new HashSet<>();
		set.add("ValidElement1");
		set.add("ValidElement2");
		set.add("ValidElement3");
		return set;
	}

	@Override
	public boolean unknownElementsAllowed(String tag) {
		if ("dummy.Tag".equals(tag)) {
			return false;
		}
		return true;
	}

}
