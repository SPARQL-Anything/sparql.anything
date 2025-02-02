package io.github.sparqlanything.fxrdb;

import io.github.sparqlanything.fxbgp.FX;
import io.github.sparqlanything.fxbgp.FXModel;
import io.github.sparqlanything.fxbgp.InterpretationOfBGP;
import io.github.sparqlanything.fxbgp.NodeInterpretationRule;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

public class FXRDBModel extends FXModel {

	private static FXRDBModel instance = null;

	public final static FXRDBModel getFXRDBModel(){
		if(instance == null){
			instance = new FXRDBModel();
		}
		return instance;
	}

	@Override
	protected void extend() {
		super.extend();
		// then
		setSpecialisedBy(FX.Container, FXRDB.ContainerTable);
		setSpecialisedBy(FX.Container, FXRDB.ContainerEntity);
		setSpecialisedBy(FX.Type, FXRDB.TypeTable);
		setSpecialisedBy(FX.SlotString, FXRDB.SlotColumn);
		setSpecialisedBy(FX.SlotNumber, FXRDB.SlotRow);
		setSpecialisedBy(FX.Value, FXRDB.Cell);

		setInconsistentWith(FXRDB.ContainerTable, FXRDB.ContainerEntity, FXRDB.SlotColumn, FXRDB.SlotRow, FXRDB.Cell, FXRDB.TypeTable, FX.TypeProperty, FX.Root);
		setInconsistentWith(FXRDB.ContainerEntity, FXRDB.ContainerTable, FXRDB.SlotColumn, FXRDB.SlotRow, FXRDB.Cell, FXRDB.TypeTable, FX.TypeProperty, FX.Root);
		setInconsistentWith(FXRDB.SlotColumn, FXRDB.ContainerEntity, FXRDB.ContainerTable, FXRDB.SlotRow, FXRDB.Cell, FXRDB.TypeTable, FX.TypeProperty, FX.Root, FX.SlotNumber);
		setInconsistentWith(FXRDB.SlotRow, FXRDB.SlotColumn, FXRDB.ContainerEntity, FXRDB.ContainerTable, FXRDB.Cell, FXRDB.TypeTable, FX.TypeProperty, FX.Root, FX.SlotString);
		setInconsistentWith(FXRDB.Cell, FXRDB.SlotColumn, FXRDB.ContainerEntity, FXRDB.ContainerTable, FXRDB.SlotRow, FXRDB.TypeTable, FX.TypeProperty, FX.Root);
		setInconsistentWith(FXRDB.TypeTable, FXRDB.SlotColumn, FXRDB.ContainerEntity, FXRDB.ContainerTable, FXRDB.SlotRow, FXRDB.Cell, FX.TypeProperty, FX.Root);

		/**
		 * 1.1 Container(o) → SlotRow(p)
		 */
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getPredicate().equals(node)
						&& previous.getInterpretation(t.getObject()).getTerm().equals(FX.Container)){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.SlotRow));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 1.1 Container(o) → ContainerTable(s)
		 */
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getSubject().equals(node)
						&& previous.getInterpretation(t.getObject()).getTerm().equals(FX.Container)){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.ContainerTable));
						return true;
					}
				}
				return false;
			}
		});


		/**
		 * 2.1 Literal(o) → ContainerEntity(s)
		 */
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getSubject().equals(node)
					&& t.getObject().isLiteral()){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.ContainerEntity));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 2.2 Literal(o) → SlotColumn(p)
		 */
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getPredicate().equals(node)
						&& t.getObject().isLiteral()){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.SlotColumn));
						return true;
					}
				}
				return false;
			}
		});


		/**
		 * 3 Root(o) → ContainerTable(s)
		 */
		addInferenceRule(new NodeInterpretationRule() {

			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getSubject().equals(node)
						&& previous.getInterpretation(t.getObject()).getTerm().equals(FX.Root)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.ContainerTable));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 4.1 TypeProperty(p) ∧ !=Root(o) → ContainerEntity(s)
		 */
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getSubject().equals(node)
						&& previous.getInterpretation(t.getPredicate()).getTerm().equals(FX.TypeProperty)
						&& !previous.getInterpretation(t.getObject()).getTerm().equals(FX.Root)
						&& !t.getObject().equals(FXRoot)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.ContainerEntity));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 4.2 TypeProperty(p) ∧ !=Root(o) → TypeTable(o)
		 */
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getObject().equals(node)
						&& previous.getInterpretation(t.getPredicate()).getTerm().equals(FX.TypeProperty)
						&& !previous.getInterpretation(t.getObject()).getTerm().equals(FX.Root)
						&& !t.getObject().equals(FXRoot)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.TypeTable));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 5.1 SlotNumber(p) → SlotRow(p)
		 */
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getPredicate().equals(node)
						&& previous.getInterpretation(t.getPredicate()).getTerm().equals(FX.SlotNumber)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.SlotRow));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 5.2 SlotRow(p) → ContainerTable(s)
		 */
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getSubject().equals(node)
						&& previous.getInterpretation(t.getPredicate()).getTerm().equals(FXRDB.SlotRow)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.ContainerTable));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 5.3 SlotNumber(p) → ContainerEntity(o)
		 */
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getObject().equals(node)
						&& previous.getInterpretation(t.getPredicate()).getTerm().equals(FXRDB.SlotRow)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.ContainerEntity));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 6.1 SlotString(p) → SlotColumn(p)
		 */
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getPredicate().equals(node)
						&& previous.getInterpretation(t.getPredicate()).getTerm().equals(FX.SlotString)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.SlotColumn));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 6.2 SlotColumn(p) → ContainerEntity(s)
		 */
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getSubject().equals(node)
						&& previous.getInterpretation(t.getPredicate()).getTerm().equals(FXRDB.SlotColumn)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.ContainerEntity));
						return true;
					}
				}
				return false;
			}
		});

		/**
		 * 6.3 SlotColumn(p) → Cell(o)
		 */
		addInferenceRule(new NodeInterpretationRule() {
			@Override
			protected boolean when(Node node, InterpretationOfBGP previous) {
				for(Triple t: previous.getOpBGP().getPattern()){
					if(t.getObject().equals(node)
						&& previous.getInterpretation(t.getPredicate()).getTerm().equals(FXRDB.SlotColumn)
					){
						set(getIF().make(previous.getOpBGP(), node, FXRDB.Cell));
						return true;
					}
				}
				return false;
			}
		});
	}
}
