package net.dreiucker.javadocextender;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class JavadocHyperlink implements IHyperlink {

	private String text;
	private IRegion region;

	public JavadocHyperlink(IRegion region, String text) {
		this.region = region;
		this.text = text;
	}
	
	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return "This is the label";
	}

	@Override
	public String getHyperlinkText() {
		return text;
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

}
