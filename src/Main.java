import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            new NServer().init();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
