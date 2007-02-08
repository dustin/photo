// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.aspects;

import net.spy.db.AbstractSavable;

/**
 * Mark savables as modified after a mutator is called.
 */
public aspect SavableModifierAspect {

	pointcut mutating(AbstractSavable s):
		target(s)
			&& (execution(public void AbstractSavable+.set*(*))
			 || execution(public void AbstractSavable+.add*(*)));

	after(AbstractSavable s) returning: mutating(s) {
		s.modify();
	}
}
