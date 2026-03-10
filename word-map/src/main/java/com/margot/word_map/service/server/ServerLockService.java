package com.margot.word_map.service.server;

import com.margot.word_map.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServerLockService {
    private final ServerRepository serverRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int acquireLock(Long serverId) {
        return serverRepository.markCleanupStarted(serverId);
    }
}
