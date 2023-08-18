import com.bai.utils.ConfigReaderUtil;

public class TestConfigReader {
    public static void main(String[] args) {
        System.out.println(ConfigReaderUtil.ConfigReader("vistor.port"));

        System.out.println(ConfigReaderUtil.ConfigReader("token"));
    }
}
