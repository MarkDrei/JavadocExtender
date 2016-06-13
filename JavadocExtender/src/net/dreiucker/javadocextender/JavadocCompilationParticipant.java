package net.dreiucker.javadocextender;

import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

/**
 * <p>Upon compilation, checks whether any managed javadoc tags.</p>
 * <p> mention elements which are not known references. 
 * If so, markers are generated. </p>
 * 
 * @author Mark
 *
 */
public class JavadocCompilationParticipant extends CompilationParticipant {

	
	public JavadocCompilationParticipant() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		super.buildStarting(files, isBatch);

		for (BuildContext file : files) {
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(file.getContents());
			parser.setResolveBindings(false);
			ASTNode node = parser.createAST(null);
			node.accept(new JavadocASTVisitor());
		}
	}

}
