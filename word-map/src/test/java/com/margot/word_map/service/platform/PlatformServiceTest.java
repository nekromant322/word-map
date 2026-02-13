package com.margot.word_map.service.platform;

import com.margot.word_map.dto.PlatformDto;
import com.margot.word_map.dto.request.CreateUpdatePlatformRequest;
import com.margot.word_map.exception.DuplicateNameException;
import com.margot.word_map.exception.PlatformNotFoundException;
import com.margot.word_map.mapper.PlatformMapper;
import com.margot.word_map.model.Platform;
import com.margot.word_map.repository.PlatformRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlatformServiceTest {

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private PlatformMapper platformMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PlatformService platformService;

    @Test
    public void testCreatePlatformSavesAndLogs() {
        String name = "test";
        CreateUpdatePlatformRequest request = new CreateUpdatePlatformRequest(name);

        PlatformDto expectedDto = new PlatformDto(1L, "test");

        when(platformRepository.existsByName(name)).thenReturn(false);
        when(platformMapper.toDto(any(Platform.class))).thenReturn(expectedDto);

        PlatformDto result = platformService.createPlatform(request);

        assertThat(result).isSameAs(expectedDto);

        verify(platformRepository).save(any(Platform.class));
        verify(auditService).log(eq(AuditActionType.PLATFORM_CREATED), eq(name));
    }

    @Test
    public void testCreatePlatformThrowsThenDuplicateName() {
        String name = "test";

        CreateUpdatePlatformRequest request = new CreateUpdatePlatformRequest(name);

        when(platformRepository.existsByName(name)).thenReturn(true);

        assertThatThrownBy(() -> platformService.createPlatform(request))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessageContaining("Платформа с таким названием уже существует: test");
    }

    @Test
    public void testUpdatePlatformUpdatesAndLogs() {
        Long platformId = 1L;
        String name = "test";
        String newName = "new";

        CreateUpdatePlatformRequest request = new CreateUpdatePlatformRequest(newName);

        Platform platform = new Platform(platformId, name);

        PlatformDto expectedDto = new PlatformDto(1L, newName);

        when(platformRepository.existsByNameExcludeId(newName, platformId)).thenReturn(false);
        when(platformRepository.findById(platformId)).thenReturn(Optional.of(platform));
        when(platformMapper.toDto(platform)).thenReturn(expectedDto);

        PlatformDto result = platformService.updatePlatform(platformId, request);

        assertThat(result).isSameAs(expectedDto);
        assertThat(platform.getName()).isEqualTo(newName);

        verify(platformRepository).save(eq(platform));
        verify(auditService).log(eq(AuditActionType.PLATFORM_UPDATED), eq(newName));
    }

    @Test
    public void testUpdatePlatformThrowsWhenDuplicateName() {
        Long id = 1L;
        String name = "test";

        CreateUpdatePlatformRequest request = new CreateUpdatePlatformRequest(name);

        when(platformRepository.existsByNameExcludeId(name, id)).thenReturn(true);

        assertThatThrownBy(() -> platformService.updatePlatform(id, request))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessageContaining("Платформа с таким названием уже существует: test");
    }

    @Test
    public void testUpdatePlatformThrowsWhenNotFound() {
        Long id = 1L;
        String name = "test";

        CreateUpdatePlatformRequest request = new CreateUpdatePlatformRequest(name);

        when(platformRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> platformService.updatePlatform(id, request))
                .isInstanceOf(PlatformNotFoundException.class)
                .hasMessageContaining("Платформа не найдена по идентификатору: 1");
    }
}
