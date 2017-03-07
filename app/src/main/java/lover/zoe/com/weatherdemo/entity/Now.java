package lover.zoe.com.weatherdemo.entity;

/**
 * Created by zoe on 2017/3/7.
 */
public class Now {


    public String fl;
    public String hum;
    public String pcpn;
    public String pres;
    public String tmp;
    public String vis;
    public Cond cond;
    public Wind wind;

    public class Cond {

        public String code;
        public String txt;
    }

    public class Wind {
        public String deg;
        public String dir;
        public String sc;
        public String spd;
    }
}
