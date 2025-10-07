package soat_fiap.siaes.domain.serviceOrderToken.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrderItem.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ActivityItem;
import soat_fiap.siaes.domain.serviceOrderToken.model.ServiceOrderToken;
import soat_fiap.siaes.infrastructure.persistence.serviceOrder.ServiceOrderRepository;
import soat_fiap.siaes.infrastructure.persistence.serviceOrderToken.ServiceOrderTokenRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceOrderClientService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceOrderTokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendApprovalLink(ServiceOrder order) {
        // Gera link
        String link = generateApprovalLink(order.getId());

        // Busca e-mail do usuário
        String userEmail = "douglas.severa96@gmail.com";//order.getUser().getEmail();

        // Cria e envia e-mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Aprovação da Ordem de Serviço");
        message.setText(
                "Olá " + order.getUser().getName() + ",\n\n" +
                        "Sua ordem de serviço está aguardando aprovação.\n" +
                        "Clique no link abaixo para aprovar ou reprovar:\n\n" +
                        link + "\n\n" +
                        "Este link expira em 7 dias."
        );

        mailSender.send(message);
    }

    // Gera link de aprovação/reprovação
    public String generateApprovalLink(UUID serviceOrderId) {
        ServiceOrder order = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço não encontrada"));

        String token = UUID.randomUUID().toString();
        ServiceOrderToken orderToken = new ServiceOrderToken();
        orderToken.setServiceOrder(order);
        orderToken.setToken(token);
        orderToken.setExpiration(LocalDateTime.now().plusDays(7)); // Expira em 7 dias

        tokenRepository.save(orderToken);

        // Retorna link completo para aprovação/reprovação (localhost para dev)
        return baseUrl + "/client/service-orders/approval?token=" + token;
    }

    // Aprovar ou reprovar via token
    @Transactional
    public ServiceOrderResponse approveOrRejectByToken(String token, ServiceOrderStatusEnum status) {
        ServiceOrderToken orderToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token inválido"));

        if (orderToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expirado");
        }

        ServiceOrder order = orderToken.getServiceOrder();

        if (order.getOrderStatusEnum() != ServiceOrderStatusEnum.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Ordem de serviço não está aguardando aprovação.");
        }

        if (status != ServiceOrderStatusEnum.APROVADO_CLIENTE && status != ServiceOrderStatusEnum.REPROVADO_CLIENTE) {
            throw new IllegalArgumentException("Status inválido");
        }

        order.setOrderStatusEnum(status);
        if (status == ServiceOrderStatusEnum.APROVADO_CLIENTE) {
            order.setStartTime(LocalDateTime.now());
        }

        ServiceOrder savedOrder = serviceOrderRepository.save(order);

        // Opcional: remover token após uso
        tokenRepository.delete(orderToken);

        return new ServiceOrderResponse(savedOrder);
    }

    public ServiceOrder findOrderByToken(String token) {
        ServiceOrderToken orderToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token inválido"));

        if (orderToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expirado");
        }

        return orderToken.getServiceOrder();
    }

    public BigDecimal calculateGrandTotal(ServiceOrder order) {
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (OrderActivity item : order.getOrderActivities()) {
            // Adiciona o custo da mão de obra
            if (item.getServiceLabor() != null && item.getServiceLabor().getLaborCost() != null) {
                grandTotal = grandTotal.add(item.getServiceLabor().getLaborCost());
            }

            // Adiciona o custo dos suprimentos
            List<ActivityItem> supplies = item.getActivityItems() != null ? item.getActivityItems() : Collections.emptyList();
            for (ActivityItem supply : supplies) {
                if (supply.getUnitPrice() != null && supply.getQuantity() != null) {
                    BigDecimal supplyTotal = supply.getUnitPrice().multiply(BigDecimal.valueOf(supply.getQuantity()));
                    grandTotal = grandTotal.add(supplyTotal);
                }
            }
        }

        return grandTotal;
    }
}