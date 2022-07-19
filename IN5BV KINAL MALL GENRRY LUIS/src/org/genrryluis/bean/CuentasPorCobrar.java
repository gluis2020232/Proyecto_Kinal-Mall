package org.genrryluis.bean;

import java.math.BigDecimal;
import org.genrryluis.system.Principal;

/**
 * @author Genrry Luis
 * @date 13/04/2021
 * @time 11:52:19 Código 
 * técnico: IN5BV
 */
public class CuentasPorCobrar {

    private int id;
    private String numeroFactura;
    private int anio;
    private int mes;
    private BigDecimal valorNetoPago;
    private String estadoPago;
    private int idAdministracion;
    private int idCliente;
    private int idLocal;

    public CuentasPorCobrar() {
    }

    public CuentasPorCobrar(int Id, String numeroFactura, int anio, int mes, BigDecimal valorNetoPago, String estadoPago, int idAdministracion, int idCliente, int idLocal) {
        this.id = Id;
        this.numeroFactura = numeroFactura;
        this.anio = anio;
        this.mes = mes;
        this.valorNetoPago = valorNetoPago;
        this.estadoPago = estadoPago;
        this.idAdministracion = idAdministracion;
        this.idCliente = idCliente;
        this.idLocal = idLocal;
    }

    public int getId() {
        return id;
    }

    public void setId(int Id) {
        this.id = Id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public BigDecimal getValorNetoPago() {
        return valorNetoPago;
    }

    public void setValorNetoPago(BigDecimal valorNetoPago) {
        this.valorNetoPago = valorNetoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public int getIdAdministracion() {
        return idAdministracion;
    }

    public void setIdAdministracion(int idAdministracion) {
        this.idAdministracion = idAdministracion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    @Override
    public String toString() {
        return  id + ", " + numeroFactura;
    } 

}
