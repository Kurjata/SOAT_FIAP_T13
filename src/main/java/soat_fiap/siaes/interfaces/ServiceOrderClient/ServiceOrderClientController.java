package soat_fiap.siaes.interfaces.ServiceOrderClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrderToken.service.ServiceOrderClientService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;

import java.math.BigDecimal;

@Controller
@RequestMapping("/client/service-orders")
@RequiredArgsConstructor
public class ServiceOrderClientController {

    private final ServiceOrderClientService clientService;

    // Mostra a página de aprovação
    @GetMapping("/approval")
    public String showApprovalPage(@RequestParam String token, Model model) {
        ServiceOrder order = clientService.findOrderByToken(token);
        // Calcula o total
        BigDecimal grandTotal = clientService.calculateGrandTotal(order);
        model.addAttribute("order", order);
        model.addAttribute("token", token);
        model.addAttribute("grandTotal", grandTotal);
        return "approval"; // <-- corresponde a templates/approval.html
    }

    // Processa aprovação ou reprovação (POST)
    @PostMapping("/decision")
    @Transactional
    public String decision(
            @RequestParam String token,
            @RequestParam ServiceOrderStatusEnum status,
            RedirectAttributes redirectAttributes) {

        try {
            ServiceOrderResponse response = clientService.approveOrRejectByToken(token, status);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ordem de serviço " + (status == ServiceOrderStatusEnum.APROVADO_CLIENTE ? "aprovada" : "reprovada") + " com sucesso!");
            return "redirect:/client/service-orders/confirmation";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Token inválido.");
            return "redirect:/client/service-orders/confirmation";
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/client/service-orders/confirmation";
        }
    }

    // Página de confirmação
    @GetMapping("/confirmation")
    public String confirmationPage() {
        return "approval-confirmation"; // novo template
    }
}
