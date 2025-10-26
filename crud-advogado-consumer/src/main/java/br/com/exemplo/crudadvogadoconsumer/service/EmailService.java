package br.com.exemplo.crudadvogadoconsumer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarEmailConfirmacaoAgendamento(
            String destinatario,
            String nomeCliente,
            String assuntoConsulta,
            String dataConsulta,
            String horaConsulta,
            String nomeAdvogado) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Confirmação de Solicitação de Agendamento");
            helper.setFrom("nao-responder@advogados.com.br");

            // Prepara o contexto para o template
            Context context = new Context(new Locale("pt", "BR"));
            context.setVariable("nomeCliente", nomeCliente);
            context.setVariable("assuntoConsulta", assuntoConsulta);
            context.setVariable("dataConsulta", dataConsulta);
            context.setVariable("horaConsulta", horaConsulta);
            context.setVariable("nomeAdvogado", nomeAdvogado);

            // Processa o template HTML
            String htmlContent = templateEngine.process("confirmacao-agendamento", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar email de confirmação", e);
        }
    }
}
