package com.margot.word_map.service.server;

import com.margot.word_map.exception.InvalidConditionException;
import com.margot.word_map.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServerLockService {
    private final ServerRepository serverRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int startWipe(Long serverId) {
        int updatedRows = serverRepository.markCleanupStarted(serverId);
        if (updatedRows == 0) {
            throw new InvalidConditionException();
        }
        return updatedRows;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeWipe(Long id, LocalDateTime wipedAt, int wipeCount) {
        serverRepository.completeWipe(id, wipedAt, wipeCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeClosing(Long id, LocalDateTime wipedAt, boolean isOpen) {
        serverRepository.completeClosing(id, wipedAt, isOpen);
    }
}
