// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: F9C00759-33E4-45B9-BBCD-C80E8357FACF

package net.spy.photo;

import net.spy.factory.CacheKey;

public interface Instance {

	@CacheKey(name="id")
	int getId();
}
