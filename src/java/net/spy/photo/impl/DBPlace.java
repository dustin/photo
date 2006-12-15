// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.spy.db.AbstractSavable;
import net.spy.db.GetPK;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.photo.MutablePlace;
import net.spy.photo.PhotoConfig;
import net.spy.photo.sp.InsertPlace;
import net.spy.photo.sp.ModifyPlace;
import net.spy.photo.sp.UpdatePlace;

/**
 * DB backed Place implementation.
 */
public class DBPlace extends AbstractSavable implements MutablePlace {

	private int id=0;
	private String name=null;
	private double lon=0d;
	private double lat=0d;

	public DBPlace() throws SQLException {
		super();

		GetPK pk=GetPK.getInstance();
		id=pk.getPrimaryKey(PhotoConfig.getInstance(), "place_id").intValue();

		setModified(false);
		setNew(true);
	}

	public DBPlace(ResultSet rs) throws SQLException {
		super();
		id=rs.getInt("place_id");
		name=rs.getString("name");
		lon=rs.getDouble("lon");
		lat=rs.getDouble("lat");

		setModified(false);
		setNew(false);
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

		ModifyPlace db=null;
		if(isNew()) {
			db=new InsertPlace(conn);
		} else {
			db=new UpdatePlace(conn);
		}

		db.setPlaceId(id);
		db.setName(name);
		db.setLat(new BigDecimal(lat));
		db.setLon(new BigDecimal(lon));

		int aff=db.executeUpdate();
		assert aff == 1 : "Expected to affect 1 record, affected " + aff;

		db.close();
	}

}
