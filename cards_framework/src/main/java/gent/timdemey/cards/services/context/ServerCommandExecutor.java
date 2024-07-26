package gent.timdemey.cards.services.context;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.state.CommandHistory;
import gent.timdemey.cards.model.entities.commands.CommandType;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.interfaces.INetworkService;

class ServerCommandExecutor extends CommandExecutorBase
{

    private final INetworkService _NetworkService;
    private final CommandDtoMapper _CommandDtoMapper;
    private final Logger _Logger;
    private final CommandFactory _CommandFactory;
    
    public ServerCommandExecutor(
        INetworkService networkService,
        CommandFactory commandFactory,
        CommandDtoMapper commandDtoMapper,
        Logger logger
    )
    {
        super(ContextType.Server);
        
        this._NetworkService = networkService;
        this._CommandFactory = commandFactory;
        this._CommandDtoMapper = commandDtoMapper;
        this._Logger = logger;
    }

    @Override
    protected void execute(CommandBase command, State state)
    {
        _Logger.info("Processing command '%s', id=%s", command.getName(), command.id);

        CommandHistory cmdHist = state.getCommandHistory();

        // a command cannot be executed twice
        if (cmdHist != null && cmdHist.containsAccepted(command))
        {
            _Logger.warn("This command was already executed: '%s'; ignoring", command.getName());
            return;
        }

        CanExecuteResponse resp = command.canExecute(state);
        CommandType cmdType = command.getCommandType();

        // syncable commands are commands that are executed clientside,
        // but can be rejected serverside e.g. when another player was earlier
        // but due to network delay the client wasn't yet aware.
        if (resp.execState == ExecutionState.Yes)
        {
            if (cmdType == CommandType.TRACKED || cmdType == CommandType.SYNCED)
            {
                state.getCommandHistory().addAccepted(command, state);

                // in case of syncable commands, we send a response to the source so
                // the client can mark the command as accepted in its history
                if (cmdType == CommandType.SYNCED)
                {
                    C_Accept acceptCmd = _CommandFactory.CreateAccept(command.id);
                    _NetworkService.send(state.getLocalId(), command.getSourceId(), acceptCmd, state.getTcpConnectionPool());
                }
            }
            else if (cmdType == CommandType.DEFAULT)
            {
                command.execute(state);
            }
        }
        else if (resp.execState == ExecutionState.No)
        {
            if (cmdType == CommandType.SYNCED)
            {
                _Logger.info("Can't execute syncable command: '%s'. Responding with a C_Reject. Reason: %s", command.getName(), resp.reason);

                C_Reject rejectCmd = _CommandFactory.CreateReject(command.id);
                String answer = _CommandDtoMapper.toJson(rejectCmd);
                state.getTcpConnectionPool().getConnection(command.getSourceId()).send(answer);
            }
            else
            {
                _Logger.error("Can't execute non-syncable command: '%s'. Reason: %s", command.getName(), resp.reason);
            }
        }
        else
        {
            _Logger.error("Can't execute command '%s' because of state error. Reason: %s", command.getName(), resp.reason);
        }
    }
}
