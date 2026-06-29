package vn.edu.fpt.SE2034_SWP391_G5.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Invoice.DataConflictException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Invoice.InvoiceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule.ScheduleConflictException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 404);
        return "error/404";
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(BadRequestException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 400);
        return "error/400";
    }

    // Trước đây không có handler riêng → NoResourceFoundException (favicon.ico, static files)
    // bị bắt bởi handleGeneral → render trang error/500 với HTTP 500
    // Fix: để Spring xử lý tự nhiên (trả 404 cho browser, không render error page)
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoResource() {
        // Không làm gì - Spring sẽ trả 404 cho static resource không tìm thấy
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + ex.getMessage());
        model.addAttribute("errorCode", 500);
        return "error/500";
    }


    /*
    *
    * Function helps handle schedule conflict
    *
    */

    @ExceptionHandler(ScheduleConflictException.class)
    public String handleScheduleConflict(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        return  "redirect:" + (referer != null ? referer : "/manager/schedules/list");
    }

    /*
     *
     * Function helps handle invoice conflict
     *
     */

    @ExceptionHandler(DataConflictException.class)
    private String handleDataConflict(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "/manager/invoices/overview";
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    private String handleInvoiceNotFound(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "/manager/invoices/overview";
    }


}

