package com.example.dobitnarae;

import java.io.Serializable;

public class BasketItem implements Serializable{
    private Clothes clothes;
    private int cnt;

    public BasketItem(Clothes clothes, int cnt){
        this.clothes = clothes;
        this.cnt = cnt;
    }

    public Clothes getClothes() {
        return clothes;
    }

    public int getCnt() {
        return cnt;
    }

    public void setClothes(Clothes clothes) {
        this.clothes = clothes;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}
