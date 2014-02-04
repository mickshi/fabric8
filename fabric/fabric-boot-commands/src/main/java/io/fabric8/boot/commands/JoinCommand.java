/**
 * Copyright (C) FuseSource, Inc.
 * http://fusesource.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.boot.commands;

import io.fabric8.api.scr.ValidatingReference;
import io.fabric8.boot.commands.service.JoinAvailable;

import java.util.Map;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.Function;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

@Command(name = JoinCommand.FUNCTION_VALUE, scope = JoinCommand.SCOPE_VALUE, description = CreateCommand.DESCRIPTION, detailedDescription = "classpath:join.txt")
@Component(immediate = true, policy = ConfigurationPolicy.OPTIONAL)
@Service({ Function.class, JoinAvailable.class })
@org.apache.felix.scr.annotations.Properties({
        @Property(name = "osgi.command.scope", value = JoinCommand.SCOPE_VALUE),
        @Property(name = "osgi.command.function", value = JoinCommand.FUNCTION_VALUE)
})
public class JoinCommand extends AbstractCommand implements JoinAvailable {

    public static final String SCOPE_VALUE = "fabric";
    public static final String FUNCTION_VALUE =  "join";
    public static final String DESCRIPTION = "Join a container to an existing fabric";

    @Reference(referenceInterface = ConfigurationAdmin.class, bind = "bindConfigAdmin", unbind = "unbindConfigAdmin")
    private final ValidatingReference<ConfigurationAdmin> configAdmin = new ValidatingReference<ConfigurationAdmin>();
    private BundleContext bundleContext;

    @Activate
    void activate(BundleContext bundleContext, Map<String, ?> props) {
        this.bundleContext = bundleContext;
    }

    @Deactivate
    void deactivate() {
    }

    @Override
    public Action createNewAction() {
        return new JoinAction(bundleContext, configAdmin.get());
    }

    void bindConfigAdmin(ConfigurationAdmin service) {
        this.configAdmin.bind(service);
    }

    void unbindConfigAdmin(ConfigurationAdmin service) {
        this.configAdmin.unbind(service);
    }
}