/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.jdbc;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class Assumption implements Interpretation {
	static class Subject extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentWith() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class Predicate extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentWith() {
			return ImmutableSet.of(Subject.class, Object.class);
		}
	}
	static class Object extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentWith() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class Container extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentWith() {
			return ImmutableSet.of(SlotValue.class, SlotColumn.class, SlotRow.class, TypeProperty.class, TypeTable.class);
		}
		@Override
		public Set<Class<? extends Interpretation>> specialisationOf() {
			return ImmutableSet.of(Subject.class, Object.class);
		}
	}
	static class TypeProperty extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentWith() {
			return ImmutableSet.of(SlotValue.class, SlotColumn.class, SlotRow.class, TypeTable.class, ContainerRow.class, ContainerTable.class);
		}

		@Override
		public Set<Class<? extends Interpretation>> specialisationOf() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class TypeTable extends Assumption {}
	static class ContainerRow extends Assumption {}
	static class ContainerTable extends Assumption {}
	static class SlotRow extends Assumption {}
	static class SlotColumn extends Assumption {}
	static class SlotValue extends Assumption {}
}
