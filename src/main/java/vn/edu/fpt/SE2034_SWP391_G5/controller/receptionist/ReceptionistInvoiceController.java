package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceListResponse;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.InvoiceService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

@Controller
@RequestMapping("/receptionist/invoice")
@RequiredArgsConstructor
public class ReceptionistInvoiceController {

    private final InvoiceService invoiceService;
    private final ReceptionistService receptionistService;
    private final vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService appointmentService;

    @GetMapping
    public String listInvoices(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String paymentStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoicePageWithStatsResponse response = invoiceService.getInvoices(keyword, paymentStatus, page, size);
        
        model.addAttribute("invoicePage", response.getPage());
        model.addAttribute("totalPaidCount", response.getTotalPaidCount());
        model.addAttribute("totalUnpaidCount", response.getTotalUnpaidCount());
        model.addAttribute("keyword", keyword);
        model.addAttribute("paymentStatus", paymentStatus);
        
        model.addAttribute("receptionist", receptionistService.getReceptionistByUsername(userDetails.getUser().getEmail()));
        model.addAttribute("activeMenu", "invoice");
        
        return "receptionist/invoice/list";
    }

    @GetMapping("/{id}")
    public String invoiceDetail(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model, RedirectAttributes redirectAttributes) {

        try {
            InvoiceDetailResponse invoice = invoiceService.getInvoiceDetail(id);
            
            model.addAttribute("invoice", invoice);
            model.addAttribute("from", from);
            model.addAttribute("receptionist", receptionistService.getReceptionistByUsername(userDetails.getUser().getEmail()));
            model.addAttribute("activeMenu", "invoice");
            
            return "receptionist/invoice/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage() + " | Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));
            return "redirect:/receptionist/invoice";
        }
    }

    @PostMapping("/{id}/pay")
    public String processPayment(
            @PathVariable Long id,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String from,
            RedirectAttributes redirectAttributes) {

        try {
            invoiceService.processPayment(id, paymentMethod);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");
            
            if ("checkin".equals(from)) {
                appointmentService.confirmCheckInAppointment(id);
                return "redirect:/receptionist/appointment/" + id + "/check-in-ticket";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/receptionist/invoice/" + id;
    }
}
