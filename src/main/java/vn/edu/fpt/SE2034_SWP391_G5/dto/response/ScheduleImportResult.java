package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//class này là kết quả trả về sau khi import thành công vào database
public class ScheduleImportResult {

    private int totalRows;
    private int validRows;
    private int importedRows;
    @Builder.Default
    private List<ScheduleImportError> errors = new ArrayList<>();

    public boolean isSuccessful() {
        return errors == null || errors.isEmpty();
    }

}
