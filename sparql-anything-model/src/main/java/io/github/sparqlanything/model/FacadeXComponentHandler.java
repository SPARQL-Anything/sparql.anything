/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.model;

import java.net.URI;

/**
 * This class adds to the resulting Dataset FacadeX components, namely the root of the FacadeX model and containers.
 * Moreover, it allows to specify properties and relations of these components.
 */
public interface FacadeXComponentHandler {

	/**
	 * Adds to the FacadeX model a child container as slot of a parent container of a given data source.
	 * @param dataSourceId the identifier of the data source of the container
	 * @param containerId the identifier of the parent container
	 * @param slotKey the key of the slot of the containerId
	 * @param childContainerId the identifier of the contained container
	 * @return true if the container is added to the model, false otherwise
	 */
	boolean addContainer(String dataSourceId, String containerId, String slotKey, String childContainerId);

	/**
	 * Adds to the FacadeX model a child container as slot of a parent container of a given data source. The relation between the parent and the child container is identified by the customKey.
	 * @param dataSourceId the identifier of the data source of the container
	 * @param containerId the identifier of the parent container
	 * @param customKey the identifier of the relation between parent and child container
	 * @param childContainerId the identifier of the contained container
	 * @return true if the container is added to the model, false otherwise
	 */
	boolean addContainer(String dataSourceId, String containerId, URI customKey, String childContainerId);

	/**
	 * Adds to the FacadeX model a child container as slot of a parent container of a given data source. The relation between the parent and the child container is identified by the customKey.
	 * @param dataSourceId the identifier of the data source of the container
	 * @param containerId the identifier of the parent container
	 * @param slotKey the key of the slot of the containerId
	 * @param childContainerId the identifier of the contained container
	 * @return true if the container is added to the model, false otherwise
	 */
	boolean addContainer(String dataSourceId, String containerId, Integer slotKey, String childContainerId);


	/**
	 * Adds to the FacadeX model the specification of the type of the container of a given data source.
	 * @param dataSourceId the identifier of the data source of the container
	 * @param containerId the identifier of the parent container
	 * @param typeId the identifier of the type
	 * @return true if the type statement is specified, false otherwise
	 */
	boolean addType(String dataSourceId, String containerId, String typeId);

	/**
	 * Adds to the FacadeX model the specification of the type of the container of a given data source.
	 * @param dataSourceId the identifier of the data source of the container
	 * @param containerId the identifier of the parent container
	 * @param type the URI identifying the type of the container
	 * @return true if the type statement is specified, false otherwise
	 */
	boolean addType(String dataSourceId, String containerId, URI type);

	/**
	 * Adds to the FacadeX model a value as slot of the container of a given data source.
	 * @param dataSourceId the identifier of the data source of the container
	 * @param containerId the identifier of the parent container
	 * @param slotKey the key of the slot of the containerId
	 * @param value the value of the container
	 * @return true if the value is added to the model, false otherwise
	 */
	boolean addValue(String dataSourceId, String containerId, String slotKey, Object value);

	/**
	 * Adds to the FacadeX model a value as slot of the container of a given data source. The relation between the parent and the value is identified by the customKey.
	 * @param dataSourceId the identifier of the data source of the container
	 * @param containerId the identifier of the parent container
	 * @param customKey the key of the slot of the containerId
	 * @param value the value of the container
	 * @return true if the value is added to the model, false otherwise
	 */
	boolean addValue(String dataSourceId, String containerId, URI customKey, Object value);

	/**
	 * Adds to the FacadeX model a value as slot of the container of a given data source.
	 * @param dataSourceId the identifier of the data source of the container
	 * @param containerId the identifier of the parent container
	 * @param slotKey the key of the slot of the containerId
	 * @param value the value of the container
	 * @return true if the value is added to the model, false otherwise
	 */
	boolean addValue(String dataSourceId, String containerId, Integer slotKey, Object value);

	/**
	 * Add the root container to the FacadeX model.
	 * @param dataSourceId the data source of the container
	 * @param rootId the identifier of the root container
	 * @return true if the root is added to the model, false otherwise
	 */
	boolean addRoot(String dataSourceId, String rootId);

}
