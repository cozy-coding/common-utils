package com.suiveg.utils.ftp;

import com.suiveg.utils.ftp.model.FTPConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

/**
 * Test for
 * @see com.suiveg.utils.ftp.FTP
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 *
 * @since 0.1
 * @version see system.properties
 */
public class FTPTest  {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testConnect() {
        final Socket socket = mock(Socket.class);

    }

    @After
    public void tearDown() throws Exception {

    }
}
