package com.bezirk.adapter.obd;

import android.bluetooth.BluetoothSocket;

import com.bezirk.adapter.obd.events.ResponseObdEngineRPMEvent;
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
public class FetchRPMTest {
    BluetoothSocket socket;
    private InputStream mockIn;
    private OutputStream mockOut;

    /**
     * @throws Exception
     */
    @BeforeMethod
    public void setUp() throws Exception {
        mockIn = createMock(InputStream.class);
        mockOut = Mockito.spy(new ByteArrayOutputStream());
        socket = Mockito.spy(BluetoothSocket.class);

        doNothing().when(mockOut).write(("01 0C" + "\r").getBytes());
        doReturn(mockIn).when(socket).getInputStream();
        doReturn(mockOut).when(socket).getOutputStream();
    }

    /**
     * @throws IOException
     */
    @Test
    public void testRPM() throws IOException {
        mockIn.read();
        expectLastCall().andReturn((byte) '4');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) 'C');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '2');
        expectLastCall().andReturn((byte) '8');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '3');
        expectLastCall().andReturn((byte) 'C');
        expectLastCall().andReturn((byte) '>');

        replayAll();
        String res = "2575";

        ObdController controller = new ObdController(socket);
        ResponseObdEngineRPMEvent responseObdEngineRPMEvent = null;

        try {
            //responseObdEngineRPMEvent = controller.getEngineRPM("ENGINE_RPM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(responseObdEngineRPMEvent.getResult(), res);
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