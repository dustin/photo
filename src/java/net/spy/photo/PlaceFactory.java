// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: A0320902-0F0C-4444-A9FC-E0C04B227A60

package net.spy.photo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import net.spy.factory.GenFactory;
import net.spy.photo.impl.DBPlace;
import net.spy.photo.sp.SelectAllPlaces;
import net.spy.util.CloseUtil;

/**
 * Maker of places.
 */
public class PlaceFactory extends GenFactory<Place> {

	private static final String CACHE_KEY = "place";
	private static final long CACHE_TIME = 86400000l;

	private static PlaceFactory instance=null;

	protected PlaceFactory() {
		super(CACHE_KEY, CACHE_TIME);
	}

	/**
	 * Get the singleton PlaceFactory instance.
	 */
	public static synchronized PlaceFactory getInstance() {
		if(instance == null) {
			instance=new PlaceFactory();
		}
		return instance;
	}

	@Override
	protected Collection<Place> getInstances() {
		Collection<Place> rv=new ArrayList<Place>();
		SelectAllPlaces db=null;
		try {
			db=new SelectAllPlaces(PhotoConfig.getInstance());
			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				rv.add(new DBPlace(rs));
			}
			rs.close();
		} catch(SQLException e ) {
			throw new RuntimeException("Couldn't load places", e);
		} finally {
			CloseUtil.close(db);
		}
		return rv;
	}

}
