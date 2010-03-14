/**
 * Copyright (C) 2009  HungryHobo@mail.i2p
 * 
 * The GPG fingerprint for HungryHobo@mail.i2p is:
 * 6DD3 EAA2 9990 29BC 4AD2 7486 1E2C 7B61 76DC DC12
 * 
 * This file is part of I2P-Bote.
 * I2P-Bote is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * I2P-Bote is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with I2P-Bote.  If not, see <http://www.gnu.org/licenses/>.
 */

package i2p.bote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadFactory;

import net.i2p.I2PAppContext;
import net.i2p.client.I2PSession;
import net.i2p.data.DataFormatException;
import net.i2p.util.Translate;

public class Util {
	private static final int BUFFER_SIZE = 32 * 1024;
	private static final String BUNDLE_NAME = "i2p.bote.locale.Messages";
	
	private Util() { }
	
	public static void copyBytes(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		boolean done = false;
		while (!done) {
			int bytesRead = inputStream.read(buffer);
			outputStream.write(buffer);
			if (bytesRead < 0)
				done = true;
		}
	}

	public static void writeKeyStream(I2PSession i2pSession, OutputStream outputStream) throws DataFormatException, IOException {
		i2pSession.getMyDestination().writeBytes(outputStream);
		i2pSession.getDecryptionKey().writeBytes(outputStream);
		i2pSession.getPrivateKey().writeBytes(outputStream);
	}

	public static InputStream createKeyStream(I2PSession i2pSession) throws DataFormatException, IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		writeKeyStream(i2pSession, outputStream);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}	

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[32*1024];
        while (true) {
            int bytesToRead = Math.min(inputStream.available(), buffer.length);
            if (bytesToRead <= 0)
                break;
            else {
                int bytesRead = inputStream.read(buffer, 0, bytesToRead);
                byteStream.write(buffer, 0, bytesRead);
            }
        }
        return byteStream.toByteArray();
    }

    public static ThreadFactory createThreadFactory(final String threadName, final int stackSize) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(Thread.currentThread().getThreadGroup(), runnable, threadName, stackSize);
            }
        };
    }
    
    /**
     * Creates a thread-safe <code>Iterable</code> from a thread-unsafe one.
     * Modifications to the old <code>Iterable</code> will not affect the
     * new one.
     * @param <E>
     * @param iterable
     * @return
     */
    public static <E> Iterable<E> synchronizedCopy(Iterable<E> iterable) {
        synchronized(iterable) {
            Collection<E> collection = new ArrayList<E>();
            for (E element: iterable)
                collection.add(element);
            return collection;
        }
    }

    /**
     * Returns the <code>i</code>-th element of a <code>Collection</code>'s <code>Iterator</code>.
     * @param <E>
     * @param collection
     * @param i
     * @return
     */
    public static <E> E get(Collection<E> collection, int i) {
        for (E element: collection) {
            if (i == 0)
                return element;
            i--;
        }
        return null;
    }
    
    public static String _(String messageKey) {
        return Translate.getString(messageKey, I2PAppContext.getGlobalContext(), BUNDLE_NAME);
    }
}
