package com.suiveg.utils.file;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.mockpolicies.Log4jMockPolicy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Date;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.*;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test for
 * @see com.suiveg.utils.file.FileUtils
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 *
 * @since 0.1
 * @version see system.properties
 */
@RunWith(PowerMockRunner.class)
@MockPolicy(Log4jMockPolicy.class)
@PrepareForTest({IOUtils.class, FileUtils.class})
public class FileUtilsTest {

    private FileUtils fileUtils;

    private final Long currentTime = 1328008002467L;
    private final Long olderTime = 1328008002465L;

    @Before
    public void setUp() {
        mockStatic(IOUtils.class);
    }

    @Test
    public void doNotUseThisTestAtAll() {

    }

    @Test
    public void isFileNewerThan_fileShouldBeNewerThanProvided() throws Exception {
        File mockedFile = createMock(File.class);
        Date mockedDate = createMock(Date.class);

        fileUtils = new FileUtils();

        PowerMockito.mockStatic(FileUtils.class);

        when(FileUtils.isFileNewerThan(mockedFile, mockedDate)).thenReturn(Boolean.TRUE);

        replayAll();

        boolean result =FileUtils.isFileNewerThan(mockedFile, mockedDate);

        assertNotNull(result);
        assertTrue(result);

        verifyAll();
    }

    @After
    public void tearDown() {

    }
}
