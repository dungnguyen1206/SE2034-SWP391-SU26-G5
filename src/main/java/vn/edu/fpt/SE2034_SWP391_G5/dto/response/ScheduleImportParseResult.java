package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.ScheduleImportRow;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//Đây là kết quả đọc file excel
public class ScheduleImportParseResult {

    private int totalRows;

    @Builder.Default
    private List<ScheduleImportRow> rows = new ArrayList<>();

    @Builder.Default
    private List<ScheduleImportError> errors = new ArrayList<>();

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}