// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.aspects;

import net.spy.db.AbstractSavable;
import net.spy.photo.Mutable;

/**
 * Mark savables as modified after a mutator is called.
 */
public aspect SavableModifierAspect {

	pointcut mutating(AbstractSavable s):
		target(s)
			&& (call(public void Mutable+.set*(*))
			 || call(public void Mutable+.add*(*))
			 || call(public void AbstractSavable+.set*(*))
			 || call(public void AbstractSavable+.add*(*)));

	after(AbstractSavable s) returning: mutating(s) {
		s.modify();
	}
}
