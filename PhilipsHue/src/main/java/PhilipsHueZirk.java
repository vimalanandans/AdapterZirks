import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.ChangeLightStateEvt;
import com.bezirk.hardwareevents.robot.ChangeRobotColorEvt;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.BezirkMiddleware;

import java.awt.Color;



public class PhilipsHueZirk {
    public Bezirk bezirk;

    public PhilipsHueZirk(Bezirk bezirk) {
        this.bezirk = bezirk;


        EventSet lightEventSet = new EventSet(ChangeLightStateEvt.class);

        lightEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof ChangeLightStateEvt) {
                    ChangeLightStateEvt changeLightStateEvt = (ChangeLightStateEvt) event;
                    System.out.println("Received change light state event: " + changeLightStateEvt.toString());

                    if (!changeLightStateEvt.on) {
                        philipsHueController.turnLightOff();
                    }
                    else {
                        setLightColor(changeLightStateEvt.hexColor);
                    }

                }

            }
        });

        bezirk.subscribe(lightEventSet);
        System.out.println("Listening for light events");
    }

    PhilipsHueController philipsHueController = new PhilipsHueController();


    private void setLightColor(HexColor hexColor) {

        Color color = Color.decode(hexColor.hexString);
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        int hue = (int)(hsb[0]*65535);
        int sat = (int)(hsb[1]*255);
        int bri = (int)(hsb[2]*255);

        System.out.println("H: " + (int)hue + " S: " + (int)sat + " B: " + (int)bri);
        philipsHueController.setLightColorHSV(hue, sat, bri);
    }

    public static void main(String argsp[]) {
        //PhilipsHueZirk p = new PhilipsHueZirk();

    }
}


