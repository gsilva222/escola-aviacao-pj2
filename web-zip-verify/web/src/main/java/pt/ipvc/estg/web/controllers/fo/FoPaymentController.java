package pt.ipvc.estg.web.controllers.fo;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.repositories.PaymentRepository;
import pt.ipvc.estg.services.PaymentSummaryService;
import pt.ipvc.estg.util.PageUtils;
import pt.ipvc.estg.web.dto.PaymentResponse;
import pt.ipvc.estg.web.dto.PaymentSummaryResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.PaymentMapper;
import pt.ipvc.estg.web.services.StudentScopeService;

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
        PageQuery query = PageQuery.of(page, size, "dueDate", true);
        PageResult<Payment> result = status != null && !status.isBlank()
                ? PageUtils.paginate(paymentRepository.findByStudentAndStatus(studentId, status), query)
                : paymentRepository.findByStudent(studentId, query);
        return DomainDtoMapper.toSpringPage(result, PaymentMapper::toResponse, "dueDate,desc");
    }

    @GetMapping("/summary")
    public PaymentSummaryResponse summary() {
        return DomainDtoMapper.toResponse(
                paymentSummaryService.summarizeForStudent(studentScopeService.requireCurrentStudent().getId()));
    }
}
