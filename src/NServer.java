import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;

/**
 * Created by zaijie on 15/8/19.
 */
public class NServer {
    private Selector selector=null;
    static final int PORT=8989;
    private Charset charset=Charset.forName("utf-8");
    public void init() throws IOException {
        selector=Selector.open();
        ServerSocketChannel server=ServerSocketChannel.open();
        InetSocketAddress inetAddress=new InetSocketAddress(PORT);
        server.bind(inetAddress);
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select()>0){
            for (SelectionKey selectionKey : selector.selectedKeys()) {
                selector.selectedKeys().remove(selectionKey);
                if (selectionKey.isAcceptable()){
                    SocketChannel sc=server.accept();
                    sc.configureBlocking(false);
                    sc.register(selector,SelectionKey.OP_READ);
                    selectionKey.interestOps(SelectionKey.OP_ACCEPT);
                }

                if (selectionKey.isReadable()){
                    SocketChannel sc= (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
                    StringBuilder content=new StringBuilder();
                    try{
                        while (sc.read(byteBuffer)>0){
                            byteBuffer.flip();
                            content.append(charset.decode(byteBuffer));
                        }
                        System.out.println(content.toString());
                        selectionKey.interestOps(SelectionKey.OP_READ);
                    }catch (IOException e){
                        selectionKey.cancel();
                        if (selectionKey.channel()!=null){
                            selectionKey.channel().close();
                        }

                        if (content.length()>0){
                            for (SelectionKey key : selector.keys()) {
                                Channel targetchannel=key.channel();{
                                    if (targetchannel instanceof SocketChannel){
                                        SocketChannel socketChannel= (SocketChannel) targetchannel;
                                        socketChannel.write(charset.encode(content.toString()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
