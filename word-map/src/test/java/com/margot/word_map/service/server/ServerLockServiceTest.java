package com.margot.word_map.service.server;

import com.margot.word_map.exception.InvalidConditionException;
import com.margot.word_map.repository.ServerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class ServerLockServiceTest {

    @Mock
    private ServerRepository serverRepository;

    @InjectMocks
    private ServerLockService serverLockService;

    @Test
    public void testStartWipe() {
        Long serverId = 1L;
        when(serverRepository.markCleanupStarted(serverId)).thenReturn(1);
        int result = serverLockService.startWipe(serverId);
        assertThat(result).isEqualTo(1);
        verify(serverRepository).markCleanupStarted(serverId);
    }

    @Test
    public void testStartWipeThrowsInvalidConditionException() {
        Long serverId = 1L;

        when(serverRepository.markCleanupStarted(serverId)).thenReturn(0);

        assertThatThrownBy(() -> serverLockService.startWipe(serverId))
                .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    public void testCompleteWipe() {
        Long serverId = 1L;
        LocalDateTime now = LocalDateTime.now();
        int wipeCount = 0;

        serverLockService.completeWipe(serverId, now, wipeCount + 1);

        verify(serverRepository).completeWipe(serverId, now, wipeCount + 1);
    }

    @Test
    public void testCompleteClosing() {
        Long serverId = 1L;
        LocalDateTime now = LocalDateTime.now();
        boolean isOpen = false;

        serverLockService.completeClosing(serverId, now, isOpen);

        verify(serverRepository).completeClosing(serverId, now, isOpen);
    }
}
