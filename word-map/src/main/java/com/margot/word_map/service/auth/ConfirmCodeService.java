package com.margot.word_map.service.auth;

import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.exception.ActiveCodeExistsException;
import com.margot.word_map.exception.ConfirmNotFoundException;
import com.margot.word_map.exception.ErrorCode;
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

    public Confirm findById(Long confirmId) {
        return confirmRepository.findById(confirmId)
                .orElseThrow(ConfirmNotFoundException::new);
    }

    @Transactional
    public ConfirmCodeDto generateConfirmCode(Long adminId) {
        String code = generateRandomCode();
        Confirm confirm = confirmRepository.findByAdminId(adminId)
                .orElse(null);

        if (confirm != null) {
            if (confirm.getExpiryAt().isAfter(LocalDateTime.now())) {
                throw new ActiveCodeExistsException();
            }
            confirmRepository.delete(confirm);
        }

        Confirm newConfirm = new Confirm(code, adminId);
        newConfirm.setCode(code);
        newConfirm.setCreatedAt(LocalDateTime.now());
        newConfirm.setExpiryAt(LocalDateTime.now().plusSeconds(confirmCodeExpirationTime));

        confirmRepository.save(newConfirm);
        return new ConfirmCodeDto(code, newConfirm.getId(), confirmCodeExpirationTime);
    }

    @Transactional
    public Confirm verifyConfirmCode(Long confirmId, String code) {
        Confirm confirm = findById(confirmId);

        validateConfirmCode(confirm, code);
        confirmRepository.delete(confirm);

        return confirm;
    }

    public Confirm verifyConfirmById(Long confirmId) {
        Confirm confirm = findById(confirmId);

        if (confirm.getExpiryAt().isAfter(LocalDateTime.now())) {
            throw new ActiveCodeExistsException();
        }

        return confirm;
    }

    private void validateConfirmCode(Confirm confirm, String code) {
        if (!confirm.getCode().equals(code)) {
            throw new InvalidConfirmCodeException();
        }
        if (confirm.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new InvalidConfirmCodeException(ErrorCode.CODE_SPOILED);
        }
    }

    private String generateRandomCode() {
        int code = secureRandom.nextInt(1_000_000);

        return String.format("%06d", code);
    }
}
