package com.mesaeva.viktorines.domain.entities;

public class CurrentVikt {
    private Integer viktId;
    private String login;
    private Integer currentQ;
    private boolean qIsChecked;

    public CurrentVikt(Integer viktId, String login) {
        this.viktId = viktId;
        this.login = login;
    }

    public CurrentVikt(Integer viktId, String login, Integer currentQ) {
        this.viktId = viktId;
        this.login = login;
        this.currentQ = currentQ;
    }

    public Integer getViktId() {
        return viktId;
    }

    public void setViktId(Integer viktId) {
        this.viktId = viktId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getCurrentQ() {
        return currentQ;
    }

    public void setCurrentQ(Integer currentQ) {
        this.currentQ = currentQ;
    }

    public boolean isqIsChecked() {
        return qIsChecked;
    }

    public void setqIsChecked(boolean qIsChecked) {
        this.qIsChecked = qIsChecked;
    }
}
