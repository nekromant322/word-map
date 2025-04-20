package com.margot.word_map.service.auth;

import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.exception.InvalidConfirmCodeException;
import com.margot.word_map.model.Confirm;
import com.margot.word_map.model.UserType;
import com.margot.word_map.repository.ConfirmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmCodeService {

    private final ConfirmRepository confirmRepository;

    @Value("${confirm.code-expiration-time}")
    private Integer confirmCodeExpirationTime;

    @Transactional
    public ConfirmCodeDto generateConfirmCode(UserType userType, Long userId) {
        Integer code = generateRandomCode();
        Confirm confirm = confirmRepository.findByUserIdAndUserType(userId, userType)
                .orElseGet(() -> new Confirm(code, userId, userType));

        confirm.setCode(code);
        confirm.setCreatedAt(LocalDateTime.now());
        confirm.setExpirationTime(LocalDateTime.now().plusSeconds(confirmCodeExpirationTime));

        Confirm savedConfirm = confirmRepository.save(confirm);
        return new ConfirmCodeDto(code, savedConfirm.getId(), confirmCodeExpirationTime);
    }

    @Transactional
    public void verifyConfirmCode(Integer code, Long userId, UserType userType) {
        Confirm confirm = confirmRepository.findByUserIdAndUserType(userId, userType)
                .orElseThrow(InvalidConfirmCodeException::new);

        validateConfirmCode(confirm, code);

        confirmRepository.delete(confirm);
    }

    private void validateConfirmCode(Confirm confirm, Integer code) {
        if (!confirm.getCode().equals(code)) {
            throw new InvalidConfirmCodeException();
        }
        if (confirm.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new InvalidConfirmCodeException();
        }
    }

    private Integer generateRandomCode() {
        return (int) (Math.random() * 900000) + 100000;
    }
}
