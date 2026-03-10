package com.margot.word_map.service.server;

import com.margot.word_map.dto.request.CreateServerRequest;
import com.margot.word_map.dto.request.UpdateServerRequest;
import com.margot.word_map.dto.response.DeleteServerResponse;
import com.margot.word_map.exception.*;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.Platform;
import com.margot.word_map.model.Server;
import com.margot.word_map.repository.ServerRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.language.LanguageService;
import com.margot.word_map.service.map.GridService;
import com.margot.word_map.service.platform.PlatformService;
import com.margot.word_map.service.player.PlayerService;
import com.margot.word_map.service.word.WordService;
import org.apache.commons.codec.language.bm.Lang;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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
    private GridService gridService;

    @Mock
    private PlayerService playerService;

    @Mock
    private WordService wordService;

    @Mock
    private PlatformService platformService;

    @Mock
    private ServerLockService serverLockService;

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

        when(serverRepository
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

    @Test
    public void testCloseServer() {
        Long id = 1L;
        boolean isOpen = true;
        Server server = Server.builder().id(id).isOpen(isOpen).build();

        when(serverLockService.acquireLock(id)).thenReturn(1);
        when(serverRepository.findById(id)).thenReturn(Optional.of(server));
        serverService.closeServer(server.getId());

        assertThat(server.getIsOpen()).isEqualTo(false);

        verify(gridService).deleteByServerId(server.getId());
        verify(wordService).deleteByServerId(server.getId());
        verify(playerService).deleteByServerId(server.getId());

        verify(serverRepository).save(server);
        verify(auditService).log(AuditActionType.SERVER_CLOSED, server.getId());
    }

    @Test
    public void testCloseServerThrowsServerNotFound() {
        Long id = 1L;

        when(serverLockService.acquireLock(id)).thenReturn(1);
        when(serverRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serverService
                .closeServer(id))
                .isInstanceOf(ServerNotFoundException.class)
                .hasMessageContaining("Сервер не найден");
    }

    @Test
    public void testCloseServerThrowsInvalidCondition() {
        Long id = 1L;

        when(serverLockService.acquireLock(id)).thenReturn(0);

        assertThatThrownBy(() -> serverService
                .closeServer(id))
                .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    public void testWipeServer() {
        Long id = 1L;
        boolean isOpen = true;
        Server server = Server.builder().id(id).isOpen(isOpen).wipeCount(0).build();

        when(serverLockService.acquireLock(id)).thenReturn(1);
        when(serverRepository.findById(id)).thenReturn(Optional.of(server));
        serverService.wipeServer(server.getId());

        verify(gridService).deleteByServerId(server.getId());
        verify(serverRepository).save(server);
        verify(auditService).log(AuditActionType.SERVER_WIPED, server.getId());
    }

    @Test
    public void testWipeServerThrowsInvalidCondition() {
        Long id = 1L;

        when(serverLockService.acquireLock(id)).thenReturn(0);

        assertThatThrownBy(() -> serverService
                .wipeServer(id))
                .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    public void testWipeServerThrowsServerNotFound() {
        Long id = 1L;

        when(serverLockService.acquireLock(id)).thenReturn(1);
        when(serverRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serverService
                .wipeServer(id))
                .isInstanceOf(ServerNotFoundException.class)
                .hasMessageContaining("Сервер не найден");
    }

    @Test
    public void testDeleteServer() {
        Long serverId = 1L;
        Language language = Language.builder().name("test").build();
        Platform platform = Platform.builder().name("test").build();
        Server server = Server.builder()
                .id(serverId)
                .language(language)
                .platform(platform)
                .cleanupInProgress(false)
                .isOpen(false)
                .build();
        when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));

        DeleteServerResponse response = serverService.deleteServer(serverId);

        assertNotNull(response);
        verify(serverRepository).deleteById(serverId);
        verify(auditService).log(
                eq(AuditActionType.SERVER_DELETED),
                eq(serverId),
                eq("test"),
                eq("test")
        );
    }

    @Test
    public void testDeleteServerThrowsServerNotFound() {
        Long serverId = 1L;

        when(serverRepository.findById(serverId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serverService
                .deleteServer(serverId))
                .isInstanceOf(ServerNotFoundException.class)
                .hasMessageContaining("Сервер не найден");
    }

    @Test
    public void testDeleteServerThrowsInvalidCondition() {
        Long serverId = 1L;
        Language language = Language.builder().name("test").build();
        Platform platform = Platform.builder().name("test").build();
        Server server = Server.builder()
                .id(serverId)
                .cleanupInProgress(true)
                .isOpen(true)
                .language(language)
                .platform(platform)
                .build();

        when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));

        assertThatThrownBy(() -> serverService
                .deleteServer(serverId))
                .isInstanceOf(ServerCleanupInProgressException.class)
                .hasMessageContaining("Выполняется очистка");
    }
    @Test
    public void testDeleteServerThrowsInvalidConditionWhenIsOpen() {
        Long serverId = 1L;
        Language language = Language.builder().name("test").build();
        Platform platform = Platform.builder().name("test").build();
        Server server = Server.builder()
                .id(serverId)
                .cleanupInProgress(false)
                .isOpen(true)
                .language(language)
                .platform(platform)
                .build();

        when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));

        assertThatThrownBy(() -> serverService
                .deleteServer(serverId))
                .isInstanceOf(InvalidConditionException.class);
    }
}
