package com.iteratrlearning.shu_book.chapter_05;

public class Main {

    public static void main(String...args) {

        var env = new Facts();
        env.setFact("name", "Bob");
        env.setFact("jobTitle", "CEO");

        var businessRuleEngine = new BusinessRuleEngine(env);

        Rule rule = RuleBuilder.when(facts -> "Richard".equals(facts.getFact("name")))
                .then(facts -> System.out.println("What's up Richard"));

        final Rule ruleSendEmailToSalesWhenCEO =
                RuleBuilder
                  .when(facts -> "CEO".equals(facts.getFact("jobTitle")))
                  .then(facts -> {
            var name = facts.getFact("name");
            System.out.println("Relevant customer!!!: " + name);
        });

        businessRuleEngine.addRule(ruleSendEmailToSalesWhenCEO);
        businessRuleEngine.run();

    }
}
