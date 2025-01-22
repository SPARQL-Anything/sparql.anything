package io.github.sparqlanything.fxbgp;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InterpretationFactory {
	final FXModel FXM;
	InterpretationFactory(FXModel model){
		FXM = model;
	}
	public InterpretationOfNode make(final OpBGP bgp, final Node node, final FX element){
		return new InterpretationOfNode() {

			@Override
			public boolean consistentWith(InterpretationOfNode node) {
				return false;
			}

			@Override
			public FX getInterpretation() {
				return element;
			}

			@Override
			public Node getNode() {
				return node;
			}

			@Override
			public boolean isGrounded() {
				return FXM.isGrounded(element);
			}

			@Override
			public OpBGP getOpBGP() {
				return bgp;
			}
		};
	}

	/**
	 * This method generates a different interpretation from an existing one.
	 * It keeps node and bgp but modifies the FX element.
	 *
	 * @param interpretation
	 * @param element
	 * @return
	 */
	public InterpretationOfNode makeFrom(final InterpretationOfNode interpretation, final FX element){
		return make(interpretation.getOpBGP(), interpretation.getNode(), element);
	}

	/**
	 * This method generates a starting interpretation of a BGP
	 * @param bgp
	 * @return
	 */
	public InterpretationOfBGP make(final OpBGP bgp){
		final Map<Node,InterpretationOfNode> nodeInderpretations = new HashMap<>();
		return new InterpretationOfBGP() {
			@Override
			public Map<Node,InterpretationOfNode>  getInterpretationOfNodes() {
				return Collections.unmodifiableMap(nodeInderpretations);
			}

			@Override
			public boolean isGrounded() {
				return false;
			}

			@Override
			public boolean isStart() {
				return true;
			}

			@Override
			public InterpretationOfNode getInterpretation(Node node) {
				return nodeInderpretations.get(node);
			}

			@Override
			public InterpretationOfBGP previous() {
				return null;
			}

			@Override
			public OpBGP getOpBGP() {
				return bgp;
			}
		};
	}


	public InterpretationOfBGP make(InterpretationOfBGP previous, Node n, FX element){
		return make(previous, make(previous.getOpBGP(), n, element));
	}

	/**
	 * We make a new interpretation of a BGP by setting the interpretation of a single node.
	 * We don't do any verification (consistency).
	 *
	 * @param previous
	 * @param newInterpretation
	 * @return
	 */
	public InterpretationOfBGP make(final InterpretationOfBGP previous, InterpretationOfNode newInterpretation){
		final Map<Node,InterpretationOfNode> nodeInderpretations = new HashMap<>();
		// Inherit all previous interpretations
		nodeInderpretations.putAll(previous.getInterpretationOfNodes());
		// ... except for this node
		nodeInderpretations.put(newInterpretation.getNode(),newInterpretation);
		// Compute if this is grounded
		boolean isGrounded = true;
		for(Map.Entry<Node,InterpretationOfNode> ion: nodeInderpretations.entrySet()){
			if(!ion.getValue().isGrounded()){
				isGrounded = false;
				break;
			}
		}
		final Boolean grounded = isGrounded;
		return new InterpretationOfBGP() {
			@Override
			public Map<Node,InterpretationOfNode>  getInterpretationOfNodes() {
				return Collections.unmodifiableMap(nodeInderpretations);
			}

			@Override
			public boolean isGrounded() {
				return grounded;
			}

			@Override
			public boolean isStart() {
				return false;
			}

			@Override
			public InterpretationOfNode getInterpretation(Node node) {
				return nodeInderpretations.get(node);
			}

			@Override
			public InterpretationOfBGP previous() {
				return previous;
			}

			@Override
			public OpBGP getOpBGP() {
				return previous().getOpBGP();
			}
		};
	}
}
