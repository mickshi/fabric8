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
package io.fabric8.process.manager.support;

import aQute.lib.osgi.Jar;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import io.fabric8.common.util.Filter;
import io.fabric8.fab.DependencyFilters;
import io.fabric8.fab.DependencyTreeResult;
import io.fabric8.fab.MavenResolverImpl;
import io.fabric8.process.manager.InstallOptions;
import io.fabric8.process.manager.config.ProcessConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.resolution.ArtifactResolutionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.jar.Attributes;

import static io.fabric8.common.util.Strings.join;

/**
 */
public class JarInstaller {

    private static final Logger LOG = LoggerFactory.getLogger(JarInstaller.class);

    MavenResolverImpl mavenResolver = new MavenResolverImpl();
    private final Executor executor;

    public JarInstaller(Executor executor) {
        this.executor = executor;
    }

    public void unpackJarProcess(ProcessConfig config, String id, File installDir, InstallOptions parameters) throws Exception {
        // lets unpack the launcher
        URL artifactUrl = parameters.getUrl();
        File libDir = new File(installDir, "lib");
        libDir.mkdirs();
        if (artifactUrl != null) {
            copyArtifactAndDependencies(config, id, installDir, parameters, libDir);
        }
        copyJarFiles(id, installDir, parameters, libDir);
    }

    protected void copyJarFiles(String id, File installDir, InstallOptions parameters, File libDir) throws IOException {
        Set<File> jarFiles = parameters.getJarFiles();
        if (jarFiles != null) {
            for (File file : jarFiles) {
                Files.copy(file, new File(libDir, file.getName()));
            }
        }
    }

    protected void copyArtifactAndDependencies(ProcessConfig config, String id, File installDir, InstallOptions parameters, File libDir) throws Exception {
        URL artifactUrl = parameters.getUrl();
        // now lets download the executable jar as main.jar and all its dependencies...
        Filter<Dependency> optionalFilter = DependencyFilters.parseExcludeOptionalFilter(join(Arrays.asList(parameters.getOptionalDependencyPatterns()), " "));
        Filter<Dependency> excludeFilter = DependencyFilters.parseExcludeFilter(join(Arrays.asList(parameters.getExcludeDependencyFilterPatterns()), " "), optionalFilter);
        DependencyTreeResult result = mavenResolver.collectDependenciesForJar(getArtifactFile(artifactUrl),
                parameters.isOffline(),
                excludeFilter);

        DependencyNode mainJarDependency = result.getRootNode();

        Artifact mainPomArtifact = mainJarDependency.getDependency().getArtifact();
        File mainJar = mavenResolver.resolveArtifact(parameters.isOffline(),
                mainPomArtifact.getGroupId(), mainPomArtifact.getArtifactId(),
                mainPomArtifact.getVersion(), mainPomArtifact.getClassifier(), "jar").
                getFile();
        if (mainJar == null) {
            System.out.println("Cannot find file for main jar " + mainJarDependency);
        } else {
            File newMain = new File(libDir, "main.jar");
            Files.copy(mainJar, newMain);
            String mainClass = parameters.getMainClass();
            if (mainClass != null) {
                setMainClass(config, installDir, newMain, id, mainClass);
            }
        }

        copyDependencies(mainJarDependency, libDir);
    }

    private File getArtifactFile(URL url) throws IOException {
        File tmpFile = File.createTempFile("artifact", ".jar");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tmpFile);
            Resources.copy(url, fos);
        } catch (Exception ex) {
            Throwables.propagate(ex);
        } finally {
            Closeables.closeQuietly(fos);
        }
        return tmpFile;
    }

    /**
     * Sets the executable class name in the given jar
     */
    protected void setMainClass(ProcessConfig config, File installDir, File jarFile, String id, String mainClass) throws Exception {
        File tmpFile = File.createTempFile("fuse-process-" + id, ".jar");
        Files.copy(jarFile, tmpFile);
        Jar jar = new Jar(tmpFile);
        Attributes attributes = jar.getManifest().getMainAttributes();
        attributes.putValue("Main-Class", mainClass);
        jar.write(jarFile);
    }

    protected void copyDependencies(DependencyNode dependency, File libDir) throws IOException, ArtifactResolutionException {
        List<DependencyNode> children = dependency.getChildren();
        if (children != null) {
            for (DependencyNode child : children) {
                if(child.getDependency().getScope().equals("provided")) {
                    LOG.debug("Dependency {} has scope provided. Not copying.", child.getDependency());
                    continue;
                }
                File file = getFile(child);
                if (file == null) {
                    System.out.println("Cannot find file for dependent jar " + child);
                } else {
                    Files.copy(file, new File(libDir, file.getName()));
                }
                copyDependencies(child, libDir);
            }
        }
    }

    protected File getFile(DependencyNode node) throws ArtifactResolutionException {
        if (node != null) {
            Dependency dependency = node.getDependency();
            if (dependency != null) {
                Artifact artifact = dependency.getArtifact();
                if (artifact != null) {
                    File file = artifact.getFile();
                    if (file == null) {
                        return mavenResolver.resolveFile(artifact);
                    }
                    return file;
                }
            }
        }
        return null;
    }

}
