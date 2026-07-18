package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleImportError {

    private int rowNumber;
    private String field;
    private String errorMessage;


}
