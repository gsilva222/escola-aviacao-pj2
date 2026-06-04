package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.PaymentRequest;
import pt.ipvc.estg.web.dto.PaymentResponse;
import pt.ipvc.estg.web.mappers.PaymentMapper;
import pt.ipvc.estg.web.repositories.PaymentRepository;
import pt.ipvc.estg.web.repositories.StudentRepository;
import pt.ipvc.estg.web.validation.BusinessRules;

import java.util.List;

@RestController
@RequestMapping("/bo/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    public PaymentController(PaymentRepository paymentRepository, StudentRepository studentRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public Page<PaymentResponse> getPayments(@RequestParam(required = false) Integer studentId,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(required = false) String sort) {
        PageRequest pageable = PageRequest.of(page, size, buildSort(sort));
        if (studentId != null) {
            List<PaymentResponse> content = paymentRepository.findByStudent_Id(studentId, pageable)
                    .stream().map(PaymentMapper::toResponse).toList();
            long total = paymentRepository.findByStudent_Id(studentId).size();
            return new PageImpl<>(content, pageable, total);
        }
        if (status != null && !status.trim().isEmpty()) {
            List<PaymentResponse> content = paymentRepository.findByStatus(status, pageable)
                    .stream().map(PaymentMapper::toResponse).toList();
            long total = paymentRepository.findByStatus(status).size();
            return new PageImpl<>(content, pageable, total);
        }
        return paymentRepository.findAll(pageable).map(PaymentMapper::toResponse);
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable("id") Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Pagamento nao encontrado"));
        return PaymentMapper.toResponse(payment);
    }

    @PostMapping
    public PaymentResponse createPayment(@Valid @RequestBody PaymentRequest request) {
        BusinessRules.requirePositive("Montante", request.amount());
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado"));

        Payment payment = new Payment(student, request.description(), request.amount(), request.dueDate());
        if (request.paidDate() != null) payment.setPaidDate(request.paidDate());
        if (request.status() != null) payment.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.PAYMENT_STATUSES));
        if (request.paymentMethod() != null) payment.setPaymentMethod(request.paymentMethod());
        if (request.notes() != null) payment.setNotes(request.notes());

        Payment created = paymentRepository.save(payment);
        return PaymentMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public PaymentResponse updatePayment(@PathVariable("id") Integer id, @RequestBody PaymentRequest request) {
        BusinessRules.requirePositive("Montante", request.amount());
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Pagamento nao encontrado"));

        if (request.description() != null && !request.description().trim().isEmpty()) {
            payment.setDescription(request.description());
        }
        if (request.amount() != null) payment.setAmount(request.amount());
        if (request.dueDate() != null) payment.setDueDate(request.dueDate());
        if (request.paidDate() != null) payment.setPaidDate(request.paidDate());
        if (request.status() != null) payment.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.PAYMENT_STATUSES));
        if (request.paymentMethod() != null) payment.setPaymentMethod(request.paymentMethod());
        if (request.notes() != null) payment.setNotes(request.notes());

        if (request.studentId() != null) {
            Student student = studentRepository.findById(request.studentId())
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
            payment.setStudent(student);
        }

        Payment updated = paymentRepository.save(payment);
        return PaymentMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable("id") Integer id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Pagamento nao encontrado");
        }
        paymentRepository.deleteById(id);
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.unsorted();
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length == 2 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(direction, property);
    }
}
