/**
 * EasySOA Registry
 * Copyright 2011-2013 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.subproject;

/**
 * For now only to allow easy parsing of subprojectId Strings,
 * LATER maybe use it everywhere in SubprojectServiceImpl
 * @author mdutoo
 *
 */
public class SubprojectId {
	
	private String projectName;
	private String subprojectName;
	private String version;

	public SubprojectId(String projectName, String subprojectName, String version) {
		this.projectName = projectName;
		this.subprojectName = subprojectName;
		this.version = version;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getSubprojectName() {
		return subprojectName;
	}

	public String getVersion() {
		return version;
	}

}
