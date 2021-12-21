/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package com.github.sparqlanything.model.filestream;

import org.apache.jena.sparql.core.Quad;

import java.util.ArrayList;
import java.util.List;

public class FileStreamBuffer {
	List<Quad> queue = new ArrayList<Quad>();
	boolean completed = false;

	boolean isEmpty(){
		return queue.isEmpty();
	}

	void add(Quad quad){
		queue.add(quad);
	}

	boolean isCompleted(){
		return completed;
	}

	boolean isWaiting(){
		return isEmpty() && !isCompleted();
	}

	Quad fetch(){
		return queue.remove(0);
	}

	void setCompleted(){
		completed = true;
	}
}
