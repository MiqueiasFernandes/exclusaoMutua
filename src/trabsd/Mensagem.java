/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabsd;

import java.io.Serializable;

/**
 *
 * @author mfernandes
 */
public class Mensagem implements Serializable {

    int id = 0;

    String mensagem, ip, rede = "";

    public Mensagem(int id) {
        this.id = id;
    }

    public Mensagem(int id, String mensagem) {
        this.id = id;
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Mensagem(int id, String mensagem, String ip) {
        this.id = id;
        this.mensagem = mensagem;
        this.ip = ip;
        this.rede = ip + ">";
    }

    public String getIp() {
        return ip;
    }

    public void addno(String no) {

        if (!rede.contains(no)) {
            rede += no + ">";
        }
    }

    public String getRede() {
        return rede;
    }

    public int getId() {
        return id;
    }

    public void ordenar(String de, String para) {

        String[] split = rede.split(">");

        String res = "";

        for (String string : split) {

            if (string.isEmpty()) {
                continue;
            }

            if (string == null ? de == null : string.equals(de)) {
                res += de + ">" + para + ">";
                continue;
            }

            if (string == null ? para == null : string.equals(para)) {
                continue;
            }

            res += string + ">";

        }

    }

    int getNovoId() {

        if (id < 999) {
            return ++id;
        } else {

            return id = 1;
        }

    }

}
