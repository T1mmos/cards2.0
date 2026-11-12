package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

public class RecordingLimitedContext extends LimitedContext
{
	public final List<CommandBase> scheduled = new ArrayList<>();

	public RecordingLimitedContext()
	{
		super(ContextType.UI, new ICommandExecutor()
		{
			public void schedule(CommandBase command, State state) { throw new AssertionError(); }
			public void run(CommandBase command, State state) { throw new AssertionError(); }
			public void shutdown() { throw new AssertionError(); }
			public void addExecutionListener(IExecutionListener listener) { throw new AssertionError(); }
			public void removeExecutionListener(IExecutionListener listener) { throw new AssertionError(); }
		});
	}

	@Override
	public void schedule(CommandBase command)
	{
		scheduled.add(command);
	}
}
