package model.executors;

import model.commands.UndoableCommand;

public interface UndoableExecutor <C extends UndoableCommand> extends Executor<C>  {
    void unexecute(C command) throws Exception;

    /**
     * Default implementation calls execute()
     * Can be overriden to implement specific behaviour
     * @param command
     * @throws Exception
     */
    default void reexecute(C command) throws Exception {
        execute(command);
    }
}