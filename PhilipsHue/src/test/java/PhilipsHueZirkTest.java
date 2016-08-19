import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.ChangeLightStateEvt;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.proxy.BezirkMiddleware;



public class PhilipsHueZirkTest {
    public static void main(String[] args) {
        Bezirk bezirk = BezirkMiddleware.registerZirk("Philips Hue Test Zirk");

        PhilipsHueZirk philipsHueZirk = new PhilipsHueZirk(bezirk);
        bezirk.sendEvent(new ChangeLightStateEvt(true, new HexColor("#FFFF00")));
        //bezirk.sendEvent(new ChangeLightStateEvt(false, HexColor.BLUE));
        System.out.println("Sent color change event");
    }
}
