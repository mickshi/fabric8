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
package io.fabric8.container.process;

import io.fabric8.api.CreateChildContainerMetadata;
import io.fabric8.api.CreateChildContainerOptions;
import io.fabric8.api.FabricService;
import io.fabric8.api.Profile;
import io.fabric8.api.Profiles;
import io.fabric8.api.scr.support.Strings;
import io.fabric8.common.util.Objects;
import io.fabric8.process.manager.InstallOptions;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the configuration of a {@link io.fabric8.process.manager.ProcessManager} based child container
 */
@Component(name = "io.fabric8.container.process", label = "Fabric8 Process Child Container Configuration", immediate = false, metatype = true)
public class ProcessContainerConfig {
    @Property(label = "Process name", description = "The descriptive name to refer to this process when listing the processes on this machine.")
    private String processName;

    @Property(label = "Distribution URL", cardinality = 1,
            description = "The URL (usually using maven coordinates) for the distribution to download and unpack.")
    private String url;

    @Property(name = "controllerPath", label = "Controller JSON Path", value = "controller.json",
            description = "The name of the JSON file in the Profile which is used to control the distribution; starting and stopping the process.")
    private String controllerPath = "controller.json";

    @Property(name = "overlayFolder", label = "Overlay folder path", value = "overlayFiles",
            description = "The folder path inside the profile used to contain files and MVEL templates which are then overlayed ontop of the process installation; for customizing the configuration of the process with configuration files maintained inside the profile; possibly with dynamically resolved values.")
    private String overlayFolder;
    @Property(name = "disableDynamicPorts", label = "Disable dynamic port resolving", cardinality = Integer.MAX_VALUE,
            description = "The list of port names which should not be dynamically resolved.")
    private String[] disableDynamicPorts;
    @Property(name = "createLocalContainerAddress", label = "Create Local Container Address",
            description = "Whether or not a local address such as 127.0.0.1, 127.0.0.2 should be created for each container instance; so it can create its own custom network interfaces while sharing the same ports (such as for creating local Cassandra clusters).")
    private boolean createLocalContainerAddress;


    public InstallOptions createProcessInstallOptions(FabricService fabricService, CreateChildContainerMetadata metadata, CreateChildContainerOptions options, Map<String, String> environmentVariables) throws MalformedURLException {
        byte[] jsonData = null;
        Set<String> profileIds = options.getProfiles();
        String versionId = options.getVersion();
        List<Profile> profiles = Profiles.getProfiles(fabricService, profileIds, versionId);
        for (Profile profile : profiles) {
            jsonData = profile.getOverlay().getFileConfiguration(controllerPath);
            if (jsonData != null) {
                break;
            }
        }
        Objects.notNull(jsonData, "No JSON file found for path " + controllerPath + " in profiles: " + profileIds + " version: " + versionId);
        String controllerJson = new String(jsonData);
        String installName = getProcessName();
        if (Strings.isNullOrBlank(installName)) {
            if (profiles.size() > 0) {
                installName = profiles.get(0).getId();
            }
        }
        metadata.setContainerType(installName);
        return InstallOptions.builder().id(options.getName()).name(installName).url(url).controllerJson(controllerJson).environment(environmentVariables).build();
    }

    /**
     * Returns the set of port names which should not have dynamic ports allocated
     */
    public Set<String>  getDisableDynamicPortSet() {
        Set<String> answer = new HashSet<String>();
        if (disableDynamicPorts != null){
            answer.addAll(Arrays.asList(disableDynamicPorts));
        }
        return answer;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }

    public String getOverlayFolder() {
        return overlayFolder;
    }

    public void setOverlayFolder(String overlayFolder) {
        this.overlayFolder = overlayFolder;
    }

    public String[] getDisableDynamicPorts() {
        return disableDynamicPorts;
    }

    public void setDisableDynamicPorts(String[] disableDynamicPorts) {
        this.disableDynamicPorts = disableDynamicPorts;
    }

    public boolean isCreateLocalContainerAddress() {
        return createLocalContainerAddress;
    }

    public void setCreateLocalContainerAddress(boolean createLocalContainerAddress) {
        this.createLocalContainerAddress = createLocalContainerAddress;
    }
}
