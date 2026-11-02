package gent.timdemey.cards.protocol;

/**
 * Implemented by every message that carries (part of) shared state.
 * <ul>
 * <li>A delta (event) with version N brings a state at version N-1 to version N. Every delta is
 * numbered, including rejections, and all clients receive the same sequence.</li>
 * <li>A full state with version N is the accumulation of deltas 1..N. It is only used to enter a
 * lobby or game; afterwards a client follows the deltas.</li>
 * </ul>
 * A receiver at version v ignores anything with version &lt;= v and applies a delta with version v+1.
 * On a delta with version N &gt; v+1 it requests the missing range v+1..N-1
 * ({@link ClientMsg.RequestMissedEvents}) and applies the deltas strictly in order, so no delta is ever skipped.
 */
public interface Versioned
{
	long version();
}
