package model.executors;

import model.commands.Command;

public interface Executor <C extends Command> {
    void execute(C command) throws Exception;
}