package com.cloudogu.smp

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.nio.file.Path
import groovy.yaml.YamlSlurper

import static org.assertj.core.api.Assertions.*

class ReleaseYamlTaskTest {

  @Test
  void shouldWriteDescriptorFile(@TempDir Path temp) throws IOException {
    def smp = temp.resolve("scm-sample-plugin.smp").toFile()
    smp << "a"
    def releaseYaml = temp.resolve("release.yml").toFile()
    def extension = new SmpExtension()
    extension.setName("scm-sample-plugin")
    extension.setVersion("2.1.0")
    extension.setCategory("Sample")
    extension.setScmVersion("2.8.0")
    extension.conditions {
      os = "Linux"
      arch = "arm"
    }
    extension.depends {
      on 'sonia.scm.plugins:scm-mail-plugin:2.1.0'
      optional 'sonia.scm.plugins:scm-editor-plugin:2.0.0'
      optional 'sonia.scm.plugins:scm-landingpage-plugin:1.0.0'
    }

    Project project = ProjectBuilder.builder().build()
    def task = project.task("release-yaml", type: ReleaseYamlTask) {
      it.extension = extension
      it.smp = smp
      it.releaseYaml = releaseYaml
    }
    task.write()

    YamlSlurper slurper = new YamlSlurper()
    def release = slurper.parse(releaseYaml)

    assertThat(release.plugin).isEqualTo("scm-sample-plugin")
    assertThat(release.tag).isEqualTo("2.1.0")
    assertThat(release.date).isNotEmpty()
    assertThat(release.checksum).isEqualTo("ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb")
    assertThat(release.url).isEqualTo("https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-sample-plugin/2.1.0/scm-sample-plugin-2.1.0.smp")
    assertThat(release.dependencies).containsOnly("scm-mail-plugin")
    assertThat(release.optionalDependencies).containsOnly("scm-editor-plugin", "scm-landingpage-plugin")
    assertThat(release.conditions.minVersion).isEqualTo("2.8.0")
    assertThat(release.conditions.os).isEqualTo("Linux")
    assertThat(release.conditions.arch).isEqualTo("arm")
  }
}