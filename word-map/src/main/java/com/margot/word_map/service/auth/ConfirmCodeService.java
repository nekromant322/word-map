package com.margot.word_map.service.auth;

import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.exception.InvalidConfirmCodeException;
import com.margot.word_map.model.Confirm;
import com.margot.word_map.repository.ConfirmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmCodeService {

    private final ConfirmRepository confirmRepository;
    private final SecureRandom secureRandom;

    @Value("${confirm.code-expiration-time}")
    private Integer confirmCodeExpirationTime;

    @Transactional
    public ConfirmCodeDto generateConfirmCode(Long adminId) {
        String code = generateRandomCode();
        Confirm confirm = confirmRepository.findByAdminId(adminId)
                .orElseGet(() -> new Confirm(code, adminId));

        confirm.setCode(code);
        confirm.setCreatedAt(LocalDateTime.now());
        confirm.setExpiryAt(LocalDateTime.now().plusSeconds(confirmCodeExpirationTime));

        Confirm savedConfirm = confirmRepository.save(confirm);
        return new ConfirmCodeDto(code, savedConfirm.getId(), confirmCodeExpirationTime);
    }

    @Transactional
    public void verifyConfirmCode(String code, Long adminId) {
        Confirm confirm = confirmRepository.findByAdminId(adminId)
                .orElseThrow(InvalidConfirmCodeException::new);

        validateConfirmCode(confirm, code);

        confirmRepository.delete(confirm);
    }

    private void validateConfirmCode(Confirm confirm, String code) {
        if (!confirm.getCode().equals(code)) {
            throw new InvalidConfirmCodeException();
        }
        if (confirm.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new InvalidConfirmCodeException();
        }
    }

    private String generateRandomCode() {
        int code = secureRandom.nextInt(1_000_000);

        return String.format("%06d", code);
    }
}
