/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.internal.component.external.model;

import com.google.common.collect.ImmutableList;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.parser.GradlePomModuleDescriptorBuilder;
import org.gradle.internal.component.external.descriptor.Configuration;
import org.gradle.internal.component.model.DefaultIvyArtifactName;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

import static org.gradle.internal.component.external.model.DefaultMavenModuleResolveMetadata.JAR_PACKAGINGS;
import static org.gradle.internal.component.external.model.DefaultMavenModuleResolveMetadata.POM_PACKAGING;

public class DefaultMutableMavenModuleResolveMetadata extends AbstractMutableModuleComponentResolveMetadata<MavenConfigurationMetadata> implements MutableMavenModuleResolveMetadata {
    private String packaging = "jar";
    private boolean relocated;
    private String snapshotTimestamp;

    /**
     * Creates default metadata for a Maven module with no POM.
     */
    public static DefaultMutableMavenModuleResolveMetadata missing(ModuleVersionIdentifier id, ModuleComponentIdentifier componentIdentifier) {
        DefaultMutableMavenModuleResolveMetadata metadata = new DefaultMutableMavenModuleResolveMetadata(id, componentIdentifier);
        metadata.setMissing(true);
        return metadata;
    }

    public DefaultMutableMavenModuleResolveMetadata(ModuleVersionIdentifier id, ModuleComponentIdentifier componentIdentifier) {
        this(id, componentIdentifier, ImmutableList.<ModuleDependencyMetadata>of());
    }

    public DefaultMutableMavenModuleResolveMetadata(ModuleVersionIdentifier id, ModuleComponentIdentifier componentIdentifier, Collection<? extends ModuleDependencyMetadata> dependencies) {
        super(id, componentIdentifier, ImmutableList.copyOf(dependencies));
    }

    DefaultMutableMavenModuleResolveMetadata(MavenModuleResolveMetadata metadata) {
        super(metadata);
        this.packaging = metadata.getPackaging();
        this.relocated = metadata.isRelocated();
        this.snapshotTimestamp = metadata.getSnapshotTimestamp();
    }

    @Override
    protected MavenConfigurationMetadata createConfiguration(ModuleComponentIdentifier componentId, String name, boolean transitive, boolean visible, ImmutableList<MavenConfigurationMetadata> parents, ImmutableList<? extends ModuleComponentArtifactMetadata> artifactOverrides) {
        ImmutableList<? extends ModuleComponentArtifactMetadata> artifacts;
        if (artifactOverrides != null) {
            artifacts = artifactOverrides;
        } else {
            if (name.equals("compile") || name.equals("runtime") || name.equals("default") || name.equals("test")) {
                artifacts = ImmutableList.of(new DefaultModuleComponentArtifactMetadata(getComponentId(), new DefaultIvyArtifactName(getComponentId().getModule(), "jar", "jar")));
            } else {
                artifacts = ImmutableList.of();
            }
        }
        return new MavenConfigurationMetadata(componentId, name, transitive, visible, parents, artifacts);
    }

    @Override
    public MavenModuleResolveMetadata asImmutable() {
        return new DefaultMavenModuleResolveMetadata(this);
    }

    @Override
    protected Map<String, Configuration> getConfigurationDefinitions() {
        return GradlePomModuleDescriptorBuilder.MAVEN2_CONFIGURATIONS;
    }

    @Nullable
    @Override
    public String getSnapshotTimestamp() {
        return snapshotTimestamp;
    }

    @Override
    public void setSnapshotTimestamp(@Nullable String snapshotTimestamp) {
        this.snapshotTimestamp = snapshotTimestamp;
    }

    @Override
    public boolean isRelocated() {
        return relocated;
    }

    @Override
    public void setRelocated(boolean relocated) {
        this.relocated = relocated;
    }

    @Override
    public String getPackaging() {
        return packaging;
    }

    @Override
    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    @Override
    public boolean isPomPackaging() {
        return POM_PACKAGING.equals(packaging);
    }

    @Override
    public boolean isKnownJarPackaging() {
        return JAR_PACKAGINGS.contains(packaging);
    }

}
