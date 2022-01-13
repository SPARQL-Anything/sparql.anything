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

package com.github.sparqlanything.model.filestream;

import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.util.UnionDatasetGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class FileStreamIndexIterator implements Iterator<Quad>{

	private static Logger log = LoggerFactory.getLogger(com.github.sparqlanything.model.filestream.FileStreamIndexIterator.class);
	public static final String ENDSIGNAL = "endsignal";
	private Quad next = null;
	private Quad target;
	private List<Object> index;
	private int idx = 0;
	private boolean unionGraph = false;

	public FileStreamIndexIterator(List<Object> index, Quad target){
		this.index = index;
		this.target = target;
		if(target.getGraph().isURI() && target.getGraph().getURI().equals("urn:x-arq:UnionGraph")){
			unionGraph = true;
		}else{
			unionGraph = false;
		}
	}

	@Override
	public boolean hasNext() {
		while(!index.get(index.size()-1).equals(ENDSIGNAL) || idx != index.size() - 1) {
			if (index.size() < idx + 1) {
				// wait
				continue;
			}
			Quad q = (Quad) index.get(idx);
			idx++;
			// Pass if matches

			if(unionGraph && target.asTriple().matches(q.asTriple())){
				next = q;
				return true;
			}else
			if (target.matches(q.getGraph(), q.getSubject(), q.getPredicate(), q.getObject())) {
				next = q;
				return true;
			}
		}

		return false;
	}

	@Override
	public Quad next() {
		return next;
	}
}
