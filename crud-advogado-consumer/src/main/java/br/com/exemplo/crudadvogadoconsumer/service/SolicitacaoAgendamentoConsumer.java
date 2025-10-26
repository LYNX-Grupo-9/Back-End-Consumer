package br.com.exemplo.crudadvogadoconsumer.service;

import br.com.exemplo.crudadvogadoconsumer.dto.SolicitacaoAgendamentoMessage;
import br.com.exemplo.crudadvogadoconsumer.model.Advogado;
import br.com.exemplo.crudadvogadoconsumer.repository.AdvogadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class SolicitacaoAgendamentoConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoAgendamentoConsumer.class);
    private final EmailService emailService;
    private final AdvogadoRepository advogadoRepository;

    public SolicitacaoAgendamentoConsumer(EmailService emailService,
                                          AdvogadoRepository advogadoRepository) {
        this.emailService = emailService;
        this.advogadoRepository = advogadoRepository;
    }

    @RabbitListener(queues = "solicitacao.agendamento.queue")
    public void receberSolicitacaoAgendamento(SolicitacaoAgendamentoMessage message) {
        try {
            logger.info("🎯 CONSUMER - Mensagem recebida! ID: {}", message.idSolicitacaoAgendamento());

            String dataFormatada = formatarData(message.dataSolicitacao());
            String horaFormatada = formatarHora(message.horaSolicitacao());

            String nomeAdvogado = buscarNomeAdvogado(message.idAdvogado());

            emailService.enviarEmailConfirmacaoAgendamento(
                    message.email(),
                    message.nome(),
                    message.assunto(),
                    dataFormatada,
                    horaFormatada,
                    nomeAdvogado
            );

            logger.info("📧 EMAIL ENVIADO para: {} - Advogado: {}", message.email(), nomeAdvogado);

        } catch (Exception e) {
            logger.error("❌ CONSUMER - Erro: {}", e.getMessage(), e);
        }
    }

    private String buscarNomeAdvogado(UUID idAdvogado) {
        final String FALLBACK_NOME = "Advogado Responsável";

        if (idAdvogado == null) {
            logger.warn("ID do advogado é nulo");
            return FALLBACK_NOME;
        }

        try {
            Optional<Advogado> advogadoOpt = advogadoRepository.findById(idAdvogado);

            if (advogadoOpt.isPresent()) {
                Advogado advogado = advogadoOpt.get();
                String nomeCompleto = advogado.getNome();


                logger.info("Advogado encontrado: {} (ID: {})", nomeCompleto, idAdvogado);
                return nomeCompleto;
            } else {
                logger.warn("Advogado não encontrado para ID: {}", idAdvogado);
                return FALLBACK_NOME;
            }

        } catch (Exception e) {
            logger.error("Erro ao buscar advogado no banco: {}", e.getMessage());
            return FALLBACK_NOME;
        }
    }

    private String formatarData(Date data) {
        if (data == null) return "Data não informada";
        try {
            return new SimpleDateFormat("dd/MM/yyyy").format(data);
        } catch (Exception e) {
            logger.warn("Erro ao formatar data: {}", e.getMessage());
            return "Data inválida";
        }
    }

    private String formatarHora(LocalTime hora) {
        if (hora == null) return "Horário não informado";
        try {
            return hora.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            logger.warn("Erro ao formatar hora: {}", e.getMessage());
            return "Horário inválido";
        }
    }
}