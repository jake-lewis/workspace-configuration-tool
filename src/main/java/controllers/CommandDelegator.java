package controllers;

import model.commands.Command;
import model.executors.Executor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

public class CommandDelegator {

    private static CommandDelegator INSTANCE = new CommandDelegator();

    private Map<Class<?>, Executor<?>> executors = new HashMap<>();
    private ListIterator<Command> commands = new LinkedList<Command>().listIterator();

    private CommandDelegator() {}

    public static CommandDelegator getINSTANCE() {
        return INSTANCE;
    }

    public <C extends Command> boolean subscribe(Executor<C> executor, Class <C> clazz) {

        //prevent duplicate subscription to a command
        if (executors.containsKey(clazz)) {
            return false;
        }

        executors.put(clazz, executor);
        return true;
    }

    public <C extends Command> boolean unsubscribe(Executor<C> executor) {
        return executors.entrySet().removeIf((entry) -> entry.getValue().equals(executor));
    }

    /**
     * Publishes command to the subscribed executor. <br/>
     * Throws exception if the command does not execute successfully. <br/>
     * Returns null if no executor is subscribed. <br/>
     * @param command The command to execute
     * @throws Exception
     */
    public boolean publish(Command command) throws Exception {
        Executor executor = executors.get(command.getClass());

        if (executor != null) {
            executor.execute(command);
            commands.add(command);
            return true;
        }

        return false;
    }
}