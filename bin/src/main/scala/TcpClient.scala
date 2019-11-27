import java.net._;
import java.io._;
import resource._
object SimpleClient extends App {
    try{
      // Open your connection to a server, at port 1234
      for (s1 <- managed(new Socket("localhost",1234));
        os <- managed(s1.getOutputStream);
        dos <- managed(new DataOutputStream(os));
        is <- managed(s1.getInputStream);
        dis <- managed(new DataInputStream(is))){
        //send word through data output stream
        dos.writeUTF("love")
        //read reply
        val reply = dis.readUTF()
        //output
        println(s"TCP: the meaning of $reply")
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
}
