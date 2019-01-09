/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */
package sonia.scm.script.infrastructure;

import com.google.common.base.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.TypeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, TempDirectory.class})
class InitScriptCollectorTest {

  @Mock
  private TypeRepository typeRepository;

  private InitScriptCollector collector;

  private Path directory;

  @BeforeEach
  void prepare(@TempDirectory.TempDir Path directory) {
    this.directory = directory;
    collector = new InitScriptCollector(typeRepository, directory);
  }

  @Test
  void shouldCollectAndSortTheScripts() throws IOException {
    when(typeRepository.findByExtension("groovy")).thenReturn(Optional.of("Groovy"));

    writeGroovyScript("020");
    writeGroovyScript("040");
    writeGroovyScript("010");
    writeGroovyScript("030");

    List<Script> scripts = collector.collect();
    assertThat(scripts).hasSize(4);

    assertThat(scripts.get(0).getContent()).isEqualTo("010");
    assertThat(scripts.get(1).getContent()  ).isEqualTo("020");
    assertThat(scripts.get(2).getContent()).isEqualTo("030");
    assertThat(scripts.get(3).getContent()).isEqualTo("040");
  }

  @Test
  void shouldIgnoreUnknownScriptTypes() throws IOException {
    when(typeRepository.findByExtension("groovy")).thenReturn(Optional.of("Groovy"));

    writeGroovyScript("020");
    writeScript("020", "hitchhikerScript");

    List<Script> scripts = collector.collect();
    assertThat(scripts).hasSize(1);

    assertThat(scripts.get(0).getContent()).isEqualTo("020");
  }

  private void writeGroovyScript(String name) throws IOException {
    writeScript(name, "groovy");
  }

  private void writeScript(String name, String extension) throws IOException {
    Path script = directory.resolve(name + "-hello." + extension);
    Files.write(script, name.getBytes(Charsets.UTF_8));
  }

}
