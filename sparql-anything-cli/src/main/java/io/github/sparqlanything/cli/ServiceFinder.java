package io.github.sparqlanything.cli;

import io.github.sparqlanything.engine.OpVisitorSkip;
import io.github.sparqlanything.engine.Utils;
import org.apache.jena.sparql.algebra.op.*;

public class ServiceFinder extends OpVisitorSkip {

	private boolean hasFxService = false;

	public boolean hasFxService() {
		return hasFxService;
	}

	@Override
	public void visit(OpService opService) {

		if (Utils.isFacadeXServiceNode(opService)) {
			this.hasFxService = true;
		}

	}

}
