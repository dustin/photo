// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: A0C96BAB-A2C9-4A15-B825-C63BACE56EBD

package net.spy.photo.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.photo.MutablePlace;

/**
 * DB backed Place implementation.
 */
public class DBPlace extends AbstractSavable implements MutablePlace {

	private int id=0;
	private String name=null;
	private double lon=0d;
	private double lat=0d;

	public DBPlace(ResultSet rs) throws SQLException {
		super();
		id=rs.getInt("place_id");
		name=rs.getString("name");
		lon=rs.getDouble("lon");
		lat=rs.getDouble("lat");
	}

	public double getLatitude() {
		return lat;
	}

	public double getLongitude() {
		return lon;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}


	public void setLatitude(double to) {
		lat=to;
	}

	public void setLongitude(double to) {
		lon=to;
	}

	public void setName(String to) {
		name=to;
	}

	@Override
	public String toString() {
		return "{DBPlace name=" + name + "}";
	}

	public void save(Connection conn, SaveContext ctx) throws SaveException,
			SQLException {
		// TODO Auto-generated method stub
		throw new SaveException("Not implemented");
	}

}
