package br.com.duxusdesafio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "composicao_time")
public class ComposicaoTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "time_id", nullable = false)
    private Time time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "integrante_id", nullable = false)
    private Integrante integrante;

    public ComposicaoTime() {
    }

    public ComposicaoTime(Time time, Integrante integrante) {
        this.time = time;
        this.integrante = integrante;
    }

    public ComposicaoTime(long id, Time time, Integrante integrante) {
        this.id = id;
        this.time = time;
        this.integrante = integrante;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integrante getIntegrante() {
        return integrante;
    }

    public void setIntegrante(Integrante integrante) {
        this.integrante = integrante;
    }

    @Override
    public final boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof ComposicaoTime)) {
            return false;
        }
        ComposicaoTime outra = (ComposicaoTime) objeto;
        return id != 0 && id == outra.id;
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ComposicaoTime{"
                + "id=" + id
                + ", timeId=" + (time == null ? null : time.getId())
                + ", integranteId=" + (integrante == null ? null : integrante.getId())
                + '}';
    }
}
