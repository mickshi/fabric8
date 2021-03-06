/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.git;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.groups.NodeState;

public class GitNode extends NodeState {

    @JsonProperty
    String[] services;

    public GitNode() {
        super();
    }

    public GitNode(String id) {
        super(id);
        this.services = services;
    }

    public String getUrl() {
        return services != null && services.length >= 1 ? services[0] : null;
    }

    public void setUrl(String url) {
        this.services = new String[] {url};
    }

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    @Override
	public String toString() {
		return "GitNode{" +
				"id='" + id + '\'' +
				", url='" + getUrl() + '\'' +
				'}';
	}
}
