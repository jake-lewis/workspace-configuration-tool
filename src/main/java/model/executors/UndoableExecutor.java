package model.executors;

import model.commands.UndoableCommand;

public interface UndoableExecutor <C extends UndoableCommand> extends Executor<C>  {
    void unexecute();
}
