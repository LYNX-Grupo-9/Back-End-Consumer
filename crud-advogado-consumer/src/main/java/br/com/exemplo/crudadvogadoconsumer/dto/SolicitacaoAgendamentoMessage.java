package br.com.exemplo.crudadvogadoconsumer.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

public record SolicitacaoAgendamentoMessage(
        Long idSolicitacaoAgendamento,
        String nome,
        String telefone,
        String email,
        String assunto,
        Date dataSolicitacao,
        LocalTime horaSolicitacao,
        UUID idAdvogado,
        String status,
        LocalDate dataCriacao
) {}