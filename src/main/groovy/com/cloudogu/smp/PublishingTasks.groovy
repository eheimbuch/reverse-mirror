package com.cloudogu.smp

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.api.tasks.javadoc.Javadoc

import static com.cloudogu.smp.Dependencies.*

class PublishingTasks {

  static void configure(Project project, SmpExtension extension, PublishArtifact smp) {
    project.afterEvaluate {
      configurePublishing(project, extension, smp)
    }
  }

  private static void configurePublishing(Project project, SmpExtension extension, PublishArtifact smp) {
    project.java {
      withJavadocJar()
      withSourcesJar()
    }

    project.tasks.withType(Javadoc) {
      failOnError false
    }

    project.publishing {
      publications {
        mavenJava(MavenPublication) {
          groupId = extension.group
          artifactId = extension.getName(project)
          version = project.version

          from project.components.java
          artifact smp

          pom {
            packaging = "smp"
            description = extension.description
          }

          pom.withXml {
            def rootNode = asNode()
            rootNode.remove(rootNode.get('dependencies'))
            def dependenciesNode = rootNode.appendNode('dependencies')

            Set<Dependency> runtime = runtimeDependencies(project)

            def provided = project.configurations.scmCoreDependency.allDependencies
              .findAll { dep ->
                return !(dep.group.equals("sonia.scm") && dep.name.equals("scm"))
              }

            appendDependencies(dependenciesNode, provided, 'provided')
            appendDependencies(dependenciesNode, runtime)
            appendDependencies(dependenciesNode, project.configurations.plugin.dependencies)
            appendDependencies(dependenciesNode, project.configurations.optionalPlugin.dependencies, null, true)
          }
        }
      }
      repositories {
        maven {
          def releasesRepoUrl = "https://packages.scm-manager.org/repository/plugin-releases/"
          def snapshotsRepoUrl = "https://packages.scm-manager.org/repository/plugin-snapshots/"
          url = project.version.endsWith('SNAPSHOT') ? snapshotsRepoUrl : releasesRepoUrl
          if (project.hasProperty("packagesScmManagerUsername") && project.hasProperty("packagesScmManagerPassword")) {
            credentials {
              username project.property("packagesScmManagerUsername")
              password project.property("packagesScmManagerPassword")
            }
            authentication {
              basic(BasicAuthentication)
            }
          }
        }
      }
    }
  }

}
