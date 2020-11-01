package com.cloudogu.smp

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

class SmpExtension implements Serializable {

  @Input
  String scmVersion = "2.0.0"

  @Input
  String group = "sonia.scm.plugins"

  @Input
  @Optional
  String name

  @Input
  String version

  @Input
  @Optional
  String displayName

  @Input
  @Optional
  String description

  @Input
  @Optional
  String author

  @Input
  @Optional
  String category

  @Input
  @Optional
  String home

  @Nested
  Conditions pluginConditions = new Conditions()

  @Nested
  OpenApiSpec openApiSpec = new OpenApiSpec()

  private List<String> dependencies = new ArrayList<>()
  private List<String> optionalDependencies = new ArrayList<>()

  @Input
  List<String> getDependencies() {
    return dependencies
  }

  @Input
  List<String> getOptionalDependencies() {
    return optionalDependencies
  }

  def conditions(Closure<Void> closure) {
    closure.delegate = pluginConditions
    closure.call()
  }

  def depends(Closure<Void> closure) {
    closure.delegate = new DependencyConfigurator(dependencies, optionalDependencies)
    closure.call()
  }

  def openapi(Closure<Void> closure) {
    closure.delegate = openApiSpec
    closure.call()
  }

  File getScmHome(Project project) {
    if (home != null) {
      return new File(home)
    }
    return new File(project.buildDir, "scm-home")
  }

  String getName(Project project) {
    if (name != null) {
      return name
    }
    return project.name
  }

  class Conditions {
    @Input
    @Optional
    String os

    @Input
    @Optional
    String arch
  }

  class OpenApiSpec {

    @Input
    @Optional
    Set<String> packages = new LinkedHashSet<>()

  }

  private class DependencyConfigurator {

    private List<String> dependencies
    private List<String> optionalDependencies

    DependencyConfigurator(List<String> dependencies, List<String> optionalDependencies) {
      this.dependencies = dependencies
      this.optionalDependencies = optionalDependencies
    }

    def on(String dependency) {
      dependencies.add(dependency);
    }

    def optional(String dependency) {
      optionalDependencies.add(dependency);
    }
  }

}
