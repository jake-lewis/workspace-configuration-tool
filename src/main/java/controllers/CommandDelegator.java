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
        for (Class subbedClass: executors.keySet()) {
            if (subbedClass.isAssignableFrom(clazz)) {
                return false;
            }
        }

        executors.put(clazz, executor);
        return true;
    }

    public <C extends Command> boolean unsubscribe(Executor<C> executor) {
        notifyListeners();
        return executors.entrySet().removeIf((entry) -> entry.getValue().equals(executor));
    }

    private Executor getExecutor(Command command) {
        for (Class clazz: executors.keySet()) {
            if (clazz.isAssignableFrom(command.getClass())) {
                return executors.get(clazz);
            }
        }

        return null;
    }

    /**
     * Publishes command to the subscribed executor. Always records for undo, see {@link #publish(Command, boolean)} <br/>
     * @param command The command to execute
     * @return false if no executor is subscribed.
     * @throws Exception if the command does not execute successfully
     */
    public synchronized boolean publish(Command command) throws Exception {
        return publish(command, true);
    }

    /**
     * Publishes command to the subscribed executor, with the option of recording for undo <br/>
     * @param command The command to execute
     * @param record whether or not to add the command to the stack, enabling undo/redo
     * @return false if no executor is subscribed.
     * @throws Exception if the command does not execute successfully
     */
    public synchronized boolean publish(Command command, boolean record) throws Exception {
        Executor executor = getExecutor(command);

        if (executor != null) {

            //remove any redoable commands in front of published command
            //i.e. can't publish, undo, publish, then redo the first publish
            while (commands.hasNext()) {
                commands.next();
                commands.remove();
            }

            //If the command is not undoable, clear all previous history
            if (!(command instanceof UndoableCommand)) {
                while (commands.hasPrevious()) {
                    commands.previous();
                    commands.remove();
                }
            }

            //Unchecked call to execute()
            //doing this because can't determine type until runtime, will be correct
            //noinspection unchecked
            executor.execute(command);
            if (record) {
                commands.add(command);
                notifyListeners();
                System.out.println("Do " + command.getName()); //TODO issue #23
            }
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
                        System.out.println("Undo " + command.getName());
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
                        undoableExecutor.reexecute((UndoableCommand) command);
                        notifyListeners();
                        System.out.println("Redo " + command.getName());
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