package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;

@Getter
@Setter
@NoArgsConstructor
// Mã OTP tạm giữ giữa bước gửi mã và bước xác minh
public class OtpToken implements Serializable {

    // Số lần được phép nhập sai trước khi mã bị hủy
    private static final int MAX_ATTEMPTS = 5;

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

    // Kiểm tra mã người dùng nhập, sai hoặc hết hạn thì báo lỗi
    public void verify(String inputOtp) {
        if (LocalDateTime.now().isAfter(expiryTime)) {
            throw new BadRequestException("Mã OTP đã hết hạn. Vui lòng thực hiện lại từ đầu.");
        }

        if (!otp.equals(inputOtp)) {
            attemptCount++;

            // Nhập sai đủ số lần cho phép thì mã bị hủy
            if (attemptCount >= MAX_ATTEMPTS) {
                throw new BadRequestException("Bạn đã nhập sai mã OTP " + MAX_ATTEMPTS + " lần. Vui lòng thực hiện lại từ đầu.");
            }

            throw new BadRequestException("Mã OTP không chính xác. Bạn còn " + (MAX_ATTEMPTS - attemptCount) + " lần thử.");
        }
    }

    // Mã không dùng được nữa: đã hết hạn hoặc nhập sai quá số lần cho phép
    public boolean isVoided() {
        return attemptCount >= MAX_ATTEMPTS || LocalDateTime.now().isAfter(expiryTime);
    }
}
