package controllers;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import model.commands.Command;
import model.commands.UndoableCommand;
import model.executors.Executor;
import model.executors.UndoableExecutor;

import java.util.*;

public class CommandDelegator implements Observable {

    private static CommandDelegator INSTANCE = new CommandDelegator();

    private Map<Class<?>, Executor<?>> executors = new HashMap<>();
    private ListIterator<Command> commands = new LinkedList<Command>().listIterator();

    private CommandDelegator() {}

    public static CommandDelegator getINSTANCE() {
        return INSTANCE;
    }

    private List<InvalidationListener> listeners = new LinkedList<>();

    public <C extends Command> boolean subscribe(Executor<C> executor, Class <C> clazz) {

        //TODO multiple classes? e.g. Class <C>... clazz
        //prevent duplicate subscription to a command
        if (executors.containsKey(clazz)) {
            return false;
        }

        executors.put(clazz, executor);
        return true;
    }

    public <C extends Command> boolean unsubscribe(Executor<C> executor) {
        notifyListeners();
        return executors.entrySet().removeIf((entry) -> entry.getValue().equals(executor));
    }

    private Executor getExecutor(Command command) {
        return executors.get(command.getClass());
    }

    /**
     * Publishes command to the subscribed executor. <br/>
     * @param command The command to execute
     * @return false if no executor is subscribed.
     * @throws Exception if the command does not execute successfully
     */
    public synchronized boolean publish(Command command) throws Exception {
        Executor executor = getExecutor(command);

        if (executor != null) {

            //remove any redoable commands in front of published command
            //i.e. can't publish, undo, publish, then redo the first publish
            while (commands.hasNext()) {
                commands.next();
                commands.remove();
            }

            //Unchecked call to execute()
            //doing this because can't determine type until runtime, will be correct
            //noinspection unchecked
            executor.execute(command);
            commands.add(command);
            notifyListeners();
            return true;
        }

        return false;
    }

    /**
     * Call unexecute command on executor on previous command
     * Only works if both command and executor are undoable
     * @return false if no executor is subscribed
     * @throws Exception if undo does not execute successfully
     */
    public synchronized boolean undo() throws Exception {
        if (commands.hasPrevious()) {
            Command command = commands.previous();

            try {
                if (command instanceof UndoableCommand) {
                    Executor executor = getExecutor(command);
                    if (executor instanceof UndoableExecutor) {
                        UndoableExecutor undoableExecutor = (UndoableExecutor) executor;
                        //Unchecked call to unexecute()
                        //doing this because can't determine type until runtime, will be correct
                        //noinspection unchecked
                        undoableExecutor.unexecute((UndoableCommand) command);
                        notifyListeners();
                        return true;
                    }
                }
                commands.next();
            } catch (Exception e) {
                //Undo rolling history back
                commands.next();
                throw e;
            }
        }

        return false;
    }

    /**
     * Call unexecute command on executor on previous command
     * Only works if both command and executor are undoable
     * @return false if no executor is subscribed
     * @throws Exception if undo does not execute successfully
     */
    public synchronized boolean redo() throws Exception {
        if (commands.hasNext()) {
            Command command = commands.next();

            try {
                if (command instanceof UndoableCommand) {
                    Executor executor = getExecutor(command);
                    if (executor instanceof UndoableExecutor) {
                        UndoableExecutor undoableExecutor = (UndoableExecutor) executor;
                        //Unchecked call to unexecute()
                        //doing this because can't determine type until runtime, will be correct
                        //noinspection unchecked
                        undoableExecutor.execute((UndoableCommand) command);
                        notifyListeners();
                        return true;
                    }
                }
                commands.previous();
            } catch (Exception e) {
                //Undo rolling history back
                commands.previous();
                throw e;
            }
        }

        return false;
    }

    public boolean canUndo() {
        //Check if there is a previous command, that is undoable
        if (commands.hasPrevious()) {
            Command previous = commands.previous();
            commands.next(); //revert position of ListIterator
            return previous instanceof UndoableCommand;
        }
        return false;
    }

    public boolean canRedo() {
        if (commands.hasNext()) {
            Command next = commands.next();
            commands.previous(); //Revert position of ListIterator
            return next instanceof UndoableCommand;
        }
        return false;
    }

    public String getUndoName() {
        if (canUndo()) {
            commands.previous();
            return commands.next().getName();
        }

        return null;
    }

    public String getRedoName() {
        if (canRedo()) {
            commands.next();
            return commands.previous().getName();
        }

        return null;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyListeners() {
        for (InvalidationListener listener : listeners) {
            listener.invalidated(this);
        }
    }
}