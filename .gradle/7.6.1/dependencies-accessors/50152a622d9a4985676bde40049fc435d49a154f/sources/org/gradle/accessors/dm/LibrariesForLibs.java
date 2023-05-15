package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
*/
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final FlinkLibraryAccessors laccForFlinkLibraryAccessors = new FlinkLibraryAccessors(owner);
    private final GoogleLibraryAccessors laccForGoogleLibraryAccessors = new GoogleLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Returns the group of libraries at flink
     */
    public FlinkLibraryAccessors getFlink() { return laccForFlinkLibraryAccessors; }

    /**
     * Returns the group of libraries at google
     */
    public GoogleLibraryAccessors getGoogle() { return laccForGoogleLibraryAccessors; }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() { return vaccForVersionAccessors; }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() { return baccForBundleAccessors; }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() { return paccForPluginAccessors; }

    public static class FlinkLibraryAccessors extends SubDependencyFactory {
        private final FlinkConnectorLibraryAccessors laccForFlinkConnectorLibraryAccessors = new FlinkConnectorLibraryAccessors(owner);
        private final FlinkStreamingLibraryAccessors laccForFlinkStreamingLibraryAccessors = new FlinkStreamingLibraryAccessors(owner);
        private final FlinkTableLibraryAccessors laccForFlinkTableLibraryAccessors = new FlinkTableLibraryAccessors(owner);

        public FlinkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for json (org.apache.flink:flink-json)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJson() { return create("flink.json"); }

        /**
         * Returns the group of libraries at flink.connector
         */
        public FlinkConnectorLibraryAccessors getConnector() { return laccForFlinkConnectorLibraryAccessors; }

        /**
         * Returns the group of libraries at flink.streaming
         */
        public FlinkStreamingLibraryAccessors getStreaming() { return laccForFlinkStreamingLibraryAccessors; }

        /**
         * Returns the group of libraries at flink.table
         */
        public FlinkTableLibraryAccessors getTable() { return laccForFlinkTableLibraryAccessors; }

    }

    public static class FlinkConnectorLibraryAccessors extends SubDependencyFactory {

        public FlinkConnectorLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for files (org.apache.flink:flink-connector-files)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getFiles() { return create("flink.connector.files"); }

    }

    public static class FlinkStreamingLibraryAccessors extends SubDependencyFactory {

        public FlinkStreamingLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for java (org.apache.flink:flink-streaming-java)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJava() { return create("flink.streaming.java"); }

    }

    public static class FlinkTableLibraryAccessors extends SubDependencyFactory {
        private final FlinkTableApiLibraryAccessors laccForFlinkTableApiLibraryAccessors = new FlinkTableApiLibraryAccessors(owner);

        public FlinkTableLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at flink.table.api
         */
        public FlinkTableApiLibraryAccessors getApi() { return laccForFlinkTableApiLibraryAccessors; }

    }

    public static class FlinkTableApiLibraryAccessors extends SubDependencyFactory {
        private final FlinkTableApiJavaLibraryAccessors laccForFlinkTableApiJavaLibraryAccessors = new FlinkTableApiJavaLibraryAccessors(owner);

        public FlinkTableApiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at flink.table.api.java
         */
        public FlinkTableApiJavaLibraryAccessors getJava() { return laccForFlinkTableApiJavaLibraryAccessors; }

    }

    public static class FlinkTableApiJavaLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public FlinkTableApiJavaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for java (org.apache.flink:flink-table-api-java)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("flink.table.api.java"); }

            /**
             * Creates a dependency provider for bridge (org.apache.flink:flink-table-api-java-bridge)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getBridge() { return create("flink.table.api.java.bridge"); }

    }

    public static class GoogleLibraryAccessors extends SubDependencyFactory {

        public GoogleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for gson (com.google.code.gson:gson)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getGson() { return create("google.gson"); }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: flink (1.17.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getFlink() { return getVersion("flink"); }

            /**
             * Returns the version associated to this alias: gson (2.8.9)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getGson() { return getVersion("gson"); }

            /**
             * Returns the version associated to this alias: log4j (2.12.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLog4j() { return getVersion("log4j"); }

            /**
             * Returns the version associated to this alias: scala (2.12)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getScala() { return getVersion("scala"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

            /**
             * Creates a dependency bundle provider for flinks which is an aggregate for the following dependencies:
             * <ul>
             *    <li>org.apache.flink:flink-streaming-java</li>
             *    <li>org.apache.flink:flink-table-api-java</li>
             *    <li>org.apache.flink:flink-table-api-java-bridge</li>
             *    <li>org.apache.flink:flink-connector-files</li>
             *    <li>org.apache.flink:flink-json</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getFlinks() { return createBundle("flinks"); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
