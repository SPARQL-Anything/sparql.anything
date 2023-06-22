/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.sparqlanything.jdbc;

public class InconsistentTypesException extends InconsistentAssumptionException {
	Class<? extends NodeInterpretation> wasType;
	Class<? extends NodeInterpretation> isType;

	public InconsistentTypesException(Class<? extends NodeInterpretation> wasType, Class<? extends NodeInterpretation> isType) {
		super("Inconsistent types: " + wasType + " " + isType);
		this.wasType = wasType;
		this.isType = isType;
	}

	public Class<? extends NodeInterpretation> getWasType(){
		return wasType;
	}

	public Class<? extends NodeInterpretation> getIsType(){
		return isType;
	}
}
