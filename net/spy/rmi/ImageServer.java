// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: ImageServer.java,v 1.1 2000/06/30 04:11:19 dustin Exp $

package net.spy.rmi;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

import java.util.*;
import net.spy.photo.*;

public interface ImageServer extends Remote { 
	public PhotoImage getImage(int image_id, boolean thumbnail)
		throws RemoteException;
	public void storeImage(int image_id, PhotoImage image)
		throws RemoteException;
	public boolean ping() throws RemoteException;
}
