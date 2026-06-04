package pt.ipvc.estg.web.controllers.fo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.web.dto.PaymentResponse;
import pt.ipvc.estg.web.dto.PaymentSummaryResponse;
import pt.ipvc.estg.web.mappers.PaymentMapper;
import pt.ipvc.estg.web.repositories.PaymentRepository;
import pt.ipvc.estg.web.services.PaymentSummaryService;
import pt.ipvc.estg.web.services.StudentScopeService;

import java.util.List;

@RestController
@RequestMapping("/fo/payments")
public class FoPaymentController {

    private final StudentScopeService studentScopeService;
    private final PaymentRepository paymentRepository;
    private final PaymentSummaryService paymentSummaryService;

    public FoPaymentController(StudentScopeService studentScopeService,
                               PaymentRepository paymentRepository,
                               PaymentSummaryService paymentSummaryService) {
        this.studentScopeService = studentScopeService;
        this.paymentRepository = paymentRepository;
        this.paymentSummaryService = paymentSummaryService;
    }

    @GetMapping
    public Page<PaymentResponse> getPayments(@RequestParam(value = "status", required = false) String status,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "20") int size) {
        int studentId = studentScopeService.requireCurrentStudent().getId();
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dueDate"));
        List<PaymentResponse> content;
        long total;
        if (status != null && !status.isBlank()) {
            content = paymentRepository.findByStudent_IdAndStatus(studentId, status).stream()
                    .map(PaymentMapper::toResponse).toList();
            total = content.size();
            int from = (int) pageable.getOffset();
            int to = Math.min(from + pageable.getPageSize(), content.size());
            content = from < content.size() ? content.subList(from, to) : List.of();
        } else {
            content = paymentRepository.findByStudent_Id(studentId, pageable)
                    .stream().map(PaymentMapper::toResponse).toList();
            total = paymentRepository.findByStudent_Id(studentId).size();
        }
        return new PageImpl<>(content, pageable, total);
    }

    @GetMapping("/summary")
    public PaymentSummaryResponse summary() {
        return paymentSummaryService.summarizeForStudent(studentScopeService.requireCurrentStudent().getId());
    }
}
