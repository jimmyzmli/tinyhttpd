package jserver;

import jserver.handler.*;
import jserver.header.*;
import jserver.request.*;
import jserver.data.*;
import jserver.module.*;
import jserver.exception.*;
import jserver.config.Configeration;

import java.io.*;   //Network access.
import java.util.*; //Storage management.
import java.net.*;  //Input tools.

/**
 * A JServer provides basic network interfacing capabilities. It will listen for a connection
 * as a given configeration tells it to.
 */
public class JServer {

	private Configeration config;
        private List<ConnectionListener> listeners = new LinkedList<ConnectionListener>();

	public JServer( Configeration conf ){
		//Assignment for later reference.
		config = conf;

		//Begins listening for each requested port.
                for( Integer port : config.listenPorts ){
                    //Create a listener.
                    ConnectionListener listener = new ConnectionListener( port );
                    //Save the reference.
                    listeners.add(listener);
                    //Start listening.
                    new Thread(listener).start();
                }
	}

        //Controller methods:
        public void stop(){
            //Stop all the listeners.
            for( ConnectionListener listener : listeners )
                listener.stop();
        }
        public void start(){
            for( ConnectionListener listener : listeners )
                listener.start();
        }

        //Acessor(s).
        public void config( Configeration newConf ){
            //Pause.
            stop();
            //Update the configeration.
            if( newConf != null )
                config = newConf;
            //Then restart.
            start();
        }
        public Configeration config(){
            return config;
        }
        

	private class ConnectionHandler implements Runnable{
		/* Takes in a connection and processes it as nessary, this class does not provide all implementation details but
		* process the request with other diferent handlers. This class have no special interfaces.*/

		private Connection connection;
		private Thread runner;

		ConnectionHandler( Connection cnt ){
			handle( cnt );
		}
		ConnectionHandler( Socket cnt, ConnectionType type ){
			handle( cnt, type );
		}

		public void handle( Connection cnt ){
			//Assignment.
			connection = cnt;
			//Start the processor.
			start();
		}
		public void handle( Socket cnt, ConnectionType type ){
			//Create the connection.
			connection = new Connection( cnt, type );
			//Uses a new thread to process the request, so that it doesn't slow anything down.
			start();
		}

		//The method start interface
		public void start(){
			//Create the thread and start it.
			new Thread( this ).start();
		}

		public synchronized void run(){

			/* Processes the request. */
			//Create a handler to handle all the user INPUTs.
                        //First, check for a override addon handler.

                        //The port which the request was send to.
                        int reqPort = connection.getType().port;
                        //If the connection is unknown, then use the real connection port.
                        if( reqPort == 0 )
                            reqPort = connection.getLink().getLocalPort();
            
                        InputHandler handler;

                        //Find the correct request handler.
                        if( HandlerManager.hasPort(reqPort) ){
                            //Let the user defined handler handle it.
                            HandlerManager.handleFor(reqPort, connection);
                            //And end the request. (The user can keep it alive in the handler as needed)
                            return;
                        }else{     
                            handler = InputHandler.createHandler( connection );
                        }

			try{
				//For responding.
				OutputStream out = connection.getOutputStream();
				while( handler.keepAlive() ){

					//Get the next request string.
					String requestStr = handler.nextRequestString();

					//Process the request string into properties.
					Request request = RequestParser.parse( requestStr, connection.getRequestType() );

					//Get the specified data.
					Data data = request.getRequestedData();

					//Process the data with any modules needed.
					ModManager.process( request, data );

					//Create a header for the requested data given.
					Header header = Header.create( request, new DataDescription(data) );

					//Write the header.
					out.write( header.getBytes() );
					//Send out the header so the data can be started sending.
					out.flush();
					//Write the data.
					out.write( data.getBytes() );
					out.flush();

				}
				//Close the connection if processing is done.
				connection.close();

			}catch( SocketException e){
				//If the problem is the socket, then there's no way to keep this connection.
				//Thus end the connection.
				if( !connection.getLink().isClosed() )
                                    connection.close();

			}catch(IOException e ){
				Err.report( "IO Error in processing request. "+e.getMessage() );
			}catch( ServerException e ){
				//If the exception is a server one, then just report it.
				e.printStackTrace();
			}catch( RuntimeException e ){
				//Let no exceptions escape, otherwise, this thread will exit. (When this method returns)
				Err.report(e.getMessage());
			}

		}

	}

	private class ConnectionListener implements Runnable{
		/* Listenes (Checks repeativly) for a network connection at a given port, then sents the connection
		* to the handler. It's job is then finished. */

		private ServerSocket listener;  //The binder/listener of the given port.
		private Thread listenThread;    //The thread running the listener.
		private int port;

		ConnectionListener(int port){
			/* Uses a NEW thread for each listener, so that they can listen asynchronizedly.
			* The consturtor makes that thread and sets up everything.*/
			//Saves the port number for further reference.
			this.port = port;
			//Readies the listener.
			try{
				listener = new ServerSocket( port );
			}catch( IOException e){
				Err.fatal("Server failed to bind at port "+port);
			}
			//The listener can now start listening... Once the listening process starts.
		}

		public void start(){
			/* Begins the listening process. */
			//Starts the listener.
			if( listenThread == null){
				listenThread = new Thread( this ); //Refer for further use.
				listenThread.start();
			}
		}

		public void stop(){
			/* Stops the listening thread and resets the ConnectionListener to it's original
			* status. Meaning that it can be started again. */
			if( listenThread != null ){ //checks if the server is indeed started.
				//Stops the process.
				listenThread.interrupt();
				//Dereferences the thread so that clean up can occur.
				listenThread = null;
			}
		}

		public void run(){
			/* Listene for a connection and accept it, repeatively. */
			//End the listen when interupted.
			while(! Thread.currentThread().interrupted() ){
				//Listenes for connection.
				Socket connection = null;
				try{
					connection = listener.accept();
				}catch(IOException e){
					Err.report( "Error in connection request, connection cancled. " + e.getMessage() );
				}
				//Connection is made! Pass the connection to the handler.
				if( connection != null ){
					new ConnectionHandler( new Connection( connection, ConnectionType.port( port ) ) );
				}
				//Listenes again.
			}
		}
	}

}
