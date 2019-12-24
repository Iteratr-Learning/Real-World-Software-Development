package com.iteratrlearning.shu_book.chapter_05;

public class Rule {

    private Condition condition;
    private Action action;

    public Rule(Condition condition, Action action) {
        this.condition = condition;
        this.action = action;
    }

    public void perform(Facts facts) {
        if(condition.evaluate(facts)){
            action.execute(facts);
        }
    }
}
