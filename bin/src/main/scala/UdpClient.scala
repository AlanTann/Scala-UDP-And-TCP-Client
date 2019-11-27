import java.net._;
import java.io._;
import resource._;
object UDPClient extends App {
  var aSocket: DatagramSocket = null;
  try {
    for (soc <- managed(new DatagramSocket())) {
    val m: Array[Byte] = "guru".getBytes// string have a function byte
    val aHost: InetAddress = InetAddress.getByName("localhost")
    val serverPort: Int = 6900
    
    //m.length is the length of byte,aHost is Inet object, server port is 6800
    val request: DatagramPacket = new DatagramPacket(m, m.length, aHost, serverPort); 
    soc.send(request); // use the socket and send the datagram
    val buffer: Array[Byte] = Array.ofDim(1000); // create a new array
    val reply: DatagramPacket = new DatagramPacket(buffer, buffer.length); //
    soc.receive(reply); //wait until something come in, if not come in thn it wait forever
    println("UDP, The meaning of " + new String(reply.getData()).trim()); // construct a string from the byte

    }
  } catch {
    case e: SocketException => println("Socket: " + e.getMessage());
    case e: IOException     => println("IO: " + e.getMessage());
  } 
}
