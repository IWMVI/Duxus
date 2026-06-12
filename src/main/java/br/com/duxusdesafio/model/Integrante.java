package br.com.duxusdesafio.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "integrante")
public class Integrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String funcao;

    @OneToMany(mappedBy = "integrante")
    private List<ComposicaoTime> composicaoTime;

    public Integrante() {
    }

    public Integrante(String nome, String funcao, List<ComposicaoTime> composicaoTime) {
        this.nome = nome;
        this.funcao = funcao;
        this.composicaoTime = composicaoTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public List<ComposicaoTime> getComposicaoTime() {
        return composicaoTime;
    }

    public void setComposicaoTime(List<ComposicaoTime> composicaoTime) {
        this.composicaoTime = composicaoTime;
    }

    @Override
    public final boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Integrante)) {
            return false;
        }
        Integrante outro = (Integrante) objeto;
        return id != 0 && id == outro.id;
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Integrante{"
                + "id=" + id
                + ", nome='" + nome + '\''
                + ", funcao='" + funcao + '\''
                + '}';
    }
}
