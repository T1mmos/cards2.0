package gent.timdemey.cards.services.animation;

/**
 * The kind of animation, which determines the layer range a component is animated in.
 * The declaration order is the stacking order: later kinds are rendered on top of earlier kinds.
 */
public enum AnimationKind
{
	/** Components that only shift into their (new) place within the same stack. */
	RELAYOUT,
	/** Components that move towards another location, e.g. a card moving to another stack. */
	MOVE,
	/** Short-lived overlays such as score labels. */
	OVERLAY,
	/** Components being dragged by the user. Not animated, but shares the layer scheme. */
	DRAG
}
