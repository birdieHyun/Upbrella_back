package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.LockerPasswordResponse;
import upbrella.be.rent.entity.Locker;
import upbrella.be.rent.exception.LockerCodeAlreadyIssuedException;
import upbrella.be.rent.exception.LockerSignatureErrorException;
import upbrella.be.rent.exception.NoSignatureException;
import upbrella.be.rent.repository.LockerRepository;
import upbrella.be.util.HotpGenerator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LockerService {

    private final LockerRepository lockerRepository;

    @Transactional
    public LockerPasswordResponse findLockerPassword(RentUmbrellaByUserRequest rentUmbrellaByUserRequest) {

        Optional<Locker> lockerOptional = lockerRepository.findByStoreMetaId(rentUmbrellaByUserRequest.getStoreId());

        return lockerOptional.map(this::getLockerPasswordResponse).orElse(null);
    }

    public void validateLockerSignature(Long storeId, String salt, String signature) {

        Optional<Locker> lockerOptional = lockerRepository.findByStoreMetaId(storeId);

        if (lockerOptional.isEmpty() && (salt != null || signature != null)) {
            throw new NoSignatureException("구형 보관함은 salt와 signature가 없습니다.");
        }

        if (lockerOptional.isPresent()) {
            Locker locker = lockerOptional.get();
            String lockerSecretKey = locker.getSecretKey().toUpperCase();
            salt = salt.toUpperCase();

            validateSignature(signature, salt, lockerSecretKey);
        }
    }

    private LockerPasswordResponse getLockerPasswordResponse(Locker locker) {

        if (locker.getLastAccess() != null && locker.getLastAccess().isAfter(LocalDateTime.now().minusMinutes(1))) {
            throw new LockerCodeAlreadyIssuedException("UBU 우산 대여 실패: 1분 이내에 이미 대여된 우산");
        }

        String password = HotpGenerator.generate((int) locker.getCount(), locker.getSecretKey());
        locker.updateCount();
        locker.updateLastAccess(LocalDateTime.now());

        return new LockerPasswordResponse(password);
    }

    private void validateSignature(String signature, String salt, String lockerSecretKey) {

        String lockerSignature = encodeHash(lockerSecretKey, salt);

        if (!lockerSignature.equals(signature)) {
            throw new LockerSignatureErrorException("우산 반납 실패: 잘못된 signature");
        }
    }

    private String encodeHash(String lockerSecretKey, String salt) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] encodedhash = digest.digest((lockerSecretKey + "." + salt).getBytes(StandardCharsets.UTF_8));

        // 바이트 배열을 16진수 문자열로 변환
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);

        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}


