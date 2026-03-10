package com.margot.word_map.service.server;

import com.margot.word_map.dto.request.CreateServerRequest;
import com.margot.word_map.dto.request.UpdateServerRequest;
import com.margot.word_map.exception.DuplicateServerException;
import com.margot.word_map.exception.LanguageNotFoundException;
import com.margot.word_map.exception.PlatformNotFoundException;
import com.margot.word_map.exception.ServerNotFoundException;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.Platform;
import com.margot.word_map.model.Server;
import com.margot.word_map.repository.ServerRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.language.LanguageService;
import com.margot.word_map.service.platform.PlatformService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServerServiceTest {

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private LanguageService languageService;

    @Mock
    private AuditService auditService;

    @Mock
    private PlatformService platformService;

    @InjectMocks
    private ServerService serverService;

    @Test
    public void testCreateServerSaves() {
        Long id = 1L;
        CreateServerRequest request = CreateServerRequest
                .builder()
                .language(1L)
                .platform(1L)
                .build();
        when(platformService.findById(request.getPlatform())).thenReturn(Optional.of(new Platform()));
        when(languageService.findById(request.getLanguage())).thenReturn(Optional.of(new Language()));
        when(serverRepository.save(any(Server.class))).thenAnswer(i -> {
            Server s = i.getArgument(0);
            s.setId(1L);
            return s;
        });

        serverService.createServer(request);
        verify(serverRepository).save(any(Server.class));
        verify(auditService).log(eq(AuditActionType.SERVER_CREATED), eq(id));
    }

    @Test
    public void testCreateServerThrowsPlatformNotFound() {
        CreateServerRequest request = CreateServerRequest
                .builder()
                .language(1L)
                .platform(1L)
                .build();

        when(platformService.findById(request.getPlatform())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serverService
                .createServer(request))
                .isInstanceOf(PlatformNotFoundException.class)
                .hasMessageContaining("Платформа не найдена");
    }

    @Test
    public void testCreateServerThrowsLanguageNotFound() {
        CreateServerRequest request = CreateServerRequest
                .builder()
                .language(1L)
                .platform(1L)
                .build();

        when(platformService.findById(request.getPlatform())).thenReturn(Optional.of(new Platform()));
        when(languageService.findById(request.getLanguage())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serverService
                .createServer(request))
                .isInstanceOf(LanguageNotFoundException.class)
                .hasMessageContaining("Нет языка с таким id");
    }

    @Test
    public void testCreateServerThrowsServerExists() {
        CreateServerRequest request = CreateServerRequest
                .builder()
                .language(1L)
                .platform(1L)
                .build();

        when (serverRepository
                .existsByPlatformIdAndLanguageIdAndIsOpenTrue(request.getPlatform(), request.getLanguage()))
                .thenReturn(true);
        when(platformService.findById(request.getPlatform())).thenReturn(Optional.of(new Platform()));
        when(languageService.findById(request.getLanguage())).thenReturn(Optional.of(new Language()));

        assertThatThrownBy(() -> serverService
                .createServer(request))
                .isInstanceOf(DuplicateServerException.class)
                .hasMessageContaining("Сервер уже существует");
    }

    @Test
    public void testUpdateServerUpdates() {
        Long id = 1L;
        String oldName = "old";
        String newName = "new";
        UpdateServerRequest request = new UpdateServerRequest(newName);
        Server server = Server.builder().id(id).name(oldName).build();

        when(serverRepository.findById(id)).thenReturn(Optional.of(server));

        serverService.updateServerName(request, id);
        assertThat(server.getName()).isEqualTo(newName);

        verify(serverRepository).save(server);
    }

    @Test
    public void testUpdateServerThrowsServerNotFound() {
        Long id = 1L;
        UpdateServerRequest request = new UpdateServerRequest();

        when(serverRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serverService
                .updateServerName(request, id))
                .isInstanceOf(ServerNotFoundException.class)
                .hasMessageContaining("Сервер не найден");
    }
}
