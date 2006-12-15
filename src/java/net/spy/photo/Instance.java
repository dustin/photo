// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import net.spy.factory.CacheKey;

public interface Instance {

	@CacheKey(name="id")
	int getId();
}
