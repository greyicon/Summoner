package com.sam.summoner.account;

//Contains all summoner information, including basic account information
//and ranked standings in all ranked queues
public class Summoner {
    private Account account;
    private RankedInfo solo;
    private RankedInfo flex;
    private RankedInfo tree;

    public Summoner(Account account, RankedInfo solo, RankedInfo flex, RankedInfo tree) {
        this.account = account;
        this.solo = solo;
        this.flex = flex;
        this.tree = tree;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public RankedInfo getSolo() {
        return solo;
    }

    public void setSolo(RankedInfo solo) {
        this.solo = solo;
    }

    public RankedInfo getFlex() {
        return flex;
    }

    public void setFlex(RankedInfo flex) {
        this.flex = flex;
    }

    public RankedInfo getTree() {
        return tree;
    }

    public void setTree(RankedInfo tree) {
        this.tree = tree;
    }
}
