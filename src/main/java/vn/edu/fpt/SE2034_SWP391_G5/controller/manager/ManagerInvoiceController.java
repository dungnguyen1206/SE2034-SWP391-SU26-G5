package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;
import vn.edu.fpt.SE2034_SWP391_G5.enums.PaymentStatus;
import vn.edu.fpt.SE2034_SWP391_G5.repository.InvoiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.InvoiceService;

import java.time.LocalDate;
import java.time.Year;

@Controller
@RequestMapping("/manager/invoices")
@RequiredArgsConstructor
public class ManagerInvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ModelAndView overview(@RequestParam(required = false) LocalDate startDate,
                                 @RequestParam(required = false) LocalDate endDate,
                                 @RequestParam(required = false) Integer month,
                                 @RequestParam(required = false) Integer year) {

        ModelAndView mv = new ModelAndView("manager/invoices/overview");

        InvoiceSummaryResponse paidInvoice = invoiceService.getInvoiceSummary(PaymentStatus.PAID.toString(),month,year,startDate,endDate);
        InvoiceSummaryResponse pendingInvoice= invoiceService.getInvoiceSummary(PaymentStatus.PENDING.toString(),month,year,startDate,endDate);

        mv.addObject("paidInvoice",paidInvoice);
        mv.addObject("pendingInvoice",pendingInvoice);
        mv.addObject("startDate", startDate);
        mv.addObject("endDate", endDate);
        mv.addObject("month", month);
        mv.addObject("year", year);
        mv.addObject("yearSelect",LocalDate.now().getYear());
        return mv;

    }
}
