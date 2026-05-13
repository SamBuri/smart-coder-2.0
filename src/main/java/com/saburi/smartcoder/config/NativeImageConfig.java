package com.saburi.smartcoder.config;

import com.saburi.smartcoder.project.ProjectMini;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.projection.TargetAware;
import org.springframework.aop.SpringProxy;
import org.springframework.core.DecoratingProxy;

/**
 * Native image configuration to register hints for GraalVM.
 */
@Configuration
@ImportRuntimeHints(NativeImageConfig.ProjectMiniHints.class)
public class NativeImageConfig {

    /**
     * Registers dynamic proxies for Spring Data projections that are not automatically detected.
     */
    static class ProjectMiniHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // The order of interfaces MUST match what GraalVM expects:
            // [ProjectionInterface, TargetAware, SpringProxy, DecoratingProxy]
            
            // Register proxy for the nested ProjectMini$Parent interface
            hints.proxies().registerJdkProxy(
                ProjectMini.Parent.class,
                TargetAware.class,
                SpringProxy.class,
                DecoratingProxy.class
            );

            // Register proxy for the main ProjectMini interface
            hints.proxies().registerJdkProxy(
                ProjectMini.class,
                TargetAware.class,
                SpringProxy.class,
                DecoratingProxy.class
            );
        }
    }
}
