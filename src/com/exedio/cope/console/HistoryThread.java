/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.console;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.CacheInfo;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ConnectionPoolInfo;

final class HistoryThread extends Thread
{
	static final Model HISTORY_MODEL = new Model(HistoryModel.TYPE);
	private static final String NAME = "COPE History";
	
	private final String name;
	private final Model loggedModel;
	private final String logPropertyFile;
	private final Object lock = new Object();
	private final String topic;
	private final MediaPath[] medias;
	private volatile boolean proceed = true;
	
	HistoryThread(final Model model, final String logPropertyFile)
	{
		super(NAME);
		this.name = NAME + ' ' + '(' + Integer.toString(System.identityHashCode(this), 36) + ')';
		setName(name);
		this.loggedModel = model;
		this.logPropertyFile = logPropertyFile;
		this.topic = name + ' ';
		
		assert model!=null;
		assert logPropertyFile!=null;
		
		final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();
		for(final Type<?> type : loggedModel.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPath)
					medias.add((MediaPath)feature);
		this.medias = medias.toArray(new MediaPath[medias.size()]);
	}
	
	@Override
	public void run()
	{
		System.out.println(topic + "run() started");
		try
		{
			sleepByWait(2000l);
			if(!proceed)
				return;
			
			System.out.println(topic + "run() connecting");
			ConnectToken loggerConnectToken = null;
			final long connecting = System.currentTimeMillis();
			try
			{
				loggerConnectToken =
					ConnectToken.issue(HISTORY_MODEL, new ConnectProperties(new File(logPropertyFile)), name);
				System.out.println(topic + "run() connected (" + (System.currentTimeMillis() - connecting) + "ms)");
				//loggerModel.tearDownDatabase(); loggerModel.createDatabase();
				try
				{
					HISTORY_MODEL.startTransaction("check");
					HISTORY_MODEL.checkDatabase();
					HISTORY_MODEL.commit();
				}
				finally
				{
					HISTORY_MODEL.rollbackIfNotCommitted();
				}
				
				for(int running = 0; proceed; running++)
				{
					System.out.println(topic + "run() LOG " + running);
					log(running);
					sleepByWait(60000l);
				}
			}
			finally
			{
				if(loggerConnectToken!=null)
				{
					System.out.println(topic + "run() disconnecting");
					final long disconnecting = System.currentTimeMillis();
					loggerConnectToken.returnIt();
					System.out.println(topic + "run() disconnected (" + (System.currentTimeMillis() - disconnecting) + "ms)");
				}
				else
					System.out.println(topic + "run() not connected");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void log(final int running)
	{
		// prepare
		final int MEDIAS_STAT_LENGTH = 7;
		final int[][] mediaValues = new int[medias.length][];
		for(int i = 0; i<mediaValues.length; i++)
			mediaValues[i] = new int[MEDIAS_STAT_LENGTH];
		
		// gather data
		final Date date = new Date();
		final ConnectionPoolInfo connectionPoolInfo = loggedModel.getConnectionPoolInfo();
		final long nextTransactionId = loggedModel.getNextTransactionId();
		final CacheInfo[] itemCacheInfos = loggedModel.getItemCacheInfo();
		final long[] queryCacheInfo = loggedModel.getQueryCacheInfo();
		final int mediasNoSuchPath = MediaPath.noSuchPath.get();
		int mediaValuesIndex = 0;
		for(final MediaPath path : medias)
		{
			mediaValues[mediaValuesIndex][0] = path.exception.get();
			mediaValues[mediaValuesIndex][1] = path.notAnItem.get();
			mediaValues[mediaValuesIndex][2] = path.noSuchItem.get();
			mediaValues[mediaValuesIndex][3] = path.isNull.get();
			mediaValues[mediaValuesIndex][4] = path.notComputable.get();
			mediaValues[mediaValuesIndex][5] = path.notModified.get();
			mediaValues[mediaValuesIndex][6] = path.delivered.get();
			mediaValuesIndex++;
		}
		
		// process data
		int itemCacheHits = 0;
		int itemCacheMisses = 0;
		int itemCacheNumberOfCleanups = 0;
		int itemCacheItemsCleanedUp = 0;
		for(final CacheInfo ci : itemCacheInfos)
		{
			itemCacheHits += ci.getHits();
			itemCacheMisses += ci.getMisses();
			itemCacheNumberOfCleanups += ci.getNumberOfCleanups();
			itemCacheItemsCleanedUp += ci.getItemsCleanedUp();
		}
		
		final int[] mediaTotal = new int[MEDIAS_STAT_LENGTH];
		for(int[] mediaValue : mediaValues)
			for(int i = 0; i<MEDIAS_STAT_LENGTH; i++)
				mediaTotal[i] += mediaValue[i];
		
		final SetValue[] setValues = new SetValue[]{
				HistoryModel.date.map(date),
				HistoryModel.running.map(running),
				HistoryModel.connectionPoolIdle.map(connectionPoolInfo.getIdleCounter()),
				HistoryModel.connectionPoolGet.map(connectionPoolInfo.getCounter().getGetCounter()),
				HistoryModel.connectionPoolPut.map(connectionPoolInfo.getCounter().getPutCounter()),
				HistoryModel.connectionPoolInvalidFromIdle.map(connectionPoolInfo.getInvalidFromIdle()),
				HistoryModel.connectionPoolInvalidIntoIdle.map(connectionPoolInfo.getInvalidIntoIdle()),
				HistoryModel.nextTransactionId.map(nextTransactionId),
				HistoryModel.itemCacheHits.map(itemCacheHits),
				HistoryModel.itemCacheMisses.map(itemCacheMisses),
				HistoryModel.itemCacheNumberOfCleanups.map(itemCacheNumberOfCleanups),
				HistoryModel.itemCacheItemsCleanedUp.map(itemCacheItemsCleanedUp),
				HistoryModel.queryCacheHits.map(queryCacheInfo[0]),
				HistoryModel.queryCacheMisses.map(queryCacheInfo[1]),
				HistoryModel.mediasNoSuchPath.map(mediasNoSuchPath),
				HistoryModel.mediasException    .map(mediaTotal[0]),
				HistoryModel.mediasNotAnItem    .map(mediaTotal[1]),
				HistoryModel.mediasNoSuchItem   .map(mediaTotal[2]),
				HistoryModel.mediasIsNull       .map(mediaTotal[3]),
				HistoryModel.mediasNotComputable.map(mediaTotal[4]),
				HistoryModel.mediasNotModified  .map(mediaTotal[5]),
				HistoryModel.mediasDelivered    .map(mediaTotal[6])
		};

		// save data
		try
		{
			HISTORY_MODEL.startTransaction("log " + running);
			new HistoryModel(setValues);
			HISTORY_MODEL.commit();
		}
		finally
		{
			HISTORY_MODEL.rollbackIfNotCommitted();
		}
	}
	
	private void sleepByWait(final long millis)
	{
		synchronized(lock)
		{
			//System.out.println(topic + "run() sleeping (" + millis + "ms)");
			//final long sleeping = System.currentTimeMillis();
			try
			{
				lock.wait(millis);
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
			//System.out.println(topic + "run() slept    (" + (System.currentTimeMillis()-sleeping) + "ms)");
		}
	}
	
	void stopAndJoin()
	{
		System.out.println(topic + "stopAndJoin() entering");
		proceed = false;
		synchronized(lock)
		{
			System.out.println(topic + "stopAndJoin() notifying");
			lock.notify();
		}
		System.out.println(topic + "stopAndJoin() notified");
		final long joining = System.currentTimeMillis();
		try
		{
			join();
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		System.out.println(topic + "stopAndJoin() joined (" + (System.currentTimeMillis() - joining) + "ms)");
	}
}