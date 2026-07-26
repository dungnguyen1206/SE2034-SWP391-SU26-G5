package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Mã OTP tạm giữ giữa bước gửi mã và bước xác minh
@Getter
@Setter
@NoArgsConstructor
public class OtpToken implements Serializable {

    private String otp;

    private LocalDateTime expiryTime;

    private String channel;

    private String identifier;

    // Số lần người dùng đã nhập sai mã
    private int attemptCount;

    public OtpToken(String otp, LocalDateTime expiryTime, String channel, String identifier) {
        this.otp = otp;
        this.expiryTime = expiryTime;
        this.channel = channel;
        this.identifier = identifier;
        this.attemptCount = 0;
    }
}
