// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Collection;

/**
 * Source of PhotoImageData.
 */
public interface PhotoImageDataSource {

	Collection getImages() throws Exception;

}
