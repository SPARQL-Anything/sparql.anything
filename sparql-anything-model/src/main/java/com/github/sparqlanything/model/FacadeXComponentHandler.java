/*
 * Copyright (c) 2022 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sparqlanything.model;

import java.net.URI;

public interface FacadeXComponentHandler {

	boolean addContainer(String dataSourceId, String containerId, String slotKey, String childContainerId);

	boolean addContainer(String dataSourceId, String containerId, URI customKey, String childContainerId);

	boolean addContainer(String dataSourceId, String containerId, Integer slotKey, String childContainerId);

	boolean addType(String dataSourceId, String containerId, String typeId);

	boolean addType(String dataSourceId, String containerId, URI type);

	boolean addValue(String dataSourceId, String containerId, String slotKey, Object value);

	boolean addValue(String dataSourceId, String containerId, URI customKey, Object value);

	boolean addValue(String dataSourceId, String containerId, Integer slotKey, Object value);

	boolean addRoot(String dataSourceId, String rootId);

}
