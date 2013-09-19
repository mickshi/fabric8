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
package org.fusesource.fabric.api;

/**
 * An optional interface that a {@link org.fusesource.fabric.api.ContainerProvider} can
 * implement to indicate it is capable of creating an instance of
 */
public interface ContainerAutoScalerFactory {

    /**
     * Returns a newly created {@link org.fusesource.fabric.api.ContainerAutoScaler} or null
     * if there is insufficient configuration information available to create an auto-scaler
     */
    ContainerAutoScaler createAutoScaler();
}