package net.dreiucker.javadocextender;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Javadoc;

public class JavadocASTVisitor extends ASTVisitor {

	public JavadocASTVisitor() {
	}
	
	@Override
	public boolean visit(Javadoc node) {
		return true;
	}
}
