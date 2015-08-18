import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by zaijie on 15/8/19.
 */
public class NClient {
    private Selector selector=null;
    static final int PORT=8989;
    private Charset charset=Charset.forName("utf-8");
    private SocketChannel socketChannel=null;

    public void init() throws IOException {
        try {
            selector=Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InetSocketAddress inetSocketAddress=new InetSocketAddress(PORT);
        socketChannel=SocketChannel.open(inetSocketAddress);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        new Thread(new ClientThread()).start();

        Scanner scanner=new Scanner(System.in);
        while (scanner.hasNextLine()){
            String line=scanner.nextLine();
            socketChannel.write(charset.encode(line));
        }
    }
    private class ClientThread implements Runnable{

        @Override
        public void run() {
            try {
                try {
                    while (selector.select()>0){
                        for (SelectionKey selectionKey : selector.selectedKeys()) {
                            selector.selectedKeys().remove(selectionKey);
                            if (selectionKey.isReadable()){
                                SocketChannel socketChannel= (SocketChannel) selectionKey.channel();
                                ByteBuffer buffer=ByteBuffer.allocate(1024);
                                String content="";
                                while (socketChannel.read(buffer)>0){
                                    socketChannel.read(buffer);
                                    buffer.flip();
                                    content+=charset.decode(buffer);
                                }
                                System.out.println("msg"+content);
                                selectionKey.interestOps(SelectionKey.OP_READ);
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
