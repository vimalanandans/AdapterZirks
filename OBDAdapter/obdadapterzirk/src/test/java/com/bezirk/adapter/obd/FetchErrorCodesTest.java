package com.bezirk.adapter.obd;

import android.bluetooth.BluetoothSocket;

import com.bezirk.adapter.obd.events.ResponseObdErrorCodesEvent;
import com.bezirk.adapter.obd.service.ObdController;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.testng.Assert.assertEquals;

/**
 */
@PrepareForTest({InputStream.class, OutputStream.class, BluetoothSocket.class})
public class FetchErrorCodesTest {
    private InputStream mockIn;
    private OutputStream mockOut;
    BluetoothSocket socket;

    /**
     * @throws Exception
     */
    @BeforeMethod
    public void setUp() throws Exception {
        mockIn = createMock(InputStream.class);
        mockOut = Mockito.spy(new ByteArrayOutputStream());
        socket = Mockito.spy(BluetoothSocket.class);
        //PowerMockito.mockStatic(Log.class);

        doNothing().when(mockOut).write(("03").getBytes());
        doReturn(mockIn).when(socket).getInputStream();
        doReturn(mockOut).when(socket).getOutputStream();
    }

    /**
     * @throws IOException
     * One Frame with 3 Error codes
     */
    @Test
    public void testErrorCodes() throws IOException {
        mockIn.read();
        expectLastCall().andReturn((byte) '4');
        expectLastCall().andReturn((byte) '3');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '3');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '4');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '>');

        replayAll();
        String res = "P0103\n";
        res += "P0104\n";

        //ObdController controller = new ObdController(bezirk, socket);
        ResponseObdErrorCodesEvent responseObdErrorCodesEvent = null;

        try {
            //responseObdErrorCodesEvent = controller.getObdErrorCodes("ERROR_CODES");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        assertEquals(responseObdErrorCodesEvent.getResult(), res);
        verifyAll();
    }

    /**
     * Clear resources.
     */
    @AfterClass
    public void tearDown() {
        mockIn = null;
        mockOut = null;
        socket = null;
    }
}