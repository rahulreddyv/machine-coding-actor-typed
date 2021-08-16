package com.lld.simpleactors;

import com.lld.simpleactors.actors.Actor;
import com.lld.simpleactors.actors.ActorRef;
import com.lld.simpleactors.actors.ActorSystem;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final ActorSystem system = Actor.newSystem("simple-actor");
        try {
            // #create-actors
            final ActorRef<Printer> printerActor = system.actorOf(Printer::new, "printerActor");
            final ActorRef<Greeter> howdyGreeter = system.actorOf(() -> new Greeter("Howdy", printerActor), "howdyGreeter");
            final ActorRef<Greeter> helloGreeter = system.actorOf(() -> new Greeter("Hello", printerActor), "helloGreeter");
            final ActorRef<Greeter> goodDayGreeter = system.actorOf(() -> new Greeter("Good day", printerActor), "goodDayGreeter");
            // #create-actors

            // #main-send-messages
            howdyGreeter.tell(gr -> gr.setWhoToGreet("Actor"));
            howdyGreeter.tell(Greeter::greet);

            howdyGreeter.tell(gr -> gr.setWhoToGreet("Rahul"));
            howdyGreeter.tell(Greeter::greet);

            helloGreeter.tell(gr -> gr.setWhoToGreet("Kalyan"));
            helloGreeter.tell(Greeter::greet);

            goodDayGreeter.tell(gr -> gr.setWhoToGreet("Deepika"));
            goodDayGreeter.tell(Greeter::greet);
            // #main-send-messages

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.shutdown();
        }
    }

    private static class Printer {
        public void print(String greeting) {System.err.println(greeting);}
    }

    private static class Greeter {
        private String message;
        private ActorRef<Printer> printerActor;
        private String greeting;

        public Greeter(String message, ActorRef<Printer> printerActor) {
            this.message = message;
            this.printerActor = printerActor;
        }

        public void setWhoToGreet(String whoToGreet) {this.greeting = message + ", " + whoToGreet;}

        public void greet() {
            String greetingMsg = greeting;
            printerActor.tell(printer -> printer.print(greetingMsg));
        }
    }
}
