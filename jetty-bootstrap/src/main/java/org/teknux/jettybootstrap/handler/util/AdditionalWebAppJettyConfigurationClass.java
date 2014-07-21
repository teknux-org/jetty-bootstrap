/*******************************************************************************
 * (C) Copyright 2014 Teknux.org (http://teknux.org/).
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
 * Contributors:
 *      "Pierre PINON"
 *      "Francois EYL"
 *      "Laurent MARCHAL"
 *  
 *******************************************************************************/

package org.teknux.jettybootstrap.handler.util;

import java.util.Arrays;
import java.util.List;

public class AdditionalWebAppJettyConfigurationClass {
    
	public enum Position {
		BEFORE,
		AFTER
	}
		
	private static final Position DEFAULT_POSITION = Position.AFTER; 
	
	private List<String> classes = null;
	private Position position = null;
	private String referenceClass = null;
	
	public AdditionalWebAppJettyConfigurationClass(String... classes) {
		this(null, DEFAULT_POSITION, classes);
	}
	
	public AdditionalWebAppJettyConfigurationClass(Position position, String... classes) {
		this(null, position, classes);
	}
	
	public AdditionalWebAppJettyConfigurationClass(String referenceClass, Position position, String... classes) {
		this.classes = Arrays.asList(classes);
		this.position = position;
		this.referenceClass = referenceClass;
	}

	public List<String> getClasses() {
		return classes;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public String getReferenceClass() {
		return referenceClass;
	}

	public void setReferenceClass(String referenceClass) {
		this.referenceClass = referenceClass;
	}
}
