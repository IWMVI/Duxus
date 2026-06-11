package br.com.duxusdesafio.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class ErroResposta {

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private final LocalDateTime instante = LocalDateTime.now();
    private final int status;
    private final String erro;
    private final List<String> mensagens;

    public ErroResposta(int status, String erro, List<String> mensagens) {
        this.status = status;
        this.erro = erro;
        this.mensagens = mensagens;
    }

    public LocalDateTime getInstante() {
        return instante;
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public List<String> getMensagens() {
        return mensagens;
    }
}
