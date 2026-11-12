package gent.timdemey.cards.services.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.SolShowPlugin;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.model.entities.commands.C_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.C_SolShowMove;
import gent.timdemey.cards.model.entities.commands.C_SolShowUse;
import gent.timdemey.cards.model.entities.commands.D_OnEndGame;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityFactory;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.cardgame.SolShowCardGameService;
import gent.timdemey.cards.services.cardgame.SolShowDevOptions;
import gent.timdemey.cards.services.context.ChangeType;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;
import gent.timdemey.cards.services.context.RecordingLimitedContext;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.frame.StateListener;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.interfaces.ISoundService;
import gent.timdemey.cards.services.resources.SolShowResourceRepository;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.lobby.SolShowLobbyPanelManager;

class CardPlacementStateListenerTest
{
	@Test
	void stackAdditionsForBothPlayersSelectOneSoundAndRemovalsAreSilent() throws Exception
	{
		Services services = new Services();
		App.init(services);
		services.install(ILogManager.class, new LogManager(LogLevel.ERROR));
		SolShowResourceRepository repository = new SolShowResourceRepository();
		repository.preload();
		services.install(IResourceRepository.class, repository);
		AtomicBoolean hosting = new AtomicBoolean(false);
		State state = new State();
		services.install(ICardPlugin.class, new SolShowPlugin()
		{
			@Override
			public State createState()
			{
				return state;
			}
		});
		RecordingLimitedContext dialogs = new RecordingLimitedContext();
		ContextService context = new ContextService()
		{
			@Override
			public boolean isUiThread()
			{
				return true;
			}

			@Override
			public LimitedContext getContext(ContextType type)
			{
				assertEquals(ContextType.UI, type);
				return dialogs;
			}

			@Override
			public boolean isInitialized(ContextType type)
			{
				return type == ContextType.Server ? hosting.get() : super.isInitialized(type);
			}
		};
		services.install(IContextService.class, context);
		context.initialize(ContextType.UI);
		services.install(IFrameService.class, unusedService(IFrameService.class));
		services.install(IPanelService.class, unusedService(IPanelService.class));
		List<ResourceDescriptor> played = new ArrayList<>();
		services.install(ISoundService.class, new SolShowSoundService()
		{
			@Override
			public void play(ResourceDescriptor descriptor)
			{
				played.add(descriptor);
			}
		});

		CardStack local = new CardStack(SolShowCardStackType.LAYDOWN, 0);
		CardStack remote = new CardStack(SolShowCardStackType.LAYDOWN, 0);
		CardStack middle = new CardStack(SolShowCardStackType.MIDDLE, 0);
		state.setCardGame(new CardGame(List.of(
				new PlayerConfiguration(UUID.randomUUID(), List.of(local, middle)),
				new PlayerConfiguration(UUID.randomUUID(), List.of(remote)))));
		Card card = new Card(Suit.SPADES, Value.V_A, true);
		StateListener listener = new StateListener();

		listener.onChange(change(ChangeType.Add, local, List.of(card), null));
		listener.onChange(change(ChangeType.Add, remote, List.of(card), null));
		listener.onChange(change(ChangeType.Add, middle, List.of(card), null));
		listener.onChange(change(ChangeType.Remove, local, null, List.of(card)));
		listener.onChange(change(ChangeType.Add, local, List.of(), null));
		assertEquals(List.of(SolShowSoundService.SoundLaydown, SolShowSoundService.SoundLaydown,
				ResourceDescriptors.SoundPutDown), played);

		UUID localId = UUID.randomUUID();
		UUID remoteId = UUID.randomUUID();
		state.setLocalId(localId);
		played.clear();
		new C_OnGameEnded(localId).execute(state);
		assertEquals(GameState.Ended, state.getGameState());
		state.setGameState(GameState.Started);
		new C_OnGameEnded(remoteId).execute(state);
		assertEquals(GameState.Ended, state.getGameState());
		assertEquals(List.of(SolShowSoundService.SoundWin, SolShowSoundService.SoundLose), played);
		assertEquals(2, dialogs.scheduled.size());
		assertEquals(localId, ((D_OnEndGame) dialogs.scheduled.get(0)).winnerId);
		assertEquals(remoteId, ((D_OnEndGame) dialogs.scheduled.get(1)).winnerId);

		SolShowDevOptions options = new SolShowDevOptions(true);
		services.install(SolShowDevOptions.class, options);
		SwingUtilities.invokeAndWait(() ->
		{
			class TestLobby extends SolShowLobbyPanelManager
			{
				void addOptions(JSLayeredPane panel)
				{
					super.addHostOptions(panel, null);
				}
			}
			TestLobby lobby = new TestLobby();
			JSLayeredPane panel = JSFactory.createLayeredPane(ComponentTypes.PANEL);
			lobby.addOptions(panel);
			assertEquals(0, panel.getComponentCount());
			hosting.set(true);
			lobby.addOptions(panel);
			assertEquals(1, panel.getComponentCount());
			JCheckBox checkbox = (JCheckBox) panel.getComponent(0);
			assertEquals("DEV MODE: easy card distribution", checkbox.getText());
			assertFalse(checkbox.isSelected());
			checkbox.doClick();
			assertTrue(options.isEasyDistribution());
			checkbox.doClick();
			assertFalse(options.isEasyDistribution());
			checkbox.doClick();
		});
		checkEasyDeal(state, dialogs, localId, remoteId, options);
	}

	private static void checkEasyDeal(State state, RecordingLimitedContext dialogs, UUID localId, UUID remoteId, SolShowDevOptions options)
	{
		SolShowDevOptions disabled = new SolShowDevOptions(false);
		assertFalse(disabled.isEnabled());
		assertThrows(IllegalStateException.class, () -> disabled.setEasyDistribution(true));
		assertTrue(options.isEasyDistribution());
		SolShowCardGameService service = new SolShowCardGameService(options);
		CardGame game = service.createCardGame(List.of(localId, remoteId));
		for (UUID player : List.of(localId, remoteId))
		{
			CardStack special = game.getCardStack(player, SolShowCardStackType.SPECIAL, 0);
			assertEquals(13, special.getCards().size());
			assertEquals(Value.V_4, special.getCards().get(0).value);
			assertEquals(Suit.HEARTS, special.getCards().get(0).suit);
			for (int group = 0; group < 3; group++)
			{
				Value expected = new Value[] { Value.V_3, Value.V_2, Value.V_A }[group];
				Set<Suit> suits = new HashSet<>();
				for (int index = 1 + group * 4; index < 5 + group * 4; index++)
				{
					Card c = special.getCards().get(index);
					assertEquals(expected, c.value);
					suits.add(c.suit);
					assertEquals(index == 12, c.visibleRef.get());
				}
				assertEquals(Set.of(Suit.values()), suits);
			}
			assertFalse(special.getCards().get(0).visibleRef.get());
			Set<String> cards = new HashSet<>();
			for (CardStack stack : game.playerConfigurations.stream().filter(pc -> pc.playerId.equals(player)).findFirst().orElseThrow().cardStacks)
			{
				for (Card c : stack.getCards())
				{
					assertTrue(cards.add(c.suit.name() + c.value.name()));
				}
			}
			assertEquals(52, cards.size());
			assertEquals(35, game.getCardStack(player, SolShowCardStackType.DEPOT, 0).getCards().size());
			for (int i = 0; i < 4; i++)
			{
				assertEquals(1, game.getCardStack(player, SolShowCardStackType.MIDDLE, i).getCards().size());
			}
		}
		state.setCardGame(game);
		state.setGameState(GameState.Started);
		for (UUID id : List.of(localId, remoteId))
		{
			P_Player payload = new P_Player();
			payload.id = id;
			payload.name = id.toString();
			state.getPlayers().add(new Player(payload));
		}
		for (UUID id : List.of(localId, remoteId))
		{
			state.setLocalId(id);
			CardStack special = game.getCardStack(id, SolShowCardStackType.SPECIAL, 0);
			while (!special.getCards().isEmpty())
			{
				Card card = special.getHighestCard();
				C_SolShowUse use = new C_SolShowUse(null, card.id);
				use.setSourceId(id);
				assertTrue(use.canExecute(state).canExecute(), card.getNotation());
				use.execute(state);
				C_SolShowMove move = (C_SolShowMove) dialogs.scheduled.remove(dialogs.scheduled.size() - 1);
				assertTrue(move.canExecute(state).canExecute());
				CardStack destination = game.getCardStack(move.dstCardStackId);
				special.removeAll(List.of(card));
				destination.addAll(List.of(card));
				card.cardStack = destination;
				if (!special.getCards().isEmpty()) special.getHighestCard().visibleRef.set(true);
			}
		}
		Set<List<String>> remainingOrders = new HashSet<>();
		for (int i = 0; i < 4; i++)
		{
			CardGame dealt = service.createCardGame(List.of(localId, remoteId));
			List<String> remaining = new ArrayList<>();
			for (int middle = 0; middle < 4; middle++)
			{
				remaining.add(dealt.getCardStack(localId, SolShowCardStackType.MIDDLE, middle).getHighestCard().getNotation());
			}
			for (Card c : dealt.getCardStack(localId, SolShowCardStackType.DEPOT, 0).getCards())
			{
				remaining.add(c.getNotation());
			}
			remainingOrders.add(remaining);
		}
		assertTrue(remainingOrders.size() > 1);
		options.setEasyDistribution(false);
		assertFalse(options.isEasyDistribution());
		Set<List<String>> ordinarySpecials = new HashSet<>();
		for (int i = 0; i < 4; i++)
		{
			CardGame dealt = service.createCardGame(List.of(localId, remoteId));
			ordinarySpecials.add(dealt.getCardStack(localId, SolShowCardStackType.SPECIAL, 0).getCards().stream()
					.map(Card::getNotation).toList());
		}
		assertTrue(ordinarySpecials.size() > 1);
	}

	private static ReadOnlyChange change(ChangeType type, CardStack stack, List<Object> added, List<Object> removed)
	{
		return ReadOnlyEntityFactory.getReadOnlyChangeListValue(type, CardStack.Cards, stack.id, added, removed);
	}

	private static <T> T unusedService(Class<T> type)
	{
		return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type },
				(proxy, method, args) -> { throw new AssertionError("Unexpected call: " + method); }));
	}
}
