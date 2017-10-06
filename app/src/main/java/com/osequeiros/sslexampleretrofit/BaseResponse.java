package com.osequeiros.sslexampleretrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Usuario on 06/10/2017.
 */

public class BaseResponse {

    @SerializedName("Codigo")
    private int codigo;

    @SerializedName("Mensaje")
    private String mensaje;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
