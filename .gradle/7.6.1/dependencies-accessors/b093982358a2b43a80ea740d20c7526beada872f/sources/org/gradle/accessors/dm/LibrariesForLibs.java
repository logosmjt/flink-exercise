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
    private final AvroLibraryAccessors laccForAvroLibraryAccessors = new AvroLibraryAccessors(owner);
    private final Log4jLibraryAccessors laccForLog4jLibraryAccessors = new Log4jLibraryAccessors(owner);
    private final Slf4jLibraryAccessors laccForSlf4jLibraryAccessors = new Slf4jLibraryAccessors(owner);
    private final StreamingLibraryAccessors laccForStreamingLibraryAccessors = new StreamingLibraryAccessors(owner);
    private final TableLibraryAccessors laccForTableLibraryAccessors = new TableLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Returns the group of libraries at avro
     */
    public AvroLibraryAccessors getAvro() { return laccForAvroLibraryAccessors; }

    /**
     * Returns the group of libraries at log4j
     */
    public Log4jLibraryAccessors getLog4j() { return laccForLog4jLibraryAccessors; }

    /**
     * Returns the group of libraries at slf4j
     */
    public Slf4jLibraryAccessors getSlf4j() { return laccForSlf4jLibraryAccessors; }

    /**
     * Returns the group of libraries at streaming
     */
    public StreamingLibraryAccessors getStreaming() { return laccForStreamingLibraryAccessors; }

    /**
     * Returns the group of libraries at table
     */
    public TableLibraryAccessors getTable() { return laccForTableLibraryAccessors; }

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

    public static class AvroLibraryAccessors extends SubDependencyFactory {
        private final AvroConfluentLibraryAccessors laccForAvroConfluentLibraryAccessors = new AvroConfluentLibraryAccessors(owner);

        public AvroLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at avro.confluent
         */
        public AvroConfluentLibraryAccessors getConfluent() { return laccForAvroConfluentLibraryAccessors; }

    }

    public static class AvroConfluentLibraryAccessors extends SubDependencyFactory {

        public AvroConfluentLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for registry (org.apache.flink:flink-avro-confluent-registry)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRegistry() { return create("avro.confluent.registry"); }

    }

    public static class Log4jLibraryAccessors extends SubDependencyFactory {
        private final Log4jSlf4jLibraryAccessors laccForLog4jSlf4jLibraryAccessors = new Log4jSlf4jLibraryAccessors(owner);

        public Log4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for api (org.apache.logging.log4j:log4j-api)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getApi() { return create("log4j.api"); }

            /**
             * Creates a dependency provider for core (org.apache.logging.log4j:log4j-core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("log4j.core"); }

        /**
         * Returns the group of libraries at log4j.slf4j
         */
        public Log4jSlf4jLibraryAccessors getSlf4j() { return laccForLog4jSlf4jLibraryAccessors; }

    }

    public static class Log4jSlf4jLibraryAccessors extends SubDependencyFactory {

        public Log4jSlf4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for impl (org.apache.logging.log4j:log4j-slf4j-impl)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getImpl() { return create("log4j.slf4j.impl"); }

    }

    public static class Slf4jLibraryAccessors extends SubDependencyFactory {

        public Slf4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for log4j12 (org.slf4j:slf4j-log4j12)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getLog4j12() { return create("slf4j.log4j12"); }

    }

    public static class StreamingLibraryAccessors extends SubDependencyFactory {

        public StreamingLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for scala (org.apache.flink:streaming-scala_2.12)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getScala() { return create("streaming.scala"); }

    }

    public static class TableLibraryAccessors extends SubDependencyFactory {
        private final TableApiLibraryAccessors laccForTableApiLibraryAccessors = new TableApiLibraryAccessors(owner);

        public TableLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at table.api
         */
        public TableApiLibraryAccessors getApi() { return laccForTableApiLibraryAccessors; }

    }

    public static class TableApiLibraryAccessors extends SubDependencyFactory {
        private final TableApiJavaLibraryAccessors laccForTableApiJavaLibraryAccessors = new TableApiJavaLibraryAccessors(owner);
        private final TableApiPlannerLibraryAccessors laccForTableApiPlannerLibraryAccessors = new TableApiPlannerLibraryAccessors(owner);

        public TableApiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at table.api.java
         */
        public TableApiJavaLibraryAccessors getJava() { return laccForTableApiJavaLibraryAccessors; }

        /**
         * Returns the group of libraries at table.api.planner
         */
        public TableApiPlannerLibraryAccessors getPlanner() { return laccForTableApiPlannerLibraryAccessors; }

    }

    public static class TableApiJavaLibraryAccessors extends SubDependencyFactory {

        public TableApiJavaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for bridge (org.apache.flink:flink-table-api-java-bridge_2.12)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getBridge() { return create("table.api.java.bridge"); }

    }

    public static class TableApiPlannerLibraryAccessors extends SubDependencyFactory {

        public TableApiPlannerLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for blink (org.apache.flink:flink-table-api-planner-blink_2.12)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getBlink() { return create("table.api.planner.blink"); }

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

            /**
             * Returns the version associated to this alias: slf4j (1.7.15)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSlf4j() { return getVersion("slf4j"); }

    }

    public static class BundleAccessors extends BundleFactory {
        private final FlinkBundleAccessors baccForFlinkBundleAccessors = new FlinkBundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
        private final Log4jBundleAccessors baccForLog4jBundleAccessors = new Log4jBundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

        /**
         * Returns the group of bundles at bundles.flink
         */
        public FlinkBundleAccessors getFlink() { return baccForFlinkBundleAccessors; }

        /**
         * Returns the group of bundles at bundles.log4j
         */
        public Log4jBundleAccessors getLog4j() { return baccForLog4jBundleAccessors; }

    }

    public static class FlinkBundleAccessors extends BundleFactory {

        public FlinkBundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

            /**
             * Creates a dependency bundle provider for flink.bundles which is an aggregate for the following dependencies:
             * <ul>
             *    <li>org.apache.flink:flink-avro-confluent-registry</li>
             *    <li>org.apache.flink:flink-table-api-java-bridge_2.12</li>
             *    <li>org.apache.flink:flink-table-api-planner-blink_2.12</li>
             *    <li>org.apache.flink:streaming-scala_2.12</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getBundles() { return createBundle("flink.bundles"); }

    }

    public static class Log4jBundleAccessors extends BundleFactory {

        public Log4jBundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

            /**
             * Creates a dependency bundle provider for log4j.bundles which is an aggregate for the following dependencies:
             * <ul>
             *    <li>org.apache.logging.log4j:log4j-api</li>
             *    <li>org.apache.logging.log4j:log4j-core</li>
             *    <li>org.apache.logging.log4j:log4j-slf4j-impl</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getBundles() { return createBundle("log4j.bundles"); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
