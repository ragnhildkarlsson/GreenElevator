package green_elevator.controller.command;

public interface Command {

    public enum CommandType {
	MOVECOMMAND, STOPCOMMAND, DOOROPENCOMMAND, DOORCLOSECOMMAND
    }

}
