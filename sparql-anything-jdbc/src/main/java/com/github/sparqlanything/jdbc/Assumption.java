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
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class Predicate extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(Subject.class, Object.class);
		}
	}
	static class Object extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class Container extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotValue.class, SlotColumn.class, SlotRow.class, TypeProperty.class, TypeTable.class);
		}
		@Override
		public Set<Class<? extends Interpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Subject.class, Object.class);
		}
	}
	static class TypeProperty extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(Subject.class, Object.class, SlotValue.class, SlotColumn.class, SlotRow.class, TypeTable.class, ContainerRow.class, ContainerTable.class);
		}

		@Override
		public Set<Class<? extends Interpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class TypeTable extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotValue.class, SlotColumn.class, SlotRow.class, ContainerRow.class, ContainerTable.class);
		}

		@Override
		public Set<Class<? extends Interpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Object.class);
		}
	}
	static class ContainerRow extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(Predicate.class, SlotRow.class, SlotColumn.class, SlotValue.class, ContainerTable.class );
		}

		@Override
		public Set<Class<? extends Interpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Subject.class, Object.class);
		}
	}

	static class ContainerTable extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(ContainerRow.class, Predicate.class, Object.class, Predicate.class, SlotRow.class, SlotColumn.class, SlotValue.class);
		}

		@Override
		public Set<Class<? extends Interpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Subject.class, Container.class);
		}
	}
	static class SlotRow extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotValue.class, ContainerRow.class, TypeProperty.class, Subject.class, Object.class, ContainerTable.class, SlotColumn.class);
		}

		@Override
		public Set<Class<? extends Interpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class SlotColumn extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotRow.class,SlotValue.class, ContainerRow.class, TypeProperty.class, Subject.class, Object.class, ContainerTable.class );
		}

		@Override
		public Set<Class<? extends Interpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Predicate.class);
		}
	}
	static class SlotValue extends Assumption {
		@Override
		public Set<Class<? extends Interpretation>> inconsistentTypes() {
			return ImmutableSet.of(SlotRow.class,SlotColumn.class, ContainerRow.class, TypeProperty.class, Subject.class, Object.class, ContainerTable.class );
		}

		@Override
		public Set<Class<? extends Interpretation>> specialisationOfTypes() {
			return ImmutableSet.of(Object.class);
		}
	}
}
