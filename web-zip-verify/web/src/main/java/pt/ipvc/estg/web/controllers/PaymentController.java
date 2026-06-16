package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.util.PageQueryParser;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.services.PaymentService;
import pt.ipvc.estg.services.PaymentSummaryService;
import pt.ipvc.estg.services.StudentService;
import pt.ipvc.estg.validation.BusinessRules;
import pt.ipvc.estg.web.dto.PaymentRequest;
import pt.ipvc.estg.web.dto.PaymentResponse;
import pt.ipvc.estg.web.dto.PaymentSummaryResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.PaymentMapper;

@RestController
@RequestMapping("/bo/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final StudentService studentService;
    private final PaymentSummaryService paymentSummaryService;

    public PaymentController(PaymentService paymentService,
                             StudentService studentService,
                             PaymentSummaryService paymentSummaryService) {
        this.paymentService = paymentService;
        this.studentService = studentService;
        this.paymentSummaryService = paymentSummaryService;
    }

    @GetMapping("/summary")
    public PaymentSummaryResponse summary(@RequestParam(value = "studentId", required = false) Integer studentId) {
        if (studentId != null) {
            return DomainDtoMapper.toResponse(paymentSummaryService.summarizeForStudent(studentId));
        }
        return DomainDtoMapper.toResponse(paymentSummaryService.summarizeAll());
    }

    @GetMapping
    public Page<PaymentResponse> getPayments(@RequestParam(value = "studentId", required = false) Integer studentId,
                                             @RequestParam(value = "status", required = false) String status,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "20") int size,
                                             @RequestParam(value = "sort", required = false) String sort) {
        PageQuery query = PageQueryParser.parse(page, size, sort);
        var result = paymentService.listPagamentos(query, studentId, status);
        return DomainDtoMapper.toSpringPage(result, PaymentMapper::toResponse, sort);
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable("id") Integer id) {
        return PaymentMapper.toResponse(paymentService.requirePagamento(id));
    }

    @PostMapping
    public PaymentResponse createPayment(@Valid @RequestBody PaymentRequest request) {
        Student student = studentService.requireEstudante(request.studentId());
        Payment payment = new Payment(student, request.description(), request.amount(), request.dueDate());
        if (request.paidDate() != null) payment.setPaidDate(request.paidDate());
        if (request.status() != null) {
            payment.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.PAYMENT_STATUSES));
        }
        if (request.paymentMethod() != null) payment.setPaymentMethod(request.paymentMethod());
        if (request.notes() != null) payment.setNotes(request.notes());
        return PaymentMapper.toResponse(paymentService.savePagamento(payment));
    }

    @PutMapping("/{id}")
    public PaymentResponse updatePayment(@PathVariable("id") Integer id, @RequestBody PaymentRequest request) {
        Payment payment = paymentService.requirePagamento(id);
        if (request.description() != null && !request.description().trim().isEmpty()) {
            payment.setDescription(request.description());
        }
        if (request.amount() != null) payment.setAmount(request.amount());
        if (request.dueDate() != null) payment.setDueDate(request.dueDate());
        if (request.paidDate() != null) payment.setPaidDate(request.paidDate());
        if (request.status() != null) {
            payment.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.PAYMENT_STATUSES));
        }
        if (request.paymentMethod() != null) payment.setPaymentMethod(request.paymentMethod());
        if (request.notes() != null) payment.setNotes(request.notes());
        if (request.studentId() != null) {
            payment.setStudent(studentService.requireEstudante(request.studentId()));
        }
        return PaymentMapper.toResponse(paymentService.savePagamento(payment));
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable("id") Integer id) {
        paymentService.eliminarPagamento(id);
    }
}
