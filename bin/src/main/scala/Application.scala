// SimpleServerLoop.java: a simple server program that runs forever in a single thead
import java.net._
import java.io._
import resource._
import scala.concurrent.ExecutionContext
object SimpleServerLoop extends App {
  var aSocket: Option[DatagramSocket] = None;
  def execute[A, B](s :A)(body: A => B) =
    ExecutionContext.global.execute(
      new Runnable {
        def run() = body(s)
      }    
    )
  def execute(body: => Unit) = ExecutionContext.global.execute(
    new Runnable {
      def run() = body
    }    
  )
  //setting up upd sever
  execute {
    val aSocket = Some(new DatagramSocket(6900));
    val buffer: Array[Byte] = Array.ofDim[Byte](1000);
  
    //upd thread
    while (true) {
      val request: DatagramPacket = new DatagramPacket(buffer, buffer.length);
      aSocket.get.receive(request);//blocking   
      execute(request) {
        request =>
           //1. read a word
          val data = request.getData()
          val word = new String(data).trim()
          //2. match data
          var buffer2: Array[Byte] = Array.ofDim[Byte](1000);
          word match {
                case "guru" =>
                  //3. sent meaning back to client
                  buffer2 = ("guru is master").getBytes
                case "love" =>
                   buffer2 = ("love is feeling").getBytes
                case _ =>
                   buffer2 = ("there is no such word").getBytes
              }
          //3. sent meaning back
          // create another new datagram, use the data that you have receive
          // the port is the port that user send to you 
          val reply: DatagramPacket = new DatagramPacket(buffer2, buffer2.length, request.getAddress(), request.getPort());
          aSocket.get.send(reply) // send it back      
      }
    }
  }
  //setting up TCP
  for (serverSocket <- managed(new ServerSocket(1234))) {
    while (true) {
      try {
        //accepting a socket
          execute(serverSocket.accept()) {
            isocket: Socket => {
            for (
              socket <- managed(isocket);
              is <- managed(socket.getInputStream);
              dataIS <- managed(new DataInputStream(is));
              os <- managed(socket.getOutputStream);
              dataOS <- managed(new DataOutputStream(os))
            ) {
              //1. read a word
              val word = dataIS.readUTF()
              //2. match meaning
              word.trim().toLowerCase() match {
                case "guru" =>
                  //3. sent meaning back to client
                  dataOS.writeUTF("guru is master")
                case "love" =>
                  dataOS.writeUTF("love is feeling")
                case _ =>
                  dataOS.writeUTF("there is no such word")
              }
            }
            }
          }        
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }
  }
}

