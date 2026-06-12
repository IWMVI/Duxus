package br.com.duxusdesafio.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "time")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Nome do Clube (associação esportiva)
     * a qual o time representa.
     * Exemplos:
     * Para Futebol - Palmeiras, Santos, etc;
     * Para Basquete - Pinheiros, Franca, etc.
     */
    @NotBlank
    @Column(nullable = false)
    private String nomeDoClube;

    /**
     * Data em que esse time foi formado (composição do time firmada)
     * Lembrando: a formação da equipe pode mudar em momentos diferentes, por isso a data.
     */
    @NotNull
    @Column(nullable = false, unique = true)
    private LocalDate data;

    /**
     * Elenco ou equipe - o conjunto de integrantes desse time
     */
    @OneToMany(mappedBy = "time", cascade = CascadeType.ALL)
    private List<ComposicaoTime> composicaoTime;

    public Time() {
    }

    public Time(String nomeDoClube, LocalDate data, List<ComposicaoTime> composicaoTime) {
        this.nomeDoClube = nomeDoClube;
        this.data = data;
        this.composicaoTime = composicaoTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNomeDoClube() {
        return nomeDoClube;
    }

    public void setNomeDoClube(String nomeDoClube) {
        this.nomeDoClube = nomeDoClube;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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
        if (!(objeto instanceof Time)) {
            return false;
        }
        Time outro = (Time) objeto;
        return id != 0 && id == outro.id;
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Time{"
                + "id=" + id
                + ", nomeDoClube='" + nomeDoClube + '\''
                + ", data=" + data
                + '}';
    }
}
